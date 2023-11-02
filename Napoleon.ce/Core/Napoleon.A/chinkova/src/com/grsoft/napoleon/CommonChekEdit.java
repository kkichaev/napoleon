package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.CheckStatusHandler;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.CommonChek;
import com.grsoft.dataobjects.CommonChekItem;
import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.RequestChek;
import com.grsoft.dataobjects.impl.CommonCheckImpl;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RequestChekImpl;
import com.grsoft.napoleon.dialogs.SelectDialog;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RequestCheckDoc;
import com.grsoft.network.ChekStatusSender;
import com.grsoft.network.CommonIncassExportHitching;
import com.grsoft.network.DataObjectSender;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class CommonChekEdit extends CommonIncassEditBase {
	protected static final int DIALOG_FROM_ID = 1;
	protected static final int DIALOG_TO_ID = 3;
	
	Date dateFrom = new Date();
	Date dateTo = new Date();
	List<ChekData> data = new ArrayList<ChekData>();
	SelectCheck df;
	ChekAdapter chekAdapter;
	
	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, CommonChekEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected CommonIncassImplBase<? extends CommonIncass> createDocument() { return new CommonCheckImpl(); }
	
	@Override
	protected void doSend() {
		CommonChek ci = (CommonChek) doc.getData();
		writeDoc(ci);
		doc.close();
		
		List<ObjectExportListener> h = new ArrayList<ObjectExportListener>();
		h.add(new CommonIncassExportHitching(doc, sendObjectName()));
		h.add(new ChekStatusSender(ci, 1));
		
		new DataObjectSender(this, R.id.btnSend, h){
			protected void onPostExecute(Boolean result) { finish(); };
		}.execute((Void)null);
	}
	
	@Override
	protected String sendObjectName() { return "CommonChek"; }

	@Override
	protected void initData() {
		super.initData();
		refreshChek();
	}
	
	@Override
	protected void initUI() {
		super.initUI();
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				df = new SelectCheck();
				df.show(getSupportFragmentManager(), df.getClass().toString());
				refreshChek();
				chekAdapter = new ChekAdapter();
			}
		});
	}

	OrgEx getOrg(String id, HashMap<String, OrgEx> orgs, OrgImpl oi) {
		OrgEx o = orgs.get(id);
		if(o == null) {
			OrgEx org = (OrgEx)oi.getData();
			org.id = id;
			if( oi.read() )
				o = (OrgEx) org.clone();
			else {
				o = new OrgEx();
				o.id = id;
			}
			
			orgs.put(id, o);
		}

		return o;
	}

	void refreshChek() {
		HashMap<Date, Boolean> loaded = new HashMap<Date, Boolean>();
		HashMap<String, OrgEx> orgs = new HashMap<String, OrgEx>();
		OrgImpl oi = new OrgImpl();
		
		data.clear();

		Spinner sp = (Spinner) findViewById(R.id.spFirm);
		KeyValue kv = (KeyValue) sp.getSelectedItem(); 
		if(kv != null && kv.key.toString().length() > 0) {
			String orgWhere = "org='" + kv.key.toString() + "'";
						
			String where = orgWhere + " and handleStatus = 1 and created >= " + Long.toString(Util.getDayStart(dateFrom).getTime()) + 
					" and created <= " + Long.toString(Util.getDayEnd(dateTo).getTime());
			
			DocList dl = RequestCheckDoc.instance().docList(null, null, where);
			for(Document<?> d : dl) {
				String id = d.getId();
				OrgEx o = getOrg(id, orgs, oi);

				ChekData cd = new ChekData();
				cd.org = o;
				cd.chek = (RequestChek) d.getData().clone();
				cd.checked = (((CommonCheckImpl)doc).haveChek(cd.chek) != null);
				data.add(cd);
				loaded.put(cd.chek.created, true);
			}
			dl.close();
		}
		oi.close();
		
		// add existing chek
		RequestChekImpl rci = new RequestChekImpl();
		RequestChek rc = rci.getData();
		
		for(CommonIncassItem cii : doc.getData().items) {
			Date created = ((CommonChekItem)cii).created;
			if( !loaded.containsKey(created)) {
				rc.created = created;
				rci.read();
				OrgEx o = getOrg(rc.id, orgs, oi);
				
				ChekData cd = new ChekData();
				cd.org = o;
				cd.chek = (RequestChek) rc.clone();
				cd.checked = true;
				data.add(cd);
			}
		}
		
		Collections.sort(data);
	}
	
	@Override
	protected void editItem(CommonIncassItem item) {
	}
	
	@Override
	protected void deleteItem(CommonIncassItem cii) {
		CheckStatusHandler cch = new CheckStatusHandler();
		cch.update(((CommonChekItem)cii).created, ChekBase.CHEK_COMMITED);
		cch.close();

		super.deleteItem(cii);
		doc.write();
		refreshChek();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(!doc.isExported())
			getMenuInflater().inflate(R.menu.common_chek_edit_context_menu, menu);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent i) {
		if( i != null ){
			if( requestCode == DIALOG_FROM_ID ) {
				Date curDate = new Date();
				long ct = i.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
				dateFrom = new Date(ct);
			}
			if( requestCode == DIALOG_TO_ID ) {
				Date curDate = new Date();
				long ct = i.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
				dateTo = new Date(ct);
			}
			if(df != null)
				df.refreshDate();
			refreshChek();
			if( chekAdapter != null )
				chekAdapter.notifyDataSetChanged();
		}
		
		super.onActivityResult(requestCode, resultCode, i);
	}
	
	class SelectCheck extends SelectDialog {
		TextView tvFrom, tvEnd; 

		@Override public int getViewId() { return R.layout.select_chek; }
		@Override public int getTitle() { return R.string.incas_chek; }

		@Override
		public void prepareView(View view) {
			ListView listView = (ListView) view.findViewById(R.id.list);
			listView.setDividerHeight(0);
			listView.setAdapter(chekAdapter);
			
			tvFrom = (TextView)view.findViewById(R.id.tvDateFrom);
			tvFrom.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(CommonChekEdit.this, CalendarActivity.class);
					i.putExtra(ExtrasConst.DATE_TAG, dateFrom.getTime());
					CommonChekEdit.this.startActivityForResult(i, DIALOG_FROM_ID);
				}
			});
			tvEnd = (TextView)view.findViewById(R.id.tvDateTo);
			tvEnd.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(CommonChekEdit.this, CalendarActivity.class);
					i.putExtra(ExtrasConst.DATE_TAG, dateTo.getTime());
					CommonChekEdit.this.startActivityForResult(i, DIALOG_TO_ID);
				}
			});
			refreshDate();
		}
		
		public void refreshDate() {
			String txt = "Дата с <font color='blue'><u>" + Util.simpleDateFormat.format(dateFrom) + "</u></font>";
			tvFrom.setText(Html.fromHtml(txt));
			
			txt = " по <font color='blue'><u>" + Util.simpleDateFormat.format(dateTo) + "</u></font>";
			tvEnd.setText(Html.fromHtml(txt));
		}
		
		@Override
		public void onOKButtonPressed(View result) {
			if(doc.isExported())
				return;
			
			HashMap<Date, CommonChekItem> items = new HashMap<Date, CommonChekItem>();
			CommonChek cc =  (CommonChek)doc.getData();
			for(CommonIncassItem ci : cc.items)
				items.put(((CommonChekItem)ci).created, (CommonChekItem) ci);
			
			CheckStatusHandler csh = new CheckStatusHandler();
			for(ChekData cd : data) {
				if(cd.checked == false)
					continue;
				
				if(items.containsKey(cd.chek.created)) {
					items.remove(cd.chek.created);
					continue;
				}
				
				CommonChekItem newI = new CommonChekItem();
				newI.created = cd.chek.created;
				newI.sum = (int)cd.chek.sum;
				newI.id = cd.org.id;
				csh.update(newI.created, ChekBase.CHEK_IN_COMMON_LIST);
				cc.items.add(newI);
			}
			
			for(CommonChekItem cci : items.values()) {
				csh.update(cci.created, ChekBase.CHEK_COMMITED);
				cc.items.remove(cci);
			}
			
			doc.write();
			Spinner spOrg = (Spinner)findViewById(R.id.spFirm);
			spOrg.setEnabled((doc.isEditable() && cc.items.size() == 0));
			
			((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
		}
	}
	
	class ChekAdapter extends BaseAdapter {

		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			ChekData cd = (ChekData)getItem(arg0);
			
			if( view == null )
				view = View.inflate(CommonChekEdit.this, R.layout.select_chek_row, null);

			TextView tv = (TextView) view.findViewById(R.id.tvName);
			tv.setText(cd.org.name);
			
			tv = (TextView) view.findViewById(R.id.tvAddress);
			tv.setText(cd.org.address);

			CheckBox cb = (CheckBox) view.findViewById(R.id.cbSelected);
			cb.setTag(cd);
			cb.setOnCheckedChangeListener(onCheck);			
			cb.setChecked(cd.checked);
			
			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(cd.chek.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			tv = (TextView) view.findViewById(R.id.tvDate);
			tv.setText(Util.simpleDateFormat.format(cd.chek.created));
			
			view.setBackgroundResource(arg0 % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
			
			return view;
		}		
	}
	
	CheckBox.OnCheckedChangeListener onCheck = new CheckBox.OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			ChekData cdata = (ChekData)buttonView.getTag();
			cdata.checked = isChecked;
		}
	};
}

class ChekData implements Comparable<ChekData> {
	public RequestChek chek;
	public OrgEx org;
	public boolean checked = false;
	
	@Override
	public int compareTo(ChekData o) {
		int cmp = org.orgid.compareTo(o.org.orgid);
		if( cmp != 0 )
			return cmp;
		cmp = org.name.compareTo(o.org.name);
		if( cmp != 0 )
			return cmp;
		
		return chek.created.compareTo(o.chek.created);
	}
}