package com.grsoft.napoleon;

import java.util.Date;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class WarehouseEx extends WarehouseNew {
	@Override
	protected FoldersAdapter createAdapterInstance() {
		FoldersAdapter ret = super.createAdapterInstance();
		if(document != null)
			ret.putFilter(new OrgMaskFilter(document.getId()));
		return ret;
	}
	
	public static class OrgMaskFilter extends Filter {

		public OrgMaskFilter(String id) {
			super("OrgMaskFilter" + id);
			
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx) oi.getData();
			oe.id = id;
			oi.read();
			oi.close();
			
			where = "((mask & " + Integer.toString(oe.mask) + ") <> 0)"; 
		}
		
	}
	
//	@Override
//	protected void openPresentation() {
//		WarehousePrezent.openPrezent(this, document, editMode);
//	}
	
	@Override
	protected Filter createZeroPositionFilter() {
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

	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}
	
	@Override
	protected void updateChildPriceView(View view, Price p) {
		super.updateChildPriceView(view, p);
		
		TextView tv = (TextView) view.findViewById(R.id.tvTranzit);
		tv.setText("");
		
		if (((PriceEx)p).tqty > 0)
			tv.setText(Util.IntToScaleStr(((PriceEx)p).tqty, Consts.QTY_SCALE));
		
		tv = (TextView) view.findViewById(R.id.tvDate);
		tv.setText("");
		
		Date d = ((PriceEx)p).tdate;
		
		if (d != null && d.getYear() > 100)
			tv.setText(Util.simpleDateFormat.format(d));
	}
	
	public View getPriceView(PriceTreeNode node, View convertView) {
		readPriceNode(node.getRowid());
		Price p = price.getData();

		View view;
		int id = getItemLayoutId();
		if (convertView != null && convertView.getTag(id) != null)
			view = convertView;
		else {
			view = View.inflate(this, id, null);
			view.setTag(id, true);
		}

		setName(view, p, 1, node);

		TextView tvClmn1 = (TextView) view.findViewById(R.id.tvClmn1);
		TextView tvClmn2 = (TextView) view.findViewById(R.id.tvClmn2);

		WindowManager wm = (WindowManager) view.getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);

		tvClmn1.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		tvClmn2.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
		
		CfgNplW config = (CfgNplW) ConfigManager.getConfig();

		setTextColumnValue(tvClmn1, config.priceClmn2Type, p);
		setTextColumnValue(tvClmn2, config.priceClmn3Type, p);

		if (Features.ID_COLUMN_IN_PRICE_LIST) {
			TextView tv = (TextView) view.findViewById(R.id.tvItemID);
			if (tv != null) {
				if (config.idInPriceList) {
					tv.setVisibility(View.VISIBLE);
					tv.setText(getItemId(p));
				} else
					tv.setVisibility(View.GONE);
			}

		}
		
		updateChildPriceView(view, p);
		
		return view;
	}
	
}
