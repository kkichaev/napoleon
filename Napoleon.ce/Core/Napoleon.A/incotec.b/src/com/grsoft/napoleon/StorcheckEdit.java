package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.SCActionItem;
import com.grsoft.dataobjects.Storcheck;
import com.grsoft.dataobjects.StorcheckActions;
import com.grsoft.dataobjects.StorcheckItem;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.StorcheckImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.StorcheckDoc;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class StorcheckEdit extends FragmentActivity implements SendResultListener {
	StorcheckImpl doc;
	StorcheckProp prop;
	StorcheckGoods goods;
	PriceImpl price = new PriceImpl();
	ItemAdapter items;
	
	public static void open(Context context, StorcheckImpl doc) {
		Intent i = new Intent(context, StorcheckEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.storcheck);
		
		long rowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc = new StorcheckImpl();
		doc.read(rowId);
		
		items = new ItemAdapter();
		
		ViewPager vp = (ViewPager)findViewById(R.id.stc_pager);
		Adapter adapter = new Adapter(getSupportFragmentManager());
		vp.setAdapter(adapter);
	
		View btnSend = findViewById(R.id.btnSend);
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(StorcheckDoc.instance().getObjectName(), doc.getData().created, doc.getId()))
				btnSend.setVisibility(View.GONE);
		}
		btnSend.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
		
		View add = findViewById(R.id.btnAddItems);
		add.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { Warehouse.open(arg0.getContext(), doc, true); }
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		items.notifyDataSetChanged();
	}
	
	protected void send() {
		saveData();
		
		new DocumentSender(this, findViewById(R.id.btnSend), 
				StorcheckDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doc.close();
		price.close();
	}
	
	@Override
	public void onBackPressed() {
		saveData();
		super.onBackPressed();
	}
	
	private void saveData() {
		if(doc.isEditable() == false)
			return;
		
		Storcheck d = doc.getData();
		if(prop != null)
			prop.setDoc(d);
		
		if(goods != null)
			goods.setDoc(d);
		
		doc.write();
	}
	
	Fragment getItem(int pos) {
		if(pos == 0) {
			prop = new StorcheckProp();
			return prop;
		}
		goods = new StorcheckGoods();
		return goods;
	}

	class Adapter extends FragmentPagerAdapter {

		public Adapter(FragmentManager fm) {
			super(fm);
		}

		@Override public Fragment getItem(int arg0) { return StorcheckEdit.this.getItem(arg0); }

		@Override public int getCount() { return 2; }
		
		@Override
		public CharSequence getPageTitle(int position) {
			return position == 0 ? "Свойства торг.точки" : "Товары";
		}
	}
	
	public class StorcheckProp extends Fragment {
		View view;
	
		public StorcheckProp() {}
		
		void setCheckBox(int id, int value, View v) {
			CheckBox cb = (CheckBox)v.findViewById(id);
			if(cb != null)
				cb.setChecked(value > 0);
		}
		
		int getCheckBox(int id) {
			return ((CheckBox)view.findViewById(id)).isChecked() ? 1 : 0;
		}
		
		void setProcentSpinner(int id, int value, View v) {
			Spinner sp = (Spinner)v.findViewById(id);
			value /= 10;
			if( sp != null && value < sp.getAdapter().getCount() )
				sp.setSelection(value);
		}
		
		void loadActions(int id, Date docDate, String value, View v) {
			StorcheckActions actions = StorcheckActions.get(docDate); 
			
			List<CharSequence> values = new ArrayList<CharSequence>();
			int sel = 0;
			values.add("");
			
			if(actions != null)
				for(SCActionItem i : actions.items) {
					if(i.name.equals(value))
						sel = values.size();
					values.add(i.name);
				}
			
			ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(StorcheckEdit.this, R.layout.simple_spinner_layout, values);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			Spinner s = (Spinner)v.findViewById(id);
			s.setAdapter(aa);
			s.setSelection(sel);			
		}
		
		int getPercentSpinner(int id) {
			Spinner sp = (Spinner)view.findViewById(id);
			return sp.getSelectedItemPosition() * 10;
		}
		
		public void setDoc(Storcheck d) {
			d.ho_best = getCheckBox(R.id.cbHO);
			d.showcase_best = getCheckBox(R.id.cbShowcase);
			d.corp_block = getCheckBox(R.id.cbCorpBlock);
			d.posm = getCheckBox(R.id.cbPOSM);
			
			Spinner sp;
			sp = (Spinner)view.findViewById(R.id.spAction);
			d.action = sp.getSelectedItem().toString();
			
			d.share_ki = getPercentSpinner(R.id.spKiPart);
			d.share_pf = getPercentSpinner(R.id.spPfPart);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			Storcheck d = doc.getData();
			View v = inflater.inflate(R.layout.storcheck_prop, container, false);
			setCheckBox(R.id.cbHO, d.ho_best, v);
			setCheckBox(R.id.cbShowcase, d.showcase_best, v);
			setCheckBox(R.id.cbCorpBlock, d.corp_block, v);
			setCheckBox(R.id.cbPOSM, d.posm, v);
			
			setProcentSpinner(R.id.spKiPart, d.share_ki, v);
			setProcentSpinner(R.id.spPfPart, d.share_pf, v);
			
			loadActions(R.id.spAction, d.created, d.action, v);
			
			view = v;
			return v;
		}
	}
	
	public class StorcheckGoods extends Fragment {
		
		public StorcheckGoods() {}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.storcheck_goods, container, false);
			ListView lv = (ListView)v.findViewById(R.id.lvItems);
			lv.setAdapter(items);
			lv.setDividerHeight(0);
			return v;
		}

		public void setDoc(Storcheck d) {
		}
	}
	
	class ItemAdapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }

		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View v, ViewGroup arg2) {
			if(v == null)
				v = View.inflate(StorcheckEdit.this, R.layout.storcheck_item, null);
			
			StorcheckItem i =(StorcheckItem)getItem(arg0);
			Price p = price.getData();
			p.id = i.id;
			price.read();
			
			TextView tv;
			tv = (TextView)v.findViewById(R.id.tvName);
			tv.setText(p.name);
			
			v.setBackgroundResource(arg0 % 2 != 0 ? R.drawable.even_row_selector: R.drawable.list_selector);
			return v;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result)
			doc.read(doc.getRowid(), false);		
	}
}
