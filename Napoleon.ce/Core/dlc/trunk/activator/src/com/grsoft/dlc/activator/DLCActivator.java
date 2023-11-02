package com.grsoft.dlc.activator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class DLCActivator extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findViewById(R.id.btnGo).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		char[] suffix_array = new char[]{'á','à','á','à',' ','ñ',
				'å','ÿ','ë','à',' ','ã','î','ð','î','õ',' ','è',' ',
				'ñ','ê','à','ç','à','ë','à',' ','á','à','á','à',' ','î','õ'};
		
		String imei = ((EditText)findViewById(R.id.edQuery)).getText().toString().trim();
		String str = imei + new String(suffix_array);
		((EditText)findViewById(R.id.edAnswer)).setText(Integer.toString(str.hashCode()));
	}
}