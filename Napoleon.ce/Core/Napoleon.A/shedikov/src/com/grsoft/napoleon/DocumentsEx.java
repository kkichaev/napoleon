package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.Skladable;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class DocumentsEx extends Documents implements OnItemSelectedListener {
	Spinner spSkald;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		spSkald = (Spinner) findViewById(R.id.spSklad);
		
		final List<Sklad> arr = new ArrayList<Sklad>();
		Sklad s = new Sklad();
		s.name = "<Все>";
		arr.add(s);
		
		DataTraveler.travel(Sklad.class, new DataTraveler.Travel<Sklad>(true){
			@Override
			public boolean travel(DataTraveler<Sklad> item) {
				arr.add(item.data);
				return true;
			}}, null);
		
		spSkald.setAdapter(new SpinnerAdapter() {
			@Override public void unregisterDataSetObserver(DataSetObserver observer) {}
			@Override public void registerDataSetObserver(DataSetObserver observer) {}
			@Override public boolean isEmpty() { return getCount() == 0; }
			@Override public boolean hasStableIds() { return false; 	}
			@Override public int getViewTypeCount() { return 0; }
			@Override public View getView(int position, View convertView, ViewGroup parent) { return getDropDownView(position, convertView, parent); }
			@Override public int getItemViewType(int position) { return 0;}
			@Override public long getItemId(int position) {	return 0; }
			@Override public Object getItem(int position) { return arr.get(position); }
			@Override public int getCount() { return arr.size(); }
			@Override
			public View getDropDownView(int position, View view, ViewGroup parent) {
				if (view == null)
					view = View.inflate(DocumentsEx.this, android.R.layout.simple_spinner_item, null);
				
				Sklad s = (Sklad) getItem(position);
				((TextView) view).setText(s.name);
				
				return view;
			}
		});
		
		spSkald.setOnItemSelectedListener(this);
	}
	
	@Override protected int getContentViewID() { return R.layout.documentsex; };

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Adapter a = (Adapter) lvDocs.getAdapter();
		
		if(a != null){
			Sklad s = (Sklad) arg0.getItemAtPosition(arg2);
			
			if( a != null)
				a.filter(s.id);
		}
	}

	@Override public void onNothingSelected(AdapterView<?> arg0) {}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		
		if (docType == DebtDoc.instance())
			spSkald.setVisibility(View.VISIBLE);
		else
			spSkald.setVisibility(View.GONE);
	}

	@Override
	protected DocumentsAdapter createAdapter(DocType docType, String id) {
		String order = getOrder(docType); 
		return new Adapter(DocumentsEx.this, docType, id, order);
	}
}

class Adapter extends DocumentsAdapter{
	List<Integer> data = new ArrayList<Integer>();
	
	public Adapter(Context context, DocType docType, String orgId, String order){
		super(context, docType, orgId, order);
		
		for(int i = 0 ; i < documents.getCount(); i++)
			data.add(i);
	}

	public void filter(String id) {
		data.clear();
		
		for(int i = 0 ; i < documents.getCount(); i++){
			Document<?> d = documents.get(i);
			String sid = "";
			
			if(d.getData() instanceof Skladable)
				sid = ((Skladable)d.getData()).getSkladId();
			
			if(id.trim().length() == 0 || sid.equals(id))
				data.add(i);
		}
		
		notifyDataSetChanged();
	}
	
	@Override public int getCount() { return data.size(); }
	@Override public Object getItem(int position) { return documents.get(data.get(position)); }
}

