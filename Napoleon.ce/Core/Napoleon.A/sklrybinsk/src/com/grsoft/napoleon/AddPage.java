package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class AddPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.addpage);

		init();
	}
	
	class ParamsListner implements DialogInterface.OnClickListener {

		private Context context;
		OrderEx order;
		View flags;
		
		ParamsListner(Context c) { context = c; }
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			OrderImpl o = CreateOrder.currentOrder();
			if( o == null )
				return;
			
			order = (OrderEx)o.getData();
			
			AlertDialog dlg = new AlertDialog.Builder(context).create();
			dlg.setTitle("Параметры");
			
			flags = View.inflate(context, R.layout.parampage, null);
			if( (order.params & OrderEx.ofDate) != 0 ) ((CheckBox)flags.findViewById(R.id.cbDate)).setChecked(true);
			if( (order.params & OrderEx.ofSert) != 0 ) ((CheckBox)flags.findViewById(R.id.cbSert)).setChecked(true);
			if( (order.params & OrderEx.ofQuality) != 0 ) ((CheckBox)flags.findViewById(R.id.cbQuality)).setChecked(true);
			
			dlg.setView(flags);
			dlg.setButton("Сохранить", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( ((CheckBox)flags.findViewById(R.id.cbDate)).isChecked() ) order.params |= OrderEx.ofDate;
					else order.params &= (~OrderEx.ofDate);

					if( ((CheckBox)flags.findViewById(R.id.cbSert)).isChecked() ) order.params |= OrderEx.ofSert;
					else order.params &= (~OrderEx.ofSert);

					if( ((CheckBox)flags.findViewById(R.id.cbQuality)).isChecked() ) order.params |= OrderEx.ofQuality;
					else order.params &= (~OrderEx.ofQuality);
				}
			});
			
			dlg.show();
		}
		
	}
	
	private void init() {
		OrderImpl order = CreateOrder.currentOrder();
		if( order == null )
			return;
		
		OrderEx o = (OrderEx)order.getData();
		
		EditText ed;
		ed = (EditText) findViewById(R.id.edNumTTN);
		ed.setText(o.collectNum);
		
		ed = (EditText) findViewById(R.id.edIncassSum);
		ed.setText(Util.IntToScaleStr(o.collectSum, Consts.SUM_SCALE));
		
		View btn = findViewById(R.id.btnDocs);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog dlg = new AlertDialog.Builder(v.getContext()).create();
				dlg.setTitle("Подтверждение");
				dlg.setMessage("Ненужная выписка документов задерживает работу склада!!!\nВы хотите выписать документы?");
				dlg.setButton("Да", new ParamsListner(v.getContext()));
				dlg.setButton2("Нет", (DialogInterface.OnClickListener)null);
				dlg.show();
			}});
	}

	public void update(OrderImpl order) {
		OrderEx o = (OrderEx)order.getData();

		EditText ed;
		ed = (EditText) findViewById(R.id.edNumTTN);
		o.collectNum = ed.getText().toString();
		
		ed = (EditText) findViewById(R.id.edIncassSum);
		o.collectSum = Util.StrToScale(ed.getText().toString(), Consts.SUM_SCALE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			CreateOrder.checkOrder();
		return super.onKeyDown(keyCode, event);
	}
}
