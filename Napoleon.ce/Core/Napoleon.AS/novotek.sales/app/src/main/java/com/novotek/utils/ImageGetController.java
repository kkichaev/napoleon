package com.novotek.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

public class ImageGetController extends ImageGetControllerBase<ImageView> {
    @Override
    protected void onImage(ImageView image, String key, Bitmap b) {
        image.setImageBitmap(b);
        image.setVisibility(View.VISIBLE);
    }
}
