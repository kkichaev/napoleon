package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.documents.DocumentUtils;
import com.grsoft.napoleon.debet_data.DogovorData;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="AgentMemo", keyFields="created")
@ServerInfo(name="AgentMemo")
public class AgentMemo extends CreateDocDataObject {
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
	public long sumPay = 0;

	@Scale(value=Consts.SUM_SCALE)
	public long overdueSum = 0;
	public int overdue = 0;
	
	public String email = "";
	public String topicName = "";

	public String deliveries = "";

	public boolean isEditable() {
		return !DocumentUtils.isExported(params);
	}

	public PicStore findPicture() {
		List<PicStore> pics = DbReader.fetch(PicStore.class, "created=" + Long.toString(created.getTime()));
		return pics.size() > 0 ? pics.get(0) : null;
	}

    public void update(DogovorData dd) {
		if(dd != null) {
			sumD = dd.sum;
			overdue = dd.overdueDays;
			overdueSum = dd.overdueSum;
			dogColor = dd.getColor();
		} else {
			sumD = 0;
			overdue = 0;
			overdueSum = 0;
			dogColor = 0;
		}
    }
}
