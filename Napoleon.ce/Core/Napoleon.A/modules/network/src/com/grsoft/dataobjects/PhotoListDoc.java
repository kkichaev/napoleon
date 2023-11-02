package com.grsoft.dataobjects;

import java.util.List;

public interface PhotoListDoc {
	public List<VisitItem> getItems();
	
	public void setItems(List<VisitItem> newItems);
	
	// server object name
	public String getDocName();

	// server object name
	public String getItemName();
}
