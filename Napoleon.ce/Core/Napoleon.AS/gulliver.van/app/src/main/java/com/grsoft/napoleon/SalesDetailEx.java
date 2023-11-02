package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.modules.print.NPrinter;

public class SalesDetailEx extends SalesDetail {
	public static final String TR_NAKL_TITLE = "Транспортная накладная";
	public static final String TR_NAKL_NAME = "tr_nakl_1,tr_nakl_2";
	
	TextView tvNumber;
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.salesdetailex);
	}
	
	@Override
	protected String[] createPrintCaption() {
		OrgImpl oi = new OrgImpl();
		OrgEx oe = (OrgEx) oi.getData();
		oe.id = doc.getId();
		oi.read();
		oi.close();

		List<OrgDogovor> dogovors = oe.dogovors;
		String dogCode = ((SalesEx)doc.getData()).dogCode;
		boolean isGeneral = false;

		if (dogovors != null){
			for(OrgDogovor od : dogovors){
				if (dogCode.equals(od.id)){
					isGeneral = od.isGeneral();
					break;
				}
			}
		}

		List<String> list = new ArrayList<String>();
		if (isGeneral){
			list.add("ТТН ТОРГ 12");
			list.add(TR_NAKL_TITLE);
			list.add(NPrinter.UPD_CAPTION);			
		}else{
			list.add("Расходные накладные");
		}

		String[] result = new String[list.size()];
		result = list.toArray(result);

		return result;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		
		MenuItem item = menu.findItem(MNU_PKO_ID);
		
		if(item != null)
			item.setVisible(false);
		
		return result;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onResume() {
		super.onResume();
		
		tvNumber = (TextView) findViewById(R.id.tvNumber);
		tvNumber.setText(((OrderImplBase<? extends Sales>)doc).getData().number);
	}
}
