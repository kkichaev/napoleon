/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Объект данных Visit
 *
 * kki   26/10/2010   creating
 */
package com.grsoft.dataobjects;
import android.annotation.SuppressLint;

import com.grsoft.aceteam.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.grsoft.database.TableInfo;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.BucketHelper;
import com.grsoft.network.DataUploader;
import com.grsoft.network.UploadContext;
import com.grsoft.network.exception.UploadException;
import com.grsoft.types.Scale;

@TableInfo(name="Visit", keyFields = "created")
public class Visit extends VisitInfo implements PhotoListDoc, DataUploader
{
	/**
	 * Служебный флаг описывающий
	 * текущее состояния
	 * 
	 * устаревшее - теперь используется <b>params</b>
	 */
	@Scale(value=1)
	@Deprecated
	public int flags;
	
	/**
	 * Фотографии
	 */
	public List<VisitItem> items = new ArrayList<VisitItem>();

	/**
	 * причины визита
	 */
	public String cause = "";
	
	public int sendedPhotos = 0;

	@Override public List<VisitItem> getItems() { return items; }
	@Override public String getDocName() { return "Visit"; }
	@Override public String getItemName() { return "VisitItemDoc"; }
	@Override public void setItems(List<VisitItem> newItems) { items = newItems; }

	@Override
	public void upload(UploadContext context) throws UploadException {
		boolean updated = false;
		Set<String> used = new HashSet<>();

		for(VisitItem vi : items) {
			if (vi.href.length() == 0 && vi.photo != null && vi.photo.length > 0) {
				@SuppressLint("SimpleDateFormat")
				String tag = new SimpleDateFormat("yyyyMMddHHmmss").format(date);
				while (used.contains(tag)) {
					tag += "_";
				}
				used.add(tag);
				BucketHelper.Result res = BucketHelper.putToBucket(new String(vi.photo), tag, ConfigManager.getConfig());
				if (res.url != null) {
					vi.href = res.url;
					updated = true;
				} else {
					throw new UploadException(res.error);
				}
			}
		}

		if(updated) {
			context.writer.insertRecord(this);
		}
	}
}
