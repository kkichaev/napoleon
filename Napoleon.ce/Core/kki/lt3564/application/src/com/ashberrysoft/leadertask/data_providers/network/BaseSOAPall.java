package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.Config;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.network.LeaderTaskException.ErrorType;
import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.service.LeaderTaskService;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.CursorySyncLogger;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;
import com.v2soft.AndLib.dataproviders.AbstractHTTPServiceRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

/**
 * Базовый сетевой метод без kSOAP. BaseNokSOAPMethod
 *
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * @author Vadim Oleynik <vadim.welldone@gmail.com>
 *
 */
public abstract class BaseSOAPall<R extends Serializable> extends AbstractHTTPServiceRequest<R, Void, R> {

    private static final long serialVersionUID = 1L;

    private static final String RESPONSE_FILE_POSTFIX = ".response.xml";
    private static final String REQUEST_FILE_POSTFIX = ".request.xml";
    private static final String ENOSPC = "ENOSPC";
    private static final String no_space = "No space left on device";

    // VALUE's
    protected LeaderTaskUser mUser;
    protected String mMethodName;
    protected int mErrorCode;
    protected String mErrorMessage;
    protected boolean mSuccess;
    protected long mRequestStart;
    private static boolean sIsSynchronize;

    public BaseSOAPall(Context context, String methodName, LeaderTaskUser user) {
        super(context);

        mMethodName = methodName;
        mUser = user;
    }

    @Override
    protected R sendRequest(Void request) throws LeaderTaskException {
        try {
            mRequestStart = System.currentTimeMillis();
            // process request
            final File outputFile = getLogFile();
            final OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile));
            final OutputStreamWriter writer = new OutputStreamWriter(out, SharedStrings.UTF_8);

            writeSOAPRequest(writer);

            writer.close();
            out.close();
            final File requestZIP = pressFileZIP();

            // process response
            final InputStream inputStream = OkHttpConnection.sendZipOkHttp(requestZIP, mMethodName);
            if (inputStream == null) {
                throw new NullPointerException();
            }

            final BufferedInputStream buffer = new BufferedInputStream(inputStream);

