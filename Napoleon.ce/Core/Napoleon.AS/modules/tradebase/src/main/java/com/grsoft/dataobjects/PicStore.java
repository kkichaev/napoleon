package com.grsoft.dataobjects;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.grsoft.database.BlobSource;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.R;

import java.io.File;
import java.io.InputStream;

@TableInfo(name="picstore", keyFields="id")
@ServerInfo(name="PicStore")
public class PicStore extends CreateDocDataObject{
	@BlobSource
	public byte[] picture;


	public void preview(Context context) {

		Uri uri = getUri(context);

		Intent i = new Intent();
		i.setAction(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		i.setDataAndType(uri, "image/*");

		context.startActivity(i);
	}

	public Uri getUri(Context context) {
		Uri uri = null;

		String path = new String(picture);
		if (Build.VERSION.SDK_INT >= 24) {
			uri = FileProvider.getUriForFile(context,
					context.getString(R.string.fileprovider_authorities),
					new File(path));
		}else
			uri = Uri.parse("file://" + path);

		return uri;
	}

	public Drawable getDrawable(Context context) {
		Drawable ret = null;

		if(picture != null && picture.length > 0) {
			try {
				Uri uri = getUri(context);
				InputStream inputStream = context.getContentResolver().openInputStream(uri);
				ret = Drawable.createFromStream(inputStream, uri.toString() );
			} catch (Exception e) {

			}
		}
		return ret;
	}
}
