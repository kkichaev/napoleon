package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.PriceCount;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.SalesDetail;
import com.grsoft.napoleon.SalesPropertiesEditor;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.modules.print.util.DocHelper;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class SalesBaseImpl<T extends Sales> extends OrderImplBase<T> implements Itemsable{

	public static SalesPropertiesEditor Editor = new SalesPropertiesEditor();

	@Override
	protected void postCopyProcess(CreatableDocument<T> copy) {
		copy.data.number = DocHelper.makeDocNumber(this);
		DocHelper.saveDocNumber(getTableName(), copy.data.number);
	}
	
	@Override
	public void open(Context context) {
		SalesDetail.open(context, this);
	}
	
	@Override public DocType getDocumentType() { return SalesDoc.instance(); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		initDocNumber();		
		return super.init(context, orgId, gpsCoord);
	}
	
	@Override
	public boolean isEditable() {
		return super.isEditable() && 
				(Features.DISABLE_EDIT_AFTER_PRINT ? ((data.params & ParamState.ofPrinted) == 0) : true); 
	}
	
	public void markPrinted() { data.params |= ParamState.ofPrinted; }

	public void initDocNumber() {
		data.number = DocHelper.makeDocNumber(this);
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCount.open(context, itemRowid, (DbObject<Sales>)this);
	}

	@Override
	public int getItemColor() {
		return R.color.item_highlight;
	}

	@Override public String getNumber() { return data.number; }

	@Override
	public int getItemQty(Price item) {
		SalesItem si = (SalesItem) findItem(item.id);
		
		if (si == null)
			return 0;
		else 
			return si.qty;
	}
	
	@Override
	public int getItemValue(Price item) {
//		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNpl)ConfigManager.getConfig()).isPackView )
//			return (int)((long)((PricePrint)item).vanQty * Consts.QTY_SCALE / item.qtyInPack);

		return ((Price)item).vanQty;
	}

	@Override
	public long getItemSum(Price item) {
		SalesItem si = (SalesItem) findItem(item.id);
		
		if (si == null)
			return 0;
		else
			return si.sum;
	}
	
	protected int getUpdateQtyValue(Price price) {
		return getItemValue(price);
	}
	
	protected SalesItem createNewItem(Price price, int qty, long cost, boolean inPack) {
		SalesItem item = null;
		Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

		try {
			item = (SalesItem) itemClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		item.cost = (int)cost;
		item.id = price.id;
		item.qty = qty;
		
		item.ntd = price.ntd;
		
//		countTaxSum(price.tax1, item);
		((SalesItem)item).countTax(data, price.tax1);
		
		if(inPack) item.flags |= OrderItem.IN_PACK;

		if(updateQtyHandler != null)
			updateQtyHandler.itemUpdated(item, data, true);
		
		return item;
	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack){
		if( !isEditable() )
			return true;
		
		Price price = (Price)priceImpl.getData();
		boolean ret = true;
		SalesItem item = (SalesItem) findItem(price.id);

		int priceUpdate = 0;
		if( checkPriceQty() ) {
			int priceQty = getUpdateQtyValue(price);
			boolean isEmpty = priceQty == 0;
			if( item != null ) priceQty += item.qty;
			if( priceQty < qty ) {
				ret = false;			
				qty = isEmpty ? 0 : priceQty;
			}
		}

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty > 0 )
			{
				item = createNewItem(price, qty, cost, inPack);
				data.items.add(item);
				priceUpdate = - qty;
			} else
				needUpdate = false;
		} else
		{
			priceUpdate = item.qty;
			
			if( qty == 0 ) {
				data.items.remove(item);
			}
			else {
				priceUpdate -= qty;
				if( item.qty != qty ) {
					item.qty = qty;
					item.cost = (int)cost;
//					countTaxSum(price.tax1, item);
					((SalesItem)item).countTax(data, price.tax1);

					if(inPack) item.flags |= OrderItem.IN_PACK;
					else item.flags &= (~OrderItem.IN_PACK);
					
				} else if( item.cost != cost ) {
					item.cost = (int)cost;
//					countTaxSum(price.tax1, item);
					((SalesItem)item).countTax(data, price.tax1);
				} else
					needUpdate = false;
				
				if(updateQtyHandler != null) {
					updateQtyHandler.itemUpdated(item, data, false);
					needUpdate = true;
				}
			}
		}
		
		if( needUpdate )
		{
			if( qty != 0 )
				beforeItemWrite(item, price);
			write();
			updateQtyPrice(priceImpl, priceUpdate);
		}
		
		return ret;
	}

	public void refreshTax() {
		PriceImpl pi = new PriceImpl();
		Price pp = (Price) pi.getData();

		for(OrderItem oi : data.items) {
			pp.id = oi.id;
			pi.read();
			((SalesItem)oi).countTax(data, pp.tax1);
		}

		pi.close();

	}


	protected void updateQtyPrice(PriceImpl priceImpl, int priceUpdate) {
		Price price = (Price) priceImpl.getData();
		if( priceUpdate != 0 && checkPriceQty() ) {
			price.vanQty += priceUpdate;
			priceImpl.write();
		}
		
		// refresh sum after writing
		getDocumentType().refreshDocSum(data.id);
		DebtDoc.instance().refreshDocSum(data.id);
	}
	
	@Override
	protected void updatePrice(PriceImpl price, int qty) {
		((Price)price.getData()).vanQty += qty;
		price.write();
	}

