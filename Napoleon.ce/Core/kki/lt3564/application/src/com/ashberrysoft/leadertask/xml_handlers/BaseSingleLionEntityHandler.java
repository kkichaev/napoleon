package com.ashberrysoft.leadertask.xml_handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public abstract class BaseSingleLionEntityHandler<T extends BaseLionEntityInterface> extends BaseXmlSaxHandlerProcessAll<T> {

    private HashMap<String, Boolean> mEntity;
    private StringBuilder mStringBuilder;

    public BaseSingleLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);

        mData = getEntityConstructor();
        mEntity = getEntityHashMap();
    }

    protected abstract T getEntityConstructor();

    protected abstract HashMap<String, Boolean> getEntityHashMap();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        final Iterator<Entry<String, Boolean>> iterator = mEntity.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, Boolean> entry = iterator.next();
            if (entry.getKey().equalsIgnoreCase(localName)) {
                entry.setValue(true);
                mStringBuilder = new StringBuilder();
                break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (!mEntity.containsValue(true)) {
            return;
        }

        final Iterator<Entry<String, Boolean>> iterator = mEntity.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, Boolean> entry = iterator.next();
            if (entry.getValue()) {
                if (mStringBuilder.length() > 0) {
                    mData.fillKeyValue(entry.getKey(), mStringBuilder.toString());
                }

                entry.setValue(false);
                mStringBuilder = null;
                break;
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        if (mStringBuilder != null) {
            for (int i = start; i < length; i++) {
                mStringBuilder.append(ch[i]);
            }
        }
    }
}