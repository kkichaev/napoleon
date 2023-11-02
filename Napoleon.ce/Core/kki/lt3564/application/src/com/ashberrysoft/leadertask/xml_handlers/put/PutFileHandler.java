package com.ashberrysoft.leadertask.xml_handlers.put;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.modern.exception.LeaderException;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;
import com.ashberrysoft.leadertask.xml_handlers.list.ListStringHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutFileHandler.SimplePutFilesEntity;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutFileHandler extends BaseXmlSaxHandlerProcessAll<SimplePutFilesEntity> implements ProcessSOAPResponseConstants {

    private StringBuilder mStringBuilder;
    private ListStringHandler mStringHandler;

    public PutFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
        mData = new SimplePutFilesEntity();
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

        else if (LION_FILE.equalsIgnoreCase(localName)) {
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

        else if (LION_FILE.equalsIgnoreCase(localName)) {
            if (mStringHandler != null && !mStringHandler.getData().isEmpty()) {
                mData.setUsnEntity(Integer.parseInt(mStringHandler.getData().get(0)));
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
    public static class SimplePutFilesEntity extends ErrorEntity {

        private static final long serialVersionUID = 1L;

        private int mUsnEntity;

        public int getUsnEntity() {
            return mUsnEntity;
        }

        public void setUsnEntity(int usnEntity) {
            mUsnEntity = usnEntity;
        }
    }
}