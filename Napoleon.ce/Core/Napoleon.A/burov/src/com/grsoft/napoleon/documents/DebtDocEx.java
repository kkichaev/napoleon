package com.grsoft.napoleon.documents;

import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.modules.print.DebtDoc;
import com.grsoft.network.exception.RuntimeException;

public class DebtDocEx extends DebtDoc {
	
	@Override
	public void refreshDocSum() throws RuntimeException {
		Map<String, Integer> sums = new HashMap<String, Integer>();
		DocList list = docList(null, null);
		
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			
			if(d instanceof PaymentImpl){
				String id = d.getId();
				int sum = d.sum();
				if( sums.containsKey(id))
					sum += sums.get(id);
				
				sums.put(id, sum);
			}
		}
		
		list.close();
		
		writeSumMap(sums);
	}
	
	@Override
	public void refreshDocSum(String orgId) {
		try{
			int sum = 0;
			DocList list = docList(orgId, null);

			for( int i=0; i<list.getCount(); i++ ){
				Document<?> d = list.get(i);
				if(d instanceof PaymentImpl)
					if( d != null ) sum += d.sum();
			}
			
			list.close();
			
			OrgSum os = new OrgSum();
			os.id = orgId;
			os.sum = sum;
			os.type = this.name;
			
			DbWriter w = new DbWriter();
			w.insertRecord(os);
			w.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
