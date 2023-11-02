package com.grsoft.adsmanager;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

public class LeftMenuAdapter extends BaseAdapter {
	private Context context;
	private Menu menu;
	
	public LeftMenuAdapter(Context context) {
		this.context = context;
		
		menu = new PopupMenu(context, null).getMenu();
		MenuInflater mi = new MenuInflater(context);
		mi.inflate(R.menu.main_navigate, menu);
	}
	
	@Override public int getCount() { return menu.size(); }
	@Override public Object getItem(int position) {	return menu.getItem(position); }
	@Override public long getItemId(int position) {	return 0; }

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.list_menu_item, null);
		
		MenuItem mi = (MenuItem) getItem(position);
		
		TextView tv = ((TextView)view); 
		tv.setText(mi.getTitle());
		//tv.setPadding(context.getResources().getDimensionPixelSize(R.dimen.drawable_text_padding), 0, 0, 0);
		tv.setCompoundDrawablePadding(context.getResources().getDimensionPixelSize(R.dimen.drawable_text_padding));
		tv.setCompoundDrawablesWithIntrinsicBounds(mi.getIcon(), null, null, null);
		
		return view;
	}

}
