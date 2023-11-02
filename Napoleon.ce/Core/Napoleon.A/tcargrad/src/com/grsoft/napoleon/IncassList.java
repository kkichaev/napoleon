package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.IncassListData;
import com.grsoft.dataobjects.IncassListItem;
import com.grsoft.dataobjects.IncassListSend;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.IncassListImpl;
import com.grsoft.dataobjects.impl.IncassListSendImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.ObjectExchange;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class IncassList extends BaseActivity {

	static final String OBJ_NAME = "IncassList";

	private static final int SYNC_END_DIALOG = 0;
	private static final int REMOVE_DIALOG = 1;
	
	OrgImpl org = new OrgImpl();
	IncassImpl incassDoc = new IncassImpl();
	Adapter adapter;
	
	public static void open(Context context) {
		Intent i = new Intent(context, IncassList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incass_list);
		
		ExpandableListView lv = (ExpandableListView)findViewById(R.id.lvDocs);
		adapter = new Adapter();
		lv.setAdapter(adapter);
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});

		findViewById(R.id.btnDel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(REMOVE_DIALOG); }
		});
	}
	
	protected void removeSended() {
		try {
			String sql;
			
			String t1 = DataObjectInfo.getInstance().getTableName(com.grsoft.dataobjects.IncassListData.class);		
			String where = "(([params] & " + Integer.toString(ParamState.ofExported) + " ) <> 0)";
			sql = "DELETE FROM [" + t1 + "] WHERE " + where;
			
			DataBaseManager.getDataBase().execSQL(sql);

			String t2 = DataObjectInfo.getInstance().getTableName(Incass.class);
			sql = "DELETE FROM [" + t2 + "] WHERE " + where;
			DataBaseManager.getDataBase().execSQL(sql);
			
			IncassDoc.instance().refreshDocSum();
			
			adapter.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		org.close();
		incassDoc.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}
	
	protected void send() {
		IncassListData doc = adapter.getUnsended();
		if( doc == null )
			return;
		
		IncassListSendImpl sendedDoc = new IncassListSendImpl();
		sendedDoc.getData().setData(doc);
		new ObjectExchange(IncassList.this, findViewById(R.id.btnSend), 
				OBJ_NAME,  ObjectExchange.WRITE_OBJECTS, sendedDoc, new ObjectExchange.ObjectSendedHandler() {						
					@Override
					public void sended(DbObject<?> object, String response, int result) {
						checkSendObject((IncassListSendImpl) object, response, result);
					}
				}).execute((Void[])null);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case SYNC_END_DIALOG:
			return createSyncEndDlg();
		case REMOVE_DIALOG: {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle(R.string.remove_title);
			b.setMessage(R.string.remove_text);
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeSended();
					dialog.dismiss();
				}
			});
			b.setNegativeButton(R.string.no, null);
			return b.create();
		}
		default:
			return super.onCreateDialog(id);
		}
	}
	
	protected Dialog createSyncEndDlg() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle(R.string.error);
		b.setMessage(R.string.necessary);
		return b.create();
	}
	

	String syncTitle;
	String syncMsg;
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if( id == SYNC_END_DIALOG ) {
			dialog.setTitle(syncTitle);
			((AlertDialog)dialog).setMessage(syncMsg);
			((AlertDialog)dialog).setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override public void onDismiss(DialogInterface dialog) { openDocAgain(); }
			});
		}
	}

	boolean reopenDocs = false;
	
	void openDocAgain() {
		if( reopenDocs ) {
			adapter.refresh();
		}
	}

	protected void checkSendObject(IncassListSendImpl object, String response, int result) {
		if( result <  0 ) {
			syncTitle = getString(R.string.error);
			syncMsg = response;
			return;
		}
		
		IncassListImpl ii = new IncassListImpl();
		IncassListData doc = ii.getData();
		IncassListSend src = object.getData();
		doc.created = src.created;
		ii.read();

		doc.number = src.number;
		if( result == ObjectExchange.RESULT_FAIL ) {
			syncTitle = getString(R.string.error_processing);
			syncMsg = response;
		} else {
			reopenDocs = true;
			
			doc.remark = response;
			
			syncTitle = getString(R.string.inform);
			if( result == ObjectExchange.RESULT_SAVE ) {
				syncMsg = (response.length() > 0) ? response : getString(R.string.doc_save_error);
			}
			else if( result == ObjectExchange.RESULT_COMMIT ) {
				syncMsg = getString(R.string.doc_process_succs);
				doc.setExported();
			}
			ii.write();	
		}
		ii.close();
		
		this.runOnUiThread(new Runnable() {			
			@Override public void run() { showDialog(SYNC_END_DIALOG); }
		});
	}

	class Adapter extends BaseExpandableListAdapter {
		
		ArrayList<IncassListData> items = new ArrayList<IncassListData>();
		
		ArrayList<IncassListItem> getUnsendedIncass() {
			ArrayList<IncassListItem> ret = new ArrayList<IncassListItem>();
			IncassListItem i = new IncassListItem();
			String table = DataObjectInfo.getInstance().getTableName(Incass.class);
			DbReader r = new DbReader();
			String where = "(([params] & " + Integer.toString(ParamState.ofExported) + " ) == 0)";
			boolean bdo = r.select(i, table, where);
			while(bdo) {
				ret.add(i);
				i = new IncassListItem();
				bdo = r.selectNext(i);
			}
			r.close();
			
			return ret;
		}
		
		public IncassListData getUnsended() {
			if(items.size() == 0)
				return null;
			IncassListData i = items.get(0);
			return i.IsExported() ? null : i;
		}
		
		public void refresh() {
			com.grsoft.dataobjects.IncassListData unsended = null;			
			// не отправленный документ может быть только один, остальные удаляем
			ArrayList<Date> needRemove = new ArrayList<Date>();
			
			items.clear();
			
			com.grsoft.dataobjects.IncassListData item = new com.grsoft.dataobjects.IncassListData();
			String table = DataObjectInfo.getInstance().getTableName(item.getClass());
			DbReader r = new DbReader();
			boolean bdo = r.select(item, table, "", "created desc");
			while( bdo ) {
				if( !item.IsExported() ) {
					if( unsended == null )
						unsended = item;
					else
						needRemove.add(item.created);
				} else {
					items.add(item);
				}
				
				item = new com.grsoft.dataobjects.IncassListData();
				bdo = r.selectNext(item);
			}
			r.close();
			
			// move all unsended incass to unsended doc  
			ArrayList<IncassListItem> incass = getUnsendedIncass();
			if( incass.size() == 0 ) {
				if(unsended != null) {
					needRemove.add(unsended.created);
					unsended = null;
				}
			} else {
				if(unsended == null) {
					unsended = new com.grsoft.dataobjects.IncassListData();
					unsended.created = Util.getDateTime();
					unsended.number = "";
					unsended.remark = "";
					unsended.items = new ArrayList<IncassListItem>();
				}
				unsended.items = incass;
				DbWriter w = new DbWriter();
				DbWriter.checkDBTable(unsended.getClass());
				w.insertRecord(unsended);
				w.close();
			}
			
			if( unsended != null )
				items.add(0, unsended);
			
			IncassListImpl ii = new IncassListImpl();
			for(Date d : needRemove) {
				ii.getData().created = d;
				if( ii.read() )
					ii.delete();
			}
			ii.close();
			
			notifyDataSetChanged();
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			IncassListData il = (IncassListData)getGroup(groupPosition);
			if( il == null )
				return null;
			return childPosition < il.items.size() ? il.items.get(childPosition) : null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return getGroupCount() * groupPosition + childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(IncassList.this, R.layout.incass_item_row, null);
			IncassListItem il = (IncassListItem)getChild(groupPosition, childPosition);
			if( il != null ) {
				Incass i = incassDoc.getData();
				i.created = il.created;
				if( incassDoc.read() ) {
					Org o = org.getData();
					o.id = i.id;
					org.read();
					
					TextView tv;
					tv = (TextView)view.findViewById(R.id.tvText);
					tv.setText(o.name);
					
					tv = (TextView)view.findViewById(R.id.tvDate);
					tv.setText(Util.simpleDateFormat.format(i.date));
					
					tv = (TextView)view.findViewById(R.id.tvSum);
					tv.setText(Util.IntToScaleStr(i.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
				}
			}
			return view;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
			if(view == null)
				view = View.inflate(IncassList.this, R.layout.incass_group_row, null);
			IncassListData i = (IncassListData)getGroup(groupPosition);
			if( i != null ) {
				String text = i.number;
				if( i.remark != null && i.remark.length() > 0 ) {
					text += "<br><i>" + i.remark + "</i>";
				}

				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvText);
				tv.setText(Html.fromHtml(text));
				
				tv = (TextView)view.findViewById(R.id.tvDate);
				tv.setText(Util.simpleDateFormat.format(i.created));
				
				tv = (TextView)view.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(i.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}
			return view;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			IncassListData il = (IncassListData)getGroup(groupPosition);
			if( il == null )
				return 0;
			return il.items.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupPosition < items.size() ? items.get(groupPosition) : null;
		}

		@Override public int getGroupCount() { return items.size(); }

		@Override public long getGroupId(int groupPosition) { return groupPosition; }

		@Override public boolean hasStableIds() { return true; }
		@Override public boolean isChildSelectable(int groupPosition, int childPosition) { return false; }
		
	}
}
