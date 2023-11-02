package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.GoodsRestItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.GoodsRestImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.GoodsRestDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

public class GoodRestForm extends RegDurationActivity {
	
	public static int startLevel = 0;
	public static final int MNU_SEND_ID = 3;
	
	GoodsRestImpl document = new GoodsRestImpl();
	ArrayList<FolderView> folders = new ArrayList<FolderView>();
	GoodsAdapter goods = new GoodsAdapter();
	PartyAdapter party = new PartyAdapter();
	ImageButton btnSend;

	public static void open(Context context, GoodsRestImpl doc) {
		Intent i = new Intent(context, GoodRestForm.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, document.getRowid());
	}
	
	@Override
	protected void onDestroy() {
		document.close();
		super.onDestroy();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.goods_form);

		long docRowId;
		if( savedInstanceState == null )
			docRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		else
			docRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);
		document.read(docRowId);

		loadFolders();
		
		TextView view = (TextView)findViewById(R.id.tvName);
		view.setMaxLines(3);

		Spinner s = (Spinner)findViewById(R.id.spFolders);
		s.setAdapter(new ArrayAdapter<FolderView>(this, R.layout.simple_spinner_layout, folders));
		s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				onFolderSelect(folders.get(pos));				
			}
			@Override public void onNothingSelected(AdapterView<?> arg0) {}
		});
		
		ListView list = (ListView)findViewById(R.id.lvGoods);
		list.setAdapter(goods);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				PriceEx p = (PriceEx) goods.getItem(pos);
				if( p != null )
					onPriceSelect(p);
			}
		});
		
		list = (ListView)findViewById(R.id.lvParty);
		list.setAdapter(party);

		btnSend = (ImageButton) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override public void onClick(View v) {
				super.onClick(v);
				send();
			}});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MNU_SEND_ID, Menu.NONE, "Отправить");;
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if( item.getItemId() == MNU_SEND_ID && btnSend != null )
			btnSend.performClick();
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void send() {
		new DocumentSender(GoodRestForm.this, btnSend, 
				GoodsRestDoc.OBJ_NAME, document, 
				document.getRowid()).execute((Void[])null);
	}

	private void onPriceSelect(PriceEx p) {
		TextView name = (TextView)findViewById(R.id.tvName);
		name.setText(((p == null) ? "" : p.name));
		party.setPriceItem(p);
	}

	private void onFolderSelect(FolderView f) {
		goods.setFolder(f);
		
		PriceEx p = null;
		if( goods.getCount() > 0 )
			p = (PriceEx)goods.getItem(0);
		onPriceSelect(p);
	}
	
	class InputVQty implements InputNumber {
		private TextView view; 
		private GoodsRestItem item;
		
		public InputVQty(TextView v) { 
			view = v;
			this.item = (GoodsRestItem) v.getTag();
		}
		
		@Override public int getValue() { return item.vqty;  }
		
		@Override
		public void applayInput(int value, Object... params) {
			item.vqty = value;
			if( document.updateItem(item) ) {
				view.setText(Util.IntToScaleStr(item.vqty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
				goods.notifyDataSetChanged();
			}
		}
	}
	
	class InputQty implements InputNumber {
		private TextView view; 
		private GoodsRestItem item;
		
		public InputQty(TextView v) { 
			view = v;
			this.item = (GoodsRestItem) v.getTag();
		}
		
		@Override public int getValue() { return item.qty;  }
		
		@Override
		public void applayInput(int value, Object... params) {
			item.qty = value;
			if( document.updateItem(item) ) {
				view.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
				goods.notifyDataSetChanged();
			}
		}
	}
	
	class TouchListner implements OnTouchListener {

		@Override public boolean onTouch(View v, MotionEvent event) {
			switch( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				v.setBackgroundColor(Color.LTGRAY);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				v.setBackgroundColor(Color.WHITE);
				break;
			}
			return false;
		}
		
	}
	
	class PartyAdapter extends BaseAdapter {
		
		ArrayList<GoodsRestItem> party = new ArrayList<GoodsRestItem>();
		String id;
		
		public void setPriceItem(PriceEx p) {
			if( p == null ) {
				id = "";
				party.clear();
			} else {
				id = p.id;
				party = document.getItems(p);
			}
			notifyDataSetChanged();
		}
		
		@Override public int getCount() { return party.size(); }

		@Override public Object getItem(int position) { return ((party.size() > position) ? party.get(position) : null); }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			TextView v;
			if( view == null ) {
				view = View.inflate(GoodRestForm.this, R.layout.party_row, null);
				v = (TextView)view.findViewById(R.id.tvQtyV);
				v.setOnTouchListener(new TouchListner());
				if( document.isExported() == false )
					v.setOnClickListener(new View.OnClickListener() {
						@Override public void onClick(View v) {
							InputNumberDlg.open(GoodRestForm.this, new InputVQty((TextView) v), Consts.QTY_SCALE, true, "Витрина");
					}});

				v = (TextView)view.findViewById(R.id.tvQty);
				v.setOnTouchListener(new TouchListner());
				if( document.isExported() == false )
					v.setOnClickListener(new View.OnClickListener() {
						@Override public void onClick(View v) {
							InputNumberDlg.open(GoodRestForm.this, new InputQty((TextView) v), Consts.QTY_SCALE, true, "Запасы");
					}});
			}
			
			GoodsRestItem item = (GoodsRestItem)getItem(position);
			if( item != null ) {
				SimpleDateFormat format =  new SimpleDateFormat("dd.MM.yyyy");
				v = (TextView)view.findViewById(R.id.tvDate);
				v.setText(format.format(item.date));
				
				v = (TextView)view.findViewById(R.id.tvQtyV);
				v.setText(Util.IntToScaleStr(item.vqty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
				v.setTag(item);

				v = (TextView)view.findViewById(R.id.tvQty);
				v.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE, Util.DEC_DELIM, true));
				v.setTag(item);
			}
			return view;
		}
		
	}
	
	class GoodsAdapter extends BaseAdapter {
		
		ArrayList<Price> price = new ArrayList<Price>();
		
		public void setFolder(Folder f) {
			price.clear();

			PriceEx p = new PriceEx();
			DbReader reader = new DbReader();
			boolean bdo = reader.select(p, DataObjectInfo.getInstance().getTableName(p.getClass()), "folderID=" + f.id, "name");
			while( bdo ) {
				price.add(p);
				p = new PriceEx();
				bdo = reader.selectNext(p);
			}
			reader.close();
			
			notifyDataSetChanged();
		}

		@Override public int getCount() { return price.size(); }

		@Override public Object getItem(int position) { return ((price.size() > position) ? price.get(position) : null); }

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if( convertView == null ) {
				LinearLayout ll = new LinearLayout(GoodRestForm.this);
				TextView name = new TextView(GoodRestForm.this);
				name.setTextColor(Color.BLACK);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				name.setId(1);
				ll.setBackgroundResource(R.drawable.list_selector);
				ll.addView(name, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.FILL_PARENT);
				convertView = ll;
			}
			
			Price p = (Price)getItem(position);
			if( p != null ) {
				TextView name = (TextView)convertView.findViewById(1);
				name.setText(p.name);
				name.setLines(1);
				name.setEllipsize(TruncateAt.END);
				name.setTextColor(((document.findItem(p.id) != null ) ? Color.GREEN : Color.BLACK));
			}
			
			return convertView;
		}
		
	}

	private void loadFolders() {
		boolean first = true;
		FolderView f = new FolderView();
		DbReader reader = new DbReader();
		boolean bdo = reader.select(f, DataObjectInfo.getInstance().getTableName(f.getClass()), null, "id");
		while( bdo ) {
			if( first ) {
				first = false;
				startLevel = f.level;
			}
			folders.add(f);
			f = new FolderView();
			bdo = reader.selectNext(f);
		}
		reader.close();
	}
	
	class FolderView extends Folder {
		@Override public String toString() { 
			String space = "";
			if( level > startLevel ) {
				StringBuilder sb = new StringBuilder();
				for( int i=level; i>startLevel; i-- )
					sb.append("  ");
				space = sb.toString();
			}
			return space + name;
		}
	}
}
