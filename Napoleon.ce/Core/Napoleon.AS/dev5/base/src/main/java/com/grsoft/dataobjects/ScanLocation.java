package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.TableInfo;

@TableInfo(name="orgcoord", keyFields="created")
public class ScanLocation extends CreateDocDataObject {
    public int accuracy;
}
