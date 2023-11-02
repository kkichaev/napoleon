package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="AgentMemo", keyFields="created")
@ServerInfo(name="AgentMemo")
public class AgentMemo extends CreateDocDataObject {
	public static final String UNLOCK_TOPIC_ID = "000000001";
	public static final String SEND_INVOICE_ID = "000000002";
	
	public int orgColor = 0;
	public int dogColor = 0;

	public String idDog = "";
	public String topic = "";
	
	// use as linked doc in create invoice request
	public Date till = new Date();
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sumD = 0;
	@Scale(value=Consts.SUM_SCALE)
	public long overdueSum = 0;
	public int overdue = 0;
	
	public String email = "";
	public String topicName = "";
	
	public boolean isEmpty() {
		return !isValid();
	}
	
	public boolean sendInvoice() { return topic.equals(SEND_INVOICE_ID); }
	
	public boolean isValid() {
		if(!topic.equals(UNLOCK_TOPIC_ID)) return email.length() > 0;
		return remark.length() > 0 && idDog.length() > 0 && topic.length() > 0;
	}
}
