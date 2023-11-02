package com.grsoft.napoleon;

import java.util.Calendar;
import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Quality;
import com.grsoft.dataobjects.QualityItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.QualityImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

public class ReturnPriceCount extends PriceCountEx {

	private Spinner spQuality;
	private Spinner spCause;
	private static final int SELECT_DATE_DLG = 0x4000;
	private Date party;
	private Button btnParty;
	
	private static final String EDIT_MODE_STR = "edit_mode";
	
	/*HACK*/
	private int selCauseIndex = -1;
	
	/***
	 * Индекс в items элемента, который мы редактируем
	 */
	private int editItemId = -1;
	
	/***
	 * Находимся внутри onCreate
	 */
	private boolean findFirst = false;
	
	public static void open(Context context, long priceRoid, 
			DbObject<? extends DataObject> doc, int editItemId) {
		Intent i = new Intent(context, ReturnPriceCount.class);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(EDIT_MODE_STR, editItemId);
		
		context.startActivity(i);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		findFirst = true;
		editItemId = getIntent().getIntExtra(EDIT_MODE_STR, -1);
		super.onCreate(savedInstanceState);
		
		spQuality = (Spinner) findViewById(R.id.spQuality);
		spCause = (Spinner) findViewById(R.id.spCause);
		
		spQuality.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
					QualityImpl quality = (QualityImpl) parent.getAdapter().getItem(position);
					
					if (quality != null){
						spCause.setAdapter(new CauseAdapter(parent.getContext(), quality.getData()));
						
						if(selCauseIndex != -1){
							spCause.setSelection(selCauseIndex);
							selCauseIndex = -1;
						}
					}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		btnParty = (Button)findViewById(R.id.btnParty);
		btnParty.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog(SELECT_DATE_DLG);
			}
		});
		
		btnParty.setTextColor(getResources().getColor(R.color.blue));
		setParty(Calendar.getInstance().getTime());
	}
	
	@Override protected void makeSaleHistory(Price p) { }
	
	@Override protected boolean isComplexSalesHistory() { return false; }
	
	@Override protected int getContentViewId() {return R.layout.returncount; }
	
	@Override
	protected void onResume() {
		super.onResume();
		try{
			QualityAdapter qualityAdapter = new QualityAdapter(this); 
			spQuality.setAdapter(qualityAdapter);
			
			if( document != null ) {
				findFirst = true;
				OrderItemEx oi = (OrderItemEx) getDocItem(price.getData());
				
				if (oi != null){
					setParty(oi.party);
					boolean finded = false;
					for(int q = 0; q < qualityAdapter.getCount() && finded == false; q++){
						QualityImpl qualityImpl = (QualityImpl) qualityAdapter.getItem(q);
						
						Quality quality = qualityImpl.getData();
						for(int c = 0; c < quality.items.size() && finded == false; c++){
							QualityItem item = quality.items.get(c);
							
							if (item.id.equals(oi.cause)){
								spQuality.setSelection(q);
								selCauseIndex = c;
								finded = true;
								break;
							}
						}
					}
				}
					
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	    
		QualityAdapter adapter = (QualityAdapter) spQuality.getAdapter();
		
		if (adapter != null)
			adapter.close();
	}

	@Override
	protected boolean updateOrder() {
		if(editItemId < 0) {
			int idx = 0;
			String id = price.getData().id;
			
			ReturnImplEx returnImpl = ((ReturnImplEx)document);
			for(OrderItem oi : returnImpl.getData().items) {
				OrderItemEx oe = (OrderItemEx)oi;
				if(oe.id.equals(id) && oe.party.equals(party)) {
					editItemId = idx;
					returnImpl.editItemId = editItemId;
					break;
				}
				idx++;
			}
		}
		super.updateOrder();
		
		findFirst = false;
		OrderItemEx item = (OrderItemEx)getDocItem(price.getData());
		
		if (item != null){
			item.party = party;
			QualityItem qi = ((QualityItem)spCause.getSelectedItem());
			if( qi != null )
				item.cause = qi.id;
			if(selectedUnit != null)
				item.unit = selectedUnit.id;
		}
		
		document.write();
		document.close();
		
		return false;
	}
	
	@Override
	protected DataObject getDocItem(Price p) {
		ReturnImplEx returnImpl = ((ReturnImplEx)document);
		returnImpl.editItemId = editItemId;
		DataObject result = returnImpl.findUpdateItem(p);

		if(result == null)
			result = findFirst ? returnImpl.findItem(p.id) : returnImpl.findLast(p);
//		List<OrderItem> items = ((ReturnImpl)document).getData().items;
//		
//		if (items != null && created){
//			if (result == null && items.size() > 0)
//				result = items.get(items.size() - 1);
//		}
		
		return result;
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case SELECT_DATE_DLG:
			return new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
				
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
					setParty(new Date(year - 1900, monthOfYear, dayOfMonth));
				}
			}, party.getYear() + 1900, party.getMonth(), party.getDate());
		default:
			return super.onCreateDialog(id);
		}
	}
	
	private void setParty(Date party){
		this.party = party;
		btnParty.setText(Html.fromHtml("<b>" + Util.simpleDateFormat.format(party) + "</b>"));
	}
}

class QualityAdapter extends DataBaseAdapter<Quality>{
	private int parityDrawable = R.drawable.even_row_selector;
    private int oddDrawabler = R.drawable.list_selector;
    
	public QualityAdapter(Context context)
			throws IllegalAccessException, InstantiationException {
		super(context, new QualityImpl());
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		return view(arg0, arg1, arg2, false);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return view(position, convertView, parent, true);
	}
	
	private View view(int position, View convertView, ViewGroup parent, boolean bg){
		if (convertView == null){
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		}
		
		QualityImpl qualityImpl = (QualityImpl) getItem(position);
		if(qualityImpl != null){
			TextView tvFirmaName = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tvFirmaName.setText(qualityImpl.getData().name);
		}
		
		if(bg)
			convertView.setBackgroundResource(position % 2 != 0 ? 
					parityDrawable : oddDrawabler);
			
		return convertView;
	}
}

class CauseAdapter extends BaseAdapter{
	private Context context;
	private Quality quality;
	private int parityDrawable = R.drawable.even_row_selector;
    private int oddDrawabler = R.drawable.list_selector;

	public CauseAdapter(Context context, Quality quality){
		this.context = context;
		this.quality = quality; 
	}

	@Override
	public int getCount() {
		return quality.items.size();
	}

	@Override
	public Object getItem(int position) {
		return  quality.items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return view(position, convertView, parent, false);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return view(position, convertView, parent, true);
	}
	
	private View view(int position, View convertView, ViewGroup parent, boolean bg){
		if (convertView == null){
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		}
		
		QualityItem item = (QualityItem) getItem(position);
		if(item != null){
			TextView tvFirmaName = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tvFirmaName.setText(item.name);
		}
			
		if(bg)
			convertView.setBackgroundResource(position % 2 != 0 ? 
					parityDrawable : oddDrawabler);
		
		return convertView;
	}
	
}
