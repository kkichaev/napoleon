package com.ashberrysoft.leadertask.xml_handlers.process;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler.SimpleProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;
import com.ashberrysoft.leadertask.xml_handlers.list.ListStringHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by Samsung on 24.11.2015.
 */
public class ProcessEmpFotoHandler  extends BaseXmlSaxHandlerProcessAll<SimpleProcessEntity> implements
        ProcessSOAPResponseConstants {

    private StringBuilder mStringBuilder;
    private ListStringHandler mStringHandler;

    public ProcessEmpFotoHandler(XMLReader reader, DefaultHandler defaultHandler) {
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

        else if (LIST_NEED_DOWNLOAD_OBJECTS.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, null);
            mReader.setContentHandler(mStringHandler);
            mStringHandler.startElement(uri, localName, qName, atts);
        }

        else if (LIST_SEND_OBJECTS.equalsIgnoreCase(localName)) {
            mStringHandler = new ListStringHandler(mReader, this, null);
            mReader.setContentHandler(mStringHandler);
            mStringHandler.startElement(uri, localName, qName, atts);
        }

        else if (LIST_NEW_OBJECTS.equalsIgnoreCase(localName)) {
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

        else if (LIST_NEED_DOWNLOAD_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListDownload(mStringHandler.getData());
                mStringHandler = null;
            }
        }

        else if (LIST_SEND_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListSend(mStringHandler.getData());
                mStringHandler = null;
            }
        }

        else if (LIST_NEW_OBJECTS.equalsIgnoreCase(localName)) {
            if (mStringHandler != null) {
                mData.setListNew(mStringHandler.getData());
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

        private List<String> mListDownload;
        private List<String> mListSend;
        private List<String> mListNew;

        public List<String> getListDownload() {
            return mListDownload;
        }

        public void setListDownload(List<String> listDownload) {
            mListDownload = listDownload;
        }

        public List<String> getListSend() {
            return mListSend;
        }

        public void setListSend(List<String> listSend) {
            mListSend = listSend;
        }

        public List<String> getListNew() {
            return mListNew;
        }

        public void setListNew(List<String> listNew) {
            mListNew = listNew;
        }
    }
}