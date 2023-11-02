package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AnswerEx;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.network.DocExportListener;

public abstract class Answerable<T extends CreateDocDataObject> extends
		CreatableDocument<T> {
	public AnswerEx findAnswer(String item, String quest) {
		DbReader reader = new DbReader();
		AnswerEx data = new AnswerEx();
		StringBuilder where = new StringBuilder();
		where.append("created=").append(getData().created.getTime())
				.append(" and price='").append(item).append("' and question='")
				.append(quest).append("'");
		boolean bdo = reader.select(data, DataObjectInfo.getInstance()
				.getTableName(data.getClass()), where.toString());
		reader.close();
		if (bdo)
			return data;

		return null;
	}

	public abstract void add(AnswerEx answer);

	@Override
	public boolean delete() {
		boolean result = super.delete();

		try {
			SQLiteDatabase db = DataBaseManager.getDataBase();
			db.delete(
					DataObjectInfo.getInstance().getTableName(AnswerEx.class),
					"created=?",
					new String[] { Long.toString(data.created.getTime()) });

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public void setExported(boolean value) {
		try{
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String params;
			
			if (value)
				params = "params = params |" + ParamState.ofExported;
			else
				params = "params = params & " + ~ParamState.ofExported;
			
			db.execSQL(String.format("UPDATE answer set %s where created=?", params), new String[] {Long.toString(data.created.getTime())});
			super.setExported(value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected String getObjectName(){
		return "";
	}
	
	public List<DocExportListener> getSendedDocuments() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(getObjectName(), this));
		String where = "created=" + data.created.getTime();
		com.grsoft.napoleon.documents.DocList dl = new com.grsoft.napoleon.documents.DocList(AnswerImpl.class, 
				where, null);
		DocSendListner dsl = new DocSendListner(QuestionDoc.OBJ_NAME, dl);
		docs.add(dsl);
		
		return docs;
	}
}
