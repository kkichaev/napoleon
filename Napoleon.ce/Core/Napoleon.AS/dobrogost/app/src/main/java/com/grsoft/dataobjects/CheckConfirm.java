package com.grsoft.dataobjects;

import java.util.Date;

public class CheckConfirm extends DataObject {
	public Date created;
	public int type = 0;
	public String qrcode = "";
	public String remark = "";
	public int status;
}
