package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public abstract class QuestControl {
	public QuestionItem item;
	private Object owner;

	public QuestControl(QuestionItem item) {
		this.item = item;
	}
	
	public View createView(Context context) {
		QuestControlsFactory f = QuestControlsFactory.getInstance();
		ViewGroup view = (ViewGroup) View.inflate(context, f.getItemLayout(), null);
		TextView tv = (TextView) view.findViewById(R.id.tvText);
		tv.setText(item.text);
		ViewGroup container = (ViewGroup) view.findViewById(R.id.container);
		adjustView(context, view, container);

		return view;
	}
	
	public abstract List<AnswerItem> getValue();
	public abstract void setValue(List<AnswerItem> value);
	
	protected AnswerItem createAnwerItem() {
		AnswerItem a = new AnswerItem();
		a.iditem = item.iditem;
		a.id = item.id;
		a.type = item.type;
		
		return a;
	}

	abstract void adjustView(Context context, ViewGroup layout, ViewGroup container);

	public Object getOwner(){return owner;};
	public void setOwner(Object val){owner = val;}
}
