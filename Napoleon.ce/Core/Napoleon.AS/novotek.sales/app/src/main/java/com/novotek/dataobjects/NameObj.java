package com.novotek.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class NameObj implements Comparable<NameObj>, Parcelable {
    public String name_en = "";
    public String name_ru = "";

    public NameObj(){}

    protected NameObj(Parcel in) {
        name_en = in.readString();
        name_ru = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name_en);
        dest.writeString(name_ru);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NameObj> CREATOR = new Creator<NameObj>() {
        @Override
        public NameObj createFromParcel(Parcel in) {
            return new NameObj(in);
        }

        @Override
        public NameObj[] newArray(int size) {
            return new NameObj[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameObj nameObj = (NameObj) o;
        return name_en.equals(nameObj.name_en);
    }

    public boolean empty() { return name_ru.length() == 0; }

    @Override
    public int hashCode() {
        return Objects.hash(name_en);
    }

    @Override
    public String toString() {
        return name_ru;
    }

    @Override
    public int compareTo(NameObj nameObj) {
        return name_ru.compareTo(nameObj.name_ru);
    }
}
