package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.ReturnRequestDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnRequestDetail extends OrderDetail implements SendResultListener {
	public static void open(Context ctx, ReturnRequestImpl doc) {
		Intent i = new Intent(ctx, ReturnRequestDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@Override protected void setContentView() { setContentView(R.layout.rr_detail); }
	
	@Override protected boolean haveFocusedGroup() { return false; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if( DocType.getCurDoc() != ReturnRequestDoc.instance() )
			DocType.setCurDoc(ReturnRequestDoc.instance());
		
		super.onCreate(savedInstanceState);
		updateColumns();
		btnSend.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		ReturnRequestDoc.instance().refreshDocSum(doc.getId());
	}
	
	@Override
	public void send() {
		if(doc.isExported()) {
			Toast.makeText(this, "Документ уже отправлен", Toast.LENGTH_SHORT).show();
			return;
		}
		
		List<DocExportListener> docs = new ArrayList<DocExportListener>();

		docs.add(new DocSendListner(ReturnRequestDoc.instance().getObjectName(), doc));
		
		VisitImplEx visit = new VisitImplEx();
		ReturnRequestImpl rri = (ReturnRequestImpl)doc;
		visit.getData().created = rri.getData().visitDoc; 
		if(visit.read() && visit.isExported() == false && !visit.isEmpty())
			docs.add(new DocSendListner(VisitDoc.instance().getObjectName(), visit));
		visit.close();
		
		new DocumentSender(this, btnSend, docs, this).execute((Void[])null); 
	}

	@Override protected void setAdapter() { lvItems.setAdapter(new ItemsAdapter()); }
	
	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			doc.read(doc.getRowid(), false);
			updateColumns();
		}
	}
	
	private void updateColumns() {
//		findViewById(R.id.AcceptTitle).setVisibility(((ReturnRequestImpl)doc).isAccepted() ? View.VISIBLE : View.GONE);
	}

	class ItemsAdapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.rrdetail_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			int svqty = 0;
//			for(ReturnItemDlv rd : ((ReturnRequestItem)item).items)
//				svqty += rd.svQty;
//			
//			if( item.qty != svqty)
//				color = Color.RED;

			super.drawInternal(view, name, color, item);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvDispatch);
			tv.setText(Util.IntToScaleStr(svqty, Consts.QTY_SCALE));
			tv.setTextColor(color);
			
		}
	}
}
