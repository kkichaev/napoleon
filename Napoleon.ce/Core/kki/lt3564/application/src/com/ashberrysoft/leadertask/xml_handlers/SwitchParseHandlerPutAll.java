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
import com.ashberrysoft.leadertask.xml_handlers.unique.VerifyUserHandler;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SwitchParseHandlerPutAll extends BaseXmlSaxHandlerPutAll {


    private boolean wasContacts = false;

    private enum ResponseType {
        NONE(SharedStrings.EMPTY),
        // PROCESS RESPONCES
        PUT_FILES("files"),
        PUT_TASKS_FILES("task_files"),
        PUT_TASKS("tasks"),
        PUT_TASKS_MESSAGES("task_msgs"),
        PUT_MARKERS("markers"),
        PUT_PROJECTS("prjs"),
        PUT_CATEGORIES("tags"),
        PUT_EMPS("emps"),
        PUT_EMPS_FOTO("emps_fotos"),
        PUT_CONTACT_GROUPS("contacts_groups"),
        PUT_CONTACTS("contacts"),
        PUT_CONTACTS_FILES("contact_files"),
        PUT_CONTACTS_FOTO("contact_fotos"),

        PUT_FILTERS("filters"),// ссаный костыль

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

    /**
     * Return that you want
     *
     * @throws Exception
     */
    public static SwitchParseHandlerPutAll newInstance(Reader is) throws Exception {
        try {
            final SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
            final XMLReader reader = sp.getXMLReader();

            final long start = System.currentTimeMillis();

            final SwitchParseHandlerPutAll handler = new SwitchParseHandlerPutAll(reader);
            reader.setContentHandler(handler);
            reader.parse(new InputSource(is));

            Utils.toLog("\tSwitchParseHandler parse time = " + (System.currentTimeMillis() - start));

            return handler;

        } catch (Exception e) {
            CursorySyncLogger.getInstance(null).toLog(e);
            throw e;
        }
    }

    private SwitchParseHandlerPutAll(XMLReader reader) {
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
                 switch (mType)
                {
                    case PUT_CATEGORIES :
                        mDataCategories = (BasePutListLionEntityHandler.BaseLionPutEntity<Category>) mHandler.getData();
                        break;
                    case PUT_PROJECTS :
                        mDataProjects = (BasePutListLionEntityHandler.BaseLionPutEntity<Project>) mHandler.getData();
                        break;
                    case PUT_CONTACTS:
                        if (!wasContacts){ // ссаный костыль
                            mDataContacts = (BasePutListLionEntityHandler.BaseLionPutEntity<Contact>) mHandler.getData();
                            wasContacts = true;
                        }
                        break;
                    case PUT_CONTACT_GROUPS:
                        mDataContactGroups = (BasePutListLionEntityHandler.BaseLionPutEntity<ContactsGroup>) mHandler.getData();
                        break;

                    case PUT_CONTACTS_FILES:
                        mDataContactFiles = (BasePutListLionEntityHandler.BaseLionPutEntity<ContactFile>) mHandler.getData();
                        break;

                    case PUT_FILTERS:
                        // ссаный костыль
                        break;

                    case PUT_MARKERS:
                        mDataMarkers = (BasePutListLionEntityHandler.BaseLionPutEntity<Marker>) mHandler.getData();
                        break;

                    case PUT_TASKS:
                        mDataTasks = (BasePutListLionEntityHandler.BaseLionPutEntity<LTask>) mHandler.getData();
                        break;

                    case PUT_TASKS_MESSAGES:
                        mDataTaskMessages = (BasePutListLionEntityHandler.BaseLionPutEntity<TaskMessage>) mHandler.getData();
                        break;

                    case PUT_TASKS_FILES:
                        mDataTaskFiles = (BasePutListLionEntityHandler.BaseLionPutEntity<TaskFile>) mHandler.getData();
                        break;

                    case PUT_EMPS:
                        mDataEmps = (BasePutListLionEntityHandler.BaseLionPutEntity<Emp>) mHandler.getData();
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private BaseXmlSaxHandlerProcessAll newInstanceHandler() throws SAXException {
        Utils.toLog(mType.toString());

        switch (mType) {
        case PUT_FILTERS:
            // ссаный костыль
            return new ProcessFilterHandler(mReader, this);

        case PUT_FILES:
            return new PutFileHandler(mReader, this);

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

        case PUT_EMPS:
            return new PutEmpHandler(mReader, this);

        case VERIFY_USER:
            return new VerifyUserHandler(mReader, this);

        case PUT_CONTACT_GROUPS:
            return new PutContactsGroupHandler(mReader, this);

        case PUT_CONTACTS:
            return new PutContactHandler(mReader, this);

        case PUT_CONTACTS_FILES:
            return new PutContactFileHandler(mReader, this);

        default:
            throw new SAXException(RESPONSE_TYPE_ERROR);
        }
    }
}