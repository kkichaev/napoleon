package com.ashberrysoft.leadertask.data_providers.network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.database.Cursor;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.interfaces.FileHandlerConstants;
import com.ashberrysoft.leadertask.utils.CursorySyncLogger;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.xml_handlers.SwitchParseHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutFileHandler.SimplePutFilesEntity;

import static android.R.id.input;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class UploadFile implements FileHandlerConstants {

    public static final String METHOD_TYPE = "upload";
    private ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

    // VALUE'
    private LeaderTaskUser mUser;
    private File mAppFolder;
    private StringBuilder mEntityBody;
    private String mFileName;
    private String mFileUid;
    private String mFileEmailCreator;
    private int mFileVersion;
    private final String mUriString;
    private final String mUIDString;
    private final int mType;

    public UploadFile (Context context, LeaderTaskUser user, File appFolder, StringBuilder sb, String fileName, String fileIid, String emailCreator, int fileVersion, int type) {
        mUser = user;
        mAppFolder = appFolder;

        mFileName = fileName;
        mFileUid = fileIid;
        mFileEmailCreator = emailCreator;
        mFileVersion = fileVersion;
        mEntityBody = sb;
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

    public Integer uploadFile() throws Exception {
        final File file = new File(mAppFolder, mFileName);
        if (!file.exists()) {
            return null;
        }

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(NAME, mUser.getName()));
        nameValuePairs.add(new BasicNameValuePair(PASSWORD, mUser.getPassword()));
        nameValuePairs.add(new BasicNameValuePair(METHOD, METHOD_TYPE));
        nameValuePairs.add(new BasicNameValuePair(mUIDString, mFileUid));
        nameValuePairs.add(new BasicNameValuePair(EMAIL_CREATOR, mFileEmailCreator));
        if (mType == 0) {
            nameValuePairs.add(new BasicNameValuePair(FILE_USN_ENTITY, String.valueOf(mFileVersion)));
        }

        String str = OkHttpConnection.uploadFile(mUriString, file, nameValuePairs).body().string();

        /*if (input == null) {
            throw new NullPointerException();
        }*/

        /*final BufferedInputStream buffer = new BufferedInputStream(input);

        //final Reader reader = new InputStreamReader(input);
        final Reader reader =  new BufferedReader(new InputStreamReader(buffer));

        final SwitchParseHandler<SimplePutFilesEntity> handler = SwitchParseHandler.newInstance(reader);*/
        //input.close();
        //reader.close();

        int usn = 0; // TODO: 07.09.2017  ебаный костыль
        try {
            usn = Integer.parseInt(str.substring(str.indexOf("<__usn_entity>") + "<__usn_entity>".length(), str.indexOf("<__usn_entity>") + "<__usn_entity>".length() + 1));
        } catch (Exception e) {

        }

        wirteTag(TaskFileContract.FIELD_USN_ENTITY);
        writeValue(String.valueOf(usn));

        return usn;
    }

    private InputStream fixErrorsInXml(InputStream is) {
        String stringInput = getStringFromInputStream(is);
        String fixedStringInput = searchErrorsInXml(stringInput);
        return new ByteArrayInputStream(fixedStringInput.getBytes());
    }

    private String searchErrorsInXml(String oldString) {

        String findStrOpen = "<soap:Envelope";
        String findStrClose = "</soap:Envelope";
        int lastIndex = 0;
        int lastIndexClose = 0;
        int count = 0;
        int countClose = 0;

        while(lastIndex != -1){
            lastIndex = oldString.indexOf(findStrOpen,lastIndex);
            if(lastIndex != -1){
                count ++;
                if(count >1) {
                    StringBuffer text = new StringBuffer(oldString);
                    int deleteIndex = oldString.indexOf(">", lastIndex);

                    text.replace(lastIndex, deleteIndex, "");
                    lastIndex -= deleteIndex - lastIndex;
                    oldString = text.toString();
                }
                else {
                    lastIndex += findStrOpen.length();
                }
            }
        }

        while(lastIndexClose != -1){
            lastIndexClose = oldString.indexOf(findStrClose,lastIndexClose);
            if(lastIndexClose != -1){
                countClose ++;
                if(countClose >1) {
                    StringBuffer text = new StringBuffer(oldString);
                    int deleteIndex = oldString.indexOf(">", lastIndexClose);

                    text.replace(lastIndexClose, deleteIndex, "");
                    lastIndexClose -= deleteIndex - lastIndexClose;
                    oldString = text.toString();
                }
                else {
                    lastIndexClose += findStrClose.length();
                }
            }
        }

        return oldString;

    }

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private String wirteTag(String s) {
        mEntityBody.append(s);
        mEntityBody.append(CursorySyncLogger.COLON);

        return s;
    }

    private String writeValue(String s) {
        mEntityBody.append(s);
        mEntityBody.append(SharedStrings.NEW_LINE_C);

        return s;
    }
}