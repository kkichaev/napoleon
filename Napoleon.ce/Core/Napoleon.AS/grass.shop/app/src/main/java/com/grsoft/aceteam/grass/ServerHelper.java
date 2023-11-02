package com.grsoft.aceteam.grass;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.GrassDiscountRcv;
import com.grsoft.dataobjects.JSONAnswerParser;
import com.grsoft.dataobjects.NumSequence;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceCostRcv;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RegUserAnswer;
import com.grsoft.dataobjects.ServerAnswer;
import com.grsoft.dataobjects.StoreQty;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.types.Scale;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerHelper {
    public interface GoodsResponse {
        void complete(List<PriceEx> result, String error);
    }

    public interface RegisterResponse {
        void complete(boolean result, String error);
    }

    public interface RequestNumberResponse {
        void complete(String number, String error);
    }

    public interface SendOrderResponse {
        void complete(Boolean sended, String error);
    }

    static String makeUrl(String endPoint) {
        return String.format("%s/grs/call/%s", Config.HOST_URL, endPoint);
    }
    static HttpURLConnection makeConnection(String serverCode, String url, String data) throws IOException {
        URL addr = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) addr.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + serverCode);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        if(data != null) {
            conn.getOutputStream().write(data.getBytes(StandardCharsets.UTF_8));
            conn.getOutputStream().close();
        }

        return conn;
    }

    static void putValues(Map<String, Object> dest, Object src, String[] items) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Class<?> ct = src.getClass();
        for(String name : items) {
            try {
                Field f = ct.getField(name);
                if(f == null) {
                    continue;
                }
                Class<?> tp = f.getType();
                Object val = f.get(src);

                if(tp == String.class) {
                    dest.put(name, val.toString());
                } else if(tp == int.class || tp == long.class) {
                    Scale s = f.getAnnotation(Scale.class);
                    if(s != null) {
                        double dval = (tp == int.class ? ((double)(int)val) : ((double)(long)val)) / s.value();
                        dest.put(name, dval);
                    } else {
                        dest.put(name, val);
                    }
                } else if(tp == Date.class) {
                    dest.put(name, sdf.format((Date)val));
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    static String orderToJSON(OrderEx doc) {
        Config cfg = ConfigManager.getConfig();
        doc.userid = cfg.userid;
        Map<String, Object> data = new HashMap<>();

        String[] objFields = new String[] {
                "number"
                ,"created"
                ,"date"
                ,"id"
                ,"userid"
        };
        putValues(data, doc, objFields);
        List<Map<String, Object>> items = new ArrayList<>();
        data.put("items", items);

        String[] itemFields = new String[] {
                "id"
                ,"qty"
                ,"qtyPack"
                ,"cost"
                ,"discount"
                ,"costItem"
                ,"unit"
        };
        for(OrderItem oi : doc.items) {
            Map<String, Object> idata = new HashMap<>();
            putValues(idata, oi, itemFields);
            items.add(idata);
        }

        Gson gson = new Gson();
        Type gsonType = new TypeToken<Map>(){}.getType();
        String res = gson.toJson(data, gsonType);
        return res;
    }

    public static void sendOrder(OrderImpl doc, final SendOrderResponse handler) {
        Thread t = new Thread(() -> {
            try {
                String data = String.format("[{\"name\":\"Order\", \"data\":[%s]}]", orderToJSON((OrderEx) doc.getData()));
                String url = String.format("%s/grs/object", Config.HOST_URL);
                HttpURLConnection conn = makeConnection(Config.SERVER_CODE, url, data);

                JSONAnswerParser jp = JSONAnswerParser.read(conn);
                if(!jp.haveError()) {
                    List<ServerAnswer> res = jp.read("ServerAnswer", ServerAnswer.class);
                    if(res.size() > 0) {
                        handler.complete(true, "");
                        return;
                    }
                }
                handler.complete(false, jp.getError());
            } catch (Exception e) {
                handler.complete(false, e.getLocalizedMessage());
            }
        });
        t.start();
    }

    public static void requestNumber(final RequestNumberResponse handler) {
        Thread t = new Thread(() -> {
            try {
                String data = "{}";
                HttpURLConnection conn = makeConnection(Config.SERVER_CODE, makeUrl("customers.grass.new_doc_number"), data);

                JSONAnswerParser jp = JSONAnswerParser.read(conn);
                if(!jp.haveError()) {
                    List<NumSequence> res = jp.read("NumSequence", NumSequence.class);
                    if(res.size() > 0) {
                        handler.complete(Integer.toString(res.get(0).number), "");
                        return;
                    }
                }
                handler.complete(null, jp.getError());
            } catch (Exception e) {
                handler.complete(null, e.getLocalizedMessage());
            }
        });
        t.start();
    }

    public static void register(Config config, final RegisterResponse handler) {
        Thread t = new Thread(() -> {
            try {
                String data = String.format("{\"userid\":\"%s\"}", config.userid);
                HttpURLConnection conn = makeConnection(Config.SERVER_CODE, makeUrl("customers.grass.register_user"), data);

                JSONAnswerParser jp = JSONAnswerParser.read(conn);
                if(!jp.haveError()) {
                    List<RegUserAnswer> res = jp.read("RegUser", RegUserAnswer.class);
                    if(res.size() > 0) {
                        config.uuid = res.get(0).uuid;
                        handler.complete(true, "");
                        return;
                    }
                }
                handler.complete(false, jp.getError());
            } catch (Exception e) {
                handler.complete(false, e.getLocalizedMessage());
            }
        });
        t.start();
    }

    public static void getGoods(String barcode, final GoodsResponse handler) {
        Thread t = new Thread(() -> {
            try {
                String data = String.format("{\"barcode\":\"%s\"}", barcode);
                HttpURLConnection conn = makeConnection(Config.SERVER_CODE, makeUrl("customers.grass.get_goods"), data);

                JSONAnswerParser jp = JSONAnswerParser.read(conn);
                if(!jp.haveError()) {
                    List<PriceEx> price = jp.read("Price", PriceEx.class);
                    Map<String, PriceEx> src = new HashMap<>();
                    for(PriceEx pe : price) {
                        src.put(pe.id, pe);
                    }

                    for(PriceCostRcv prc : jp.read("PriceCost", PriceCostRcv.class)) {
                        PriceEx pe = src.get(prc.idItem);
                        if(pe != null) {
                            pe.addCost(prc.cost);
                        }
                    }
                    for(StoreQty qty : jp.read("StoreQty", StoreQty.class)) {
                        PriceEx pe = src.get(qty.idItem);
                        if( pe != null)
                            pe.qty = qty.qty;
                    }
                    for(GrassDiscountRcv i : jp.read("GrassDiscounts", GrassDiscountRcv.class)) {
                        PriceEx pe = src.get(i.id);
                        if(pe != null) {
                            pe.discounts.add(i);
                        }
                    }

                    DbWriter w = new DbWriter();
                    for(PriceEx pe : price) {
                        w.insertRecord(pe);
                    }
                    w.close();
                    handler.complete(price, "");
                } else {
                    handler.complete(null, jp.getError());
                }
            } catch (Exception e) {
                handler.complete(null, e.getLocalizedMessage());
            }
        });
        t.start();
    }
}
