package com.grsoft.napoleon;

import com.grsoft.dataobjects.WhPrice;
import com.grsoft.util.ExtrasConst;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class PriceReplace extends PriceList {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ListView lv = (ListView)findViewById(android.R.id.list);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				if( position >= price.size() )
					return;
				
				final WhPrice p = price.get(position);
				AlertDialog.Builder b = new AlertDialog.Builder(PriceReplace.this);
				b.setTitle("Заменить товар?");
				b.setMessage(p.name);
				b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent();
						i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, p.id);
						setResult(RESULT_OK, i);
						finish();
					}
				});
				b.setNegativeButton("Нет", null);
				b.create().show();
			}
		});
	}
}
