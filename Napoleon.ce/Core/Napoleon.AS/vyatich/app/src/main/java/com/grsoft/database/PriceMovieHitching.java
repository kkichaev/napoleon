package com.grsoft.database;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.PriceMovie;
import com.grsoft.dataobjects.impl.PriceMovieImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class PriceMovieHitching extends Hitching {
    PriceMovieImpl pm = new PriceMovieImpl();

    public PriceMovieHitching() {
        super(PriceMovie.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            DataBaseManager.getDataBase().execSQL("update " + pm.getTableName() + " set received=0");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnd() {
        super.onEnd();
        pm.close();
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        PriceMovie data = (PriceMovie) rawObject.createDataObject(dataObject);
        pm.update(data);
    }
}
