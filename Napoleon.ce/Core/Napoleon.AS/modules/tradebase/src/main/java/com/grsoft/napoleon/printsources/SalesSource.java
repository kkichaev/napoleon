package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.modules.print.DataSource;

import android.content.Context;

public class SalesSource extends DataSource {
	protected SalesPrint data;
	
	public SalesSource(Sales sales){
		this(new SalesPrint(sales));
	}
	
	public SalesSource(SalesPrint data){
		this.data = data;
	}
	
	public SalesPrint getData() { return data; }
	
	@Override
	public void startPage() { data.init(); }
	
	@Override
	public void init(Context context, int res) {
		data.initSource(context, res);
	}
	
	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		if (value != null && name != null){
			value.setLength(0);
			ConfigImpl config = new ConfigImpl();
			
			return config.getValue(value, name) || 
				data.getSupplSorce().getValue(value, name, format) ||
				data.getValue(value, name, format);
		}else 
			return false;
	}

	@Override
	public DataSource getObject(String name) {
		return data.getItems();
	}

}
