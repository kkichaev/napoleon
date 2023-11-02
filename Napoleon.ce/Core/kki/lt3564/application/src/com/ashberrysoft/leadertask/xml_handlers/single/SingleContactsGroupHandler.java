package com.ashberrysoft.leadertask.xml_handlers.single;

import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

public class SingleContactsGroupHandler extends BaseSingleLionEntityHandler<ContactsGroup> {

    public SingleContactsGroupHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected ContactsGroup getEntityConstructor() {
        return new ContactsGroup();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(14);

        map.put(Project.FIELD_UID, false);
        map.put(Project.FIELD_CREATOR, false);
        map.put(Project.FIELD_NAME, false);
        map.put(Project.FIELD_UID_PARENT, false);
        map.put(Project.FIELD_COMMENT, false);
        map.put(Project.FIELD_ORDER, false);
        map.put(Project.FIELD_COLLAPSED, false);
        map.put(Project.FIELD_LIST_MEMBERS, false);
        map.put(Project.FIELD_USN, false);
        map.put(Project.FIELD_USN_NAME, false);
        map.put(Project.FIELD_USN_UID_PARENT, false);
        map.put(Project.FIELD_USN_COMMENT, false);
        map.put(Project.FIELD_USN_ORDER, false);
        map.put(Project.FIELD_USN_COLLAPSED, false);
        map.put(Project.FIELD_USN_LIST_MEMBERS, false);

        return map;
    }
}