            InputStream newStr = saveResponse(buffer);
            unpressFileZIP();
            final LTApplication app = (LTApplication) mContext.getApplicationContext();
            final File file = new File(app.getAppFolderLogs(), mMethodName + RESPONSE_FILE_POSTFIX);
            final Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), SharedStrings.UTF_8));
            inputStream.close();

            final R result = parseResponse(reader);
            reader.close();

            return result;

        } catch (Exception e) {
            onErrorParseResponse(e);
            Utils.toLog(e);

            // if (mMethodName.equals(VerifyUser.METHOD_NAME)) {
            // return null;
            // }

            if (e instanceof LeaderException) {
                throw (LeaderException) e;
            }

            else if (e instanceof NullPointerException) {
                throw new LeaderTaskException(ErrorType.error_serv, mContext, LeaderTaskException.ERROR_WRONG_SERVER, e);
            }

            else if (e instanceof SSLHandshakeException) {
                if (!mMethodName.equals(VerifyUser.METHOD_NAME)) {
                    throw new LeaderTaskException(ErrorType.SSL_HANDSHAKE_ERROR, mContext, e.getMessage(), e);
                } else {
                    Intent intent = new Intent(ServiceConstants.ACTION_SSL_HANDSHAKE_ERROR);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                    return null;
                }
            }

            else if(e instanceof ClientProtocolException
                    || e instanceof NoHttpResponseException) {
                throw new LeaderTaskException(ErrorType.error_serv, mContext, LeaderTaskException.ERROR_WRONG_SERVER, e);
            }

            else if (e instanceof UnknownHostException
                    || e instanceof SSLException
                    || e instanceof SocketException
                    || e instanceof ConnectTimeoutException
                    || e instanceof SocketTimeoutException) {
                throw new LeaderTaskException(ErrorType.error_serv, mContext, LeaderTaskException.ERROR_INTERNET_ACCESS, e);
            }

            else if(isNoSpaceLeftOnDevice(e)) {
                throw new LeaderTaskException(ErrorType.XML_PARSE_ERROR, mContext, LeaderTaskException.ERROR_NO_SPACE_ON_DEVICE, e);
            }

            else if (e instanceof XmlPullParserException || e instanceof IOException) {
                throw new LeaderTaskException(ErrorType.XML_PARSE_ERROR, mContext, e.getMessage(), e);
            }

            else if (e instanceof LeaderTaskException && ((LeaderTaskException) e).getCode() != 0) {
                throw new LeaderTaskException(ErrorType.error_serv, mContext, ((LeaderTaskException) e).getCode(), e);
            }

            else if (e instanceof LeaderTaskException) {
                throw (LeaderTaskException) e;
            }

            else {
                throw new LeaderTaskException(ErrorType.UNKNOWN_TYPE_ERROR, mContext, 0, e);
            }
        } finally {
            postParseResponse();
        }
    }


    protected void preParseResponse() {}

    protected void onErrorParseResponse(Throwable e) {}

    protected void postParseResponse() {}

    protected abstract R parseResponse(Reader inputStream) throws Exception;

    protected abstract void writeRequestSubXML(OutputStreamWriter writer) throws Exception;

    protected abstract void writeClearSessionChanges(OutputStreamWriter writer) throws Exception;
    public static final String XML_HEADER_1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
    public static final String XML_HEADER_2 = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
    public static final String XML_BODY = "soap:Body";
    public final String XML_SYNC_URL = " xmlns=\""+(LTSettings.getInstance().getSyncNamespace().equals(Config.SOAP_NAMESPACE_DEFAULT) ? LTSettings.getInstance().getSyncNamespace() : Config.LT_SYNC_SERVICE)+"\"";
    public static final String XML_NAME = "name";
    public static final String XML_PASSWORD = "password";
    public static final String XML_UID_SESSION = "str_uid_session";
    public static final String XML_ENVELOPE = "soap:Envelope";

    private void writeSOAPRequest(OutputStreamWriter writer) throws Exception {
        writer.write(XML_HEADER_1);
        writer.write(XML_HEADER_2);
        writer.write(getOpen(XML_BODY));
        writer.write(getOpen(mMethodName + XML_SYNC_URL));

        writeRequestSubXML(writer);

        if ((mMethodName.equals("GetSessionChanges") || mMethodName.equals("ProcessAll") || mMethodName.equals("PutAll")) && LTSettings.getInstance().getSessionUUID() != null) {
            writer.write(getValueLine(XML_UID_SESSION,LTSettings.getInstance().getSessionUUID()));
        } else {
            if (mUser != null && mUser.isValid()) {
                writer.write(getValueLine(XML_NAME, Utils.escapeCharacter(mUser.getName())));
                writer.write(getValueLine(XML_PASSWORD, Utils.escapeCharacter(mUser.getPassword())));
            }
        }

        writer.write(getClose(mMethodName));
        writer.write(getClose(XML_BODY));
        writer.write(getClose(XML_ENVELOPE));
    }

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected String getContentType() {
        return "text/xml; charset=utf-8";
    }

    @Override
    protected boolean shouldLogInteractionToLogCat() {
        return false;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public boolean isSuccessfull() {
        return mSuccess;
    }

    @Override
    protected String getServiceAction() {
        return ServiceConstants.RECIVE;
    }

    @Override
    protected Class<?> getServiceClass() {
        return LeaderTaskService.class;
    }

    private InputStream saveResponse(InputStream input) {
        return saveResponse(mContext, input, mMethodName);
    }

    public InputStream saveResponse(Context context, InputStream input, String methodName) {
        if (context == null) {
            CursorySyncLogger.getInstance(null).toLog("saveResponse : context == null");
            return null;
        }

        final LTApplication app = (LTApplication) context.getApplicationContext();
        //File log = new File(app.getAppFolderLogs(), methodName + RESPONSE_FILE_POSTFIX);
        final File logZIP = new File(app.getAppFolderZips(), methodName + RESPONSE_FILE_POSTFIX);
        final byte[] buffer = new byte[8192];
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(logZIP));

            int read = 0;
            while ((read = input.read(buffer)) > 0) {
                output.write(buffer, 0, read);
            }

            //return new FileInputStream(unpressFileZIP(log));
            //log = unpressFileZIP(logZIP);
            return new FileInputStream(logZIP);
        } catch (Exception e) {
            CursorySyncLogger.getInstance(app).toLog(e);

            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {}
            }

            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {}
            }
        }
    }

    @Override
    protected Void prepareParameters() throws AbstractDataRequestException {
        return null;
    }

    @Override
    protected R parseResult(R data) throws AbstractDataRequestException {
        return data;
    }

    private File getLogFile() {
        final LTApplication app = (LTApplication) mContext.getApplicationContext();
        /*
         * create file with particular name and OutputStream instance for this file
         */
        if (mContext != null) {
            return new File(app.getAppFolderLogs(), mMethodName + REQUEST_FILE_POSTFIX);
        }
        return null;
    }

    private static final String FORMAT_OPEN = "<";
    private static final String FORMAT_CLOSE = ">";
    private static final String FORMAT_OPEN_CLOSE = "</";
    private static final String FORMAT_CLOSE_XMLNS = " xmlns=\"\">";
    private static final String FORMAT_CLOSE_OPEN_XMLNS = " xmlns=\"\"/>";
    private static final String BEGIN_TERM_DATE = "01.01.1900 00:00:00";
    private static final String END_TERM_DATE = "01.01.9000 23:59:59";

    public static String getOpen(String value) {
        return getOpen(null, value);
    }

    public static String getOpen(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN);
        sb.append(value);
        sb.append(FORMAT_CLOSE);

        return sb.toString();
    }

    public static String getClose(String value) {
        return getClose(null, value);
    }

    public static String getClose(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN_CLOSE);
        sb.append(value);
        sb.append(FORMAT_CLOSE);

        return sb.toString();
    }

    private static String getOpenXmlns(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN);
        sb.append(value);
        sb.append(FORMAT_CLOSE_XMLNS);

        return sb.toString();
    }

    private static String getOpenCloseXmlns(StringBuilder sb, String value) {
        if (sb == null) {
            sb = new StringBuilder();
        }

        sb.append(FORMAT_OPEN);
        sb.append(value);
        sb.append(FORMAT_CLOSE_OPEN_XMLNS);

        return sb.toString();
    }

    private static String createValueLine(String tag, String value) {
        final StringBuilder sb = new StringBuilder();

        getOpen(sb, tag);
        sb.append(value);
        getClose(sb, tag);

        return sb.toString();
    }

    public static String getValueLine(String tag, String value) {
        return createValueLine(tag, value);
    }

    private static String createXmlnsValueLine(String tag, String value) {
        final StringBuilder sb = new StringBuilder();

        getOpenXmlns(sb, tag);
        sb.append(value);
        getClose(sb, tag);

        return sb.toString();
    }

    public static String createXmlnsNoValueLine(String tag) {
        return getOpenCloseXmlns(null, tag);
    }

    public static String getXmlnsValueLine(String tag, String value) {
        if (TextUtils.isEmpty(value)) {
            return createXmlnsNoValueLine(tag);
        }
        return createXmlnsValueLine(tag, StringEscapeUtils.escapeXml(value));
    }

    public static String getXmlnsValueLine(String tag, int value) {
        return createXmlnsValueLine(tag, String.valueOf(value));
    }

    public static String getXmlnsValueLine(String tag, boolean value) {
        return createXmlnsValueLine(tag, value ? SharedStrings.ONE : SharedStrings.ZERO);
    }

    public static String getXmlnsValueLine(String tag, long value) {
        return createXmlnsValueLine(tag, String.valueOf(value));
    }

    public static String getXmlnsValueLine(String tag, UUID value) {
        if (value == null) {
            return createXmlnsNoValueLine(tag);
        }
        return createXmlnsValueLine(tag, value.toString());
    }

    public static String getXmlnsValueLine(String tag, Date value, boolean beginTerm) {
        return createXmlnsValueLine(tag, value != null ? Task.SDF.format(value) : //
                beginTerm ? BEGIN_TERM_DATE : END_TERM_DATE);
    }

    public static String getXmlnsValueLine(String tag, Date value) {
        return createXmlnsValueLine(tag, value == null ? END_TERM_DATE : Task.SDF.format(value));
    }

    public static boolean equalsOne(String value) {
        return SharedStrings.ONE.equals(value);
    }

    public static boolean equalsOne(int value) {
        return value == 1;
    }

    public static Date parseDate(String value) {
        switch (value) {
            case BEGIN_TERM_DATE:
            case END_TERM_DATE:
                return null;

            default:
                try {
                    final Date date = Task.SDF.parse(value);
                    return date;

                } catch (ParseException e) {
                    Utils.toLog(e);
                    return null;
                }
        }
    }

    public static List<UUID> convertStringsToUUIDs(List<String> strings) {
        final List<UUID> uuids = new ArrayList<UUID>(strings.size());
        for (String string : strings) {
            try {
                uuids.add(UUID.fromString(string));

            } catch (Exception e) {
                Utils.toLog(string);
            }
        }

        strings.clear();
        return uuids;
    }

    /**
     * Prepare XML pull parser.
     *
     * @param inputStream
     * @return
     * @throws XmlPullParserException
     */
    protected XmlPullParser prepareParser(Reader inputStream) throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream);
        return parser;
    }

    protected void checkCode(XmlPullParser parser, String label) throws IOException, XmlPullParserException, LeaderTaskException {
        parser.getEventType();
        parser.nextTag();
        parser.nextTag();
        parser.nextTag();
        String name = parser.getName();
        if (!name.equals(label)) {
            throw new XmlPullParserException(mContext.getResources().getString(
                    com.ashberrysoft.leadertask.R.string.error_wrong_answer_from_server_incorrect_root_container_title, new Object[] { label, name }));
        }
        parser.nextTag();
        parser.nextTag();
        name = parser.getName();
        if (!name.equals("error_code")) {
            throw new XmlPullParserException(mContext.getResources().getString(
                    com.ashberrysoft.leadertask.R.string.error_wrong_answer_from_server_absent_error_code_tag));
        }
        String code = parser.nextText();
        if (!code.equals("0")) {
            throw new LeaderTaskException(ErrorType.error_serv, mContext, Integer.parseInt(code), null);
        }
    }

    public static void setIsSynchronize(boolean isSynchronize) {
        sIsSynchronize = isSynchronize;
    }

    public static boolean isSynchronize() {
        return sIsSynchronize;
    }

    public static void clearStringBuilder(StringBuilder sb) {
        Utils.clearStringBuilder(sb);
    }

    private File pressFileZIP()
    {
        final LTApplication app = (LTApplication) mContext.getApplicationContext();
        final File fileZIP = new File(app.getAppFolderZips(), mMethodName + REQUEST_FILE_POSTFIX);
        final File file = new File(app.getAppFolderLogs(), mMethodName + REQUEST_FILE_POSTFIX);
        byte[] buffer = new byte[8192];
        try
        {
            GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(fileZIP));
            FileInputStream  in = new FileInputStream(file);
            int len;
            while ((len = in.read(buffer)) > 0)
            {
                gzos.write(buffer, 0, len);
            }
            in.close();
            gzos.finish();
            gzos.close();
        }
        catch(IOException ex)
        {
            CursorySyncLogger.getInstance(mContext).toLog(ex);
        }
        return fileZIP;
    }

    private void unpressFileZIP()
    {
        final LTApplication app = (LTApplication) mContext.getApplicationContext();
        final File file = new File(app.getAppFolderLogs(), mMethodName + RESPONSE_FILE_POSTFIX);
        final File fileZIP = new File(app.getAppFolderZips(), mMethodName + RESPONSE_FILE_POSTFIX);
        byte[] buffer = new byte[1024];
        try
        {
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(fileZIP)); // ошибка
            FileOutputStream out =  new FileOutputStream(file);
            int len;
            while ((len = gzis.read(buffer)) > 0)
            {
                out.write(buffer, 0, len);
            }
            gzis.close();
            out.close();
        }
        catch(IOException ex)
        {
            CursorySyncLogger.getInstance(mContext).toLog(ex);
            // если что-то упало то сделать файл который пришел уже разорхифированным файлом
            try {
                Utils.FileWorker.copyFile(fileZIP, file);
            } catch (Exception e) {
                CursorySyncLogger.getInstance(mContext).toLog(e);
            }
            //
        }
    }

    private boolean isNoSpaceLeftOnDevice (Exception e) {
        if(e instanceof IOException ) {
            String temp = e.getMessage();
            if (temp.indexOf(ENOSPC) != -1 || temp.indexOf(no_space) != -1) {
                return true;
            }
        }
        return false;
    }
}