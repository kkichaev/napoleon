package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

import android.content.Context;

public abstract class OrderImplBase<T extends Order> extends CreatableDocument<T> implements Itemsable
{
    private final int DEF_DELAY_VALUE = 5;
    
    /**
     * Используется в методе updateQty для инициализации дополнительных полей OrderItem
     * @author 1111
     *
     */
    public interface UpdateQtyHandler {
    	void itemUpdated(OrderItem item, Order order, boolean isNewItem);
    }
    
    protected UpdateQtyHandler updateQtyHandler = null;

	abstract public void editProperties(Context ctx, boolean isOldOrder);
	
	/**
	 * просто возвращает new от документа - метод нужен для copy
	 * @return
	 */
	abstract public CreatableDocument<T> createInstance();
	
	public DocType getDocumentType() { return OrderDoc.instance(); }
	
	public void setUpdateQtyHandler(UpdateQtyHandler handler) { updateQtyHandler = handler; }
	
    @Override
	public long sum() {
		if( Features.DELIVERY_REPLACE_ORDER_SUM && data.number.length() > 0 ) {
			DeliveryImpl di = new DeliveryImpl();
			Delivery d = di.getData();
			d.id = data.id;
			d.number = data.number;
			boolean readed = di.read();
			di.close();
			if( readed )
				return di.sum();
		}

		return data.sum();
	}
    
    public int count() {
    	int qty = 0;
    	
    	if( data.items != null )
	    	for(OrderItem item : data.items )
	    		qty += item.qty;
    	
    	return qty / Consts.QTY_SCALE;
    }
	
    /**
     * вес товара по документу
     * @return точный вес документа масштаб Consts.WEIGHT_SCALE
     */
	public int weight() {
		int weight = 0;
		
		if( !Features.NO_WEIGHT_IN_ORDER && data.items != null ) {
			PriceImpl p = new PriceImpl();
			p.setReadingFields("weight");
			
			Price pd = p.getData();
			for (OrderItem item: data.items) {
				pd.id = item.id;
				
				if( p.read() )
					weight += FPOperation.itemMul(item.qty, pd.weight, Consts.QTY_SCALE);
			}
			p.close();
		}
		
		return weight;
	}

	protected void prepareDeleteItem(PriceImpl pi, OrderItem item) {
		updatePrice(pi, item.qty);
	}
	
	@Override
	public boolean delete()
	{
		boolean res = super.delete();

		if (res)
			deleteItems();

		return res;
	}

	public void deleteItems() {
		DataBaseManager.getDataBase().beginTransaction();
		try {
			if( !isExported() && data.items != null && checkPriceQty() )
			{
				PriceImpl pi = new PriceImpl();
				Price p = pi.getData();
				for(OrderItem item : data.items) {
					p.id = item.id;
					pi.read();
					prepareDeleteItem(pi, item);
				}
				pi.close();
			}

			data.items = new ArrayList<>();

			getDocumentType().refreshDocSum(data.id);
			DataBaseManager.getDataBase().setTransactionSuccessful();
		}
		catch(Exception e) { }
		finally { DataBaseManager.getDataBase().endTransaction(); }
	}

	public DataObject findItem(String itemId) { return data.findItem(itemId);  }
	
	/**
	 * Проверять остатки товара на складе в функции updateQty
	 * @return
	 */
	protected boolean checkPriceQty() { return true; }
	
	protected void updatePrice(PriceImpl price, int qty) {
		if (Features.WH_QTY)
			price.updateSkladQty(qty, data.whIndex);
		else
			price.updateQty(qty);
	}
	
	protected int checkPriceQty(PriceImpl p, int qty, OrderItem item) {
		int newQty = qty;
		int whQty = getItemValue(p.getData());
		
		if( ((CfgNplW)ConfigManager.getConfig()).checkPrice ) {
			int priceQty = whQty;
			if( item != null ) priceQty += item.qty;
			
			if( priceQty < qty ) {
				if( whQty < 0 ) newQty = 0;
				else newQty = priceQty;
			}
		}
		
		return newQty;
	}
	
	protected void beforeItemWrite(OrderItem item, Price p) {
		
	}
	
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
		Price price = priceImpl.getData();
		boolean ret = true;
		OrderItem item = (OrderItem) findUpdateItem(price);

		if( checkPriceQty() ) {
			int newQty = checkPriceQty(priceImpl, qty, item);
			if( newQty != qty ) {
				ret = false;			
				qty = newQty;
			}
		}

