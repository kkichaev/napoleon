package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskContract;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleTaskHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListTaskHandler extends BaseListLionEntityHandler<LTask> {

    public ListTaskHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, TaskContract.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<LTask> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleTaskHandler(reader, defaultHandler);
    }
}