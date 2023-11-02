package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import android.util.Xml;

import com.google.gson.Gson;
import com.grsoft.types.Scale;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONAnswerParser {

    Map<String, List<Map<String, Object>>> data = new HashMap<>();
    String error = "";
    Gson gson = new Gson();

    public JSONAnswerParser() {
    }

    public String getError() { return error; }
    public void setError(String err) { error = err; }
    public boolean haveError() { return error.length() > 0; }

    public boolean parse(String json) {
        int idx = json.indexOf("\r\n\r\n");
        if(idx >= 0) {
            json = json.substring(idx + 4);
        }
        if(json.startsWith("[")) {
            List data = gson.fromJson(json, List.class);
            for (Object el : data) {
                if (el instanceof Map) {
                    loadElement((Map) el);
                }
            }
        } else {
            Map el = gson.fromJson(json, Map.class);
            loadElement(el);
        }

        return data.size() > 0;
    }

    private void loadElement(Map el) {
        String name = null;
        List<Map<String, Object>> eldata = new ArrayList<>();

        Map<String,Object> objel = (Map<String, Object>) el;
        for(Map.Entry<String, Object> me : objel.entrySet()) {
            if(me.getKey().equals("data")) {
                if(me.getValue() instanceof List) {
                    for(Object iel : (List)me.getValue()) {
                        if(iel instanceof Map) {
                            eldata.add((Map<String, Object>) iel);
                        }
                    }
                }
            } else if(me.getKey().equals("name")) {
                name = me.getValue().toString();
            }
        }

        if(name != null && eldata.size() > 0) {
            this.data.put(name, eldata);
        }
    }

    public <T extends DataObject> List<T> read(String objName, Class<T> cls) {
        List<T> ret = new ArrayList<>();

        List<Map<String, Object>> els = data.get(objName);
        if(els != null) {
            Field[] fields = cls.getFields();
            for(Map<String,Object> el : els) {
                try {
                    T dest = cls.newInstance();
                    if(setElement(dest, el, fields))
                        ret.add(dest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    private <T extends DataObject> boolean setElement(T dest, Map<String, Object> src, Field[] fields) {
        for(Field f : fields) {
            int mdf = f.getModifiers();
            if( (mdf & (Modifier.FINAL | Modifier.STATIC)) != 0 || (mdf & Modifier.PUBLIC) == 0 ) continue;

            Object val = src.get(f.getName());
            if(val == null)
                continue;
            try {
                Class<?> dt = f.getType();
                Class<?> st = val.getClass();

                if(st == String.class) {
                    f.set(dest, val.toString());
                }

                if (st == Double.class && dt == String.class){
                    double ival = Double.parseDouble(val.toString());
                    DecimalFormat format = new DecimalFormat("0.#");
                    f.set(dest, format.format(ival));
                }
                else if(dt == int.class) {
                    if(st == String.class) {
                        int ival = Integer.parseInt(val.toString());
                        f.set(dest, ival);
                    } else if(st == Double.class) {
                        Scale s = f.getAnnotation(Scale.class);
                        int ival =  (int)((Double)val + 0.5);
                        if(s!= null) {
                            ival =  (int)((Double)val * s.value() + 0.05);
                        }
                        f.set(dest, ival);
                    } else if(st == Integer.class) {
                        f.set(dest, (int)val);
                    }
                } else if(dt == double.class) {
                    if(st == String.class) {
                        double ival = Double.parseDouble(val.toString());
                        f.set(dest, ival);
                    } else {
                        f.set(dest, (double)val);
                    }
                } else if(dt == Date.class) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                    f.set(dest, sdf.parse(val.toString()));
                } else if (dt == List.class) {
                    ArrayList list = new ArrayList();
                    f.set(dest, list);
                    Class<? extends DataObject> itemType = DataObjectInfo.getInstance().getListType(dest.getClass(), f.getName());
                    for(Object el : (List)val) {
                        if(el instanceof Map) {
//                            Map map = gson.fromJson(el.toString(), Map.class);
                            T item = (T) itemType.newInstance();
                            if (setElement(item, (Map<String, Object>) el, itemType.getFields()))
                                list.add(item);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static JSONAnswerParser read(HttpURLConnection conn) {
        JSONAnswerParser jp = new JSONAnswerParser();
        try {
            InputStream istr = conn.getResponseCode() < 300 ? conn.getInputStream() : conn.getErrorStream();
            ByteArrayOutputStream baso = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int n;
            while((n = istr.read(chunk)) > 0) {
                baso.write(chunk, 0, n);
            }

            String body = baso.toString(Xml.Encoding.UTF_8.name());
            jp.parse(body);
        } catch (Exception e) {
            e.printStackTrace();
            jp.setError(e.getLocalizedMessage());
        }

        return jp;
    }
}
