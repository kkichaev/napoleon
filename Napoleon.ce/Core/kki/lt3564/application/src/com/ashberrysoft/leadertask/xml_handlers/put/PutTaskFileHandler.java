package com.ashberrysoft.leadertask.xml_handlers.put;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListTaskFileHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutTaskFileHandler extends BasePutListLionEntityHandler<TaskFile> {

    public PutTaskFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<TaskFile>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                          DefaultHandler defaultHandler) {
        return new ListTaskFileHandler(reader, defaultHandler);
    }
}