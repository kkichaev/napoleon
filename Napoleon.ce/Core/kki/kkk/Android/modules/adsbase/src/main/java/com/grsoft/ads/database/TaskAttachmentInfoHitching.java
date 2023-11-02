package com.grsoft.ads.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.grsoft.ads.dataobjects.TaskAttachmentInfo;
import com.grsoft.database.DbWriter;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.dataobjects.DataObjectInfo;

public class TaskAttachmentInfoHitching extends HitchOnSelect {
	private SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yyyy");
	
	public TaskAttachmentInfoHitching() {
		super(TaskAttachmentInfo.class, "TaskAttachmentInfo");
		
		setCondition(sdf.format(new Date()));
	}

	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
}
