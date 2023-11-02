package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.IncassImpl;

public class IncassListSend extends IncassListBase {
	
	public List<IncassEx> items;
	
	public void setData(IncassListData base) {
		created = base.created;
		remark = base.remark;
		number = base.number;
		params = base.params;

		items = new ArrayList<IncassEx>();
		
		IncassImpl ii = new IncassImpl();
		IncassEx doc = (IncassEx) ii.getData();
		for(IncassListItem i : base.items) {
			doc.created = i.created;
			if( ii.read() ) {
				IncassEx dest = (IncassEx) doc.clone();
				if( dest != null )
					items.add(dest);
			}
		}
		ii.close();
	}
}
