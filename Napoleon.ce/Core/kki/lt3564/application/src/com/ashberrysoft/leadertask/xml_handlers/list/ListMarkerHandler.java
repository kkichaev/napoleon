package com.ashberrysoft.leadertask.xml_handlers.list;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.xml_handlers.BaseListLionEntityHandler;
import com.ashberrysoft.leadertask.xml_handlers.BaseXmlSaxHandlerProcessAll;
import com.ashberrysoft.leadertask.xml_handlers.single.SingleMarkerHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class ListMarkerHandler extends BaseListLionEntityHandler<Marker> {

    public ListMarkerHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, Marker.SERVER_CLASS);
    }

    @Override
    protected BaseXmlSaxHandlerProcessAll<Marker> getBaseLionEntityHandler(XMLReader reader, DefaultHandler defaultHandler) {
        return new SingleMarkerHandler(reader, defaultHandler);
    }
}