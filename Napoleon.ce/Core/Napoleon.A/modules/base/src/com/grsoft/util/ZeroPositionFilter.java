package com.grsoft.util;

import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;

public class ZeroPositionFilter extends Filter{
	public static String NAME = "ZeroPositionFilterFilter";
	
	/**
	 * т.к. SalesDoc определен невсегда - здесь будет на него ссылка
	 */
	public static DocType SalesDoc = null;
	protected static String prevWhere = "";
	
	Document<?> document;
	PriceImpl price;
	CostStrategy costStrategy;
	
	public ZeroPositionFilter() {
		super(NAME);
		initWhere();
	}
	
	void initWhere() {
		if( Features.PRINT_MODULE && SalesDoc != null && SalesDoc.getClass().isAssignableFrom(DocType.getCurDoc().getClass()) )
			where = "vanQty>0";
		else
			where = "qty>0"; 
		
		if(prevWhere != where) {
			FoldersAdapter.resetCache();
			prevWhere = where;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ZeroPositionFilter(Document<?> document, PriceImpl price) {
		super(NAME);
		initWhere();
		
		this.document = document;
		this.price = price;
		if(document != null)
			this.costStrategy = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
	}

	@Override
	public boolean inset(long priceRowID, String id) {
		if(document != null && price.read(priceRowID)) {
			return (costStrategy.getItemCost(price.getData(), document) > 0);
		}
		return super.inset(priceRowID, id);
	}
}
