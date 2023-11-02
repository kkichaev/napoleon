package com.grsoft.manager;

import com.grsoft.dataobjects.Mapgis;
import com.grsoft.dataobjects.impl.MapgisImpl;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class MapFragment extends Fragment {
	public static final String TAG = "com.grsoft.manager.MapFragment";
	
	private WebView webView;
	private TextView tvTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		
		initView(view);
        refresh();
        
		return view;
	}

	public void refresh() {
		Mapgis mapgis = readData();
		final String MIME_TYPE = "text/html";
		webView.loadDataWithBaseURL(null, mapgis.html, MIME_TYPE, null, null);
		tvTitle.setText(Html.fromHtml(mapgis.title));
	}

	public Mapgis readData() {
		MapgisImpl impl = new MapgisImpl();
		Mapgis mapgis = impl.getData();
		SelParam sp = (SelParam) getActivity();
		mapgis.userid = sp.getUserid();
		mapgis.date = sp.getDate();
		impl.read();
		impl.close();
		return mapgis;
	}

	public void initView(View view) {
		webView = (WebView) view.findViewById(R.id.wv);
		webView.getSettings().setJavaScriptEnabled(true); 
        webView.getSettings().setSupportZoom(true);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
	}
}
