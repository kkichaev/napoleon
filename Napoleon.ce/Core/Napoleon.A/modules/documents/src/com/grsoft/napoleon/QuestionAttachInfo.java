package com.grsoft.napoleon;

import android.os.Parcel;
import android.os.Parcelable;

public class QuestionAttachInfo implements Parcelable{
	public String id = "";
	public String name = "";
	
	@Override
	public int describeContents() {
		return 0;
	}

	public QuestionAttachInfo(Parcel in) {
		id = in.readString();
		name = in.readString();
	}

	
	public QuestionAttachInfo() {
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
	}
	
	public static final Parcelable.Creator<QuestionAttachInfo> CREATOR = new Parcelable.Creator<QuestionAttachInfo>() {
		public QuestionAttachInfo createFromParcel(Parcel in) {
			return new QuestionAttachInfo(in);
		}

		public QuestionAttachInfo[] newArray(int size) {
			return new QuestionAttachInfo[size];
		}
	};
}
