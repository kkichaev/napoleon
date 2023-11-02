package com.novotek.dataobjects.ws;

import android.content.Context;

import com.novotek.dataobjects.Action;
import com.novotek.dataobjects.Brand;
import com.novotek.dataobjects.Catalog;
import com.novotek.dataobjects.CommonInfo;
import com.novotek.dataobjects.Order;
import com.novotek.dataobjects.OrderCancelResult;
import com.novotek.dataobjects.OrderSend;
import com.novotek.dataobjects.Partner;
import com.novotek.dataobjects.ProjectData;
import com.novotek.dataobjects.PartnerSrc;
import com.novotek.dataobjects.Price;
import com.novotek.dataobjects.priceTree.PriceTree;
import com.novotek.dataobjects.xml.FieldReader;
import com.novotek.dataobjects.xml.FieldWriter;
import com.novotek.sales.BuildConfig;
import com.novotek.sales.MainActivity;
import com.novotek.sales.R;
import com.novotek.sales.login_views.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kotlin.text.Charsets;

public class WSExchange {
    JSONFault fault = null;

    String urlBase = "https://mx.novo-tek.net:58443/ut-nsk-dk-hs-aa/hs/AndroidApp/";

    static  String FAULT_TAG = "Fault";

    Map<Class<?>, List<FieldWriter>> writers = new HashMap<Class<?>, List<FieldWriter>>();

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

    public JSONFault error () { return fault; }

    public void reqCode(ReqCodeParam data) {
        PostData(data, ReqCodeResult.class, urlBase + "auth", "");
    }

    public void getTestData() {
        try {
            InputStream stream = context.getResources().openRawResource(R.raw.orgs);
            List<PartnerSrc> partners = (List<PartnerSrc>) parseResult(stream, new ArrayList<PartnerSrc>() {}.getClass());

            stream = context.getResources().openRawResource(R.raw.brands);
            List<Brand> brands = (List<Brand>)parseResult(stream, new ArrayList<Brand>() {}.getClass());

            stream = context.getResources().openRawResource(R.raw.common_info);
            CommonInfo ci = (CommonInfo) parseResult(stream, CommonInfo.class);

            List<Partner> prts = new ArrayList<>();
            for(PartnerSrc p : partners) {
                int idx = partners.indexOf(p);
                if(idx == 0) { //0Н-00028596
                    stream = context.getResources().openRawResource(R.raw.prc_1);
                    List<Price> price = (List<Price>) parseResult(stream, new ArrayList<Price>() {}.getClass());

                    stream = context.getResources().openRawResource(R.raw.cat_1);
                    List<Catalog> catalog = (List<Catalog>) parseResult(stream, new ArrayList<Catalog>() {}.getClass());

                    stream = context.getResources().openRawResource(R.raw.act_1);
                    List<Action> actions = (List<Action>) parseResult(stream, new ArrayList<Action>() {}.getClass());

                    stream = context.getResources().openRawResource(R.raw.ord_1);
                    List<Order> orders = (List<Order>) parseResult(stream, new ArrayList<Order>() {}.getClass());

                    Partner ret = new Partner(p);
                    ret.setOrders(orders);
                    ret.setPrice(PriceTree.load(price, catalog, actions));
                    prts.add(ret);
                } else if(idx == 5) { //0Н-00007916
                    stream = context.getResources().openRawResource(R.raw.prc_2);
                    List<Price> price = (List<Price>) parseResult(stream, new ArrayList<Price>() {}.getClass());

                    stream = context.getResources().openRawResource(R.raw.cat_2);
                    List<Catalog> catalog = (List<Catalog>) parseResult(stream, new ArrayList<Catalog>() {}.getClass());

                    stream = context.getResources().openRawResource(R.raw.act_2);
                    List<Action> actions = (List<Action>) parseResult(stream, new ArrayList<Action>() {}.getClass());

                    stream = context.getResources().openRawResource(R.raw.ord_2);
                    List<Order> orders = (List<Order>) parseResult(stream, new ArrayList<Order>() {}.getClass());

                    Partner ret = new Partner(p);
                    ret.setOrders(orders);
                    ret.setPrice(PriceTree.load(price, catalog, actions));
                    prts.add(ret);
                } else {
                    Partner ret = new Partner(p);
                    prts.add(ret);
                }
            }

            ProjectData.commonInfo = ci;
            ProjectData.brands = new HashMap<>();
            for(Brand b : brands) {
                ProjectData.brands.put(b.name, b);
            }
            ProjectData.setPartners(prts);

            handler.complete(true, prts, WSExchange.this);
        } catch (Exception e) {

        }
    }

