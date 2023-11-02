package com.grsoft.dataobjects.impl;

import java.io.File;

import android.content.Context;

import com.grsoft.dataobjects.DefectReport;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.DefectReportEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;


public class DefectReportImpl extends CreatableDocument<DefectReport> 
implements PhotoDocument{

	@SuppressWarnings("deprecation")
	@Override
	public boolean isExported() { 
		if ((data.flags & ParamState.ofExported) == ParamState.ofExported)
			return true;
		else return super.isExported();
	}
	
	@Override
	public void open(Context context) { 
		DefectReportEdit.open(context, this); 
	}

	@Override
	public boolean delete() {
		deleteSrcItems();
		return super.delete();
	}
	
	public void deleteSrcItems(){
		Visit visit = getData();
		
		if (visit.items == null)
			return;
		for(VisitItem vi : visit.items){
			File file = new File(new String(vi.id));
			file.delete();
		}
		
		visit.items.clear();
	}
	
	@Override
	public void addPhoto(byte[] photo) {
		VisitItem visitItem = new VisitItem();
		visitItem.id = photo;
		getData().items.add(visitItem);
		write();
		close();
	}
	
	@Override
	public long size() {
		long result = super.size();
		Visit visit = getData();
		
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
