package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.Sales;
import com.grsoft.napoleon.documents.Document;

public class DebetList extends HashMap<Integer, DocDebtData> {
	
	private static final long serialVersionUID = 1L;
	
	DocDebtData firstUnpayed;
	
	/**
	 * распределяет оплаты по накладным
	 * @param debtDocs - список сортирован по датам по возрастаниюs
	 */
	public void load(com.grsoft.napoleon.documents.DocList debtDocs) {
		ArrayList<DocDebtData> list = new ArrayList<DocDebtData>();
		int sum = loadDeliveries(list, debtDocs);
		
		firstUnpayed = distibuteSumToDocs(list, sum);
	}
	
	public DocDebtData getFirstUnpayed() { return firstUnpayed; }
	
	private DocDebtData distibuteSumToDocs(ArrayList<DocDebtData> list, int sum) {
		DocDebtData unpayed = null;
		
		Collections.sort(list);
		for( int i = list.size() - 1; i>= 0; i-- ) {
			DocDebtData dd = list.get(i);
			if( dd.isDelivery || dd.sum > 0 ) {
				long sumd = (sum > dd.sum) ? dd.sum : sum;
				dd.sumD = sumd;
				sum -= sumd;
				if( sumd > 0 )
					unpayed = dd;
				
				this.put(dd.index, dd);
			} 
//			else if( dd.sum < 0 ) 
//				sum += dd.sum;
		}
		return unpayed;
	}

	int loadDeliveries(List<DocDebtData> list, com.grsoft.napoleon.documents.DocList debtDocs) {
		int sum = 0;
		int index = 0;
		for(Document<?> doc : debtDocs ) {
			sum += doc.sum();
			DataObject data = doc.getData();
			if( data instanceof DeliveryEx ) {
				DocDebtData dd = new DocDebtData((DeliveryEx)data, index);
				list.add(dd);
			} else if( data instanceof Sales ) {
				DocDebtData dd = new DocDebtData((Sales)data, index);
				list.add(dd); 
			} else if( data instanceof IncassEx ) {
				DocDebtData dd = new DocDebtData((IncassEx)data, index);
				list.add(dd); 
			} else if( data instanceof Payment && sum > 0 ) {
				DocDebtData dd = new DocDebtData((Payment)data, index);
				list.add(dd);
			}
			index++;
		}
		
		return sum;
	}

}
