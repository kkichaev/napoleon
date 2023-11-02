package com.grsoft.camera;

public interface BarcodeHandler {
    boolean onReadBarcode(String barcode, int type, long elapsesMs);
}
