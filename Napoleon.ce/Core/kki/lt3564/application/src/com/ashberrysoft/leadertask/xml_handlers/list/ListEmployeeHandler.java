package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleEmployeeHandler;

/**
 * 
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListEmployeeHandler extends BaseListLionEntityHandler<Employee> {

    public ListEmployeeHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, EmployeeContract.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<Employee> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleEmployeeHandler(reader, defaultHandler);
    }
}