package com.grsoft.network;

import com.grsoft.dataobjects.GRServerInfo;
import com.grsoft.dataobjects.JSONAnswerParser;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ConnectionHelper {
    public static boolean TESTING = false;
    public static String ADDR;
    public static String UUID;
    static public int PORT;

    static public String BUCKET = "data.napmobile.ru";
    static public String BUCKET_KEY_ID = "YCAJEeesm_2BKHxmOjybS5VBh";
    static public String BUCKET_KEY = "YCN0bLoO8JZSBUblMqJ3T2SybAjVrUxrZvfGWtmn";
    static public String BUCKET_REGION = "ru-central1";
    static public String BUCKET_HOST = "storage.yandexcloud.net";

//    static public String BUCKET = "";
//    static public String BUCKET_KEY_ID = "";
//    static public String BUCKET_KEY = "";
//    static public String BUCKET_REGION = "";
//    static public String BUCKET_HOST = "";

    public static String credentials() {
        String res = String.format("/%s/s3/aws4_request", BUCKET_REGION);
        return res;
    }

    static public class Result {
        public SocketConnection connection;
        public String error;

        public Result(SocketConnection c, String err) { connection = c; error = err;}
    }

    static String curaddr;
    static int curport;

    public static void clearCache() {
        curaddr = null;
    }

    /**
     *
     * @param ui
     * @return Returns connected socket or error
     */
    public static Result getConnection(UserInfo ui) {
        if(TESTING) {
            String err = null;
            CfgNpl c = (CfgNpl) ConfigManager.getConfig();
            c.uuid = UUID;
            SocketConnection res = new SocketConnection(ADDR, PORT);
            if(res.connect()) {
                res.setWin();
            } else {
                err = res.getError();
                res = null;
            }
            return new Result(res, err);
        }

        if(curaddr != null) {
            SocketConnection res = new SocketConnection(curaddr, curport);
            if(res.connect())
                return new Result(res, null);

            clearCache();
            return new Result(null, res.getError());
        }

        SocketConnection res = null;
        String err = null;
        try {
            if(ui.getServerCode().length() > 0) {
                String url = String.format("%s/api/connection", Config.HOST_URL);
                URL addr = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) addr.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + ui.getServerCode());

                JSONAnswerParser jp = JSONAnswerParser.read(conn);
                if(!jp.haveError()) {
                    List<GRServerInfo> si = jp.read("ServerConnection", GRServerInfo.class);
                    if(si.size() > 0) {
                        GRServerInfo ts = si.get(0);
                        res = new SocketConnection(ts.address, ts.port);
                        if(res.connect()) {
                            res.setWin();

                            curaddr = ts.address;
                            curport = ts.port;

                            BUCKET = ts.bucket;
                            BUCKET_HOST = ts.bucket_host;
                            BUCKET_KEY = ts.bucket_key;
                            BUCKET_REGION = ts.bucket_region;
                            BUCKET_KEY_ID = ts.bucket_key_id;
                        } else {
                            err = res.getError();
                            res = null;
                        }
                    }
                } else {
                    err = jp.getError();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            err = e.getLocalizedMessage();
        }
        return new Result(res, err);
    }
}
