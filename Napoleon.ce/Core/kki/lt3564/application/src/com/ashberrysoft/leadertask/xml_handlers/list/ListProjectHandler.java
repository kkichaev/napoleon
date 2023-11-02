package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleProjectHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListProjectHandler extends BaseListLionEntityHandler<Project> {

    public ListProjectHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, Project.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<Project> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleProjectHandler(reader, defaultHandler);
    }
}