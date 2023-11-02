package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.util.MenuActionHandler;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

import android.app.Activity;

public class NapoleonAppBase3_62 extends NapoleonAppBase {
	@Override
	protected void initChildActivity() {
		super.initChildActivity();
		
		Warehouse.activity = Warehouse3_62.class;
		Setting.WarehouseSettingActivity = WarehouseSetting3_62.class;
		QuestionWebView.activity = QuestEdit.class;
		OrderDetail.activity = OrderDetail3_62.class;
	}
	
	@Override
	protected void initChildDocTypes() {
		super.initChildDocTypes();
		
		Main.mainMenuPrepared.add(new MenuPrepareHitching() {

			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuActionHandler(activity.getString(R.string.show_route_menu_hint),
						new Runnable() {			
							@Override public void run() { 
								((Main3_62)activity).openReports(); 
							}
						},
						R.drawable.ic_reports)); 
				
				menu.add(new MenuActionHandler(activity.getString(R.string.reports_menu_hint),
					new Runnable() {			
						@Override public void run() { 
							((Main3_62)activity).showRouteMap(); 
						}
					},
					R.drawable.globus)); 
			}
		});
	}
}
