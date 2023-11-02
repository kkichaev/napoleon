package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemValues;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class QuestSet extends QuestControl {

	private RadioGroup radioGroup;

	public QuestSet(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();

		RadioButton rb = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());

		if (rb != null) {
			AnswerItem a = createAnwerItem();
			a.answer = rb.getText().toString().trim();
			result.add(a);
		}

		return result;
	}

	@Override
	public void setValue(List<AnswerItem> value) {
		if (value.size() > 0) {
			for (int i = 0; i < radioGroup.getChildCount(); i++) {
				RadioButton rb = (RadioButton) radioGroup.getChildAt(i);

				if (rb.getText().toString().endsWith(value.get(0).answer)) {
					rb.setChecked(true);
					break;
				}
			}
		}
	}

	@Override
	void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		radioGroup = new RadioGroup(context);

		for (QuestionItemValues v : item.values) {
			RadioButton rb = new RadioButton(context);
			rb.setText(v.value);
			radioGroup.addView(rb);
		}

		container.addView(radioGroup);
	}
}
