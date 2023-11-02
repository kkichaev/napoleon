package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceTypes;
import com.grsoft.dataobjects.impl.DistribImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.PriceTypesImpl;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.view.BaseActivity;

@SuppressLint("UseSparseArrays")
public class DistribEdit extends BaseActivity implements SendResultListener {
	protected static final int SELECT_TH_STATE = 1;
	protected static final int SELECT_PRICE_TYPE = 0;
	protected static final int CONFIRM_PRICE_CHANGES = 2;
	protected static final int CONFIRM_TH_CHANGES = 3;
	
	DistribImpl doc;
	PriceImpl pi = new PriceImpl();
	Adapter adapter;
	ThStateAdapter thAdapter;
	boolean canChangeSelector;
	String selectedPriceType, selectedThState;
	
	public static void open(Context c, DistribImpl doc) {
		Intent i = new Intent(c, DistribEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		doc = new DistribImpl();
		doc.read(getIntent().getExtras().getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		canChangeSelector = doc.getData().items.size() == 0;
		
		setContentView(R.layout.distrib);
		ListView lv = (ListView)findViewById(R.id.lvItems);
		adapter = new Adapter();		
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvPriceType);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { showDialog(SELECT_PRICE_TYPE); }
		});
		refreshPriceType();
		
