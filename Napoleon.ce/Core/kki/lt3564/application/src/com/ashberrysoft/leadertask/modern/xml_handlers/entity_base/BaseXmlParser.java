package com.ashberrysoft.leadertask.modern.xml_handlers.entity_base;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class BaseXmlParser<DATA> extends DefaultHandler {

    private final XMLReader mReader;
    private final DefaultHandler mDefaultHandler;
    private int mDepth = 0;
    private DATA mData;

    protected BaseXmlParser(XMLReader reader, DefaultHandler defaultHandler) {
        mReader = reader;
        mDefaultHandler = defaultHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        mDepth++;
        onStartElement(uri, localName, qName, atts);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        mDepth--;
        if (mDepth == 0) {
            if (mDefaultHandler != null) {
                mDefaultHandler.endElement(uri, localName, qName);
                mReader.setContentHandler(mDefaultHandler);
            }
        }

        onEndElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        onCharacters(ch, start, length);
    }

    protected abstract void onStartElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException;

    protected abstract void onEndElement(String uri, String localName, String qName) throws SAXException;

    protected abstract void onCharacters(char[] ch, int start, int length) throws SAXException;

    protected XMLReader getReader() {
        return mReader;
    }

    protected DefaultHandler getDefaultHandler() {
        return mDefaultHandler;
    }

    public DATA getData() {
        return mData;
    }

    protected void setData(DATA data) {
        mData = data;
    }

    protected int getDepth() {
        return mDepth;
    }

    protected void setDepth(int depth) {
        mDepth = depth;
    }
}