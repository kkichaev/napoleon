package com.ashberrysoft.leadertask.xml_handlers.put;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListContactHandler;

/**
 * 2014-06-18
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutContactHandler extends BasePutListLionEntityHandler<Contact> {

    public PutContactHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Contact>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                         DefaultHandler defaultHandler) {
        return new ListContactHandler(reader, defaultHandler);
    }
}