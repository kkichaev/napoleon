package com.grsoft.napoleon;

import android.widget.TextView;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price){
		if(type == COLUMN_QTY_WH && (price instanceof PricePrint) && DocType.getCurDoc() == MovementDoc.instance()){
			StringBuilder sb = new StringBuilder();
			sb.append(Util.IntToScaleStr(((PricePrint)price).vanQty, Consts.QTY_SCALE, 
					Util.DEC_DELIM, true));
			sb.append(" / ");
			sb.append(Util.IntToScaleStr(price.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
			textView.setText(sb.toString());
		}else
			super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	protected Filter createZeroPositionFilter() {
		if(DocType.getCurDoc() == MovementDoc.instance())
			return new ZeroPositionFilter(){
				String where = "vanQty>0";
				@Override
				public String getWhereStr() {
					return where;
				}
			};
		else
			return super.createZeroPositionFilter();
	}
}
