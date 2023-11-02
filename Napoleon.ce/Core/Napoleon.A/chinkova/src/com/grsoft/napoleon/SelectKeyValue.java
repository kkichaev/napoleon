package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.Banks;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Dover;
import com.grsoft.dataobjects.IDNameBase;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.view.dialog_helper.KeyValue;
import com.grsoft.view.BaseActivity;

public class SelectKeyValue extends BaseActivity {
	static final String DOC_TYPE = "docType";
	static final String BANK_TAG = "bank";
	static final String ORG_WHERE_TAG = "org";
	static final String DVR_TAG = "dvr";

	public enum DocTypes { Bank, Dover };
	
	Adapter adapter;
	
	String selectedID = "";
	
	List<KeyValue> values = new ArrayList<KeyValue>();
	
	public static Intent makeIntent(Context c, DocTypes docType) {
		Intent i = new Intent(c, SelectKeyValue.class);
		i.putExtra(DOC_TYPE, docType == DocTypes.Bank ? BANK_TAG : DVR_TAG);
		return i;
	}

	public static Intent makeBanksIntent(Context c, String org) {
		Intent i = new Intent(c, SelectKeyValue.class);
		i.putExtra(DOC_TYPE, BANK_TAG);
		
		String orgWhere = "org='" + org + "'";
		i.putExtra(ORG_WHERE_TAG, orgWhere);
		return i;
	}
	
	void fillValues(Class<? extends IDNameBase> cs, String where) {
		DataTraveler.travel(cs, new DataTraveler.Travel<IDNameBase>() {

			@Override
			public boolean travel(DataTraveler<IDNameBase> item) {
				values.add(new KeyValue(item.data.id, item.data.name));
				return true;
			}
			
		}, where);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_key_value);

		String dt = getIntent().getExtras().getString(DOC_TYPE);		
		String where = getIntent().getExtras().getString(ORG_WHERE_TAG, "");
		
		fillValues(dt.equals(BANK_TAG) ? Banks.class : Dover.class, where);
		
		final EditText ed = (EditText)findViewById(R.id.edFind);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter(values);
		lv.setAdapter(adapter);
		FindTextWatcher fd = new FindTextWatcher(ed, lv);
		ed.addTextChangedListener(fd);
	
		findViewById(R.id.btnDelFind).setOnClickListener(new OnClickListener() {			
			@Override public void onClick(View v) { ed.setText(""); }
		});
	
		findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		findViewById(R.id.btnOK).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if( selectedID.length() > 0 ) {
					Intent i = new Intent();
					i.putExtra(ExtrasConst.ORG_ID_STR, selectedID);
					setResult(RESULT_OK, i);
					finish();
				} else {
					Toast.makeText(SelectKeyValue.this, "Пожалуйста, укажите элемент", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	OnClickListener setSelected = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			selectedID = ((KeyValue)v.getTag()).key.toString();
			adapter.notifyDataSetChanged();
		}
	};
	
	class Adapter extends BaseAdapter implements FilterAdapter {

		List<KeyValue> items = new ArrayList<KeyValue>(); 
		
		public Adapter(List<KeyValue> v) {
			items.addAll(v);
		}
		
		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return arg0 < getCount() ? items.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int index, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(SelectKeyValue.this, R.layout.select_key_value_row, null);
			
			KeyValue kv = (KeyValue) getItem(index);
			
			int resId = index % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector;
			if( kv != null ) {
				TextView tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(kv.value);
				if( kv.key.equals(selectedID))
					resId = R.drawable.sel_row;
				tv.setTag(kv);
				tv.setOnClickListener(setSelected);
			}
			
			view.setBackgroundResource(resId);
			return view;
		}

		@Override
		public void applyFilter(String value) {
			value = value.toUpperCase(Locale.getDefault());
			items.clear();
			for(KeyValue kv : values)
				if( kv.value.toString().toUpperCase(Locale.getDefault()).contains(value))
					items.add(kv);

			notifyDataSetChanged();
		}

		@Override
		public void resetFilter() {
			items.clear();
			items.addAll(values);
			notifyDataSetChanged();
		}
	}
}
