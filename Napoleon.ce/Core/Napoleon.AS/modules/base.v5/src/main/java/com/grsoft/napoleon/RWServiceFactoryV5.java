package com.grsoft.napoleon;

import com.grsoft.database.Hitching;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.ReadServiceBase;
import com.grsoft.network.ReadServiceV5;
import com.grsoft.network.WriteServiceBase;
import com.grsoft.network.WriteServiceV5;

import java.util.List;

public class RWServiceFactoryV5 extends RWServiceFactoryNapoleon{
    @Override
    public ReadServiceBase createReadService(List<Hitching> hitchings) {
        return new ReadServiceV5(hitchings);
    }

    @Override
    public WriteServiceBase createWriteService(List<? extends ObjectListener> objectsToSend, boolean rcvRemnants) {
        return new WriteServiceV5(objectsToSend, rcvRemnants);
    }
}
