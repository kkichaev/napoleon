package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DocNumberStock;

public class DocNumberStockImpl extends DbObject<DocNumberStock> {
	public int nextDocNumber(String type){
		data.type = type;
		read();
		data.number += 1;
		write();
		close();
		
		return data.number;
	}
}
