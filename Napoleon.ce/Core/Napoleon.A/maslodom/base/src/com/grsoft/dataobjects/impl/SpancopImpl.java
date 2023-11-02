package com.grsoft.dataobjects.impl;

import java.util.List;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Spancop;
import com.grsoft.napoleon.documents.CreatableDocument;

public class SpancopImpl extends CreatableDocument<Spancop> {

	@Override
	public void open(Context context) {

	}
	
	public boolean read(String id){
		StringBuilder where = new StringBuilder();
		where.append("id='").append(id).append("'");
		List<Long> ids = DbReader.readIds(table, where.toString(), null);

		if(ids.size() > 0)
			return read(ids.get(0));
		
		return false;
	}

}
