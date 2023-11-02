package com.grsoft.napoleon;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.PlanRouteItem;
import com.grsoft.dataobjects.impl.ScriptImplEx;
import com.grsoft.script.ScriptEdit;

public class ScriptEditEx extends ScriptEdit {
	{
		doc = new ScriptImplEx();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		PlanRouteItem item = ScriptImplEx.getPlanItem(doc.getId());
		
		if(item != null && item.spectask.length() > 0){
			TextView tv = (TextView)findViewById(R.id.tvSpecTask);
			tv.setVisibility(View.VISIBLE);
			tv.setText(Html.fromHtml("Спецзадача: <i><font color='blue'>" + item.spectask + "</font></i>"));
		}
	}
	
	@Override
	protected int getLayoutid() {
		return R.layout.script_editex;
	}
}
