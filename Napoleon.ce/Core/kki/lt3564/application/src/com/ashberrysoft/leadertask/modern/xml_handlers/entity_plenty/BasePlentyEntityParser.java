package com.ashberrysoft.leadertask.modern.xml_handlers.entity_plenty;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser.PremierEntityUnloader;

public abstract class BasePlentyEntityParser<DATA> extends BaseXmlParser<List<DATA>> {

    // BASE
    private final String mLionName;

    // VALUE's
    private BaseXmlParser<DATA> mParser;
    private boolean mMatch;

    public BasePlentyEntityParser(XMLReader reader, DefaultHandler defaultHandler, String lionName) {
        super(reader, defaultHandler);

        mLionName = lionName;

        setData(new ArrayList<DATA>(PremierEntityParser.UNLOAD_COUNT));
    }

    @Override
    protected void onStartElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        mMatch = mLionName.equalsIgnoreCase(localName);

        if (mMatch) {
            mParser = newInstanceParser(getReader(), this);
            getReader().setContentHandler(mParser);
            mParser.startElement(uri, localName, qName, atts);
        }
    }

    protected abstract BaseXmlParser<DATA> newInstanceParser(XMLReader reader, DefaultHandler defaultHandler);

    @Override
    protected void onEndElement(String uri, String localName, String qName) throws SAXException {
        if (mMatch && mParser != null) {
            getData().add(mParser.getData());

            if (getData().size() % PremierEntityParser.UNLOAD_COUNT == 0) {
                //mUnloader.unload(getData());
            }
        }

        mParser = null;
        mMatch = false;
    }

    @Override
    protected void onCharacters(char[] ch, int start, int length) throws SAXException {}
}