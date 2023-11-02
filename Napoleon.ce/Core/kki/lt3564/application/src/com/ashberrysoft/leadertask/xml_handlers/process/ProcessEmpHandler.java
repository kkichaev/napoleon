package com.ashberrysoft.leadertask.xml_handlers.process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListEmpHandler;

/**
 * 2014-06-19
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessEmpHandler extends BaseProcessListLionEntityHandler<Emp> {

    public ProcessEmpHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Emp>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                         DefaultHandler defaultHandler) {
        return new ListEmpHandler(reader, defaultHandler);
    }
}