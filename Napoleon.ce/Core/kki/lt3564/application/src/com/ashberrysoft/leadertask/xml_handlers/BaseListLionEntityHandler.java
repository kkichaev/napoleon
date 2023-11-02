package com.ashberrysoft.leadertask.xml_handlers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public abstract class BaseListLionEntityHandler<T> extends BaseXmlSaxHandlerProcessAll<List<T>> {

    private String mServerClassName;
    private BaseXmlSaxHandlerProcessAll<T> mBaseLionEntityHandler;

    public BaseListLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler, String serverCalssName) {
        super(reader, defaultHandler);

        mServerClassName = serverCalssName;
        mData = new ArrayList<T>();
    }

    protected abstract BaseXmlSaxHandlerProcessAll<T> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler);

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        if (mServerClassName.equalsIgnoreCase(localName)) {
            mBaseLionEntityHandler = getBaseLionEntityHandler(mReader, this);
            mReader.setContentHandler(mBaseLionEntityHandler);
            mBaseLionEntityHandler.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (mServerClassName.equalsIgnoreCase(localName)) {
            if (mBaseLionEntityHandler != null) {
                mData.add(mBaseLionEntityHandler.getData());
                mBaseLionEntityHandler = null;
            }
        }
    }
}