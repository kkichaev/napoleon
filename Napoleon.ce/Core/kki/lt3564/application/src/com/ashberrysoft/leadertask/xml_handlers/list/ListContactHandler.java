package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleContactHandler;

/**
 * 2014-06-18
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListContactHandler extends BaseListLionEntityHandler<Contact> {

    public ListContactHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, ContactContract.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<Contact> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleContactHandler(reader, defaultHandler);
    }
}