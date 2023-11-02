package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;

public class ChangeUserPassword extends BaseSOAP<String> {

    private static final long serialVersionUID = 1L;
    private String mNewPassword;
    private Context mContext;

    public static final String METHOD_NAME = "ChangePassword";

    public ChangeUserPassword(Context context, LeaderTaskUser user, String newPassword) {
        super(context, METHOD_NAME, user);
        mNewPassword = newPassword;
        mContext = context;
    }

    @Override
    public String getResultAction() {
        return null;
    }

    @Override
    protected String parseResponse(Reader reader) throws Exception {

        final SwitchParseHandler<ErrorEntity> handler = SwitchParseHandler.newInstance(reader);
        final ErrorEntity entity = handler.getData();
        if (entity.getErrorCode() == 0) {
            // все нормально и пароль поменяли
            LTSettings settings = LTSettings.getInstance();
            settings.saveUser(mUser.getName(), mNewPassword);
            settings.setUserProfile(new LeaderTaskUser(mUser.getName(), mNewPassword));
            // и синхронизируемся по ProcessAll с новым пароле, удалив сессию
            //LTSettings.getInstance().setSessionUUID(null);
            Utils.startSync((LTApplication)mContext.getApplicationContext());
        }
        return null;
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        String name = "name";
        String old_password = "old_password";
        String new_password = "new_password";

        writer.write(getOpen(name));
        writer.write(mUser.getName());
        writer.write(getClose(name));

        writer.write(getOpen(old_password));
        writer.write(mUser.getPassword());
        writer.write(getClose(old_password));

        writer.write(getOpen(new_password));
        writer.write(mNewPassword);
        writer.write(getClose(new_password));
    }
}
