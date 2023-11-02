package com.grsoft.database;

import com.grsoft.dataobjects.SendPhotoCount;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class SendPhotoCountHitching extends Hitching{

    public interface Events {
        void onPhotoCount(int count);
    }

    public static Events handler = null;

    public SendPhotoCountHitching() {
        super(SendPhotoCount.class, "SendPhotoCount");
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        SendPhotoCount data = (SendPhotoCount) rawObject.createDataObject(dataObject);
        if(handler != null) {
            handler.onPhotoCount(data.count);
            handler = null;
        }
    }
}
