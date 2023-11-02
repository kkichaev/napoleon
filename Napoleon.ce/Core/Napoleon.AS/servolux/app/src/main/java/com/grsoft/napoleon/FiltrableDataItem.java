package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class FiltrableDataItem implements Comparable<FiltrableDataItem> {
	protected String name;
	protected String id;		
	protected String brand;
	protected String prefix = "";
	public String firm = "";
	
	List<FiltrableDataItem> childs;

	public List<FiltrableDataItem> getChilds() { return childs; }
	
	public void sort() {
		if(isFolder())
			Collections.sort(childs);
	}

	public boolean isFolder() { return childs != null; }
	
	@Override public int compareTo(FiltrableDataItem arg0) { return name.compareTo(arg0.name); }
	
	public void addChild(FiltrableDataItem ch) {
		if(childs == null)
			childs = new ArrayList<FiltrableDataItem>();
		childs.add(ch);
	}
	
	public abstract FiltrableDataItem createFolderItem();
	public abstract int getQty();
}
