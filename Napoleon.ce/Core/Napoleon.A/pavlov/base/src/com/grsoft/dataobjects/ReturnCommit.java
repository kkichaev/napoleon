package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.util.Consts;

@TableInfo(name="ReturnCommit", keyFields="created")
@ServerInfo(name="ReturnCommit")
public class ReturnCommit extends DataObject {
	public Date created;
	public List<ReturnCommitItem> items = new ArrayList<ReturnCommitItem>();
	
	public long sum() {
		long sum = 0;
		for(ReturnCommitItem i : items)
			sum += i.qty * i.sum / Consts.QTY_SCALE;
		return sum;
	}
}
