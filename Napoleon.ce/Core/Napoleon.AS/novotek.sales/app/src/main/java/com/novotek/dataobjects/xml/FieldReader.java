package com.novotek.dataobjects.xml;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FieldReader {
    protected String element;
    protected Field src;

    public FieldReader(String element, Field src) {
        this.element = element;
        this.src = src;
    }

    public abstract void read(JSONObject obj, Object o);

    public void prepareRead() {}

    public static FieldReader create(String element, Field f) {
        Class<?> c = f.getType();
        return create(element, c, f);
    }

    static FieldReader create(String element, Class<?> c, Field f) {
        if(c == String.class) {
            return new StringReader(element, f);
        } else if(c == boolean.class) {
            return new BooleanReader(element, f);
        } else if(c == int.class || c == long.class) {
            return new NumberReader(element, f);
        } else if(c == float.class || c == double.class) {
            return new FloatReader(element, f);
        } else if(c == Date.class) {
            return new DateReader(element, f);
        } else if(c == List.class) {
            return new ListReader(element, f);
        } else {
            return new ObjectReader(element, f);
        }
    }

    static Map<Class, List<FieldReader>> readers = new HashMap<>();

    public static List<FieldReader> createReader(Class c, boolean aNew) {
        List<FieldReader> rdr;
        if(!aNew) {
            rdr = readers.get(c);
            if (rdr != null)
                return rdr;
        }
        rdr = new ArrayList<>();

        Field[] fields = c.getFields();
        for(Field f : fields) {
            int mdf = f.getModifiers();
            if ((mdf & (Modifier.FINAL | Modifier.STATIC)) != 0 || (mdf & Modifier.PUBLIC) == 0)
                continue;
            FieldReader fr = FieldReader.create(f.getName(), f);
            if(fr != null) {
                rdr.add(fr);
            }
        }
        readers.put(c, rdr);
        return rdr;
    }

    public static void clear() {
        readers.clear();
    }
}

class ObjectReader extends FieldReader {

    List<FieldReader> readers = null;

    public ObjectReader(String element, Field src) {
        super(element, src);
    }

    @Override
    public void read(JSONObject obj, Object o) {
        JSONObject tobj = obj.optJSONObject(element);
        if(tobj != null) {
            if(readers == null)
                readers = FieldReader.createReader(src.getType(), false);
            try {
                Object dest = src.getType().newInstance();
                for(FieldReader fd : readers) {
                    fd.read(tobj, dest);
                }
                src.set(o, dest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class FloatReader extends FieldReader {

    public FloatReader(String element, Field src) {
        super(element, src);
    }

    @Override
    public void read(JSONObject obj, Object o) {
        double val = obj.optDouble(element, 0);
        try {
            if(src.getType() == float.class)
                src.set(o, (float)val);
            else
                src.set(o, val);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ListReader extends FieldReader {
    Class<?> itemClass;
    List<FieldReader> reader = null;
    List<?> list;
    String endItem;
    FieldReader primitiveReader = null;

    public ListReader(String element, Field src) {
        super(element, src);

        itemClass = (Class<?>) ((ParameterizedType) src.getGenericType()).getActualTypeArguments()[0];
        if(isPrimitive(itemClass)) {
//            reader = new ArrayList<>();
            primitiveReader = FieldReader.create("", itemClass, null);
        } else {
//            reader = FieldReader.createReader(itemClass);
        }
        list = createList(itemClass);
        endItem = element;
    }

    boolean isPrimitive(Class<?> c) {
        return c == String.class || c == Integer.class || c == Long.class || c == Boolean.class || c == Double.class || c == Float.class;
    }

    <T> List<T> createList(Class<T> c) {
        return new ArrayList<T>();
    }

    <T> void makeNewList(List<FieldReader> used) {
        list = createList(itemClass);
        recreateChildsList(used);
    }

    @Override
    public void prepareRead() {
        makeNewList(new ArrayList<>());
    }

    void recreateChildsList(List<FieldReader> used) {
        if(reader == null)
            return;
        for(FieldReader fr : reader) {
            if(!used.contains(fr) && fr instanceof ListReader) {
                used.add(fr);
                ((ListReader)fr).makeNewList(used);
            }
        }
    }

    <T> void readPrimitive(Class<T> c, JSONArray a) {
        try {
            List<T> src = (List<T>)list;

            int i = 0;
            while(true) {
                Object sval = a.opt(i++);
                if(sval == null)
                    break;
                src.add((T)sval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read(JSONObject obj, Object o) {
        JSONArray a = obj.optJSONArray(element);
        if(a != null) {
            readList(itemClass, a);
            try {
                src.set(o, list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    <T> void readList(Class<T> c, JSONArray a) {
        if(a == null) {
            return;
        }
        if(primitiveReader != null) {
            readPrimitive(c, a);
            return;
        }
        if(reader == null)
            reader = FieldReader.createReader(itemClass, true);
        try {
            List<T> src = (List<T>)list;

            int index = 0;
            while(true) {
                JSONObject el = a.optJSONObject(index++);
                if(el == null)
                    break;

                T item = c.newInstance();
                for(FieldReader fr : reader) {
                    fr.read(el, item);
                }
                src.add(item);
                recreateChildsList(new ArrayList<>());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class BooleanReader extends FieldReader {

    public BooleanReader(String element, Field src) {
        super(element, src);
    }

    @Override
    public void read(JSONObject obj, Object o) {
        try {
            src.set(o, obj.optBoolean(element, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class NumberReader extends  FieldReader {

    public NumberReader(String element, Field src) {
        super(element, src);
    }

    @Override
    public void read(JSONObject obj, Object o) {
        long val = obj.optLong(element, 0);

        try {
            if (src.getType() == int.class) {
                src.set(o, (int)val);
            } else {
                src.set(o, val);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

class StringReader extends FieldReader {

    public StringReader(String element, Field src) {
        super(element, src);
    }

    @Override
    public void read(JSONObject obj, Object o) {
        try {
            src.set(o, obj.optString(element, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class DateReader extends  FieldReader {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
//    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    public DateReader(String element, Field src) {
        super(element, src);
    }

    @Override
    public void read(JSONObject obj, Object o) {
        String val = obj.optString(element);
        if(val != null) {
            try {
                Date d = df.parse(val);
                src.set(o, d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
