package com.ashberrysoft.leadertask.xml_handlers.list;

import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleContactsGroupHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListContactsGroupHandler extends BaseListLionEntityHandler<ContactsGroup> {

    public ListContactsGroupHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, ContactsGroup.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<ContactsGroup> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleContactsGroupHandler(reader, defaultHandler);
    }
}