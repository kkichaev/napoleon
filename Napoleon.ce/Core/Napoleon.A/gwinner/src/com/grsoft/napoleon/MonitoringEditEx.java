package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Monitoring;
import com.grsoft.dataobjects.MonitoringDocItem;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.MonitoringVolumeItem;
import com.grsoft.dataobjects.impl.MonitoringDocImpl;
import com.grsoft.dataobjects.impl.MonitoringItemImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MonitoringEditEx extends MonitoringEdit {
	@Override
	protected int getContentViewId() {
		return R.layout.monitoring_editex;
	}
	
	@Override
	protected void applayAdapter(){
		ListView listView = (ListView)findViewById(R.id.lvItems);
		listView.setAdapter(new Adapter());
	}
	
	@Override
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
	
	class Adapter extends BaseAdapter{
		List<Item> items = new ArrayList<Item>();
		
		public Adapter(){
			items.clear();
			MonitoringItemImpl mii = new MonitoringItemImpl();
			MonitoringItem mi = mii.getData();
			for(MonitoringDocItem mdi : doc.getData().items) {
				mi.id = mdi.id;
				if( mii.read() ) {
					Item item = new Item();
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
				convertView = View.inflate(MonitoringEditEx.this, R.layout.monitor_group_rowex, null);
			Item i = (Item) getItem(position);
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
					@Override public void onClick(View v) { setFace((TextView)v, (Item)v.getTag()); }
				});

				tv = (TextView)convertView.findViewById(R.id.tvSKU);
				text = "SKU:" + Integer.toString(i.item.sku);
				tv.setText(text);
				tv.setTag(i);
				tv.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { setSKU((TextView)v, (Item)v.getTag()); }
				});
				
				tv = (TextView)convertView.findViewById(R.id.tvCost);
				text = "Цена:" + Util.IntToScaleStr(i.item.cost,Consts.SUM_SCALE);
				tv.setText(text);
				tv.setTag(i);
				tv.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { setCost((TextView)v, (Item)v.getTag()); }
				});
			}
			return convertView;
		}
		
		protected void setSKU(final TextView tv, final Item item) {
			InputNumberDlg.open(MonitoringEditEx.this, new InputNumber() {				
				@Override public int getValue() { return item.item.sku; }
				@Override public void applayInput(int value, Object... params) {
					item.item.sku = value;
					String text = "SKU:" + Integer.toString(item.item.sku);
					tv.setText(text);
				}
			}, 1, true, "Число SKU");
		}

		protected void setFace(final TextView tv, final Item item) {
			InputNumberDlg.open(MonitoringEditEx.this, new InputNumber() {				
				@Override public int getValue() { return item.item.face; }
				@Override public void applayInput(int value, Object... params) {
					item.item.face = value;
					String text = "Фейсов:" + Integer.toString(item.item.face);
					tv.setText(text);
				}
			}, 1, true, "Число фейсов");
		}
		
		protected void setCost(final TextView tv, final Item item) {
			InputNumberDlg.open(MonitoringEditEx.this, new InputNumber() {				
				@Override public int getValue() { return item.item.cost; }
				@Override public void applayInput(int value, Object... params) {
					item.item.cost = value;
					String text = "Цена:" + Util.IntToScaleStr(item.item.cost,Consts.SUM_SCALE);
					tv.setText(text);
				}
			}, Consts.SUM_SCALE, true, "Цена SKU");
		}
		
	}
}