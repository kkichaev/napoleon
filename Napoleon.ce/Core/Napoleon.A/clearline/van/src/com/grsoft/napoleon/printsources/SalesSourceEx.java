package com.grsoft.napoleon.printsources;

import java.lang.reflect.Field;
import java.util.List;

import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.ListDataSource;

import android.content.Context;

public class SalesSourceEx extends SalesSource {

	public SalesSourceEx(SalesPrint data) {
		super(data);
	}
	
	@Override
	public void init(Context context, int res) {
		super.init(context, res);
		((SalesPrintEx)data).initForm(res);
	}

	@Override
	public DataSource getObject(String name) {
		if(name.equals("items"))
			return super.getObject(name);
		
		DataSource ds = null;
		try {
			Field f = data.getClass().getField(name);
			if( f != null && List.class.isAssignableFrom(f.getType()) ) {
				Object fv = f.get(data);
				if( fv instanceof List<?>)
					ds = new ListDataSource((List<?>)fv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ds;
	}
}
