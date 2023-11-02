package com.grsoft.napoleon;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.grsoft.dataobjects.OrderPropData;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PropList extends BaseActivity {
	protected Adapter adapter;	
	protected List<OrderPropData> ordProps;	
	protected Hashtable<String, com.grsoft.dataobjects.OrderProps> keyNames;
	
	/**
	 * вызывается кода сво-во отредактировано
	 */
	protected void edited() {
		
	}
	
	protected void init(List<OrderPropData> props, Hashtable<String, com.grsoft.dataobjects.OrderProps> names) {
		ordProps = props;
		keyNames = names;
		
		adapter = new Adapter();
		ListView lv;
		lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				OrderPropData item = (OrderPropData)adapter.getItem(arg2);
				if( item != null ) {
					com.grsoft.dataobjects.OrderProps op = keyNames.get(item.id);
					if( op != null ) {
						PropEditor editor = getEditor(op.type);
						if( editor != null ) {
							editor.edit(item, PropList.this, new Runnable() {
								
								@Override
								public void run() {
									edited();
									adapter.refresh();
								}
							});
						}
					}
				}
			}
		});
	}

	PropEditor[] editors = new PropEditor[com.grsoft.dataobjects.OrderProps.NUM_EDITORS];
	PropEditor getEditor(int propType) {
		propType--;
		if( propType >= editors.length || propType < 0 )
			return null;
		
		if( editors[propType] == null ) {
			switch(propType+1) {
			case com.grsoft.dataobjects.OrderProps.STRING_TYPE:
				editors[propType] = new StringEditor();
				break;
			case com.grsoft.dataobjects.OrderProps.NUMBER_TYPE:
				editors[propType] = new NumberEditor();
				break;
			case com.grsoft.dataobjects.OrderProps.DATE_TYPE:
				editors[propType] = new DateEditor();
				break;
			case com.grsoft.dataobjects.OrderProps.TIME_TYPE:
				editors[propType] = new TimeEditor();
				break;
			case com.grsoft.dataobjects.OrderProps.BOOL_TYPE:
				editors[propType] = new BoolEditor();
				break;
			default:
				return null;
			}
		}
		return editors[propType];
	}
	
	class Adapter extends BaseAdapter {
		
		public void refresh() {
			notifyDataSetChanged();
		}

		@Override public int getCount() { return ordProps.size(); }

		@Override
		public Object getItem(int position) {
			return position < ordProps.size() ? ordProps.get(position) : null;
		}

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(PropList.this, R.layout.prop_item, null);
			OrderPropData item = (OrderPropData)getItem(position);
			if( item != null ) {
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvKey);
				com.grsoft.dataobjects.OrderProps op = keyNames.get(item.id); 
				String text = (op == null) ? "?" : op.name;
				tv.setText(text);
				
				tv = (TextView)view.findViewById(R.id.tvValue);
				String value = item.value;
				if( op.type == com.grsoft.dataobjects.OrderProps.BOOL_TYPE ) {
					if(value.equals("1"))
						value = "да";
					else if(value.equals("0"))
						value = "нет";
				}
				tv.setText(value);
			}
			return view;
		}
		
	}

	class BoolEditor extends PropEditor {
		CheckBox cb;

		@Override
		void edit(OrderPropData data, Activity ctx, Runnable runOnOk) {
			if( dialog == null ) {
				View  v = createDialog(ctx, R.layout.bool_prop, runOnOk);		
				cb = (CheckBox)v.findViewById(R.id.cbValue);
			}

			com.grsoft.dataobjects.OrderProps op = keyNames.get(data.id); 
			cb.setText(op.name);
			cb.setChecked(data.value.equals("1"));

			this.data = data;
			dialog.show();
		}

		@Override
		void saveData() {
			data.value = (cb.isChecked()) ? "1" : "0";
		}
	}
}


abstract class PropEditor {
	AlertDialog dialog;
	OrderPropData data;

	abstract void edit(OrderPropData data, Activity ctx, Runnable runOnOk);
	
	abstract void saveData();
	
	View createDialog(Activity ctx, int layout, final Runnable runOnOk) {
		LayoutInflater inflater = ctx.getLayoutInflater();
		
		View v = inflater.inflate(layout, null);

		AlertDialog.Builder b = new AlertDialog.Builder(ctx);
		
		b.setTitle(R.string.value_editor_title);
		b.setView(v);
		b.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override public void onClick(DialogInterface dialog, int which) {
				saveData();
				runOnOk.run();
			}
		});

		dialog = b.create();
		return v;
	}
}

class StringEditor extends PropEditor {
	
	EditText tv;

	@Override
	void edit(OrderPropData data, Activity ctx, final Runnable runOnOk) {
		if( dialog == null ) {
			View v = createDialog(ctx, R.layout.string_prop, runOnOk);		
			tv = (EditText)v.findViewById(R.id.edValue);
		}
		tv.setText(data.value);

		this.data = data;
		dialog.show();
	}

	@Override
	void saveData() {
		data.value = tv.getText().toString();
	}
}

class NumberEditor extends PropEditor {

	EditText tv;

	@Override
	void edit(OrderPropData data, Activity ctx, final Runnable runOnOk) {
		if( dialog == null ) {
			View  v = createDialog(ctx, R.layout.string_prop, runOnOk);		
			tv = (EditText)v.findViewById(R.id.edValue);
		}
		tv.setKeyListener(new DigitsKeyListener(true, true));
		tv.setText(data.value);

		this.data = data;
		dialog.show();
	}

	@Override
	void saveData() {
		data.value = tv.getText().toString();
	}
}

class DateEditor extends PropEditor {
	
	DatePicker dt;

	@Override
	void edit(OrderPropData data, Activity ctx, final Runnable runOnOk) {
		if( dialog == null ) {
			View v = createDialog(ctx, R.layout.date_prop, runOnOk);
			dt = (DatePicker)v.findViewById(R.id.dpValue);
		}
		
		if( data.value.length() > 0) {
			try {
				Date d = Util.simpleDateFormat.parse(data.value);
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				dt.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		this.data = data;
		dialog.show();
	}

	@Override
	void saveData() {
	    int day = dt.getDayOfMonth();
	    int month = dt.getMonth();
	    int year =  dt.getYear();

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, day);

	    data.value = Util.simpleDateFormat.format(calendar.getTime());
	}
}

class TimeEditor extends PropEditor {
	
	static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	TimePicker tp;

	@Override
	void edit(OrderPropData data, Activity ctx, final Runnable runOnOk) {
		if( dialog == null ) {
			View v = createDialog(ctx, R.layout.time_prop, runOnOk);
			tp = (TimePicker)v.findViewById(R.id.tpValue);
			tp.setIs24HourView(true);
		}
		
		if( data.value.length() > 0 ) {
			try {
				Date d = timeFormat.parse(data.value);
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				tp.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				tp.setCurrentMinute(c.get(Calendar.MINUTE));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		this.data = data;
		dialog.show();
	}
	
	@Override
	void saveData() {
	    int h = tp.getCurrentHour();
	    int m = tp.getCurrentMinute();

	    Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.HOUR_OF_DAY, h);
	    calendar.set(Calendar.MINUTE, m);

	    data.value = timeFormat.format(calendar.getTime());
	}
}
