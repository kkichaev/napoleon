package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.dataobjects.Plan;
import com.grsoft.dataobjects.impl.PlanImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class PlanList extends BaseActivity {
	Adapter adapter;
	
	public static void open(Context ctx) {
		Intent i = new Intent(ctx, PlanList.class);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plan_list);
		
		adapter = new Adapter(); 
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PlanImpl p = (PlanImpl) adapter.getItem(position);
				if( p != null )
					p.open(PlanList.this);
			}
		});
		registerForContextMenu(lv);
		
		View v;
		v = findViewById(R.id.btnNewDoc);
		v.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PlanImpl pi = new PlanImpl();
				GpsCoord coord = new GpsCoord(0, 0);
				pi.init(PlanList.this, "", coord);
				pi.open(PlanList.this);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.refresh();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if( DocType.getCurDoc().isCreatable() )
			getMenuInflater().inflate(R.menu.plan_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		PlanImpl doc = (PlanImpl) adapter.getItem(menuInfo.position);
		if( doc != null ) {
			if (item.getItemId() == R.id.itDelete) {
				doc.delete();
				adapter.refresh();
			} else if (item.getItemId() == R.id.itEdit) {
				doc.open(this);
			}
		}
		
		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		adapter.close();
	}
	
	class Adapter extends BaseAdapter {

		DocList plans;
		
		@Override
		public int getCount() { return (plans == null) ? 0 : plans.getCount(); }
		
		public void close() {
			if( plans != null )
				plans.close();
		}

		public void refresh() {
			close();
			plans = new DocList(PlanImpl.class, null, "date desc");
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			if( position >= getCount() )
				return null;
			return plans.get(position);
		}

		@Override public long getItemId(int position) { return position; }

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			if( view == null )
				view = View.inflate(PlanList.this, R.layout.plan_row, null);
			
			PlanImpl pi = (PlanImpl)getItem(position);
			if( pi != null ) {
				Plan p = pi.getData();
				
				TextView tv;
				tv = (TextView)view.findViewById(R.id.tvOther);
				tv.setText(pi.getDescription(view.getContext()));
				
				tv = (TextView)view.findViewById(R.id.tvDate);
				tv.setText(Util.simpleDateFormat.format(p.date));

				tv = (TextView)view.findViewById(R.id.tvSum);
				tv.setText(Util.IntToScaleStr(p.sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
			}			
			return view;
		}
		
	}
}
