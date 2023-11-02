package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.napoleon.chart.ChartActivity;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.LinesOnClickListener;

public class Main3_62 extends Main{
	
	@Override
	public int getPrefValue(String name, int defValue) {
		if (name.equals(LinesOnClickListener.PREF_NAME))
			return LinesOnClickListener.VARIABLE_LINE_HEIGHT;
		return super.getPrefValue(name, defValue);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		int res = docType.getDocTitle();
		if (res != -1)  
			setTitle(res);
	}
	
	protected void showRouteMap() {
		ArrayList<String> ids = new ArrayList<String>();
		
		for(OrgFolderItem i : ((FoldersMainAdapter)foldersMainAdapter).currentFolder().items)
			ids.add(i.name);
		
		MapActivity.open(this, ids);
	}

	public void openReports() {
		ChartActivity.open(this);
	}
}
