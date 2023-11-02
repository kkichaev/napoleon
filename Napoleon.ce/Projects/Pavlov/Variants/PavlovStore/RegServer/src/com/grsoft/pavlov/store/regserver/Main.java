package com.grsoft.pavlov.store.regserver;

import com.grsoft.pavlov.store.regserver.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Main extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		findViewById(R.id.btnGen).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				long req = readValue(((EditText)findViewById(R.id.edReq)).getText().toString());
				
				req = hash(req);
				EditText ed = (EditText)findViewById(R.id.edAnswer);
				String val = String.format("%04X-%04X-%04X-%04X", (int)(req>>48 & 0xFFFF), (int)(req>>32 & 0xFFFF), (int)(req>>16 & 0xFFFF), (int)(req & 0xFFFF));
//				String val = String.format("%04X-%04X-%04X-%04X", (int)(req>>0 & 0xFFFF), (int)(req>>16 & 0xFFFF), (int)(req>>32 & 0xFFFF), (int)(req>>48 & 0xFFFF));
				ed.setText(val);
			}
		});
	}

	protected long hash(long a) {
		  long b = 0xe08c1d668b756f82l, c = 0x9e3779b9l;

		  a -= b; a -= c; a ^= (c >> 43);
		  b -= c; b -= a; b ^= (a << 9);
		  c -= a; c -= b; c ^= (b >> 8);
		  a -= b; a -= c; a ^= (c >> 38);
		  b -= c; b -= a; b ^= (a << 23);
		  c -= a; c -= b; c ^= (b >> 5);
		  a -= b; a -= c; a ^= (c >> 35);
		  b -= c; b -= a; b ^= (a << 49);
		  c -= a; c -= b; c ^= (b >> 11);
		  a -= b; a -= c; a ^= (c >> 12);
		  b -= c; b -= a; b ^= (a << 18);
		  c -= a; c -= b; c ^= (b >> 22);

		  return c;
	}

	protected long readValue(String val) {
		String[] data = val.split("-");
		long res = 0;
		for(int i=0; i<data.length; i++) {
			String di = data[i];
			long part = Long.parseLong(di, 16);
			res = (res << 16) + part;
		}
		return res;
	}
}
