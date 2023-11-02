package com.ashberrysoft.leadertask.xml_handlers.put;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListTaskHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutTaskHandler extends BasePutListLionEntityHandler<LTask> {

    public PutTaskHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<LTask>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                       DefaultHandler defaultHandler) {
        return new ListTaskHandler(reader, defaultHandler);
    }
}