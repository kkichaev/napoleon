package com.grsoft.database;

import android.content.Context;
import android.os.Environment;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Planograms;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.PricePhotoItem;
import com.grsoft.napoleon.Features;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

public class PlanogramHitching extends RcvNewHitching {
    private boolean enableExternalStore = false;

    Context context;
    public PlanogramHitching(Context context) {
        super(Planograms.class, "Planograms");

        this.context = context;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            enableExternalStore = true;
            File photoDir = PricePhotoHitching.getPhotoDir(context);
            if (!photoDir.exists()){
                enableExternalStore = photoDir.mkdirs();

                if (enableExternalStore){
                    if(Features.SHARED_PICTURES == false ){
                        try{
                            File noMedia = new File(photoDir, ".nomedia");
                            noMedia.createNewFile();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRead(RawObject rawObject) throws RuntimeException {
        if (enableExternalStore) {
            Planograms data = (Planograms) rawObject.createDataObject(Planograms.class);
            if(data.photo != null && data.photo.length > 0) {
                try{
                    String fileName = String.format("%s.jpg", UUID.randomUUID().toString().replace("-", ""));
                    File file = new File(PricePhotoHitching.getPhotoDir(context), fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data.photo);
                    fos.close();

                    data.photo = file.getAbsolutePath().toString().getBytes();
                    dbProxy.insertRecord(data);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
