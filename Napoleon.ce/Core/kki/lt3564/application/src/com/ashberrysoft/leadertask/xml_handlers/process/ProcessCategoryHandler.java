package com.ashberrysoft.leadertask.xml_handlers.process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListCategoryHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessCategoryHandler extends BaseProcessListLionEntityHandler<Category> {

    public ProcessCategoryHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Category>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                              DefaultHandler defaultHandler) {
        return new ListCategoryHandler(reader, defaultHandler);
    }
}