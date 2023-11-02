package com.ashberrysoft.leadertask.xml_handlers.put;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.xml_handlers.BasePutListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListCategoryHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class PutCategoryHandler extends BasePutListLionEntityHandler<Category> {

    public PutCategoryHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Category>> getBasePutListLionEntityHandler(XMLReader reader,
                                                                                          DefaultHandler defaultHandler) {
        return new ListCategoryHandler(reader, defaultHandler);
    }
}