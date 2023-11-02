package com.grsoft.napoleon;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSAddOrderDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
		if(DocType.getCurDoc() == SalesDoc.instance())
			ret.putFilter(new FakeFilter());
		return ret;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		if(DocType.getCurDoc() == WSAddOrderDoc.instance()){
			if(textView.getId() == R.id.tvClmn1)
				super.setTextColumnValue(textView, COLUMN_QTY_WH, price);
			else if (textView.getId() == R.id.tvClmn2)
				textView.setVisibility(View.GONE);
		}else if(DocType.getCurDoc() == WSOrderDoc.instance()){
			if(textView.getId() == R.id.tvClmn1)
				super.setTextColumnValue(textView, COLUMN_QTY_WH, price);
			else if (textView.getId() == R.id.tvClmn2)
				textView.setText(Util.IntToScaleStr(((PricePrint)price).vanQty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
		}else
			super.setTextColumnValue(textView, type, price);
	}
}

class FakeFilter extends Filter {
	public FakeFilter() {
		super("fake");
	}
}