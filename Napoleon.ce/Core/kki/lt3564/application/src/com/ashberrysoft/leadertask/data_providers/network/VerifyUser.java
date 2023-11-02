package com.ashberrysoft.leadertask.data_providers.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Locale;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.VerifyUserHandler.VerifyUserEntity;

import static com.ashberrysoft.leadertask.application.LTSettings.iCanBuyLeadertask;

public class VerifyUser extends BaseSOAP<String> {

    private static final long serialVersionUID = 1L;

    public static final String METHOD_NAME = "VerifyUser";
    private boolean mIsSuccesful;

    private String mInviteUUID;
    private String mInviteNameDir;
    private String mInviteEmailDir;
    private String mInviteOrg;

    public VerifyUser(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    public String getResultAction() {
        if (mIsSuccesful) {
            if (mErrorCode == 0) {
                return ServiceConstants.ACTION_LOGIN;
            } else {
                return ServiceConstants.ACTION_LOGIN_WITHOUT_SYNC;
            }
        } else {
            return ServiceConstants.ACTION_NOT_SUCCESSFUL_LOGIN;
        }
    }

    public String getInviteName() {
        return mInviteNameDir;
    }

    public String getInviteOrg() {
        return mInviteOrg;
    }

    public String getInviteEmail() {
        return mInviteEmailDir;
    }

    public String getInviteUUID() {
        return mInviteUUID;
    }

    @Override
    protected String parseResponse(Reader reader) throws Exception {
        mIsSuccesful = true;


        final SwitchParseHandler<VerifyUserEntity> handler = SwitchParseHandler.newInstance(reader);
        final VerifyUserEntity entity = handler.getData();
        mInviteUUID = entity.getInviteUUID();
        mInviteEmailDir = entity.getInviteEmail();
        mInviteNameDir = entity.getInviteName();
        mInviteOrg = entity.getInviteOrg();
        mErrorCode = entity.getErrorCode();
        if (mErrorCode == 0 || mErrorCode == 6 || mErrorCode == 15) {
            if (!entity.getEmployees().isEmpty()) {
                final ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(entity
                        .getEmployees().size());

                iCanBuyLeadertask = entity.getEmployees().size() == 1;
 
                for (Employee employee : entity.getEmployees()) {
                    operations.add(ContentProviderOperation//
                            .newInsert(EmployeeContract.CONTENT_URI)//
                            .withValues(employee.getContentValues(null))//
                            .build());
                }

                mContext.getContentResolver().delete(EmployeeContract.CONTENT_URI, null, null);
                mContext.getContentResolver().applyBatch(LeaderTaskProviderMetaData.AUTHORITY, operations);
            }
            return null;
        }

        // try {
        // final Persister serializer = new Persister();
        // final VerifyuserResponse response = serializer.read(VerifyuserResponse.class, reader, false);
        // mErrorCode = response.mErrorCode;
        // if (mErrorCode == 0 || mErrorCode == 6 || mErrorCode == 15) {
        // return null;
        // }
        // } catch (Exception e) {}

        mIsSuccesful = false;
        switch (mErrorCode) {
        case 3:
            return mContext.getString(R.string.error_wrong_auth);
            
        case 24:
            return mContext.getString(R.string.error_need_confirm_registration);
            
        case 9:
            return mContext.getString(R.string.error_account_frozen);

        case 16:
            return mContext.getString(R.string.error_end_emp_limit);

        case -1:
        case 22222:
            return mContext.getString(R.string.error_wrong_serv);

        case 26:
            return mContext.getString(R.string.error_api_disabled);

        case 14:
            return mContext.getString(R.string.error_account_blocked);

        default:
            return mContext.getString(R.string.error_server) + " " + mErrorCode;
        }
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        writer.write("<system_name>android</system_name>");
        writer.write("<system_version>" + Build.VERSION.RELEASE + "</system_version>");
        writer.write("<device>" + Build.MODEL + "</device>");
        writer.write("<app_version>" + ((LTApplication) mContext.getApplicationContext()).getApplicationBuildVersion()
                + "</app_version>");
        writer.write("<language>" + mContext.getString(R.string.currlang) + "</language>");
        writer.write("<system_language>" + Locale.getDefault().getLanguage() + "</system_language>");
    }
}
