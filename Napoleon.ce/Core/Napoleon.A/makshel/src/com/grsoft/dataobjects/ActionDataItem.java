package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class ActionDataItem implements Parcelable {
	public int promoId = 0;
	public String priceId = "";
	
	@Scale(value = Consts.QTY_SCALE)
	public int qty = 0;
	
	@Scale(value = Consts.SUM_SCALE)
	public int cost = 0;
	
	@Scale(value = Consts.SUM_SCALE)
	public int dsc = 0;

	@Scale(value = Consts.SUM_SCALE)
	public int priceCost = 0;

	@Override public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel prc, int arg1) {
		prc.writeInt(promoId);
		prc.writeString(priceId);
		prc.writeInt(qty);
		prc.writeInt(cost);
		prc.writeInt(dsc);
		prc.writeInt(priceCost);
	}
	
	public static final Parcelable.Creator<ActionDataItem> CREATOR = new Creator<ActionDataItem>() {
		@Override public ActionDataItem[] newArray(int arg0) { return new ActionDataItem[arg0]; }
		@Override public ActionDataItem createFromParcel(Parcel arg0) { return new ActionDataItem(arg0); }
	};
	
	public ActionDataItem() {}
	
	private ActionDataItem(Parcel prc) {
		promoId = prc.readInt();
		priceId = prc.readString();
		qty = prc.readInt();
		cost = prc.readInt();
		dsc = prc.readInt();
		priceCost = prc.readInt();
	}

	public ActionDataItem(OrderItemEx oei) {
		promoId = oei.idTrd;
		priceId = oei.id;
		qty = oei.qty;
		cost = oei.cost;
		dsc = oei.disc;
		priceCost = oei.priceCost;
	}
	
	public long sum() { return (long)cost * qty / Consts.QTY_SCALE; }
}
