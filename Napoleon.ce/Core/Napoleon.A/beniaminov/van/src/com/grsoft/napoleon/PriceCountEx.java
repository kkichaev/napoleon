package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.CheckBox;

public class PriceCountEx extends PriceCount {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox)findViewById(R.id.cbPackets)).setText("штуками");
	}
}
