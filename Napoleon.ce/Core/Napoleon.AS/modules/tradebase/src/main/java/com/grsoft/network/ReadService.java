package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.grsoft.database.DayDeliveryHitching;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitching;
import com.grsoft.database.PODHitching;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.Features;

public class ReadService extends ReadServiceBase{
	public static List<Hitching> recievers = new ArrayList<Hitching>();
	public static List<Hitching> requestObjects = new ArrayList<Hitching>();
	
	public ReadService(List<Hitching> hitchings) {
		this(hitchings, false, null);
	}
	
	public ReadService(List<Hitching> hitchings, boolean readAsManager, Context ctx) {
		super(hitchings, readAsManager, ctx);
		
		recieveHitch.add(createPODHitching());
		recieveHitch.add(createMessageHitching());
		
		if(recievers.size() > 0)
			recieveHitch.addAll(recievers);
		
		sendHitch.addAll(requestObjects);
		recieveHitch.addAll(requestObjects);
		
		if (Features.DDLV)
			recieveHitch.add(new DayDeliveryHitching());
	}

	protected Hitching createMessageHitching() {
		return new MessageHitching();
	}


	protected PODHitching createPODHitching(){ return PODHitching.instance(); }
	
	protected void clearBase(){
		super.clearBase();
		DbWriter.checkDBTable(OrgSum.class);
	}
}
