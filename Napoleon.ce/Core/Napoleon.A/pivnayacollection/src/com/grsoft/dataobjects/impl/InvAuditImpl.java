package com.grsoft.dataobjects.impl;

import java.util.Date;
import android.content.Context;
import android.widget.Toast;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.InvAudit;
import com.grsoft.dataobjects.InvAuditItem;
import com.grsoft.dataobjects.OrgInv;
import com.grsoft.napoleon.InvAuditDetail;
import com.grsoft.napoleon.InvAuditEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;


public class InvAuditImpl extends CreatableDocument<InvAudit> {

	@Override
	public void open(Context context) {	InvAuditDetail.open(context, getRowid()); }
	
	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		
		if(data.items.size() > 0)
			InvAuditEdit.open(context, getRowid(), false);
		else{
			delete();
			Toast.makeText(context, "Не выгржено оборудование для контрагента", Toast.LENGTH_SHORT).show();
		}
		
		return false;
	}
	
	@Override
	public void postInit() {
		Date now = Util.getDate();
		data.penult = now;
		data.last = now;
		
		DataTraveler.travel(OrgInv.class, new DataTraveler.Travel<OrgInv>() {

			@Override
			public boolean travel(DataTraveler<OrgInv> item) {
				InvAuditItem i = new InvAuditItem();
				i.id = item.data.id_i;
				i.qty = item.data.qty;
				data.items.add(i);
				return true;
			}}, "id='"+data.id+"' and tare=0");
		
		super.postInit();
	}
	

}
