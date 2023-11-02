package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.OrgImpl;

public class DebtDocEx extends DebtDoc {
	public static void init() {
		DataObjectInfo.getInstance().replacePrimaryKey(DeliveryEx.class, "ido,number");
		DataObjectInfo.getInstance().replacePrimaryKey(PaymentEx.class, "ido,number");

		instance = new DebtDocEx();
	}
	
	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = "";
		if( orgId != null ) {
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = orgId;
			oi.read();
			oi.close();
			
			whereStr = "(ido='" + o.ido + "')";
		}
		
		if( where != null && where.length() > 0 )
			whereStr += " AND " + where;
		
		return new DebtDocList(whereStr, order, LoadDelivery);
	}

	List<String> getOrgs(DataObject doc, HashMap<String, ArrayList<String>> baseOrgs) {
		ArrayList<String> ret = null;
		
		String ido = null;
		if( doc instanceof DeliveryEx ) {
			ido = ((DeliveryEx)doc).ido;
		} else if( doc instanceof PaymentEx ) {
			ido = ((PaymentEx)doc).ido;
		}
		
		if( ido != null ) {
			ret = baseOrgs.get(ido);
			if( ret == null ) {
				ret = new ArrayList<String>();
				
				try {
					String table = DataObjectInfo.getInstance().getTableName(Org.class);
					String sql = "SELECT id from [" + table +"] where ido=?";
					Cursor c = DataBaseManager.getDataBase().rawQuery(sql, new String[] {ido} );
					while(c.moveToNext()) {
						ret.add(c.getString(0));
					}
					c.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				baseOrgs.put(ido, ret);
			}
		}
		
		return ret;
	}
	
	@Override
	public void refreshDocSum() throws RuntimeException {
		DbWriter.checkDBTable(OrgSum.class);
		Map<String, Integer> sums = new HashMap<String, Integer>();
		
		HashMap<String, ArrayList<String>> baseOrgs = new HashMap<String, ArrayList<String>>();
		
		DocList list = docList(null, null);
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			int sum = d.sum();

			List<String> orgs = getOrgs(d.getData(), baseOrgs);
			
			for(String id : orgs) {
				int isum = sum;
				if( sums.containsKey(id))
					isum += sums.get(id);				
				sums.put(id, isum);
			}
		}
		list.close();
		
		writeSumMap(sums);
	}
}
