package com.grsoft.napoleon;

import com.grsoft.napoleon.main.SignHelper;
import com.grsoft.napoleon.modules.print.BaseDataSource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class PrintDataSource extends BaseDataSource {
    public PrintDataSource(Object object) {
        super(object);
    }

    @Override
    public byte[] getImage(String name) {
        String path = "";
        if (name.equals("mine"))
            path = SignHelper.getMainSignPath();
        else{
            PrintData pd = (PrintData)object;
            path = pd.sign_path;
        }

        byte[] ret = null;

        if (path.length() == 0)
            return ret;

        File f = new File(path);
        int len = (int) f.length();
        ret = new byte[len];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(f));
            buf.read(ret, 0, ret.length);
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
            ret = null;
        }

        return ret;
    }

    @Override
    public int getImageHeight(String name) {
        return 30;
    }
}
