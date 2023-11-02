package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.PassportPhotos;

public class PassportPhotosImpl extends DbObject<PassportPhotos> {
    public boolean hasPassport(String seria, String number){
        if(seria.trim().length() == 0 || number.trim().length() == 0)
            return false;

        data.number = String.format("%s%s", seria, number);
        boolean res = read();
        close();
        return  res;
    }
}
