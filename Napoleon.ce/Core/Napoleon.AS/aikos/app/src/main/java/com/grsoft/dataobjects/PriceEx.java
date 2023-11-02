package com.grsoft.dataobjects;

public class PriceEx extends Price {


    public int width = 0;
    public int wall = 0;
    public int diameter = 0;

    public String brand = "";
    public String subbrand = "";
    public String autoType = "";

    public int season = 0;
    public int studded = 0;

    public int keySKU = 0;

    public String getDescription() {
        return autoType + getSeason() + getStudded();
    }
    public String getSeason() { return season == 1 ? ", зима" : season == 2 ? ", лето" : season == 3 ? ", всесезонная" : ""; }
    public String getStudded() { return studded == 0 ? ", не шипованная" : studded == 1 ? ", шипованная" : studded == 2 ? ", фрикыионная" : ""; }

    public int docFilter = 0;
}
