package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;

public class FolderTree extends ArrayList<Folder> {

	private static final long serialVersionUID = 6872293710734029468L;

	public void load() {
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(Folder.class);
		Folder data = new Folder();
		boolean bdo = r.select(data, table, null, "id");
		while (bdo) {
			add(data);
			data = new Folder();
			bdo = r.selectNext(data);
		}
		r.close();
	}
	
	public Folder getFolder(int id) {
		int i = findFolder(id);
		return (i<0) ? null : get(i);
	}
	
	public int findFolder(int id) {
		int i=0;
		
		for(Folder cf : this) {
			if( cf.id == id )
				return i;
			i++;
		}
		
		return -1;
	}
	
	public Folder getParent(Folder f) {
		Folder ret = null;
		
		for(Folder fld : this) {
			if(fld.level < f.level)
				ret = fld;
			else if( fld.id == f.id )
				break;
		}

		return ret;
	}
}
