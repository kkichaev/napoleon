package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.os.Parcel;
import android.os.Parcelable;

@TableInfo(name="AgentManagerMemo", keyFields="userid,created")
@ServerInfo(name="AgentManagerMemo")
public class AgentManagerMemo extends DataObject implements Comparable<AgentManagerMemo>, Parcelable {
	
	//static final int READED_STATE 	= 1; 
	static final int REJECTED_STATE = 2;
	static final int ALLOWED_STATE 	= 4;
	//	static final int SENDED_STATE 	= 8;
	
	public String userid = "";
	public Date created = new Date();
	public Date sended = new Date();
	
	public String id = "";
	public String orgName = "";
	public String dogName = "";
	public String orgAddress = "";
	public int orgColor = 0;

	public String deliveries = "";

	public int dogColor = 0;

	public String idDog = "";
	public String topic = "";
	public Date till = new Date();
	
	@Scale(value=Consts.SUM_SCALE)
	public long sum = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public long sumD = 0;
	@Scale(value=Consts.SUM_SCALE)
	public long overdueSum = 0;
	public int overdue = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public long dogLimit = 0;
	public int dogDue = 0;
	
	
	public String remark="";
	public int params = 0;
	
	public String managerRemark = "";
	
	public boolean isUnreaded() { return isEditable(); }
	public boolean isEditable() { return (params & (REJECTED_STATE | ALLOWED_STATE)) == 0; }

	public int state() {
		if(isUnreaded()) return 0;
		return isAllowed() ? 1 : 2;
	}
	
//	public boolean setReaded(boolean readed) {
//		if(isEditable()) {
//			if(readed)
//				params |= READED_STATE;
//			else
//				params &= (~READED_STATE);
//			
//			return true;
//		}
//		return false;
//	}
	
	public boolean isAllowed() { return (params & ALLOWED_STATE) != 0; }
	public boolean isRejected() { return (params & REJECTED_STATE) != 0; }
	
	public boolean setAllowed(boolean allowed) {
		if(isEditable()) {
			if(allowed)
				params |= ALLOWED_STATE;
			else
				params |= REJECTED_STATE;
			
			return true;
		}
		return false;
	}

	@Override
	public int compareTo(AgentManagerMemo arg0) {
		if(isUnreaded()) {
			if(!arg0.isUnreaded())
				return -1;
		} else {
			if(arg0.isUnreaded())
				return 1;
		}
		return arg0.created.compareTo(created);
	}

	public AgentManagerMemo() {}
	
	AgentManagerMemo(Parcel prc) {
		userid = prc.readString();
		created = new Date(prc.readLong());
		sended = new Date(prc.readLong());
		id = prc.readString();
		orgName = prc.readString();
		orgAddress = prc.readString();
		orgColor = prc.readInt();
		dogColor = prc.readInt();
		params = prc.readInt();
		remark = prc.readString();
	}
	
	public static final Parcelable.Creator<AgentManagerMemo> CREATOR = new Creator<AgentManagerMemo>() {
		@Override public AgentManagerMemo[] newArray(int arg0) { return new AgentManagerMemo[arg0]; }
		@Override public AgentManagerMemo createFromParcel(Parcel arg0) { return new AgentManagerMemo(arg0); }
	};	
	
	@Override public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel prc, int arg1) {
		prc.writeString(userid);
		prc.writeLong(created.getTime());
		prc.writeLong(sended.getTime());
		prc.writeString(id);
		prc.writeString(orgName);
		prc.writeString(orgAddress);
		prc.writeInt(orgColor);
		prc.writeInt(dogColor);
		prc.writeInt(params);
		prc.writeString(remark);
	}
}
