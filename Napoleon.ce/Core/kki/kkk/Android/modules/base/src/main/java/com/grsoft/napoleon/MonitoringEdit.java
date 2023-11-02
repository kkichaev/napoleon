package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Monitoring;
import com.grsoft.dataobjects.MonitoringDocItem;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.MonitoringVolumeItem;
import com.grsoft.dataobjects.impl.MonitoringDocImpl;
import com.grsoft.dataobjects.impl.MonitoringItemImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class MonitoringEdit extends BaseActivity implements ScriptActivity {
	public static Class<? extends MonitoringEdit> activity = MonitoringEdit.class;
	MonitoringDocImpl doc = new MonitoringDocImpl();
	
	public static void open(Context ctx, MonitoringDocImpl doc) {
		Intent i = new Intent(ctx, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(getContentViewId());
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID));
		
		Monitoring d = doc.getData();
		
		if(d.items != null && d.items.size() == 0)
			init(doc);
		
		EditText ed = (EditText)findViewById(R.id.edRemark);
		ed.setText(d.remark);
		
		applayAdapter();
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(v); }
		});

		ScriptHelper.initView(this, MonitoringDoc.instance().getObjectName(), doc.getData().created, doc.getId());
	}

	protected int getContentViewId() {
		return R.layout.monitoring_edit;
	}

	protected void applayAdapter() {
		ListView listView = (ListView)findViewById(R.id.lvItems);
		listView.setAdapter(new Adapter());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	protected void send(View v) {
		if(doc.isEditable())
			save();
		
		new DocumentSender(MonitoringEdit.this, v, 
				DocType.getCurDoc().getObjectName(), doc, 
				doc.getRowid()).execute((Void[])null);
	}

	protected void init(MonitoringDocImpl d) {
		Monitoring md = d.getData();
		MonitoringItem item = new MonitoringItem();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(item.getClass());
		boolean bdo = r.select(item, table, "", "name");
		while(bdo) {
			MonitoringDocItem mdi = new MonitoringDocItem();
			mdi.items = new ArrayList<MonitoringVolumeItem>();
			mdi.id = item.id;
			md.items.add(mdi);
			bdo = r.selectNext(item);
		}
		r.close();
	}
	
	@Override
	protected void onDestroy() {
		doc.close();
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			save();
			finish();
			return true;
		}else
			return super.onKeyDown(keyCode, event);
	}

	private void save() {
		if( doc.isEditable() ) {
			Monitoring d = doc.getData();
			EditText ed = (EditText)findViewById(R.id.edRemark);
			d.remark = ed.getText().toString();
			
			doc.write();
		}
	}

	@Override
	public boolean closeDocument() {
		save();
		return true;
	}

	class Adapter extends BaseAdapter{
		List<_MntrItem> items = new ArrayList<_MntrItem>();
		
		public Adapter(){
			buildData();
		}

		protected void buildData() {
			items.clear();
			MonitoringItemImpl mii = new MonitoringItemImpl();
			MonitoringItem mi = mii.getData();
			for(MonitoringDocItem mdi : doc.getData().items) {
				mi.id = mdi.id;
				if( mii.read() ) {
					_MntrItem item = new _MntrItem();
					item.name = mi.name;
					item.isOur = mi.isOur();
					item.item = mdi;
					items.add(item);
				}
			}
			mii.close();
		}
		
		@Override
		public int getCount() {
			return items.size();
		}
	
		@Override
		public Object getItem(int position) {
			return items.get(position);
		}
	
		@Override
		public long getItemId(int position) { return 0;	}
	
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(MonitoringEdit.this, R.layout.monitor_group_row, null);
			_MntrItem i = (_MntrItem) getItem(position);
			if( i != null ) {
				TextView tv;
				tv = (TextView)convertView.findViewById(R.id.tvText);
				tv.setText(i.name);

				String text;
				tv = (TextView)convertView.findViewById(R.id.tvFace);
				text = "Фейсов:" + Integer.toString(i.item.face);
				tv.setText(text);
				tv.setTag(i);
				tv.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { setFace((TextView)v, (_MntrItem)v.getTag()); }
				});

				tv = (TextView)convertView.findViewById(R.id.tvSKU);
				text = "SKU:" + Integer.toString(i.item.sku);
				tv.setText(text);
				tv.setTag(i);
				tv.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { setSKU((TextView)v, (_MntrItem)v.getTag()); }
				});
				
				tv = (TextView)convertView.findViewById(R.id.tvCost);
				text = "Цена:" + Util.IntToScaleStr(
						((MonitoringDocItem)i.item).cost,Consts.SUM_SCALE);
				tv.setText(text);
				tv.setTag(i);
				tv.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { setCost((TextView)v, (_MntrItem)v.getTag()); }
				});
			}
			return convertView;
		}
		
		protected void setSKU(final TextView tv, final _MntrItem item) {
			InputNumberDlg.open(MonitoringEdit.this, new InputNumber() {				
				@Override public int getValue() { return item.item.sku; }
				@Override public void applayInput(int value, Object... params) {
					item.item.sku = value;
					String text = "SKU:" + Integer.toString(item.item.sku);
					tv.setText(text);
				}
			}, 1, true, "Число SKU");
		}

		protected void setFace(final TextView tv, final _MntrItem item) {
			InputNumberDlg.open(MonitoringEdit.this, new InputNumber() {				
				@Override public int getValue() { return item.item.face; }
				@Override public void applayInput(int value, Object... params) {
					item.item.face = value;
					String text = "Фейсов:" + Integer.toString(item.item.face);
					tv.setText(text);
				}
			}, 1, true, "Число фейсов");
		}
		
		protected void setCost(final TextView tv, final _MntrItem item) {
			InputNumberDlg.open(MonitoringEdit.this, new InputNumber() {				
				@Override public int getValue() { return ((MonitoringDocItem)item.item).cost; }
				@Override public void applayInput(int value, Object... params) {
					((MonitoringDocItem)item.item).cost = value;
					String text = "Цена:" + Util.IntToScaleStr(
							((MonitoringDocItem)item.item).cost,Consts.SUM_SCALE);
					tv.setText(text);
				}
			}, Consts.SUM_SCALE, true, "Цена SKU");
		}
		
	}
}

class _MntrItem {
	public String name;
	public boolean isOur;
	public MonitoringDocItem item;	
}
