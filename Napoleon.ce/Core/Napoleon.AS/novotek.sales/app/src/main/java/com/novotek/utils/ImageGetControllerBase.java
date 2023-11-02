package com.novotek.utils;

import android.graphics.Bitmap;

import com.novotek.sales.PictureHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageGetControllerBase<T> {
    Map<String, T> images = new HashMap<>();

    void putImage(String key, T image) {
        images.put(key, image);
    }

    public void setImage(String key, T image) {
        Bitmap b = PictureHolder.get(key);
        if(b != null) {
            onImage(image, key, b);
        } else {
            putImage(key, image);
        }
    }

    public void update() {
        List<String> rmv = new ArrayList<>();

        for (Map.Entry<String, T> kv : images.entrySet()) {
            Bitmap b = PictureHolder.get(kv.getKey());
            if(b != null) {
                rmv.add(kv.getKey());
                onImage(kv.getValue(), kv.getKey(), b);
            }
        }

        for(String k : rmv) images.remove(k);
    }

    protected void onImage(T image, String key, Bitmap b) {}
}
