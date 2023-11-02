package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.ActionItem;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;


public class ActionView extends Activity{
	public static void open(Context ctx, long rowid){
		Intent intent = new Intent(ctx, ActionView.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		ctx.startActivity(intent);
	}
	
	private ListView list;
	private Document<?> doc;
	private Button btnPrice;
	private Timer waitTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actionview);
		
		btnPrice = (Button) findViewById(R.id.btnPrice);
		
		doc = DocType.getCurDoc().create();
		
		if(doc!= null){
			doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
			doc.close();
		
			list = (ListView) findViewById(R.id.list);
			list.setAdapter(new Adapter());
		}
		
		btnPrice.setOnClickListener( new OnClickListener() {
			@Override
			public void onClick(View v) { 
				Warehouse.open(v.getContext(), doc, false);  
				finish(); 
			}
		}); 
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		
		if(cfg.getValue(sb, "НеЗакрыватьАкции"))
			try{
				int time = Integer.parseInt(sb.toString());
				
				btnPrice.setEnabled(false);
				
				waitTimer = new Timer();
				waitTimer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						btnPrice.post(new Runnable() {					
							@Override
							public void run() {
								btnPrice.setEnabled(true);
								waitTimer = null;
							}
						});
						
					}
				}, time * 1000);
				
			}catch(Exception e){ e.printStackTrace(); }
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && waitTimer != null)
			return false;
		else
			return super.onKeyDown(keyCode, event);
	}
	
	class Title implements OnClickListener{
		String text;
		public boolean isClickable() {return false;}
		@Override
		public void onClick(View v) {}
		public void setView(TextView tv) {
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_text_sz));
			tv.setTextColor(getResources().getColor(R.color.black));
			tv.setText(Html.fromHtml(text));
			int PADDING = 10;
			tv.setPadding(0, PADDING, 0, PADDING);
			tv.setOnClickListener(this);
			tv.setGravity(getGravity());
		}
		
		protected int getGravity() {
			return Gravity.CENTER;
		}
	}
	
	class Data extends Title {
		String id;
		
		public boolean isClickable() {return true;}

		@Override
		public void onClick(View v) {
			PriceImpl pimpl = new PriceImpl();
			pimpl.read("id", id);
			pimpl.close();
			
			if (pimpl.getRowid() != ExtrasConst.INVALID_ID){
				PriceCount2Ex.open(v.getContext(), pimpl.getRowid(), doc, true);
				finish();
			}
		}
		
		protected int getGravity() {
			return Gravity.LEFT;
		}
	}
	
	class Group extends Data{
		@Override
		public void onClick(View v) {
			Cursor c = null;
			try{
				String sql = "select id from folder where fid=?";
				c = DataBaseManager.getDataBase().rawQuery(sql, new String[]{id});
				
				if(c.moveToFirst()){
					WarehouseEx.open(v.getContext(), doc, c.getInt(0), false);
					finish();
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(c != null)
					c.close();
			}
		}
	}
	
	public class Adapter extends BaseAdapter{
		List<Title> data = new ArrayList<Title>();	
		int count = 0;
		
		public Adapter() {
			DataTraveler.travel(Action.class, new DataTraveler.Travel<Action>() {

				@Override
				public boolean travel(DataTraveler<Action> item) {
					Title i = new Title();
					i.text = item.data.text;
					data.add(i);
					
					for(ActionItem ai : item.data.items){
						Data d = ai.group > 0 ? new Group() : new Data();
						d.text = ai.text;
						d.id = ai.id;
						
						data.add(d);
					}
					
					item.data = new Action();
					return true;
				}}, null);
		}
		
		@Override
		public int getCount() { return data.size(); }

		@Override
		public Object getItem(int position) { return data.get(position); }

		@Override
		public long getItemId(int position) { return 0; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if(view == null)
				view = new TextView(ActionView.this);
			
			Title i = (Title) getItem(position);
			TextView tv = (TextView)view;
			i.setView(tv);
			
			return view;
		}
	}

}
