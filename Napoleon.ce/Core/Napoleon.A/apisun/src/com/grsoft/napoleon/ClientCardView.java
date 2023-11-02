package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ClientCardImpl;
import com.grsoft.util.ExtrasConst;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;


public class ClientCardView extends Activity {
	public static void open(Context context, String id){
		Intent intent = new Intent(context, ClientCardView.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, id);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		WebView wv = new WebView(this);
		setContentView(wv);
		
		ClientCardImpl cci = new ClientCardImpl();
		cci.read("id", getIntent().getStringExtra(ExtrasConst.DOC_ROW_ID_STR));
		
		wv.loadDataWithBaseURL(null, cci.getData().html, "text/html", "utf-8", null);
	}
}
