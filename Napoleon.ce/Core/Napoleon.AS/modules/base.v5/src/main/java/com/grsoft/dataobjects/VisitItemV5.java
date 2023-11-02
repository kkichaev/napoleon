package com.grsoft.dataobjects;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitItemV5 extends VisitItem {
    public static int SMALL_PHOTO_DIMENSION = 220;

    @BlobSource
    @FieldOrder(order=2)
    public byte[] photo;

    @BlobSource
    @FieldOrder(order=3)
    public byte[] smallPhoto;

    @BlobSource
    @FieldOrder(order=4)
    public String smallSize = "";

    @Override
    public String getImageFileName() { return photo == null? "" : new String(photo); }

    public String getSmallFileName() { return smallPhoto == null? "" : new String(smallPhoto); }

    @Override
    public void setImageFileName(byte[] fn) {
        photo = fn;
        makeSmallPhoto();
    }

    @SuppressLint("DefaultLocale")
    void makeSmallPhoto() {
        String fn = getImageFileName();
        String smallfn = fn + ".small";
        try (FileOutputStream out = new FileOutputStream(smallfn)) {
            Bitmap b = resizeBitmap(fn, SMALL_PHOTO_DIMENSION, SMALL_PHOTO_DIMENSION);
            b.compress(Bitmap.CompressFormat.JPEG, 80, out);

            smallPhoto = smallfn.getBytes();
            smallSize = String.format("%d*%d", b.getWidth(), b.getHeight());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    Bitmap resizeBitmap(String photoPath, int targetW, int targetH) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        Bitmap src = BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true; //Deprecated API 21

        return BitmapFactory.decodeFile(photoPath, bmOptions);
    }

//    @Override
//    public Field getField(String name) {
//        if(name.equals("id"))
//            return null;
//        return super.getField(name);
//    }

    @Override
    public void deletePhoto() {
        super.deletePhoto();

        File f = new File(getSmallFileName());
        f.delete();
    }

//    @Override
//    public Field[] getFields() {
//        List<Field> ret = new ArrayList<>(Arrays.asList(super.getFields()));
//
//        for(Field f : ret) {
//            if(f.getName().equals("id")) {
//                ret.remove(f);
//                break;
//            }
//        }
//
//        Field[] arrs = new Field[ret.size()];
//        return ret.toArray(arrs);
//    }
}
