package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import com.grsoft.dataobjects.Delivery;
import com.grsoft.napoleon.IncassDebDistrEdit;
import com.grsoft.napoleon.R;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {

	@Override
	protected void send() {
		if( !docHaveErrors(true))
			super.send();
	}
	
	@Override
	public void onBackPressed() {
		if( !docHaveErrors(false))
			super.onBackPressed();
	}
	
	@Override protected boolean askClearSum() { return false; }
	
	boolean docHaveErrors(boolean toSend) {
		if(doc.isEditable()) {
			long sm = getSum();
			if(toSend && sm == 0) {
				return true;
			}
			if(!autoMode) {
				long sumdstr = 0;
				for(Entry<DlvKey, Long> kv : sums.entrySet())
					sumdstr += kv.getValue();
				if( sm != sumdstr) {
					Toast.makeText(this, "Распределите сумму по накладным", Toast.LENGTH_SHORT).show();
					return true;
				}
			}
		}
		return false;
	}
	
	@Override protected ItemsAdapter createAdapter() { return new Adapter(); }
	@Override protected Item createItem(Delivery d) { return new ItemEx(d);	}
	
	class ItemEx extends Item {
		public Date payDate;
		
		public ItemEx(Delivery d) {
			super(d);
			payDate = d.payDate;
		}
	}
	
	class Adapter extends IncassDebDistrEdit.ItemsAdapter {
		@Override
		protected void draw(Item item, View view) {
			super.draw(item, view);

			SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
			TextView tv;

			tv = (TextView)view.findViewById(R.id.tvDlvDate);
			String str = sd.format(item.dlv.date) + "<br/>" + sd.format(((ItemEx)item).payDate);
			tv.setText(Html.fromHtml(str));
			tv.setTextColor(item.dlv.color);
			
		}
	}
}
