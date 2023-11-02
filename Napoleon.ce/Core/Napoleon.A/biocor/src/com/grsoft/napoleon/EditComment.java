package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgFolderItemEx;
import com.grsoft.dataobjects.impl.OrgFoldersImpl;
import com.grsoft.napoleon.dialogs.SelectDialog;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

public class EditComment extends SelectDialog {
	public static final String REFRESH_ACTION = "com.grsoft.napoleon.SelectDialog.RefreshAction";

	OrgFoldersImpl doc;
	OrgFolderItemEx item;
	
	public void setData(OrgFoldersImpl doc, OrgFolderItemEx item) {
		this.doc = doc;
		this.item = item;
	}
	
	@Override
	public void onOKButtonPressed(View result) {
		EditText ed = (EditText)result.findViewById(R.id.edComment);
		item.comment = ed.getText().toString();
		doc.write();
		Intent i = new Intent(REFRESH_ACTION);
		getActivity().sendBroadcast(i);
	}

	@Override public int getTitle() { return R.string.input_remark; }
	@Override public int getViewId() { return R.layout.edit_comment; }

	@Override
	public void prepareView(View view) {
		EditText ed = (EditText)view.findViewById(R.id.edComment);
		ed.setText(item.comment);
	}
}
