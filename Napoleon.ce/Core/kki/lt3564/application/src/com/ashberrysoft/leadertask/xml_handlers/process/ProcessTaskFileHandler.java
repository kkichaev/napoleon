package com.ashberrysoft.leadertask.xml_handlers.process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListTaskFileHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessTaskFileHandler extends BaseProcessListLionEntityHandler<TaskFile> {

    public ProcessTaskFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<TaskFile>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                              DefaultHandler defaultHandler) {
        return new ListTaskFileHandler(reader, defaultHandler);
    }

}