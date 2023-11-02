package com.grsoft.manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.grsoft.view.Refreshable;

public class MapFragmentNew extends Fragment implements Refreshable {
	private WebView webView;
	private ListView list;
	private MapFragmentMapUtil mapUtil;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment_new, container, false);

		webView = view.findViewById(R.id.wv);
		list = view.findViewById(R.id.list);

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

		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				Log.d("Manager", consoleMessage.message() + " -- From line " +
						consoleMessage.lineNumber() + " of " + consoleMessage.sourceId());
				return true;
			}
		});
		webView.addJavascriptInterface(this, "Android");

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});

		webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
}
