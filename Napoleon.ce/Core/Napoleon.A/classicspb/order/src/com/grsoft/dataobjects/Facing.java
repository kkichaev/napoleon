package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.CreateDocDataObject;

@TableInfo(name="facing", keyFields="created")
public class Facing extends CreateDocDataObject {
	public List<FacingItem> items = new ArrayList<FacingItem>();
}
