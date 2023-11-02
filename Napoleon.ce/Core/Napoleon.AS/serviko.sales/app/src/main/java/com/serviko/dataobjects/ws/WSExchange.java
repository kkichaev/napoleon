package com.serviko.dataobjects.ws;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.util.Xml;

import com.serviko.dataobjects.OrderSend;
import com.serviko.dataobjects.xml.FieldReader;
import com.serviko.dataobjects.xml.FieldWriter;
import com.serviko.dataobjects.xml.WSDLElement;
import com.serviko.sales.R;
import com.serviko.sales.BuildConfig;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WSExchange {
    static final String TAG = WSExchange.class.toString();

    public static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    SOAPFault fault = null;

    String pwd = "tY2ni4ma";
    String login = "GRSOFT";

    static final String REF_URL;
    static final String BLOB_URL;

    //    static final String REF_URL = "https://tst.serviko.ru/distr_13/ws/ASFMobileTrade";
    // = "https://1c.serviko.ru/distr_original/ws/ASFMobileTrade/";

    static final String BLOB_ACTION = "ВызовСервера";
    static final String BLOB_SOAPACTION = "MobileTrade_Blob#MobileTrade_Blob:" + BLOB_ACTION;

    //    static final String BLOB_URL = "https://tst.serviko.ru/DISTR_13/ws/MobileTrade_Blob";
    // = "https://1c.serviko.ru/distr_original/ws/MobileTrade_Blob";

    static {
        if(BuildConfig.FLAVOR.equals("serverTest")) {
            BLOB_URL = "https://tst.serviko.ru/DISTR_13/ws/MobileTrade_Blob";
            REF_URL = "https://tst.serviko.ru/distr_13/ws/ASFMobileTrade";
        }  else {
            BLOB_URL = "https://1c.serviko.ru/distr_original/ws/MobileTrade_Blob";
            REF_URL = "https://1c.serviko.ru/distr_original/ws/ASFMobileTrade/";
        }
    }


    static  String FAULT_TAG = "Fault";

    Map<Class<?>, List<FieldWriter>> writers = new HashMap<Class<?>, List<FieldWriter>>();
    Map<Class<?>, Map<String, FieldReader>> readers = new HashMap<>();

    Context context;

    public interface Events {
        void complete(boolean result, Object response, WSExchange exchange);
        void error(Exception e);
    }

    Events handler;

    int answerResourceId = 0;

    public WSExchange(Context context) {
        this.context = context;
    }

    public void setHandler(Events handler) { this.handler = handler; }

    public void setAnswerResource(int id) { answerResourceId = id; }

    public SOAPFault error () { return fault; }

    public void reqCode(ReqCodeParam data) {
        BlobExchange(data, ReqCodeResult.class, "ПолучитьКодПодтверждения_v2");
//        DoExchange(new Object[] {data}, ReqCodeResult.class,
//                "ПолучитьКодПодтверждения_v2");
    }

    public void acceptCode(AcceptCodeParam data) {
        BlobExchange(data, AcceptCodeResult.class, "ПодтвердитьКодПодтверждения_v3");
//        DoExchange(new Object[] {data}, AcceptCodeResult.class,
//                "ПодтвердитьКодПодтверждения_v3");
    }

    public void reqPrice(ReqPriceParam data) {
        BlobExchange(data, ReqPriceResult.class, "ПолучитьНоменклатуру_v3");
//        DoExchange(new Object[] {data}, ReqPriceResult.class,
//                "ПолучитьНоменклатуру_v3");
    }

    String orderToString(String ns, OrderSend order) {
        StringWriter wr = new StringWriter();
        List<FieldWriter> fw = FieldWriter.createWriter(order.getClass(), wr);

        XmlSerializer s = Xml.newSerializer();
        String body = "";
        String value = "Значение";
        try {
            s.setOutput(wr);
            s.setPrefix(ns, "ASFMobileTrade");
            s.startTag("ASFMobileTrade", value);

            for(FieldWriter i : fw) {
                i.writeElement(s, "ASFMobileTrade", order);
            }

            s.endTag("ASFMobileTrade", value);
            s.flush();
            body = wr.toString().replaceAll("<(.*)Значение ([^>]*)>", "<$1Значение>");



        } catch(Exception e) {
            e.printStackTrace();
        }
        body = "@" + ns + ":МассивЗаказов@" + body;
        return body;
    }

    public void sendOrder(SendBasketParam data) {
        if(data.orders.size() == 0)
            return;

        List<String> params = makeBlobParams(data);

        params.remove(0);
        params.add(0, orderToString("ord", data.orders.get(0)));

        BlobExchange(params, SendBasketResult.class, "ОтправитьЗаказы");
//        DoExchange(new Object[] {data}, SendBasketResult.class,
//                "ASFMobileTrade#ASFMobileTrade:ОтправитьЗаказы",
//                "ОтправитьЗаказы", BLOB_URL);
    }

    public void reqOrders(ReqOrdersParam data) {
        BlobExchange(data, ReqOrdersResult.class, "ПолучитьЗаказы_v2");
//        DoExchange(new Object[] {data}, ReqOrdersResult.class,
//                "ПолучитьЗаказы_v2");
    }

    public void reqKupec(String orgId) {
        GetKupecRequest r = new GetKupecRequest();
        r.id = orgId;
        BlobExchange(r, GetKupecResponse.class, "ПолучитьПрайсЛистКупец");

//        DoExchange(new Object[]{r}, GetKupecResponse.class, "ПолучитьПрайсЛистКупец");
    }

//    void DoExchange(final Object[] params, final Class<?> resultType, final String methodName) {
//        DoExchange(params, resultType, "ASFMobileTrade#ASFMobileTrade:" + methodName, methodName, REF_URL);
//    }

    Map<String, Field> mapFields(Class<?> cl) {
        Map<String, Field> ret = new HashMap<>();
        for(Field f : cl.getFields()) {
            WSDLElement w = f.getAnnotation(WSDLElement.class);
            if(w != null) {
                ret.put(w.name(), f);
            }
        }
        return ret;
    }

    List<String> makeBlobParams(Object param) {
        List<String> ret = new ArrayList<>();

        Class<?> pt = param.getClass();
        Map<String, Field> fields = mapFields(pt);

        WSDLElement w = (WSDLElement) pt.getAnnotation(WSDLElement.class);
        if(w == null) {
            return ret;
        }

        for(String s : w.memberOrder().split(",")){
            Field f = fields.get(s);
            if( f != null ) {
                try {
                    Object val = f.get(param);
                    String sval = "";
                    if(val instanceof List) sval = "@m:МассивСтрок@";
                    else if(val != null) sval = val.toString();
                    ret.add(sval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    String makeBlobBody(String operation, List<String> params) {
        BLOBParam ret = new BLOBParam();
        BLOBBody body = new BLOBBody();
        ret.body = body;

        body.operation = operation;

        for(String p : params) {
            body.params.add(p);
        }

        return makeBody(BLOB_ACTION, new Object[] {ret}, "MobileTrade_Blob");
    }

    private Object decodeResult(BLOBResult result, Class<?> resultType) {
        Object res = null;
        try {
            byte[] body = Base64.decode(result.body, Base64.DEFAULT);
            File zipFile = File.createTempFile("upload", "zip", context.getCacheDir());
//            File zipFile = new File(context.getFilesDir(), "upload.zip");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(zipFile));
            bos.write(body);
            bos.close();

            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile)));
            ZipEntry ze;
            byte[] buffer = new byte[10240];
            int count;
            while((ze = zis.getNextEntry()) != null) {
                if(ze.isDirectory())
                    continue;


                File outFile = File.createTempFile(ze.getName(), null, context.getCacheDir());
//                File outFile = new File(context.getFilesDir(), ze.getName());
                FileOutputStream fos = new FileOutputStream(outFile);
                while((count = zis.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                zis.closeEntry();
                fos.close();

                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
                parser.setInput(new FileInputStream(outFile), "utf8");
                int evType = parser.getEventType();
                while(evType != XmlPullParser.END_DOCUMENT) {
                    if(evType == XmlPullParser.START_TAG) {
                        res = resultType.newInstance();
                        setObject(res, parser, "");
                        break;
                    }
                    evType = parser.next();
                }
            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    String streamToString(InputStream is) {
        String str = "";
        try {

            byte[] b = new byte[1000];
            int count;
            while((count = is.read(b)) > 0) {
                str += new String(b, 0, count, StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    String getError(HttpURLConnection conn) {
        return streamToString(conn.getErrorStream());
    }

    void BlobExchange(Object param, final Class<?> resultType, String operation) {
        List<String> params = makeBlobParams(param);
        BlobExchange(params, resultType, operation);
    }

    void BlobExchange(List<String> params, final Class<?> resultType, String operation) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    if(answerResourceId != 0) {
                        doTestAnswer(resultType);
                        return;
                    }

                    Log.d(TAG, "start blob exchange " + resultType.toString());

                    String body = makeBlobBody(operation, params);
                    byte[] bodyBytes = body.getBytes("UTF-8");

                    String auth = login + ":" + pwd;
                    String authHeaderValue = "Basic " + new String(Base64.encode(auth.getBytes("UTF-8"), Base64.DEFAULT));

                    URL url = new URL(BLOB_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("SOAPAction", BLOB_SOAPACTION);
                    conn.setRequestProperty("Authorization", authHeaderValue);

                    boolean done = false;
                    Object retObj = null;
                    try {
                        conn.setDoOutput(true);
                        conn.setChunkedStreamingMode(0);
                        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                        out.write(bodyBytes);
                        out.flush();

//                        String res = streamToString(conn.getInputStream());
//                        InputStream in = new ByteArrayInputStream(res.getBytes());
                        InputStream in = new BufferedInputStream(conn.getInputStream());
                        BLOBResult result = (BLOBResult) parseResult(in, BLOBResult.class);
                        if(result != null) {
                            retObj = decodeResult(result, resultType);
                            done = retObj != null;
                        } else {
                            retObj = fault;
                        }
                    } catch(Exception e) {
                        String str = getError(conn);
                        if(str.length() == 0)
                        {
                            str = conn.getResponseMessage();
                        }
                        Log.d(TAG, str);

                        fault = (SOAPFault) parseResult(new ByteArrayInputStream(str.getBytes()), SOAPFault.class);
                        if(fault == null) {
                            fault = new SOAPFault();
//                            String erres = conn.getResponseMessage();
                            fault.message = context.getString(R.string.server_fault);
                        }
                        retObj = fault;
                        e.printStackTrace();
//                            handler.error(e);
                    } finally {
                        conn.disconnect();
                    }

                    if(handler != null) {
                        handler.complete(done, retObj, WSExchange.this);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    handler.error(e);
                }
            }
        };
        t.start();
    }

    void doTestAnswer(Class<?> resultType) {
        try {
            InputStream ans = context.getResources().openRawResource(answerResourceId);
            Object out = parseResult(ans, resultType);
            boolean done = true;
            if(out == null) {
                done = false;
                out = fault;
            }
            if(handler != null) {
                handler.complete(done, out, WSExchange.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.error(e);
        }
    }

    void DoExchange(final Object[] params, final Class<?> resultType, final String actionName,
                    final String methodName, final String sUrl) {

            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        if(answerResourceId != 0) {
                            doTestAnswer(resultType);
                            return;
                        }

                        String body = makeBody(methodName, params, "MobileTrade");
                        byte[] bodyBytes = body.getBytes("UTF-8");

                        String auth = login + ":" + pwd;
                        String authHeaderValue = "Basic " + new String(Base64.encode(auth.getBytes("UTF-8"), Base64.DEFAULT));

                        URL url = new URL(sUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestProperty("SOAPAction", actionName);
                        conn.setRequestProperty("Authorization", authHeaderValue);

                        boolean done = false;
                        Object retObj = null;
                        try {
                            conn.setDoOutput(true);
                            conn.setChunkedStreamingMode(0);
//                            conn.setFixedLengthStreamingMode(bodyBytes.length);
                            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                            out.write(bodyBytes);
                            out.flush();

                            InputStream in = new BufferedInputStream(conn.getInputStream());
//                            int rc = conn.getResponseCode();
                            Object result = parseResult(in, resultType);
                            if(result != null) {
                                done = true;
                                retObj = result;
                            } else {
                                retObj = fault;
                            }
                        } catch(Exception e) {
                            String str = getError(conn);
                            fault = (SOAPFault) parseResult(new ByteArrayInputStream(str.getBytes()), SOAPFault.class);
                            if(fault == null) {
                                fault = new SOAPFault();
                                fault.message = context.getString(R.string.server_fault);
                            }
                            retObj = fault;
                            e.printStackTrace();
//                            handler.error(e);
                        } finally {
                            conn.disconnect();
                        }
                        if(handler != null) {
                            handler.complete(done, retObj, WSExchange.this);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                        handler.error(e);
                    }
                }
            };
            t.start();
    }

    void DoExchange1(final Object[] params, final Class<?> resultType, final String actionName, final String methodName) {

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    if(answerResourceId != 0) {
                        doTestAnswer(resultType);
                        return;
                    }
                    String body = makeBody(methodName, params, "MobileTrade");
                    byte[] bodyBytes = body.getBytes("UTF-8");

                    String auth = login + ":" + pwd;
                    String authHeaderValue = "Basic " + new String(Base64.encode(auth.getBytes("UTF-8"), Base64.DEFAULT));

                    HttpClient cli = HttpClients.createDefault();
                    HttpPost post = new HttpPost(REF_URL);
                    post.setHeader("Authorization", authHeaderValue);
                    post.setHeader("SOAPAction", actionName);
                    post.setEntity(new ByteArrayEntity(bodyBytes, ContentType.APPLICATION_XML));

                    boolean done = false;
                    Object retObj = null;
                    CloseableHttpResponse resp = (CloseableHttpResponse) cli.execute(post);
                    if(resp.getCode() < 300 ) {
                        HttpEntity re = resp.getEntity();
                        Object result = parseResult(re.getContent(), resultType);
                        if(result != null) {
                            done = true;
                            retObj = result;
                        } else {
                            retObj = fault;
                        }
                    } else {
                        fault = new SOAPFault();
                        String erres = EntityUtils.toString(resp.getEntity());
                        if(erres.contains("http://schemas.xmlsoap.org/soap/envelope")) {
                            fault = (SOAPFault) parseResult(new ByteArrayInputStream(erres.getBytes()), SOAPFault.class);
                        } else {
                            fault.message = context.getString(R.string.server_fault);
                        }
                        retObj = fault;
                    }
                    if(handler != null) {
                        handler.complete(done, retObj, WSExchange.this);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    handler.error(e);
                }
            }
        };
        t.start();
    }

    void setObject(Object o, XmlPullParser parser, String endTag) {
        try {
            Class<?> c = o.getClass();
            Map<String, FieldReader> rdr = readers.get(c);
            if(rdr == null) {
                rdr = FieldReader.createReader(c);
                readers.put(c, rdr);
            }

            while(true) {
                int evType = parser.next();
                if (evType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    FieldReader fr = rdr.get(name);
                    if(fr != null) {
                        fr.read(parser, o);
                    }
                } else if (evType == XmlPullParser.END_DOCUMENT) {
                    break;
                } else if (evType == XmlPullParser.END_TAG) {
                    if(endTag.equals(parser.getName()))
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Object makeResult(XmlPullParser parser, Class<?> resultType) {
        Object res = null;
        try {
            WSDLElement w = (WSDLElement) resultType.getAnnotation(WSDLElement.class);
            if(w != null) {
                while(true) {
                    int evType = parser.next();
                    if(evType == XmlPullParser.START_TAG) {
                        String name = parser.getName();
                        if(name.equals(w.name())) {
                            Object tres = resultType.newInstance();
                            setObject(tres, parser, w.name());
                            res = tres;
                        } else if(FAULT_TAG.equals(name)) {
                            setObject(fault, parser, FAULT_TAG);
                        }
                    } else if(evType == XmlPullParser.END_DOCUMENT)
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private Object parseResult(InputStream in, Class<?> resultType) throws XmlPullParserException, IOException {
        Object res = null;

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(in, null);
        int evType = parser.getEventType();
        while(evType != XmlPullParser.END_DOCUMENT) {
            if(evType == XmlPullParser.START_TAG && parser.getName().equals("Body")) {
                res = makeResult(parser, resultType);
                break;
            }
            evType = parser.next();
        }
        return res;
    }

    private String  makeBody(String methodName, Object[] params, String namespace) {
        String body = "";
        XmlSerializer s = Xml.newSerializer();
        StringWriter wr = new StringWriter();
        try {
            s.setOutput(wr);
            s.startDocument("UTF-8", false);

            String envns = "http://schemas.xmlsoap.org/soap/envelope/";

            s.setPrefix ("soapenv", envns);
            s.setPrefix("m", namespace);
            s.setPrefix("xsi", XSI_NS);
            s.startTag(envns, "Envelope")
                .startTag(envns, "Header")
                .endTag(envns, "Header")
                .startTag(envns, "Body")
                .startTag(namespace, methodName);

            for(Object o : params) {
                objToParam(s, namespace, o, wr);
            }

            s.endTag(namespace, methodName)
                .endTag(envns, "Body")
                .endTag(envns, "Envelope")
                .endDocument();

            body = wr.toString();


        } catch(Exception e) {
            e.printStackTrace();
        }

        return body;
    }

    private void objToParam(XmlSerializer s, String namespace, Object o, Writer out) {
        Class<?> c = o.getClass();
        List<FieldWriter> wrs = writers.get(c);

        if(wrs == null) {
            wrs = FieldWriter.createWriter(c, out);
            writers.put(c, wrs);
        }

        for(FieldWriter fw : wrs) {
            fw.writeElement(s, namespace, o);
        }
    }

    public static ErrResult checkError(boolean result, Object response) {
        ErrResult res = null;
        if(!result) {
            res = new ErrResult();
            res.error = ((SOAPFault) response).message;
            res.result = false;
        } else {
            if(response instanceof ErrResult && !((ErrResult) response).result)
                res = (ErrResult) response;
        }

        return res;
    }
}
