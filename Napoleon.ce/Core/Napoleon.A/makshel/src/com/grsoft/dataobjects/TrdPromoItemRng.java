package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class TrdPromoItemRng extends DataObject implements Parcelable {
	@FieldOrder(order = 0)
	public String id = "";

	@FieldOrder(order = 1)
	public String idMfr = "";

	@FieldOrder(order = 2)
	public String idOwr = "";

	@FieldOrder(order = 3)
	public String idCat = "";

	@FieldOrder(order = 4)
	@Scale(value = Consts.SUM_SCALE)
	public int cost = 0;
	
	@Override public int describeContents() { return 0;	}

	public static final Parcelable.Creator<TrdPromoItemRng> CREATOR = new Creator<TrdPromoItemRng>() {
		@Override public TrdPromoItemRng[] newArray(int size) { return new TrdPromoItemRng[size]; }
		@Override public TrdPromoItemRng createFromParcel(Parcel prc) { return new TrdPromoItemRng(prc); }
	};
	
	public boolean contains(PriceEx p) {
		return id.equals(p.id) || idMfr.equals(p.idMfr) || idOwr.equals(p.idOwr) || idCat.equals(p.idCat);
	}
	
	@Override
	public void writeToParcel(Parcel out, int arg1) {
		out.writeString(id);
		out.writeString(idMfr);
		out.writeString(idOwr);
		out.writeString(idCat);
		out.writeInt(cost);
	}
	
	private TrdPromoItemRng(Parcel prc) {
		id = prc.readString();
		idMfr = prc.readString();
		idOwr = prc.readString();
		idCat = prc.readString();
		cost = prc.readInt();
	}
	
	public TrdPromoItemRng() {}
	
	public String getWhere() {
		String res = "";
		if(id.length() > 0)
			res += "id='" + id + "'";
		if(idMfr.length() > 0) {
			if(res.length() > 0) res += " and ";
			res += "idMfr='" + idMfr + "'"; 
		}
		if(idCat.length() > 0) {
			if(res.length() > 0) res += " and ";
			res += "idCat='" + idCat + "'"; 
		}
		if(idOwr.length() > 0) {
			if(res.length() > 0) res += " and ";
			res += "idOwr='" + idOwr + "'"; 
		}
		
		return res;
	}
}
