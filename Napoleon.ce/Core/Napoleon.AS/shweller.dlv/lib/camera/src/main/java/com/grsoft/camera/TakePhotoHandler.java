package com.grsoft.camera;

import android.net.Uri;

import java.io.File;

public interface TakePhotoHandler {
    File getPhotoFile();
    boolean photoSaved(File file, Uri savedUri);
}
