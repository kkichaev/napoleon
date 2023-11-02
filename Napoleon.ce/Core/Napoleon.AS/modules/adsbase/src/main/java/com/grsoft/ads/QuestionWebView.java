package com.grsoft.ads;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.HashMap;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Toast;
import com.grsoft.ads.dataobjects.QuestAnswer;
import com.grsoft.ads.dataobjects.QuestAnswerItem;
import com.grsoft.ads.dataobjects.QuestionItem;
import com.grsoft.ads.dataobjects.QuestionItemValues;
import com.grsoft.ads.dataobjects.impl.QuestAnswerImpl;
import com.grsoft.ads.dataobjects.impl.QuestionImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;

public class QuestionWebView extends Activity {
	public static Class<? extends Activity> activity = QuestionWebView.class;
	private static final String SCRIPT = "%%script%%";
	protected static final String QUATE = "'quate'";
	private static final String DATASETNAME = "%%datasetname%%";
	private static final String DATASET = "%%dataset%%";
	protected static final String TAG = "QuestionWebView";
	private static final String ANSWER_ROW_ID = "answerrowid";
	private static final String ONLOAD = "%%onload%%";
	private final static String COMMITBUTTON = "%%commitbutton%%";
	private static final String TASKID = "taskid";
	
	QuestionImpl questionImpl = new QuestionImpl();
	protected String orgid = "";
	protected WebView webView;
	protected long answerrowid = ExtrasConst.INVALID_ID;
	private String taskid = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		
		Intent intent = getIntent(); 
		long rowid = intent.getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		orgid = intent.getStringExtra(ExtrasConst.ORG_ID_STR);
		answerrowid = intent.getLongExtra(ANSWER_ROW_ID, ExtrasConst.INVALID_ID);
		taskid = intent.getStringExtra(TASKID);
		
		if (rowid != ExtrasConst.INVALID_ID){
			questionImpl.read(rowid);
			questionImpl.close();
		}
        
        String html = questionImpl.getData().html;
        InputStream is = getResources().openRawResource(R.raw.submit);
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
		
		if (answerrowid != ExtrasConst.INVALID_ID){
        	
        	QuestAnswerImpl answerImpl = new QuestAnswerImpl();
        	
        	if (answerImpl.read(answerrowid)){
    			init = createInit(answerImpl.getData());
    			html = html.replace(ONLOAD, "onLoad=\"init()\"");
    			
    			if(answerImpl.isExported())
            		html = html.replaceAll(COMMITBUTTON, "");
        	}
        	
        	answerImpl.close();
		}else
			html = html.replaceAll(ONLOAD, "");
		
		html = html.replaceAll(SCRIPT, sb.toString() + init.toString());
		StringBuilder commitBtn = new StringBuilder();
		commitBtn.append("<p><input type=\"button\" name=\"commit\" id=\"commit\" value=\""+ 
				getString(R.string.remember) + "\" onClick=\"doSubmit('")
			.append(questionImpl.getData().idquest).append("')\"/></p>");
		html = html.replaceAll(COMMITBUTTON, commitBtn.toString());
        
        int pos = html.indexOf(DATASET);
        
        while(pos != -1){
        	html = insertDataSet(html, pos);
        	pos = html.indexOf(DATASET);
        }
        
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true); 
        webView.getSettings().setSupportZoom(true);
        webView.addJavascriptInterface(this, "Android");
                
        //--------BEGIN DEBUG SECTION ---------- 
