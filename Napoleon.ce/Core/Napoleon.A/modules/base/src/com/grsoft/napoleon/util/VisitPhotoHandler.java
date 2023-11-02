package com.grsoft.napoleon.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.CameraPreview;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.napoleon.documents.VisitDoc;

import java.io.File;

public class VisitPhotoHandler extends PhotoClickHandler implements PhotoClickHandler.EventHandler {
    VisitImpl doc;
    String photoFile = "";
    static String PIC_PATH = "VisitPhotoHandler.PhotoFile";

    public VisitPhotoHandler(VisitImpl doc) {
        super(doc, null, VisitDoc.instance());
        handler = this;
        this.doc = doc;
    }

    @Override public void prepareBoforeClick() { doc.write(); }
    @Override public void makePhotoFile(File newFile) { photoFile = newFile.getAbsolutePath(); }

    public void storeData(Bundle b) { b.putString(PIC_PATH, photoFile); }
    public void restoreData(Bundle b) { photoFile = b.getString(PIC_PATH); }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean res = false;
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == PhotoClickHandler.CAMERA_ACTIVITY) {
                if (photoFile != null && photoFile.trim().length() > 0) {
                    doc.addPhoto(photoFile.getBytes());
                    res = true;
                }
            } else if(requestCode == CameraPreview.CAMERA_PREVIEW_ACTIVITY) {
                doc.read(doc.getRowid(), false);
                res = true;
            } else if (requestCode == R.id.camera_preview) {
            	String path = data.getStringExtra(CameraPreview.PHOTO_PATH); 
            	doc.addPhoto(path.getBytes());
                res = true;
            }
        }
        return res;
    }
}
