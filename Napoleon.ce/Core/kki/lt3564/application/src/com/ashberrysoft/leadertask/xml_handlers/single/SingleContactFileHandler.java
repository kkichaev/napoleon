package com.ashberrysoft.leadertask.xml_handlers.single;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.ContactsFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.ContactFile;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleContactFileHandler extends BaseSingleLionEntityHandler<ContactFile> {

    public SingleContactFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected ContactFile getEntityConstructor() {
        return new ContactFile();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(13);

        map.put(ContactsFileContract.FIELD_UID, false);
        map.put(ContactsFileContract.FIELD_CONTACTUID, false);
        map.put(ContactsFileContract.FIELD_FILEUID, false);
        map.put(ContactsFileContract.FIELD_EMAILCREATOR, false);
        map.put(ContactsFileContract.FIELD_ORDER, false);
        map.put(ContactsFileContract.FIELD_FILENAME, false);
        map.put(ContactsFileContract.FIELD_FILESIZE, false);
        map.put(ContactsFileContract.FIELD_FILEVERSION, false);
        map.put(ContactsFileContract.FIELD_USN_ENTITY, false);
        map.put(ContactsFileContract.FIELD_USN_FIELD_ORDER, false);
        map.put(ContactsFileContract.FIELD_USN_FIELD_NAME, false);
        map.put(ContactsFileContract.FIELD_USN_FIELD_SIZE, false);
        map.put(ContactsFileContract.FIELD_USN_FIELD_VERSION, false);

        return map;
    }
}