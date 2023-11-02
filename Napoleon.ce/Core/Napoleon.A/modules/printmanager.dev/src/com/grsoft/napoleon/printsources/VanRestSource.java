package com.grsoft.napoleon.printsources;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.util.VanRestData;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.content.Context;

public class VanRestSource extends DataSourceAdapter {
	VanItems items;
	
	@Scale(value=Consts.WEIGHT_SCALE)
	public long weight = 0;

	@Scale(value=Consts.QTY_SCALE)
	public long qty = 0;
	
	public VanRestSource(List<VanRestData> data) {
		items = new VanItems(data);
		for(VanRestData i : data) {
			weight += i.weight;
			qty += i.qty;
		}
	}
	
	@Override
	public DataSource getObject(String name) {
		if( name.equals("items"))
			return items;
		return null;
	}
	
	@Override
	public void init(Context context, int res) {
		items.init(context, res);
	}
}

class VanItems extends DataSource {

	List<VanRestData> items;
	
	int index = 0;
	
	public VanItems(List<VanRestData> src) {
		items = new ArrayList<VanRestData>();
		items.addAll(src);
	}

	@Override public void init(Context context, int res) { index = 0; }

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, items.get(index), format);
	}

	@Override public DataSource getObject(String name) { return this; }

	@Override public boolean haveMoreData() { return (index + 1 < items.size()); }

	@Override
	public boolean moveNext() {
		index++;		
		return (index >= items.size()) ? false : true;
	}
	
}
