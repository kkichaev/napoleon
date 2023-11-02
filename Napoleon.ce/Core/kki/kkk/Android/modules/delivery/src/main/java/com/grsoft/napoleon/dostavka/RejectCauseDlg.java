package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.impl.ConfigImpl;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class RejectCauseDlg extends BaseDialogFragment{
	public static final String REJECT_CAUSE = "reject_cause"; 
	private Spinner spCause;
	private EditText edRemark;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.rejectcausedlg, container, false);
		spCause = (Spinner) view.findViewById(R.id.spCause);
		edRemark = (EditText) view.findViewById(R.id.edRemark);
		
		View btnOK = view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { okClick();	}});
		
		List<String> values = new ArrayList<String>();
		values.add(getString(R.string.myreject_cause));
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		cfg.getValue(sb, "RejectCause");
		
		for (String s : sb.toString().split(";"))
			if(s.trim().length() > 0)
				values.add(s);
		
		ArrayAdapter<String> a = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
		spCause.setAdapter(a);
		spCause.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { 
				edRemark.setEnabled(position == 0);
				
				if(position > 0)
					edRemark.setText(parent.getItemAtPosition(position).toString());
			}
			@Override public void onNothingSelected(AdapterView<?> parent) {}}
		);
		
		return view;
	}

	protected void okClick() {
		String remark = edRemark.getText().toString().trim();
		
		if(spCause.getSelectedItemPosition() == 0 && remark.length() == 0)
			Toast.makeText(getActivity(), R.string.need_input_text, Toast.LENGTH_SHORT).show();
		else{
			Intent i = new Intent();
			i.putExtra(REJECT_CAUSE, remark);
			getTargetFragment().onActivityResult(R.id.cause_dlg_result, Activity.RESULT_OK, i);
			dismiss();
		}
	}
}
