/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Фотография к посещению(Visist)
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects;
import com.grsoft.database.UploadSource;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.grsoft.database.BlobSource;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.BucketHelper;
import com.grsoft.network.DataUploader;
import com.grsoft.network.exception.UploadException;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.FieldVersion;
import com.grsoft.util.BitmapUtils;

public class VisitItem extends DataObject {
	// deprecated
	@FieldOrder(order=0)
	public byte[] id;
	
	@FieldOrder(order=1)
	public Date date = new Date();

	@UploadSource()
	@FieldOrder(order=2)
	public byte[] photo;

	@BlobSource
	@FieldOrder(order=3)
	public byte[] smallPhoto;

	@FieldOrder(order=4)
	public String smallSize = "";

	@FieldOrder(order=5)
	@FieldVersion(version = 1)
	public String href = "";

	@SuppressWarnings("unused")
	private String __nameBase;

	@SuppressWarnings("unused")
	private Date __date;

	public String getImageFileName() { return photo == null? "" : new String(photo); }
	public String getSmallFileName() { return smallPhoto == null? "" : new String(smallPhoto); }

	public void setImageFileName(byte[] fn) {
		photo = fn;
		makeSmallPhoto();
	}

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

	public void setNameBase(String str) { __nameBase = str; }
	public void setDateBase(Date val) { __date = val; }

	public void deletePhoto() {
		File file = new File(getImageFileName());
		file.delete();

		file = new File(getSmallFileName());
		file.delete();
	}
	
	@Override
	public Field[] getFields() {
		List<Field> vals = new ArrayList<Field>(); 
		vals.addAll(Arrays.asList(super.getFields()));
		
		try {
			Field nb = VisitItem.class.getDeclaredField("__nameBase");
			nb.setAccessible(true);
			vals.add(nb);
			nb = VisitItem.class.getDeclaredField("__date");
			nb.setAccessible(true);
			vals.add(nb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Field[] arrs = new Field[vals.size()];
		return vals.toArray(arrs);
	}
	
	@Override
	public Field getField(String name) {
		if(name.equals("__nameBase") || name.equals("__date")) {
			Field nb = null;
			try {
				nb = VisitItem.class.getDeclaredField(name);
				nb.setAccessible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return nb;
		}
		return super.getField(name);
	}
}
