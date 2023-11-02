package com.grsoft.napoleon.script_wizard;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistry;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ScriptDefEx;
import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.List;

public class Model extends ViewModel {
    public ScriptImplEx doc = null;
    public ScriptDefEx scriptDef = null;
    private static final String STEP = "step";

    MutableLiveData<Integer> curStep = new MutableLiveData<>(0);

    CreatableDocument<?>[] docs;

    public Model(SavedStateHandle savedStateHandle){
        Bundle bundle = savedStateHandle.get(ScriptImplEx.class.toString());
        Log.d("Model", "model constructor");

        if (bundle != null) {
            doc = new ScriptImplEx();
            doc.read(bundle.getLong(ExtrasConst.DOC_ROW_ID_STR));
            doc.close();
            curStep.setValue(bundle.getInt(STEP));
            Log.d("Model", "model init doc: " + this.doc + " step:" + curStep.getValue() + " this: " + this);
        }

        savedStateHandle.setSavedStateProvider(ScriptImplEx.class.toString(), new DocSavedStateProvider());
    }

    private class DocSavedStateProvider implements SavedStateRegistry.SavedStateProvider {

        @Override
        public Bundle saveState() {
            Bundle bundle = new Bundle();
            if (doc != null) {
                Log.d("Model", "saveState");
                bundle.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
                bundle.putInt(STEP, curStep.getValue());
            }
            return bundle;
        }
    }

    public void init(ScriptImplEx doc, int step) {
        Log.d("Model", "init: " + doc + " this: " + this + " step: " + step);
        this.doc = doc;
        curStep.setValue(step);

        if(doc != null) {
            List<ScriptDefEx> res = DbReader.fetch(ScriptDefEx.class, "id=" + doc.getData().scriptId);
            if(res.size() > 0) {
                scriptDef = res.get(0);

                docs = doc.getDocuments();
            }
        }
    }

    public ScriptDefItem getCurScriptDef() {
        return scriptDef == null ? null : scriptDef.items.get(curStep.getValue());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(doc != null) {
            doc.close();
        }
        if(docs != null) {
            for(CreatableDocument<?> di : docs) {
                if(di != null) {
                    di.close();
                }
            }
        }
    }

    public boolean move(boolean forward) {
        if(docs == null) {
            return false;
        }

        boolean ret = false;
        int ci = curStep.getValue();
        if(forward) {
            if(ci < docs.length) {
                ci++;
                ret = true;
            }
        } else {
            if(ci > 0) {
                ci--;
                ret = true;
            }
        }
        if(curStep.getValue() != ci)
            curStep.postValue(ci);
        return ret;
    }

    public boolean isLastDoc() {
        return docs == null || curStep.getValue() == docs.length - 1;
    }

    /**
     * ¬озвращает документ. ≈сли документа нет, он создаетс€ и инициализируетс€, но не записываетс€
     * @param context
     * @return
     */
    public CreatableDocument< ? >  getCurDoc(Context context) {
        Log.d("Model", "getCurDoc: " + doc + " this: " + this + " docs:" + docs);
        int ci = curStep.getValue();
        if(docs == null || ci >= docs.length)
            return null;

        CreatableDocument<?> ret = docs[ci];
        ScriptItem item = doc.getData().items.get(ci);

        if(ret == null || item.state == ScriptItem.DOC_NONE) {
            ScriptDefItem sdi = scriptDef.items.get(ci);
            DocTypeBase dt = DocType.getDocType(sdi.curType);

            if(dt != null) {
                Document<?> cd = dt.create();
                if(cd instanceof CreatableDocument<?> && cd instanceof Scriptable) {
                    ((Scriptable)cd).initDoc(context, GPSUtilNew.getLastKnownLocation(), doc, sdi);
                    ret = (CreatableDocument<?>) cd;
                    docs[ci] = ret;
                    item.state = ScriptItem.DOC_PENDING;
                }
            }
        }

        if ((item.state == ScriptItem.DOC_INITED || item.state == ScriptItem.DOC_INITED) && item.date != null && item.date.getTime() > 0){
            ret.getData().created = item.date;
            ret.read();
            ret.close();
        }

        Log.d("Model", "getCurDoc ret: " + ret);
        return ret;
    }

    public LiveData<Integer> getCurStep() { return curStep; }

    public boolean saveCurrentDoc() {
        if(docs == null)
            return true;
        int ci = curStep.getValue();
        CreatableDocument<?> curDoc = docs[ci];
        if(curDoc.isEmpty()) {
            if(!scriptDef.items.get(ci).canSkip())
                return false;
            curDoc.delete();
            doc.skipItemsTo(ci);
        } else {
            curDoc.write();
            doc.updateDocument(ci, curDoc);
        }
        doc.write();
        return true;
    }

    public boolean isFirstDoc() {
        return curStep.getValue() == 0;
    }

    public boolean containsPurchase() {
        if(docs == null)
            return false;
        for(CreatableDocument<?> cd : docs) {
            if(cd instanceof PurchaseImpl) {
                return cd.sum() != 0;
            }
        }

        return false;
    }

    public PurchaseImpl getPurchase() {
        PurchaseImpl res = null;

        if(docs == null)
            return null;

        for(CreatableDocument<?> cd : docs) {
            if(cd instanceof PurchaseImpl) {
                if(cd.sum() != 0)
                    return (PurchaseImpl) cd;
                else
                    return null;
            }
        }

        return res;
    }

    public void closeScript() {
        doc.closeScript();
    }
}
