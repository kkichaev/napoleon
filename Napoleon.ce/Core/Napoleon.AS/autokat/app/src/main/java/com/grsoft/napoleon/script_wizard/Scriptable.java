package com.grsoft.napoleon.script_wizard;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

public interface Scriptable {
    /**
     * »нициализаци€ без записи документа в таблицу
     * @param context
     * @param gpsCoord
     * @param owner
     */
    void initDoc(Context context, GpsCoord gpsCoord, ScriptImpl owner, ScriptDefItem item);

    BaseFragment getView();

    Fragment getPreview();
}