//	protected void countTaxSum(int tax, SalesItem item) {
//		item.sum = (int)FPOperation.itemMul(item.cost, item.qty, Consts.QTY_SCALE);
//		double val = (double)item.cost * 100 / (100 + tax);
//		item.taxSum = (int)(item.sum - val * item.qty / Consts.QTY_SCALE + 0.5);
//		item.costWOtax = (int)(val + 0.5);
//	}
	
	@Override
	public void updateItemsCost(int sumType) {
		Sales doc = getData();
		doc.sumType = sumType;
		PriceImpl priceImpl = new PriceImpl();
		Price p = (Price)priceImpl.getData();
		
		boolean svF = Features.CAN_CHANGE_COST;
		Features.CAN_CHANGE_COST = false;
		try{
			for(OrderItem it : doc.items){
				SalesItem item = (SalesItem)it;
				p.id = item.id;
				if (priceImpl.read()) {
					item.cost = (int)CostStrategy.getInstance((Class<? extends Document<?>>) this.getClass()).getItemCost(p, this);
//					countTaxSum(p.tax1, item);
					((SalesItem)item).countTax(data, p.tax1);
				}
			}
			
			if (write() != Consts.INVALID_ID)
				getDocumentType().refreshDocSum(doc.id);
		}catch(Exception e){ 
			e.printStackTrace();
		}finally{
			priceImpl.close();
		}
		Features.CAN_CHANGE_COST = svF;
	}

	@Override
	public long sum() {
		long result = 0;
		if( data.items != null ) {
			for (OrderItem item: data.items)
				result += ((SalesItem)item).sum;
		}
		return result;
	}
	
	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		Editor.edit(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<T> createInstance() {
		return (CreatableDocument<T>) SalesDoc.instance().create();
	}

	protected void processInit(OrderImplBase<?> src) {}

	protected void initItem(OrderItem oi) {}

	@Override
	public boolean delete() {
		if(Features.CANT_DEL_PRINTED_DOCS && (data.params & ParamState.ofPrinted) != 0)
			return true;
		return super.delete();
	}
	
	public int weightBrutto() {
		int weight = 0;
		
		if( data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("weight,brutto");
			
			Price pd = (Price) p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() ) {
					int iw = (pd.brutto == 0) ? pd.weight : pd.brutto;
					weight += (int)((long)item.qty * iw / Consts.QTY_SCALE);
				}
			}
			p.close();
		}
		
		return weight;
	}
}
