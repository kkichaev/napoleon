package com.grsoft.napoleon;

import android.view.View;
import com.grsoft.script.ScriptEdit;


public class ScriptEditEx extends ScriptEdit {

	protected void onResume() {
		super.onResume();
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	};
}
