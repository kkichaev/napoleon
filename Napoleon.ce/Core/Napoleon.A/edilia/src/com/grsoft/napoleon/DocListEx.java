package com.grsoft.napoleon;

import android.graphics.Color;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	DeliveryImpl di = new DeliveryImpl();
	boolean readed = false;
	
	@Override
	protected void onDestroy() {
		di.close();
		super.onDestroy();
	}
	
	@Override
	protected int getDocColor(Document<?> doc) {
		readed = false;
		if( doc instanceof OrderImplEx) {
			OrderEx oe = (OrderEx) doc.getData();
			if( oe.number.length() > 0 ) {
				Delivery d = di.getData();
				d.id = oe.id;
				d.number = oe.number;
				readed = di.read();
				if( !readed ) {
					oe.number = "";
					doc.write();
				}
				if(readed && OrgUtils.isDocsDiff(oe, d))
					return Color.RED;
			}
		}
		
		return super.getDocColor(doc);
	}
	
	@Override
	protected boolean countSumFromDocuments(boolean useFilter) {
		return (DocType.getCurDoc() == OrderDoc.instance()) || super.countSumFromDocuments(useFilter);
	}
	
	@Override
	protected int countDocs(DocListAdapter adapter) {
		if( (DocType.getCurDoc() != OrderDoc.instance()))
			return super.countDocs(adapter);
		
		int count = 0;
		for( int i=0; i<adapter.getCount(); i++ ) {
			Document<?> d = (Document<?>) adapter.getItem(i);
			OrderEx oe = (OrderEx)d.getData();
			if( oe.items != null && oe.items.size() > 0 )
				count++;
		}
		return count;
	}
	
	@Override
	protected long getDocSum(Document<?> doc) {
		if( doc instanceof OrderImplEx && ((Order)doc.getData()).number.length() == 0 )
			return 0;

		return super.getDocSum(doc);
	}
	
	@Override
	protected String docSumText(Document<?> doc) {
		if( doc instanceof OrderImplEx) {
			if( readed ) {
				String text = "<b>" + Util.IntToScaleStr(di.getData().sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
				return text;
			}
		}
		return super.docSumText(doc);
	}
}
