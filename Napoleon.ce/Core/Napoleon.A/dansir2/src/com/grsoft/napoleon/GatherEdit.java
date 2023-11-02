package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Gather;
import com.grsoft.dataobjects.GatherItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.WhPrice;
import com.grsoft.dataobjects.impl.GatherImpl;
import com.grsoft.dataobjects.impl.WhPriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class GatherEdit extends Activity {
	GatherImpl gatherImpl = new GatherImpl();
	GatherItem item;
	ListAdapter adapter;
	
	private static final String ROWID = "rowid";
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, GatherEdit.class);
		intent.putExtra(ROWID, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gather_edit);
		
		Intent intent = getIntent();
				
		if(intent != null){
			long rowid = intent.getLongExtra(ROWID, ExtrasConst.INVALID_ID);
			
			if(rowid != ExtrasConst.INVALID_ID) {
				gatherImpl.read(rowid);
				setData(gatherImpl.getData());
				setListAdapter(gatherImpl);
			}
		}
	}

	private void setData(Gather gather) {
		StringBuilder caption = new StringBuilder();
		caption.append("<font color=blue>Круг:&nbsp;")
			.append(Integer.toString(gather.krug))
			.append("<br><b>")
			.append(Util.simpleDateFormat.format(gather.date))
			.append("&nbsp;")
			.append(gather.id)
			.append("&nbsp;")
			.append(gather.name)
			.append("</b><br><i>")
			.append(gather.address)
			.append("</i></font>");
		
			EditText ed = (EditText)findViewById(R.id.edRemark);
			ed.setText(gather.remark);
		
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbInWork);
		cb.setChecked((gather.params & Gather.IN_WORK) != 0);
		cb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Gather g = gatherImpl.getData();
				if(((CheckBox)v).isChecked())
					g.params |= Gather.IN_WORK;
				else
					g.params &= (~Gather.IN_WORK);
				gatherImpl.write();					
			}
		});
			
		((TextView)findViewById(R.id.tvDocInfo)).setText(Html.fromHtml(caption.toString()));
	}

	private void setListAdapter(GatherImpl g) {
		ListView lv = (ListView)findViewById(android.R.id.list);
		adapter = new ListAdapter(this, g.getData());
		lv.setAdapter(adapter);
		
		if( (gatherImpl.getData().params & ParamState.ofExported) == 0 ) {
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	
				@Override
				public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
					item = (GatherItem)((ListAdapter)parent.getAdapter()).getItem(position);
					InputNumberDlg.open(view.getContext(), new InputNumber() {
						@Override public void applayInput(int value, Object... params) {
							item.newQty = value;
							gatherImpl.getData().params |= Gather.COMPLEETE;
							gatherImpl.write();
							adapter.notifyDataSetChanged();
						}
						
						@Override public int getValue() { return item.newQty; } 					
					}, Consts.QTY_SCALE, false, "Количество");
				}
			});
			
			lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
	
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					doReplace(position);
					return true;
				}
			});
		}
	}
	
	public void doReplace(int position) {
		if( gatherImpl.isExported() )
			return;
		
		item = (GatherItem)adapter.getItem(position);
		if( item.new_id.length() > 0 ) {
			aksForReplace();
		} else {
			Intent i = new Intent(GatherEdit.this, PriceReplace.class);
			startActivityForResult(i, 0);
		}
	}
	
	protected void aksForReplace() {
		CharSequence[] items = { "удалить замену", "сделать новую" };
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setItems(items, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( which == 0 ) {
					item.new_id = "";

					WhPriceImpl wpi = new WhPriceImpl();
					wpi.getData().id = item.i_id;
					if( wpi.read() )
						item.item = wpi.getData().name;
					wpi.close();

					gatherImpl.write();
					adapter.notifyDataSetChanged();
				} else {
					Intent i = new Intent(GatherEdit.this, PriceReplace.class);
					startActivityForResult(i, 0);
				}
			}
		});
		b.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if( resultCode == RESULT_OK && data != null) {
			String id = data.getStringExtra(ExtrasConst.PRICE_ROW_ID_STR);
			if( id != null ) {
				item.new_id = id;
				WhPriceImpl wpi = new WhPriceImpl();
				wpi.getData().id = item.new_id;
				if( wpi.read() )
					item.item = wpi.getData().name;
				wpi.close();
				
				gatherImpl.write();
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		gatherImpl.close();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if( !gatherImpl.isExported() ) {
				boolean haveZeroItems = false;

				Gather g = gatherImpl.getData();

				EditText ed = (EditText)findViewById(R.id.edRemark);
				g.remark = ed.getText().toString();
				gatherImpl.write();
				
				for(GatherItem i : g.items) {
					if( i.newQty == 0 ) {
						haveZeroItems = true;
						break;
					}
				}
				
				if( haveZeroItems && ((CheckBox)findViewById(R.id.cbInWork)).isChecked() == false ) {
					AlertDialog.Builder b = new AlertDialog.Builder(this);
					b.setTitle("Предупреждение");
					b.setMessage("В документе есть позиции с нулевым весом. Отметить накладную \"в работе\"?");
					b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { 
							gatherImpl.getData().params |= Gather.IN_WORK;
							gatherImpl.write();
							dialog.dismiss();
							finish(); 
						}
					});
					b.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which) { 
							dialog.dismiss();
							finish();
						}
					});
					b.create().show();
					
					return true;
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}

class ListAdapter extends BaseAdapter{
	GatherEdit context;
	Gather gather;
	
	public ListAdapter(GatherEdit context, Gather g){
		this.context = context;
		this.gather = g;
	}
	
	@Override
	public int getCount() {
		return gather.items == null ? 0 : 
			gather.items.size();
	}

	@Override
	public Object getItem(int position) {
		return gather.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		if(view == null)
			view = View.inflate(context, R.layout.gather_detail_row, null);
		
		GatherItem item = (GatherItem) getItem(position);
		
		View ir = view.findViewById(R.id.ivReplace);
		ir.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { context.doReplace(position); }
		});
		
		StringBuilder caption = new StringBuilder();
		if( item.new_id.length() > 0 ) {
			WhPriceImpl wp = new WhPriceImpl();
			WhPrice price = wp.getData();
			price.id = item.i_id;
			wp.read();
			wp.close();
			caption.append("<font color=green>")
				.append(item.item)
				.append(" (")
				.append(price.name)
				.append(")")
				.append("</font><br>");
		} else {
			caption.append("<font color=blue>")
				.append(item.item)
				.append("</font><br>");
		}
		caption.append(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE))
			.append("&nbsp;")
			.append(item.unit)
			;
		
		((TextView)view.findViewById(R.id.tvName)).setText(Html.fromHtml(caption.toString()));
		
		String text = Util.IntToScaleStr(item.newQty, Consts.QTY_SCALE, Util.DEC_DELIM, false) + " кг";
		((TextView)view.findViewById(R.id.tvNewQty)).setText(text);

		return view;
	}
}
