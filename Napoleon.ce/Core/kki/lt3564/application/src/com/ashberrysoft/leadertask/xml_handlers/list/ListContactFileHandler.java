package com.ashberrysoft.leadertask.xml_handlers.list;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleContactFileHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListContactFileHandler extends BaseListLionEntityHandler<ContactFile> {

    public ListContactFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, ContactsFileContract.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<ContactFile> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleContactFileHandler(reader, defaultHandler);
    }
}