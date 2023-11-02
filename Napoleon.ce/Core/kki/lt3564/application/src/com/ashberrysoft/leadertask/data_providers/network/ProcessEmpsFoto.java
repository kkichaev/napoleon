package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler;

/**
 * Created by Samsung on 24.11.2015.
 */
public class ProcessEmpsFoto  extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessEmpsFotos";
    private static ArrayList<Emp> mListEmps = new ArrayList<>();

    public ProcessEmpsFoto(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {

        final ContentResolver cr = mContext.getContentResolver();

        final Cursor v = cr.query(EmpContract.CONTENT_URI, null, null, null, null);
        writer.write(getOpen(OBJECTS_TO_VERIFY));
        if (v.getCount() > 0) {
            final int uid = v.getColumnIndex(EmpContract.UID);
            final int usnFoto = v.getColumnIndex(EmpContract.USN_FIELD_FOTO);

            for (v.moveToFirst(); !v.isAfterLast(); v.moveToNext()) {
                Emp emp = new Emp(v);
                mListEmps.add(emp);
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                final String stringUUID = v.getString(uid);
                writer.write(stringUUID.equals(Emp.DEFAULT_UUID_EMP_S) ? Emp.DEFAULT_STRING_EMP : stringUUID);
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(v.getString(usnFoto));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));
        writer.write(getOpen(OBJECTS_TO_REMOVE));
        writer.write(getClose(OBJECTS_TO_REMOVE));
        v.close();
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {
/*
        final SwitchParseHandler<ProcessEmpFotoHandler.SimpleProcessEntity> handler = SwitchParseHandler.newInstance(inputStream);
        final ProcessEmpFotoHandler.SimpleProcessEntity entity = handler.getData();
        LTApplication mApp = (LTApplication) mContext.getApplicationContext();


        if (!entity.getListDownload().isEmpty()) {
            //качать фотки
            for (int i=0; i< entity.getListDownload().size()-1; i++) {
                for (Emp emp : mListEmps) {
                    if (emp.getUid().toString().equals(entity.getListDownload().get(i).equals(Emp.DEFAULT_STRING_EMP) ? Emp.DEFAULT_UUID_EMP_S : entity.getListDownload().get(i))) {
                        try {
                            try {
                                File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + emp.getLogin());
                                if (cacheImgFile.exists()) {
                                    cacheImgFile.delete();
                                }
                            } catch (Exception e) {

                            }
                            new DownloadFile(mContext, entity.getListDownload().get(i), emp.getLogin(), LTSettings.getInstance().getUserProfile(), mApp.getAppFolder(), 1).downloadFile();
                        }
                        catch (Exception e) {
                            
                        }
                        finally {
                            break;
                        }
                    }
                }
            }
        }

        if (!entity.getListNew().isEmpty()) {

        }

        if (!entity.getListSend().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            UploadFile uploadFile = new UploadFile(mContext, mApp.getSettings().getUserProfile(), mApp.getAppFolder(), sb, LTSettings.getInstance().getUserName(), entity.getListSend().get(0), LTSettings.getInstance().getUserName(), 0, 1);
            final Integer usn = uploadFile.uploadFile();

        }*/
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_EMPS_FOTOS;
    }
}