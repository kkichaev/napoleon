package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.RequestChek;
import com.grsoft.dataobjects.ReturnChekBack;
import com.grsoft.napoleon.ReturnCheckEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Util;

import android.content.Context;

public class ReturnChekBackImpl extends CreatableDocument<com.grsoft.dataobjects.ReturnChekBack> {

	@Override
	public void open(Context context) {
		ReturnCheckEdit.open(context, this);
	}

	public static ReturnChekBackImpl find(RequestChekImpl chek) {
		String stmt = "\"chek\" = " + Long.toString(chek.getData().created.getTime());
		List<Long> rids = DbReader.readIds(new ReturnChekBack().getTableName(), stmt, "");
		
		ReturnChekBackImpl ret = null;
		if(rids.size() > 0) {
			ret = new ReturnChekBackImpl();
			ret.read(rids.get(0));
		}
		return ret;
	}
	
	public static ReturnChekBackImpl createFrom(RequestChekImpl chek) {
		RequestChek src = chek.getData();
		src.handleStatus = ChekBase.CHEK_HAVE_RETURN;
		chek.write();
		
		ReturnChekBackImpl ret = new ReturnChekBackImpl();
		ReturnChekBack data = ret.data;
		data.date = Util.getDateTime();
		data.created = data.date;
		data.sum = src.sum;
		data.chek = src.created;
		data.id = src.id;
		ret.write();
		return ret;
	}
	
	@Override public long sum() { return data.sum; }
}
