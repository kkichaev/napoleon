package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentMonthlyPlans;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class MonthlyPlans extends BaseActivity {
	static final String PLAN_TYPE = "PlanType";
	
	public enum PlanType { SalesPlan, PDZPlan };
	
	PlanType curPlan;
	
	public static void open(Context context, PlanType type) {
		Intent i = new Intent(context, MonthlyPlans.class);
		i.putExtra(PLAN_TYPE, type);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.monthly_plans);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		curPlan = (PlanType) b.getSerializable(PLAN_TYPE);
		
		AgentMonthlyPlans plan;
		plan = new AgentMonthlyPlans();
		Date cur = Util.getMonthStart(new Date());
		Date end = Util.getMonthEnd(new Date());
		String where = "date >= " + Long.toString(cur.getTime()) + " and date < " + Long.toString(end.getTime() + 24 * 3600* 1000);
		DbReader r = new DbReader();
		r.select(plan, plan.getTableName(), where);
		r.close();
		
		com.grsoft.napoleon.documents.DocList docs = DeliveryDoc.instance().docList(null, null, (curPlan == PlanType.SalesPlan) ? where : "");
		
		String title = "";
		List<MonthlyPlayRow> rows = new ArrayList<MonthlyPlayRow>();
		if(curPlan == PlanType.SalesPlan) {
			title = "Общий план продаж";
			loadSalesPlan(rows, plan, docs);
		} else {
			title = "План ПДЗ";
			loadPDZPlan(rows, plan, docs, cur);
		}
		
		docs.close();
		((TextView)findViewById(R.id.tvTitle)).setText(title);
		
		int nid[] = new int[] { R.id.tvName1, R.id.tvName2, R.id.tvName3 };
		int vid[] = new int[] { R.id.tvValue1, R.id.tvValue2, R.id.tvValue3 };
		
		for(int i=0; i<3; i++) {
			MonthlyPlayRow row = rows.get(i);
			TextView tv = (TextView)findViewById(nid[i]);
			tv.setText(Html.fromHtml(row.title));
			
			tv = (TextView)findViewById(vid[i]);
			tv.setText(Html.fromHtml(row.value));
		}
	}
	
	private void loadPDZPlan(List<MonthlyPlayRow> rows, AgentMonthlyPlans plan, DocList docs, Date startMonth) {
		Date checkDate = new Date(Util.getDate().getTime() + 24 * 3600 * 1000);
		long sum = 0;
		long pdz = 0;
		for(Document<?> d : docs) {
			Delivery doc = (Delivery) d.getData();
			if(doc.payDate.compareTo(checkDate) < 0) {
				pdz += doc.sumD;
			}
			//if( doc.date.compareTo(startMonth) >= 0)
				sum += doc.sumD;
		}		
		rows.add(new MonthlyPlayRow("Задолженность (руб)", Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false)));
		String text = "<b>" + Util.IntToScaleStr(pdz, Consts.SUM_SCALE) + " (" + getProcent(sum, pdz) + ")</b>";
		rows.add(new MonthlyPlayRow("Просроченная (руб)", text));
		
		text = Util.IntToScaleStr(plan.pdz, Consts.SUM_SCALE) + " %";
		rows.add(new MonthlyPlayRow("План ПДЗ", text));
	}

	String getProcent(long plan, long fact) {
		float pc = plan == 0 ? 0 : (float)fact / (float)plan;
		return Util.IntToScaleStr((long)(pc * 1000), 10, Util.DEC_DELIM, false) + "%";
	}
	
	private void loadSalesPlan(List<MonthlyPlayRow> rows, AgentMonthlyPlans plan, com.grsoft.napoleon.documents.DocList docs) {
		rows.add(new MonthlyPlayRow("План (руб)", Util.IntToScaleStr(plan.plan, Consts.SUM_SCALE)));
		
		long sum = 0;
		for(Document<?> d : docs) {
			sum += d.sum();
		}
		
		String text = Util.IntToScaleStr(sum, Consts.SUM_SCALE) + "&nbsp;&nbsp;&nbsp;" + getProcent(plan.plan, sum);
		rows.add(new MonthlyPlayRow("Факт", text));
		
		Calendar c = Calendar.getInstance(Locale.getDefault());
		float curDay = c.get(Calendar.DAY_OF_MONTH);
		float endDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		sum = (long)(sum * endDay / curDay);
		text = Util.IntToScaleStr(sum, Consts.SUM_SCALE) + "&nbsp;&nbsp;&nbsp;" + getProcent(plan.plan, sum);
		rows.add(new MonthlyPlayRow("Прогноз", text));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(PLAN_TYPE, curPlan);
		super.onSaveInstanceState(outState);
	}
}

class MonthlyPlayRow {
	public String title;
	public String value;
	
	public MonthlyPlayRow(String t, String v) {
		title = t;
		value = v;
	}
}
