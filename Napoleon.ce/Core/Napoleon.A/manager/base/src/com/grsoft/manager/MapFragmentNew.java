package com.grsoft.manager;

import com.grsoft.view.Refreshable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ListView;

public class MapFragmentNew extends Fragment implements Refreshable {
	private WebView webView;
	private ListView list;
	private MapFragmentMapUtil mapUtil;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment_new, container, false);

		webView = (WebView) view.findViewById(R.id.wv);
		list = (ListView) view.findViewById(R.id.list);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
		        Log.d("GRManager", consoleMessage.message() + " -- From line " +
		                consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
		                return true;
			}
		});

		mapUtil = new MapFragmentMapUtil();
		refreshContent();
		
		return view;
	}

	@Override
	public void refreshContent() {
		Activity activity = getActivity(); 
		Context context = activity.getApplicationContext();
		SelParam sp = (SelParam)activity;
		String html = mapUtil.createHtml(context, sp.getUserid(), sp.getDate(), AgentRouteNew.routePoints);
		
		if(mapUtil.mapData != null)
			list.setAdapter(new MapFragmentAdapter(getActivity(), mapUtil.mapData));
		
		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
}
