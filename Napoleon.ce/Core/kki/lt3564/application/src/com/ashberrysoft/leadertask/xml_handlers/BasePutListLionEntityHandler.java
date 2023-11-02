package com.ashberrysoft.leadertask.xml_handlers;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.interfaces.PutSOAPResponseConstants;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.list.ListStringHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public abstract class BasePutListLionEntityHandler<T> extends BaseXmlSaxHandlerProcessAll<BaseLionPutEntity<T>> implements
        PutSOAPResponseConstants {

    protected StringBuilder mStringBuilder;
    protected ListStringHandler mStringHandler;
    protected BaseXmlSaxHandlerProcessAll<List<T>> mBasePutListLionEntityHandler;

    public BasePutListLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
        mData = new BaseLionPutEntity<T>();
    }

    protected abstract BaseXmlSaxHandlerProcessAll<List<T>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                            DefaultHandler defaultHandler);

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        if (ERROR_CODE.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (ERROR_STRING.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (LIST_CHANGE_OBJECTS.equalsIgnoreCase(localName)) {
            mBasePutListLionEntityHandler = getBasePutListLionEntityHandler(mReader, this);
            mReader.setContentHandler(mBasePutListLionEntityHandler);
            mBasePutListLionEntityHandler.startElement(uri, localName, qName, atts);
        }

        else if (LIST_FAILED_OBJECTS.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, UID);
            mReader.setContentHandler(mStringHandler);
            mStringHandler.startElement(uri, localName, qName, atts);
        }

        else if (LIST_DELETE_OBJECTS.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, null);
            mReader.setContentHandler(mStringHandler);
            mStringHandler.startElement(uri, localName, qName, atts);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (ERROR_CODE.equalsIgnoreCase(localName)) {
            mData.setErrorCode(Integer.parseInt(mStringBuilder.toString()));
            mStringBuilder = null;
        }

        else if (ERROR_STRING.equalsIgnoreCase(localName)) {
            mData.setMessage(mStringBuilder.toString());
            mStringBuilder = null;

            if (mData.getError() != LTServerError.NO_ERROR) {
                throw LeaderException.create(getData());
            }
        }

        else if (LIST_CHANGE_OBJECTS.equalsIgnoreCase(localName)) {
            if (mBasePutListLionEntityHandler != null) {
                mData.setListChange(mBasePutListLionEntityHandler.getData());
                mBasePutListLionEntityHandler = null;
            }
        }

        else if (LIST_FAILED_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListFailed(mStringHandler.getData());
                mStringHandler = null;
            }
        }

        else if (LIST_DELETE_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListDelete(mStringHandler.getData());
                mStringHandler = null;
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

    /**
     * 
     * @author Tregub Artem tregub.artem@gmail.com
     */
    public static class BaseLionPutEntity<T> extends ErrorEntity {

        private static final long serialVersionUID = 1L;

        private List<T> mListChange;
        private List<String> mListFailed;
        private List<String> mListDelete;

        public List<T> getListChange() {
            return mListChange;
        }

        public void setListChange(List<T> listChange) {
            mListChange = listChange;
        }

        public List<String> getListFailed() {
            return mListFailed;
        }

        public void setListFailed(List<String> listFailed) {
            mListFailed = listFailed;
        }

        public List<String> getListDelete() {
            return mListDelete;
        }

        public void setListDelete(List<String> listDelete) {
            mListDelete = listDelete;
        }
    }
}