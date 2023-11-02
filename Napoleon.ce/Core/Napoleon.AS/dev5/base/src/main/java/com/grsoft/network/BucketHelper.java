package com.grsoft.network;

import android.annotation.SuppressLint;
import android.util.Xml;

import com.grsoft.napoleon.util.Config;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class BucketHelper {

    public static class Result {
        public String error;
        public String url;
    }

    private static byte[] hmacSHA256(byte[] data, byte[] key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }

    private static byte[] getSignatureKey(String date) throws Exception {
        byte[] kSecret = ("AWS4" + ConnectionHelper.BUCKET_KEY).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = hmacSHA256(date.getBytes(StandardCharsets.UTF_8), kSecret);
        byte[] kRegion = hmacSHA256(ConnectionHelper.BUCKET_REGION.getBytes(StandardCharsets.UTF_8), kDate);
        byte[] kService = hmacSHA256("s3".getBytes(StandardCharsets.UTF_8), kRegion);
        byte[] kSigning = hmacSHA256("aws4_request".getBytes(StandardCharsets.UTF_8), kService);
        return kSigning;
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    static String getExtension(byte[] data) {
        switch (data[0]) {
            case (byte) 0x89:
                return ".png";
            case (byte) 0xFF:
                return ".jpeg";
            default:
                return ".dat";
        }
    }

    static String makeObjectKey(byte[] data, String objectId, Config config) {
        String ext = getExtension(data);

        return String.format("%s/%s/%s"
            ,hash(config.serverCode)
            ,config.uuid.replace("-", "")
            ,objectId + ext);
    }

    static String getContentType(String id) {
        if(id.endsWith(".jpeg")) return "image/jpeg";
        if(id.endsWith(".jpg")) return "image/jpeg";
        if(id.endsWith(".png")) return "image/png";

        return "application/octet-stream";
    }

    public static Result putToBucket(String fileName, String objectId, Config config) {
        Result res = new Result();
        File file = new File(fileName);
        int size = (int) file.length();
        byte[] data = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(data, 0, data.length);
            buf.close();

            String verb = "PUT";
            String objKey = makeObjectKey(data, objectId, config);
            String urlStr = makeSignedUrl(objKey, verb);

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", getContentType(fileName));
            conn.setDoInput(true);
            conn.setRequestMethod(verb);

            conn.getOutputStream().write(data);
            conn.getOutputStream().close();

            InputStream istr = conn.getResponseCode() < 300 ? conn.getInputStream() : conn.getErrorStream();
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while((n = istr.read(chunk)) > 0) {
                baso.write(chunk, 0, n);
            }
            if(conn.getResponseCode() < 300)
                res.url = "https://" + ConnectionHelper.BUCKET + "/" + objKey;
            else
                res.error = baso.toString(Xml.Encoding.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
            res.error = e.getLocalizedMessage();
        }

        return res;
    }

    private static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return toHexString(messageDigest);
        } catch (Exception e) {
            return toHexString(text.getBytes(StandardCharsets.UTF_8));
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String makeSignedUrl(String objectKey, String httpVerb) {
        String signedUrl = "";

        int expired = 3600;
        Date now = new Date();

        final String DATE_FORMAT = "yyyyMMdd'T'HHmmss'Z'";
        final String DATE_FORMAT_SHORT = "yyyyMMdd";


        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String amzDate = sdf.format(now);

        sdf = new SimpleDateFormat(DATE_FORMAT_SHORT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStamp = sdf.format(now);

        try {
            String credentialScope = dateStamp + ConnectionHelper.credentials();

            String canonicalQueryParams = "X-Amz-Algorithm=AWS4-HMAC-SHA256"
                    + "&X-Amz-Credential=" + URLEncoder.encode(ConnectionHelper.BUCKET_KEY_ID + "/" + credentialScope, "UTF-8")
                    + "&X-Amz-Date=" + URLEncoder.encode(amzDate, "UTF-8")
                    + "&X-Amz-Expires=" + Integer.toString(expired)
                    + "&X-Amz-SignedHeaders=host";

            String canonicalUri = "/" + ConnectionHelper.BUCKET + "/" + objectKey;
            String canonicalHeaders = "host:" + ConnectionHelper.BUCKET_HOST + "\n";

            String canonicalRequest =
                    httpVerb + "\n" +
                    canonicalUri + "\n" +
                    canonicalQueryParams + "\n" +
                    canonicalHeaders + "\n" +
                    "host\n" +
                    "UNSIGNED-PAYLOAD";

            String stringToSign =  "AWS4-HMAC-SHA256\n" + amzDate + "\n" + credentialScope + "\n" + hash(canonicalRequest);
            byte[] signingKey = getSignatureKey(dateStamp);
            String signature = toHexString(hmacSHA256(stringToSign.getBytes(StandardCharsets.UTF_8), signingKey));

            signedUrl = "https://" + ConnectionHelper.BUCKET_HOST + canonicalUri +
                    "?" + canonicalQueryParams + "&X-Amz-Signature=" + signature;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return signedUrl;
    }
}
