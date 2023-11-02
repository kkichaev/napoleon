package com.grsoft.napoleon.documents;

public class DebtDocEx extends com.grsoft.napoleon.modules.print.DebtDoc {
	
//	
//	@Override
//	public DocList docList(String orgId, String order, String where) {
//		String whereStr = "";
//		if( orgId != null ) {
//			OrgImpl oi = new OrgImpl();
//			OrgEx o = (OrgEx)oi.getData();
//			o.id = orgId;
//			oi.read();
//			oi.close();
//			
//			whereStr = "(id='" + orgId + "' or ido='" + o.ido + "')";
//		}
//		
//		if( whereStr.length() > 0 )
//			whereStr += " AND ";
//		
//		if( where != null && where.length() > 0 )
//			whereStr += where;
//		
//		return new DebtDocList(whereStr, order, LoadDelivery);
//	}
//	
//
//	List<String> getOrgs(DataObject doc, HashMap<String, ArrayList<String>> baseOrgs) {
//		ArrayList<String> ret = null;
//		
//		String ido = null;
//		if( doc instanceof DeliveryEx )
//			ido = ((DeliveryEx)doc).ido;
//		else if( doc instanceof PaymentEx)
//			ido = ((PaymentEx)doc).ido;
//		
//		if( ido != null ) {
//			ret = baseOrgs.get(ido);
//			if( ret == null ) {
//				ret = new ArrayList<String>();
//				
//				try {
//					String table = (new OrgImpl()).getTableName();
//					String sql = "SELECT id from [" + table +"] where ido=?";
//					Cursor c = DataBaseManager.getDataBase().rawQuery(sql, new String[] {ido} );
//					while(c.moveToNext()) {
//						ret.add(c.getString(0));
//					}
//					c.close();
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//				
//				baseOrgs.put(ido, ret);
//			}
//		}
//		
//		return ret;
//	}
//	
//	@Override
//	public void refreshDocSum() throws RuntimeException {
//		DbWriter.checkDBTable(OrgSum.class);
//		Map<String, Long> sums = new HashMap<String, Long>();
//		
//		HashMap<String, ArrayList<String>> baseOrgs = new HashMap<String, ArrayList<String>>();
//		
//		DocList list = docList(null, null);
//		for( int i=0; i<list.getCount(); i++ ) {
//			Document<?> d = list.get(i);
//			long sum = d.sum();
//
//			List<String> orgs = getOrgs(d.getData(), baseOrgs);
//			
//			for(String id : orgs) {
//				long isum = sum;
//				if( sums.containsKey(id))
//					isum += sums.get(id);				
//				sums.put(id, isum);
//			}
//		}
//		list.close();
//		
//		writeSumMap(sums);
//	}
}
