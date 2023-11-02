package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.EmployeeContract;
import com.ashberrysoft.leadertask.domains.ordinary.Employee;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * @since 2014-06-20
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleEmployeeHandler extends BaseSingleLionEntityHandler<Employee> {

    public SingleEmployeeHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected Employee getEntityConstructor() {
        return new Employee();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(4, 1.0f);

        map.put(EmployeeContract.NAME, false);
        map.put(EmployeeContract.EMAIL, false);
        map.put("invite", false);
        map.put("phone", false);

        return map;
    }
}