		tv = (TextView)findViewById(R.id.tvThState);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) {
				if( doc.getData().priceType.length() == 0)
					Toast.makeText(DistribEdit.this, "Выберите сначала вид груза", Toast.LENGTH_SHORT).show();
				else
					showDialog(SELECT_TH_STATE); 
			}
		});
		refreshThState();
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
	}
	
	boolean checkDocValid() {
		int invalidItemPos = doc.checkValid();
		if(invalidItemPos >= 0) {
			((ListView)findViewById(R.id.lvItems)).setSelection(invalidItemPos);
			Toast.makeText(this, "Проставьте, пожалуйста отметки для всех товаров", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	void send() {
		if( !checkDocValid() )
			return;
		doc.write();
		DocumentSender ds = new DocumentSender(this, findViewById(R.id.btnSend), 
				DistribDoc.instance().getObjectName(), doc, doc.getRowid(), this);
		ds.execute((Void[])null);
	}
	
	@Override
	public void onBackPressed() {
		if( !checkDocValid() )
			return;
		doc.write();
		super.onBackPressed();
	}
	
	private void refreshPriceType() {
		PriceTypesImpl pti = new PriceTypesImpl();
		PriceTypes pt = pti.getData();		
		Distrib d = doc.getData();
		
		pt.id = d.priceType;
		String text = (pti.read()) ? pt.name : "<Укажите вид груза>";
		pti.close();
		
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new UnderlineSpan(), 0, text.length(), 0);
		((TextView)findViewById(R.id.tvPriceType)).setText(ss);
	}

	private void refreshThState() {
		String text = doc.getData().thermalState;
		if(text.length()== 0)
			text = "<Укажите термическое состояние>";
		
		SpannableString ss = new SpannableString(text);
		ss.setSpan(new UnderlineSpan(), 0, text.length(), 0);
		((TextView)findViewById(R.id.tvThState)).setText(ss);
	}
	
	void changeThState(String thState) {
		doc.changeThState(thState);
		canChangeSelector = true;
		
		refreshThState();
		adapter.notifyDataSetChanged();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case SELECT_PRICE_TYPE: {
			return createPriceTypeSelectDialog();
		}
		case SELECT_TH_STATE: {
			thAdapter = new ThStateAdapter();
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Укажите терм.состояние");
			b.setAdapter(thAdapter, new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					String th = (String) thAdapter.getItem(arg1);
					
					if(!doc.isEditable())
						return;
					if( canChangeSelector )
						changeThState(th);
					else {
						selectedThState = th;
						showDialog(CONFIRM_TH_CHANGES);
					}
				}
				
			});
			return b.create();
		}
		case CONFIRM_PRICE_CHANGES: {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтвердите изменение");
			b.setMessage("При изменении вида груза очистятся все веденные данные. Продолжть?");
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { changePriceType(selectedPriceType); }
			});
			b.setNegativeButton(R.string.no, null);
			return b.create();
		}
		case CONFIRM_TH_CHANGES: {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтвердите изменение");
			b.setMessage("При изменении терм.состяния очистятся все веденные данные. Продолжть?");
			b.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { changeThState(selectedThState); }
			});
			b.setNegativeButton(R.string.no, null);
			return b.create();
		}}
		return super.onCreateDialog(id);
	}
	
	class ThStateAdapter extends BaseAdapter {
		List<String> items = new ArrayList<String>();
		
		public ThStateAdapter() { refresh(); }
		
		public void refresh() {
			items.clear();
			String sql = "select distinct thermalState from [" + new Price().getTableName() + "] where idType='" +
					doc.getData().priceType + "'";
			Cursor c = null;
			try {
				c = DataBaseManager.getDataBase().rawQuery(sql, null);
				while(c.moveToNext()) {
					items.add(c.getString(0));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if( c != null )
					c.close();
			}
			notifyDataSetChanged();
		}

		@Override public int getCount() { return items.size(); }
		@Override public Object getItem(int arg0) { return ( arg0 < items.size() )? items.get(arg0) : null; }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null ) {
				view = View.inflate(DistribEdit.this, R.layout.simple_spinner_layout_drop_down, null);
			}
			String item = (String)getItem(arg0);
			if( item != null ) {
				TextView tv = (TextView)view.findViewById(R.id.tvFirmaName);
				tv.setText(item);
			}
			return view;
		}
		
	}

	private Dialog createPriceTypeSelectDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Укажите вид груза");
		
		final List<PriceTypes> types = new ArrayList<PriceTypes>();
		DataTraveler.travel(PriceTypes.class, new DataTraveler.Travel<PriceTypes>() {

			@Override
			public boolean travel(DataTraveler<PriceTypes> item) {
				types.add(item.data);
				item.data = new PriceTypes();
				return true;
			}
		}, "", "name");
		
		int index = 0;
		CharSequence[] items = new CharSequence[types.size()];
		for(PriceTypes pt : types)
			items[index++] = pt.name;
		
		b.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

			@Override public void onClick(DialogInterface dialog, int which) { 
				dialog.dismiss();
				PriceTypes priceTypes = types.get(which); 
				if(!doc.isEditable())
					return;
				if( canChangeSelector )
					changePriceType(priceTypes.id);
				else {
					selectedPriceType = priceTypes.id;
					showDialog(CONFIRM_PRICE_CHANGES);
				}
			}
		});
		return b.create();
	}

	void changePriceType(String newPriceType) {
		doc.changePriceType(newPriceType);
		canChangeSelector = true;
		
		refreshPriceType();
		refreshThState();
		adapter.notifyDataSetChanged();
		if( thAdapter != null )
			thAdapter.refresh();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doc.close();
		pi.close();
	}
	
	class RBHandler implements View.OnClickListener {
		int value;
		public RBHandler(int value) { this.value = value; }

		@Override
		public void onClick(View v) {
			DistribItem di = (DistribItem)v.getTag();
			CompoundButton cb = (CompoundButton)v;
			boolean isChecked = cb.isChecked();
			if( di != null && isChecked && doc.isEditable() && di.exists != value ) {
				di.exists = value;
				canChangeSelector = false;
				adapter.notifyDataSetChanged();
			}
		}		
	}
	RBHandler pressNo = new RBHandler(0), pressExists = new RBHandler(1);
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }

		@Override public Object getItem(int arg0) { return arg0 < getCount() ? doc.getData().items.get(arg0) : null; }

		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null) {
				view = View.inflate(DistribEdit.this, R.layout.distr_row, null);
			}
			
			DistribItem i = (DistribItem) getItem(arg0);
			if( i != null ) {
				Price p = (Price) pi.getData();
				p.id = i.id;
				pi.read();
				
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvName);
				tv.setText(p.name);
				
				CheckBox rb, rb1;
				rb = (CheckBox)view.findViewById(R.id.cbNone);
				rb1 = (CheckBox)view.findViewById(R.id.cbHave);

				rb.setChecked(i.exists == 0);
				rb1.setChecked(i.exists == 1);

				rb.setTag(i);
				rb1.setTag(i);
				if( doc.isEditable() ) {
					rb.setOnClickListener(pressNo);
					rb1.setOnClickListener(pressExists);
				}
			}
			view.setBackgroundResource((arg0%2) ==0 ? R.drawable.list_selector : R.drawable.even_row_selector); 
			return view;
		}
		
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
}
