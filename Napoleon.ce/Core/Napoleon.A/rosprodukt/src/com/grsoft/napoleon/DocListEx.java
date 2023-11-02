package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.app.AlertDialog;
import android.view.View;
import android.widget.Spinner;

public class DocListEx extends DocList {
	private int supply = -1;
	private List<CharSequence> firms = new ArrayList<CharSequence>(); 
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if(DocType.getCurDoc() instanceof OrderDoc){
			
			int sum = 0;
			int weight = 0;

			for( int i=0; i<adapter.getCount(); i++ ) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += getDocSum(d);
				weight += ((OrderImplBase<?>)d).weight();
			}
			
			DocType.getCurDoc().updateTotalSum(this, sum, weight, 0, R.id.tvDocSum);
			
		}else 
			super.refreshTotalSum(useFilter);
			
	}
	
	protected void postUpdateFilterView(View view) {
		ConfigImpl config = new ConfigImpl();
		Spinner spFirma = (Spinner) view.findViewById(R.id.spFirma);
		DialogHelper.loadSpinnerFromConfig(config, "Организация", firms, spFirma, "");
		config.close();
	}
	
	protected int getFilterLayout() {
		return R.layout.date_selection_ex;
	}
	
	@Override
	protected void postFilterClick(AlertDialog view) {
		super.postFilterClick(view);
		
		Spinner spFirma = (Spinner) view.findViewById(R.id.spFirma);
		supply  = spFirma.getSelectedItemPosition();
	}
	
	@Override
	protected void llFilterPanelClick() {
		supply = -1;
		super.llFilterPanelClick();
	}
	
	@Override
	protected DocListAdapter createListAdapter(final DocType docType) {
		return new DocListAdapter(this, docType, saveDatePeriod) {
			@Override
			protected void postFetchByPeriod() {
				super.postFetchByPeriod();
				
				if (curDocType == OrderDoc.instance() && supply != -1) {
					List<Long> ids = new ArrayList<Long>();
					
					for(Document<?> d : documents) {
						OrderImpl impl = (OrderImpl)d;
						
						if (impl.getData().supplyer != supply) {
							ids.add(d.getRowid());
						}
						
					}
					
					documents.removeDocuments(ids);
					
				}else if (curDocType == IncassDoc.instance() && supply != -1) {
					List<Long> ids = new ArrayList<Long>();
					String fid = firms.get(supply).toString();
					
					for(Document<?> d : documents) {
						IncassImpl impl = (IncassImpl)d;
						IncassEx ie = (IncassEx) impl.getData();
						
						if (!ie.firm.equals(fid)) {
							ids.add(d.getRowid());
						}
						
					}
					
					documents.removeDocuments(ids);
				}
				
				
			}
		};
	}
}
