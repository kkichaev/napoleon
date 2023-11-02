package com.grsoft.dataobjects.impl;

import java.io.File;

import android.content.Context;

import com.grsoft.dataobjects.Responce;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;


public class ResponceImpl extends CreatableDocument<Responce> implements PhotoDocument {

	@Override
	public void open(Context context) { }

	@Override
	public void addPhoto(byte[] photo) {
		if( isEditable() ) {
			VisitItem visitItem = new VisitItem();
			visitItem.id = photo;
			getData().items.clear();
			getData().items.add(visitItem);
			write();
			close();
		}
	}

	@Override
	public long size() {
		long result = super.size();
		Responce visit = getData();
		
		if (visit != null && visit.items != null && visit.items.size() > 0)
			for(VisitItem vi : visit.items){
				File file = new File(new String(vi.id));
				result += file.length();
			}
		
		return result;
	}

	@Override
	public int count() { return data.items.size();	}

}
