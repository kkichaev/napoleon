package com.ashberrysoft.leadertask.xml_handlers.unique;

import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class ChangePasswordHandler extends BaseXmlSaxHandlerProcessAll<ErrorEntity>//
        implements ProcessSOAPResponseConstants {

    private StringBuilder mStringBuilder;

    public ChangePasswordHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
        mData = new ErrorEntity();
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

    public int getErrorCode() {
        return mData.getErrorCode();
    }
}