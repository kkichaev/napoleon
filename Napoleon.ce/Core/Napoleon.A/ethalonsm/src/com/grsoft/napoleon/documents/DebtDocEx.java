package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrgSumEx;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public interface DebtDbObject{
		int getSum();
		int getExp();
		Date getDate();
		String getDescr();
	}
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}

	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Сумма");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Долг");
		
		tv = (TextView)documentsView.findViewById(R.id.SumColumnTitle);
		if( tv != null )
			tv.setText("Просроч.");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DataObject dobj = doc.getData();
		if( dobj instanceof DebtDbObject ){
			TextView tv;
			
			DebtDbObject ddo = (DebtDbObject)dobj;
			tv = (TextView)view.findViewById(R.id.tvDate);
			tv.setText(Util.IntToScaleStr(ddo.getSum(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
			
			tv = (TextView)view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(ddo.getExp(), 
					Consts.SUM_SCALE, Util.DEC_DELIM, false));
						
			tv = (TextView)view.findViewById(R.id.tvOther);
			String str;
			str = ddo.getDescr() + "\n" + Util.simpleDateFormat.format(ddo.getDate());
			tv.setText(str);
		}
	}
	
	class Pair{
		int val1;
		int val2;
		
		public Pair(int v1, int v2){
			val1 = v1;
			val2 = v2;
		}
	}
	@Override
	public void refreshDocSum() throws com.grsoft.network.exception.RuntimeException {
		Map<String, Pair> sums = new HashMap<String, Pair>();
		DocList list = docList(null, null);
		
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			DataObject dbobj = d.getData();
			
			if(dbobj instanceof DebtDbObject){
				String id = d.getId();
				
				if( sums.containsKey(id)){
					Pair entry = sums.get(id);
					entry.val1 += ((DebtDbObject) dbobj).getSum();
					entry.val2 += ((DebtDbObject) dbobj).getExp();
				} else
					sums.put(id, new Pair(((DebtDbObject) dbobj).getSum(),
							((DebtDbObject) dbobj).getExp()));
			}
		}
		list.close();
		
		OrgSumEx os = new OrgSumEx();
		DataBaseManager.getDataBase().execSQL(String.format("DELETE FROM '%s' WHERE type='%s'", 
				DataObjectInfo.getInstance().getTableName(os.getClass()), name));
		
		DbWriter w = new DbWriter();
		DbWriter.checkDBTable(OrgSumEx.class);
		os.type = this.name;
		for( Entry<String, Pair> v : sums.entrySet() ) {
			os.id = v.getKey();
			Pair p = v.getValue();
			os.sum = p.val1;
			os.sum2 = p.val2;
			w.insertRecord(os);
		}
		w.close();
	}
	
	protected String getValueFromOrgSum(OrgSumImpl orgSumImpl){
		StringBuilder result = new StringBuilder();
		long sum1 = 0;
		int sum2 = 0;
		
		if (orgSumImpl != null && orgSumImpl.read()){
			OrgSumEx ose = (OrgSumEx) orgSumImpl.getData();
			sum1 = ose.sum;
			sum2 = ose.sum2;
		}
		
		result.append(Util.IntToScaleStr(sum1, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		result.append("<br>");
		result.append(Util.IntToScaleStr(sum2, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		
		return result.toString();
	}
	
	public void updateTotalSum(Activity activity, int sum, int weight, int count, int textViewId){
		
		Bundle bundle = activity.getIntent().getExtras(); 
		if (bundle != null && bundle.getString(ExtrasConst.ORG_ID_STR) != null){
			OrgSumImpl oi = new OrgSumImpl();
			OrgSumEx os = (OrgSumEx) oi.getData();
			os.id = bundle.getString(ExtrasConst.ORG_ID_STR);
			os.type = DocType.getCurDoc().getName();
			oi.read();
			oi.close();
			
			StringBuilder text = new StringBuilder();
			text.append("<b>");
			text.append(Util.IntToScaleStr(os.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			text.append("<br>");
			text.append(Util.IntToScaleStr(os.sum2, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			text.append("</b>");
			
			TextView tvTotalSum = (TextView) activity.findViewById(textViewId);		
			tvTotalSum.setText(Html.fromHtml(text.toString()));
		}else
			super.updateTotalSum(activity, sum, weight, count, textViewId);
	}
}
