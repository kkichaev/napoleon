package com.ashberrysoft.leadertask.xml_handlers.process;

import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListContactHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;


public class ProcessFilterHandler extends BaseProcessListLionEntityHandler<Contact> {

    public ProcessFilterHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Contact>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                             DefaultHandler defaultHandler) {
        return new ListContactHandler(reader, defaultHandler);
    }
}