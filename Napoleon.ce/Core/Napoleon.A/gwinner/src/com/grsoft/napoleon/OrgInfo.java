package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgInfoData;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class OrgInfo extends Activity {
	String orgId = "";

	List<SectionInfo> data = new ArrayList<SectionInfo>();
	
	@Override
	protected void onCreate(Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.org_info);

		if( b == null )
			b = getIntent().getExtras();
	
		orgId = b.getString(ExtrasConst.ORG_ID_STR);

		DataTraveler.travel(OrgInfoData.class, new DataTraveler.Travel<OrgInfoData>() {
			
			SectionInfo curSection = null;

			@Override
			public boolean travel(DataTraveler<OrgInfoData> item) {
				if(curSection == null || curSection.id != item.data.sectionID) {
					curSection = new SectionInfo();
					curSection.id = item.data.sectionID;
					curSection.name = item.data.sectionName;
					
					data.add(curSection);
				}
				
				curSection.addParam(item.data);
				return true;
			}
		}, "id='" + orgId + "'", "sectionID,paramID");

		LinearLayout lgroup = (LinearLayout)findViewById(R.id.llItems);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		for(SectionInfo si : data) {
			View v = View.inflate(OrgInfo.this, R.layout.section_view, null);
			TextView tv;
			tv = (TextView)v.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(si.name));

			LinearLayout ll = (LinearLayout)v.findViewById(R.id.llItems);
			for(ParamInfo pi : si) {
				View vc = View.inflate(OrgInfo.this, R.layout.param_view, null);
				String text = Integer.toString(pi.id) + " " + pi.name + " " + pi.value;
				tv = (TextView)vc.findViewById(R.id.tvText);
				tv.setText(Html.fromHtml(text));
				
				ll.addView(vc, lp);
			}
			
			lgroup.addView(v, lp);
		}
		
//		ListView lv = (ListView)findViewById(R.id.lvItems);
//		lv.setAdapter(new Adapter());
//		lv.setDividerHeight(0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);		
		outState.putString(ExtrasConst.ORG_ID_STR, orgId);
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int arg0) { return data.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if( arg1 == null )
				arg1 = View.inflate(OrgInfo.this, R.layout.section_view, null);
			
			SectionInfo si = (SectionInfo)getItem(arg0);
			TextView tv;
			tv = (TextView)arg1.findViewById(R.id.tvName);
			tv.setText(Html.fromHtml(si.name));
			
			ListView lv = (ListView)arg1.findViewById(R.id.lvItems); 
			lv.setAdapter(new ParamAdapter(si));
			lv.setDividerHeight(0);
			
			return arg1;
		}
		
	}
	
	class ParamAdapter extends BaseAdapter {
		SectionInfo section;
		
		public ParamAdapter(SectionInfo data) {
			this.section = data;
		}
		
		@Override public int getCount() { return section.size(); }
		@Override public Object getItem(int arg0) { return section.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if( arg1 == null )
				arg1 = View.inflate(OrgInfo.this, R.layout.param_view, null);
			
			ParamInfo si = (ParamInfo)getItem(arg0);
			String text = Integer.toString(si.id) + " " + si.name + " " + si.value;
			TextView tv;
			tv = (TextView)arg1.findViewById(R.id.tvText);
			tv.setText(Html.fromHtml(text));
			
			return arg1;
		}
	}
}

class ParamInfo {
	public int id;
	public String name;
	public String value;
}

class SectionInfo extends ArrayList<ParamInfo> {
	private static final long serialVersionUID = 1L;
	
	public int id;
	public String name;
	
	public void addParam(OrgInfoData data) {
		ParamInfo pi = new ParamInfo();
		
		pi.id = data.paramID;
		pi.name = data.paramName;
		pi.value = data.paramValue;
		
		add(pi);
	}
}