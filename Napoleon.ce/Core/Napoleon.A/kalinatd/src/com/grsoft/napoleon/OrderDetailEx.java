package com.grsoft.napoleon;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.DialogFragment;
import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailEx extends OrderDetail {
	
	@Override
	protected boolean keyBackPressed() {
		if(!doc.isEmpty() && doc.isEditable() && !ScriptImpl.containsDocument(docType.getObjectName(), doc.getData().created, doc.getId())){
			DialogFragment dlg = new AskToSendDlg();
			dlg.show(getFragmentManager(), dlg.getClass().getCanonicalName());
			return false;
		}
		
		return super.keyBackPressed();
	}
	
	@Override
	public void send() {
		if(doc.isExported())
			Toast.makeText(this, R.string.order_sended, Toast.LENGTH_SHORT).show();
		else
			super.send();
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetail_ex);
	}
	
	@Override
	protected void updateTotalSum() {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		
		if(cfg.ordSumEx) {
			long s1 = 0;
			long s2 = 0;
			
			PriceImpl p = new PriceImpl();
			
			for(OrderItem i : (((Order)doc.getData()).items) ) {
				p.getData().id = i.id;
				
				long sum = doc.getItemSum(p.getData());
				
				if(p.read()) {
					if (doc.getItemValue(p.getData()) > 0)
						s1 += sum;
					else
						s2 += sum;
				}
			}
			
			String s = DocType.getCurDoc().getTotalSumStr(this, doc.sum(), doc.weight(), 
					((CfgNplW)ConfigManager.getConfig()).isPackView ? doc.countPack() : doc.count());
			
			s += "<b>";
			s += "<br>" + Util.IntToScaleStr(s1, Consts.SUM_SCALE);
			s += "<br>" + Util.IntToScaleStr(s2, Consts.SUM_SCALE);
			s += "</b>";
			
			TextView tv = (TextView) findViewById(R.id.tvTotalSum);
			tv.setText(Html.fromHtml(s));
		}else
			super.updateTotalSum();
	}
}
