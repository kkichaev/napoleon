package com.grsoft.napoleon.manager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class MapFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.map_fragment, container, false);
		
		WebView webView = (WebView) view.findViewById(R.id.wv);
		webView.getSettings().setJavaScriptEnabled(true); 
        webView.getSettings().setSupportZoom(true);

        webView.loadDataWithBaseURL(null, ((AgentRoute)getActivity()).getHtml()
        		, "text/html", null, null);
        
//        try{
//	        AssetManager am = inflater.getContext().getAssets();
//	        InputStream is = am.open("sample.html");
//	        InputStreamReader isr = new InputStreamReader(is);
//	        BufferedReader reader = new BufferedReader(isr);
//	        
//	        StringBuilder html = new StringBuilder();
//	        String str = "";
//	        while ((str = reader.readLine()) != null)
//	        	html.append(str);
//	        
//	        webView.loadDataWithBaseURL(null, html.toString(), "text/html", null, null);
//        }catch(Exception e){
//        	e.printStackTrace();
//        }
        
		return view;
	}
}
