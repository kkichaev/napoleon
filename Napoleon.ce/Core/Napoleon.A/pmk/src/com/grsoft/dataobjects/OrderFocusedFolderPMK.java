package com.grsoft.dataobjects;

import java.util.HashSet;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.FolderTree;

@TableInfo(name="OffPMK")
public class OrderFocusedFolderPMK extends DataObject {
	private HashSet<String> folders = null;
	public int count = 0;
	
	HashSet<String> getFolders() {
		if(folders == null) {
			folders = new HashSet<String>();
			FolderTree ft = new FolderTree();
			ft.load();
			
			if(ft.size() > 0) {
				Folder f = ft.get(0);
				folders.add(f.fid);
				for( int i=1; i<ft.size(); i++) {
					Folder cf = ft.get(i);
					if(cf.level == f.level)
						folders.add(cf.fid);
				}
			}
		}
		return folders;
	}
	
	public static OrderFocusedFolderPMK currentPlan() {
		OrderFocusedFolderPMK ret = new OrderFocusedFolderPMK();		
		
		StringBuilder value = new StringBuilder();
		ConfigImpl ci = new ConfigImpl();
		if( ci.getValue(value, "ПапокВЗаказе") ) {
			try {
				ret.count = Integer.parseInt(value.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	public static boolean isOrderCompleete(Order o) {
		OrderFocusedFolderPMK plan = currentPlan();
		
		HashSet<String> folders = plan.getFolders();
		
		PriceImpl pi = new PriceImpl();
		Price p = pi.getData();
		FolderTree ft = null;
		
		for(OrderItem item : o.items) {
			if(plan.count <= 0 )
				break;
			
			p.id = item.id;
			if( pi.read() ) {
				
				if(ft == null) {
					ft = new FolderTree();
					ft.load();
				}
				
				Folder f = ft.getFolder(p.folderID);
				while(f != null) {
					if(folders.contains(f.fid)) {
						plan.count--;
						folders.remove(f.fid);
						break;
					}
					f = ft.getParent(f);
				}
			}
		}
				
		pi.close();
		return plan.count == 0;
	}
}