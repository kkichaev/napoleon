package com.grsoft.napoleon;

import java.util.HashSet;
import java.util.Set;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Offer;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.FolderImpl;
import com.grsoft.dataobjects.impl.ISklad;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OfferDoc;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.ZeroPositionFilter;


public class WarehouseEx extends WarehouseNew {

	static int whIndex = 0;
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrow_ex;
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		Price p = price.getData();
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());

		View ret = super.getPriceView(node, convertView);
		TextView tv;
		int value;
		tv = (TextView)ret.findViewById(R.id.tvClmn3);
		value = ((Itemsable)document).getItemValue(p);
		tv.setText(Util.IntToScaleStr(value, Consts.QTY_SCALE));
		
		tv = (TextView)ret.findViewById(R.id.tvClmn4);
		value = cs.getItemCost(p, document);
		tv.setText(Util.IntToScaleStr(value, Consts.SUM_SCALE, Util.DEC_DELIM, false));
		return ret;
	}
	
	@Override
	protected void setTextColumnValue(TextView textView, int type, Price price) {
		@SuppressWarnings("unchecked")
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
		int qip = price.qtyInPack;
		if( qip == 0 )
			qip = Consts.QTY_SCALE;

		if(type == COLUMN_COST) {
			int cost = (int)((long)cs.getItemCost(price, document) * qip / Consts.QTY_SCALE);
			textView.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			return;
		}
		if(type == COLUMN_QTY_WH) {
			int value = (int)((long)((Itemsable)document).getItemValue(price) * qip / Consts.QTY_SCALE);
			textView.setText(Util.IntToScaleStr(value, Consts.QTY_SCALE, Util.DEC_DELIM, true));
			return;
		}
		super.setTextColumnValue(textView, type, price);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lvItemSelect.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(DocType.getCurDoc() == OfferDoc.instance()){
					Object node =  parent.getItemAtPosition(position);
				
					if(node instanceof PriceTreeNode){
						PriceImpl price = new PriceImpl();
						price.read("id",((PriceTreeNode)node).getId());
						int fid = price.getData().folderID;
						
						Cursor crs = DataBaseManager.getDataBase().query(
								DataObjectInfo.getInstance().getTableName(Price.class), 
								new String[]{"id"}, "folderid=?", new String[]{Integer.toString(fid)}, null, null, null);
						
						Set<String> ids = new HashSet<String>();
						
						for(OrderItem i : ((Offer)document.getData()).items)
							ids.add(i.id);
						
						while(crs.moveToNext()){
							String i = crs.getString(crs.getColumnIndex("id"));
							if(!ids.contains(i)){
								OrderItem oi = new OrderItem();
								oi.id = i;
								oi.qty = 1 * Consts.QTY_SCALE;
								((Offer)document.getData()).items.add(oi);
							}
								
						}
						
						document.write();
						((BaseAdapter)parent.getAdapter()).notifyDataSetChanged();
						
						FolderImpl fldImpl = new FolderImpl();
						fldImpl.read("id", fid);
						
						Toast.makeText(view.getContext(), getString(R.string.items_from_folder_was_added, fldImpl.getData().name), Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}});
	}

	@Override
	protected Filter createZeroPositionFilter() {
		int curIndex = 0;
		if( document instanceof ISklad ) {
			curIndex = ((ISklad)document).getWhIndex();
		} else if( document instanceof ReturnImplEx ) {
			curIndex = ((ReturnImplEx)document).getWhIndex();
		}
		
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
	
	@Override
	protected void postAdapterInit() {
		if ((adapter.getFilter(ZeroPositionFilter.NAME) == null) && (DocType.getCurDoc() == OrderDoc.instance()))
			adapter.putFilter(createZeroPositionFilter());
			
		super.postAdapterInit();
	}
}
