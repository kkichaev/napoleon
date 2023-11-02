package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemValues;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class QuestList extends QuestControl {
	List<CheckBox> controls = new ArrayList<CheckBox>();

	public QuestList(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();

		for (CheckBox cb : controls) {
			if (cb.isChecked()) {
				AnswerItem a = createAnwerItem();
				a.answer = cb.getText().toString();
				result.add(a);
			}
		}

		return result;
	}

	@Override
	public void setValue(List<AnswerItem> value) {
		Map<String, CheckBox> map = new HashMap<String, CheckBox>();

		for (CheckBox cb : controls) {
			if (!map.containsKey(cb.getText().toString().trim()))
				map.put(cb.getText().toString().trim(), cb);
		}

		for (AnswerItem i : value)
			if (map.containsKey(i.answer.trim()))
				map.get(i.answer.trim()).setChecked(true);
	}

	@Override
	void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		for (QuestionItemValues v : item.values) {
			CheckBox cb = new CheckBox(context);
			cb.setText(v.value);
			int pd =  (int) context.getResources().getDimension(R.dimen.quest_list_item_padding);
			cb.setPadding(0, pd, 0, pd);
			container.addView(cb);
			controls.add(cb);
		}
	}
}
