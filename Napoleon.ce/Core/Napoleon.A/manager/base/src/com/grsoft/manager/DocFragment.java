package com.grsoft.manager;

import java.util.Date;

import com.grsoft.util.Util;
import com.grsoft.view.Refreshable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class DocFragment extends Fragment implements Refreshable{
	private FragmentTabHost tabhost;
	private TextView tvTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_fragment, container, false);

		tabhost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		tabhost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

		Date date = ((SelParam)getActivity()).getDate();
		
		View doctab = View.inflate(getActivity(), R.layout.doctabheader, null);
		
		tvTitle = (TextView)doctab.findViewById(R.id.tvTitle); 
		tvTitle.setText(getString(R.string.doclist, Util.simpleDateFormat.format(date)));
		
		Class<?> dt = getDocListType();
		TabSpec ts = tabhost.newTabSpec(dt.getName()).setIndicator(doctab);
		tabhost.addTab(ts, dt, null);
		
		doctab = View.inflate(getActivity(), R.layout.doctabheader, null);
		
		TextView tv = (TextView) doctab.findViewById(R.id.tvTitle);
		tv.setText(getString(R.string.user_actions));
		ts = tabhost.newTabSpec(LogFragment.class.getName()).setIndicator(doctab);
		tabhost.addTab(ts, LogFragment.class, null);

		return view;
	}

	@Override
	public void refreshContent() {
		android.support.v4.app.FragmentManager fm = getChildFragmentManager();
		
		DocListFragment doc = (DocListFragment) fm.findFragmentByTag(getDocListType().getName());
		
		if(doc != null)
			doc.refresh();
		
		LogFragment log = (LogFragment) fm.findFragmentByTag(LogFragment.class.getName());
		
		if(log != null)
			log.refresh();
		
		Date date = ((SelParam)getActivity()).getDate();
		tvTitle.setText(getString(R.string.doclist, Util.simpleDateFormat.format(date)));
	}
	
	protected Class<?> getDocListType(){ return DocListFragment.class; }
}
