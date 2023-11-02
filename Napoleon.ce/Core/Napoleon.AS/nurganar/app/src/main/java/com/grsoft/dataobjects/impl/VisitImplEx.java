package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;

import java.io.File;
import java.util.Date;

public class VisitImplEx extends VisitImpl {
    static String photoTag = "";

    public static void setPhotoTag(String newTag) { photoTag = newTag; }

    @Override
    public void addPhoto(byte[] photo) {
        addPhoto(photo, photoTag);
        photoTag= "";
    }

    public void addPhoto(byte[] photo, String tag) {
        VisitItemEx vi = findPhoto(tag);
        if(vi == null) {
            vi = new VisitItemEx();
            vi.id = photo;
            vi.tag = tag;
            vi.date = new Date();
            data.items.add(vi);
        } else {
            File file = new File(new String(vi.id));
            file.delete();
            vi.id = photo;
            vi.date = new Date();
        }
        write();
        close();
    }

    public VisitItemEx findPhoto(String tag) {
        if(tag == null || tag.length() == 0) {
            return null;
        }
        for(VisitItem vi : data.items) {
            VisitItemEx ve = (VisitItemEx)vi;
            if(ve.tag.equals(tag))
                return ve;
        }

        return null;
    }

    public static void removePhoto(Long rowid, String id) {
        VisitImplEx doc = new VisitImplEx();
        doc.read(rowid);
        doc.close();

        for(VisitItem vi : doc.getData().items) {
            VisitItemEx ve = (VisitItemEx) vi;
            if (ve.tag.equals(id)) {
                File file = new File(new String(vi.id));
                file.delete();
                break;
            }
        }

        if (doc.isEmpty()){
            doc.delete();
            doc.close();
        }
    }
}
