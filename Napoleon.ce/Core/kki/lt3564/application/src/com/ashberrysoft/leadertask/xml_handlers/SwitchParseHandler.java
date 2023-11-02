package com.ashberrysoft.leadertask.xml_handlers;

import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ashberrysoft.leadertask.utils.CursorySyncLogger;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessCategoryHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactFotoHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactsFileHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactsGroupHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessMarkerHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessProjectHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessTaskFileHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessTaskHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessTaskMessageHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutCategoryHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutContactFileHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutContactHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutContactsGroupHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutEmpHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutFileHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutMarkerHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutProjectHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutTaskFileHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutTaskHandler;
import com.ashberrysoft.leadertask.xml_handlers.put.PutTaskMessageHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.ChangePasswordHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.ClearSessionChangesHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.CreateSessionHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.VerifyUserHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SwitchParseHandler<T> extends BaseXmlSaxHandlerProcessAll<T> {

    private enum ResponseType {
        NONE(SharedStrings.EMPTY),
        // PROCESS RESPONCES
        PROCESS_FILES("ProcessFilesResponse"), PROCESS_TASKS_FILES("ProcessTasksFilesResponse"), //
        PROCESS_TASKS("ProcessTasksResponse"), PROCESS_TASKS_MESSAGES("ProcessTasksMessagesResponse"), //
        PROCESS_TASKS_LABELS("ProcessTasksLabelsResponse"), PROCESS_MARKERS("ProcessMarkersResponse"), //
        PROCESS_PROJECTS("ProcessProjectsResponse"), PROCESS_CATEGORIES("ProcessCategoriesResponse"), //
        PROCESS_LABELS("ProcessLabelsResponse"), PROCESS_EMPS("ProcessEmpsResponse"),
        PROCESS_EMPS_FOTO("ProcessEmpsFotosResponse"), PROCESS_CONTACT_GROUPS("ProcessContactsGroupsResponse"),
        PROCESS_CONTACTS("ProcessContactsResponse"), PROCESS_CONTACTS_FILES("ProcessContactsFilesResponse"),
        PROCESS_CONTACTS_FOTO("ProcessContactsFotosResponse"),

        // PUT RESPONCES
        PUT_FILES("PutFilesResponse"), PUT_TASKS_FILES("PutTasksFilesResponse"), //
        PUT_CATEGORIES("PutCategoriesResponse"), PUT_TASKS_LABELS("PutTasksLabelsResult"), //
        PUT_LABELS("PutLabelsResponse"), PUT_MARKERS("PutMarkersResponse"), //
        PUT_PROJECTS("PutProjectsResponse"), PUT_TASKS_MESSAGES("PutTasksMessagesResponse"), //
        PUT_TASKS("PutTasksResponse"), PUT_EMPS("PutEmpsResponse"),
        PUT_CONTACT_GROUPS("PutContactGroupsResponse"),
        PUT_CONTACTS("PutContactsResponse"), PUT_CONTACTS_FILES("PutContactsFilesResponse"),
        // UNIQUE
        CREATE_SESSION("CreateSessionResponse"),
        CLEAR_SESSION_CHANGES("ClearSessionChanges"),
        CHANGE_PASSWORD("ChangePasswordResponse"),
        VERIFY_USER("VerifyUserResponse");

        private String mString;

        private ResponseType(String s) {
            mString = s;
        }

        @Override
        public String toString() {
            return mString;
        }

        public static ResponseType getType(String s) {
            for (ResponseType type : values()) {
                if (type.toString().equalsIgnoreCase(s)) {
                    return type;
                }
            }
            return NONE;
        }
    }

    private static final String RESPONSE_TYPE_ERROR = "In newDefaultHandlerInstance missing constructor for ResponseType";

    private BaseXmlSaxHandlerProcessAll<?> mHandler;
    private ResponseType mType;

    /**
     * Return that you want
     * 
     * @throws Exception
     */
    public static <T> SwitchParseHandler<T> newInstance(Reader is) throws Exception {
        try {
            final SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            final XMLReader reader = sp.getXMLReader();

            final long start = System.currentTimeMillis();
            
            final SwitchParseHandler<T> handler = new SwitchParseHandler<T>(reader);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(is));
            
            Utils.toLog("\tSwitchParseHandler parse time = " + (System.currentTimeMillis() - start));

            return handler;

        } catch (Exception e) {
            CursorySyncLogger.getInstance(null).toLog(e);
            throw e;
        }
    }

    private SwitchParseHandler(XMLReader reader) {
        super(reader, null);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);

        mType = ResponseType.getType(localName);
        if (mType != ResponseType.NONE) {
            mHandler = newInstanceHandler();
            mReader.setContentHandler(mHandler);
            mHandler.startElement(uri, localName, qName, atts);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (ResponseType.getType(localName) == mType) {
            if (mHandler != null) {
                mData = (T) mHandler.getData();
            }
        }
    }

    private BaseXmlSaxHandlerProcessAll<?> newInstanceHandler() throws SAXException {
        Utils.toLog(mType.toString());

        switch (mType) {
        case PUT_FILES:
            return new PutFileHandler(mReader, this);

        case PROCESS_EMPS_FOTO:
            return new ProcessEmpFotoHandler(mReader, this);

        case PROCESS_CONTACTS_FOTO:
            return new ProcessContactFotoHandler(mReader, this);

        case PROCESS_FILES:
            return new SimpleProcessHandler(mReader, this);

        case PROCESS_TASKS_FILES:
            return new ProcessTaskFileHandler(mReader, this);

        case PUT_TASKS_FILES:
            return new PutTaskFileHandler(mReader, this);

        case PUT_CATEGORIES:
            return new PutCategoryHandler(mReader, this);

        case PUT_MARKERS:
            return new PutMarkerHandler(mReader, this);

        case PUT_PROJECTS:
            return new PutProjectHandler(mReader, this);

        case PUT_TASKS_MESSAGES:
            return new PutTaskMessageHandler(mReader, this);

        case PUT_TASKS:
            return new PutTaskHandler(mReader, this);

        case PROCESS_TASKS:
            return new ProcessTaskHandler(mReader, this);

        case PROCESS_TASKS_MESSAGES:
            return new ProcessTaskMessageHandler(mReader, this);

        case PROCESS_MARKERS:
            return new ProcessMarkerHandler(mReader, this);

        case PROCESS_PROJECTS:
            return new ProcessProjectHandler(mReader, this);

        case PROCESS_CATEGORIES:
            return new ProcessCategoryHandler(mReader, this);

        case PROCESS_EMPS:
            return new ProcessEmpHandler(mReader, this);

        case PUT_EMPS:
            return new PutEmpHandler(mReader, this);

        case VERIFY_USER:
            return new VerifyUserHandler(mReader, this);

        case CREATE_SESSION:
            return new CreateSessionHandler(mReader, this);

        case CLEAR_SESSION_CHANGES:
            return new ClearSessionChangesHandler(mReader, this);

        case CHANGE_PASSWORD:
            return new ChangePasswordHandler(mReader, this);

        case PROCESS_CONTACT_GROUPS:
            return new ProcessContactsGroupHandler(mReader, this);

        case PUT_CONTACT_GROUPS:
            return new PutContactsGroupHandler(mReader, this);

        case PROCESS_CONTACTS:
            return new ProcessContactHandler(mReader, this);

        case PUT_CONTACTS:
            return new PutContactHandler(mReader, this);

        case PUT_CONTACTS_FILES:
            return new PutContactFileHandler(mReader, this);

        case PROCESS_CONTACTS_FILES:
            return new ProcessContactsFileHandler(mReader, this);

        /*case PROCESS_CONTACTS_FOTO:
            return new ProcessContactsFotoHandler(mReader, this);*/

        default:
            throw new SAXException(RESPONSE_TYPE_ERROR);
        }
    }
}