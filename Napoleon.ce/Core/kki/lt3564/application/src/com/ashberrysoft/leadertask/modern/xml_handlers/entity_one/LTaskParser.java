package com.ashberrysoft.leadertask.modern.xml_handlers.entity_one;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.modern.domains.lion.LTask;

public class LTaskParser extends BaseOneEntityParser<LTask> {

    public LTaskParser(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, new LTask());
    }
}