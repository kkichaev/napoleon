package com.serviko.sales;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.serviko.dataobjects.Price;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class PictureHolder {

    public interface Handler {
        void onReceive(String id, Bitmap img);
    }
    static List<Handler> handlers = new ArrayList<>();
    static WeakHashMap<String, Bitmap> images = new WeakHashMap<>();

    static List<String> active = new ArrayList<>();

    public static void addHandler(Handler h) {
        handlers.add(h);
    }

    public static void removeHandler(Handler h) {
        handlers.remove(h);
    }

    public static Bitmap get(final String picUrl) {
        Bitmap res = images.get(picUrl);
        if(res != null)
            return res;

        if(!active.contains(picUrl)) {
            addActive(picUrl);
            new Thread(() -> {
                try {
                    InputStream in = new java.net.URL(picUrl).openStream();
                    Bitmap b = BitmapFactory.decodeStream(in);
                    if(b != null) {
                        addImage(picUrl, b);
                        onReceive(picUrl, b);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    removeActive(picUrl);
                }
            }).start();
        }
        return null;
    }

    public static void setImage(ImageView iv, Price item) {
//        if(iv != null) {
//            Bitmap b = get(item.code, BASE_URL);
//            if(b != null) {
//                iv.setImageBitmap(b);
//            } else {
//                iv.setImageResource(R.drawable.coming_soon);
//            }
//        }
    }

    synchronized static void onReceive(String id, Bitmap bmp) {
        for(Handler h : handlers) {
            try {
                h.onReceive(id, bmp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized static void addActive(String id) {
        if(active.contains(id) == false)
            active.add(id);
    }

    synchronized static void removeActive(String id) {
        active.remove(id);
    }

    synchronized static void addImage(String id, Bitmap bmp) {
        images.put(id, bmp);
    }
}
