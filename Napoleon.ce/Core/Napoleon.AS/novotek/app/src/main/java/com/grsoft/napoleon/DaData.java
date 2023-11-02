package com.grsoft.napoleon;

import android.app.Notification;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DaData {
    public interface Action{
        void run(String val);
    }

    static final String API_KEY = "9a8451d58983aa389852d262b4c13f7e45f56a06";
    static final String SECRET_KEY = "948ba4e62bcba6cef28d26a13308e30ee21368f3";
    static final int MIN_LENGTH = 4;


    public static void getNameOrg(String filter, Action action) {
        final List<String> res =  new ArrayList<>();

        if(filter.length() > 0) {
            Thread t = new Thread(() -> {
                try {
                    String url = "https://suggestions.dadata.ru/suggestions/api/4_1/rs/findById/party";
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

    public static List<String> getAddresses(String filter) {
        final List<String> res =  new ArrayList<>();
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


}
