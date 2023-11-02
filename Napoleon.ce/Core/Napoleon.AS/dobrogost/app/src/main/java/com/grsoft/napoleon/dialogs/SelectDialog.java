package com.grsoft.napoleon.dialogs;

import com.grsoft.napoleon.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

public abstract class SelectDialog extends DialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View result = inflater.inflate(getViewId(), container, false);
		getDialog().setTitle(getTitle());
		prepareView(result);
		result.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onOKButtonPressed(result);
				dismiss();
			}
		});
		
		result.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		return result;
	}

	public abstract void onOKButtonPressed(View result);
	public abstract  int getViewId();
	public abstract void prepareView(View view);
	public abstract int getTitle();
}
