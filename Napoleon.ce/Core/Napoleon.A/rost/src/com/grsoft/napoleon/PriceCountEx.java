package com.grsoft.napoleon;

import java.util.Date;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPriceItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount{
	private int minQty = 0;

	@Override
	protected void postOnCreate() {
		if(document != null && DocType.getCurDoc() == OrderDoc.instance()){
			OrgImpl orgImpl = new OrgImpl();
			orgImpl.read("id", document.getId());
			
			for(OrgPriceItem i : ((OrgEx)orgImpl.getData()).price)
				if(i.id.equals(price.getData().id))
					minQty = i.minQty;
		}
		
		TextView tv = (TextView) findViewById(R.id.tvSelOut);
		tv.setText(fmtDateText(((PriceEx)price.getData()).selout));
	}
	
	private CharSequence fmtDateText(Date dt) {
		String result = "";
		
		if(dt != null && dt.getYear() > 114)
			result = Util.simpleDateFormat.format(dt);
		
		return result;
	}

	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		
		if(DocType.getCurDoc() == OrderDoc.instance() && minQty > 0)
			result = minQty <= qtyItems;
			
		return result;
	}
	
	@Override
	protected void invalidInputValueHandler() {
		Toast.makeText(this, getString(R.string.order_min_qty, Util.IntToScaleStr(minQty, Consts.QTY_SCALE)), Toast.LENGTH_SHORT).show();
		edCount.setText(Util.IntToScaleStr((int) minQty, Consts.QTY_SCALE));
		edCount.selectAll();
	}
	
	@Override protected int getContentViewId() { return R.layout.pricecountex; }
}
