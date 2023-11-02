package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.grsoft.dataobjects.AnswerItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.QuestionItem;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class QuestDataset extends QuestControl {

	private Spinner spinner;

	public QuestDataset(QuestionItem item) {
		super(item);
	}

	@Override
	public List<AnswerItem> getValue() {
		List<AnswerItem> result = new ArrayList<AnswerItem>();
		
		KeyValue kv = (KeyValue) spinner.getSelectedItem();
		
		if (kv != null && kv.key.length() > 0) {
			AnswerItem a = createAnwerItem();
			a.remark = item.values.get(0).value;
			a.answer = kv.key.toString();
			result.add(a);
		}
		
		return result;
	}

	@Override
	public void setValue(List<AnswerItem> value) {
		if (value.size() > 0) {
			String id = value.get(0).answer;
			
			for(int i = 0; i < spinner.getCount(); i++) {
				KeyValue kv = (KeyValue) spinner.getItemAtPosition(i);
				
				if (kv.key.toString().equals(id)) {
					spinner.setSelection(i, true);
					break;
				}
			}
		}
	}
	
	@Override
	void adjustView(Context context, ViewGroup layout, ViewGroup container) {
		if (item.values.size() > 0) {
			String dataset = item.values.get(0).value;
			
			final List<KeyValue> list = new ArrayList<KeyValue>();
			
			if (dataset.equals("Организация")) {
				DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {

					@Override
					public boolean travel(DataTraveler<Org> item) {
						KeyValue kv = new KeyValue(item.data.id, String.format("%s (%s)", item.data.name, item.data.address));
						list.add(kv);
						return true;
					}
				}, null);
			}else if (dataset.equals("Прайс")) {
				DataTraveler.travel(Price.class, new DataTraveler.Travel<Price>() {

					@Override
					public boolean travel(DataTraveler<Price> item) {
						KeyValue kv = new KeyValue(item.data.id, item.data.name);
						list.add(kv);
						return true;
					}
				}, null);
			}
			
			Collections.sort(list, new Comparator<KeyValue>() {

				@Override
				public int compare(KeyValue lhs, KeyValue rhs) {
					return lhs.value.toString().compareTo(rhs.value.toString());
				}
			});
			
			ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(context, R.layout.simple_spinner_layout, list);
			list.add(0, new KeyValue(""));
			
			spinner = new Spinner(context);
			spinner.setAdapter(aa);
			container.addView(spinner);
		}
	}
}
