package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandler;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleTaskFileHandler extends BaseSingleLionEntityHandler<TaskFile> {

    public SingleTaskFileHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected TaskFile getEntityConstructor() {
        return new TaskFile();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(13);

        map.put(TaskFileContract.FIELD_UID, false);
        map.put(TaskFileContract.FIELD_TASKUID, false);
        map.put(TaskFileContract.FIELD_FILEUID, false);
        map.put(TaskFileContract.FIELD_EMAILCREATOR, false);
        map.put(TaskFileContract.FIELD_ORDER, false);
        map.put(TaskFileContract.FIELD_FILENAME, false);
        map.put(TaskFileContract.FIELD_FILESIZE, false);
        map.put(TaskFileContract.FIELD_FILEVERSION, false);
        map.put(TaskFileContract.FIELD_USN_ENTITY, false);
        map.put(TaskFileContract.FIELD_USN_FIELD_ORDER, false);
        map.put(TaskFileContract.FIELD_USN_FIELD_NAME, false);
        map.put(TaskFileContract.FIELD_USN_FIELD_SIZE, false);
        map.put(TaskFileContract.FIELD_USN_FIELD_VERSION, false);

        return map;
    }
}