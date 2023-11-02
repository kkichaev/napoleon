package com.grsoft.prch_order;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import com.grsoft.database.DbWriter;
import com.grsoft.prch_order.dataobjects.ConfigHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Config extends Activity {
	
	static final int REQ_CONFIG = 1;
	static final int REQ_ORDER = 2;
	
	public static void show(Context context) {
		Intent i = new Intent(context, Config.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.config);
		
		EditText ed = (EditText)findViewById(R.id.edEmail);
		ed.setText(ConfigHelper.get(ConfigHelper.USER_EMAIL));
		
		findViewById(R.id.btnSelectConfig).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { selectFile(REQ_CONFIG);}
		});
		findViewById(R.id.btnSelectOrder).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { selectFile(REQ_ORDER);}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		String email = ((EditText)findViewById(R.id.edEmail)).getText().toString();
		com.grsoft.dataobjects.Config c = new com.grsoft.dataobjects.Config();
		c.key = ConfigHelper.USER_EMAIL;
		c.value = email;
		
		DbWriter w = new DbWriter();
		w.insertRecord(c);
		w.close();
	}

	protected void selectFile(int reqCode) {
		Intent intent = new Intent().setType("*/*").setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select a file"), reqCode);	
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			if(requestCode == REQ_CONFIG || requestCode == REQ_ORDER) {
				try {
					Uri selFile = data.getData();
					InputStream inputStream = getContentResolver().openInputStream(selFile);
					
					if(requestCode == REQ_CONFIG)
						ConfigHelper.update(inputStream);
					else {
						if(inputStream == null || !selFile.toString().toUpperCase().endsWith(".XLS")) {
							Toast.makeText(this, "Выбран не правильный файл", Toast.LENGTH_SHORT).show();
							return;
						}
						
						try {
							OutputStream out = new FileOutputStream(App.getOrderFile());
							App.copyFile(inputStream, out);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				return;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
