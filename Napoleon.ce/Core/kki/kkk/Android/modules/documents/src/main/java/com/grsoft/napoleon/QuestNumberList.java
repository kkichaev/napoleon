package com.grsoft.napoleon;

import android.content.Context;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemValues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestNumberList extends QuestControl {
	List<LinearLayout> controls = new ArrayList<>();

	public QuestNumberList(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();

		for (LinearLayout view : controls) {
			EditText ed = view.findViewById(R.id.editText);
			TextView tv = view.findViewById(R.id.textView);

			AnswerItem a = createAnwerItem();
			a.answer = tv.getText().toString().trim();
			String val = ed.getText().toString().trim();
			if(item.optional == 0 && val.length() == 0) {
				result.clear();
				break;
			}
			a.remark = val;
			result.add(a);
		}

		return result;
	}

	// handle special case when two question items has same text
	@Override
	public void setValue(List<AnswerItem> value) {
		Map<String, List<LinearLayout>> map = new HashMap<>();

		for (LinearLayout view : controls) {
			TextView tv =view.findViewById(R.id.textView);
			String key = tv.getText().toString().trim();
			if (!map.containsKey(key))
				map.put(key, new ArrayList<LinearLayout>());
			map.get(key).add(view);
		}

		for (AnswerItem i : value) {
			String key = i.answer.trim();
			List<LinearLayout> ll = map.get(key);
			if (ll != null && ll.size() > 0) {
				EditText ed = ll.get(0).findViewById(R.id.editText);
				ed.setText(i.remark);
				ll.remove(0);
			}
		}
	}

	@Override
	void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		for (QuestionItemValues v : item.values) {
			LinearLayout line = new LinearLayout(context);
			line.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			line.setOrientation(LinearLayout.HORIZONTAL);

			EditText ed = new EditText(context);
			ed.setId(R.id.editText);
			ed.setInputType(InputType.TYPE_CLASS_NUMBER);
			LinearLayout.LayoutParams pp = new LinearLayout.LayoutParams((int)context.getResources().getDimension(R.dimen.quest_edit_text_width),
					LinearLayout.LayoutParams.WRAP_CONTENT);
			pp.setMargins(0, 0, (int)context.getResources().getDimension(R.dimen.quest_edit_text_padding), 0);

			ed.setLayoutParams(pp);

			line.addView(ed);

			TextView tv = new TextView(context);
			tv.setId(R.id.textView);
			tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			tv.setText(v.value);

			line.addView(tv);

			container.addView(line);
			controls.add(line);
		}
	}
}
