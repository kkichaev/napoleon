package com.grsoft.napoleon;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DocFilterOnClickListener;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;

import java.util.ArrayList;

public class MainEx extends Main {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

    }

    @Override
    protected ArrayList<MenuHandler> createDocMenuList() {
        ArrayList<MenuHandler> ret = super.createDocMenuList();
        ret.add(new MenuHandler(getString(R.string.wsorder_title), new Runnable() {
            @Override
            public void run() {
                WSOrderList.open(MainEx.this);
            }
        }));
        return ret;
    }

    @Override
    protected void onResume() {
        if (DocType.getCurDoc() == WSOrderDoc.instance())
            DocType.setCurDoc(SalesDoc.instance());

        super.onResume();
    }

    @Override
    protected DocFilterOnClickListener createDocFilter() {
        return new DocFilterOnClickListener(this) {
            @Override
            protected void initData(boolean creatableFilter) {
                super.initData(creatableFilter);
                data.remove(WSOrderDoc.instance());
            }
        };
    }
}
