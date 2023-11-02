package com.ashberrysoft.leadertask.xml_handlers.process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.xml_handlers.BaseProcessListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.list.ListMarkerHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ProcessMarkerHandler extends BaseProcessListLionEntityHandler<Marker> {

    public ProcessMarkerHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<List<Marker>> getBaseProcessListLionEntityHandler(XMLReader reader,
                                                                                            DefaultHandler defaultHandler) {
        return new ListMarkerHandler(reader, defaultHandler);
    }
}