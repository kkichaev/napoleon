package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.TaskDone;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.TaskDoneDoc;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;


public class Documents extends DocumentsW implements OrgSelectDialog.OrgSelect {
	private CreatableDocument<?> selectedDoc; 
	
	@Override protected int getContextMenuId() { return R.menu.doc_context_menuex; }
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == TaskDoneDoc.instance()) {
			DocType.setCurDoc(docType);
			TaskDocList.open(this, (Org) org.getData());
			finish();
		} else if( docType == DebtDoc.instance()) {
			DocType.setCurDoc(docType);
			BalanceView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		View v = findViewById(R.id.btnTaskAlert);
		if( v != null ) {
			v.setOnClickListener(new View.OnClickListener() {
				@Override public void onClick(View arg0) { TaskDocList.open(Documents.this, (Org) org.getData(), true); }
			});
			
			v.setVisibility(pendingTaskCount(org.getData().id) > 0  ? View.VISIBLE : View.GONE);
		}
	}
	
	public static int pendingTaskCount(String id) {
		int res = 0;

		DbWriter.checkDBTable(OrgTask.class);
		DbWriter.checkDBTable(TaskDone.class);
		String sql= "select count(*) from " + new OrgTask().getTableName() + " where not id in (select idTask from " + 
				new TaskDone().getTableName() + " where id = '" + id + "') and orgid = '" + id + "'";
		Cursor c = null;
		try {
			c = DataBaseManager.getDataBase().rawQuery(sql, null);
			if( c.moveToNext())
				res = c.getInt(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if( c != null )
				c.close();
		}

		return res;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.org_change_dlg)
			return OrgSelectDialog.create(this, this);
		return super.onCreateDialog(id);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itOrgChange){
			AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
			selectedDoc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
			if(selectedDoc.isEditable())
				showDialog(R.id.org_change_dlg);
			else
				Toast.makeText(this, R.string.cant_change_org, Toast.LENGTH_SHORT).show();
			return true;
		}else
			return super.onContextItemSelected(item);
	}

	@Override
	public void selected(Org org) {
		String oldId = selectedDoc.getData().id;
		String newId = org.id;
		
		if (!oldId.equals(newId)){
			selectedDoc.getData().id = newId;
			selectedDoc.write();
			selectedDoc.close();
			DocType dt = DocType.getCurDoc();
			dt.refreshDocSum(oldId);
			dt.refreshDocSum(newId);
			finish();
			open(this, org);
		}
	}
	
	protected void doCreate() {
		if (DispositionActivity.isNeedDisposition(this, org.getData().id, DocType.getCurDoc().getObjectName()))
			docCreatingDisposition();
		else
			docCreating();
	}
	
	private void docCreatingDisposition() {
		if ( hasLocationPermission() == false)
			showDialog(R.id.ask_for_location_permission);
		else if( isGPSTurnOn() == false ) 
			showDialog(R.id.ask_for_open_gps);
		else 
			docCreatingProccess();
		
	}

	private void docCreatingProccess() {
		DispositionActivity.open(this, org.getData().id);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == R.id.disposition_result && resultCode == RESULT_OK)
			createNewDoc();
	}
}
