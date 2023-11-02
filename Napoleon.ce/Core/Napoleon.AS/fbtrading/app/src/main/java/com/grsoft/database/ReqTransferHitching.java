package com.grsoft.database;

import com.grsoft.dataobjects.Agent;
import com.grsoft.dataobjects.FBTransfer;
import com.grsoft.dataobjects.FBTransferCommit;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.util.Map;

public class ReqTransferHitching extends Hitching {
    Map<Object, Agent> agents;

    public ReqTransferHitching() {
        super(FBTransfer.class, "ReqTransfer", true);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        if(agents == null)
            agents = DbReader.fetchDic(Agent.class, "id");

        FBTransfer doc = (FBTransfer) rawObject.createDataObject(dataObject);
        doc.params |= ParamState.ofExported;
        dbProxy.insertRecord(doc);
    }
}
