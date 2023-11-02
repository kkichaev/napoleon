package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ActiveOrgActionItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgAction;
import com.grsoft.dataobjects.impl.ActiveOrgActionsImpl;
import com.grsoft.dataobjects.impl.OrgActionImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.ActiveOrgActionsDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.RegDurationActivity;

public class ActiveActionsDetail extends RegDurationActivity implements SendResultListener {

	boolean started = true;
	Adapter adapter;
	ActiveOrgActionsImpl doc = new ActiveOrgActionsImpl();
	OrgActionImpl action = new OrgActionImpl();
	List<OrgAction> activeActions;
	
	public static void open(Context context, ActiveOrgActionsImpl doc) {
		Intent i = new Intent(context, ActiveActionsDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.actions_detail);
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgImpl o = new OrgImpl();
		Org org = o.getData();
		org.id = doc.getId();
		o.read();
		o.close();

		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.name);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		adapter = new Adapter();
		adapter.refresh();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				DataItem item = (DataItem) arg0.getAdapter().getItem(arg2);
				doc.changeItem(item.action.id);
				adapter.refresh();
				adapter.notifyDataSetChanged();
			}
		});
	}
	
	View.OnClickListener changeAction = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			OrgAction oa = (OrgAction)arg0.getTag();
			doc.changeItem(oa.id);
			adapter.refresh();
			adapter.notifyDataSetChanged();
		}
	};
	
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), ActiveOrgActionsDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}
	
	@Override
	protected void onDestroy() {
		doc.close();
		action.close();
		super.onDestroy();
	}

	@Override
	public void postSendExecute(boolean result) {
		doc.read(doc.getRowid(), false);
	}
	
	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.getData().items.size() == 0) {
			String id = doc.getId();
			doc.delete();
			ActiveOrgActionsDoc.instance().refreshDocSum(id);
		}
		super.onBackPressed();
	}

	class Adapter extends BaseAdapter {
		List<DataItem> items;
		
		public Adapter() { refresh(); }
		
		@Override public int getCount() { return items.size(); }
		
		public void refresh() {
			items = new ArrayList<DataItem>();
			
			HashMap<String, ActiveOrgActionItem> docItems = new HashMap<String, ActiveOrgActionItem>();
			for(ActiveOrgActionItem ai : doc.getData().items) 
				docItems.put(ai.id, ai);

			OrgAction oa = new OrgAction();
			String table = DataObjectInfo.getInstance().getTableName(oa.getClass());
			String now = Long.toString((new Date()).getTime());
			DbReader r = new DbReader();
			boolean bdo = r.select(oa, table, "[start] <= " + now + " and [end] >= " + now, "[start]");
			while(bdo) {
				DataItem item = new DataItem(oa, docItems.get(oa.id));
				items.add(item);
				
				oa = new OrgAction();
				bdo = r.selectNext(oa);
			}
			r.close();
			
		}

		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null ) {
				view = View.inflate(ActiveActionsDetail.this, R.layout.actioins_detail_row, null);
			}
			
			DataItem item = (DataItem)getItem(arg0);

			TextView tv = (TextView)view.findViewById(R.id.tvName);
			tv.setText(item.action.name);
			
			CheckBox cb = (CheckBox)view.findViewById(R.id.cbAction);
			cb.setChecked(item.item != null);
			cb.setTag(item.action);
			cb.setOnClickListener(changeAction);
			return view;
		}		
	}
}

class DataItem {
	public DataItem(OrgAction oa, ActiveOrgActionItem item) {
		this.action = oa;
		this.item = item;
	}
	
	public OrgAction action;
	public ActiveOrgActionItem item;
}
