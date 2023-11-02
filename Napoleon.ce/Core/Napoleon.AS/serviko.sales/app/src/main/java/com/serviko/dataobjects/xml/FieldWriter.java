package com.serviko.dataobjects.xml;

import android.content.Intent;

import com.serviko.dataobjects.ws.WSExchange;

import org.xmlpull.v1.XmlSerializer;

import java.io.Writer;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class FieldWriter implements Comparable<FieldWriter> {
    protected  String element;
    protected  Field src;
    protected int index;
    Writer out;

    public FieldWriter(String element, Field src, int index, Writer out) {
        this.element = element;
        this.src = src;
        this.index = index;
        this.out = out;
    }

    @Override
    public int compareTo(FieldWriter o) {
        if(index < 0)
            return o.index < 0 ? element.compareTo(o.element) : 1;
        return index - o.index;
    }

    public void writeElement(XmlSerializer s, String namespace, Object o) {
        try {
            if(element.length() > 0)
                s.startTag(namespace, element);
            Object value = src == null ? o : src.get(o);
            s.text(valueToString(value));
            if(element.length() > 0)
                s.endTag(namespace, element);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    abstract String valueToString(Object value);

    public static FieldWriter create(WSDLElement el, Field f, int index, Writer out) {
        String name = el.name();
        Class<?> c = f.getType();
        if(c == String.class) {
            return new StringWriter(name, f, index, out);
        } else if(c == boolean.class) {
            return new BooleanWriter(name, f, index, out);
        } else if(c == int.class || c == long.class) {
            return new NumberWriter(name, f, index, out);
        } else if(c == float.class || c == double.class) {
            return new FloatWriter(name, f, index, out);
        } else if(c == Date.class) {
            return new DateWriter(name, f, index, out);
        } else if(c == List.class) {
            return new ListWriter(name, f, index, out);
        }
        ObjectWriter ow = new ObjectWriter(name, f, index, out);
        if(ow.writers.size() > 0)
            return ow;
        return null;
    }

    public static List<FieldWriter> createWriter(Class c, Writer out) {
        List<FieldWriter> wrs = new ArrayList<>();
        Field[] fields = c.getFields();

        List<String> order = new ArrayList<>();

        WSDLElement w = (WSDLElement)c.getAnnotation(WSDLElement.class);
        if(w != null) {
            String ostr = w.memberOrder();
            if(ostr.length() > 0)
                for (String o : ostr.split(","))
                    order.add(o);
        }

        for(Field f : fields) {
            int mdf = f.getModifiers();
            if( (mdf & (Modifier.FINAL | Modifier.STATIC)) != 0 || (mdf & Modifier.PUBLIC) == 0 ) continue;
            w = f.getAnnotation(WSDLElement.class);
            if(w != null) {
                FieldWriter fw = FieldWriter.create(w, f, order.indexOf(w.name()), out);
                if(fw != null) {
                    wrs.add(fw);
                }
            }
        }
        if(order.size() > 0)
            Collections.sort(wrs);
        return wrs;
    }
}

class ObjectWriter extends FieldWriter {
    List<FieldWriter> writers;

    public ObjectWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
        writers = createWriter(src.getType(), out);
    }

    @Override
    public void writeElement(XmlSerializer s, String namespace, Object o) {
        try {
            Object srcEl = src.get(o);

            if(element.length() > 0)
                s.startTag(namespace, element);

            for(FieldWriter fw : writers) {
                fw.writeElement(s, namespace, srcEl);
            }
            if(element.length() > 0)
                s.endTag(namespace, element);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    String valueToString(Object value) {
        return null;
    }
}

class ListWriter extends FieldWriter {
    List<FieldWriter> writers;
    String elementName = "";
    Class<?> itemClass;

    Pattern p = Pattern.compile("@(.*)@");

    public ListWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
        itemClass = (Class<?>) ((ParameterizedType) src.getGenericType()).getActualTypeArguments()[0];
        WSDLElement w = itemClass.getAnnotation(WSDLElement.class);
        if(w != null)
            elementName = w.name();

        if(itemClass == String.class) {
            writers = new ArrayList<>();
            writers.add(new StringWriter("", null, 0, out));
        } else {
            writers = createWriter(itemClass, out);
        }
    }

    public boolean StringElementWriter(XmlSerializer s, String namespace, Object o) {
        try{
            String val = writers.get(0).valueToString(o);

            if (element.length() > 0) {
                Matcher m = p.matcher(val);
                if(m.find()) {
                    String type = m.group(1);
                    String ns = type.split(":")[0];
                    val = val.substring(m.group(0).length());

                    s.setPrefix(ns, "ASFMobileTrade");
                    s.startTag(namespace, element);
                    s.attribute(WSExchange.XSI_NS, "type", type);
                } else {
                    s.startTag(namespace, element);
                }
            }

            s.flush();
            out.write(val);
//            s.text(val);
            if(element.length() > 0)
                s.endTag(namespace, element);

             return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void writeElement(XmlSerializer s, String namespace, Object o) {
        try {
            List<?> value = (List<?>) src.get(o);
            if(value.size() == 0) {
                s.startTag(namespace, element);
                s.endTag(namespace, element);
            } else  {
                if(elementName.length() > 0)
                    s.startTag(namespace, element);
                for(Object el : value) {
                    if(itemClass == String.class && StringElementWriter(s, namespace, el))
                        continue;

                    if(elementName.length() > 0)
                        s.startTag(namespace, elementName);
                    else
                        s.startTag(namespace, element);
                    for(FieldWriter fw : writers) {
                        fw.writeElement(s, namespace, el);
                    }
                    if(elementName.length() > 0)
                        s.endTag(namespace, elementName);
                    else
                        s.endTag(namespace, element);
                }
                if(elementName.length() > 0)
                    s.endTag(namespace, element);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    String valueToString(Object value) {
        return null;
    }
}

class BooleanWriter extends FieldWriter {

    public BooleanWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
    }

    @Override
    String valueToString(Object value) {
        if(value == null)
            return "false";
        return ((boolean)value) ? "true" : "false";
    }
}

class NumberWriter extends  FieldWriter {

    public NumberWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
    }

    @Override
    String valueToString(Object value) {
        if(value == null)
            return "0";
        return value instanceof Integer ? Integer.toString((int)value) : Long.toString((long)value);
    }
}

class FloatWriter extends  FieldWriter {

    public FloatWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
    }

    @Override
    String valueToString(Object value) {
        if(value == null)
            return "0";
        return value instanceof Double ? Double.toString((double)value) : Float.toString((float)value);
    }
}

class StringWriter extends FieldWriter {

    public StringWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
    }

    @Override
    String valueToString(Object value) {
        return value == null ? "" : value.toString();
    }
}

class DateWriter extends FieldWriter {
    SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public DateWriter(String element, Field src, int index, Writer out) {
        super(element, src, index, out);
    }

    @Override
    String valueToString(Object value) {
        return dtf.format((Date)value);
    }
}