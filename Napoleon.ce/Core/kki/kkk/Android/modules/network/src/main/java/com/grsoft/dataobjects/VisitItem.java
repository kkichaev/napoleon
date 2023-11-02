/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Фотография к посещению(Visist)
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.grsoft.database.BlobSource;
import com.grsoft.types.FieldOrder;

public class VisitItem extends DataObject
{
	/**
	 * Снимок с камеры к посещению
	 */
	@BlobSource
	@FieldOrder(order=0)
	public byte[] id;
	
	@FieldOrder(order=1)
	public Date date;
	
	@SuppressWarnings("unused")
	private String __nameBase;

	@SuppressWarnings("unused")
	private Date __date;
	
	public void setNameBase(String str) { __nameBase = str; }
	public void setDateBase(Date val) { __date = val; }
	
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

	public Bitmap getImage() {
		Bitmap ret = null;
		if(id != null) {
			ret = BitmapFactory.decodeFile(new String(id));
		}
		return ret;
	}
}
