package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.ISReturn;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.network.exception.RuntimeException;

public class DebtDocEx extends com.grsoft.napoleon.modules.print.DebtDoc {
	@Override
	public DocList docList(String orgId, String order, String where) {
		String whereStr = "";
		if( orgId != null ) {
			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = orgId;
			oi.read();
			oi.close();
			
			whereStr = "(id='" + orgId + "' or ido='" + o.ido + "')";
		}
		
		if( where != null && where.length() > 0 )
			whereStr += " AND " + where;
		
		return new DebetDocListEx(whereStr, order, LoadDelivery);
	}
	
	@Override public void viewOpened(Activity documentsView) { }	
	@Override public void viewClosed(Activity documentsView) { }

	List<String> getOrgs(DataObject doc, HashMap<String, ArrayList<String>> baseOrgs) {
		ArrayList<String> ret = null;
		
		String ido = null;
		if( doc instanceof DeliveryEx ) {
			ido = ((DeliveryEx)doc).ido;
		} else if( doc instanceof SalesEx ) {
			ido = ((SalesEx)doc).ido;
		} else if( doc instanceof IncassEx ) {
			ido = ((IncassEx)doc).ido;
		} else if( doc instanceof ReturnEx ) {
			ido = ((ReturnEx)doc).ido;
		} else if( doc instanceof ISReturn ) {
			ido = ((ISReturn)doc).ido;
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
		Map<String, Long> sums = new HashMap<String, Long>();
		
		HashMap<String, ArrayList<String>> baseOrgs = new HashMap<String, ArrayList<String>>();
		
		DocList list = docList(null, null);
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			long sum = d.sum();

			List<String> orgs = getOrgs(d.getData(), baseOrgs);
			
			for(String id : orgs) {
				long isum = sum;
				if( sums.containsKey(id))
					isum += sums.get(id);				
				sums.put(id, isum);
			}
		}
		list.close();
		
		writeSumMap(sums);
	}
}
