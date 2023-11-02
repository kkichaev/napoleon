package com.ashberrysoft.leadertask.data_providers.network;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPRequestConstants;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactFotoHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samsung on 24.11.2015.
 */
public class ProcessContactsFoto extends BaseTimeSOAP<Serializable> implements ProcessSOAPRequestConstants {

    private static final long serialVersionUID = 1L;
    protected static final String METHOD_NAME = "ProcessContactsFotos";
    private static List<Contact> mListContacts = new ArrayList<>();

    public ProcessContactsFoto(Context context, LeaderTaskUser user) {
        super(context, METHOD_NAME, user);
    }

    @Override
    protected void writeRequestSubXML(OutputStreamWriter writer) throws IOException {
        //
        try {
            mListContacts = DbHelper.getInstance(mContext).getContactDao().queryBuilder().orderBy(ContactContract.ORDERS, true).query();
        } catch (SQLException e) {
        }
        //

        writer.write(getOpen(OBJECTS_TO_VERIFY));
        DbHelper.getInstance(mContext).getContactDao();
        if (mListContacts.size() > 0) {
            for (Contact contact: mListContacts) {
                writer.write(getOpen(OBJ_CLIENT_TO_VERIFY));

                writer.write(getOpen(_STR_UID));
                writer.write(contact.getUid().toString());
                writer.write(getClose(_STR_UID));

                writer.write(getOpen(_USN_ENTITY));
                writer.write(String.valueOf(contact.getUsnFieldFoto()));
                writer.write(getClose(_USN_ENTITY));

                writer.write(getClose(OBJ_CLIENT_TO_VERIFY));
            }
        }
        writer.write(getClose(OBJECTS_TO_VERIFY));
        writer.write(getOpen(OBJECTS_TO_REMOVE));
        writer.write(getClose(OBJECTS_TO_REMOVE));
    }

    @Override
    protected Serializable parseResponse(Reader inputStream) throws Exception {

        final SwitchParseHandler<ProcessContactFotoHandler.SimpleProcessEntity> handler = SwitchParseHandler.newInstance(inputStream);
        final ProcessContactFotoHandler.SimpleProcessEntity entity = handler.getData();
        LTApplication mApp = (LTApplication) mContext.getApplicationContext();

        if (!entity.getListDownload().isEmpty()) {
            //качать фотки
            for (int i=0; i< entity.getListDownload().size()-1; i++) {
                for (Contact contact : mListContacts) {
                    if (contact.getUid().toString().equals(entity.getListDownload().get(i).equals(Emp.DEFAULT_STRING_EMP) ? Emp.DEFAULT_UUID_EMP_S : entity.getListDownload().get(i))) {
                        try {
                            try {
                                File cacheImgFile = new File(mApp.getAppFolder() + "/cache_" + contact.getUid().toString());
                                if (cacheImgFile.exists()) {
                                    cacheImgFile.delete();
                                }
                            } catch (Exception e) {

                            }
                            new DownloadFile(mContext, entity.getListDownload().get(i), contact.getUid().toString(), LTSettings.getInstance().getUserProfile(), mApp.getAppFolder(), 2).downloadFile();
                        }
                        catch (Exception e) {
                            int m = 0;
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

        }
        return null;
    }

    @Override
    public String getResultAction() {
        return ServiceConstants.ACTION_PROCESS_CONTACTS_FOTOS;
    }
}