package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleProjectHandler extends BaseSingleLionEntityHandler<Project> {

    public SingleProjectHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected Project getEntityConstructor() {
        return new Project();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(23);

        map.put(Project.FIELD_UID, false);
        map.put(Project.FIELD_CREATOR, false);
        map.put(Project.FIELD_NAME, false);
        map.put(Project.FIELD_UID_PARENT, false);
        map.put(Project.FIELD_COMMENT, false);
        map.put(Project.FIELD_ORDER, false);
        map.put(Project.FIELD_COLLAPSED, false);
        map.put(Project.FIELD_GROUP, false);
        map.put(Project.FIELD_SHOW, false);
        map.put(Project.FIELD_FAVORITE, false);
        map.put(Project.FIELD_IS_CLOSED, false);
        map.put(Project.FIELD_LIST_MEMBERS, false);
        map.put(Project.FIELD_USN, false);
        map.put(Project.FIELD_USN_NAME, false);
        map.put(Project.FIELD_USN_UID_PARENT, false);
        map.put(Project.FIELD_USN_COMMENT, false);
        map.put(Project.FIELD_USN_ORDER, false);
        map.put(Project.FIELD_USN_COLLAPSED, false);
        map.put(Project.FIELD_USN_GROUP, false);
        map.put(Project.FIELD_USN_SHOW, false);
        map.put(Project.FIELD_USN_FAVORITE, false);
        map.put(Project.FIELD_USN_ISCLOSED, false);
        map.put(Project.FIELD_USN_LIST_MEMBERS, false);
        map.put(Project.FIELD_QUIET, false);
        map.put(Project.FIELD_USN_QUIET, false);

        return map;
    }
}