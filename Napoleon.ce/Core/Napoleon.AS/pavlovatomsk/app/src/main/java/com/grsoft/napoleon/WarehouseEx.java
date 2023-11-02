package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.BonusDefImpl;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;
import com.grsoft.util.view.dialog_helper.DialogHelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class WarehouseEx extends Warehouse {
	private static final String COSTYPE = "costype"; 
	private HashMap<String, BonusDef> actionItems = new HashMap<String, BonusDef>();
	
	static public void open(Context context,  int costype) {
		Intent i = new Intent(context, WarehouseEx.class);
		i.putExtra(COSTYPE, costype);
		context.startActivity(i);		
	}
	
	@Override
	protected BaseAdapter createListAdapter() {
		FoldersAdapter.resetCache();
		
		if( document instanceof ReturnImplEx)
			return new ReturnAdapter(this, document.getId());
		else
			return super.createListAdapter();
	}

	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		FoldersAdapter.resetCache();
		
		
		
		super.onCreate(savedInstanceState);
	}
	
	@Override protected int getLayoutId() { return R.layout.warehouseex; }
	
	@Override
	protected void onResume() {
		super.onResume();
		
		ArrayList<CharSequence> price = new ArrayList<CharSequence>(); 
		
		ConfigImpl ci = new ConfigImpl();
		Config c = ci.getData();
		c.key = "¬ид÷ены";
		ci.read();
		ci.close();
		
		DialogHelper.makeList(c.value, price);
		
		int ct = document == null ? 0 : document.getSumType();
		String priceText = "цена: ";
		if( ct < price.size() )
			priceText += price.get(ct).toString();
		
		((TextView)findViewById(R.id.tvCostInfo)).setText(priceText);
		
		if( document instanceof OrderImpl )
			actionItems = BonusDefImpl.getActiveBonuses(document.getDate()); 
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View v = super.getPriceView(node, convertView);

		TextView tv = (TextView)v.findViewById(R.id.tvPriceItemName);
		tv.setCompoundDrawablesWithIntrinsicBounds(actionItems.containsKey(price.getData().id) ? R.drawable.bonus_doc : 0, 0, 0, 0);

		return v;
	}
	
	@Override
	protected void postDocInited() {
		super.postDocInited();
		
		if(document.getRowid() == ExtrasConst.INVALID_ROWID && document instanceof OrderImpl){
			((OrderImpl)document).getData().sumType = getIntent().getIntExtra(COSTYPE, 0);
		}
	}
	
	protected Filter createZeroPositionFilter() {
		return	Features.COST_FILTER_IN_PRICE ? new ZeroPositionFilter(document, price) {
			@Override
			public boolean inset(long priceRowID, String id) {
				boolean result =  super.inset(priceRowID, id);
				
				if (result)
					result = checkQty(id);
				
				return result;
			}
		} : 
			new ZeroPositionFilter() {
			@Override
			public boolean inset(long priceRowID, String id) {
				boolean result =  super.inset(priceRowID, id);
				
				if (result)
					result = checkQty(id);
				
				return result;
			}
		};
	}

	protected boolean checkQty(String id) {
		Price p = PriceCash.getPrice(id);
		
		if (p == null)
			return false;
		
		return document instanceof OrderImplEx && ((OrderImplEx)document).getItemValue(p) > 0;		
	}
	
	class ReturnAdapter extends FoldersAdapter {

		HashSet<String> ids = new HashSet<String>();
		
		public ReturnAdapter(WarehouseManager warehouse, String orgId) {
			super(warehouse);
			
			com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
			for(Document<?> d : dl) {
				for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
					ids.add(di.id);
			}
			dl.close();
		}
		
		@Override public boolean inset(long rowid, String id) { return ids.contains(id); }
	}

	@Override
	protected int getDefaultColor(Price p) {
		if (document.getRowid() != ExtrasConst.INVALID_ROWID && DocType.getCurDoc() == OrderDoc.instance())
			if (((PriceEx)p).merc == 1 && ((PriceEx)p).chznak == 1)
				return getResources().getColor(R.color.mercchznak);
			else if (((PriceEx)p).merc == 1)
				return getResources().getColor(R.color.merc);
			else if (((PriceEx)p).chznak == 1)
				return getResources().getColor(R.color.chznak);
			else
				return super.getDefaultColor(p);
		else
			return super.getDefaultColor(p);
	}

	@Override
	protected void loadDailySales() {
		currentOrders.clear();
		
		Date beg = Util.getDate();
		Date end = new Date(beg.getTime() + 1000l * 3600 * 24);
		
		HashMap<String, Integer> weightCach = new HashMap<String, Integer>();
		DatePeriod dp = new DatePeriod(beg, end);
		Price p = null;
		
		DocList dl = OrderDoc.instance().docList(null, null, dp);
		for(Document<?> d : dl) {
			if (!(d instanceof OrderImplBase))
				continue;
			
			OrderImplBase<?> oi = (OrderImplBase<?>)d;
			for(OrderItem item : oi.getData().items) {
				long sum = (int)((long)item.cost * item.qty / Consts.QTY_SCALE);
				Integer baseWeight = weightCach.get(item.id);
				if( baseWeight == null ) {
					p = PriceCash.getPrice(item.id);

					if (p == null)
						continue;
					
					baseWeight = p.weight;
					weightCach.put(p.id, baseWeight);
				}
				
				double pack = 0;
				
				p = PriceCash.getPrice(item.id);
				if (p != null) {
					
					if (p.qtyInPack != 0)
						pack = (double)item.qty / p.qtyInPack;
				}
				
				//long weight = item.qty * baseWeight; /// (Consts.QTY_SCALE * Consts.WEIGHT_SCALE);
				long weight = FPOperation.itemMul(item.qty, baseWeight, Consts.QTY_SCALE);
				Integer folder = foldersCache.get(item.id);
				if( folder == null ) {
					p = PriceCash.getPrice(item.id);
					
					if (p != null) {
						folder = p.folderID;
						foldersCache.put(p.id, folder);
					}
				}
				
				WarehouseCurrentOrderDataEx val = (WarehouseCurrentOrderDataEx) currentOrders.get(folder);
				if( val == null ) {
					val = new WarehouseCurrentOrderDataEx();
					currentOrders.put(folder, val);
				}
				
				val.sum += sum;
				val.weight += weight;
				val.pack += pack;
			}
		}
		dl.close();
		
		if( folderTree.size() == 0 )
			folderTree.load();
	}
	
	static class WarehouseCurrentOrderDataEx extends WarehouseCurrentOrderData{
		double pack = 0.0;
	}
	
	@Override
	protected WarehouseCurrentOrderData getSales(int folderId) {
		WarehouseCurrentOrderDataEx ret = new WarehouseCurrentOrderDataEx();//currentOrders.get(folderId);
//		if( ret == null )
//			ret = new WarehouseCurrentOrderData();
		
		int index = folderTree.findFolder(folderId);
		if( index >= 0 ) {
			// добавим все подчиненные папки
			Folder f = folderTree.get(index);
			for(  ; index < folderTree.size(); index++) {
				Folder check = folderTree.get(index);
				if( check.level <= f.level && f != check )
					break;
				
				WarehouseCurrentOrderDataEx csum = (WarehouseCurrentOrderDataEx) currentOrders.get(check.id); 
				if( csum != null ) {
					ret.sum += csum.sum;
					ret.weight += csum.weight;
					ret.pack += csum.pack;
				}
			}
		}
		return ret;
	}
	
	public View getFolderView(FolderTreeNode node, View convertView) {
		int id = getFolderLayoutId();
		View result;
		if (convertView != null && convertView.getTag(id) != null)
			result = convertView;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}
		
		TextView tvOrgName = (TextView) result.findViewById(R.id.tvItemSelectRowName);
		tvOrgName.setText(node.name);
		linesController.prepareTextView(tvOrgName);
		tvOrgName.setTag(node);

		TextView tvSales = (TextView)result.findViewById(R.id.tvSales);
		if(tvSales != null ) {
			int visibility = View.GONE;
			if(isShowDailySales() && currentOrders.size() > 0) {
				visibility = View.VISIBLE;
				WarehouseCurrentOrderDataEx sales = (WarehouseCurrentOrderDataEx) getSales(node.id);
				String text = "";
				
				if( sales.sum > 0) {
					text = Util.IntToScaleStr(sales.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
					if(Features.SHOW_DAILY_WEIGHT_IN_WAREHOUSE) {
						text += "<br/>";
						text += DocType.getCurDoc().weightToString(sales.weight, getString(R.string.kg));
						//text += Util.IntToScaleStr(sales.weight, 1, Util.DEC_DELIM, true) + " " + getString(R.string.kg);
					}
					
					text += "<br/>";
					text += "<font color='red'>";
					text += String.format("%.2f", sales.pack);
					text += "</font>";
				}
					
				tvSales.setText(Html.fromHtml(text));
			}
			tvSales.setVisibility(visibility);
		}
		
		return result;
	}

	@Override
	public void editItem(long rowid) {
		if (document.getRowid() != ExtrasConst.INVALID_ROWID && DocType.getCurDoc() == OrderDoc.instance()) {
			price.read(rowid, false);
			OrgImpl org = new OrgImpl();
			org.read("id", orgid);

			if (((PriceEx) price.getData()).merc == 1 && ((PriceEx) price.getData()).chznak == 1
				&& (((OrgEx)org.getData()).merc == 0 || ((OrgEx)org.getData()).chznak == 0)) {
					Toast.makeText(this, R.string.mercurychznak_error, Toast.LENGTH_SHORT).show();
				return;
			}else if (((PriceEx) price.getData()).merc == 1 && ((OrgEx)org.getData()).merc == 0) {
				Toast.makeText(this, R.string.mercury_error, Toast.LENGTH_SHORT).show();
				return;
			}else if (((PriceEx) price.getData()).chznak == 1 && ((OrgEx)org.getData()).chznak == 0) {
				Toast.makeText(this, R.string.chznak_error, Toast.LENGTH_SHORT).show();
				return;
			}
		}

		super.editItem(rowid);
	}
}
