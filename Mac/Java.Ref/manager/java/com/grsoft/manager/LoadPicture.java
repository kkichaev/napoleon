package com.grsoft.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.core.content.FileProvider;

class LoadPicture extends AsyncTask<String, Void, File> {
	Context context;
	
	public LoadPicture(Context context) {
		this.context = context;
	}
	
    protected File doInBackground(String... urls) {
    	File res = null;
        try
        {
            HttpURLConnection conn= (HttpURLConnection)new URL(urls[0]).openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bmImg = BitmapFactory.decodeStream(is,null,options);

            if (Environment.getExternalStorageState()
    				.equals(Environment.MEDIA_MOUNTED)){
    			File folder = createFolder();
    			res = savePic(bmImg, folder);
    		}
        } 
        catch(IOException e){
            e.printStackTrace();
        }
        
        return res;
    }
    
    @Override
    protected void onPostExecute(java.io.File result) {
    	if (result != null)
    		showGallery(result);
    }
    
	private void showGallery(File file) {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		
		Uri uri = null;
		
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context,context.getString(R.string.fileprovider_authorities), file);
		}else
			uri = Uri.parse("file://" + file);
		
		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setDataAndType(uri, "image/*");
		
		context.startActivity(i);
	}
    
    private static final String SHARED_FOLDER = "vispic/";
    
    public File createFolder() {
		File folder = new File(Environment.getExternalStorageDirectory(),SHARED_FOLDER);
		
		if (!folder.exists())
			folder.mkdirs();
		return folder;
	}

    private File savePic(Bitmap bmp, File folder) {
    	File res = new File(folder, "load.jpg");
    	try  {
    		FileOutputStream out = new FileOutputStream(res);
    	    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); 
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	
    	return res;
	}
    
    
}