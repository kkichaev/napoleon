package com.grsoft.dataobjects.impl;

import android.os.Bundle;

import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.napoleon.main.SignHelper;
import com.grsoft.napoleon.script_wizard.ScriptProp;

import java.io.File;
import java.util.Date;
import java.util.UUID;

public class VisitImplEx extends VisitImpl {
    static String photoTag = "";

    public static void setPhotoTag(String newTag) { photoTag = newTag; }

    @Override
    public void addPhoto(byte[] photo) {
        addPhoto(photo, photoTag);
        photoTag= "";
    }

    public void addPhoto(byte[] photo, String tag) {
        VisitItemEx vi = new VisitItemEx();
        vi.id = photo;
        vi.tag = tag;
        vi.date = new Date();
        data.items.add(vi);
        write();
        close();
    }

    public String getSignPath() {
        for(VisitItem i : data.items)
            if (((VisitItemEx)i).tag.equals(ScriptProp.SIGN_TAG))
                return new String(i.id);

        String file = String.format("%s.png", UUID.randomUUID().toString().replace("-",""));
        return String.format("%s/%s",SignHelper.getSignPath(), file);
    }

    public void setSignature(String file) {
        for(VisitItem i : data.items)
            if (((VisitItemEx)i).tag.equals(ScriptProp.SIGN_TAG)) {
                i.id = file.getBytes();
                return;
            }

        VisitItemEx i = new VisitItemEx();
        i.id = file.getBytes();
        i.tag = ScriptProp.SIGN_TAG;
        data.items.add(i);
        write();
        close();
    }

    public boolean hasSignature(){
        for(VisitItem i : data.items)
            if (((VisitItemEx)i).tag.equals(ScriptProp.SIGN_TAG))
                return true;

        return false;
    }
}


