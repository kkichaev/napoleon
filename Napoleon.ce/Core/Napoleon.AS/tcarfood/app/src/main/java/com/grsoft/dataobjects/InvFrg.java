package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="invfrg", keyFields="created")
public class InvFrg extends CreateDocDataObject {
	public static final int INITED = 1;
	
	public List<InvFrgItem> items = new ArrayList<InvFrgItem>();
	public Date st1;
	public int st1_state = 0;
	public Date st2;
	public int st2_state = 0;
	public Date st3;
	public int st3_state = 0;
}
