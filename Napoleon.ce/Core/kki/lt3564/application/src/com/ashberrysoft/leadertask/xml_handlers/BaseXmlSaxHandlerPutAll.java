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
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler.BaseLionPutEntity;
import com.ashberrysoft.leadertask.xml_handlers.SimpleProcessHandler.SimpleProcessEntity;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessContactFotoHandler;
import com.ashberrysoft.leadertask.xml_handlers.process.ProcessEmpFotoHandler;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class BaseXmlSaxHandlerPutAll<T> extends DefaultHandler {

    protected XMLReader mReader;
    protected DefaultHandler mDefaultHandler;
    protected int mDepth = 0;
    protected BaseLionPutEntity<Category> mDataCategories;
    protected BaseLionPutEntity<Project> mDataProjects;
    protected BaseLionPutEntity<Contact> mDataContacts;
    protected BaseLionPutEntity<ContactsGroup> mDataContactGroups;
    protected BaseLionPutEntity<ContactFile> mDataContactFiles;
    protected BaseLionPutEntity<Marker> mDataMarkers;
    protected BaseLionPutEntity<LTask> mDataTasks;
    protected BaseLionPutEntity<TaskMessage> mDataTaskMessages;
    protected BaseLionPutEntity<TaskFile> mDataTaskFiles;
    protected BaseLionPutEntity<Emp> mDataEmps;
    protected T mData;

    public BaseXmlSaxHandlerPutAll(XMLReader reader, DefaultHandler defaultHandler) {
        mReader = reader;
        mDefaultHandler = defaultHandler;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        mDepth++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        mDepth--;
        if (0 == mDepth) {
            if (mDefaultHandler != null) {
                mDefaultHandler.endElement(uri, localName, qName);
                mReader.setContentHandler(mDefaultHandler);
            }
        }
    }

    public BaseLionPutEntity<Category> getDataCategory() {
        return mDataCategories;
    }

    public BaseLionPutEntity<Project> getDataProjects() {
        return mDataProjects;
    }

    public BaseLionPutEntity<Contact> getDataContacts() {
        return mDataContacts;
    }

    public BaseLionPutEntity<ContactsGroup> getDataContactGroups() {
        return mDataContactGroups;
    }

    public BaseLionPutEntity<ContactFile> getDataContactFiles() {
        return mDataContactFiles;
    }

    public BaseLionPutEntity<Marker> getDataMarkers() {
        return mDataMarkers;
    }

    public BaseLionPutEntity<LTask> getDataTasks() {
        return mDataTasks;
    }

    public BaseLionPutEntity<TaskMessage> getDataTaskMessages() {
        return mDataTaskMessages;
    }

    public BaseLionPutEntity<TaskFile> getDataTaskFiles() {
        return mDataTaskFiles;
    }

    public BaseLionPutEntity<Emp> getDataEmps() {
        return mDataEmps;
    }

    public T getData() {
        return mData;
    }

}