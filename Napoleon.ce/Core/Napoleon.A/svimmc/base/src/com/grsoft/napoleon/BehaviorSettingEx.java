package com.grsoft.napoleon;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import com.grsoft.napoleon.modules.print.TextPrinter;
import com.grsoft.util.CfgNplEx;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class BehaviorSettingEx extends BehaviorSetting {
	EditText edUnfireRest;
	
	@Override
	protected int getContentViewID() {
		return R.layout.behavior_settingex;
	}
	
	@Override
	protected void init() {
		super.init();
		
		CfgNplEx cfg = (CfgNplEx)config;
		edUnfireRest = (EditText) findViewById(R.id.edUnfireRest);
		edUnfireRest.setText(Integer.toString(cfg.unfire_rest));
	
		SortedMap<String, Charset> cs = Charset.availableCharsets();
		List<String> values = new ArrayList<String>(); 
		int selected = -1;
		for(Entry<String,Charset> e : cs.entrySet()) {
			if( e.getValue().aliases().contains(cfg.encoding) )
				selected = values.size();
			values.add(e.getKey());
		}
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, values);
		Spinner sp = (Spinner)findViewById(R.id.spEncoding);
		sp.setAdapter(aa);
		if( selected >= 0 )
			sp.setSelection(selected);
	}
	
	@Override
	public void save() {
		try{
			CfgNplEx cfg = (CfgNplEx)config;
			cfg.unfire_rest = Integer.parseInt(edUnfireRest.getText().toString());
			Spinner sp = (Spinner)findViewById(R.id.spEncoding);
			cfg.encoding = (String)sp.getSelectedItem();
			TextPrinter.OUTPUT_FILE_ENCODE = cfg.encoding;
			if( TextPrinter.OUTPUT_FILE_ENCODE.length() == 0 )
				TextPrinter.OUTPUT_FILE_ENCODE = "windows-1251";
		}catch(Exception e){
			e.printStackTrace();
		}
		
		super.save();
	}
}
