package com.grsoft.util;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.impl.DbObject;

public class FolderTree extends ArrayList<Folder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0;

	public void load(String table) {
		DbReader r = new DbReader();
		
		try {
			Folder data = (Folder) DbObject.getDataType(Folder.class).newInstance();
			boolean bdo = r.select(data, table, null, "id");
			while (bdo) {
				add(data);
				data = new Folder();
				bdo = r.selectNext(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		r.close();
	}
	
	public void load() {
		load(DataObjectInfo.getInstance().getTableName(Folder.class));
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
	
	public Folder getFolder(String fid) {
		int i = findFolder(fid);
		return (i<0) ? null : get(i);
	}

	public int findFolder(String fid) {
		int i=0;
		
		for(Folder cf : this) {
			if( cf.fid.equals(fid) )
				return i;
			i++;
		}
		
		return -1;
	}

	public List<Folder> getWithDescendats(int id) {
		List<Folder> ret = new ArrayList<>();

		int exitLevel = -1;
		for(Folder cf : this) {
			if(cf.id == id ) {
				exitLevel = cf.level;
				ret.add(cf);
				continue;
			}
			if(cf.level <= exitLevel) {
				break;
			}
			if(exitLevel >= 0) {
				ret.add(cf);
			}
		}

		return ret;
	}

	public List<Folder> getWithDescendats(String fid) {
		List<Folder> ret = new ArrayList<>();

		int exitLevel = -1;
		for(Folder cf : this) {
			if(cf.fid.equals(fid)) {
				exitLevel = cf.level;
				ret.add(cf);
				continue;
			}
			if(cf.level <= exitLevel) {
				break;
			}
			if(exitLevel >= 0) {
				ret.add(cf);
			}
		}

		return ret;
	}
}
