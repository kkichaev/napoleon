package com.grsoft.napoleon.util;

import com.grsoft.napoleon.documents.DocumentSender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AskForSend {
	public static void askSend(Context context, final DocumentSender sender) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Вопрос");
		b.setMessage("Отправить документ?");
		b.setNegativeButton("Нет", null);
		b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				sender.execute((Void[])null);
			}
		});
		b.create().show();
	}
}
