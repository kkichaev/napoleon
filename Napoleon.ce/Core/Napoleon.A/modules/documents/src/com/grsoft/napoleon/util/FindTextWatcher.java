package com.grsoft.napoleon.util;

import java.util.Timer;
import java.util.TimerTask;

import com.grsoft.napoleon.Features;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

public class FindTextWatcher implements TextWatcher {
	//TO-DO надо его отвязать от визуальных елементов, а событие когда сделать поиск в адаптере сделать broadcast
	public static int TIMER_DELAY = 1000;

	String curValue;
	// FilterAdapter adapter = null;
	protected EditText findField;
	Timer timer = null;
	private boolean blocked = false;
	FilterAdapter adapter;
	ListView listView;

	public FindTextWatcher(EditText findField, ListView listView) {
		this.listView = listView;
		this.findField = findField;
		curValue = findField.getText().toString();
	}

	public FindTextWatcher(EditText findField, FilterAdapter adapter) {
		this.adapter = adapter;
		this.findField = findField;
		curValue = findField.getText().toString();
	}
	
	private FilterAdapter getAdapter() {
		return (FilterAdapter) (adapter != null ? adapter : listView.getAdapter());
	}
	
	public void blockListner(boolean block) {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

		blocked = block;
	}
	
	protected void doSearch(FilterAdapter adapter, String val) {
		curValue = val;
		if (val.length() == 0)
			adapter.resetFilter();
		else {
			if(Features.MULTI_WORD_SEARCH)
				val = val.replace(' ', '%');
			adapter.applyFilter(val);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (blocked)
			return;

		if (timer != null)
			timer.cancel();

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timer.cancel();
				timer = null;

				findField.post(new Runnable() {
					@Override
					public void run() {
						FilterAdapter fa = getAdapter();
						
						if (fa != null) {
							String val = findField.getText().toString();
							if (val.equals(curValue) == false) {
								doSearch(fa, val);
							}
						}
					}
				});
			}
		}, TIMER_DELAY);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
