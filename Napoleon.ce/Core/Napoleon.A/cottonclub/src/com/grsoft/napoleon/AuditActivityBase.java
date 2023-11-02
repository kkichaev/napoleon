package com.grsoft.napoleon;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Cities;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Retails;
import com.grsoft.dataobjects.impl.Answerable;
import com.grsoft.dataobjects.impl.CitiesImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.RetailsImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.ExtrasConst;

public abstract class AuditActivityBase extends FragmentActivity {
	protected ListView list;
	protected ImageButton btnSend;
	
	protected CreatableDocument<? extends CreateDocDataObject> document;
	
	static void open(Context context, CreatableDocument<? extends CreateDocDataObject> document, Class<? extends Activity> activity) {
		Intent i = new Intent(context, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, document.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutID());
		list = (ListView) findViewById(R.id.list);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		document = createDocument();
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		document.read(rid);
		
		View v = findViewById(R.id.btnMakePhoto);
		if( v != null )
			v.setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) {makePhoto(); }
			});
		
		OrgImpl org = new OrgImpl();
		org.getData().id = document.getId();
		org.read();
		org.close();
		
		TextView tv = (TextView) findViewById(R.id.tvOrgInfo);
		tv.setText(org.getData().name);
		
		btnSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DocumentSender ds = new DocumentSender(v.getContext(), findViewById(R.id.btnSend),
						((Answerable<?>)document).getSendedDocuments());
				ds.execute((Void[])null);
			}
		});
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		document.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, document.getRowid());
	}
	
	protected abstract CreatableDocument<? extends CreateDocDataObject> createDocument();
	protected abstract int getLayoutID(); 
	
	protected void makePhoto() {
		File path = new File(Path.getDataDir());
		path.mkdir();
		File file = new File(Path.getDataDir(), makeFileName());
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivity(intent);
	}

	String stripString(String str) {
		StringBuilder res = new StringBuilder();
		for( int i=0; i<str.length(); i++ ) {
			char sym = str.charAt(i);
			res.append(Character.isLetterOrDigit(sym) ? sym : '_');
			
		}
		return res.toString();
	}

	String makeFileName() {
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = document.getId();
		oi.read();
		oi.close();
		
		CitiesImpl ci = new CitiesImpl();
		Cities c = ci.getData();		
		c.id = o.city;
		ci.read();
		ci.close();
		
		RetailsImpl ri = new RetailsImpl();
		Retails r = ri.getData();
		r.id = o.retail;
		ri.read();
		ri.close();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
		String res = r.name + "_" + c.name + "_" + o.name + "_" + sdf.format(new Date());
		return stripString(res);
	}
}
