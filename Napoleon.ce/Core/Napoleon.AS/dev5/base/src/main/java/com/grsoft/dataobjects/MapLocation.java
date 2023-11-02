package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import android.location.Location;

public class MapLocation {
    public double mLatitude = 0;
    public double mLongitude = 0;

    public MapLocation(Location src) {
        this.mLatitude = src.getLatitude();
        this.mLongitude = src.getLongitude();
    }
}
