package com.grsoft.dataobjects;

import java.util.Date;
import java.util.HashMap;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.ReturnRequestImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.ReturnRequestDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;

@TableInfo(name="returnLimit", keyFields="start,priceType")
@ServerInfo(name="ReturnLimit")
public class ReturnLimit extends DataObject {
	public static final int LIMIT_SUM = 0;
	public static final int LIMIT_WEIGHT = 1;
	
	public Date start = new Date();
	public Date end = new Date();
	
	public String priceType = "";
	
	public int limitType = 0;
	public long limit = 0;
	
	public int canOverlimit = 0;


	public long countCurrentLimit() {
		long value = limit;
		
		ValueCounter counter = (limitType == LIMIT_SUM) ? new SumCounter(priceType) : new WeightCounter(priceType);
		DatePeriod dp = new DatePeriod(start, end);
		DocList dl = ReturnRequestDoc.instance().docList(null, null, dp);
		for(Document<?> d : dl) {
			long cv = counter.count((ReturnRequestImpl) d);
			if( value < cv ) {
				value = 0;
				break;
			}
			value -= cv;
		}
		
		dl.close();
		counter.close();
		
		return value >= 0 ? value : 0;
	}
}

abstract class ValueCounter {
	DbReader reader = new DbReader();
	String priceType;
	
	HashMap<String, PriceEx> items = new HashMap<String, PriceEx>();
	
	public ValueCounter(String priceType) { this.priceType = priceType; }

	public long count(ReturnRequestImpl doc) {
		long count = 0;
		ReturnRequest r = doc.getData();
		
		for(OrderItem oi : r.items) {
			PriceEx p = getItem(oi.id);
			if(priceType.equals(p.idType)) {
				count += count((ReturnRequestItem)oi, p);
			}
		}
		
		return count;
	}
	
	public abstract long count(ReturnRequestItem item, PriceEx p);
	public void close() { reader.close(); }

	PriceEx getItem(String id) {
		PriceEx ret = items.get(id);
		if(ret == null) {
			ret = new PriceEx();
			String where = "id=?";
			reader.select(ret, ret.getTableName(), where, null, new String[] {id}, false);
			items.put(id, ret);
		}
		return ret;
	}
}

class WeightCounter extends ValueCounter {
	
	public WeightCounter(String priceType) { super(priceType); }

	@Override
	public long count(ReturnRequestItem item, PriceEx p) {
		long count = 0;
		int w = p.weight;
		for(ReturnItemDlv rdi : item.items) {
			int qty = rdi.qty;
			long ds = (long)qty * w / Consts.QTY_SCALE;
			count += ds;
		}
		return count / Consts.WEIGHT_SCALE;
	}

}

class SumCounter extends ValueCounter {
	public SumCounter(String priceType) { super(priceType); }

	@Override
	public long count(ReturnRequestItem item, PriceEx p) {
		long count = 0;
		for(ReturnItemDlv rdi : item.items) {
			int qty = rdi.qty;
			long ds = (long)qty * rdi.cost / Consts.QTY_SCALE;
			count += ds;
		}

		return count / Consts.SUM_SCALE;
	}
}

