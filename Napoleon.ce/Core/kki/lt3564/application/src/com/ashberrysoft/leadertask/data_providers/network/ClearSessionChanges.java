package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;
import android.os.Build;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.CreateSessionHandler.CreateSessionEntity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Locale;

public class ClearSessionChanges extends BaseSOAP<String> {

    private static final long serialVersionUID = 1L;

    public static final String METHOD_NAME = "ClearSessionChanges";

    private String mSessionOrder;
    private String mUUIDSession;

    public ClearSessionChanges(Context context, LeaderTaskUser user, String uid, String sessionOrder) {
        super(context, METHOD_NAME, user);
        mSessionOrder = sessionOrder;
        mUUIDSession = uid;
    }

    @Override
    public String getResultAction() {
        return null;
    }

    @Override
    protected String parseResponse(Reader reader) throws Exception {
        final SwitchParseHandler<ClearSessionChanges> handler = SwitchParseHandler.newInstance(reader);
        final ClearSessionChanges entity = handler.getData();

        return null;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write("<str_uid_session>"+ mUUIDSession + "</str_uid_session>");
        writer.write("<str_order>" + mSessionOrder  + "</str_order>");
    }
}
