package com.grsoft.napoleon.modules.print;
import com.grsoft.aceteam.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.grsoft.napoleon.printsources.SilentReflector;

import android.content.Context;

public class ListDataSource extends DataSource {
	List<?> items;
	int index = 0;
	
	List<DataSource> childs = new ArrayList<DataSource>();

	public ListDataSource(List<?> items) {
		this.items = items;
	}
	
	@Override public boolean haveMoreData() { return (index + 1 < items.size()); }
	@Override 
	public void init(Context context, int res) {
		index = 0; 
		for(DataSource ds : childs) {
			try {
				ds.init(context, res);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public byte[] getImage(String name) {
		if( index < items.size() ) {
			Object val = items.get(index);
			if(val instanceof DataSource)
				return ((DataSource) val).getImage(name);
		}
		
		return super.getImage(name);
	}
	
	@Override
	public int getImageHeight(String name) {
		if( index < items.size() ) {
			Object val = items.get(index);
			if(val instanceof DataSource)
				return ((DataSource) val).getImageHeight(name);
		}
		return super.getImageHeight(name);
	}

	@Override
	public boolean getValue(StringBuilder value, String name, String format) {
		if( index >= items.size() )
			return false;
		
		Object val = items.get(index);
		if(val instanceof DataSource)
			return ((DataSource) val).getValue(value, name, format);
		
		return SilentReflector.getFieldValue(value, name, val, format);
	}

	@Override
	public DataSource getObject(String name) {
		if( index >= items.size() )			
			return null;
		DataSource ds = null;
		Object val = items.get(index);
		try {
			Field f = val.getClass().getField(name);
			if( f != null && List.class.isAssignableFrom(f.getType()) ) {
				Object fv = f.get(val); 
				ds = new ListDataSource((List<?>)fv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		childs.add(ds);
		return ds;
	}

	@Override public void calculate() { }

	@Override
	public boolean moveNext() {
		index++;
		return (index >= items.size()) ? false : true;
	}
}
