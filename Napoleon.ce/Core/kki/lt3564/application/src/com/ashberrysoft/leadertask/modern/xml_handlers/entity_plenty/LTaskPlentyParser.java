package com.ashberrysoft.leadertask.modern.xml_handlers.entity_plenty;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.BaseXmlParser;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_base.PremierEntityParser.PremierEntityUnloader;
import com.ashberrysoft.leadertask.modern.xml_handlers.entity_one.LTaskParser;

public class LTaskPlentyParser extends BasePlentyEntityParser<LTask> {

    public LTaskPlentyParser(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler, LTaskContract.TABLE_NAME);
    }

    @Override
    protected BaseXmlParser<LTask> newInstanceParser(XMLReader reader, DefaultHandler defaultHandler) {
        return new LTaskParser(reader, defaultHandler);
    }
}