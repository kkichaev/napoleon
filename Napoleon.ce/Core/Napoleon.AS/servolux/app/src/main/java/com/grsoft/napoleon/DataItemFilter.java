package com.grsoft.napoleon;

public abstract class DataItemFilter {
	abstract public boolean inSet(FiltrableDataItem item, FiltrableDataItem parent);
	abstract public boolean haveFolder(FiltrableDataItem parent);
	public void clear() {} 
}
