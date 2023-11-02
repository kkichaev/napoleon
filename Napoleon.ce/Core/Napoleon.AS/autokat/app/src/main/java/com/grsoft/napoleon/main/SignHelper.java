package com.grsoft.napoleon.main;

import com.grsoft.napoleon.util.debug.Path;

import java.io.File;
import java.util.Date;

public class SignHelper {
    public static final String MINE_SIGN = "sing.png";
    public static final Date minePicStore = new Date(1000);

    public static String getSignPath() {
        String res = String.format("%s/sign/", Path.getDataDir());
        File file = new File(res);
        if (!file.exists()) file.mkdirs();
        return res;
    }

    public static String getMainSignPath() {
        return String.format("%s/%s", getSignPath(), MINE_SIGN);
    }
}
