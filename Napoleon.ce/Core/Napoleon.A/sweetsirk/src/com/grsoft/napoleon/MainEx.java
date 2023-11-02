package com.grsoft.napoleon;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.napoleon.documents.Debt;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MainEx extends Main {
	public Set<String> debetOrgs = new HashSet<String>();
	
	@Override
	protected BaseAdapter createSolidMainAdapter() {
		return new SolidMainAdapter(this){
			@Override
			protected void load(String filter) {
				super.load(filter);
				
				if( DocType.getCurDoc() == DebtDoc.instance()) {
					filterOrgDebet();
				}
			}

			protected void filterOrgDebet() {
				debetOrgs.clear();
				Date now = new Date();
				
				Map<String, Org> map = new HashMap<String, Org>();
				
				
				for (Org o : data) {
					com.grsoft.napoleon.documents.DocList list = DebtDoc.instance().docList(o.id);
					
					for (Document<?> d : list) {
						if (d instanceof DeliveryImpl) {
							DeliveryImpl dlv = (DeliveryImpl)d;
							
							if (dlv.getData().sumD > 0) {
								map.put(o.id,o);
								
								if (dlv.getData().payDate.compareTo(now) < 0) {
									debetOrgs.add(o.id);
								}
							}
						}
					}
				}
				
				data.clear();
				data.addAll(map.values());
				
				Collections.sort(data, new Comparator<Org>() {
					@Override public int compare(Org lhs, Org rhs) { return lhs.name.compareTo(rhs.name); }});
			}
		};
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if (docType == DebtDoc.instance() || DocType.getCurDoc() == DebtDoc.instance()) {
			DocType.setCurDoc(docType, true);
			((SolidMainAdapter)solidMainAdapter).applyFilter(null); 
		}
		
		super.adjustViewForDocType(docType);
	}
	
	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);
		
		if (DocType.getCurDoc() == DebtDoc.instance() && debetOrgs.contains(org.id))
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(getResources().getColor(R.color.red));
	}
}
