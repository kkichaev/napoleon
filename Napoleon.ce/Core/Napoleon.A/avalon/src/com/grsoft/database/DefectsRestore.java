package com.grsoft.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DefectReport;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.DefectReportDoc;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.SrcDataCounter;

public class DefectsRestore extends DocumentRestoreEx {
	public DefectsRestore() {
		super(DefectReportDoc.instance());
	}

	@Override
	protected void beforeWrite(DataObject dobj) {
		super.beforeWrite(dobj);

		for(VisitItem vi : ((DefectReport)dobj).items) {
			try{
				byte[] data = vi.id;
				File file = new File(Path.getDataDir(), Integer.toString(SrcDataCounter.getValue())); 
				OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
				fos.write(data);
				fos.close();
				vi.id = file.getAbsolutePath().toString().getBytes();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
