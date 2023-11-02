package com.ashberrysoft.leadertask.modern.xml_handlers.entity_process;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser.PremierEntityUnloader;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_plenty.LTaskPlentyParser;

public class LTaskProcessParser extends BaseProcessEntityParser<LTask> {

    public LTaskProcessParser(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected BaseXmlParser<List<LTask>> newInstanceParser(XMLReader reader, DefaultHandler defaultHandler) {
        return new LTaskPlentyParser(reader, defaultHandler);
    }
}