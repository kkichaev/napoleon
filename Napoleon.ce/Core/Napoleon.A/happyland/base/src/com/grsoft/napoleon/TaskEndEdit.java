package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.TaskInfo;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;


public class TaskEndEdit extends TaskStartEdit {
	
	SQLiteStatement stm;
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, TaskEndEdit.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StringBuilder sqlCmd = new StringBuilder();
		sqlCmd.append("UPDATE \"");
		sqlCmd.append(DataObjectInfo.getInstance().getTableName(TaskInfo.class));
		sqlCmd.append("\" SET ");
		sqlCmd.append("done=?, donedate=?, params=0");
		sqlCmd.append(" WHERE id=? and idgr=? and date=?");
		
		try{
			stm = DataBaseManager.getDataBase().compileStatement(sqlCmd.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		CreateTask.open(this, doc.getRowid());
		super.onBackPressed();
	}
	
	@Override
	protected int getItemLayout() {	return R.layout.taskendedit_row; }
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(isFinishing()){
			if(stm != null)
				stm.close();
		}
	}
	
	@Override
	public void updateView(TaskInfo ti, View convertView) {
		CheckBox cbDone = (CheckBox) convertView.findViewById(R.id.cbDone);
		cbDone.setTag(ti);
		cbDone.setChecked(ti.done == 1);
		
		cbDone.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				TaskInfo t = (TaskInfo) buttonView.getTag();
				int d = isChecked ? 1 : 0;
				
				if( d != t.done){
					stm.bindLong(1, d);
					stm.bindLong(2, Util.getDate().getTime());
					stm.bindString(3, t.id);
					stm.bindString(4, t.idgr);
					stm.bindLong(5, t.date.getTime());
					
					stm.execute();
				}
			}
		});
	}
}
