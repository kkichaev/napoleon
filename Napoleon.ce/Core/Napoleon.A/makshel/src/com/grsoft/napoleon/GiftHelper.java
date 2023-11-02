package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Gift;
import com.grsoft.dataobjects.GiftItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class GiftHelper {
	OrderImplEx doc;

	List<KeyValue> gifts = new ArrayList<KeyValue>();
	Gift gift = new Gift();
	String giftitem = "";
	String giftpriev = "";
	
	public GiftHelper(OrderImplEx doc) {
		this.doc = doc;
	}
	
	public int giftQty() { return gift.qty; }
	
	public int giftDialogId() {
		return R.id.sel_gift_dlg;
	}
	
	public boolean loadGift(String priceId, StringBuilder giftDef) {
		boolean ret = false;
		long now = new Date().getTime();
		
		StringBuilder where = new StringBuilder();
		where.append("start <= ").append(now).append(" and finish >= ").append(now).append(" and id_i='").append(priceId).append("'");
		DbReader r = new DbReader();		
		if(r.select(gift, DataObjectInfo.getInstance().getTableName(Gift.class), where.toString())){
			ret = true;
			
			PriceImpl p = new PriceImpl();
			Price prc = p.getData();
			for(GiftItem i : gift.items){
				prc.id = i.id;
				p.read();
				
				if(giftDef != null ) {
					if(giftDef.length() > 0)
						giftDef.append(" или ");					
					giftDef.append(p.getData().name);
				}
				
				gifts.add(new KeyValue(prc.id, prc.name));
			}
			p.close();
				
			for(OrderItem i : ((Order)doc.getData()).items ){
				OrderItemEx e = (OrderItemEx)i;
				
				if(e.gift.equals(gift.giftid))
					giftitem = e.id;
			}
				
			if(gifts.size() == 1)
				giftitem = gifts.get(0).key.toString();
		}
		r.close();
		
		return ret;
	}
	
	int giftQty(int qty) {
		return (int) (gift.qty != 0 ? (long)qty * Consts.QTY_SCALE / gift.qty / Consts.QTY_SCALE * Consts.QTY_SCALE : 0);
	}
	
	public boolean needShowGiftDialog(int qty) {
		return gift.items.size() > 1 && giftQty(qty) > 0;
	}
	
	public void updateGift(int qty) {
		int q = giftQty(qty);
		PriceImpl p  = new PriceImpl();
		p.read("id", giftitem);
		doc.updateQty(p, q, 0, gift.giftid);
		
		if(giftpriev.length() > 0 && !giftitem.equals(giftpriev)){
			p = new PriceImpl();
			p.read("id", giftpriev);
			doc.updateQty(p, 0, 0, gift.giftid);
		}
	}
	
	public Dialog createSelectGiftDialog(Context context, int id, Runnable onGiftSelect) {
		if( id != R.id.sel_gift_dlg )
			return null;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.select_gift);
		int sz = gifts.size();
		CharSequence[] data = new CharSequence[sz];
		
		int checked = -1;
		
		for(int i = 0; i < sz; i++){
			KeyValue p = gifts.get(i);
			data[i] = p.value.toString();
			
			if (checked == -1 && p.key.toString().equals(giftitem))
				checked = i;
		}
		
		builder.setSingleChoiceItems(data, checked, new OnGiftSelect(onGiftSelect));
		return builder.create();
	}
	
	class OnGiftSelect implements DialogInterface.OnClickListener {
		Runnable run;
		
		public OnGiftSelect(Runnable run) {
			this.run = run;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			giftpriev = giftitem;
			giftitem = gifts.get(which).key.toString();
			
			run.run();
		}
	}
}
