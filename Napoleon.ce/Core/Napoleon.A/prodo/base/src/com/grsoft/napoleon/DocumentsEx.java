package com.grsoft.napoleon;

import java.util.Date;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageButton;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPlan;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.ProdoWorkTimeListener;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {

	ProdoWorkTimeListener wtl;
	
	public static String makeOrgInfo(Org o) {
		OrgEx oe = (OrgEx)o;
		String info = oe.name;
		info += "<br>Лимит: " + oe.limit;
		info += "<br>График оплат: " + oe.payData;
		
		info = appendPlanInfo(o.id, info);
		
		return info;
	}
	
	interface  PlanHandler {
		void inflate(int plan, int weight);
	}
	
	public static double calcPartPlan(Date p1, Date p2, Date r1, Date r2){
		 if (r1.getTime() < p1.getTime())
            r1 = p1;

         if (r2.getTime() > p2.getTime())
            r2 = p2;

         double plan = DatePeriod.daysDiff(p1, p2) + 1;
         double range = DatePeriod.daysDiff(r1, r2);

         return range / plan;
	}
	
	public static void readPlan(String id, PlanHandler handler, Date start, Date finish){
		//String now = Long.toString(new Date().getTime());
		final String where = "id=? and start <= ? and finish >= ?";
		final String VALUE = "value";
		final String START = "start";
		final String FINISH = "finish";
		
		Cursor c = null;
		
		try{
			c = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(OrgPlan.class),
					new String[]{VALUE, START, FINISH}, where, new String[]{id, Long.toString(finish.getTime()), 
						Long.toString(start.getTime())}, null, null, null);
			
			if(c.moveToFirst()){
				int plan = c.getInt(c.getColumnIndex(VALUE));
				
				Date p1 = new Date(c.getLong(c.getColumnIndex(START)));
				Date p2 = new Date(c.getLong(c.getColumnIndex(FINISH)));
				
				plan = (int)(plan * calcPartPlan(p1, p2, start, finish));
				
				if(plan > 0){
					int weight = 0;
					
					OrderDoc order = (OrderDoc) OrderDoc.instance();
					DatePeriod dp = new DatePeriod(start, finish);
					com.grsoft.napoleon.documents.DocList list = order.docList(id, null, dp);
					
					for(Document<?> d: list)
						weight += ((OrderImpl) d).weight();
					
					handler.inflate(plan, weight);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (c != null)
				c.close();
		}
	}
	
	private static String appendPlanInfo(String id, String info) {
		final StringBuilder result = new StringBuilder();
		result.append(info);
		DatePeriod dp = DatePeriod.createRange(Util.getDate(), 24 * 60);
		readPlan(id, new PlanHandler() {
			
			@Override
			public void inflate(int plan, int weight) {
				int percent = 0;
				
				if(plan > 0)
					percent = (int) Math.round(((double) weight	/ plan * 100));
				
				result.append("<br>План: " + Util.IntToScaleStr(plan / Consts.WEIGHT_SCALE * Consts.WEIGHT_SCALE , Consts.WEIGHT_SCALE) + " / " + Util.IntToScaleStr(weight, Consts.WEIGHT_SCALE) 
						+ " / " + Integer.toString(percent));
			}
		}, dp.begin, dp.end);
		
		return result.toString();
	}

	@Override protected String orgInfo(Org o) { return makeOrgInfo(o); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wtl = new ProdoWorkTimeListener((NapoleonApp)getApplication(), org.getData().id, (ImageButton) findViewById(R.id.btnStart), btnNewDoc);
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
		
	@Override
	public void onBackPressed() {
		if( wtl.isInWork() )
			return;
		super.onBackPressed();
	}
	
	@Override
	protected boolean canCreateDoc(DocType docType) {
		return wtl.isInWork() && super.canCreateDoc(docType);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else {
			super.adjustViewForDocType(docType);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		tvOrgInfo.setText(Html.fromHtml(orgInfo(org.getData())));
	}
}
