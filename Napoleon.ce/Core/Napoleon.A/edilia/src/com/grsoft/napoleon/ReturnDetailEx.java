package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.grsoft.database.OrderResultHitching;
import com.grsoft.dataobjects.DocHandleStatus;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesAdapter;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.PhotoClickHandler.EventHandler;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnDetailEx extends ReturnDetail implements EventHandler, SendResultListener {
	VisitImpl visit;
	String picPath;
	ImagesAdapter adapter;
	boolean inited = false;

	@Override protected void setContentView() { setContentView(R.layout.returndetailex); }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		openAssociatedVisit();
		adapter = new ImagesAdapter(this, visit);
		
		HorizontalListView g = (HorizontalListView)findViewById(R.id.gvItems);
		g.setAdapter(adapter);
		
		findViewById(R.id.btnPhoto).setOnClickListener(new PhotoClickHandler(visit, this, VisitDoc.instance()));
	}

	private void openAssociatedVisit() {
		visit = new VisitImpl();
		Order o = doc.getData();
		long created = o.created.getTime() + 1000;
		Date dt = new Date(created);
		visit.getData().created = dt;
		
		if( visit.read() == false ) {
			Visit v = visit.getData();
			v.date = Util.getDateTime();
			v.created = dt;			
			v.id = doc.getId();
			v.latitude = o.latitude;
			v.longitude = o.longitude;
			v.params = 0;
			
			visit.write();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( inited ) {
			visit.read(visit.getRowid(), false);
			adapter.notifyDataSetChanged();
		}
		inited = true;
		updateDocInfo();
	}
	
	@Override
	public void onBackPressed() {
		if( isCorrectReturn(true) )
			super.onBackPressed();
	}
	
	private boolean isCorrectReturn(boolean showAlert) {
		boolean ret = true;
		if( visit != null && visit.getData().items.size() > 0 )
			return true;
		
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if( ci.getValue(sb, "ФотоПричиныВозврата") ) {
			HashSet<String> checkedCause = new HashSet<String>();
			for(String sv : sb.toString().split(";"))
				checkedCause.add(sv);
			
			for(OrderItem item : doc.getData().items) {
				if(checkedCause.contains(((ReturnItemEx)item).cause)) {
					ret = false;
					break;
				}
			}
		}
		
		if( !ret && showAlert ) {
			runOnUiThread(new Runnable() {
				@Override public void run() { Toast.makeText(ReturnDetailEx.this, "Необходимо создать фото возвращаемых товаров.", Toast.LENGTH_SHORT).show(); }
			});
		}
		return ret;
	}

	@Override
	public void send() {
		if( isCorrectReturn(true) ) {
			List<DocExportListener> docs = new ArrayList<DocExportListener>();
			docs.add(new DocSendListner(ReturnDoc.instance().getObjectName(), doc));
			if(visit != null && visit.isExported() == false)
				docs.add(new DocSendListner(VisitDoc.instance().getObjectName(), visit));
			new DocumentSender(this, btnSend, docs, this).execute((Void[])null);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Visit v = visit.getData();
		boolean canDelete = (v.remark.trim().length() == 0 && v.items.size() == 0);
		if( canDelete )
			visit.delete();
		visit.close();
	}

	@Override
	public void prepareBoforeClick() {
	}

	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(picPath != null && picPath.trim().length() > 0) {
				visit.addPhoto(picPath.getBytes());
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	void updateDocInfo() {
		String text = "";
		TextView tv = (TextView)findViewById(R.id.tvDocInfo);
		ReturnEx re = (ReturnEx) doc.getData();
		if(re.docStatus == DocHandleStatus.FAIL) {
			text = "Ошибка при записи " + re.docMessage;
		} else {
			if( re.retNumber.length() > 0 )
				text += "№ документа <b>" + re.retNumber + "</b>";
		}
		tv.setText(Html.fromHtml(text));
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			doc.read(doc.getRowid(), false);
			
			updateDocInfo();
			
			String errMsg = OrderResultHitching.getErrorMessage();
			if(errMsg.length() > 0) {
				MessageBox.show(this, getString(R.string.error), errMsg);
			}
		}
	}
}
