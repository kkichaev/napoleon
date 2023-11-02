package com.grsoft.database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.DataObject;

@TableInfo(name="storytape", keyFields="created,userid")
@ServerInfo(name="StoryTape")
public class StoryTape extends DataObject {
	public Date created;
	public Date sended;
	public String userid = "";
	public String username = "";
	public String id = "";
	public String org = "";
	public List<StoryTapeItem> items = new ArrayList<StoryTapeItem>();
	public List<StoryTapePic> photo = new ArrayList<StoryTapePic>();
	
}
