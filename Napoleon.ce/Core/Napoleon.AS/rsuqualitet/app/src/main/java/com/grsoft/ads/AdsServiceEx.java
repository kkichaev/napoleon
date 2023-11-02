package com.grsoft.ads;

import android.content.Context;

import com.grsoft.ads.database.TaskHitchingEx;
import com.grsoft.database.Hitching;

public class AdsServiceEx extends AdsService {
    public Hitching createTaskHitching(Context context){
        return  new TaskHitchingEx(context);
    }
}
