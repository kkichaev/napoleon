package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="gift", keyFields="giftid")
@ServerInfo(name="Gift")
public class Gift extends DataObject {
	public String giftid = "";
	public String id = "";
	public String id_i = "";
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	public Date start = null;
	public Date finish = null;
	public List<GiftItem> items = new ArrayList<GiftItem>(); 
}
