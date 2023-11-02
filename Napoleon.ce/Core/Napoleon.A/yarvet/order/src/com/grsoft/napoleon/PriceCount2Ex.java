package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.Order2Ex;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImpl2Ex;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class PriceCount2Ex extends PriceCountEx {
	TextView tvRen;
	OrgImpl org = new OrgImpl();
	
	@Override
	protected int getContentViewId() { return R.layout.pricecount2ex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		tvRen = (TextView) findViewById(R.id.tvRen);
		
		org.read("id", document.getId());
		
		if (document == null)
			document = new OrderImpl2Ex();
		
		if(document instanceof OrderImpl) {
			String selected = "";
			OrderImpl orderImpl = (OrderImpl)document; 
			
			if (document.getRowid() != ExtrasConst.INVALID_ID )
				selected = ((Order2Ex)document.getData()).whCode;
			
			ConfigImpl config = new ConfigImpl();
			
			ArrayList<KeyValue> values = new ArrayList<KeyValue>();
			Config c = config.getData();
			c.key = "Склады";
			config.read();
			
			int sel = DialogHelper.makeListWithKey(c.value, values, selected);
			
			ArrayList<KeyValueIndex> indexs = new ArrayList<KeyValueIndex>();
			int index  = 0;
			for(KeyValue kv:values) {
				if (sel != index)
					indexs.add(new KeyValueIndex(kv, index));
				
				index++;
			}
			
			StringBuilder sb = new StringBuilder();
			String fmtstr = "%s&nbsp;&nbsp;%s";
			String whCode = ((Order2Ex)orderImpl.getData()).whCode;
			int whIndex = ((Order2Ex)orderImpl.getData()).whIndex;
			
			for(KeyValueIndex v : indexs) {
				((Order2Ex)orderImpl.getData()).whCode = v.key;
				((Order2Ex)orderImpl.getData()).whIndex = v.index;
				
				int qty = orderImpl.getItemValue(price.getData());
				
				sb.append(String.format(fmtstr, v.value, Util.IntToScaleStr(qty, Consts.QTY_SCALE)));
				sb.append("<br>");
			}
			
			((Order2Ex)orderImpl.getData()).whCode = whCode;
			((Order2Ex)orderImpl.getData()).whIndex = whIndex;
					
			TextView tv = (TextView) findViewById(R.id.tvSklads);
			tv.setText(Html.fromHtml(sb.toString()));
		}
	}
	
	@Override
	protected void updateSumTextView() {
		super.updateSumTextView();
		
		//Рентабельность
		long ren = 0;
		
		if (priceVal != 0) {
			ren = (long)(((double)(priceVal - minCost)) / priceVal * 100 * Consts.SUM_SCALE); 
		}
		
		if (tvRen == null)
			tvRen = (TextView) findViewById(R.id.tvRen);
		
		tvRen.setText(Util.IntToScaleStr(ren, Consts.SUM_SCALE));
	}
	
	@Override
	protected boolean checkCost() {
		int check = 0;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		cfg.getValue(sb, "РазрешитьПродаватьНижеМинимальнойЦены");
		
		try {
			check = Integer.parseInt(sb.toString());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return check == 0;
	}
}
