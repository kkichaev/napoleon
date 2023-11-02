package com.grsoft.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.debug.Path;

public class VisitRestore extends DocumentRestore {
	public VisitRestore() {
		super(VisitDoc.instance(), "VisitInfo");
	}
	
	@Override
	protected void beforeWrite(DataObject src) {
		super.beforeWrite(src);

		Visit dobj = (Visit) src;
		File path = new File(Path.getDataDir());
		path.mkdir();
		
		int ctr = 0;
		for(VisitItem item : dobj.items) {
			File file = new File(Path.getDataDir(), Long.toString(dobj.created.getTime())+ '_' + Integer.toString(ctr++));
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(item.id);
				item.id = file.getAbsolutePath().getBytes();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
