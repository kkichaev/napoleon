package com.ashberrysoft.leadertask.modern.xml_handlers.entity_put;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.interfaces.PutSOAPResponseConstants;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser.PremierEntityUnloader;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PutEntityHolder;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.list.ListStringHandler;

public abstract class BasePutEntityParser<DATA> extends BaseXmlParser<PutEntityHolder<DATA>> {

    // BASE
    private final PremierEntityUnloader<DATA> mUnloader;

    // VALUE's
    private final StringBuilder mStringBuilder;
    private ElementType mElementType;
    private BaseXmlParser<List<DATA>> mParser;
    private ListStringHandler mListParser;

    public BasePutEntityParser(XMLReader reader, DefaultHandler defaultHandler, PremierEntityUnloader<DATA> unloader) {
        super(reader, defaultHandler);

        mUnloader = unloader;

        setData(new PutEntityHolder<DATA>());
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

        case LIST_CHANGE_OBJECTS:
            mParser = newInstanceParser(getReader(), this, mUnloader);
            getReader().setContentHandler(mParser);
            mParser.startElement(uri, localName, qName, atts);
            break;

        case LIST_FAILED_OBJECTS:
            mListParser = new ListStringHandler(getReader(), this, PutSOAPResponseConstants.UID);
            getReader().setContentHandler(mListParser);
            mListParser.startElement(uri, localName, qName, atts);
            break;

        case LIST_DELETE_OBJECTS:
            mListParser = new ListStringHandler(getReader(), this, null);
            getReader().setContentHandler(mListParser);
            mListParser.startElement(uri, localName, qName, atts);
            break;

        default:
            break;
        }
    }

    protected abstract BaseXmlParser<List<DATA>> newInstanceParser(XMLReader reader, DefaultHandler defaultHandler,
            PremierEntityUnloader<DATA> unloader);

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

        case LIST_CHANGE_OBJECTS:
            if (mParser != null) {
                getData().setChange(mParser.getData());
                mParser = null;
            }
            break;

        case LIST_FAILED_OBJECTS:
            if (mListParser != null) {
                getData().setFailed(mListParser.getData());
                mListParser = null;
            }
            break;

        case LIST_DELETE_OBJECTS:
            if (mListParser != null) {
                getData().setDelete(mListParser.getData());
                mListParser = null;
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
        NONE, ERROR_CODE, ERROR_STRING, LIST_CHANGE_OBJECTS, LIST_FAILED_OBJECTS, LIST_DELETE_OBJECTS;

        public static ElementType getElementType(String element) {
            if (PutSOAPResponseConstants.ERROR_CODE.equalsIgnoreCase(element)) {
                return ERROR_CODE;
            }

            if (PutSOAPResponseConstants.ERROR_STRING.equalsIgnoreCase(element)) {
                return ERROR_STRING;
            }

            if (PutSOAPResponseConstants.LIST_CHANGE_OBJECTS.equalsIgnoreCase(element)) {
                return LIST_CHANGE_OBJECTS;
            }

            if (PutSOAPResponseConstants.LIST_FAILED_OBJECTS.equalsIgnoreCase(element)) {
                return LIST_FAILED_OBJECTS;
            }

            if (PutSOAPResponseConstants.LIST_DELETE_OBJECTS.equalsIgnoreCase(element)) {
                return LIST_DELETE_OBJECTS;
            }

            return NONE;
        }
    }
}