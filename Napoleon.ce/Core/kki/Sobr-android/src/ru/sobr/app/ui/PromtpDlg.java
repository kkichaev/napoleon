package ru.sobr.app.ui;

import ru.sobr.app.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.CursorJoiner.Result;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

public class PromtpDlg extends DialogFragment {
	String msg;
	
	public PromtpDlg(String msg){
		this.msg = msg;
	}
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		View result = inflater.inflate(R.layout.promptdlg, container);
//		((TextView) result.findViewById(R.id.tvMsg)).setText(msg);
//		result.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				dismiss();
//			}
//		});
//		return result;
//	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder result = new  AlertDialog.Builder(getActivity());
		result.setTitle(R.string.message);
		result.setMessage(msg);
		result.setPositiveButton(R.string.ok, null);
		return result.create();
	}
}
