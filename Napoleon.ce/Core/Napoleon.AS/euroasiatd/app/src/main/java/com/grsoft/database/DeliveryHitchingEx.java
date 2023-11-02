package com.grsoft.database;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliverySum;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.util.DatePeriod;

public class DeliveryHitchingEx extends DeliveryHitching {
	@Override
	public void onEnd() {
		super.onEnd();
		
		
		final Map<String, DeliverySum> sums = new HashMap<String, DeliverySum>();
		final Date now = new Date();
		
		DataTraveler.travel(Delivery.class, new DataTraveler.Travel<Delivery>() {

			@Override
			public boolean travel(DataTraveler<Delivery> item) {
				if (item.data.sumD > 0) {
					if (!sums.containsKey(item.data.id)) {
						DeliverySum ds = new DeliverySum();
						ds.id = item.data.id;
						ds.date = now;
						sums.put(item.data.id, ds);
					}
					
					DeliverySum d = sums.get(item.data.id);
					
					if (item.data.payDate.compareTo(now) < 0)
						d.dsum += item.data.sumD;
					else if (DatePeriod.daysDiff(now, item.data.payDate) < DebtDocEx.NOTICE_DAY_COUNT)
						d.dsum2 += item.data.sumD;
					
				}
				
				return true;
			}
		}, null);
		
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(DeliverySum.class));
		DbWriter.checkDBTable(DeliverySum.class);
		DbWriter w = new DbWriter();
		
		for(DeliverySum ds : sums.values())
			w.insertRecord(ds);
		
		w.close();
	}
}
