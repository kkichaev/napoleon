package com.grsoft.network;
import com.grsoft.database.DayDeliveryHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.MessageHitching;
import com.grsoft.database.PODHitching;
import com.grsoft.database.PrrogramSettingsHitching;
import com.grsoft.database.RemnantsHitching;
import com.grsoft.database.SyncInfoHitching;
import com.grsoft.aceteam.R;

import android.content.Context;

import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.dataobjects.ForcePutCommandArgs;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Debug;

import java.util.ArrayList;
import java.util.List;

public class WriteService extends WriteServiceBase{
    public static List<Hitching> recievers = new ArrayList<Hitching>();
    public static List<Hitching> requestObjects = new ArrayList<Hitching>();

    public WriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
        super(objectsToSend, rcvRemnants);

        reader.addHitching(recievers);

        requestHitchs.addAll(requestObjects);

        reader.addHitching(createPODHitching());
        reader.addHitching(createMessageHitching());
        if(rcvRemnants)
            reader.addHitching(new RemnantsHitching());

        if (Features.DDLV)
            reader.addHitching(new DayDeliveryHitching());

        if(objectsToSend != null)
            this.objectsToSend.add(new SyncInfoHitching());

        if(Features.SEND_PROGRAM_SETTINGS)
            this.objectsToSend.add(new PrrogramSettingsHitching());
    }

    protected Hitching createMessageHitching() {
        return new MessageHitching();
    }

    protected PODHitching createPODHitching(){ return PODHitching.instance(); }
}
