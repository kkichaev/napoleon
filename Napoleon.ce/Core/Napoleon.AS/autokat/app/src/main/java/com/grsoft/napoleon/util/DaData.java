package com.grsoft.napoleon.util;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DaData {
    static final int MIN_LENGTH = 4;
    static final String API_KEY = "5ef69ebb9bec256e2b7cdf5c4dc481b475f4a805 ";
    static final String SECRET_KEY = "9c01389ed414f6976e7d96601d713588a64c256c";

    public interface Action{
        void run(String val);
    }

    public static List<String> getAddresses(String filter) {
        final List<String> res =  new ArrayList<>();
//        res.add(filter);
//        res.add(filter);

        if(filter.length() >= MIN_LENGTH) {
            Thread t = new Thread(() -> {
                try {
                    String url = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/address";
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Authorization", "Token " + API_KEY);
                    conn.setRequestMethod("POST");

                    conn.setDoOutput(true);
                    conn.setChunkedStreamingMode(0);
                    OutputStream out = conn.getOutputStream();
                    String query = String.format("{ \"query\": \"%s\" }", filter.trim());
                    out.write(query.getBytes("UTF-8"));

                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line;
                    String retSrc = "";
                    while ((line = reader.readLine()) != null) {
                        retSrc += line;
                    }
                    conn.disconnect();

                    JsonObject client = null;
                    JsonElement root = new JsonParser().parse(retSrc);
                    if (root.isJsonObject()) {
                        JsonElement el = root.getAsJsonObject().get("suggestions");
                        if (el != null && el.isJsonArray()) {
                            JsonArray data = el.getAsJsonArray();
                            for(JsonElement sel : data) {
                                if(sel.isJsonObject()) {
                                    JsonElement vel = sel.getAsJsonObject().get("value");
                                    if(vel != null) {
                                        res.add(vel.getAsString());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            try {
                t.start();
                t.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static void getIssueOrg(String filter, Action action) {
        final List<String> res =  new ArrayList<>();

        if(filter.length() == 7) {
            Thread t = new Thread(() -> {
                try {
                    String url = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/suggest/fms_unit";
                    URL u = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Authorization", "Token " + API_KEY);
                    conn.setRequestMethod("POST");

                    conn.setDoOutput(true);
                    conn.setChunkedStreamingMode(0);
                    OutputStream out = conn.getOutputStream();
                    String query = String.format("{ \"query\": \"%s\" }", filter.trim());
                    out.write(query.getBytes("UTF-8"));

                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line;
                    String retSrc = "";
                    while ((line = reader.readLine()) != null) {
                        retSrc += line;
                    }
                    conn.disconnect();

                    JsonObject client = null;
                    JsonElement root = new JsonParser().parse(retSrc);
                    if (root.isJsonObject()) {
                        JsonElement el = root.getAsJsonObject().get("suggestions");
                        if (el != null && el.isJsonArray()) {
                            JsonArray data = el.getAsJsonArray();
                            for(JsonElement sel : data) {
                                if(sel.isJsonObject()) {
                                    JsonElement vel = sel.getAsJsonObject().get("value");
                                    if(vel != null) {
                                        res.add(vel.getAsString());
                                    }
                                }
                            }
                        }
                    }

                    action.run(res.size() > 0 ? res.get(0) : "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            try {
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface TAction{
        void resolve(PassportResponce data);
        void reject(String data);
    }

    public enum PassportResponce{
        PASSPORT_OK, PASSPORT_INPUT_EMPTY, PASSPORT_INVALID_FORMAT, PASSPORT_INVALID, PASSPORT_SERVICE_ERROR
    }

    public static void checkPassport(String source, TAction action) {
        final String filter = source.trim();
        Thread t = new Thread(() -> {
            String res = "";

            try {
                String url = "https://cleaner.dadata.ru/api/v1/clean/passport";
                URL u = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", String.format("Token %s", API_KEY));
                conn.setRequestProperty("X-Secret", SECRET_KEY);
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                conn.setChunkedStreamingMode(0);
                OutputStream out = conn.getOutputStream();
                String query = String.format("[ \"%s\" ]", filter);
                out.write(query.getBytes("UTF-8"));

                int htmlError = conn.getResponseCode();

                if (htmlError != 200){
                    action.reject("Html Error: " + htmlError);
                    return;
                }

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line;
                String retSrc = "";
                while ((line = reader.readLine()) != null) {
                    retSrc += line;
                }
                conn.disconnect();

                JsonObject client = null;
                JsonElement retJson = new JsonParser().parse(retSrc);
                if (retJson.isJsonArray()){
                    JsonArray data = retJson.getAsJsonArray();

                    if (data.size() > 0) {
                        res = ((JsonObject) data.get(0)).get("qc").toString();
                    }
                }

                if (res.length() == 0)
                    action.reject("not data");
                else {
                    PassportResponce ret = PassportResponce.PASSPORT_OK;

                    if (res.equals("2"))
                        ret = PassportResponce.PASSPORT_INPUT_EMPTY;
                    else if(res.equals("1"))
                        ret = PassportResponce.PASSPORT_INVALID_FORMAT;
                    else if (res.equals("10"))
                        ret = PassportResponce.PASSPORT_INVALID;

                    action.resolve(ret);
                }
            } catch (Exception e) {
                e.printStackTrace();
                action.reject(e.getMessage());
            }
        });
        try {
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
            action.reject(e.getMessage());
        }
    }

}
