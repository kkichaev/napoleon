package com.keeper.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.keeper.db.data.DataObject;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class DataAdapter<T extends DataObject> extends BaseAdapter{
	abstract View newView(Context context, DataObject item, ViewGroup parent);
	abstract void bindView(View view, Context context, DataObject item);
	abstract T createItem(Cursor c);
	abstract Comparator<? super T> getCmp();
	
	protected List<T> data = new ArrayList<T>();
	private Context context;
	private Cursor cursor;
	
	public DataAdapter(Context context, Cursor c) {
		this.context = context;
		this.cursor = c;
		
		load(c);
	}
	
	@Override
	public int getCount() { 
		return data.size();	
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return ((DataObject)getItem(position))._id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DataObject item = (DataObject) getItem(position);
		
		if (convertView == null)
			convertView = newView(context, item, parent);
		
		bindView(convertView, context, item);
		
		return convertView;
	}
	
	public void reload() {
		cursor.requery();
		data.clear();
		load(cursor);
	}
	
	void load(Cursor c) {
		while(c.moveToNext()) {
			data.add(createItem(c));
		}
		
		Collections.sort(data, getCmp());
	}
	
}
