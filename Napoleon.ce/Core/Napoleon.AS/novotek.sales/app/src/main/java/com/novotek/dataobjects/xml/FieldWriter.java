package com.novotek.dataobjects.xml;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FieldWriter  {
    protected  String element;
    protected  Field src;

    public FieldWriter(String element, Field src) {
        this.element = element;
        this.src = src;
    }

    public void writeElement(JSONObject dest, Object o) {
        try {
            dest.put(element, src.get(o));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static FieldWriter create(Field f) {
        String name = f.getName();
        Class<?> c = f.getType();
        if(c == String.class || c == boolean.class ||
                c == int.class || c == long.class ||
                c == float.class || c == double.class) {
            return new FieldWriter(name, f);
        } else if(c == Date.class) {
            return new DateWriter(name, f);
        } else if(c == List.class) {
            return new ListWriter(name, f);
        }
        return null;
    }

    public static List<FieldWriter> createWriter(Class c) {
        List<FieldWriter> wrs = new ArrayList<>();
        Field[] fields = c.getFields();

        for(Field f : fields) {
            int mdf = f.getModifiers();
            if( (mdf & (Modifier.FINAL | Modifier.STATIC)) != 0 || (mdf & Modifier.PUBLIC) == 0 ) continue;
            FieldWriter fw = FieldWriter.create(f);
            if(fw != null) {
                wrs.add(fw);
            }
        }
        return wrs;
    }
}

class ListWriter extends FieldWriter {
    List<FieldWriter> writers;

    public ListWriter(String element, Field src) {
        super(element, src);
        Class<?> itemClass = (Class<?>) ((ParameterizedType) src.getGenericType()).getActualTypeArguments()[0];
        writers = createWriter(itemClass);
    }

    @Override
    public void writeElement(JSONObject dest, Object o) {
        JSONArray a = new JSONArray();

        try {
            List<?> value = (List<?>) src.get(o);
            for(Object e : value) {
                JSONObject del = new JSONObject();
                for(FieldWriter fw : writers) {
                    fw.writeElement(del, e);
                }
                a.put(del);
            }
            dest.put(element, a);
        } catch(Exception e) {
            e.printStackTrace();
        }
//        super.writeElement(dest, o);
    }

}

class DateWriter extends FieldWriter {
    SimpleDateFormat dtf = new SimpleDateFormat("yyyyMMdd");

    public DateWriter(String element, Field src) {
        super(element, src);
    }

    @Override
    public void writeElement(JSONObject dest, Object o) {
        try {
            Object od = src.get(o);
            if (od instanceof Date) {
                String val = dtf.format((Date) od);
                dest.put(element, val);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}