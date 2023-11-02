package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;

public class NapoleonEx extends Napoleon {
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
	protected MainOrgsAdapter getMainOrgAdapter() throws IllegalAccessException, InstantiationException {
		if( adapter == null )
			adapter = new Adapter(this, isFiltred);
		return adapter;
	}
	
	class Adapter extends MainOrgsAdapter {
		
		OrgImpl org = new OrgImpl();
		
		boolean isFiltred;
		ArrayList<Long> filtred = new ArrayList<Long>();
		HashSet<String> nonZero = new HashSet<String>();

		public Adapter(Context context, boolean isFiltred) throws IllegalAccessException, InstantiationException {
			super(context);
			this.isFiltred = isFiltred;
			
			updateNonZero();
		}

		private void updateNonZero() {
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
		public void close() {
			org.close();
			super.close();
		}
		
		@Override
		public int getCount() {
			return (!isFiltred ) ? super.getCount() : filtred.size();
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if( !isFiltred )
				return super.getView(arg0, arg1, arg2);
			
			long pos = filtred.get(arg0);
			org.read(pos);
			return getMainView(org, arg0, arg1, arg2);
		}
		
		@Override
		public void applyFilter(String value) {
			super.applyFilter(value);
			if( isFiltred )
				updateItems();
		}
		
		@Override
		public void resetFilter() {
			super.resetFilter();
			if( isFiltred )
				updateItems();
		}
		
		@Override
		public void refresh() {
			super.refresh();
			updateNonZero();
			if( isFiltred )
				updateItems();
		}

		private void updateItems() {
			filtred.clear();
			
			for( int i=0; i<cursor.getCount(); i++ ) {
				OrgImpl oi = (OrgImpl)cursor.get(i);
				if( nonZero.contains(oi.getData().id))
					filtred.add(oi.getRowid());
			}
		}
		
	}
}