/*		try {
			final String FILE_LOG = "quest.html";
			File f = new File( Environment.getExternalStorageDirectory().getPath(), FILE_LOG);
			OutputStream os = new FileOutputStream(f);
			os.write(html.getBytes());
			os.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
		//--------END DEBUG SECTION ----------
		
		Log.d(TAG, html); 
        webView.loadDataWithBaseURL(null, html, "text/html", null, null);
        webView.getSettings().setBuiltInZoomControls(true);
	}
	
	private String createInit(QuestAnswer answer) {
		StringBuilder result = new StringBuilder();
		result.append("\n\r<script type=\"text/javascript\">\n\r");
		result.append("function init() {\n\r");
		
		if (answer.items != null &&
				answer.items.size() > 0){
			for (QuestAnswerItem item : answer.items){
				int answType = GetAnswerType(item.iditem);
				
				Log.d(TAG, item.id + '\t' + item.answer + '\t' + answType);
				
				switch(answType){
				case QuestionItem.TEXT:
				case QuestionItem.NUMBER:
				case QuestionItem.DATASET:
					result.append(makeFunctionForText(item.iditem, item.answer));
					break;
				case QuestionItem.LIST:
				case QuestionItem.SET:
				case QuestionItem.BOOLEAN:
					result.append(makeFunctionForList(item.iditem, item.answer));
					break;
				}
			}
		}
		
		result.append("}</script>\n\r");
		
		return result.toString();
	}
	
	private Object makeFunctionForList(String id, String answer) {
		StringBuilder result = new StringBuilder();
		
		result.append("document.getElementById(\"")
			.append(id).append('_').append(getItemListIndex(id, answer))
			.append("\").checked = \"true\";\n\r");
		
		return result.toString();
	}
	
	private String makeFunctionForText(String id, String answer){
		StringBuilder result = new StringBuilder();
		result.append("document.getElementById(\"")
		    .append(id).append("_0") 
			.append("\").value = \"")
			.append(replaceQuate(answer)).append("\";\n\r");
		
		return result.toString();
	}

	private int getItemListIndex(String id, String val){
		int result = 0;
		
		for (QuestionItem qi : questionImpl.getData().items){
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
	
	private int GetAnswerType(String id){
		int result = ExtrasConst.INVALID_ID;
		
		for (QuestionItem qi : questionImpl.getData().items){
			if (qi.iditem.equals(id)){
				result = qi.type; 
			}
		}
		
		return result;
	}

	private String insertDataSet(String html, int pos) {
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

	private Object makePriceSelect() {
//		SQLiteDatabase db = DataBaseManager.getDataBase();
//		Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Price.class),
//				new String[]{"id", "name"}, 
//				null, null, null, null, null);
//		
		StringBuilder result = new StringBuilder();
		
//		while(c.moveToNext())
//			result.append(insertOption(c.getString(c.getColumnIndex("id")),
//					c.getString(c.getColumnIndex("name"))));
//		
//		c.close();
		
		return result.toString();
	}

	private String makeOrgSelect() {
//		SQLiteDatabase db = DataBaseManager.getDataBase();
//		Cursor c = db.query(DataObjectInfo.getInstance().getTableName(Org.class),
//				new String[]{"id", "name", "address"}, 
//				null, null, null, null, null);
//		
		StringBuilder result = new StringBuilder();
//		
//		while(c.moveToNext()){
//			StringBuilder sb = new StringBuilder();
//			sb.append(c.getString(c.getColumnIndex("name"))).append(" ")
//				.append(c.getString(c.getColumnIndex("address")));
//			result.append(insertOption(c.getString(c.getColumnIndex("id")),
//					sb.toString()));
//		}
//
//		c.close();
//		
		return result.toString();
	}
	
	@SuppressWarnings("unused")
	private String insertOption(String value, String text){
		StringBuilder result = new StringBuilder("<option ");
		result.append("value=\"").append(replaceQuate(value)).append("\">")
			.append(text).append("</option>");
		
		return result.toString();
	}

	protected String replaceQuate(String value) {
		return value.replaceAll("\"", QUATE);
	}
	
	
	public static void open(Context context, long rowid, String orgid){
		open(context, rowid, orgid, ExtrasConst.INVALID_ID);
	}
	
	public static void open(Context context, long rowid, String orgid, long answerrowid){
		open(context, rowid, orgid, ExtrasConst.INVALID_ID, "");
	}
	
	public static void open(Context context, long rowid, String orgid, long answerrowid, String taskid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		intent.putExtra(ExtrasConst.ORG_ID_STR, orgid); 
		intent.putExtra(ANSWER_ROW_ID, answerrowid);
		intent.putExtra(TASKID, taskid);
				
		context.startActivity(intent);
	}
	
	public void handle(String answer){
		Log.d(TAG, answer);
		String dansw = URLDecoder.decode(answer);
		dansw = dansw.replaceAll(QUATE, "\"");
		Log.d(TAG, dansw);
		String[] items = dansw.split("&");
		
		QuestAnswerImpl answerImpl = new QuestAnswerImpl();
		
		if (answerrowid == ExtrasConst.INVALID_ID){
			answerImpl.init(this, orgid, GPSUtilNew.getLastKnownLocation());
			answerImpl.getData().question = questionImpl.getData().idquest;
			answerImpl.getData().qname = questionImpl.getData().name;
			answerImpl.getData().task = taskid;
		} else {
			answerImpl.read(answerrowid);
			answerImpl.close();
			answerImpl.getData().items.clear();
		}
		
		HashMap<String, String> ids = new HashMap<String, String>();
		fillNecessaryQuestion(ids);
		
		HashMap<String, String> quests = new HashMap<String, String>();
		for(QuestionItem qi : questionImpl.getData().items)
			quests.put(qi.iditem, qi.id);

		
		for(String s : items){
			String[] nameVal = s.split("=");
			
			if (nameVal.length == 2){
				String id = nameVal[0].substring(0,nameVal[0].lastIndexOf('_'));
				String val = nameVal[1];
				
				QuestAnswerItem answerItem = new QuestAnswerItem();
				answerItem.iditem = id;
				answerItem.id = quests.get(id);
				answerItem.answer = val;
				
				for(QuestionItem qitem: questionImpl.getData().items){
					if (qitem.iditem.equals(id)){
						answerItem.type = qitem.type;
						
						if (qitem.type == QuestionItem.DATASET && qitem.values.size() > 0) 
							answerItem.remark = qitem.values.get(0).value;
					}
				}
				
				answerImpl.getData().items.add(answerItem);
				
				ids.remove(id);
			}
		}
		
		if (ids.size() > 0){
			Toast.makeText(this, R.string.all_question_should_been_processed, 
					Toast.LENGTH_LONG).show();
			
			if (answerrowid == ExtrasConst.INVALID_ID)
				answerImpl.delete();
			
		}else{
			answerImpl.write();
			answerImpl.close();
			finish();
		}
	}

	protected void fillNecessaryQuestion(HashMap<String, String> ids) {
		for(QuestionItem qi : questionImpl.getData().items)
			ids.put(qi.iditem, qi.id);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			
			if(answerrowid != ExtrasConst.INVALID_ID){
				QuestAnswerImpl answerImpl = new QuestAnswerImpl();
	        	
	        	if (answerImpl.read(answerrowid) &&
	        			answerImpl.getData().items.size() == 0)
	        			answerImpl.delete();
	        	
	        	answerImpl.close();
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
