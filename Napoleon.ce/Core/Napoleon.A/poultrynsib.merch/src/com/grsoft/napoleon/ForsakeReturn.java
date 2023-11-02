package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class ForsakeReturn extends Activity {
	private OrgImpl org = new OrgImpl(); 
	private ListView list;
	
	public static void open(Context context){
		Intent intent = new Intent(context, ForsakeReturn.class);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.forsakereturn);
		
		list = (ListView) findViewById(R.id.list);
		list.setAdapter(new Adapter());
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int pos,
					long arg3) {
				ReturnImpl ret = (ReturnImpl) adapter.getItemAtPosition(pos);
				ret.open(ForsakeReturn.this);
			}
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		org.close();
	}
	
	class Adapter extends BaseAdapter{
		public DocList list;
		
		public Adapter(){
			list = new DocList(ReturnImpl.class, "forsake=1", null);
		}
		
		@Override
		public int getCount() {	return list.getCount();	}

		@Override
		public Object getItem(int pos) { return list.get(pos); }

		@Override
		public long getItemId(int arg0) { return 0;	}

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if(view == null)
				view = inflateViewRow();
			
			ReturnImpl ret = (ReturnImpl) getItem(pos);
			
			org.getData().id = ret.getId();
			org.read();
			
			TextView tv = (TextView) view.findViewById(R.id.tvOrg);
			tv.setText(org.getData().name);
			
			tv = (TextView) view.findViewById(R.id.tvData);
			tv.setText(Util.simpleDateFormat.format(ret.getData().created));
			
			tv = (TextView) view.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(ret.sum(), Consts.SUM_SCALE));
			
			view.setBackgroundResource(pos % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);		
			
			return view;
		}
		
	}

	public View inflateViewRow() {
		return View.inflate(this, R.layout.forsake_list_row, null);
	}
}
