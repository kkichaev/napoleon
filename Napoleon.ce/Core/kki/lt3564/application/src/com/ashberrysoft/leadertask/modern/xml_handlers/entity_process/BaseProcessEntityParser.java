package com.ashberrysoft.leadertask.modern.xml_handlers.entity_process;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser.PremierEntityUnloader;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.ProcessEntityHolder;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.list.ListStringHandler;

public abstract class BaseProcessEntityParser<DATA> extends BaseXmlParser<ProcessEntityHolder<DATA>> {

    // VALUE's
    private final StringBuilder mStringBuilder;
    private ElementType mElementType;
    private BaseXmlParser<List<DATA>> mParser;
    private ListStringHandler mListParser;

    public BaseProcessEntityParser(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);


        setData(new ProcessEntityHolder<DATA>());
        mStringBuilder = new StringBuilder();
    }

    @Override
    protected void onStartElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        mElementType = ElementType.getElementType(localName);

        switch (mElementType) {
        case ERROR_CODE:
        case ERROR_STRING:
            Utils.clearStringBuilder(mStringBuilder);
            break;

        case LIST_DELETE_OBJECTS:
        case LIST_SEND_OBJECTS:
            mListParser = new ListStringHandler(getReader(), this, null);
            getReader().setContentHandler(mListParser);
            mListParser.startElement(uri, localName, qName, atts);
            break;

        case LIST_PROCESS_DELETED:
            mListParser = new ListStringHandler(getReader(), this, ProcessSOAPResponseConstants.UID);
            getReader().setContentHandler(mListParser);
            mListParser.startElement(uri, localName, qName, atts);
            break;

        case LIST_ADD_OBJECTS:
            mParser = newInstanceParser(getReader(), this);
            getReader().setContentHandler(mParser);
            mParser.startElement(uri, localName, qName, atts);
            break;

        default:
            break;
        }
    }

    protected abstract BaseXmlParser<List<DATA>> newInstanceParser(XMLReader reader, DefaultHandler defaultHandler);

    @Override
    protected void onEndElement(String uri, String localName, String qName) throws SAXException {
        switch (mElementType) {
        case ERROR_CODE:
            getData().setErrorCode(Integer.parseInt(mStringBuilder.toString()));
            break;

        case ERROR_STRING:
            getData().setMessage(mStringBuilder.toString());

            if (getData().getError() != LTServerError.NO_ERROR) {
                throw LeaderException.create(getData());
            }
            break;

        case LIST_DELETE_OBJECTS:
            if (mListParser != null) {
                getData().setDelete(mListParser.getData());
                mListParser = null;
            }
            break;

        case LIST_SEND_OBJECTS:
            if (mListParser != null) {
                getData().setSend(mListParser.getData());
                mListParser = null;
            }
            break;

        case LIST_PROCESS_DELETED:
            if (mListParser != null) {
                getData().setProcess(mListParser.getData());
                mListParser = null;
            }
            break;

        case LIST_ADD_OBJECTS:
            if (mParser != null) {
                getData().setAdd(mParser.getData());
                mParser = null;
            }
            break;

        default:
            break;
        }

        mElementType = ElementType.NONE;
    }

    @Override
    protected void onCharacters(char[] ch, int start, int length) throws SAXException {
        for (int i = start; i < length; i++) {
            mStringBuilder.append(ch[i]);
        }
    }

    private static enum ElementType {
        NONE, ERROR_CODE, ERROR_STRING, LIST_DELETE_OBJECTS, LIST_SEND_OBJECTS, LIST_PROCESS_DELETED, LIST_ADD_OBJECTS;

        public static ElementType getElementType(String element) {
            if (ProcessSOAPResponseConstants.ERROR_CODE.equalsIgnoreCase(element)) {
                return ERROR_CODE;
            }

            if (ProcessSOAPResponseConstants.ERROR_STRING.equalsIgnoreCase(element)) {
                return ERROR_STRING;
            }

            if (ProcessSOAPResponseConstants.LIST_DELETE_OBJECTS.equalsIgnoreCase(element)) {
                return LIST_DELETE_OBJECTS;
            }

            if (ProcessSOAPResponseConstants.LIST_SEND_OBJECTS.equalsIgnoreCase(element)) {
                return LIST_SEND_OBJECTS;
            }

            if (ProcessSOAPResponseConstants.LIST_PROCESS_DELETED.equalsIgnoreCase(element)) {
                return LIST_PROCESS_DELETED;
            }

            if (ProcessSOAPResponseConstants.LIST_ADD_OBJECTS.equalsIgnoreCase(element)) {
                return LIST_ADD_OBJECTS;
            }

            return NONE;
        }
    }
}