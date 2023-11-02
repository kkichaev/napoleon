package com.grsoft.napoleon;

import android.os.Bundle;

import com.grsoft.database.DocHandleResultHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.ReportHitching;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ReqDeliveries;
import com.grsoft.network.ExecServerModule;
import com.grsoft.util.MessageBox;

import java.util.Arrays;

public class DocumentsEx extends Documents implements ExecServerModule.Events {

    @Override
    protected int getContentViewID() { return R.layout.documentsex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnRefreshDelivery).setOnClickListener(v -> {
            requestDeliveries();
        });
    }

    void requestDeliveries() {
        Hitching[] h = new Hitching[] {
                new DocHandleResultHitching(),
                new Hitching(Delivery.class, "ReqDelivery"),
        };

        ReportHitching rh = new ReportHitching("get_deliveries",
                new ReqDeliveries(org.getData().id),
                Arrays.asList(h)
        );

        new ExecServerModule(this, findViewById(R.id.btnRefreshDelivery), rh, this).execute((Void) null);
    }

    @Override
    protected String orgInfo(Org o) {
        String ret = super.orgInfo(o);

        String info = ((OrgEx)o).info;
        if(info.length() > 0) {
            ret += "<br/>" + info;
        }

        return ret;
    }

    @Override
    public void onComplete(ExecServerModule sender, boolean result) {
        if(DocHandleResultHitching.Result.isFail())
            MessageBox.show(this, getString(R.string.error), DocHandleResultHitching.Result.message);
    }
}
