package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.Toast;

import com.grsoft.dataobjects.Banner;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ShelfShareDoc;
import com.grsoft.napoleon.documents.TaskDoneDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.MenuHandler;

public class MainEx extends Main{
    boolean mini = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(bundle == null) {
            BannerView.open(this, Banner.PLACE_START, null);
        }
    }

    @Override
    protected void doSync() {
        if(Features.CHECK_UNCOMPLETE_SCRIPTS) {
            if (ScriptImpl.hasUncomplete()) {
                Toast.makeText(this, R.string.has_uncomplete_scripts, Toast.LENGTH_LONG).show();
                openScriptList();
                return;
            }
            if(ShelfShareDoc.instance().hasIncomplete()) {
                Toast.makeText(this, R.string.has_incomplete_shelf, Toast.LENGTH_LONG).show();
                ShelfDocList.open(this);
                return;
            }
        }
        openUpdateActivity();
    }

    @Override
    protected boolean skipMenuItem(MenuHandler menuHandler) {
        if (mini)
            return menuHandler.name.equals(getString(R.string.plans));
        else
            return false;
    }

    @Override
    protected void onResume() {
        ConfigImpl config = new ConfigImpl();
        StringBuilder sb = new StringBuilder();
        config.getValue(sb, "MinimizeInterface");
        mini = sb.toString().trim().equals("1");

        if (mini) {
            DocType.removeType(OrderDoc.instance());
            DocType.removeType(TaskDoneDoc.instance());
            DocType.removeType(QuestionDoc.instance());
            DocType.removeType(RemnantsDoc.instance());
            DocType.removeType(VisitDoc.instance());
            DocTypeBase.setCurDoc(DebtDoc.instance());
        }else{
            DocType.addType(OrderDoc.instance());
            DocType.addType(TaskDoneDoc.instance());
            DocType.addType(QuestionDoc.instance());
        }

        super.onResume();
    }
}
