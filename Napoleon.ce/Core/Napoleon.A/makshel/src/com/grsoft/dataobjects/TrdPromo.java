package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.TableInfo;

import android.os.Parcel;
import android.os.Parcelable;

@TableInfo(name="TrdPromo", keyFields="id")
public class TrdPromo extends DataObject implements Parcelable {
	public int id = 0;
	public String name = "";
	public String userid = "";
	
	public Date start = new Date();
	public Date end = new Date();

	public List<TrdPromoOrg> orgs = new ArrayList<TrdPromoOrg>();
	public List<TrdPromoItem> items = new ArrayList<TrdPromoItem>();
	
	public boolean isActive(OrgEx org) {
		for(TrdPromoOrg tpo : orgs) {
			if(tpo.rel == TrdPromoOrg.ALL_REL)
				return true;
			if(tpo.rel == TrdPromoOrg.ORG_REL && tpo.code.equals(org.id))
				return true;
			if(tpo.rel == TrdPromoOrg.GROUP_REL && org.haveGroup(tpo.code))
				return true;
		}
		return false;
	}
	
	
	public boolean haveItem(int itemId) {
		for(TrdPromoItem i: items)
			if(i.id == itemId)
				return true;
		
		return false;
	}


	public TrdPromo() {}
	
	TrdPromo(Parcel prc) {
		id = prc.readInt();
		name = prc.readString();
		userid = prc.readString();
		start = new Date(prc.readLong());
		end = new Date(prc.readLong());
		
		prc.readList(items, TrdPromoItem.class.getClassLoader());
	}
	
	@Override public int describeContents() { return 0; }

	public static final Parcelable.Creator<TrdPromo> CREATOR = new Creator<TrdPromo>() {
		@Override public TrdPromo[] newArray(int arg0) { return new TrdPromo[arg0]; }
		@Override public TrdPromo createFromParcel(Parcel arg0) { return new TrdPromo(arg0); }
	};

	@Override
	public void writeToParcel(Parcel prc, int arg1) {
		prc.writeInt(id);
		prc.writeString(name);
		prc.writeString(userid);
		prc.writeLong(start.getTime());
		prc.writeLong(end.getTime());
		prc.writeList(items);
	}
}
