package com.grsoft.napoleon;

import java.util.List;
import android.app.Activity;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.MenuPrepareHitching;

public class EdRoute {
	public static void init(){
		Napoleon.docMenuPrepared.add(new MenuPrepareHitching() {
			@Override
			public void menuPrepared(List<MenuHandler> menu, final Activity activity) {
				menu.add(new MenuHandler(activity.getString(R.string.agent_route_doc), new Runnable() {
					@Override public void run() { AgentRouteEdit.open(activity); }
				}));
			}
		});

	}
}
