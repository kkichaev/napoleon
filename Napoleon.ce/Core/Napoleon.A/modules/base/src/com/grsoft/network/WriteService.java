package com.grsoft.network;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DayDeliveryHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PrrogramSettingsHitching;
import com.grsoft.database.RemnantsHitching;
import com.grsoft.database.SyncInfoHitching;
import com.grsoft.napoleon.Features;

public class WriteService extends WriteServiceBase{
	public static List<Hitching> recievers = new ArrayList<Hitching>();
	public static List<Hitching> requestObjects = new ArrayList<Hitching>();
	
	public WriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants){
		super(objectsToSend, rcvRemnants);
		
		reader.addHitching(createPODHitching());
		reader.addHitching(createMessageHitching());
		if(rcvRemnants)
			reader.addHitching(new RemnantsHitching());
		
		reader.addHitching(recievers);
		requestHitchs.addAll(requestObjects);
		
		if (Features.DDLV)
			reader.addHitching(new DayDeliveryHitching());
		
		if(objectsToSend != null) 
			this.objectsToSend.add(new SyncInfoHitching());
		
		if(Features.SEND_PROGRAM_SETTINGS)
			this.objectsToSend.add(new PrrogramSettingsHitching());
	}

	protected MessageHitching createMessageHitching() {
		return new MessageHitching();
	}
	
	protected PODHitching createPODHitching(){ return PODHitching.instance(); }
}
