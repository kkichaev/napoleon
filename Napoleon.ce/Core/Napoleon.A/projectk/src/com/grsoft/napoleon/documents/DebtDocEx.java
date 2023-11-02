package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.DocumentsEx;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDocEx уже создан!");
		instance = new DebtDocEx();
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		LinearLayout llDebtInfo = (LinearLayout) documentsView.findViewById(R.id.llDebtInfo);
		
		if (llDebtInfo != null){
			llDebtInfo.setVisibility(View.VISIBLE);
			
			ConfigImpl configImpl = new ConfigImpl();
			configImpl.getData().key = "ДатаСинхрониазацииДолгов";
			
			if (configImpl.read()){
				TextView tvSyncData = (TextView) documentsView.findViewById(R.id.tvSyncData);
				tvSyncData.setText(configImpl.getData().value);
			}
			
			configImpl.close();
			
			if (documentsView instanceof DocumentsEx){
				TextView tvDebtInfo = (TextView) documentsView.findViewById(R.id.tvDebtInfo);
				tvDebtInfo.setText(getDebtInfo(((DocumentsEx)documentsView).getOrgId()));
			}
		}
		
		TextView tvNameTitle = (TextView) documentsView.findViewById(R.id.NameTitle);
		
		if (tvNameTitle != null){
			tvNameTitle.setTextColor(documentsView.getResources().getColor(R.color.black));
			tvNameTitle.setText("Дата/Номер");
		}
		
		TextView tvDateTitle = (TextView) documentsView.findViewById(R.id.DateTitle);
		
		if (tvDateTitle != null)
			tvDateTitle.setText("Долг");
		
		TextView tvSumColumnTitle = (TextView) documentsView.findViewById(R.id.SumColumnTitle);
		
		if (tvSumColumnTitle != null)
			tvSumColumnTitle.setText("Пр.долг");
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		LinearLayout llDebtInfo = (LinearLayout) documentsView.findViewById(R.id.llDebtInfo);
		
		if (llDebtInfo != null)
			llDebtInfo.setVisibility(View.GONE);
		
		TextView tvNameTitle = (TextView) documentsView.findViewById(R.id.NameTitle);
		
		if (tvNameTitle != null)
			tvNameTitle.setTextColor(0);
		
		TextView tvDateTitle = (TextView) documentsView.findViewById(R.id.DateTitle);
		
		if (tvDateTitle != null)
			tvDateTitle.setText("Дата");
		
		TextView tvSumColumnTitle = (TextView) documentsView.findViewById(R.id.SumColumnTitle);
		
		if (tvSumColumnTitle != null)
			tvSumColumnTitle.setText("Сумма");
	}
	
	private String getDebtInfo(String orgid){
		String result = "";
		
		try{
			int debt = 0;
			int out_debt = 0;
			
			DocList list = docList(orgid, null);
			for( int i=0; i<list.getCount(); i++ )
			{
				Document<?> d = list.get(i);
				if( d != null ){
					debt += d.sum();
					
					if (d.getData() instanceof DeliveryEx){
						out_debt += ((DeliveryEx) d.getData()).out_deb;
					}
				}
				
			}
			list.close();
			
			return String.format("%s/%s", Util.IntToScaleStr(debt, Consts.SUM_SCALE), 
					Util.IntToScaleStr(out_debt, Consts.SUM_SCALE));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		
		int textColor = (doc.getData() instanceof DeliveryEx) ? 
				Util.GrServerColorToSystem(((DeliveryEx)doc.getData()).color) 
				: view.getResources().getColor(R.color.black);
				
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setText(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
		tv.setGravity(Gravity.RIGHT);
		tv.setTextColor(textColor);
		tv.setPadding(0, 0, 0, 0);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		tv.setTextColor(textColor);
		
		if (doc.getData() instanceof DeliveryEx)
			tv.setText(Util.IntToScaleWStr(((DeliveryEx)doc.getData()).out_deb , Consts.SUM_SCALE, 2, false));
		else
			tv.setText("0");
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(doc.getDescription());		
		tv.setTextColor(textColor);
	}
}
