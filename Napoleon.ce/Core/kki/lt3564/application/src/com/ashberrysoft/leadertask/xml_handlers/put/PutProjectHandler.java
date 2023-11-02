package com.ashberrysoft.leadertask.xml_handlers.put;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListProjectHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutProjectHandler extends BasePutListLionEntityHandler<Project> {

    public PutProjectHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Project>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                         DefaultHandler defaultHandler) {
        return new ListProjectHandler(reader, defaultHandler);
    }
}