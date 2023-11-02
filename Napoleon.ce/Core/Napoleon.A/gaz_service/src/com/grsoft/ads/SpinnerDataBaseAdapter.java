package com.grsoft.ads;

import java.lang.reflect.Field;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.DataBaseAdapter;

public class SpinnerDataBaseAdapter<T extends DataObject> extends DataBaseAdapter<T>{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SpinnerDataBaseAdapter<? extends DataObject> create(Context context,
			DbObject<? extends DataObject> implClass, 
			String dataFieldName, String keyFieldName, String cond){
		SpinnerDataBaseAdapter<? extends DataObject> result = null;
		
		try{
			result = new SpinnerDataBaseAdapter(context, 
					implClass, dataFieldName, keyFieldName, cond);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public String getData(int position) {
		String result = "";
		
		DbObject<T> dbObject = (DbObject<T>) getItem(position);
		
		if (dbObject != null && keyField != null)
			try {
				result = keyField.get(dbObject.getData()).toString();
			} catch (Exception e) {
				e.printStackTrace();
			} 
		
		return result;
	}
	
	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return super.getItemId(pos);
	}

	@SuppressWarnings("unchecked")
	public int getItemPosition(String val) {
		if (val.length() > 0)
			for(int i = 0; i < getCount(); i++){
				DbObject<T> item = (DbObject<T>) getItem(i);
				
				try{ 
					if (keyField != null &&
							item != null &&
							keyField.get(item.getData()).toString().equals(val))
						return i;
				}catch (Exception e){ 
					e.printStackTrace();
				}
					
			}
		
		return 0;
	}

	private Field field;
	private Field keyField;
	
	public SpinnerDataBaseAdapter(Context context, DbObject<T> implClass, String fName,
			String fkName, String cond)
			throws IllegalAccessException, InstantiationException {
		super(context, implClass, cond);
		
		Field[] fields = DataObjectInfo.getInstance().getFields(implClass.getData().getClass());
		for(Field f : fields){
			String name= f.getName();
			if (field == null && name.equals(fName))
				field = f;
			
			if (keyField == null && name.equals(fkName))
				keyField = f;
			
			if (field != null && keyField != null)
				break;
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return updateView(android.R.layout.simple_spinner_item, 
				position, convertView);
	}

	@SuppressWarnings("unchecked")
	public View updateView(int layoutid, int position, View convertView) {
		if (convertView == null)
			convertView = View.inflate(context, layoutid, null);
		
		DbObject<T> dbObject = (DbObject<T>) getItem(position);
		
		try{
			String caption = "не выбран";
			
			if (dbObject != null && field != null)
				caption = field.get(dbObject.getData()).toString();
			
			((TextView)convertView.findViewById(android.R.id.text1)).setText(caption);
			
				
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	@Override
	public View getDropDownView(int position, View convertView,
			ViewGroup parent) {
		return updateView(android.R.layout.simple_spinner_dropdown_item, 
				position, convertView);
	}
	
	@Override
	public int getCount() {
		return super.getCount() + 1;
	}
	
	@Override
	public Object getItem(int pos) {
		if (pos == 0)
			 return null;
		else
			return super.getItem(pos - 1);
	}
	
}