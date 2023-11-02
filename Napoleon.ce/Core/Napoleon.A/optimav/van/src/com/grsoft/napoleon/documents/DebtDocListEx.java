package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DeliveryPrintEx;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.IncassImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.dataobjects.impl.SalesImpl;

public class DebtDocListEx extends DebtDocList {
	ArrayList<Document<?>> docs;
	
	public DebtDocListEx(String where, String order, boolean loadDelivery) {
		super(where, order, loadDelivery);
	}
	
	@Override
	protected void init(String where, String order, boolean loadDelivery) {
		docs = new ArrayList<Document<?>>();
		deliveries = (loadDelivery) ? new DocList(DeliveryImpl.class, where, order) : null;
		payments = new DocList(PaymentImpl.class, where, order);
		DocList orders = new DocList(OrderImpl.class, where, order);
		DocList sales = new DocList(SalesImpl.class, where, order);
		DocList incass = new DocList(IncassImpl.class, where, order);
		
		for (int i = 0; i < deliveries.getCount(); i++) {
			DeliveryImpl d = new DeliveryImpl();
			
			if(d.read(deliveries.get(i).getRowid())){
				DeliveryPrintEx dlv = (DeliveryPrintEx) d.getData();
				dlv.sumDD = (int)dlv.sumD;
				docs.add(d);
				d.write();
			}
						
			d.close();
		}
		
		for (int i = 0; i < payments.getCount(); i++) {
			PaymentImpl p = new PaymentImpl();
			
			if(p.read(payments.get(i).getRowid()))
				docs.add(p);
			
			p.close();
		}
		
		for (int i = 0; i < orders.getCount(); i++) {
			OrderImpl o = new OrderImpl();
			
			if(o.read(orders.get(i).getRowid())){
				OrderEx ord = ((OrderEx)o.getData());
				ord.sumD = (int)o.sum();
				o.write();
				o.close();
				docs.add(o);
			}
			
			o.close();
		}
		
		for (int i = 0; i < sales.getCount(); i++) {
			SalesImpl s = new SalesImpl();
			
			if(s.read(sales.get(i).getRowid())){
				SalesEx sls = ((SalesEx)s.getData());
				sls.sumD = (int)s.sum();
				s.write();
				s.close();
				docs.add(s);
			}
			
			s.close();
		}
		
		for (int i = 0; i < incass.getCount(); i++) {
			IncassImpl inc = new IncassImpl();
			
			if(inc.read(incass.get(i).getRowid()))
				docs.add(inc);
			
			inc.close();
		}
		
		Collections.sort(docs, new Comparator<Document<?>>() {

			@Override
			public int compare(Document<?> lhs,
					Document<?> rhs) {
				DocDataObject leftDoc = lhs.getData();
				DocDataObject rightDoc = rhs.getData();
				
				Date leftData = leftDoc instanceof CreateDocDataObject ?
						((CreateDocDataObject)leftDoc).created : leftDoc.date;
						
				Date rightData = rightDoc instanceof CreateDocDataObject ?
						((CreateDocDataObject)rightDoc).created : rightDoc.date;
						
				return leftData.compareTo(rightData);
			}
		});
	}
	
	@Override
	public Document<?> get(int index) {
		return docs.get(index);
	}
	
	@Override
	protected void orderDocuments() {}
	
	@Override
	public int getCount() { return docs.size(); }
}
