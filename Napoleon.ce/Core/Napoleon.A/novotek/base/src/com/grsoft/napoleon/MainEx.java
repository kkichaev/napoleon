package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MainEx extends Main {
	boolean isFiltred = false;
	Adapter adapter;
	
	@Override protected int getResourceID() { return R.layout.mainex; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnFilter).setOnClickListener(new View.OnClickListener() {			
			@Override public void onClick(View v) { changeFilter();}
		});
	}

	protected void changeFilter() {
		if(DocType.getCurDoc() == DebtDoc.instance() && adapter != null) {
			isFiltred = adapter.changeFilter();
		}
	}
		
	@Override
	protected BaseAdapter createSolidMainAdapter() {
		adapter = new Adapter(this, isFiltred); 
		return adapter;
	}
	
	class Adapter extends SolidMainAdapter {
				
		boolean isFiltred;
		List<Org> filtred = new ArrayList<Org>();
		HashSet<String> nonZero;

		public Adapter(Main context, boolean isFiltred) {
			super(context);
			this.isFiltred = isFiltred;
			
			updateNonZero();
		}
		
		boolean isFiltred() {
			return (isFiltred && DocType.getCurDoc() == DebtDoc.instance());
		}
		
		@Override
		public Object getItem(int pos) {
			if(isFiltred())
				return filtred.get(pos);
			return super.getItem(pos);
		}
		

		private void updateNonZero() {
			if(nonZero == null)
				nonZero = new HashSet<String>();
			else
				nonZero.clear();
			DataTraveler.travel(OrgSum.class, new DataTraveler.Travel<OrgSum>() {

				@Override
				public boolean travel(DataTraveler<OrgSum> item) {
					if(item.data.sum > 0)
						nonZero.add(item.data.id);
					return true;
				}
			}, "type='" + DebtDoc.instance().getName() + "'");
		}

		public boolean changeFilter() {
			isFiltred = !isFiltred;
			updateItems();
			notifyDataSetChanged();
			return isFiltred;
		}

		@Override
		public int getCount() {
			return (!isFiltred() ) ? super.getCount() : filtred.size();
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if( !isFiltred() )
				return super.getView(arg0, arg1, arg2);
			
			Org o = filtred.get(arg0);
			return main.getSolidMainView(o, arg0, arg1);
		}
		
		@Override
		public void applyFilter(String value) {
			super.applyFilter(value);
			if( isFiltred() )
				updateItems();
		}
		
		@Override
		public void resetFilter() {
			super.resetFilter();
			if( isFiltred() )
				updateItems();
		}
		
		@Override
		protected void load(String filter) {
			super.load(filter);
			updateNonZero();
			if( isFiltred() )
				updateItems();
		}
		
		private void updateItems() {
			filtred.clear();
			
			for(Org o : data) {
				if( nonZero.contains(o.id))
					filtred.add(o);
			}
		}
		
	}}
