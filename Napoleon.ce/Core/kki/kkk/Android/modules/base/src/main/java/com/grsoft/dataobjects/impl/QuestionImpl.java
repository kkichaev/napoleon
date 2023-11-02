package com.grsoft.dataobjects.impl;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Question;
import com.grsoft.napoleon.QuestAnswer;
import com.grsoft.napoleon.QuestionWebView;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;

public class QuestionImpl extends Document<Question> {

	@Override
	public void open(Context context) {
		String orgid = ((Activity)context).getIntent()
			.getStringExtra(ExtrasConst.ORG_ID_STR);

		if(hasAnswers(orgid))
			QuestAnswer.open(context, rowid, orgid);
		else
			QuestionWebView.open(context, getRowid(), orgid);
	}
	
	@Override
	public String getDescription(Context context) {
		return getData().name;
	}
	
	protected boolean hasAnswers(String id){
		boolean result = false;
		DbWriter.checkDBTable(DbObject.getDataType(Answer.class));
		SQLiteDatabase db = DataBaseManager.getDataBase();
		android.database.Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Answer.class), 
				new String[]{"rowid"}, "id=? and question=?", 
				new String[]{id, getData().idquest}, null, null, null);
		
		result = c.moveToFirst();
		c.close();
		return result;
	}
}
