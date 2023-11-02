package com.grsoft.napoleon;

import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemValues;
import com.grsoft.dataobjects.impl.QuestionImpl;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class QuestAdapter extends BaseAdapter {
	private QuestionImpl quest = new QuestionImpl();
	private Context context;
	
	public QuestAdapter(Context context, long rowid) {
		this.context = context;
		quest.read(rowid);
		quest.close();
	}
	
	@Override
	public int getCount() {
		return quest.getData().items.size();
	}

	@Override
	public Object getItem(int position) {
		return quest.getData().items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		
		QuestionItem i = (QuestionItem) getItem(position);
		
		if (view == null) {
			view = View.inflate(context, R.layout.quest_item_view, null);
			
			if (i.type == QuestionItem.TEXT) {
				EditText ed = new EditText(context);
				ed.setInputType(InputType.TYPE_CLASS_TEXT);
				ed.setHint("¬ведите текст");
				((LinearLayout)view).addView(ed);
			}else if (i.type == QuestionItem.NUMBER) {
				EditText ed = new EditText(context);
				((LinearLayout)view).addView(ed);
				ed.setHint("¬ведите число");
				ed.setInputType(InputType.TYPE_CLASS_NUMBER);
			}else if (i.type == QuestionItem.LIST) {
				for(QuestionItemValues v : i.values) {
					CheckBox cb = new CheckBox(context);
					cb.setText(v.value);
					((LinearLayout)view).addView(cb);
				}
			}
		}
		
		TextView tv = (TextView) view.findViewById(R.id.tvText);
		tv.setText(i.text);
		
		return view;
	}

}
