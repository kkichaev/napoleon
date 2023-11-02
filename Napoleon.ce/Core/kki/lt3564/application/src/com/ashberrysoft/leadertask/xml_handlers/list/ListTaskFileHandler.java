package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleTaskFileHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListTaskFileHandler extends BaseListLionEntityHandler<TaskFile> {

    public ListTaskFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, TaskFileContract.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<TaskFile> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleTaskFileHandler(reader, defaultHandler);
    }
}