package com.grsoft.dataobjects;
import com.grsoft.database.UploadSource;
import com.grsoft.aceteam.R;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.grsoft.database.BlobSource;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.BucketHelper;
import com.grsoft.network.DataUploader;
import com.grsoft.network.UploadContext;
import com.grsoft.network.exception.UploadException;
import com.grsoft.types.FieldOrder;
import com.grsoft.util.BitmapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

@TableInfo(name="picstore", keyFields="id")
@ServerInfo(name="PicStore")
public class PicStore extends CreateDocDataObject implements DataUploader {
	@UploadSource()
	public byte[] picture;

	@BlobSource
	@FieldOrder(order=3)
	public byte[] smallPhoto;

	@BlobSource
	@FieldOrder(order=4)
	public String smallSize = "";
	public String smallName = "";

	public String href = "";

	public void setImageFileName(byte[] fn) {
		picture = fn;
		makeSmallPhoto();
	}

	public String getImageFileName() { return new String(picture); }

	@SuppressLint("DefaultLocale")
	void makeSmallPhoto() {
		String fn = getImageFileName();
		String smallfn = fn + ".small";
		try (FileOutputStream out = new FileOutputStream(smallfn)) {
			Bitmap b = BitmapUtils.resizeBitmap(fn, Features.SMALL_PHOTO_DIMENSION, Features.SMALL_PHOTO_DIMENSION);
			b.compress(Bitmap.CompressFormat.JPEG, 80, out);

			smallPhoto = smallfn.getBytes();
			smallSize = String.format("%d*%d", b.getWidth(), b.getHeight());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

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

	@Override
	public void upload(UploadContext context) throws UploadException {
		if(href.length() == 0 && picture!= null && picture.length > 0) {
			@SuppressLint("SimpleDateFormat") String tag = new SimpleDateFormat("yyyyMMddHHmmss").format(created);
			BucketHelper.Result res = BucketHelper.putToBucket(new String(picture), tag, ConfigManager.getConfig());
			if(res.url != null) {
				href = res.url;
				context.writer.insertRecord(this);
			} else {
				throw new UploadException(res.error);
			}
		}
	}
}
