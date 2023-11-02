package com.ashberrysoft.leadertask.xml_handlers.process;

import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListContactFileHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessContactsFileHandler extends BaseProcessListLionEntityHandler<ContactFile> {

    public ProcessContactsFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<ContactFile>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                                 DefaultHandler defaultHandler) {
        return new ListContactFileHandler(reader, defaultHandler);
    }

}