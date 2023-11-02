package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.os.Parcel;
import android.os.Parcelable;

public class TrdPromoItem extends DataObject implements Parcelable {
	public static final int TYPE_COST = 0;	
	public static final int TYPE_DSC = 1;	

	public static final int CND_NO_CHECK = 0;	
	public static final int CND_CHECK = 1;	

	public static final int SBJ_QTY = 0;	
	public static final int SBJ_SUM = 1;	
	
	@FieldOrder(order = 0)
	public int id = 0;

	@FieldOrder(order = 1)
	public String name = "";
	
	@FieldOrder(order = 2)
	@Scale(value=Consts.SUM_SCALE)
	public int val = 0;

	@FieldOrder(order = 3)
	public int valType = 0;

	@FieldOrder(order = 4)
	public int valCnd = 0;

	@FieldOrder(order = 5)
	public int valSbj = 0;
	
	@FieldOrder(order = 6)
	@Scale(value=Consts.SUM_SCALE)
	public int start = 0;

	@FieldOrder(order = 7)
	@Scale(value=Consts.SUM_SCALE)
	public int end = 0;
	
	@FieldOrder(order = 8)
	public List<TrdPromoItemRng> items = new ArrayList<TrdPromoItemRng>();

	@Override public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel prc, int arg1) {
		prc.writeInt(id);
		prc.writeString(name);
		prc.writeInt(val);
		prc.writeInt(valType);
		prc.writeInt(valCnd);
		prc.writeInt(valSbj);
		prc.writeInt(start);
		prc.writeInt(end);
		
		prc.writeTypedList(items);
	}
	
	public static final Parcelable.Creator<TrdPromoItem> CREATOR = new Creator<TrdPromoItem>() {
		@Override public TrdPromoItem[] newArray(int arg0) { return new TrdPromoItem[arg0]; }
		@Override public TrdPromoItem createFromParcel(Parcel arg0) { return new TrdPromoItem(arg0); }
	};
	
	public TrdPromoItem() {}
	
	private TrdPromoItem(Parcel prc) {
		id = prc.readInt();
		name = prc.readString();
		val = prc.readInt();
		valType = prc.readInt();
		valCnd = prc.readInt();
		valSbj = prc.readInt();
		start = prc.readInt();
		end = prc.readInt();
		
		prc.readTypedList(items, TrdPromoItemRng.CREATOR);
	}
}
