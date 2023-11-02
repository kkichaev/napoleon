package com.grsoft.camera;

import android.app.Activity;
import android.view.View;

public interface BarcodeHandler {
    boolean onReadBarcode(Activity owner, String barcode, int type, long elapsesMs);
    void initActivity(Activity owner);
}
