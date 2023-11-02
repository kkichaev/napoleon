package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.RoutePointImpl;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;


public class KeyInputDlg extends BaseDialogFragment {
	private View btnOk;
	private EditText edInput;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.keyinputdlg, container, false);
		btnOk = view.findViewById(R.id.btnOK);
		edInput = (EditText) view.findViewById(R.id.edInput);
		btnOk.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { ok(); }});
		return view;
	}

	protected void ok() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String oldKey = pref.getString(NapoleonApp.AUTORIZATION_KEY, "").trim();
		String key = edInput.getText().toString().trim();
		
		if(key.length() > 0 && !key.equals(oldKey)){
			if (!RoutePointImpl.isRouteComplete()) {
				Bundle args = new Bundle();
				args.putString(NapoleonApp.AUTORIZATION_KEY, key);
				getActivity().showDialog(R.id.load_new_route, args);
			}else {
				Editor ed = pref.edit();
				ed.putString(NapoleonApp.AUTORIZATION_KEY, key);
				ed.commit();
				((Main)getActivity()).doSync(true);
			}
		}if (key.equals(oldKey))
			getActivity().showDialog(R.id.route_is_loaded);
		
		dismiss();
	}
}
