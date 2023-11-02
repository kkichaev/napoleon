package com.grsoft.dataobjects.impl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.grsoft.napoleon.main.QuestEditPreview;
import com.grsoft.napoleon.script_wizard.BaseFragment;
import com.grsoft.napoleon.script_wizard.QuestEdit;
import com.grsoft.napoleon.script_wizard.Scriptable;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

public class AnswerImplEx extends AnswerImpl implements Scriptable {
    @Override
    public void initDoc(Context context, GpsCoord gpsCoord, ScriptImpl owner, ScriptDefItem item) {
        initData(context, owner.getId(), gpsCoord);
        data.question = item.condParam;
    }

    @Override
    public BaseFragment getView() {
        return new QuestEdit();
    }

    @Override
    public Fragment getPreview() {
        return new QuestEditPreview();
    }
}
