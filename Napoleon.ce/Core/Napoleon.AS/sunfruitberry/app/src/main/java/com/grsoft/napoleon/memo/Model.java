package com.grsoft.napoleon.memo;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentMemo;
import com.grsoft.dataobjects.MemoType;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PicStore;
import com.grsoft.dataobjects.impl.PicStoreImpl;
import com.grsoft.napoleon.debet_data.DebetList;
import com.grsoft.napoleon.util.debug.Path;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Model extends ViewModel {
    public AgentMemo doc;
    public OrgEx org;
    public Map<Object, MemoType> memoTypes = new HashMap<>();
    DebetList dogData = new DebetList();

    Uri photoUri = null;
    PicStore picture = null;

    MutableLiveData<Boolean> disabled = new MutableLiveData<>(false);

    public LiveData<Boolean> getDisabled() { return disabled; }
    public void setDisabled(boolean val) { disabled.postValue(val); }

    public MemoType getType(String id) {
        MemoType ret = memoTypes.get(id);
        return ret == null ? new MemoType() : ret;
    }

    public boolean isEmpty() {
        return !isValid();
    }

    public boolean isValid() {
        MemoType mt = memoTypes.get(doc.topic);
        if(mt == null) {
            return false;
        }
        if(!mt.unlock()) return doc.email.length() > 0;
        return doc.remark.length() > 0 && doc.idDog.length() > 0;
    }

    public boolean sendInvoice() {
        MemoType mt = memoTypes.get(doc.topic);
        if(mt == null) {
            return false;
        }
        return mt.sendingInvoice();
    }

    public void setDogovor(String dogId) {
        doc.idDog = dogId;
        doc.update(dogData.get(dogId));
    }

    public void loadPicture(Context context) {
        picture = doc.findPicture();
        if(picture != null) {
            photoUri = picture.getUri(context);
        } else {
            File f = new File(Path.getDataDir(), Long.toString(doc.created.getTime()));
            photoUri = Uri.fromFile(f);
        }
    }

    public void deletePicture() {
        if(picture != null) {
            PicStoreImpl.delete(picture.id);
            new File(new String(picture.picture)).delete();
            picture = null;
        }
    }

    void copyFile(Uri src, Uri dest, Context context) {
        try {
            File file = new File(dest.getPath());
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            try {
                bis = new BufferedInputStream(context.getContentResolver().openInputStream(src));
                bos = new BufferedOutputStream(new FileOutputStream(file, false));
                byte[] buf = new byte[1024];
                bis.read(buf);
                do {
                    bos.write(buf);
                } while (bis.read(buf) != -1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bis != null)
                        bis.close();
                    if (bos != null)
                        bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void updatePicture(Uri src, Context context) {

        copyFile(src, photoUri, context);

        String path = photoUri.getPath();
        if(picture == null) {
            picture = new PicStore();
            picture.id = UUID.randomUUID().toString().replace("-", "");
            picture.created = doc.created;
            picture.date = picture.created;
        }
        picture.picture = path.getBytes();
        DbWriter w = new DbWriter();
        w.insertRecord(picture);
        w.close();
    }
}
