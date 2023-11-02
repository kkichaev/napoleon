package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleCategoryHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListCategoryHandler extends BaseListLionEntityHandler<Category> {

    public ListCategoryHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, Category.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<Category> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleCategoryHandler(reader, defaultHandler);
    }
}