    public void getData() {

        final Events svH = handler;
        Events h = new Events() {
            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {

                loadBrands();
                loadCommonInfo();
                
                List<PartnerSrc> plist = (List<PartnerSrc>)response;
                List<Partner> partners = new ArrayList<>();
                for(PartnerSrc p : plist) {
                    try {
                        Partner prt = LoadPartnerData(p);
                        if(prt != null) {
                            partners.add(prt);
                        }
                        if(BuildConfig.DEBUG) {
                            break;
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }

                ProjectData.setPartners(partners);
                if(svH != null) {
                    svH.complete(true, partners, WSExchange.this);
                }
            }

            private void loadCommonInfo() {
                Object res = Exchange(urlBase + "common_info", MainActivity.getAppToken(), CommonInfo.class, null);
                if(res != fault) {
                    ProjectData.commonInfo = (CommonInfo) res;
                }
            }

//            void loadBrands(String orgId) {
//                Object res = Exchange(urlBase + "brands?orgid=" + orgId, MainActivity.appToken, new ArrayList<Brand>(){}.getClass(), null);
//                if(res != fault) {
//                    ProjectData.brands = new HashMap<>();
//                    for(Brand b : (List<Brand>)res) {
//                        ProjectData.brands.put(b.name, b);
//                    }
//                }
//            }
            void loadBrands() {
                Object res = Exchange(urlBase + "brands", MainActivity.getAppToken(), new ArrayList<Brand>(){}.getClass(), null);
                if(res != fault) {
                    ProjectData.brands = new HashMap<>();
                    for(Brand b : (List<Brand>)res) {
                        ProjectData.brands.put(b.name, b);
                    }
                }
            }

            private Partner LoadPartnerData(PartnerSrc p) throws UnsupportedEncodingException {
                Partner ret = null;

                List<Catalog> catalog;
                List<Price> price;
                List<Action> actions;
                List<Order> orders;

                String id = URLEncoder.encode(p.id, "utf-8");
//                loadBrands(id);
                Object res = Exchange(urlBase + "categories?orgid=" + id, MainActivity.getAppToken(), new ArrayList<Catalog>(){}.getClass(), null);
                if(res == fault) {
                    if(svH != null) {
                        svH.complete(false, res, WSExchange.this);
                    }
                    return ret;
                }
                catalog = (List<Catalog>) res;

                res = Exchange(urlBase + "products?orgid=" + id, MainActivity.getAppToken(), new ArrayList<Price>(){}.getClass(), null);
                if(res == fault) {
                    if(svH != null) {
                        svH.complete(false, res, WSExchange.this);
                    }
                    return ret;
                }
                price = (List<Price>) res;

                res = Exchange(urlBase + "actions?orgid=" + id, MainActivity.getAppToken(), new ArrayList<Action>(){}.getClass(), null);
                if(res == fault) {
                    if(svH != null) {
                        svH.complete(false, res, WSExchange.this);
                    }
                    return ret;
                }
                actions = (List<Action>) res;

                res = Exchange(urlBase + "orders?orgid=" + id, MainActivity.getAppToken(), new ArrayList<Order>(){}.getClass(), null);
                if(res == fault) {
                    if(svH != null) {
                        svH.complete(false, res, WSExchange.this);
                    }
                    return ret;
                }
                orders = (List<Order>) res;

                ret = new Partner(p);
                ret.setOrders(orders);
                ret.setPrice(PriceTree.load(price, catalog, actions));

                return ret;
            }

            @Override
            public void error(Exception e) {
                if(svH != null) {
                    svH.error(e);
                }
            }
        };

        handler = h;
        getPartners();
    }

    public void getPartners() {
        GetData(urlBase + "get_orgs", new ArrayList<PartnerSrc>(){}.getClass(), MainActivity.getAppToken());
    }

    public void sendMessage(String text) {
        Thread t = new Thread(() -> {
            DoGetOrPost(urlBase + "feedback", MainActivity.getAppToken(), null, text.getBytes(StandardCharsets.UTF_8));
        });
        t.start();
    }

    public void cancelOrder(Order o, Partner p) {
        try {
            String url = urlBase + "order_cancel?id=" + URLEncoder.encode(o.id, "utf-8") +
                    "&orgid=" + URLEncoder.encode(p.id, "utf-8");
            Thread t = new Thread(() -> {
                DoGetOrPost(url, MainActivity.getAppToken(), OrderCancelResult.class, null);
            });
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendOrder(OrderSend data) {
        PostData(data, SendBasketResult.class, urlBase + "order", MainActivity.getAppToken());
    }

    public void reqOrders(Partner p) {
        String id = null;
        try {
            id = URLEncoder.encode(p.id, "utf-8");
            Object res = Exchange(urlBase + "orders?orgid=" + id, MainActivity.getAppToken(), new ArrayList<Order>(){}.getClass(), null);
            if(res == fault) {
                if(handler != null) {
                    handler.complete(false, res, WSExchange.this);
                }
            }
            handler.complete(true, res, WSExchange.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void GetData(final String urlStr, final Class<?> resultType, final String auth) {
        Thread t = new Thread() {
            @Override
            public void run() {
                DoGetOrPost(urlStr, auth, resultType, null);
            }
        };
        t.start();
    }

    Object Exchange(String urlStr, String auth, Class<?> resultType, byte[] body) {
        Object retObj = null;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            if (auth != null && auth.length() > 0)
                conn.setRequestProperty("Authorization", "Bearer " + auth);

            try {
                if(body != null && body.length > 0) { // make POST
                    conn.setDoOutput(true);
                    conn.setChunkedStreamingMode(0);
                    OutputStream out = new BufferedOutputStream(conn.getOutputStream());
                    out.write(body);
                    out.flush();
                }

                if(resultType != null) {
                    Object result = parseResult(conn.getInputStream(), resultType);
                    if (result != null) {
                        retObj = result;
                    } else {
                        retObj = fault;
                    }
                }
            } catch (Exception e) {
                fault = new JSONFault();
                fault.message = conn.getResponseMessage();
                retObj = fault;
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return retObj;
    }

    void DoGetOrPost(String urlStr, String auth, Class<?> resultType, byte[] body) {
        Object res = Exchange(urlStr, auth, resultType, body);
        if (handler != null) {
            handler.complete((res == null || res != fault), res, this);
        }
    }

    void PostData(final Object param, final Class<?> resultType, final String urlStr, final String auth) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    String body = makeBody(param);
                    byte[] bodyBytes = body.getBytes("UTF-8");
                    DoGetOrPost(urlStr, auth, resultType, bodyBytes);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    void setObject(Object o, JSONObject obj) {
        try {
            Class<?> c = o.getClass();
            List<FieldReader> rdr = FieldReader.createReader(c, false);
            for(FieldReader fr : rdr) {
                fr.read(obj, o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Object makeResult(JSONObject src, Class<?> resultType) {
        Object res = null;
        try {
            Object tres = resultType.newInstance();
            setObject(tres, src);
            res = tres;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    Class<?> getGenericClass(Class<?> theClass) {
        Type type = null;

        while(theClass.getSuperclass() != null){
            type = theClass.getGenericSuperclass();

            if (type != null && type instanceof ParameterizedType)
                break;

            theClass = theClass.getSuperclass();
        }

        if (type != null)
            return (Class<?>) ((ParameterizedType)type).getActualTypeArguments()[0];
        else
            return null;
    }

    <T> List<T> loadList(JSONArray src, Class<T> elClass) {
        List<T> dest = new ArrayList<T>();

        try {
            int i = 0;
            List<FieldReader> rdr = FieldReader.createReader(elClass, false);
            while(true) {
                T tres = elClass.newInstance();
                JSONObject obj = src.optJSONObject(i++);
                if(obj == null)
                    break;

                for(FieldReader fr : rdr) {
                    fr.prepareRead();
                    fr.read(obj, tres);
                }
                dest.add(tres);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dest;
    }


    Object makeResult(JSONArray src, Class<?> resultType) {
        Object res = null;
        try {
            Class<?> elClass = getGenericClass(resultType);
            res = loadList(src, elClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    private Object parseResult(InputStream in, Class<?> resultType) throws IOException {
        Object res = null;

        FieldReader.clear();

        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        StringBuilder total = new StringBuilder();
        for (String line; (line = r.readLine()) != null; ) {
            total.append(line);
        }
        try {
            if(List.class.isAssignableFrom(resultType)) {
                JSONArray obj = new JSONArray(total.toString());
                res = makeResult(obj, resultType);
            } else {
                JSONObject obj = new JSONObject(total.toString());
                res = makeResult(obj, resultType);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res;
    }

    private String makeBody(Object param) {
        String body = "";
        try {
            JSONObject res = objToParam(param);
            body = res.toString();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return body;
    }

    private JSONObject objToParam(Object o) {
        Class<?> c = o.getClass();
        List<FieldWriter> wrs = writers.get(c);

        if(wrs == null) {
            wrs = FieldWriter.createWriter(c);
            writers.put(c, wrs);
        }

        JSONObject res = new JSONObject();
        for(FieldWriter fw : wrs) {
            fw.writeElement(res, o);
        }
        return res;
    }

    public static ErrResult checkError(boolean result, Object response) {
        ErrResult res = null;
        if(!result) {
            res = new ErrResult();
            res.message = ((JSONFault) response).message;
            res.error = -1;
        }

        return res;
    }
}
