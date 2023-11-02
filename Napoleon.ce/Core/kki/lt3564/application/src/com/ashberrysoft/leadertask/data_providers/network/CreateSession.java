package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.CreateSessionHandler.CreateSessionEntity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;

public class CreateSession extends BaseSOAP<String> {

    private static final long serialVersionUID = 1L;

    public static final String METHOD_NAME = "CreateSession";

    public CreateSession(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    public String getResultAction() {
        return null;
    }

    @Override
    protected String parseResponse(Reader reader) throws Exception {

        final SwitchParseHandler<CreateSessionEntity> handler = SwitchParseHandler.newInstance(reader);
        final CreateSessionEntity entity = handler.getData();
        String uid = entity.getUidSession();
        LTSettings.getInstance().setSessionUUID(uid);
        return null;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {

    }
}
