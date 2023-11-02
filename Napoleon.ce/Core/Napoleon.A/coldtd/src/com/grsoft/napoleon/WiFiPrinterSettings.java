package com.grsoft.napoleon;

import android.os.Bundle;
import android.widget.EditText;

import com.grsoft.napoleon.modules.print.util.BTPrinterHelper;
import com.grsoft.napoleon.modules.print.util.BTPrinterSettings;
import com.grsoft.napoleon.utl.WiFiPrinterConfig;
import com.grsoft.util.SettingActivity;

public class WiFiPrinterSettings extends SettingActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_prn_settings);
		
		update();
	}
	
	@Override
	public void save() {
		WiFiPrinterConfig cfg = WiFiPrinterConfig.get(this);

		EditText ed;
		ed = (EditText)findViewById(R.id.edIp);
		cfg.ip = ed.getText().toString();

		ed = (EditText)findViewById(R.id.edPort);
		cfg.port = Integer.parseInt(ed.getText().toString());

		ed = (EditText)findViewById(R.id.edCopies);
		cfg.copies = Integer.parseInt(ed.getText().toString());
		
		try {
			ed = (EditText) findViewById(R.id.edRowCount);
			BTPrinterSettings bcfg = BTPrinterHelper.getSettings(this);
			bcfg.row_count = Integer.parseInt(ed.getText().toString());
			BTPrinterHelper.saveSettings(bcfg, this);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		cfg.put(this);
	}

	@Override
	public void update() {
		WiFiPrinterConfig cfg = WiFiPrinterConfig.get(this);
		EditText ed;
		ed = (EditText)findViewById(R.id.edIp);
		ed.setText(cfg.ip);
		
		ed = (EditText)findViewById(R.id.edPort);
		ed.setText(Integer.toString(cfg.port));

		ed = (EditText)findViewById(R.id.edCopies);
		ed.setText(Integer.toString(cfg.copies));
	
		BTPrinterSettings bcfg = BTPrinterHelper.getSettings(this);
		ed = (EditText)findViewById(R.id.edRowCount);
		ed.setText(Integer.toString(bcfg.row_count));
	}

	@Override public int getName() { return R.string.print_settings; }
	@Override public int getIcon() { return R.drawable.print; }
}
