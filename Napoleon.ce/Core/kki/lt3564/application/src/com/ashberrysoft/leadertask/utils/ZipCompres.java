package com.ashberrysoft.leadertask.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 * */

public class ZipCompres {

    private File[] mFiles;
    private File mDbFile;
    private File mZipFile;
    private boolean mStopZip;

    public ZipCompres(File[] files, File dbFile, File zipFile) {
        mStopZip = dbFile == null
                && (files.length == 0 || files.length == 1 && files[0].getName().equals(zipFile.getName()));
        if (mStopZip) {
            return;
        }

        mFiles = files;
        mDbFile = dbFile;
        mZipFile = zipFile;
        mZipFile.delete();
    }

    public void toZip() {
        if (mStopZip) {
            return;
        }

        FileOutputStream dest = null;
        ZipOutputStream out = null;
        try {
            dest = new FileOutputStream(mZipFile);
            out = new ZipOutputStream(new BufferedOutputStream(dest));
            final byte data[] = new byte[2048];

            BufferedInputStream origin = null;
            for (File file : mFiles) {
                fileToZip(file.getAbsolutePath(), file.getName(), origin, out, data);
            }
            if (mDbFile != null && mDbFile.exists()) {
                fileToZip(mDbFile.getAbsolutePath(), mDbFile.getName() + ".sqlite", origin, out, data);
            }
            out.close();

        } catch (Exception e) {
            Utils.toLog(e);
            mZipFile.delete();
        } finally {
            if (dest != null) {
                try {
                    dest.close();
                } catch (IOException e) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
    }

    private void fileToZip(String path, String name, BufferedInputStream origin, ZipOutputStream out, byte data[])
            throws IOException {
        if (mZipFile.getName().equals(name)) {
            return;
        }

        final FileInputStream fi = new FileInputStream(path);
        origin = new BufferedInputStream(fi, 2048);
        final ZipEntry entry = new ZipEntry(name);
        out.putNextEntry(entry);

        int count;
        while ((count = origin.read(data, 0, 2048)) != -1) {
            out.write(data, 0, count);
        }

        origin.close();
    }
}