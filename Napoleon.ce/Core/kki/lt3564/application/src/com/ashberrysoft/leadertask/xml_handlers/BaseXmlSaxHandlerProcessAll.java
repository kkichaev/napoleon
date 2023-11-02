package com.ashberrysoft.leadertask.xml_handlers;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler.BaseLionProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.list.ListTaskHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactFotoHandler;
import com.ashberrysoft.leadertask.xml_handlers.SimpleProcessHandler.SimpleProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler;

import static android.R.attr.id;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class BaseXmlSaxHandlerProcessAll<T> extends DefaultHandler {

    protected XMLReader mReader;
    protected DefaultHandler mDefaultHandler;
    protected int mDepth = 0;
    protected BaseLionProcessEntity<Category> mDataCategories;
    protected BaseLionProcessEntity<Project> mDataProjects;
    protected BaseLionProcessEntity<Contact> mDataContacts;
    protected BaseLionProcessEntity<ContactsGroup> mDataContactGroups;
    protected BaseLionProcessEntity<ContactFile> mDataContactFiles;
    protected ProcessContactFotoHandler.SimpleProcessEntity mDataContactFotos;
    protected BaseLionProcessEntity<Marker> mDataMarkers;
    protected BaseLionProcessEntity<LTask> mDataTasks;
    protected BaseLionProcessEntity<TaskMessage> mDataTaskMessages;
    protected SimpleProcessEntity mDataFiles;
    protected BaseLionProcessEntity<TaskFile> mDataTaskFiles;
    protected BaseLionProcessEntity<Emp> mDataEmps;
    protected ProcessEmpFotoHandler.SimpleProcessEntity mDataEmpsFoto;
    protected String mSessionOrder;
    protected T mData;

    private StringBuilder mStringBuilder;

    public static final String SESSION_ORDER = "str_order";
    public boolean wasError = false;

    public BaseXmlSaxHandlerProcessAll(XMLReader reader, DefaultHandler defaultHandler) {
        mReader = reader;
        mDefaultHandler = defaultHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (SESSION_ORDER.equalsIgnoreCase(localName)) {
            mStringBuilder = new StringBuilder();
        }

        mDepth++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        mDepth--;

        if (0 == mDepth) {
            if (mDefaultHandler != null) {
                mDefaultHandler.endElement(uri, localName, qName);
                mReader.setContentHandler(mDefaultHandler);
                /*try {
                    if (localName.equals("LionTask"))
                        if (((ListTaskHandler) mDefaultHandler).getData().size() > 209){
                            int m = 0;
                            m++;
                        }
                } finally {

                }*/
            }
        }

        /*if (localName.equals("LionTask")) {
            if (qName.equals("Comment")) {
                int n = 0;
                n++;
            }
        }*/



        if (SESSION_ORDER.equalsIgnoreCase(localName)) {
            mSessionOrder = mStringBuilder.toString();
            mStringBuilder = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
        super.characters(ch, start, length);

        if (mStringBuilder != null) {
            for (int i = start; i < length; i++) {
                mStringBuilder.append(ch[i]);
            }
        }

        } finally {

        }
    }

    public BaseLionProcessEntity<Category> getDataCategory() {
        return mDataCategories;
    }

    public BaseLionProcessEntity<Project> getDataProjects() {
        return mDataProjects;
    }

    public BaseLionProcessEntity<Contact> getDataContacts() {
        return mDataContacts;
    }

    public BaseLionProcessEntity<ContactsGroup> getDataContactGroups() {
        return mDataContactGroups;
    }

    public BaseLionProcessEntity<ContactFile> getDataContactFiles() {
        return mDataContactFiles;
    }

    public BaseLionProcessEntity<Marker> getDataMarkers() {
        return mDataMarkers;
    }

    public BaseLionProcessEntity<LTask> getDataTasks() {
        return mDataTasks;
    }

    public BaseLionProcessEntity<TaskMessage> getDataTaskMessages() {
        return mDataTaskMessages;
    }

    public SimpleProcessEntity getDataFiles() {
        return mDataFiles;
    }

    public BaseLionProcessEntity<TaskFile> getDataTaskFiles() {
        return mDataTaskFiles;
    }

    public BaseLionProcessEntity<Emp> getDataEmps() {
        return mDataEmps;
    }

    public ProcessEmpFotoHandler.SimpleProcessEntity getDataEmpsFotos() {
        return mDataEmpsFoto;
    }

    public String getSessionOrder() {
        return mSessionOrder;
    }

    public ProcessContactFotoHandler.SimpleProcessEntity getDataContactFotos() {
        return mDataContactFotos;
    }

    public T getData() {
        return mData;
    }
}