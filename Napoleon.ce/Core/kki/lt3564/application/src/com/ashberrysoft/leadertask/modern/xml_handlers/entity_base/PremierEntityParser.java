package com.ashberrysoft.leadertask.modern.xml_handlers.entity_base;

import java.io.Reader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LionEntity;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.exception.ExceptionReason;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_process.LTaskProcessParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_put.LTaskPutParser;
import com.ashberrysoft.leadertask.utils.CursorySyncLogger;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

public class PremierEntityParser<DATA extends LionEntity<DATA>, HOLDER> extends BaseXmlParser<HOLDER> {

    public static final int UNLOAD_COUNT = 128;

    private final PremierEntityUnloader<DATA> mUnloader;
    private BaseXmlParser<HOLDER> mParser;

    public interface PremierEntityUnloader<DATA> {
        void unload(List<DATA> data);
    }

    public static <DATA extends LionEntity<DATA>, HOLDER> HOLDER parse(Reader reader,
            PremierEntityUnloader<DATA> unloader) throws Exception {
        try {
            final XMLReader xmlReader;
            {
                final SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
                xmlReader = sp.getXMLReader();
            }

            final long start = System.currentTimeMillis();

            final PremierEntityParser<DATA, HOLDER> parser = new PremierEntityParser<>(xmlReader, unloader);
            xmlReader.setContentHandler(parser);
            xmlReader.parse(new InputSource(reader));

            Utils.toLog("\tPremierEntityParser parse time = " + (System.currentTimeMillis() - start));
            return parser.getData();

        } catch (Exception e) {
            CursorySyncLogger.getInstance(null).toLog(e);
            throw LeaderException.create(ExceptionReason.PARSING_RESPONSE, e);
        }
    }

    private PremierEntityParser(XMLReader reader, PremierEntityUnloader<DATA> unloader) {
        super(reader, null);
        mUnloader = unloader;
    }

    @Override
    protected void onStartElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        mParser = newInstanceParser(ResponseType.getResponseType(localName), getReader(), this, mUnloader);
        if (mParser != null) {
            getReader().setContentHandler(mParser);
            mParser.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    protected void onEndElement(String uri, String localName, String qName) throws SAXException {
        if (mParser != null) {
            setData(mParser.getData());
            mParser = null;
        }
    }

    @Override
    protected void onCharacters(char[] ch, int start, int length) throws SAXException {}

    @SuppressWarnings("unchecked")
    private static <DATA extends LionEntity<DATA>, HOLDER> BaseXmlParser<HOLDER> newInstanceParser(ResponseType type,
            XMLReader reader, DefaultHandler defaultHandler, PremierEntityUnloader<DATA> unloader) {
        switch (type) {
        case PROCESS_TASKS:
            return (BaseXmlParser<HOLDER>) new LTaskProcessParser(reader, defaultHandler);

        case PUT_TASKS:
            return (BaseXmlParser<HOLDER>) new LTaskPutParser(reader, defaultHandler,
                    (PremierEntityUnloader<LTask>) unloader);

        default:
            return null;
        }
    }

    private static enum ResponseType {
        NONE(SharedStrings.EMPTY),
        // PROCESS RESPONCES
        PROCESS_TASKS("processtasksresponse"),
        // PUT RESPONCES
        PUT_TASKS("puttasksresponse");

        final String mString;

        ResponseType(String string) {
            mString = string;
        }

        @Override
        public String toString() {
            return mString;
        }

        public static ResponseType getResponseType(String name) {
            final String nameLower = name.toLowerCase();

            for (ResponseType type : values()) {
                if (type.toString().equals(nameLower)) {
                    Utils.toLog(type.name());
                    return type;
                }
            }

            return NONE;
        }
    }
}