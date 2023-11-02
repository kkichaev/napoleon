package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleMarkerHandler extends BaseSingleLionEntityHandler<Marker> {

    public SingleMarkerHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected Marker getEntityConstructor() {
        return new Marker();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(13);

        map.put(Marker.FIELD_UID, false);
        map.put(Marker.FIELD_ORDER, false);
        map.put(Marker.FIELD_NAME, false);
        map.put(Marker.FIELD_IS_UPPERCASE, false);
        map.put(Marker.FIELD_TEXT_COLOR, false);
        map.put(Marker.FIELD_BACK_COLOR, false);
        map.put(Marker.FIELD_USN, false);
        map.put(Marker.FIELD_USN_ORDER, false);
        map.put(Marker.FIELD_USN_NAME, false);
        map.put(Marker.FIELD_USN_IS_UPPERCASE, false);
        map.put(Marker.FIELD_USN_TEXT_COLOR, false);
        map.put(Marker.FIELD_USN_BACK_COLOR, false);
        map.put(Marker.FIELD_EMAIL_CREATOR, false);

        return map;
    }
}