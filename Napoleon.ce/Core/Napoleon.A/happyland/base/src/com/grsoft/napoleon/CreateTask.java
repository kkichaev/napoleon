package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.TaskInfo;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class CreateTask extends Activity {
	private LinearLayout llHole;
	private CreatableDocument<? extends CreateDocDataObject> doc;
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, CreateTask.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createtask);
		
		llHole = (LinearLayout) findViewById(R.id.llHole);
		
		doc = (CreatableDocument<? extends CreateDocDataObject>) DocType.getCurDoc().create();
		doc.read(getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR));
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if (cfg.getValue(sb, TaskInfo.TASK_GROUP_KEY)){
			String[] arr = sb.toString().split(TaskInfo.TASK_GROUP_DELIMITER);
			SQLiteCursor cursor = null;
			DbWriter.checkDBTable(TaskInfo.class);
			
			final String WHERE = "select text from " + DataObjectInfo.getInstance().getTableName(TaskInfo.class) + " where done=0 and idgr=? and date = ?";
			
			try{
				for(String s : arr){
					TextView tv = new TextView(this);
					tv.setTextColor(getResources().getColor(R.color.black));
					tv.setText(s);
					tv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					
					EditText ed = new EditText(this);
					ed.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
					final int PADDING_BOTTOM_DP = 15;
					final int PADDING_BOTTOM_PX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, PADDING_BOTTOM_DP, getResources().getDisplayMetrics());;
					ed.setPadding(0, 0, 0, PADDING_BOTTOM_PX);
					ed.setTag(s);
					
					String[] args =  new String[]{s, Long.toString(Util.getDate().getTime())};
					
					if(cursor == null)
						cursor = (SQLiteCursor) DataBaseManager.getDataBase().rawQuery(WHERE, args);
					else{
						cursor.setSelectionArguments(args);
						cursor.requery();
					}
					
					if(cursor.moveToFirst())
						ed.setText(cursor.getString(cursor.getColumnIndex("text")));
					
					llHole.addView(tv);
					llHole.addView(ed);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(cursor != null)
				cursor.close();
		}else
			Toast.makeText(this, R.string.task_group_not_prezent, Toast.LENGTH_SHORT).show();;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing() && checkValues()){
			SQLiteStatement stm = null;
			try{
				StringBuilder sqlCmd = new StringBuilder();
				sqlCmd.append("INSERT OR REPLACE INTO ");
				sqlCmd.append(DataObjectInfo.getInstance().getTableName(TaskInfo.class));
				sqlCmd.append("(id,text,idgr,done,date,docdate,params ) VALUES (?,?,?,?,?,?,0)");
				stm = DataBaseManager.getDataBase().compileStatement(sqlCmd.toString());
				
				for (int i=0; i < llHole.getChildCount(); i++){
					View v = llHole.getChildAt(i);
					
					if(v instanceof EditText){
						EditText ed = (EditText)v;
						
						stm.bindString(1, doc.getId());
						stm.bindString(2, ed.getText().toString().trim());
						stm.bindString(3, ed.getTag().toString());
						stm.bindLong(4, 0);
						stm.bindLong(5, Util.getDate().getTime());
						stm.bindLong(6, doc.getData().created.getTime());
						
						stm.execute();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			if(stm != null)
				stm.close();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(checkValues())
			super.onBackPressed();
		else
			Toast.makeText(this, R.string.need_value, Toast.LENGTH_SHORT).show();
	}

	private boolean checkValues() {
		boolean result = true;
		
		
		for (int i=0; i < llHole.getChildCount(); i++){
			View v = llHole.getChildAt(i);
			
			if(v instanceof EditText && ((EditText)v).getText().toString().trim().length() == 0){
				result = false;
				break;
			}
		}
		
		return result;
	}
}
