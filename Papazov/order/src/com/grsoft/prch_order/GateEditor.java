package com.grsoft.prch_order;

import java.util.UUID;

import com.grsoft.prch_order.dataobjects.Gate;
import com.grsoft.prch_order.dataobjects.impl.GateImpl;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class GateEditor extends FragmentActivity {
	
	GateImpl gi;
	Gate gate;
	EditPage[] pages;
	
	public static void open(Context context, Gate gate) {
		Intent i = new Intent(context, GateEditor.class);
		if(gate != null)
			i.putExtra(ExtrasConst.ORG_ID_STR, gate.id);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.gate);
		gi = new GateImpl();
		gate = gi.getData();
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		String newId = UUID.randomUUID().toString().replace("-", "");
		String id = b == null ? newId : b.getString(ExtrasConst.ORG_ID_STR, newId);
		gate.id = id;
		gi.read();

		pages = new EditPage[] {
				new CustomerEdit(),
				new GateBuildEdit(),
				new GateAddBuildEdit(),
			};
		
		for(EditPage ep : pages)
			ep.bind(gate);
		
		ViewPager vp = (ViewPager)findViewById(R.id.pager);
		
		vp.setAdapter(new PageAdapter(getSupportFragmentManager(), pages));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		gi.close();
	}
	
	class PageAdapter extends FragmentStatePagerAdapter {

		EditPage[] pages;
		
		public PageAdapter(FragmentManager fm, EditPage[] pages) {
			super(fm);
			this.pages = pages;
		}

		@Override public Fragment getItem(int arg0) { return pages[arg0]; }
		@Override public int getCount() { return pages.length; }
		@Override public CharSequence getPageTitle(int position) { return pages[position].getTitle(); }
	}
}
