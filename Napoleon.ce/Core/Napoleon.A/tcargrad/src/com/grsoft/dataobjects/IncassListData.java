package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.impl.IncassImpl;

@TableInfo(name="IncassList",keyFields="created")
public class IncassListData extends IncassListBase {
	public List<IncassListItem> items;
	
	public boolean IsExported() { return (params & ParamState.ofExported) != 0; }

	public int sum() {
		int s = 0;
		if( items != null ) {
			IncassImpl ii = new IncassImpl();
			Incass doc = ii.getData();
			for(IncassListItem i : items) {
				doc.created = i.created;
				if( ii.read() ) {
					s += doc.sum;
				}
			}
			ii.close();
		}
		return s;
	}

	public void setExported() {
		params |= ParamState.ofExported;
		if( items != null ) {
			IncassImpl ii = new IncassImpl();
			Incass doc = ii.getData();
			for(IncassListItem i : items) {
				doc.created = i.created;
				if( ii.read() ) {
					ii.setExported(true);
					ii.write();
				}
			}
			ii.close();
		}
	}
}
