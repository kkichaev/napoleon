package com.grsoft.ads.documents;

import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.grsoft.ads.OrderSummary;
import com.grsoft.ads.R;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.napoleon.util.LinesCountController;

public class AdapterListDocType extends DocType {
	protected DataBaseAdapter<? extends DataObject> adapter;
	
	protected AdapterListDocType(String name,
			Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}
	
	public void setListControls(Context context, ListView listView, 
			LinesCountController linesCount){
		try{
			if (adapter != null)
				adapter.close();
			
			adapter = createAdapter(context, linesCount);
			listView.setOnItemClickListener(getListener());
			listView.setAdapter(adapter);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public DataBaseAdapter<? extends DataObject> createAdapter(Context context, 
			LinesCountController linesCount){
		return adapter;
	}
	
	public OnItemClickListener getListener(){
		return null;
	}
	
	public void close(){
		if (adapter != null)
			adapter.close();
	}
	
	public void deleteItem(int position){}
	
	public void refreshAdapter(){
		adapter.notifyDataSetChanged();
	}
	
	public Object getSelectedItem(int position){
		return adapter.getItem(position);
	}
	
	public Class<?> getSummary(){
		return OrderSummary.class;
	}
	
	public String getTitle(){
		return "Работа с заявкой";
	}
	
	public int getSummaryIndicator(){
		return R.drawable.order;
	}
	
	public String getSummaryTitle(){
		return "Заявка";
	}
	
	public boolean hasAddress(){
		return false;
	}
}
