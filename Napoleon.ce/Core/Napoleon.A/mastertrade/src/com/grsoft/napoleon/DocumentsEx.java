package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DocumentsEx extends Documents {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		OrgEx orgEx = (OrgEx) org.getData();
		TextView tvLicense = (TextView) findViewById(R.id.tvLicense);
		
		tvLicense.setText(getString(R.string.license_date, 
				orgEx.license.getTime() == -14400000 ? "..." : 
				Util.simpleDateFormat.format(orgEx.license)));
		
		com.grsoft.napoleon.documents.DocList list = DebtDoc.instance().docList(orgEx.id, "date", (DatePeriod)null);
		TextView tvDlvinfo = (TextView)findViewById(R.id.tvDlvinfo);
		tvDlvinfo.setVisibility(View.GONE);
		
	    for(Document<?> dlv : list){
			if(dlv instanceof DeliveryImpl){
				Delivery d = ((DeliveryImpl)dlv).getData();
				Date curDate = new Date();
				if( (d.sumD > 0 && d.payDate.compareTo(curDate) < 0)){
					long days = DatePeriod.daysDiff(d.payDate,curDate); 
					tvDlvinfo.setVisibility(View.VISIBLE);
					tvDlvinfo.setText(getString(R.string.dlvinfo, Util.IntToScaleStr(d.sumD, Consts.SUM_SCALE), days));
				}
				
				break;
			}
		}
	    
	    long diff = DatePeriod.daysDiff(Calendar.getInstance().getTime(), orgEx.license); 
	    
	    if(diff <= 7)
	    	tvLicense.setTextColor(getResources().getColor(R.color.red));
	}
	
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
}
