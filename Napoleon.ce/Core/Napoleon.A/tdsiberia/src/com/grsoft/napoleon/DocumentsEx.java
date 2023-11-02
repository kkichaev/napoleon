package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class DocumentsEx extends Documents {
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);

		DocType ct = DocType.getCurDoc(); 
		if( adapter != null ) {
			if( ct instanceof OrderDoc || ct instanceof DebtDocEx) {
				int sum = 0;
				int sumDlv = 0;
				int weight = 0;
	
				for( int i=0; i<adapter.getCount(); i++ ) {
					Document<?> d = (Document<?>) adapter.getItem(i);
					sum += d.sum();
					DataObject dobj = d.getData();
					if( dobj instanceof DeliveryEx )
						sumDlv += ((DeliveryEx)dobj).sum();
					else if( d instanceof OrderImplBase<?> )
						weight += ((OrderImplBase<?>)d).weight();
				}
				if(ct instanceof DebtDocEx)
					((DebtDocEx)ct).setDlvSum(sumDlv);
				
				ct.updateTotalSum(this, sum, weight, 0, R.id.tvTotalSum);
			}
		}
	}
}
