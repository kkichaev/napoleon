package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.CellsAuditImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.util.OrgTaskListHelper;


public class DocumentsEx extends Documents {
	private static final int CHOOSE_COPY_ORG = 0x1000;
	private ImageButton btnTask;
	@Override protected int getContentViewID() { return R.layout.documentsex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnTask = (ImageButton) findViewById(R.id.btnTask);
		btnTask.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DocType.setCurDoc(TaskDoneDoc.instance());
				doCreate();
			}
		});
	}
	
	List<Org> orgs;
	CellsAuditImpl copied;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CHOOSE_COPY_ORG ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите автомат");
			
			orgs = new ArrayList<Org>();
			final List<String> orgList = new ArrayList<String>();
			
			DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {

				@Override
				public boolean travel(DataTraveler<Org> item) {
					orgs.add(item.data);
					orgList.add(item.data.name);
					item.data = new Org();
					return true;
				}
				
			}, "", "name");
			
			String[] items = new String[orgList.size()];
			b.setSingleChoiceItems(orgList.toArray(items), -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					CellsAuditImpl cp = (CellsAuditImpl) copied.copy();
					cp.getData().id = orgs.get(which).id;
					cp.write();
					cp.open(DocumentsEx.this);
					dialog.dismiss();
				}
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		btnTask.setVisibility(hasMissedTas() && DocType.getCurDoc() != TaskDoneDoc.instance() ? View.VISIBLE : View.GONE);
	}

	private boolean hasMissedTas() {
		return new OrgTaskListHelper().getTaskList(org.getData().id, true).size() > 0;
	}
	
	@Override
	protected void onContextAction(MenuItem item, Document<?> doc) {
		if( doc instanceof CellsAuditImpl && item.getItemId() == R.id.itCopy ) {
			copied = (CellsAuditImpl)doc;
			showDialog(CHOOSE_COPY_ORG);
			return;
		}
		super.onContextAction(item, doc);
	}
}
