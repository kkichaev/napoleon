package com.ksoft.dms;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriPermission;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.ksoft.dms.database.DBHelper;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ExportHelper {
    public Context context;

    public ExportHelper(Context context){
        this.context = context;
    }

    public void exportData(){
        String[] args = new String[]{
                getDataBaseFullPath().getAbsolutePath(),
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()};
        ExportAsync exp = new ExportAsync(this);
        exp.execute(args);
    }

    public void importData(){
        File src = new File( Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS), DBHelper.name);
        String[] args = new String[]{
                src.getAbsolutePath(),
                getDataBaseFullPath().getAbsoluteFile().getParent()
        };

        if (src.exists()) {
            ImportAsync imp = new ImportAsync(this);
            imp.execute(args);
        }
    }

    private void copyAttach(File src, File folder) throws IOException {
        if (!folder.exists())
            folder.mkdir();

        if (src.listFiles() != null)
            for(File f : src.listFiles())
                copy(context, f,new File(folder, f.getName()));
    }

    @NonNull
    private File copyBase(File src, File folder) throws IOException {
        DBHelper db = new DBHelper(context);
        db.close();

        if (!folder.exists())
            folder.mkdirs();

        File dist = new File(folder, DBHelper.name);
        copy(context, src,dist);
        return dist;
    }

    public static void copy(Context context, File src, File dst) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(Uri.fromFile(src));
        ParcelFileDescriptor des = context.getContentResolver().openFileDescriptor(Uri.fromFile(dst), "w");
        OutputStream out = new FileOutputStream(des.getFileDescriptor());

        //OutputStream out = context.getContentResolver().openOutputStream(Uri.fromFile(dst), "w");
        final int CPY_BUF_SIZE = 1024;

        byte[] buf = new byte[CPY_BUF_SIZE];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public File getDataBaseFullPath(){
        return context.getDatabasePath(DBHelper.name);
    }

    public void sayExportReult(String res){
        if (res.length() == 0)
            res = context.getString(R.string.export_error);
        else
            res = context.getString(R.string.export_done) + " " + res;

        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }

    public static class DataCopy extends AsyncTask<String, Void, String>{
        protected ExportHelper helper;

        public DataCopy(ExportHelper helper){
            this.helper = helper;
        }

        public String copyBase(String src, String dst) throws Exception{
            return helper.copyBase(new File(src), new File(dst)).getAbsolutePath();
        }

        @Override
        protected String doInBackground(String... args) {
            String src = args[0];
            String dst = args[1];
            String res = "";

            try {
                String dstFull = copyBase(src, dst);
                postProcess(src, dst);

                res = dstFull;
            }catch (Exception e){
                e.printStackTrace();
            }

            return res;
        }

        public void postProcess(String src, String dst) throws Exception{

        }
    }

    public static class ExportAsync extends DataCopy{

        public ExportAsync(ExportHelper helper) {
            super(helper);
        }

        public void postProcess(String src, String dst) throws Exception{
            File fsrc = FileUtil.getShareDir();
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), FileUtil.FOLDER);

            helper.copyAttach(fsrc, folder);
        }

        @Override
        protected void onPostExecute(String res) {
            helper.sayExportReult(res);
        }
    }

    public static class ImportAsync extends DataCopy {

        public ImportAsync(ExportHelper helper) {
            super(helper);
        }

        @Override
        public void postProcess(String src, String dst) throws Exception {
            super.postProcess(src, dst);

            helper.fixImageSrc(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    FileUtil.FOLDER).getAbsolutePath());
        }

        @Override
        protected void onPostExecute(String res) {
            helper.sayImportReult(res);
        }
    }

    private void fixImageSrc(String dst) {
        SQLiteDatabase db = new DBHelper(context).getWritableDatabase();

        Cursor c = db.query("notes_items", new String[]{"note_html", "id"}, null,
                null, null, null, null);

        while (c.moveToNext()){
            String html = c.getString(c.getColumnIndex("note_html"));
            html = fixHtml(html, dst);

            ContentValues cv = new ContentValues();
            cv.put("note_html", html);

            db.update("notes_items", cv, "id=?",
                    new String[]{c.getString(c.getColumnIndex("id"))});
        }

    }

    private String fixHtml(String html, String dst) {
        String strs[] = StringUtils.substringsBetween(html, "<img src=\"", "\">");

        if (strs != null)
            for (String s : strs)
                html = StringUtils.replace(html, s, replacePath(s, dst));

        return html;
    }

    private String replacePath(String s, String dst) {
        String res = StringUtils.substringAfterLast(s, "/");
        return String.format("%s/%s", dst, res);
    }

    private void sayImportReult(String res) {
        if (res.length() == 0)
            res = context.getString(R.string.import_error);
        else
            res = context.getString(R.string.import_succes);

        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }

}
