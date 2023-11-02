package com.grsoft.napoleon.modules.print;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.printsources.SilentReflector;

import android.content.Context;

public class BaseDataSource extends DataSource {
	protected Object object;
	
	List<DataSource> childs = new ArrayList<DataSource>();
	
	public BaseDataSource(Object object) {
		this.object = object;
	}
	
	@Override
	public void init(Context context, int res) {
		for(DataSource ds : childs) {
			try {
				ds.init(context, res);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
		@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		return SilentReflector.getFieldValue(value, name, object, format);
	}

	@Override
	public DataSource getObject(String name) {
		DataSource ds = null;
		try {
			Field f = object.getClass().getField(name);
			if( f != null && List.class.isAssignableFrom(f.getType()) ) {
				Object fv = f.get(object);
				if( fv instanceof List<?>)
					ds = createListSource(name, (List<?>)fv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		childs.add(ds);
		return ds;
	}
	
	protected DataSource createListSource(String fieldName, List<?> list) {
		return new ListDataSource(list);
	}
}
