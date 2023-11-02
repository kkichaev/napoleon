package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.AgentOrgHitching;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.database.PotenzialOrgHitching;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.PriceHitchingEx;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.TaskSendHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dealer;
import com.grsoft.dataobjects.OrgData;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgNotes;
import com.grsoft.dataobjects.OrgRegion;
import com.grsoft.dataobjects.OrgType;
import com.grsoft.dataobjects.PlanogramDef;
import com.grsoft.network.NetworkAsyncTask;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected int getContentView() {
		return R.layout.updatedbex;
	}
	
	@Override
	protected Hitching getOrgHitching() {
		return new OrgHitchingEx();
	}
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
//		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return new PriceHitchingEx();
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(OrgRegion.class, "OrgRegion"));
		ret.add(new Hitching(OrgType.class, "OrgType"));
		ret.add(new Hitching(Dealer.class, "Dealer"));
		ret.add(new RcvNewHitching(AgentPrefix.class, "AgentPrefix"));
		
		if (((CheckBox)findViewById(R.id.cbPlanogramm)).isChecked())
			ret.add(new RcvNewHitching(PlanogramDef.class));
		
		return ret;
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = new ArrayList<ObjectListener>();
		
		PotenzialOrgHitching poh = new PotenzialOrgHitching("Org");
		if( poh.size() > 0 ){
			result.add(poh);
			result.add(new AgentOrgHitching(poh));
		}
			
		ObjectExportListener ol = new TaskSendHitching();
		if( ol.size() > 0 )
			result.add(ol);
		
		ol = new OrgNotesSender();
		if( ol.size() > 0 )
			result.add(ol);
		
		ol = new OrgDataSender();
		if(ol.size() > 0)
			result.add(ol);

		return result;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		NapoleonEx.loaded = false;
		return true;
	}
}

class OrgNotesSender extends Hitching implements ObjectExportListener {
	List<OrgNotes> list = new ArrayList<OrgNotes>();
	
	public OrgNotesSender() {
		super(OrgNotes.class, "OrgNotes");
		
		OrgNotes on = new OrgNotes();
		DbWriter.checkDBTable(on.getClass());
		String table = DataObjectInfo.getInstance().getTableName(OrgNotes.class); 
		DbReader r = new DbReader();
		boolean bdo = r.select(on, table, null);
		while( bdo ) {
			list.add(on);
			
			on = new OrgNotes();
			bdo = r.selectNext(on);
		}
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		return list.get(i);
	}
}

class OrgDataSender extends Hitching implements ObjectExportListener {
	List<OrgData> list = new ArrayList<OrgData>();
	
	public OrgDataSender() {
		super(OrgData.class);
		DataTraveler.travel(OrgData.class, new DataTraveler.Travel<OrgData>(true) {
			@Override
			public boolean travel(DataTraveler<OrgData> item) {
				list.add(item.data);
				return true;
			}}, null);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		return list.get(i);
	}
}

class OrgHitchingEx extends OrgHitching {
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		OrgEx dobj = (OrgEx)rawObject.createDataObject(dataObject);
		dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase();
		dobj.contacts = new ArrayList<Contact>();
		
		if(dobj.contact.trim().length() > 0){
			Contact c = new Contact();
			c.name = dobj.contact;
			c.phone = dobj.contactPhone;
			dobj.contacts.add(c);
		}
			
		if(dobj.cheif.trim().length() > 0){
			Contact c = new Contact();
			c.name = dobj.cheif;
			c.phone = dobj.cheifPhone;
			dobj.contacts.add(c);
		}
		
		dbProxy.insertRecord(dobj);
	}
	
	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
}
