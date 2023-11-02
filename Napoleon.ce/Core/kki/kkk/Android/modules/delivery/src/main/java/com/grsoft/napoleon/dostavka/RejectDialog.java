package com.grsoft.napoleon.dostavka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

public class RejectDialog extends BaseDialogFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.reject_dlg, null);

		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				String val = ((EditText)view.findViewById(R.id.edRemark)).getText().toString().trim();
				
				if(val.length() > 0) {
					((RejectAction)getActivity()).doReject(val);
					getActivity().finish();
				}
			}
		});

		view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		return view;
	}
}
