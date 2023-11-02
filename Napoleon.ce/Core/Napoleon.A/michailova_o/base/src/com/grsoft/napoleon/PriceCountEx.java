package com.grsoft.napoleon;

import java.util.ArrayList;
import com.grsoft.dataobjects.OffTakeCoeff;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OffTakeCoeffImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase.UpdateQtyHandler;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import android.os.Bundle;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(DocType.getCurDoc() == OrderDoc.instance()){
			OrderImpl ordImpl = (OrderImpl) document;
			ordImpl.setUpdateQtyHandler(ordUpdateQty);
		}
	}
	
	@Override
	protected void makeSaleHistory(Price p) {
		OffTakeHistory.inflator = new OffTakeCoeffReader(p);
		super.makeSaleHistory(p);
	}
	
	private UpdateQtyHandler ordUpdateQty = new UpdateQtyHandler() {

		@Override
		public void itemUpdated(OrderItem item, Order order, boolean isNewItem) {
			if(item != null){
				OrderItemEx ie = (OrderItemEx) item;
				ie.rekzak = -1;
				
				if(history != null && isComplexSalesHistory()){
					ArrayList<OffTakeHistory.Item> h = history.getHistory(price.getData().id);
					
					if(h != null && h.size() > 0)
						ie.rekzak = h.get(0).qty;
				}
			}
			
		}};
}

class OffTakeCoeffReader extends OffTakeHistory.OffTakeInflator {
	int coef = OFF_TAKE_COEF;
	public OffTakeCoeffReader(Price p) {
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		final String KEY = "OFFTAKE_KEY";
		
		if(cfg.getValue(sb, KEY)){
			try{
				coef = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		OffTakeCoeffImpl ci = new OffTakeCoeffImpl();
		OffTakeCoeff ce = ci.getData();
		ce.id = p.id;
		if( ci.read() )
			coef = ce.coef;
		ci.close();
	}
	
	@Override public int getOffTake() { return coef; }
}