		int priceUpdate = 0;
		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty > 0 )
			{
				Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

				try {
					item = (OrderItem) itemClass.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				item.cost = cost;
				item.id = price.id;
				item.qty = qty;
				
				if(inPack) item.flags |= OrderItem.IN_PACK;
		
				if(updateQtyHandler != null)
					updateQtyHandler.itemUpdated(item, data, true);
				
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
					item.cost = cost;
					if(inPack) item.flags |= OrderItem.IN_PACK;
					else item.flags &= (~OrderItem.IN_PACK);
				} else if( item.cost != cost ) {
					item.cost = cost;					
				} else if (item.inPack() != inPack){
					if(inPack) item.flags |= OrderItem.IN_PACK;
					else item.flags &= (~OrderItem.IN_PACK);
				}else
					needUpdate = false;
				
				if(updateQtyHandler != null) {
					updateQtyHandler.itemUpdated(item, data, false);
					needUpdate = true;
				}
			}
		}
		
		if( needUpdate ) {
			if( qty != 0 )
				beforeItemWrite(item, price);
			
			write();
			if( priceUpdate != 0 && checkPriceQty() )
				updatePrice(priceImpl, priceUpdate);
			
			// refresh sum after writing
			getDocumentType().refreshDocSum(data.id);
		}
		
		return ret;
	}

	protected DataObject findUpdateItem(Price price) {
		return findItem(price.id);
	}

	@Override
	public CreatableDocument<T> copy() {
		CreatableDocument<T> copy = null;
		if( rowid != ExtrasConst.INVALID_ID ) {
			copy = createInstance();
			copy.read(rowid);
			copy.data.created = Util.getDateTime();
			copy.data.date = Util.getDate();
			copy.data.params = 0;
			copy.data.number = "";
			copy.data.podRemark = "";
			copy.rowid =  ExtrasConst.INVALID_ID;
			postCopyProcess(copy);
			copy.write();
			copy.close();
		}
		return copy;
	}

	protected void postCopyProcess(CreatableDocument<T> copy) {}

	public boolean initSilent(String orgId, GpsCoord coord) {
		return initSilent(null, orgId, coord);
	}

	@Override
	public boolean initSilent(Context context, String orgId, GpsCoord gpsCoord) {
		data.delay = DEF_DELAY_VALUE;
		data.supplyer = 0;
		data.sumType = 0;

		return super.initSilent(context, orgId, gpsCoord);
	}

	/**
	 * Автозаказ
	 * @param orgId
	 * @param coord
	 * @param items - если кол-во <= 0 то этот товар не добавляется в заказ
	 */
	public void autoorder(String orgId, GpsCoord coord, HashMap<String, Integer> items) {
		autoorder(orgId,coord,items,false);
	}

	@SuppressWarnings("unchecked")
	public void autoorder(String orgId, GpsCoord coord, HashMap<String, Integer> items, boolean inited) {
		if(!inited)
			initSilent(orgId, coord);
		
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) getClass());
		PriceImpl p = new PriceImpl();
		Price prc = p.getData();
		for(Entry<String, Integer> se : items.entrySet()) {
			int qty = se.getValue();
			if( qty > 0 ) {
				prc.id = se.getKey();
				if( p.read() ) {
					long cost = cs.getItemCost(prc, this);
					updateQty(p, qty, cost, false);
				}
			}
		}
		p.close();
	}
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		if( initSilent(orgId, coord) )
			this.editProperties(context, false);
		return false;
	}

	@Override
	public String getDescription(Context context) {
		return (data.number.length() > 0) ? 
				data.number : 
				super.getDescription(context); 
	}

	@Override
	public int getSumType() { return data.sumType; }

	@Override
	public int getItemColor() {return R.color.item_highlight; }

	@Override
	public int getItemValue(Price item) {
		if (Features.WH_QTY)
			return getSkladsItemValue(item);
		else
			return item.qty;
	}

	private int getSkladsItemValue(Price item) {
		int whIndex = data.whIndex;
		List<PriceQtyItem> whQty = item.whQty;

		return ( whIndex == 0 || whIndex > whQty.size() ) ?  item.qty : whQty.get(whIndex-1).qty;
	}

	public void editProperties(Context ctx) { editProperties(ctx, true); }
	
	public int qty() {
		int result = 0;
		
		if( data.items != null ) {
			for(OrderItem item : data.items)
				result += item.qty;
		}
		return result / Consts.QTY_SCALE;
	}
	
	/**
	 * Вызывает <b>write</b> внутри
	 * @param sumType
	 */
	@SuppressWarnings("unchecked")
	public void updateItemsCost(int sumType){
		Order order = getData();
		order.sumType = sumType;
		PriceImpl priceImpl = new PriceImpl();
		Price p = priceImpl.getData();
		
		boolean svF = Features.CAN_CHANGE_COST;
		Features.CAN_CHANGE_COST = false;
		CostStrategy cs = CostStrategy.getInstance((Class<? extends Document<?>>) this.getClass());
		try{
			for(OrderItem item : order.items){
				p.id = item.id;
				if (priceImpl.read()){
					item.cost = cs.getItemCost(p, this);
					postItemUpdate(item);
				}
			}
			
			if (write() != Consts.INVALID_ID)
				getDocumentType().refreshDocSum(order.id);
		}catch(Exception e){ 
			e.printStackTrace();
		}finally{
			priceImpl.close();
		}
		Features.CAN_CHANGE_COST = svF;
	}

	protected void postItemUpdate(OrderItem item) {}

	@Override
	public int getItemQty(Price item) {
		OrderItem oi = (OrderItem) findItem(item.id);
		
		if (oi == null)
			return 0;
		else 
			return oi.qty;
	}

	@Override
	public long getItemSum(Price item) {
		OrderItem oi = (OrderItem) findItem(item.id);
		
		if (oi == null)
			return 0;
		else {
			int qtyScale = DataObjectInfo.getInstance().getScale(OrderItem.class, "qty");
			return FPOperation.itemMul(oi.cost, oi.qty, qtyScale);
		}
	}
	
	/**
	 * Учитывать сумму документа в сумме сценария
	 */
	public boolean useDocSumInscriptSum(){
		return true;
	}

	public int countPack() {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
    	int qty = 0;
    	
    	if( data.items != null )
	    	for(OrderItem item : data.items ) {
	    		p.id = item.id;
	    		pi.read();
	    		int inPack = p.qtyInPack;
	    		if( inPack == 0 )
	    			inPack = Consts.QTY_SCALE;
	    		
	    		qty += (int)((long)item.qty * Consts.QTY_SCALE / inPack);
	    	}
    	
    	pi.close();
    	return qty / Consts.QTY_SCALE;
	}
	
	@Override
	public boolean isEmpty() {
		return data.items == null || data.items.size() == 0;
	}
}
