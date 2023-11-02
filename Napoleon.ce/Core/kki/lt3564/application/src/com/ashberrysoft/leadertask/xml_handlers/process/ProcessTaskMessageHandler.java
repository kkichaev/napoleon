package com.ashberrysoft.leadertask.xml_handlers.process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListTaskMessageHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessTaskMessageHandler extends BaseProcessListLionEntityHandler<TaskMessage> {

    public ProcessTaskMessageHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<TaskMessage>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                                 DefaultHandler defaultHandler) {
        return new ListTaskMessageHandler(reader, defaultHandler);
    }

}