package com.grsoft.napoleon.util;

import java.util.List;

import com.grsoft.napoleon.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;

public class CommentChoice {
	EditText editor;
	
	public CommentChoice(EditText editor) {
		this.editor = editor;
		editor.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				choiceComment();
				return false;
			}
		});
	}

	protected void choiceComment() {
		Context ctx = editor.getContext();
		AlertDialog.Builder b = new AlertDialog.Builder(ctx);
		final List<String> values = CommentList.getCommentList(ctx);
		
		b.setTitle(R.string.select_comment);
		
		b.setItems(values.toArray(new CharSequence[0]), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) { 
				editor.setText(values.get(arg1));
				arg0.dismiss();
			}
			
		});
		b.create().show();
	}
}
