package com.grsoft.napoleon;

import androidx.annotation.NonNull;

public class RingToneData {
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
}
