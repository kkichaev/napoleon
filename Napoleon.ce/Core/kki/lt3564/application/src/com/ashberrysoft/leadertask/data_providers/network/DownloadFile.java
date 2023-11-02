package com.ashberrysoft.leadertask.data_providers.network;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.ResponseContent;

import android.content.ContentValues;
import android.content.Context;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.interfaces.FileHandlerConstants;
import com.ashberrysoft.leadertask.utils.Utils.FileWorker;

import okhttp3.Headers;
import okhttp3.Response;

import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.client;

/**
 *
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class DownloadFile implements FileHandlerConstants {

    private static final String METHOD_TYPE = "download";
    private ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

    // BASE's
    private final Context mContext;
    private final String mUID;
    private final String mFileName;
    private final LeaderTaskUser mUser;
    private final File mAppFolder;
    private final String mUriString;
    private final String mUIDString;
    private final int mType;

    public DownloadFile(Context context, String uid, String fileName, LeaderTaskUser user, File appFolder, int type) {
        mContext = context;
        mUID = uid;
        mFileName = fileName;
        mUser = user;
        mAppFolder = appFolder;
        mType = type;
        if(mType != 0) { // фото
            mUriString = POST_URI_EMP_FOTO;
            mUIDString = FILE_UID_EMP_FOTO;
        }
        else { // файлы к задачам
            mUriString = POST_URI;
            mUIDString = FILE_UID;
        }
    }

    public void downloadFile() throws LeaderTaskException {
        //

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(NAME, mUser.getName()));
        nameValuePairs.add(new BasicNameValuePair(PASSWORD, mUser.getPassword()));
        nameValuePairs.add(new BasicNameValuePair(METHOD, METHOD_TYPE));
        nameValuePairs.add(new BasicNameValuePair(mUIDString, mUID));


        Response response = null;
        try {
            response = OkHttpConnection.downloadFile(mUriString, nameValuePairs);
        } catch (Exception e) {
            throw new LeaderTaskException(ErrorType.error_serv, mContext, LeaderTaskException.ERROR_INTERNET_ACCESS, e);

        }


        final int statusCode = response.code();
        if (statusCode != 200) {
            throw new LeaderTaskException(statusCode == 519 ? ErrorType.FILE_NOT_DOWNLOADED : ErrorType.error_serv,
                    mContext, statusCode, null);
        }

        final InputStream input;
        try {
            input = response.body().byteStream();

        } catch (Exception e) {
            throw new LeaderTaskException(ErrorType.error_serv, mContext, LeaderTaskException.ERROR_INTERNET_ACCESS, e);
        }


        final boolean fileExist;
        try {
            fileExist = FileWorker.makeFile(new File(mAppFolder, mFileName), input);

        } catch (IOException e) {
            throw new LeaderTaskException(ErrorType.UNKNOWN_TYPE_ERROR, mContext, 0, e);
        }

        if (fileExist) {
            if(mType == 0) { // качаем файл к задаче
                final ContentValues cv = new ContentValues(1);
                cv.put(TaskFileContract.FILE_EXIST, 1);

                mContext.getContentResolver().update(TaskFileContract.CONTENT_URI, cv, TaskFileContract.selectionFieldFileUid(mUID), null);
            }
            else {
                if(mType == 1) { // качаем фото сотрудника
                    //USN_FIELD_FOTO обновить
                    final ContentValues cv = new ContentValues(1);
                    cv.put(EmpContract.USN_FIELD_FOTO, response.headers().get(FOTO_USN_ENTITY));
                    mContext.getContentResolver().update(EmpContract.CONTENT_URI, cv, EmpContract.selectionLogin(mFileName), null);


                }
                else { // фото для контактов
                    //USN_FIELD_FOTO обновить
                    final List<UUID> uuids = new ArrayList<UUID>();
                    uuids.add(UUID.fromString(mFileName));
                    try {
                        final List<Contact> contacts = DbHelper.getInstance(mContext).getContactDao().queryBuilder().where().in(ContactContract.UID, uuids).query();
                        contacts.get(0).setUsnFieldFoto(Long.parseLong(response.headers().get(FOTO_USN_ENTITY), 10));
                        DbHelper.getInstance(mContext).updateContactsUsnFoto(contacts);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        } else {
            throw new LeaderTaskException(ErrorType.UNKNOWN_TYPE_ERROR, mContext, 0, null);
        }
    }
}