package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleCategoryHandler extends BaseSingleLionEntityHandler<Category> {

    public SingleCategoryHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected Category getEntityConstructor() {
        return new Category();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(21, 1.0f);

        map.put(Category.FIELD_USN, false);
        map.put(Category.FIELD_USN_NAME, false);
        map.put(Category.FIELD_USN_UID_PARENT, false);
        map.put(Category.FIELD_USN_COMMENT, false);
        map.put(Category.FIELD_USN_ORDER, false);
        map.put(Category.FIELD_USN_COLLAPSED, false);
        map.put(Category.FIELD_USN_GROUP, false);
        map.put(Category.FIELD_USN_SHOW, false);
        map.put(Category.FIELD_USN_FAVORITE, false);
        map.put(Category.FIELD_UID, false);
        map.put(Category.FIELD_UID_PARENT, false);
        map.put(Category.FIELD_ORDER, false);
        map.put(Category.FIELD_COLLAPSED, false);
        map.put(Category.FIELD_GROUP, false);
        map.put(Category.FIELD_SHOW, false);
        map.put(Category.FIELD_FAVORITE, false);
        map.put(Category.FIELD_NAME, false);
        map.put(Category.FIELD_COMMENT, false);
        map.put(Category.FIELD_USN_FIELD_COLOR, false);
        map.put(Category.FIELD_COLOR, false);
        map.put(Category.FIELD_EMAIL_CREATOR, false);

        return map;
    }
}