package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="visitpreview", keyFields = "created")
public class VisitPreview extends VisitInfo {
	
	public List<VisitPreviewItem> items = new ArrayList<VisitPreviewItem>();
}
