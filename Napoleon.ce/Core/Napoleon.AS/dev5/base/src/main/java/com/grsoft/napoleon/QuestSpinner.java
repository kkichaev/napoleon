package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.dataobjects.QuestionItemValues;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class QuestSpinner extends QuestControl {

	private Spinner spinner;

	public QuestSpinner(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();

		Object o = spinner.getSelectedItem();
		String answer = o.toString().trim();

		if (o != null && answer.length() > 0) {
			AnswerItem a = createAnwerItem();
			a.answer = answer;
			result.add(a);
		}

		return result;
	}

	@Override
	public void setValue(List<AnswerItem> value) {
		if (value.size() > 0) {
			String v = value.get(0).answer;

			for (int i = 0; i < spinner.getCount(); i++) {
				if (v.equals(spinner.getItemAtPosition(i).toString())) {
					spinner.setSelection(i, true);
					break;
				}
			}
		}
	}

	@Override
	public void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		if (item.values.size() > 0) {
			List<String> list = new ArrayList<String>();
			list.add("");

			for (QuestionItemValues v : item.values)
				list.add(v.value);

			ArrayAdapter<String> aa = new ArrayAdapter<String>(context, R.layout.simple_spinner_layout, list);
			spinner = new Spinner(context);
			spinner.setAdapter(aa);
			container.addView(spinner);
		}
	}

}
