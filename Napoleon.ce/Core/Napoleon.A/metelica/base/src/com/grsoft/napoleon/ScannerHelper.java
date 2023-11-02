package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.util.Consts;

import android.util.Log;
import android.view.KeyEvent;

public class ScannerHelper {
	private static final String TAG = "Scanner helper";
	
	RemnantsImpl doc;
	PriceImpl price= new PriceImpl();
	StringBuilder barCode = new StringBuilder();
	CostStrategy cs;
	DocUpdated handler;
	
	public interface DocUpdated {
		void updated(RemnantsImpl doc, PriceImpl p);
		void notUpdated(String barcode);
	}
	
	public ScannerHelper(RemnantsImpl doc, DocUpdated handler) {
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
		boolean updated = false;
		Price p = price.getData();
		String table = DataObjectInfo.getInstance().getTableName(p.getClass());
		String where = "barcode like '%," + tval + ",%'";
		List<Long> ids = DbReader.readIds(table, where, null);
		if( ids.size() > 0 ) {
			price.read(ids.get(0));
			RemnantItem oi = (RemnantItem) doc.findItem(p.id);
			int cost = cs.getItemCost(p, doc);
			doc.updateQty(price, (oi == null) ? Consts.QTY_SCALE : oi.qty + Consts.QTY_SCALE, cost, false);
			if( handler != null ) {
				updated = true;
				handler.updated(doc, price);
			}
		}
		
		if(!updated && handler != null) {
			handler.notUpdated(tval);
		}
	}
}
