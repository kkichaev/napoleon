package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleEmpHandler;

/**
 * 2014-06-19
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListEmpHandler extends BaseListLionEntityHandler<Emp> {

    public ListEmpHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, EmpContract.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<Emp> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleEmpHandler(reader, defaultHandler);
    }
}