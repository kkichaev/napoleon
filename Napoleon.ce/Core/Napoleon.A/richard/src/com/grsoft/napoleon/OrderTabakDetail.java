package com.grsoft.napoleon;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.ScannedItems;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class OrderTabakDetail extends OrderDetail {
    Adapter adapter;
    
    
	public static void open(Context context, OrderImplEx doc) {
		Intent i = new Intent(context, OrderTabakDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override protected void setContentView() { setContentView(R.layout.order_detail_tabak); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { doScan(); }
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void setAdapter() {
		adapter = new Adapter();
		lvItems.setAdapter(adapter);
	}
	
	void doScan() {
		if(doc.isEditable() == false)
			return;
		
		IntentIntegrator ii = new IntentIntegrator(this);
		ii.initiateScan();
	}
	
	boolean checkAndAdd(Price p, String bc, BarcodeData bd) {
    	OrderItemEx oie = (OrderItemEx) doc.findItem(p.id);
    	if(oie != null) {
    		if(oie.isScanned()) {
				Toast.makeText(this, "Товар уже набран", Toast.LENGTH_LONG).show();
    			return false;
    		}
    		
    		boolean haveSamePack = (bd.isItemCode && oie.unitInpack == Consts.QTY_SCALE) || (!bd.isItemCode && oie.unitInpack == p.qtyInPack);
    		if(!haveSamePack) {
				Toast.makeText(this, "Упаковка в заказе не соответствует товару", Toast.LENGTH_LONG).show();
    			return false;
    		}
    		
    		for(ScannedItems si : oie.barcodes) {
    			if(si.barcode.equals(bc)) {
    				Toast.makeText(this, "GTIN уже добавлен", Toast.LENGTH_LONG).show();
    				return false;
    			}
    		}
    		
    		ScannedItems sadd = new ScannedItems();
    		sadd.barcode = bc;
    		oie.barcodes.add(sadd);
    		adapter.notifyDataSetChanged();
    		doc.write();

    		runOnUiThread(new Runnable() {
				@Override public void run() { Toast.makeText(OrderTabakDetail.this, "Код маркировки отсканирован", Toast.LENGTH_LONG).show(); }
			});
    		
    		return true;
    	}
    	
    	return false;
	}
	
	Price findItem(BarcodeData bd) {
		
        PriceEx pe = new PriceEx();
        DbReader r = new DbReader();
        boolean have = false;

        boolean bdo = false;
        bdo = r.select(pe, pe.getTableName(), "barcode like '%" + bd.itemBC + "%'");
        while(bdo) {
            int inpack = pe.qtyInPack;
            if(inpack == 0)
                inpack = Consts.QTY_SCALE;
            int checkCost = bd.isItemCode ? bd.cost : (int) ((long) bd.cost * Consts.QTY_SCALE / inpack);
            int itemCost = pe.mrc;
            if (checkCost == itemCost) {
                have = true;
                break;
            }
            bdo = r.selectNext(pe);
        }
        r.close();
        
        return have ? pe : null;
	}
	
	void onNewBarcode(final String bc) {
		if(bc == null){
			return;
		}
		
		final BarcodeData bd = new BarcodeData(bc);
		if(bd.haveError) {
			return;
		}
		Price p = findItem(bd);
		
        if(p != null) {
        	checkAndAdd(p, bc, bd);
        } else {
    		runOnUiThread(new Runnable() {

				@Override
				public void run() {
					String text = "Не найден ШК <b>" + bd.itemBC + "</b>" +
							"<br/>МРЦ <b>" + Util.IntToScaleStr(bd.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>" +
							"<br/>Полный код " + bc;
					MessageBox.show(OrderTabakDetail.this, "Ошибка", text);
				}
    			
			});
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null) {
			onNewBarcode(scanResult.getContents());
		}
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.orderdeliverydetail_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			OrderItemEx oie = (OrderItemEx)item;
			String text = "";
			int scanned = (int)((long)(oie).barcodes.size() * oie.unitInpack / Consts.QTY_SCALE);
			if(scanned > 0) {
				text = Integer.toString(scanned);
			}
			if(oie.isScanned())
				color = Color.GREEN;
			
			super.drawInternal(view, name, color, item);
			
			TextView tv = (TextView)view.findViewById(R.id.tvDispatch);
			tv.setText(text);
			tv.setTextColor(color);
		}
	}
}
