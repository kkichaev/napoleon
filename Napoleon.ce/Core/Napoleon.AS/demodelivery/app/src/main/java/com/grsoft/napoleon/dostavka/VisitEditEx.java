package com.grsoft.napoleon.dostavka;

import android.net.Uri;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.grsoft.camera.CameraActivity;
import com.grsoft.camera.TakePhotoHandler;
import com.grsoft.napoleon.documents.PhotoDocument;

import java.io.File;
import java.text.SimpleDateFormat;

public class VisitEditEx extends VisitEditDelivery{
    private final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

    @Override
    protected View.OnClickListener createPhotoClickHandler() {
        return view -> {
            CameraActivity.openCamera(VisitEditEx.this, new TakePhotoHandler() {
                @Override
                public File getPhotoFile() {
                    SimpleDateFormat sdf = new SimpleDateFormat(FILENAME_FORMAT);
                    String name = sdf.format(System.currentTimeMillis()) + ".jpeg";

                    File[] externalStorageVolumes =
                            ContextCompat.getExternalFilesDirs(getApplicationContext(), null);
                    File primaryExternalStorage = externalStorageVolumes[0];
                    File dataDir = new File(primaryExternalStorage, "datadir");
                    dataDir.mkdirs();

                    return new File(dataDir, name);
                }

                @Override
                public boolean photoSaved(File file, Uri savedUri) {
                    ((PhotoDocument)visit).addPhoto(file.getAbsolutePath().getBytes());
                    return true;
                }
            });
        };
    }
}
