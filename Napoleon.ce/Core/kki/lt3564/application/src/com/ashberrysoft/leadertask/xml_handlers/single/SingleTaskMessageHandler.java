package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.TaskMessage;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleTaskMessageHandler extends BaseSingleLionEntityHandler<TaskMessage> {

    public SingleTaskMessageHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected TaskMessage getEntityConstructor() {
        return new TaskMessage();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(10);

        map.put(TaskMessage.FIELD_UID, false);
        map.put(TaskMessage.FIELD_CREATOR, false);
        map.put(TaskMessage.FIELD_MESSAGE, false);
        map.put(TaskMessage.FIELD_IS_DELETED, false);
        map.put(TaskMessage.FIELD_TASK_UID, false);
        map.put(TaskMessage.FIELD_DATE_CREATE, false);
        map.put(TaskMessage.FIELD_DATE_MODIFY, false);
        map.put(TaskMessage.FIELD_USN, false);
        map.put(TaskMessage.FIELD_USN_MESSAGE, false);
        map.put(TaskMessage.FIELD_USN_IS_DELETED, false);

        return map;
    }
}