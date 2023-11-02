package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import java.util.List;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.AnswerItemEx;
import com.grsoft.dataobjects.QuestionEx;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.napoleon.QuestControl;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuditEditEx extends AuditEdit {
	private TextView tvTask;
	
	@Override
	protected int getActionBarLayoutID() {
		return R.layout.auditedit_action_bar;
	}
	
	@Override
	protected void adjustChildActionBar(View v) {
		super.adjustChildActionBar(v);
		
		tvTask = (TextView) findViewById(R.id.tvTask);
		tvTask.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.tvTask)
			showTask();
		else
			super.onClick(v);
	}
	
	private void showTask() {
		if (answer.getData().agentid.trim().length() == 0)
			Toast.makeText(this, R.string.need_to_select_agent, Toast.LENGTH_SHORT).show();
		else 
			SPKTaskAgentActivity.open(this, answer.getData().agentid);
	}
	
	@Override
	protected void settingQuestControlView(View v, QuestionItem i) {
		v.findViewById(R.id.edType2).setVisibility(((QuestionEx)quest.getData()).type2 == 0 ? View.GONE : View.VISIBLE);
	}
	
	@Override
	protected void postProcess(List<AnswerItem> val, QuestControl c) {
		EditText ed = (EditText) c.view.findViewById(R.id.edType2);
		String type2 = ed.getText().toString().trim();
		
		for(AnswerItem ai : val) {
			((AnswerItemEx)ai).type2 = type2;
		}
	}
	
	@Override
	protected void setValueQuestControl(QuestControl iv, List<AnswerItem> list) {
		super.setValueQuestControl(iv, list);
		
		EditText ed = (EditText) iv.view.findViewById(R.id.edType2);
		ed.setText("");
		
		if (list.size() > 0)
			ed.setText(((AnswerItemEx)list.get(0)).type2);
	}
	
	@Override
	protected boolean isNotComplete(List<AnswerItem> val, QuestControl c) {
		boolean res = super.isNotComplete(val, c);
		
		if (!res && ((QuestionEx)quest.getData()).type2 != 0) {
			EditText ed = (EditText) c.view.findViewById(R.id.edType2);
			
			res = ed.getText().toString().length() == 0;
		}
		
		return res;
	}
}
