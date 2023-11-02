package com.grsoft.napoleon;

import android.graphics.Color;
import android.widget.TextView;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class WarehouseEx extends WarehouseNew {
	@Override
	public void setColor(TextView textView, Price price) {
		super.setColor(textView, price);
		
		if(((PriceEx)price).urgent > 0)
			textView.setBackgroundResource(R.drawable.urgent_item_back);
		else if (focusedItems.contains(price.id)
				|| focusedGroups.contains(price.folderID))
			textView.setBackgroundResource(R.drawable.focused_item_back);
		else
			textView.setBackgroundColor(Color.TRANSPARENT);
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		super.setTextColumnValue(textView, type, price);
		
		if(type == COLUMN_QTY_WH || type == COLUMN_QTY_ORD){
			Itemsable id = (Itemsable) document;
			int value = 0;

			boolean useInPack = false;
			String s = getString(R.string.oneitem);
			if (type == COLUMN_QTY_WH){
				value = id.getItemValue(price);
				useInPack = ((PriceEx)price).usePack > 0;
				
			}else if (type == COLUMN_QTY_ORD){
				value = id.getItemQty(price);
				
				DataObject item = id.findItem(price.id);
				
				if(item != null && item instanceof OrderItem)
					useInPack = ((OrderItem)item).inPack(); 
			}
			
			if(useInPack){
				int inPack = price.qtyInPack;
				if( inPack == 0 )
					inPack = Consts.QTY_SCALE;
				
				value = (int)((long)value * Consts.QTY_SCALE / inPack);
				s = getString(R.string.packitem);
			}
			
			String text = String.format("%s %s", Util.IntToScaleStr(value, Consts.QTY_SCALE, Util.DEC_DELIM,true), s);
			textView.setText(text);
		}
	}
}
