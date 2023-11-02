package com.serviko.dataobjects.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.channels.FileLock;
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

    public void read(XmlPullParser parser, Object o) {
        try {
            while(true) {
                int et = parser.next();
                if(et == XmlPullParser.TEXT) {
                    if(src != null) {
                        Object value = fromText(parser.getText());
                        src.set(o, value);
                    }
                } else if(et == XmlPullParser.END_TAG && element.equals(parser.getName())) {
                    break;
                } else if(et == XmlPullParser.END_DOCUMENT) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    abstract Object fromText(String text);

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
        }
        return null;
    }

    public static Map<String, FieldReader> createReader(Class c) {
        Map<String, FieldReader> rdr = new HashMap<>();

        Field[] fields = c.getFields();
        for(Field f : fields) {
            int mdf = f.getModifiers();
            if ((mdf & (Modifier.FINAL | Modifier.STATIC)) != 0 || (mdf & Modifier.PUBLIC) == 0)
                continue;
            WSDLElement w = f.getAnnotation(WSDLElement.class);
            if (w != null) {
                FieldReader fr = FieldReader.create(w.name(), f);
                if(fr != null) {
                    rdr.put(w.name(), fr);
                }
            }
        }
        return rdr;
    }
}

class FloatReader extends FieldReader {

    public FloatReader(String element, Field src) {
        super(element, src);
    }

    @Override
    Object fromText(String text) {
        if(src.getType() == float.class) {
            return new Float(text);
        }
        return new Double(text);
    }
}

class ListReader extends FieldReader {
    Class<?> itemClass;
    Map<String, FieldReader> reader;
    List<?> list;
    String endItem;
    FieldReader primitiveReader = null;

    public ListReader(String element, Field src) {
        super(element, src);

        itemClass = (Class<?>) ((ParameterizedType) src.getGenericType()).getActualTypeArguments()[0];
        if(isPrimitive(itemClass)) {
            reader = new HashMap<>();
            primitiveReader = FieldReader.create("", itemClass, null);
        } else {
            reader = FieldReader.createReader(itemClass);
        }
        list = createList(itemClass);
        endItem = element;
    }

    boolean isPrimitive(Class<?> c) {
        return c == String.class || c == Integer.class || c == Long.class || c == Boolean.class || c == Double.class || c == Float.class;
    }

    <T> List<T>     createList(Class<T> c) {
        return new ArrayList<T>();
    }

    <T> void makeNewList() {
        list = createList(itemClass);
        recreateLists();
    }

    void recreateLists() {
        for(FieldReader fr : reader.values()) {
            if(fr instanceof ListReader) {
                ((ListReader)fr).makeNewList();
            }
        }
    }

    <T> void readPrimitive(Class<T> c, XmlPullParser parser) {
        try {
            List<T> src = (List<T>)list;

            while(true) {
                int evType = parser.next();
                if (evType == XmlPullParser.TEXT) {
                    T val = (T)primitiveReader.fromText(parser.getText());
                    src.add(val);
                } else if (evType == XmlPullParser.END_DOCUMENT) {
                    break;
                } else if (evType == XmlPullParser.END_TAG) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    <T> void readList(Class<T> c, XmlPullParser parser) {
        if(primitiveReader != null) {
            readPrimitive(c, parser);
            return;
        }
        try {
            List<T> src = (List<T>)list;

            T item = c.newInstance();
            while(true) {
                int evType = parser.next();
                if (evType == XmlPullParser.START_TAG) {
                    String name = parser.getName();
                    FieldReader fr = reader.get(name);
                    if(fr != null) {
                        fr.read(parser, item);
                    }
                } else if (evType == XmlPullParser.END_DOCUMENT) {
                    break;
                } else if (evType == XmlPullParser.END_TAG) {
                    if(endItem.equals(parser.getName())) {
                        src.add(item);
                        recreateLists();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void read(XmlPullParser parser, Object o) {
        try {
            if(src.get(o) != list)
                src.set(o, list);
            readList(itemClass, parser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override Object fromText(String text) { return null; }
}

class BooleanReader extends FieldReader {

    public BooleanReader(String element, Field src) {
        super(element, src);
    }

    @Override
    Object fromText(String text) {
        return "true".equalsIgnoreCase(text);
    }
}

class NumberReader extends  FieldReader {

    public NumberReader(String element, Field src) {
        super(element, src);
    }

    @Override
    Object fromText(String text) {
        if(src.getType() == int.class) {
            return new Integer(text);
        }
        return new Long(text);
    }
}

class StringReader extends FieldReader {

    public StringReader(String element, Field src) {
        super(element, src);
    }

    @Override
    Object fromText(String text) {
        return text;
    }
}

class DateReader extends  FieldReader {
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


    public DateReader(String element, Field src) {
        super(element, src);
    }

    @Override
    Object fromText(String text) {
        try {
            if(text.contains("T"))
                return dtf.parse(text);
            return df.parse(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
