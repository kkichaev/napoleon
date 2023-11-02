package com.ksoft.dms;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileUtil {
    public static final String FOLDER = "DMSShare";

    public static  File getShareDir(){
        File result = new File(Environment.getExternalStorageDirectory(), FOLDER);

        if (!result.exists())
            result.mkdirs();

        return  result;
    }

    public static File copyToShare(Context context, Uri source, String dist){
       File dir = getShareDir();

        File result = new File(dir, dist);
        copy(context, source, result);

        return result;
    }

    public static void copy(Context context, Uri source, File dist){
        try {
            BufferedInputStream in = new BufferedInputStream(context.getContentResolver().openInputStream(source));
            OutputStream out = new FileOutputStream(dist);

            byte[] buf = new byte[1024];
            int len = 0;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.flush();
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
