package com.grsoft.view;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.Html;

import com.grsoft.napoleon.R;

public class SimpleMessageBox
{
	private String title;
	private String message;
	private Context context;
	private AlertDialog alertDialog;
	
	class ButtonInfo
	{
		public ButtonInfo(int index, String title, OnClickListener listner) {
			this.index = index;
			this.title = title;
			this.listner = listner;
		}
		
		int index;
		String title;
		DialogInterface.OnClickListener listner;
	};
	
	ArrayList<ButtonInfo> buttons;
		
	public  SimpleMessageBox (String message, Context context)
	{
		this(context.getString(R.string.error), message, context);
	}
	
	public SimpleMessageBox(String title, 
			String message, Context context)
	{
		this.title = title;
		this.message = message;
		this.context = context;
	}
	
	public void setButton(int index, String title, DialogInterface.OnClickListener listner) {
		if( buttons == null ) buttons = new ArrayList<ButtonInfo>();
		buttons.add(new ButtonInfo(index, title, listner));
	}

	public void show()
	{
		try
		{
			alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(Html.fromHtml(message));
			
			if( buttons != null ) {
				for( ButtonInfo b : buttons)
					alertDialog.setButton(b.index, b.title, b.listner);
			}

			alertDialog.show();
		}
		catch(Exception exception)
		{
			exception.printStackTrace();
		}
	}
	
	public void hide()
	{
		if (alertDialog != null && alertDialog.isShowing())
			try{
				alertDialog.dismiss();
			}catch(Exception e){}
	}
}
