package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

@TableInfo(name="PSActions", keyFields="item,id")
@ServerInfo(name="Actions")
public class Action extends DataObject {
	public static final int DISTR_TYPE_DOC = 0; 
	public static final int DISTR_TYPE_PACKET = 1; 
	public static final int DISTR_TYPE_ITEM = 2; 
	
	public String id = "";
	public String item = "";
	public String name= "";
	
	public Date start = new Date();
	public Date end = new Date();
	
	public int forNewClient = 0;
	
	@Scale(value= Consts.SUM_SCALE)
	public int discount = 0;
	
	/**
	 * discount is prc or is sum 
	 */
	public int inPrc = 0;
	
	public int opAnd = 0;
	
	@Scale(value= Consts.SUM_SCALE)
	public int minSum = 0;
	public int minSku = 0;
	public int minKeg = 0;
	public int minPack = 0;
	public String mustBe = "";
	
	public int distrType = 0;
	
	public List<ActionItem> items = new ArrayList<ActionItem>();
	
	public static Action get(String action, String item) {
		DbReader r = new DbReader();
		String where = "id='" + action +"' and item='" + item +"'";
		Action ret = new Action();
		if(!r.select(ret, ret.getTableName(), where))
			ret = null;
		r.close();
		
		return ret;
	}
	
	public static List<Action> availActions(final Order doc) {
		final List<Action> ret = new ArrayList<Action>();
		
		String date = Long.toString(Util.getDayStart(doc.date).getTime());
		
		String where = "[start] <= " + date + " and [end] >= " + date;
		DataTraveler.travel(Action.class, new DataTraveler.Travel<Action>(true) {

			@Override
			public boolean travel(DataTraveler<Action> item) {
				if(item.data.canApply(doc))
					ret.add(item.data);
				return true;
			}
		}, where);
		
		return ret;
	}
	
	public boolean canApply(Order doc) {
		if(doc.findItem(item) == null)
			return false;
		
		if(forNewClient != 0) {
			DocList dl = DeliveryDoc.instance().docList(doc.id);
			if(dl.getCount() > 0)
				return false;
		}
		
		DocActionData data = new DocActionData();
		countDocData(doc, data);
		return checkConditions(data, 1);
	}
	
	void countDocData(Order doc, DocActionData data) {
		PriceImpl pi = new PriceImpl();
		PriceEx p = (PriceEx)pi.getData();
		for(OrderItem oi : doc.items) {
			if(inActionItems(oi))
				data.sku++;
			if(minKeg > 0 || minPack > 0) {
				p.id = oi.id;
				pi.read();
				if(p.isKeg > 0)
					data.keg++;
				else
					data.packs++;
			}
		}
		pi.close();
		data.sum = (int) doc.sum();
		data.haveItem = (mustBe.length() == 0 || doc.findItem(mustBe) != null);
	}
	
	boolean checkConditions(DocActionData data, int count) {
		if(!data.haveItem)
			return false;
		
		// check condintions
		int[] acTest = new int[] { count * minSum, count * minKeg, count * minPack, count * minSku };
		int[] docTest = new int[] { data.sum, data.keg, data.packs, data.sku };
		
		boolean res = true;
		for( int i=0; i<acTest.length; i++) {
			if(acTest[i] > 0) {
				int val = docTest[i] - acTest[i];
				res = (val >= 0);
				if(!res || (opAnd == 0))
					break;
			}
		}
		
		return res;
	}
	
	boolean inActionItems(OrderItem oi) {
		for(ActionItem ai : items)
			if(ai.id.equals(oi.id))
				return true;
		
		return false;
	}

	/**
	 * Значение суммы умножаем на Consts.QTY_SCALE
	 * @param doc
	 * @param pos
	 * @return
	 */
	int countDiscount(Order doc, OrderItemEx pos) {
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		p.id = pos.id;
		pi.read();
		pi.close();
		
		int qip = p.qtyInPack;
		if(qip == 0) qip = Consts.QTY_SCALE;
		int maxQty = pos.qty /qip;
		
		// если скидка - сумму считаем от упаковки
		int dsc = (inPrc == 0) ? discount * Consts.QTY_SCALE : (int)((long)pos.costWD * qip * discount / 10000);
		
		DocActionData data = new DocActionData();
		countDocData(doc, data);
		
		int totDiscount = 0;
		for(int i = 1; i<=maxQty; i++) {
			if(!checkConditions(data, i))
				break;
			totDiscount += dsc;
		}
		
		return totDiscount;
	}

	public void distributeDiscount(OrderEx doc, OrderItemEx docItem) {
		int sumToRemove = countDiscount(doc, docItem);
		if(sumToRemove == 0)
			return ;
		doc.actionSum = sumToRemove / Consts.QTY_SCALE;
		List<OrderItemEx> distrItems = new ArrayList<OrderItemEx>();
		
		if(distrType != DISTR_TYPE_ITEM) 
			for(OrderItem oi : doc.items) {
				if(oi != docItem && (distrType == DISTR_TYPE_DOC || inActionItems(oi)))
					distrItems.add((OrderItemEx) oi);
			}

		long sumQuant = sumToRemove / (distrItems.size() + 1);
		for(OrderItemEx oie : distrItems) {
			long curSum = oie.costWD * oie.qty;
			if(curSum < sumQuant) {
				oie.cost = 0;
				sumToRemove -= curSum;
			} else {
				sumToRemove -= sumQuant;
				oie.cost = (int)(((long)oie.costWD * oie.qty - sumQuant) / oie.qty);
			}
		}
		docItem.cost = (int)(((long)docItem.costWD * docItem.qty - sumToRemove) / docItem.qty);
		if(docItem.cost < 0)
			docItem.cost = 0;
	}
}

class DocActionData {
	public int sku = 0;
	public int keg = 0;
	public int packs = 0;
	public int sum = 0;
	public boolean haveItem = false;
}

