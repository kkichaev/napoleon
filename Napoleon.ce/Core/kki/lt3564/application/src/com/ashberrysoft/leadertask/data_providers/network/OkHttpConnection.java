package com.ashberrysoft.leadertask.data_providers.network;

import com.ashberrysoft.leadertask.application.Config;
import com.ashberrysoft.leadertask.application.LTSettings;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ashberrysoft.leadertask.instance_sync.LeaderTaskSyncService.client;
import static com.ashberrysoft.leadertask.interfaces.FileHandlerConstants.EMAIL_CREATOR;
import static com.ashberrysoft.leadertask.interfaces.FileHandlerConstants.METHOD;
import static com.ashberrysoft.leadertask.interfaces.FileHandlerConstants.NAME;
import static com.ashberrysoft.leadertask.interfaces.FileHandlerConstants.PASSWORD;

public class OkHttpConnection {

    private static final int TIMEOUT_DEFAULT = 240000;

    public static InputStream sendZipOkHttp (File requestZIP, String mMethodName) {
        int size = (int) requestZIP.length();
        byte[] bytes = new byte[size];
        try {
            InputStream buf = new FileInputStream(requestZIP);
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return PostWithOkHttp(mMethodName, bytes, true);
        }
    }

    public static InputStream PostWithOkHttp(String METHOD_NAME, byte[] bodyByte, boolean zip ) {
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
                //return null;
            }
        };

        SSLSocketFactory sslSocketFactory = null;
        try {
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();


            } catch (Exception e) {

            }
        //
        OkHttpClient client = new OkHttpClient.Builder()
                /*.connectTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)*/
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .followSslRedirects(true)
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        InputStream inputStream = null;
        Request request = null;
        try {
            MediaType SOAP = MediaType.parse("text/xml; charset=utf-8");
            //MediaType SOAP = MediaType.parse("application/octet-stream; charset=utf-8");


            String url = "";
            if (LTSettings.getInstance().getSyncPort() == 0) {
                url = LTSettings.getInstance().getSyncUri() + METHOD_NAME;
            } else {
                url = LTSettings.getInstance().getSyncNamespaceToEdit()+ "LeaderTaskSyncService.asmx?op="+ METHOD_NAME;
            }

            RequestBody body = RequestBody.create(SOAP, bodyByte);
            if (!zip) {
                request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "text/xml; charset=utf-8")
                        .addHeader("SOAPAction", LTSettings.getInstance().getSyncNamespace() + METHOD_NAME)
                        .post(body)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .addHeader("Content-Type", "text/xml; charset=utf-8")
                        .addHeader("SOAPAction",(LTSettings.getInstance().getSyncNamespace().equals(Config.SOAP_NAMESPACE_DEFAULT) ? Config.SOAP_NAMESPACE_DEFAULT : Config.LT_SYNC_SERVICE+"/")+ METHOD_NAME)
                        .addHeader("Content-Encoding", "gzip")
                        .addHeader("Accept-Encoding", "gzip")
                        .post(body)
                        .build();
            }
            Response response = client.newCall(request).execute();
            inputStream = response.body().byteStream();
            return inputStream;
        } catch (Exception e) {
            try {
                Response response = client.newCall(request).execute();
                inputStream = response.body().byteStream();
                return inputStream;
            } catch (Exception ex) {

            }
        }
        return inputStream;
    }

    public static String postWithParams(List<NameValuePair> nameValuePairs, String url) {
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
                //return null;
            }
        };

        SSLSocketFactory sslSocketFactory = null;
        try {
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();


        } catch (Exception e) {

        }
        //
        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
//                .writeTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
//                .readTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .followSslRedirects(true)
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        Request request = null;
        try {

            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (NameValuePair valuePair : nameValuePairs) {
                builder.addFormDataPart(valuePair.getName(), valuePair.getValue());
            }

            RequestBody requestBody = builder.build();

            request = new Request.Builder()
                    .addHeader("Content-Type", " application/x-www-form-urlencoded")
                    .url(url)
                    .method("POST", RequestBody.create(null, new byte[0]))
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {

        }
        return "";
    }

    public static Response uploadFile(String url, File file, List<NameValuePair> nameValuePairs){
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
                //return null;
            }
        };

        SSLSocketFactory sslSocketFactory = null;
        try {
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();


        } catch (Exception e) {

        }
        //
        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
//                .writeTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
//                .readTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .followSslRedirects(true)
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();


        Response response = null;
        try {

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM)
            .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("text/plain"), file));


            for (NameValuePair valuePair : nameValuePairs) {
                builder.addFormDataPart(valuePair.getName(), valuePair.getValue());
            }

            RequestBody formBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();
            ;
            //inputStream = response.body().byteStream();

            return response = client.newCall(request).execute();
        } catch (Exception e) {

        }
        return response;
    }

    public static Response downloadFile(String url, List<NameValuePair> nameValuePairs){
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    chain[0].checkValidity();
                } catch (Exception e) {
                    throw new CertificateException("Certificate not valid or trusted.");
                }
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
                //return null;
            }
        };

        SSLSocketFactory sslSocketFactory = null;
        try {
            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            sslSocketFactory = sslContext.getSocketFactory();


        } catch (Exception e) {

        }
        //
        OkHttpClient client = new OkHttpClient.Builder()
//                .connectTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
//                .writeTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
//                .readTimeout(TIMEOUT_DEFAULT, TimeUnit.MILLISECONDS)
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .followSslRedirects(true)
                .sslSocketFactory(sslSocketFactory)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();

        Response response = null;

        try {
            //
            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (NameValuePair valuePair : nameValuePairs) {
                builder.addFormDataPart(valuePair.getName(), valuePair.getValue());
            }

            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .addHeader("Content-Type", " application/x-www-form-urlencoded")
                    .url(url)
                    .method("POST", RequestBody.create(null, new byte[0]))
                    .post(requestBody)
                    .build();

            response = client.newCall(request).execute();

            return response;
        } catch (Exception e) {

        }
        return response;
    }
}
