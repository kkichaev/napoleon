package com.grsoft.util;
import com.grsoft.aceteam.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemValues;
import com.grsoft.aceteam.R;

public class AnswerHTML {
	private static final String SCRIPT = "%%script%%";
	protected static final String QUATE = "'quate'";
	private static final String DATASETNAME = "%%datasetname%%";
	private static final String DATASET = "%%dataset%%";
	protected static final String TAG = "QuestionWebView";
	private static final String ONLOAD = "%%onload%%";
	private final static String COMMITBUTTON = "%%commitbutton%%";
	private static final String COMMA_SEP = ",";
	private static final String DOT_SEP = ".";
	
	public static String makeHTML(Question question, Answer answer, boolean isEditable, Context context) {
		String html = question.html;
		
        InputStream is = context.getResources().openRawResource(R.raw.submit);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		
		try{
			String s = br.readLine();
			while(s != null){
				sb.append(s).append("\n");
				s = br.readLine();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	
		String init = "";
		if( answer != null ) {
			init = createInit(question, answer);
			html = html.replace(ONLOAD, "onLoad=\"init()\"");
			
			if(isEditable == false)
        		html = html.replaceAll(COMMITBUTTON, "");			
		} else
			html = html.replaceAll(ONLOAD, "");
	
		html = html.replaceAll(SCRIPT, sb.toString() + init.toString());
		StringBuilder commitBtn = new StringBuilder();
		commitBtn.append("<p><input type=\"button\" name=\"commit\" id=\"commit\" value=\""+ 
				context.getString(R.string.remember) + "\" onClick=\"doSubmit('")
			.append(question.idquest).append("')\"/></p>");
		
		html = html.replaceAll(COMMITBUTTON, commitBtn.toString());
        
        int pos = html.indexOf(DATASET);
        
        while(pos != -1){
        	html = insertDataSet(html, pos);
        	pos = html.indexOf(DATASET);
        }
	
        return html;
	}

	static String createInit(Question question, Answer answer) {
		StringBuilder result = new StringBuilder();
		result.append("\n\r<script type=\"text/javascript\">\n\r");
		result.append("function init() {\n\r");
		
		if (answer.items != null &&
				answer.items.size() > 0){
			for (AnswerItem item : answer.items){
				int answType = getAnswerType(question, item.iditem);
				
				//Log.d(TAG, item.id + '\t' + item.answer + '\t' + answType);
				
				switch(answType){
				case QuestionItem.NUMBER:
					result.append(makeFunctionForNumber(item.iditem, item.answer));
					break;
				case QuestionItem.TEXT:
				case QuestionItem.DATASET:
					result.append(makeFunctionForText(item.iditem, item.answer));
					break;
				case QuestionItem.LIST:
				case QuestionItem.SET:
				case QuestionItem.BOOLEAN:
					result.append(makeFunctionForList(question, item.iditem, item.answer));
					break;
				}
			}
		}
		
		result.append("}</script>\n\r");
		
		return result.toString();
	}
	
	static int getAnswerType(Question question, String id){
		int result = ExtrasConst.INVALID_ID;
		
		for (QuestionItem qi : question.items){
			if (qi.iditem.equals(id)){
				result = qi.type; 
			}
		}
		
		return result;
	}
	
	
	static Object makeFunctionForNumber(String id, String answer) {
		answer = answer.replace(COMMA_SEP, DOT_SEP);
		
		StringBuilder result = new StringBuilder();
		result.append("document.getElementById(\"")
		    .append(id).append("_0") 
			.append("\").value = \"")
			.append(replaceQuate(answer)).append("\";\n\r");
		
		return result.toString();
	}

	static Object makeFunctionForList(Question question, String id, String answer) {
		StringBuilder result = new StringBuilder();
		
		result.append("document.getElementById(\"")
			.append(id).append('_').append(getItemListIndex(question, id, answer))
			.append("\").checked = \"true\";\n\r");
		
		return result.toString();
	}
	
	static int getItemListIndex(Question question, String id, String val){
		int result = 0;
		
		for (QuestionItem qi : question.items){
			if (qi.iditem.equals(id)){
				result = 0;
				for(QuestionItemValues v : qi.values){
					result++;
					
					if(v.value.equals(val))
							break;
				}
			}
		}
		
		return result;
	}
	
	
	static String makeFunctionForText(String id, String answer){
		StringBuilder result = new StringBuilder();
		result.append("document.getElementById(\"")
		    .append(id).append("_0") 
			.append("\").value = \"")
			.append(replaceQuate(answer)).append("\";\n\r");
		
		return result.toString();
	}
	
	static String insertDataSet(String html, int pos) {
		int start = pos + DATASET.length();
		int end = html.indexOf('<', start);
		String nameid = html.substring(start, end);
		String[] nameidarr = nameid.split(DATASETNAME);
		String name = nameidarr[0];
		String id = nameidarr[1];
		
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"").append(id).append("\" ")
			.append("id=\"").append(id).append("\"").append(">");
		
		if( name.equals("Организация"))
			sb.append(makeOrgSelect());
		else if (name.equals("Прайс"))
			sb.append(makePriceSelect());
		
		sb.append("</select>");
		
		StringBuilder result = new StringBuilder();
		result.append(html.substring(0, pos));
		result.append(sb.toString());
		result.append(html.substring(end, html.length()));
		
		return result.toString();
	}

	static Object makePriceSelect() {
		SQLiteDatabase db = DataBaseManager.getDataBase();
		Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Price.class),
				new String[]{"id", "name"}, 
				null, null, null, null, null);
		
		StringBuilder result = new StringBuilder();
		
		while(c.moveToNext())
			result.append(insertOption(c.getString(c.getColumnIndex("id")),
					c.getString(c.getColumnIndex("name"))));
		
		c.close();
		
		return result.toString();
	}

	static String makeOrgSelect() {
		SQLiteDatabase db = DataBaseManager.getDataBase();
		Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Org.class),
				new String[]{"id", "name", "address"}, 
				null, null, null, null, null);
		
		StringBuilder result = new StringBuilder();
		
		while(c.moveToNext()){
			StringBuilder sb = new StringBuilder();
			sb.append(c.getString(c.getColumnIndex("name"))).append(" ")
				.append(c.getString(c.getColumnIndex("address")));
			result.append(insertOption(c.getString(c.getColumnIndex("id")),
					sb.toString()));
		}

		c.close();
		
		return result.toString();
	}

	static String insertOption(String value, String text){
		StringBuilder result = new StringBuilder("<option ");
		result.append("value=\"").append(replaceQuate(value)).append("\">")
			.append(text).append("</option>");
		
		return result.toString();
	}

	static String replaceQuate(String value) {
		return value.replaceAll("\"", QUATE);
	}
	
}
