package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DailyPlan;
import com.grsoft.dataobjects.DailyPlanItem;
import com.grsoft.dataobjects.DailySalesData;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DailyPlanImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.Documents;
import com.grsoft.napoleon.R;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

public class DailyPlanDoc extends DateDocType {
	static DailyPlanDoc instance = null;

	DailySalesData salesData = null;
	
	public static DailyPlanDoc instance() {
		if(instance == null)
			instance = new DailyPlanDoc();
		return instance;
	}
	
	DailyPlanDoc() {
		super("Дневной план", "DailyPlan", DailyPlanImpl.class);
	}
	
	@Override public int getResurceId() { return R.drawable.daily_plan; }
	
	@Override
	public void refreshDocSum() throws RuntimeException {
		Date d = Util.getDate(); 
		try {
			DbWriter w = new DbWriter();
			OrgSum os = new OrgSum();
			os.type = this.name;
			os.date = new Date();
			
			DataBaseManager.getDataBase().execSQL("DELETE FROM '" + os.getTableName() + "' WHERE type='" + this.name + "'");

			final Map<String, Integer> weights = new HashMap<String, Integer>();
			String where = "date >= " + Long.toString(d.getTime());
			DataTraveler.travel(DailyPlan.class, new DataTraveler.Travel<DailyPlan>(){

				@Override
				public boolean travel(DataTraveler<DailyPlan> item) {
					Integer w = weights.get(item.data.id);
					if(w == null)
						w = 0;
					for(DailyPlanItem i : item.data.items) {
						w += i.weight;
					}
					weights.put(item.data.id, w);
					return true;
				}
			}, where);
			
			for(Entry<String, Integer> kv : weights.entrySet()) {
				os.sum = ((long)kv.getValue()) * Consts.SUM_SCALE / Consts.WEIGHT_SCALE;
				os.id = kv.getKey();
				w.insertRecord(os);				
			}
			
//			String stmt = "select count(*), id from " + (new DailyPlan()).getTableName() + " where date >= " + Long.toString(d.getTime()) + " group by id";
//			android.database.Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
//			while(c.moveToNext()) {
//				os.sum = c.getInt(0) * Consts.SUM_SCALE;
//				os.id = c.getString(1);
//				w.insertRecord(os);
//			}
//			c.close();
			w.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		
		if(documentsView instanceof Documents)
			salesData = null;
		
		tv = (TextView) documentsView .findViewById(R.id.SumColumnTitle);
		if(tv != null)
			tv.setText(R.string.sum);
		tv = (TextView) documentsView .findViewById(R.id.DateTitle);
		if(tv != null)
			tv.setText(R.string.date);
		tv = (TextView) documentsView .findViewById(R.id.NameTitle);
		if(tv != null)
			tv.setText("");
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv = (TextView) documentsView.findViewById(R.id.tvMainDocValColTitle);
		
		if (tv != null)
			tv.setText("Планов");
		
		tv = (TextView) documentsView .findViewById(R.id.SumColumnTitle);
		if(tv != null)
			tv.setText("Факт");
		tv = (TextView) documentsView .findViewById(R.id.DateTitle);
		if(tv != null)
			tv.setText("План");
		tv = (TextView) documentsView .findViewById(R.id.NameTitle);
		if(tv != null) {
			tv.setVisibility(View.VISIBLE);
			tv.setText("Дата");
		}
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DailyPlanImpl dd = (DailyPlanImpl)doc;
		if(salesData == null)
			salesData = DailySalesData.load(dd.getId(), null);
		
		TextView tv;
		tv = (TextView) view.findViewById(R.id.tvOther);
		tv.setText(Util.simpleDateFormat.format(doc.getDate()));
		
		tv = (TextView) view.findViewById(R.id.tvDate);
		tv.setText(Util.IntToScaleStr(dd.countTotal(), Consts.WEIGHT_SCALE, Util.DEC_DELIM, false));
		
		String fact = "";
		long factW = salesData.countTotal(dd.getData());
		if(factW > 0)
			fact = Util.IntToScaleStr(factW, Consts.WEIGHT_SCALE, Util.DEC_DELIM, false);
		tv = (TextView) view.findViewById(R.id.tvSum);
		tv.setText(fact);
//		super.setView(adapter, view, doc);
	}
	
	@Override
	protected String getValueFromOrgSum(OrgSumImpl os) {
		String text = "";
		if(os != null && os.read())
			text = Long.toString(os.getData().sum / Consts.SUM_SCALE); 
		return text;
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		int count = 0;
		Date d = Util.getDate(); 
		try {
			String stmt = "select count(*) from " + (new DailyPlan()).getTableName() + " where id='" + orgId + "' and date >= " + Long.toString(d.getTime());
			android.database.Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
			if(c.moveToNext()) {
				count = c.getInt(0);
			}
			c.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		OrgSum os = new OrgSum();
		os.id = orgId;
		os.sum = count * Consts.SUM_SCALE;
		os.date = new Date();
		os.type = this.name;
		
		DbWriter w = new DbWriter();
		w.insertRecord(os);
		w.close();
	}
}
