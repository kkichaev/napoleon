package com.grsoft.manager;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.impl.MAnswerImpl;
import com.grsoft.dataobjects.impl.MQuestionImpl;
import com.grsoft.manager.documents.MAnswerDoc;
import com.grsoft.napoleon.QuestControl;
import com.grsoft.napoleon.QuestControlsFactory;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestEdit extends FragmentActivity {
	private static final String ANSWER_ROW_ID = "answerrowid";
	private View btnSave;
	private List<QuestControl> controls = new ArrayList<QuestControl>();
	
	private MAnswerImpl answer = (MAnswerImpl) MAnswerDoc.instance().create();
	private MQuestionImpl quest = new MQuestionImpl();
	private String orgid = "";
	private static final String COUNTER = "counter";
	private String storePath = "";
	private static final int CAMERA_ACTIVITY = 0x181212; //1;
	private TextView tvName;
	private View btnAttach;

	public static void open(Context context, MAnswerImpl answer) {
		Intent i = new Intent(context, QuestEdit.class);
		i.putExtra(ExtrasConst.ROW_ID_FIELD, answer.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.quest_layout);

		View v = getLayoutInflater().inflate(R.layout.action_bar, null);
		TextView tv = (TextView) v.findViewById(R.id.tvTitle);
		tv.setText(getString(R.string.quest_doc_title));

		ActionBar a = getActionBar();
		a.setCustomView(v);
		a.setDisplayShowTitleEnabled(false);
		a.setDisplayShowCustomEnabled(true);

		btnSave = findViewById(R.id.btnSave);
		tvName = (TextView) findViewById(R.id.tvName);
		btnAttach = findViewById(R.id.btnAttach);

		long rid = getIntent().getLongExtra(ExtrasConst.ROW_ID_FIELD, ExtrasConst.INVALID_ROWID);


		
		answer.read(rid);
		answer.close();

		quest.getData().idquest = answer.getData().question;
		quest.read();
		quest.close();
		quest.close();
		
		Map<String, List<AnswerItem>> map = new HashMap<String, List<AnswerItem>>();
		
		for(AnswerItem i : answer.getData().items) {
			if (!map.containsKey(i.iditem))
				map.put(i.iditem, new ArrayList<AnswerItem>());
			
			map.get(i.iditem).add(i);
		}
		
		orgid  = getIntent().getStringExtra(ExtrasConst.ORG_ID_STR);

		LinearLayout holder = findViewById(R.id.holder);
		
		tv = findViewById(R.id.tvDescr);
		tv.setText(quest.getData().text);

		QuestControlsFactory factory = QuestControlsFactory.getInstance();
		
		Collections.sort(quest.getData().items, new Comparator<QuestionItem>() {

			@Override
			public int compare(QuestionItem lhs, QuestionItem rhs) {
				return lhs.number - rhs.number;
			}
		});

		for (int pos = 0; pos < quest.getData().items.size(); pos++) {
			QuestionItem i = quest.getData().items.get(pos);
			QuestControl iv = factory.createItem(i);

			if (iv != null) {
				v = iv.createView(this);
				v.setBackgroundResource(pos % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
				
				int c = i.optional == 0 ? getResources().getColor(R.color.red) : getResources().getColor(R.color.green);
				v.findViewById(R.id.status).setBackgroundColor(c);

				holder.addView(v);
				
				if (map.containsKey(i.iditem))
					iv.setValue(map.get(i.iditem));
				
				controls.add(iv);
			}
		}
		
		tvName.setText(quest.getData().name);
	}

	protected boolean save() {
		boolean isOk = true;
		List<AnswerItem> items = new ArrayList<AnswerItem>();
		
		for(QuestControl c : controls){
			List<AnswerItem> val = c.getValue();
			
			
			if (c.item.optional == 0 && val.size() == 0) {
				isOk = false;
				break;
			}
			
			items.addAll(val);
		}
		
		if (!isOk)
			Toast.makeText(this, R.string.all_question_should_been_processed, 
					Toast.LENGTH_LONG).show();
		else {
			if (answer.getRowid() == ExtrasConst.INVALID_ID) {
				answer.init(this, orgid, GPSUtilNew.getLastKnownLocation());
				answer.getData().question = quest.getData().idquest;
				answer.getData().qname = quest.getData().name;
			}
			
			answer.getData().items = items;
			
			answer.write();
			answer.close();
		}

		return  isOk;
	}
}
