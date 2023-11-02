package com.ashberrysoft.leadertask.modern.xml_handlers.entity_one;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LionEntity;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.utils.Utils;

public abstract class BaseOneEntityParser<DATA extends LionEntity<DATA>> extends BaseXmlParser<DATA> {

    private final StringBuilder mStringBuilder;

    public BaseOneEntityParser(XMLReader reader, DefaultHandler defaultHandler, DATA data) {
        super(reader, defaultHandler);

        setData(data);
        mStringBuilder = new StringBuilder();
    }

    @Override
    protected void onStartElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        Utils.clearStringBuilder(mStringBuilder);
    }

    @Override
    protected void onEndElement(String uri, String localName, String qName) throws SAXException {
        if (mStringBuilder.length() > 0) {
            getData().fillKeyValue(localName.toLowerCase(), mStringBuilder.toString());
        }
    }

    @Override
    protected void onCharacters(char[] ch, int start, int length) throws SAXException {
        for (int i = start; i < length; i++) {
            mStringBuilder.append(ch[i]);
        }
    }
}