package com.ashberrysoft.leadertask.xml_handlers.list;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListStringHandler extends BaseXmlSaxHandlerProcessAll<List<String>> {

    // VALUE's
    private final String mChildTag;
    private final StringBuilder mStringBuilder;
    private boolean mMatch;

    public ListStringHandler(XMLReader reader, DefaultHandler defaultHandler, String childTag) {
        super(reader, defaultHandler);

        mChildTag = childTag;
        mStringBuilder = new StringBuilder();
        mData = new ArrayList<String>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        mMatch = mChildTag == null || localName.equalsIgnoreCase(mChildTag);
        if (mMatch) {
            Utils.clearStringBuilder(mStringBuilder);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (mMatch && mStringBuilder.length() > 0) {
            mData.add(mStringBuilder.toString());
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        for (int i = start; i < length; i++) {
            mStringBuilder.append(ch[i]);
        }
    }
}