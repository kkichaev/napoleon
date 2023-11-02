package com.grsoft.dataobjects.impl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.script_wizard.BaseFragment;
import com.grsoft.napoleon.script_wizard.ScriptProp;
import com.grsoft.napoleon.script_wizard.Scriptable;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.GpsCoord;

public class ScriptPropImpl extends CreatableDocument<ScriptEx> implements Scriptable {
    @Override
    public void open(Context context) {

    }

    @Override
    public void initDoc(Context context, GpsCoord gpsCoord, ScriptImpl owner, ScriptDefItem item) {
        initData(context, owner.getId(), gpsCoord);
        data = (ScriptEx) owner.getData();
    }

    @Override
    public BaseFragment getView() {
        return new ScriptProp();
    }

    @Override
    public Fragment getPreview() {
        return null;
    }

    public void setPassportStatus(ScriptEx.PSTATYS status){
        ((ScriptEx)data).pstatus = status.ordinal();
    }

    public boolean isPassportChecked(){
        return ((ScriptEx)data).pstatus != ScriptEx.PSTATYS.NOT_CHECK.ordinal();
    }

    public boolean isPassportSuccess(){
        return ((ScriptEx)data).pstatus != ScriptEx.PSTATYS.SUCCESS.ordinal();
    }

    public void setPassportOKStatus(){
        setPassportStatus(ScriptEx.PSTATYS.SUCCESS);
    }

    public void setPassportOperatorStatus() {
        setPassportStatus(ScriptEx.PSTATYS.OPERATOR);
    }

    public void clearPassportStatus() {
        setPassportStatus(ScriptEx.PSTATYS.NOT_CHECK);
    }
}
