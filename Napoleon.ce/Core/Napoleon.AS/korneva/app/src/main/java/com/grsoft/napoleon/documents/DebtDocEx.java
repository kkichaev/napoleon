package com.grsoft.napoleon.documents;

import java.util.Date;
import android.app.Activity;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	public static final int ID_DLV_MODE = 0;
	public static final int IDO_DLV_MODE = 1;
	
	private int orgmode = ID_DLV_MODE;
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);
		TextView tvInfo = (TextView) view.findViewById(R.id.tvInfo);
		
		if (doc instanceof DeliveryImpl){
			DeliveryEx dlv = (DeliveryEx)doc.getData();
			TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
			
			if (doc.getDate() == null || dlv.payDate == null)
				tvDate.setText(R.string.doc_error);
			else {
				String text = Util.simpleDateFormat.format(doc.getDate()) + "<br>" +
						Util.simpleDateFormat.format(dlv.payDate);
				tvDate.setText(Html.fromHtml(text));
			}
			
			TextView tv = (TextView)view.findViewById(R.id.tvOverPay);
			String text = "";
			Date checkDate = new Date();
			if( dlv.payDate.compareTo(checkDate)< 0) {
				long diff = checkDate.getTime() - dlv.payDate.getTime();
				diff /= (1000 * 86400);
				if( diff > 0 )
					text = Long.toString(diff);
			}
			tv.setText(text);
			tv.setVisibility(View.VISIBLE);
			
			TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
			tvSum.setText(Html.fromHtml(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false) + "<br>" +
					Util.IntToScaleWStr(dlv.sum(), Consts.SUM_SCALE, 2, false)));
			
			int cl = Util.GrServerColorToSystem(dlv.color);
			tvDate.setTextColor(cl);
			tvSum.setTextColor(cl);
			
			tv = (TextView)view.findViewById(R.id.tvOther);
			tv.setTextColor(cl);
			
			
			tvInfo.setText(dlv.info);
		}else
			tvInfo.setText("");
	}
	
//	
// Заккоментарил т.к не нашел где этот код используется 2014.10.11 kki
//	
//	public DocList dlvList(String orgId, String order, String where) {
//		String whereStr = (orgId == null) ? "" : "id='" + orgId + "'";
//		if( where != null && where.length() > 0 ) {
//			if( whereStr.length() > 0 )
//				whereStr += " AND ";
//			whereStr += where;
//		}
//		return new DebtDocList(whereStr, order, LoadDelivery){
//			@Override
//			protected void init(String where, String order, boolean loadDelivery) {
//				deliveries = (loadDelivery) ? new DocList(BalanceDelivery.class, where, order) : null;
//				payments = null;
//			}
//		};
//	}
	
	@Override
	protected String getOrgWhere(String orgId) {
		StringBuilder result = new StringBuilder();
		
		if(orgId != null){
			switch (orgmode) {
			case ID_DLV_MODE:
				result.append("id='").append(orgId).append("'");
				break;
			case IDO_DLV_MODE:
				OrgImpl org = new OrgImpl();
				org.read("id", orgId);
				result.append("ido='").append(((OrgEx)org.getData()).ido).append("'");
				break;
			default:
				break;
			}
		}
		
		return result.toString();
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		ImageView iv = (ImageView) documentsView.findViewById(R.id.ivFilter);
		
		if(iv != null)
			iv.setVisibility(View.VISIBLE);
		
		View v = documentsView.findViewById(R.id.OverPay);
		
		if( v != null )
			v.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		
		ImageView iv = (ImageView) documentsView.findViewById(R.id.ivFilter);
		
		if(iv != null)
			iv.setVisibility(View.GONE);
	
		View v = documentsView.findViewById(R.id.OverPay);
		
		if( v != null )
			v.setVisibility(View.GONE);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.shipped;
	}

	public void setDlvMode(int mode) { orgmode = mode; }
}
