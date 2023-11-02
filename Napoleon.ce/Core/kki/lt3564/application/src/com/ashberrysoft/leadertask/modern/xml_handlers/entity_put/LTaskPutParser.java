package com.ashberrysoft.leadertask.modern.xml_handlers.entity_put;

import java.util.List;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser.PremierEntityUnloader;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_plenty.LTaskPlentyParser;

public class LTaskPutParser extends BasePutEntityParser<LTask> {

    public LTaskPutParser(XMLReader reader, DefaultHandler defaultHandler, PremierEntityUnloader<LTask> unloader) {
        super(reader, defaultHandler, unloader);
    }

    @Override
    protected BaseXmlParser<List<LTask>> newInstanceParser(XMLReader reader, DefaultHandler defaultHandler,
            PremierEntityUnloader<LTask> unloader) {
        return new LTaskPlentyParser(reader, defaultHandler);
    }
}