package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import com.grsoft.dataobjects.OrderDecision;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.SyncInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderDecisionImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgTaskMImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class DocumentsEx extends Documents {
	
	private static final int SHOW_TASK_DLG = R.id.show_task_dlg;
	private static final int ORG_INFO_DIALOG = R.id.org_info_dlg;
	private ArrayList<Long> data = new ArrayList<Long>();
	
	boolean initnig = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int priceSpan = 0;
		ConfigImpl ci = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		if(ci.getValue(sb, "АктуальностьПрайса")) {
			try {
				priceSpan = Integer.parseInt(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(priceSpan > 0) {
			Date now = new Date();
			Date lastSync = SyncInfo.getLastSync(SyncInfo.GEN_DATA);
			if(lastSync == null || ((now.getTime() - lastSync.getTime()) / (3600*1000)) > priceSpan ) {
				UpdateDB.open(this);
				Toast.makeText(this, "Необходимо актуализировать прайс-лист", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
		}
	}
	
	@Override
	protected void onResume() {
		if(initnig) {
			initnig = false;
			
			data.addAll(OrgTaskListM.getTaskList(org.getData().id, true));
			if(data.size() > 0)
				showDialog(SHOW_TASK_DLG);
		}
		super.onResume();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == SHOW_TASK_DLG) return createShowTaskDlg();
		if(id == ORG_INFO_DIALOG) return createOrgInfoDlg();
		return super.onCreateDialog(id);
	}

	private Dialog createOrgInfoDlg() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Информация по контрагенту");
		b.setMessage(Html.fromHtml(((OrgEx)org.getData()).info));
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				DocumentsEx.super.doCreate();
			}
		});
		return b.create();
	}

	private Dialog createShowTaskDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		CharSequence[] t = new CharSequence[data.size()];
		
		OrgTaskMImpl impl = new OrgTaskMImpl();
		
		for(int i = 0; i < data.size(); i++){
			long rowid = data.get(i);
			impl.read(rowid);
			t[i] = impl.getData().text;
		}
		
		impl.close();
		builder.setItems(t, null);
		builder.setTitle(R.string.tasks);
		builder.setPositiveButton(R.string.ok, null);
		return builder.create();
	}
	
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new DocumentsAdapter(this, docType, id, order, R.layout.docs_list_rowex){
			@Override
			protected void setData(View view, Document<?> doc, int position) {
				super.setData(view, doc, position);
				
				TextView tv = (TextView) view.findViewById(R.id.tvComment);
				tv.setVisibility(View.GONE);
				
				if (doc instanceof OrderImplEx){
					OrderDecision decision = OrderDecisionImpl.getDecision(((OrderImplEx)doc).getData().created);
					
					if(decision != null && decision.remark.length() > 0){
						tv.setVisibility(View.VISIBLE);
						tv.setText(decision.remark);
					}
				}
			}
		};
	}
	
	@Override
	protected void doCreate() {
		if(DocType.getCurDoc() == OrderDoc.instance() && ((OrgEx)org.getData()).info.length() > 0) {
			showDialog(ORG_INFO_DIALOG);
			return;
		}
		super.doCreate();
	}
}
