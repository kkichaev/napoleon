package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.AssortmentMatrixAdapter;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class AssortmentMatrixAdapterEx extends AssortmentMatrixAdapter {
	public static final int PERIOD = 30;
	
	
	public AssortmentMatrixAdapterEx(WarehouseNewW warehouse, String id) {
		super(warehouse, id);
	}
	
	public static List<MatrixItem> collect(String id){
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		
		Set<String> ids = new HashSet<String>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(Util.getDate());
		c.add(Calendar.DATE, -PERIOD);
		
		DatePeriod dp = new DatePeriod(c.getTime(), new Date());
		com.grsoft.napoleon.documents.DocList list = DebtDoc.instance().docList(id, null, dp);
		
		for(Document<?> d : list) {
			if (d instanceof DeliveryImpl) {
				DeliveryImpl di = (DeliveryImpl)d;
				
				for(DeliveryItem i : di.getData().items) {
					if (!ids.contains(i.id)) {
						MatrixItem mi = new MatrixItem();
						mi.id = i.id;
						
						result.add(mi);
						ids.add(i.id);
					}
				}
			}
		}
		
		list = RemnantsDoc.instance().docList(id, null, dp);
		
		for(Document<?> d : list) {
			if (d instanceof RemnantsImpl) {
				RemnantsImpl ri = (RemnantsImpl)d;
				
				for(RemnantItem i : ri.getData().items) {
					if (!ids.contains(i.id) && i.qty > 0) {
						MatrixItem mi = new MatrixItem();
						mi.id = i.id;
						
						result.add(mi);
						ids.add(i.id);
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	protected List<MatrixItem> getMatrixItems() {
		return collect(id);
	}
}
