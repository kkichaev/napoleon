package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmpContract;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 2014-06-19
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleEmpHandler extends BaseSingleLionEntityHandler<Emp> {

    public SingleEmpHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected Emp getEntityConstructor() {
        return new Emp();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>();

        map.put(EmpContract.UID, false);
        map.put(EmpContract.LOGIN, false);
        map.put(EmpContract.ORDER, false);
        map.put(EmpContract.FIRST_NAME, false);
        map.put(EmpContract.MIDDLE_NAME, false);
        map.put(EmpContract.LAST_NAME, false);
        map.put(EmpContract.DETAILS, false);
        map.put(EmpContract.COUNTRY, false);
        map.put(EmpContract.PROVINCE, false);
        map.put(EmpContract.POSTAL_CODE, false);
        map.put(EmpContract.CITY, false);
        map.put(EmpContract.STREET, false);
        map.put(EmpContract.COMMUNICATION, false);
        map.put(EmpContract.GENDER, false);
        map.put(EmpContract.GROUP_UID, false);
        map.put(EmpContract.BIRTHDAY, false);
        map.put(EmpContract.TITLE, false);
        map.put(EmpContract.COMMENT, false);
        map.put(EmpContract.NOTIFY_BIRTHDAY, false);
        map.put(EmpContract.FAVORITE, false);
        map.put(EmpContract.SHOW_IN_NAVIGATOR, false);
        map.put(EmpContract.PHONE, false);

        map.put(EmpContract.USN_ENTITY, false);
        map.put(EmpContract.USN_FIELD_FIRSTNAME, false);
        map.put(EmpContract.USN_FIELD_LASTNAME, false);
        map.put(EmpContract.USN_FIELD_MIDDLENAME, false);
        map.put(EmpContract.USN_FIELD_DETAILS, false);
        map.put(EmpContract.USN_FIELD_GENDER, false);
        map.put(EmpContract.USN_FIELD_COUNTRY, false);
        map.put(EmpContract.USN_FIELD_PROVINCE, false);
        map.put(EmpContract.USN_FIELD_POSTALCODE, false);
        map.put(EmpContract.USN_FIELD_CITY, false);
        map.put(EmpContract.USN_FIELD_STREET, false);
        map.put(EmpContract.USN_FIELD_UID_GROUP, false);
        map.put(EmpContract.USN_FIELD_BIRTHDAY, false);
        map.put(EmpContract.USN_FIELD_COMMUNICATION, false);
        map.put(EmpContract.USN_FIELD_ORDER, false);
        map.put(EmpContract.USN_FIELD_TITLE, false);
        map.put(EmpContract.USN_FIELD_COMMENT, false);
        map.put(EmpContract.USN_FIELD_NOTIFYBIRTHDAY, false);
        map.put(EmpContract.USN_FIELD_FAVORITE, false);
        map.put(EmpContract.USN_FIELD_SHOWINNAVIGATOR, false);
        map.put(EmpContract.USN_FIELD_PHONE, false);

        return map;
    }
}