package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.util.MainExceptionHandler;

public class NapoleonEx extends Napoleon {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new MainExceptionHandler(this));
	}
	
	@Override
	protected void showAbout() {
		 View messageView = getLayoutInflater().inflate(R.layout.about, null, false);
	        TextView tvLink = (TextView) messageView.findViewById(R.id.tvLink);
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setView(messageView);
	        builder.create();
	        final AlertDialog dialog = builder.show();
	        
	        tvLink.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							dialog.dismiss();
						}
					}).start();
				}
			});
	        
	       
	}
}
