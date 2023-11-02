package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.Answer;
import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.QuestionItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class QuestControl {
	public QuestionItem item;
	public View view;
	
	public QuestControl(QuestionItem item) {
		this.item = item;
	}
	
	public View createView(Context context) {
		ViewGroup view = (ViewGroup) View.inflate(context, R.layout.quest_item_view, null);
		TextView tv = (TextView) view.findViewById(R.id.tvText);
		tv.setText(item.text + " pos:" + Integer.toString(item.number));
		ViewGroup container = (ViewGroup) view.findViewById(R.id.container);
		adjustView(context, view, container);
		this.view = view;
		
		return view;
	}
	
	public abstract List<AnswerItem> getValue();
	public abstract void setValue(List<AnswerItem> value);
	
	protected AnswerItem createAnwerItem() {
		AnswerItem a = new AnswerItem();
		
		try {
			a = (AnswerItem) DataObjectInfo.getInstance().getListType(Answer.class, "items").newInstance();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		a.iditem = item.iditem;
		a.id = item.id;
		a.type = item.type;
		
		return a;
	}
	
	abstract void adjustView(Context context, ViewGroup layout, ViewGroup container);
}
