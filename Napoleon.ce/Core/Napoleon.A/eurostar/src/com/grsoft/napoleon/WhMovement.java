package com.grsoft.napoleon;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.MovementWhImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class WhMovement extends WarehouseNew {
	static int whIndex = 0;

	public static void open(Context c, MovementWhImpl doc, boolean editMode) {
		Intent i = new Intent(c, WhMovement.class);
		
		if( doc != null ) {
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
			i.putExtra(ExtrasConst.ORG_ID_STR, doc.getId());
			i.putExtra(ExtrasConst.EDIT_MODE_STR, editMode);
		}
		c.startActivity(i);		
	}
	
	@Override
	protected void createDocument() {
		document = MovementDoc.instance().create();
	}
	
	@Override protected int getItemLayoutId() { return R.layout.mv_wh_itemrow; }

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View ret = super.getPriceView(node, convertView);
		
		Price p = price.getData();
		int value = ((MovementWhImpl)document).getItemDestValue(p); 

		TextView tv;
		tv = (TextView)ret.findViewById(R.id.tvDestQty);
		tv.setText(Util.IntToScaleStr(value, Consts.QTY_SCALE));
		
		return ret;
	}

	@Override
	protected Filter createZeroPositionFilter() {
		int curIndex = 0;
		curIndex = ((MovementWhImpl)document).getWhIndex();
		
		if( whIndex != curIndex ) {
			whIndex = curIndex;
			FoldersAdapter.resetCache();
		}
		return new ZeroFilter();
	}

	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			if( !(document instanceof Itemsable) )
				return super.inset(priceRowID, id);
			
			boolean result = false; 			
			if(price.read(priceRowID))
				result = (((Itemsable)document).getItemValue(price.getData()) > 0);			
			return result;
		}
	}
}
