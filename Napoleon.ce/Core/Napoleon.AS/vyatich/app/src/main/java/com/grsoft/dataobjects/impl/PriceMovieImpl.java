package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.PriceMovie;

import java.io.File;

public class PriceMovieImpl extends DbObject<PriceMovie> {
    public void update(PriceMovie src) {
        data.id = src.id;
        data.file = "";
        if(read() && data.url.compareTo(src.url) != 0 && data.file.length() > 0) {
            File file = new File(data.file);
            file.delete();
            data.file = "";
        }

        data.url = src.url;
        data.received = 1;
        write();
    }

    @Override
    public boolean delete() {
        if(data.file.length() > 0) {
            File file = new File(data.file);
            file.delete();
        }
        return super.delete();
    }
}
