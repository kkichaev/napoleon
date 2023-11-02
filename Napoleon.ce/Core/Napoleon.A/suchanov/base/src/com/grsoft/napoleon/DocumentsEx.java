package com.grsoft.napoleon;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;


public class DocumentsEx extends Documents {
	
	static final int SELECT_AGENT_DIALOG = 1; 
	static final String ALL_AGENTS = "<все>";

	CharSequence[] agents = null; 
	             
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		agents = loadAgents();
		
		findViewById(R.id.btnAgents).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(SELECT_AGENT_DIALOG); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == SELECT_AGENT_DIALOG )
			return createAgentDialog();
		
		return super.onCreateDialog(id);
	}

	private CharSequence[] loadAgents() {
		ArrayList<CharSequence> a = new ArrayList<CharSequence>();
		a.add(ALL_AGENTS);
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String table = DataObjectInfo.getInstance().getTableName(Delivery.class);			
		String sql = "SELECT distinct agent FROM " + table + " WHERE id = ?";
		
		String[] args = { org.getData().id };
		try {
			Cursor c = db.rawQuery(sql, args);
			while( c.moveToNext() )
				a.add(c.getString(0));
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		CharSequence[] ret = new String[a.size()];
		return a.toArray(ret);
	}

	private void agentSelected(int which) {

		CharSequence agent = (which == 0) ? null : agents[which];
		((DocumentsAdapterEx)adapter).setAgentFilter(agent);
		if( which == 0 ) {
			refreshTotalSum();
		} else {
			String tableName = DataObjectInfo.getInstance().getTableName(Delivery.class);
			String sql = "SELECT sum(sumD) FROM " + tableName + " WHERE id='" + org.getData().id + 
				"' AND agent='" + agent.toString() + "'";

			SQLiteDatabase db = DataBaseManager.getDataBase();
			int sum = 0;
			try {
				SQLiteStatement stmt = db.compileStatement(sql);
				sum = (int)stmt.simpleQueryForLong();
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateTotalSum(sum, 0);
		}
	}

	protected Dialog createAgentDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Выберите агента");
		b.setItems(agents, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) { agentSelected(which); }
		});
		
		return b.create();
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		int visible = (docType == DebtDoc.instance()) ? View.VISIBLE : View.GONE;
		findViewById(R.id.btnAgents).setVisibility(visible);
	}
	
	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) { 
		return new DocumentsAdapterEx(this, docType, id, null, R.layout.docs_list_row); 
	}
	
	@Override 
	protected int getContentViewID() { return R.layout.docs_ex; }
	
	class DocumentsAdapterEx extends DocumentsAdapter {

		private boolean docChanged;

		protected DocumentsAdapterEx(Context context, DocType docType, String orgId, String order, int id) {
			super(context, docType, orgId, order, id);
			docChanged = true;
		}
		
		@Override
		public void setDocType(DocType docType) {
			docChanged = true;
			super.setDocType(docType);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( docChanged ) {
				docChanged = false;
				convertView = null;
			}
			return super.getView(position, convertView, parent);
		}

		public void setAgentFilter(CharSequence agent) {
			if( curDocType == DebtDoc.instance()) {
				documents.close();
				String where = (agent == null) ? null : " agent='" + agent + "'";
				documents = curDocType.docList(orgId, order, where);
				notifyDataSetChanged();			
			}
		}
	}
}
