package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;

import android.content.Context;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class QuestText extends QuestControl {

	private EditText editText;

	public QuestText(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();
		String val = editText.getText().toString().trim();

		if (val.length() > 0) {
			AnswerItem a = createAnwerItem();
			a.answer = val;
			result.add(a);
		}
		
		return result;
	}

	@Override
	public void setValue(List<AnswerItem> value) {
		if (value.size() > 0)
			editText.setText(value.get(0).answer);
	}

	@Override
	void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		editText = new EditText(context);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		editText.setFilters(EmojiFilter.getFilter());
		editText.setHint("¬ведите текст");
		editText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		container.addView(editText);
	}
}
