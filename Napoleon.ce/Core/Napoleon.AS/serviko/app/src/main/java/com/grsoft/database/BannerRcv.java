package com.grsoft.database;

import android.content.Context;

import com.grsoft.dataobjects.Banner;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BannerRcv extends HitchOnSelect {

    File dir;
    int index = 0;
    public BannerRcv(Context context) {
        super(Banner.class, "Banner");
        clearTable = true;
        dir = context.getExternalFilesDir(null);

    }

    @Override
    protected String getCondition() {
        Date dt = null;
        for(Banner b : DbReader.fetch(Banner.class, "", "date desc")) {
            dt = b.date;
            break;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return dt == null ? "01/01/1970" : sdf.format(dt);
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        Banner b = rawObject.createDataObject(dataObject);
        if(b.pic == null || b.pic.length == 0)
            return;

        String fileName = String.format("banner%d.jpg", index++);
        File f = new File(dir, fileName);
        if(f.exists()) {
            f.delete();
        }

        try {
            FileOutputStream fow = new FileOutputStream(f);
            fow.write(b.pic);
            fow.close();
            b.pic = f.getAbsolutePath().getBytes(StandardCharsets.UTF_8);
            dbProxy.insertRecord(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
