package com.grsoft.napoleon;

import androidx.annotation.NonNull;

public class RingToneData implements Comparable<RingToneData> {
    public String uri;
    public String title;

    public RingToneData(String u, String t) {
        uri = u;
        title = t;
    }

    @NonNull
    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(RingToneData ringToneData) {
        return title.compareTo(ringToneData.title);
    }
}
