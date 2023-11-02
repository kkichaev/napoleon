package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactContract;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 2014-06-18
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleContactHandler extends BaseSingleLionEntityHandler<Contact> {

    public SingleContactHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected Contact getEntityConstructor() {
        return new Contact();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(59);

        map.put(ContactContract.UID, false);
        map.put(ContactContract.UID_PARENT, false);
        map.put(ContactContract.EMAIL_CREATOR, false);
        map.put(ContactContract.UID_GROUP, false);
        map.put(ContactContract.TITLE, false);
        map.put(ContactContract.IS_GROUP, false);
        map.put(ContactContract.GENDER, false);
        map.put(ContactContract.FIRST_NAME, false);
        map.put(ContactContract.MIDDLE_NAME, false);
        map.put(ContactContract.LAST_NAME, false);
        map.put(ContactContract.COMPANY_NAME, false);
        map.put(ContactContract.JOB_TITLE, false);
        map.put(ContactContract.DETAILS, false);
        map.put(ContactContract.BIRTHDAY, false);
        map.put(ContactContract.COMMUNICATIONS, false);
        map.put(ContactContract.HOME_COUNTRY, false);
        map.put(ContactContract.HOME_REGION, false);
        map.put(ContactContract.HOME_INDEX, false);
        map.put(ContactContract.HOME_CITY, false);
        map.put(ContactContract.HOME_STREET, false);
        map.put(ContactContract.WORK_COUNTRY, false);
        map.put(ContactContract.WORK_REGION, false);
        map.put(ContactContract.WORK_INDEX, false);
        map.put(ContactContract.WORK_CITY, false);
        map.put(ContactContract.WORK_STREET, false);
        map.put(ContactContract.ORDER, false);
        map.put(ContactContract.COLLAPSED, false);
        map.put(ContactContract.FAVORITE, false);
        map.put(ContactContract.SHOW_NAVIGATOR, false);
        map.put(ContactContract.NOTIFY_BIRTHDAY, false);

        map.put(ContactContract.USN_ENTITY, false);
        map.put(ContactContract.USN_FIELD_UID_PARENT, false);
        map.put(ContactContract.USN_FIELD_UID_GROUP, false);
        map.put(ContactContract.USN_FIELD_TITLE, false);
        map.put(ContactContract.USN_FIELD_ISGROUP, false);
        map.put(ContactContract.USN_FIELD_GENDER, false);
        map.put(ContactContract.USN_FIELD_FIRSTNAME, false);
        map.put(ContactContract.USN_FIELD_MIDDLENAME, false);
        map.put(ContactContract.USN_FIELD_LASTNAME, false);
        map.put(ContactContract.USN_FIELD_COMPANY_NAME, false);
        map.put(ContactContract.USN_FIELD_JOB_TITLE, false);
        map.put(ContactContract.USN_FIELD_DETAILS, false);
        map.put(ContactContract.USN_FIELD_BIRTHDAY, false);
        map.put(ContactContract.USN_FIELD_COMMUNICATIONS, false);
        map.put(ContactContract.USN_FIELD_HOME_CITY, false);
        map.put(ContactContract.USN_FIELD_HOME_COUNTRY, false);
        map.put(ContactContract.USN_FIELD_HOME_REGION, false);
        map.put(ContactContract.USN_FIELD_HOME_INDEX, false);
        map.put(ContactContract.USN_FIELD_HOME_STREET, false);
        map.put(ContactContract.USN_FIELD_WORK_CITY, false);
        map.put(ContactContract.USN_FIELD_WORK_COUNTRY, false);
        map.put(ContactContract.USN_FIELD_WORK_REGION, false);
        map.put(ContactContract.USN_FIELD_WORK_INDEX, false);
        map.put(ContactContract.USN_FIELD_WORK_STREET, false);
        map.put(ContactContract.USN_FIELD_ORDER, false);
        map.put(ContactContract.USN_FIELD_COLLAPSED, false);
        map.put(ContactContract.USN_FIELD_FAVORITE, false);
        map.put(ContactContract.USN_FIELD_SHOW_NAVIGATOR, false);
        map.put(ContactContract.USN_FIELD_NOTIFY_BIRTHDAY, false);

        return map;
    }
}