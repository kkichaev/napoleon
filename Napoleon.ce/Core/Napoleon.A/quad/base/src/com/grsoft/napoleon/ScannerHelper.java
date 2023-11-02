package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;

import android.util.Log;
import android.view.KeyEvent;

public class ScannerHelper {
	private static final String TAG = "Scanner helper";
	
	OrderImpl doc;
	PriceImpl price= new PriceImpl();
	StringBuilder barCode = new StringBuilder();
	CostStrategy cs;
	DocUpdated handler;
	
	public interface DocUpdated {
		void updated(OrderImpl doc, PriceImpl p);
	}
	
	public ScannerHelper(OrderImpl doc, DocUpdated handler) {
		this.doc = doc;
		cs = CostStrategy.getInstance(doc.getClass());
		this.handler = handler;
	}
	
	public void close() {
		price.close();
	}
	
	public boolean onKeyDown(KeyEvent event) {
		
		Log.d(TAG, String.format("Got %d,  %c", event.getKeyCode(), event.getNumber()));
		
		if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
			if( barCode.length() > 0) {
				String tval = barCode.toString();
				barCode.delete(0, barCode.length());
				doBarcode(tval);
			}
		} else
			barCode.append(event.getNumber());
		return false;
	}

	private void doBarcode(String tval) {
		Price p = price.getData();
		String table = DataObjectInfo.getInstance().getTableName(p.getClass());
		String where = "barcode = '" + tval + "'";
		List<Long> ids = DbReader.readIds(table, where, null);
		if( ids.size() > 0 ) {
			price.read(ids.get(0));
			OrderItem oi = (OrderItem) doc.findItem(p.id);
			int cost = cs.getItemCost(p, doc);
			doc.updateQty(price, (oi == null) ? Consts.QTY_SCALE : oi.qty + Consts.QTY_SCALE, cost, false);
			if( handler != null )
				handler.updated(doc, price);
		}
	}
}
