package com.ashberrysoft.leadertask.xml_handlers;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.xml_handlers.SimpleProcessHandler.SimpleProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.list.ListStringHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SimpleProcessHandler extends BaseXmlSaxHandlerProcessAll<SimpleProcessEntity> implements
        ProcessSOAPResponseConstants {

    private StringBuilder mStringBuilder;
    private ListStringHandler mStringHandler;

    public SimpleProcessHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
        mData = new SimpleProcessEntity();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        if (ERROR_CODE.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (ERROR_STRING.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        else if (LIST_DELETE_OBJECTS.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, null);
            mReader.setContentHandler(mStringHandler);
            mStringHandler.startElement(uri, localName, qName, atts);
        }

        else if (LIST_SEND_OBJECTS.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, null);
            mReader.setContentHandler(mStringHandler);
            mStringHandler.startElement(uri, localName, qName, atts);
        }

        else if (LIST_PROCESS_DELETED.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, UID);
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

        else if (LIST_DELETE_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListDelete(mStringHandler.getData());
                mStringHandler = null;
            }
        }

        else if (LIST_SEND_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListSend(mStringHandler.getData());
                mStringHandler = null;
            }
        }

        else if (LIST_PROCESS_DELETED.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListProcess(mStringHandler.getData());
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
    public static class SimpleProcessEntity extends ErrorEntity {

        private static final long serialVersionUID = 1L;

        private List<String> mListDelete;
        private List<String> mListSend;
        private List<String> mListProcess;

        public List<String> getListDelete() {
            return mListDelete;
        }

        public void setListDelete(List<String> listDelete) {
            mListDelete = listDelete;
        }

        public List<String> getListSend() {
            return mListSend;
        }

        public void setListSend(List<String> listSend) {
            mListSend = listSend;
        }

        public List<String> getListProcess() {
            return mListProcess;
        }

        public void setListProcess(List<String> listProcess) {
            mListProcess = listProcess;
        }
    }
}