package com.ashberrysoft.leadertask.xml_handlers.unique;

import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.interfaces.ProcessSOAPResponseConstants;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;
import com.ashberrysoft.leadertask.xml_handlers.list.ListEmployeeHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.CreateSessionHandler.CreateSessionEntity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class CreateSessionHandler extends BaseXmlSaxHandlerProcessAll<CreateSessionEntity>//
        implements ProcessSOAPResponseConstants {

    private StringBuilder mStringBuilder;

    public CreateSessionHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
        mData = new CreateSessionEntity();
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

        else if (CreateSessionEntity.UID_SESSION.equalsIgnoreCase(localName)) {
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

        else if (CreateSessionEntity.UID_SESSION.equalsIgnoreCase(localName)) {
            mData.setUidSession(mStringBuilder.toString());
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

    public static class CreateSessionEntity extends ErrorEntity {
        
        private static final long serialVersionUID = 1L;

        public static final String UID_SESSION = "uid_session";

        private String mUUIDSession;

        public CreateSessionEntity() {

        }

        public String getUidSession() {
            return mUUIDSession;
        }

        public void setUidSession(String uid) {
            mUUIDSession = uid;
        }
    }
}