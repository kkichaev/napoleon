package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="Strocheck", keyFields="created")
@ServerInfo(name="Storcheck")
public class Storcheck extends CreateDocDataObject {
	public int ho_best = 0;
	public int showcase_best = 0;
	public int corp_block = 0;
	public int posm = 0;
	
	public String action = "";
	
	public int share_ki = 0;
	public int share_pf = 0;
	
	public List<StorcheckItem> items = new ArrayList<StorcheckItem>();
}
