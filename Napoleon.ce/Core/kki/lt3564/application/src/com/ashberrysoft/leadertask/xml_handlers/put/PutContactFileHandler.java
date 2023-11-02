package com.ashberrysoft.leadertask.xml_handlers.put;

import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListContactFileHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutContactFileHandler extends BasePutListLionEntityHandler<ContactFile> {

    public PutContactFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<ContactFile>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                             DefaultHandler defaultHandler) {
        return new ListContactFileHandler(reader, defaultHandler);
    }
}