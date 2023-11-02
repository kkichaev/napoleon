package com.grsoft.napoleon;

import java.util.Comparator;

import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolderItemEx;

public class OrgFolderItemCmp implements Comparator<OrgFolderItem> {
	@Override
	public int compare(OrgFolderItem lhs, OrgFolderItem rhs) {
		String lt = ((OrgFolderItemEx)lhs).time;
		String rt = ((OrgFolderItemEx)rhs).time;
		
		if (lt.length() == 0 || rt.length() == 0)
			return lt.length() == 0 ? 1 : rt.length() == 0 ? -1 : lhs.name.compareTo(rhs.name); 

		String[] ls = lt.split(":");
		String[] rs = rt.split(":");
		
		int lv = 0;
		int rv = 0;
		
		try{
			if(ls.length == 2)
				lv = Integer.parseInt(ls[0]) * 60 + Integer.parseInt(ls[1]);
			
			if(rs.length == 2)
				rv = Integer.parseInt(rs[0]) * 60 + Integer.parseInt(rs[1]);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return lv - rv;
	}
}