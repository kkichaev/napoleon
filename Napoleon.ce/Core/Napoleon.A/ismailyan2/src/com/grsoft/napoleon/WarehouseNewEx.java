package com.grsoft.napoleon;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.PriceEx;

public class WarehouseNewEx extends WarehouseNew {
	final static int MIN_CLMN_WIDTH = 30;
	final int DEF_CLMN_WDTH = 70;
	int clmnWidth = 70;
	PopupWindow popupWnd;
	final static String CLMNWIDTH = "clmnwidth";
	final static String CLMNVISIBLE = "clmnvisible"; 
	boolean clmnVisible = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		popupWnd = new PopupHandler(this);
		
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE); 
		clmnWidth = pref.getInt(CLMNWIDTH, DEF_CLMN_WDTH);
		clmnVisible = pref.getBoolean(CLMNVISIBLE, false);
	}
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);
		TextView tvClmn0 = (TextView) result.findViewById(R.id.tvClmn0);
		tvClmn0.setWidth(clmnWidth);
		tvClmn0.setOnLongClickListener(onPopupShow);
		tvClmn0.setVisibility(clmnVisible ? View.VISIBLE : View.GONE);
		tvClmn0.setText(((PriceEx)price.getData()).info);
		return result;
	}
	
	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}

	public void updateColmnWidth(int rawX) {
		clmnWidth = rawX;
		lvItemSelect.invalidateViews();
	}
	
	OnPopupShow onPopupShow = new OnPopupShow();
	class OnPopupShow implements OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			int[] xy = new int[2];
			v.getLocationInWindow(xy);
			popupWnd.showAtLocation(v, Gravity.NO_GRAVITY, 50, 50);
			((PopupHandler)popupWnd).init(xy[1], v);
			return false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		popupWnd.dismiss();
		Editor edit = getPreferences(Context.MODE_PRIVATE).edit();
		edit.putInt(CLMNWIDTH, clmnWidth);
		edit.putBoolean(CLMNVISIBLE, clmnVisible);
		edit.commit();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.itFirstClmn){
			clmnVisible = !clmnVisible;
			lvItemSelect.invalidateViews();
			popupWnd.dismiss();
			return true;
		}else
			return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected int getOptionsMenuId() {
		return R.menu.warehouse_opt_menu_ex;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);
		
		MenuItem item = menu.findItem(R.id.itFirstClmn);
		if(item != null){
			if(clmnVisible)
				item.setTitle(R.string.clmnInfoHide);
			else 
				item.setTitle(R.string.clmnInfoShow);
		}
			
		return result;
	}
	
	public boolean isAllowChanging(int x){
		return x > MIN_CLMN_WIDTH;
	}
}

class PopupHandler extends PopupWindow implements OnTouchListener{
	static int size = 70;
	int y;
	WarehouseNewEx activity;
	
	public PopupHandler(Context context){
		super(context);
		setTouchInterceptor(this);
		setContentView(View.inflate(context, R.layout.handler, null));
		activity = (WarehouseNewEx)context;
		setBackgroundDrawable(new BitmapDrawable());
		setOutsideTouchable(true);
	}

	public void init(int y, View view) {
		this.y = y + 30;
		update(((TextView) view).getWidth() - 5, this.y, size, size);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean result = false;
		
		if (event.getAction() == MotionEvent.ACTION_MOVE){
			int x = (int)event.getRawX();
			if(activity.isAllowChanging(x)){
				update(x, y, size, size);
				activity.updateColmnWidth(x);
			}
			result = true;
		}else if (event.getAction() == MotionEvent.ACTION_OUTSIDE){
			dismiss();
		}

		return result;
	}
}

