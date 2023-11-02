package com.grsoft.napoleon;

import android.os.Parcel;
import android.os.Parcelable;

public class InputDlgParam implements Parcelable {

    int label;
    long value;
    int scale;

    public InputDlgParam(int label, long value, int scale) {
        this.label = label;
        this.value = value;
        this.scale = scale;
    }

    protected InputDlgParam(Parcel in) {
        label = in.readInt();
        value = in.readInt();
        scale = in.readInt();
    }

    public static final Creator<InputDlgParam> CREATOR = new Creator<InputDlgParam>() {
        @Override
        public InputDlgParam createFromParcel(Parcel in) {
            return new InputDlgParam(in);
        }

        @Override
        public InputDlgParam[] newArray(int size) {
            return new InputDlgParam[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(label);
        dest.writeLong(value);
        dest.writeInt(scale);
    }
}
