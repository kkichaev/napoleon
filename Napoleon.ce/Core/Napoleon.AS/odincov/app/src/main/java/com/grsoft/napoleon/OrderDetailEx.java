package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	List<BonusDef> bonuses = new ArrayList<BonusDef>();
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
		findViewById(R.id.btnBonus).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.bonuses_dlg); }
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case R.id.bonuses_dlg:
			return createBonusDlg();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	private Dialog createBonusDlg() {
		AlertDialog.Builder result = new AlertDialog.Builder(this);
		result.setTitle(R.string.select_bonus);
		CharSequence[] items = new CharSequence[bonuses.size()];
		
		PriceImpl price = new PriceImpl();
		for(int i=0; i < bonuses.size(); i++){
			BonusDef bd = bonuses.get(i);
			
			if(bd.type == BonusDef.PRICE){
				price.read("id", bd.iditem);
				items[i] = String.format("%s %s", price.getData().name, Util.IntToScaleStr(bd.qty, Consts.QTY_SCALE));
			}else
				items[i] = String.format("сумма заказа %s руб.", Util.IntToScaleStr(bd.sum, Consts.SUM_SCALE));
		}
		
		result.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				makeBonus(bonuses.get(which));
			}
		});
		
		return result.create();
	}

	protected void makeBonus(BonusDef def) {
		BonusImpl bi = BonusImpl.fromOrder(doc, def);
		if( bi != null )
			BonusDetail.open(this, bi);
	}
	
	boolean canMakeBonus() {
		for(OrderItem oi : doc.getData().items){
			BonusDef bi = getBonusItem(oi.id);
			if( bi != null && oi.qty >= bi.qty )
				return true;
			}
		
			long sum = doc.sum();
			
			for(BonusDef bd : bonuses)
				if(bd.type != BonusDef.PRICE && bd.sum <= sum)
					return true;
			
		return false;
	}
	
	BonusDef getBonusItem(String id) {
		for(BonusDef item : bonuses)
			if(item.iditem.equals(id))
				return item;
		return null;
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		boolean showPack = (item.inPack() && ((CfgNpl)ConfigManager.getConfig()).isPackView);
		String qtyText;
		if( !showPack )
			qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		else {
			Price p = price.getData();
			int inPack = p.qtyInPack;
			if( inPack == 0 )
				inPack = Consts.QTY_SCALE;
			int qty = (int)((long)item.qty * Consts.QTY_SCALE / inPack);
			qtyText = Util.IntToScaleStr(qty, Consts.QTY_SCALE) + " у.";
		}
		
		BonusDef bi = getBonusItem(item.id);
		if( bi != null ) {
			String font = "", fontEnd = "";
			if( (item.qty / Consts.QTY_SCALE) < bi.qty ) {
				font = "<font color='red'>";
				fontEnd = "</font>";
			}
			qtyText += "<br>" + font + Util.IntToScaleStr(bi.qty, Consts.QTY_SCALE) + fontEnd;
		}
		tvQty.setText(Html.fromHtml(qtyText));
		tvQty.setGravity(Gravity.RIGHT);
		tvQty.setTextColor(color);

	}

	@Override
	protected void onResume() {
		super.onResume();

		bonuses.clear();
		BonusDefImpl.loadBonus(doc.getDate(), new BonusDefImpl.BonusAction() {

			@Override
			public boolean doAction(BonusDef item) {
				if( item.type != BonusDef.PRICE || doc.findItem(item.iditem) != null )
					bonuses.add(item);
				return true;
			}
		});
		
		View actionView = findViewById(R.id.btnBonus);
		if( doc.isEditable() && canMakeBonus() ) {
			View v = findViewById(R.id.llFocus);
			v.setVisibility(View.VISIBLE);
			if( haveFocusedGroup() == false ) {
				findViewById(R.id.btnFocus).setVisibility(View.GONE);
			}
			actionView.setVisibility(View.VISIBLE);
		} else {
			actionView.setVisibility(View.GONE);
		}
	}
	@Override
	protected void setAdapter(){
		lvItems.setAdapter(new Adapter());
	}
	
	public class Adapter extends OrderItemsAdapter{
		@Override
		int getResourceID() { return R.layout.orderdetailex_list_row; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			super.drawInternal(view, name, color, item, pos);
			TextView tvPrice = (TextView)view.findViewById(R.id.tvUnitPrice);
			
			DataObjectInfo dataObjectInfo = DataObjectInfo.getInstance();
			int costScale = dataObjectInfo.getScale(OrderItem.class, "cost");
			
			tvPrice.setText(Util.IntToScaleWStr(item.cost, costScale, Consts.PRICE_DEC_WIDTH, false));
			tvPrice.setGravity(Gravity.RIGHT);
			tvPrice.setTextColor(color);
		}
	}
}
