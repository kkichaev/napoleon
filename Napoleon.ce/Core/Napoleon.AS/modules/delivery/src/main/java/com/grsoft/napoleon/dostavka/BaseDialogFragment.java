package com.grsoft.napoleon.dostavka;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class BaseDialogFragment extends DialogFragment{
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_white_square);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}
}
