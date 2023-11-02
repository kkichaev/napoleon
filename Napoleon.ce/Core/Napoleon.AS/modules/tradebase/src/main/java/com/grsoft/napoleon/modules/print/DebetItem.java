package com.grsoft.napoleon.modules.print;

import java.util.Date;

import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.Document;

public class DebetItem implements Comparable<DebetItem> {
	public DocList docs;
	public int index;
	
	public boolean isDelivery;
	public Date date;
	public String number = "";
	
	public Document<?> getDocument() { return docs.get(index); }

	@Override
	public int compareTo(DebetItem another) {
		int cmp = date.compareTo(another.date);
		return (cmp != 0) ? cmp : 
				(isDelivery == another.isDelivery) ? number.compareTo(another.number) : 
				(isDelivery) ? -1 : 1;
	}
}
