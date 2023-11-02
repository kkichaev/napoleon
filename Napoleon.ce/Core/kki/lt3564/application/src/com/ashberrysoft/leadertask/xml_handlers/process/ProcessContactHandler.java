package com.ashberrysoft.leadertask.xml_handlers.process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListContactHandler;

/**
 * 2014-06-18
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessContactHandler extends BaseProcessListLionEntityHandler<Contact> {

    public ProcessContactHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Contact>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                             DefaultHandler defaultHandler) {
        return new ListContactHandler(reader, defaultHandler);
    }
}