package com.grsoft.database;

import com.grsoft.dataobjects.PicStoreSrc;
import com.grsoft.network.exception.RuntimeException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PicsSrcHitching extends Hitching{
    Date date;
    String userid = null;
    public PicsSrcHitching(Date date) {
        super(PicStoreSrc.class, "PicStoreSrc");
        this.date = date;
        setCommand("SELECT");
    }

    public PicsSrcHitching(String userid, Date date) {
        this(date);
        this.userid = userid;
    }

    @Override
    public String getParams() throws RuntimeException {
        String params = super.getParams();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String todate = sdf.format(date);
        params += String.format(":created > ToDate('%1$s') and created <= ToDate('%1$s 23:59:59')", todate);
        if( userid != null ) {
            params += String.format(" and userid = '%s'", userid);
        }
        return params;
    }
}
