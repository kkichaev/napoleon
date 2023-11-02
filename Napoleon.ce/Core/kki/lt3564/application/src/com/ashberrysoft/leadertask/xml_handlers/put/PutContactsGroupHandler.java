package com.ashberrysoft.leadertask.xml_handlers.put;

import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListContactsGroupHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutContactsGroupHandler extends BasePutListLionEntityHandler<ContactsGroup> {

    public PutContactsGroupHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<ContactsGroup>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                               DefaultHandler defaultHandler) {
        return new ListContactsGroupHandler(reader, defaultHandler);
    }
}