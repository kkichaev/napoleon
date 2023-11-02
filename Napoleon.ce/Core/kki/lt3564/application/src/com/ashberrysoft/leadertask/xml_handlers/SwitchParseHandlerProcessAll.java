package com.ashberrysoft.leadertask.xml_handlers;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
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
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessFilterHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessMarkerHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessProjectHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessTaskFileHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessTaskHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessTaskMessageHandler;
import com.ashberrysoft.leadertask.xml_handlers.unique.VerifyUserHandler;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SwitchParseHandlerProcessAll extends BaseXmlSaxHandlerProcessAll {


    private boolean wasContacts = false;
    public static final String ERROR = "error_code";
    private StringBuilder mStringBuilder;
    protected String mErrorCode;

    private enum ResponseType {
        NONE(SharedStrings.EMPTY),
        // PROCESS RESPONCES
        PROCESS_FILES("files"),
        PROCESS_TASKS_FILES("task_files"),
        PROCESS_TASKS("tasks"),
        PROCESS_TASKS_MESSAGES("task_msgs"),
        PROCESS_MARKERS("markers"),
        PROCESS_PROJECTS("prjs"),
        PROCESS_CATEGORIES("tags"),
        PROCESS_EMPS("emps"),
        PROCESS_EMPS_FOTO("emps_fotos"),
        PROCESS_CONTACT_GROUPS("contacts_groups"),
        PROCESS_CONTACTS("contacts"),
        PROCESS_CONTACTS_FILES("contact_files"),
        PROCESS_CONTACTS_FOTO("contact_fotos"),

        PROCESS_FILTERS("filters"),// ссаный костыль

        // UNIQUE
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


    public static SwitchParseHandlerProcessAll newInstance(Reader is) throws Exception {
        try {
            final SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            final XMLReader reader = sp.getXMLReader();

            final long start = System.currentTimeMillis();

            final SwitchParseHandlerProcessAll handler = new SwitchParseHandlerProcessAll(reader);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(is));

            Utils.toLog("\tSwitchParseHandler parse time = " + (System.currentTimeMillis() - start));

            return handler;

        } catch (Exception e) {
            CursorySyncLogger.getInstance(null).toLog(e);
            throw e;
        }
    }

    private SwitchParseHandlerProcessAll(XMLReader reader) {
        super(reader, null);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        super.startElement(uri, localName, qName, atts);


        if (ERROR.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }


        mType = ResponseType.getType(localName);
        if (mType != ResponseType.NONE) {
            mHandler = newInstanceHandler();
            mReader.setContentHandler(mHandler);
            mHandler.startElement(uri, localName, qName, atts);
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

    @SuppressWarnings("unchecked")
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        if (ERROR.equalsIgnoreCase(localName)) {
            if (!wasError) {
                wasError = true;
                mErrorCode = mStringBuilder.toString();
                mStringBuilder = null;
            }
        }

        if (ResponseType.getType(localName) == mType) {
            if (mHandler != null) {
                 switch (mType)
                {
                    case PROCESS_CATEGORIES :
                        mDataCategories = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<Category>) mHandler.getData();
                        break;
                    case PROCESS_PROJECTS :
                        mDataProjects = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<Project>) mHandler.getData();
                        break;
                    case PROCESS_CONTACTS:
                        if (!wasContacts){ // ссаный костыль
                            mDataContacts = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<Contact>) mHandler.getData();
                            wasContacts = true;
                        }
                        break;
                    case PROCESS_CONTACT_GROUPS:
                        mDataContactGroups = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<ContactsGroup>) mHandler.getData();
                        break;

                    case PROCESS_CONTACTS_FILES:
                        mDataContactFiles = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<ContactFile>) mHandler.getData();
                        break;

                    case PROCESS_CONTACTS_FOTO:
                        mDataContactFotos = (ProcessContactFotoHandler.SimpleProcessEntity) mHandler.getData();
                        break;

                    case PROCESS_FILTERS:
                        // ссаный костыль
                        int m = 0;
                        break;

                    case PROCESS_MARKERS:
                        mDataMarkers = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<Marker>) mHandler.getData();
                        break;

                    case PROCESS_TASKS:
                        mDataTasks = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<LTask>) mHandler.getData();
                        break;

                    case PROCESS_TASKS_MESSAGES:
                        mDataTaskMessages = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<TaskMessage>) mHandler.getData();
                        break;

                    case PROCESS_FILES:
                        mDataFiles = (SimpleProcessHandler.SimpleProcessEntity) mHandler.getData();
                        break;

                    case PROCESS_TASKS_FILES:
                        mDataTaskFiles = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<TaskFile>) mHandler.getData();
                        break;

                    case PROCESS_EMPS:
                        mDataEmps = (BaseProcessListLionEntityHandler.BaseLionProcessEntity<Emp>) mHandler.getData();
                        break;

                    case PROCESS_EMPS_FOTO:
                        mDataEmpsFoto = (ProcessEmpFotoHandler.SimpleProcessEntity) mHandler.getData();

                    default:
                        break;
                }
            }
        }
    }


    public String getErrorCode() {
        return mErrorCode;
    }

    private BaseXmlSaxHandlerProcessAll newInstanceHandler() throws SAXException {
        Utils.toLog(mType.toString());

        switch (mType) {
        case PROCESS_FILTERS:
            // ссаный костыль
            return new ProcessFilterHandler(mReader, this);

        /*case PUT_FILES:
            return new PutFileHandler(mReader, this);*/

        case PROCESS_EMPS_FOTO:
            return new ProcessEmpFotoHandler(mReader, this);

        case PROCESS_CONTACTS_FOTO:
            return new ProcessContactFotoHandler(mReader, this);

        case PROCESS_FILES:
            return new SimpleProcessHandler(mReader, this);

        case PROCESS_TASKS_FILES:
            return new ProcessTaskFileHandler(mReader, this);

        /*case PUT_TASKS_FILES:
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
            return new PutTaskHandler(mReader, this);*/

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

        /*case PUT_EMPS:
            return new PutEmpHandler(mReader, this);*/

        case VERIFY_USER:
            return new VerifyUserHandler(mReader, this);

        case PROCESS_CONTACT_GROUPS:
            return new ProcessContactsGroupHandler(mReader, this);

        /*case PUT_CONTACT_GROUPS:
            return new PutContactsGroupHandler(mReader, this);*/

        case PROCESS_CONTACTS:
            return new ProcessContactHandler(mReader, this);

        /*case PUT_CONTACTS:
            return new PutContactHandler(mReader, this);

        case PUT_CONTACTS_FILES:
            return new PutContactFileHandler(mReader, this);*/

        case PROCESS_CONTACTS_FILES:
            return new ProcessContactsFileHandler(mReader, this);

        default:
            throw new SAXException(RESPONSE_TYPE_ERROR);
        }
    }
}