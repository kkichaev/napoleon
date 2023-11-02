package com.grsoft.dataobjects;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Date;

public class SignDocResponse extends DataObject {
    public static final int REJECTED = 0;
    public static final int SIGNED = 1;

    public Date created = new Date();
    public int status = REJECTED;
    public byte[] sign = null;

    public static SignDocResponse makeResponse(DocsToSign doc, String signFile) {
        SignDocResponse ret = new SignDocResponse();
        ret.created = doc.created;
        if(signFile != null) {
            ret.status = SIGNED;
            File file = new File(signFile);
            if(file.length() > 0) {
                try {
                    ret.sign = new byte[(int) file.length()];
                    FileInputStream fis = new FileInputStream(file);
                    fis.read(ret.sign);
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }
}
