package com.grsoft.manager;

import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;

import com.grsoft.database.ArchiveMessageHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.LastScriptHitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.AgentReportData;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.LastScript;
import com.grsoft.dataobjects.Price;
import com.grsoft.script.dataobjects.ScriptDef;

import java.util.ArrayList;
import java.util.List;

public class ManagerNewEx extends ManagerNew{
    public static class LSParam extends DataObject{
        public String userid = "";
    }

    @Override
    protected int getMainContextMenu() {
        return R.menu.maincontextmenuex;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.itOpenScripts){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();

            Adapter a = list.getAdapter();
            final AgentReportData ai = (AgentReportData) a.getItem(info.position);

            LSParam param = new LSParam();
            param.userid = ai.id;

            List<Hitching> ret = new ArrayList<Hitching>();
            List<Hitching> res = new ArrayList<>();
            res.add(new RcvNewHitching(LastScript.class));
            ret.add(new ReportHitching("lastscript", param, res));

            UpdateProcess upp = new UpdateProcess( this, new UpdateCtrl() {

                @Override
                public void onFinish(boolean result) {
                    if( result )
                        ManagerNewEx.this.runOnUiThread(new Runnable() {
                            @Override public void run() { LastScriptView.open(ManagerNewEx.this, ai.id); }
                        });
                }

                @Override
                public void updateCtrl(boolean enabled) {
                }
            }, ret);
            upp.execute((Void[]) null);


            return true;
        }else
            return super.onContextItemSelected(item);
    }
}
