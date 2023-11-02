package com.grsoft.napoleon.dostavka;

import java.io.File;
import java.lang.reflect.Method;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

public class PhotoUtil {
	/**
	 * Вызывает у context метод startActivityForResult -  R.id.photo_dlg_result
	 * @param context
	 * @return путь к фото
	 */
	public static String takePhoto(Context context) {
		String result = ""; 
		try{
			File path = new File(Path.getDataDir());
			path.mkdir();
			File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue()));
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			
			Method call = context.getClass().getMethod("startActivityForResult", Intent.class, int.class);
			
			if(call != null){
				call.invoke(context, intent, R.id.photo_dlg_result);
			}
			
			result = file.getAbsolutePath();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
}
