package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleTaskMessageHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListTaskMessageHandler extends BaseListLionEntityHandler<TaskMessage> {

    public ListTaskMessageHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, TaskMessage.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<TaskMessage> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleTaskMessageHandler(reader, defaultHandler);
    }
}