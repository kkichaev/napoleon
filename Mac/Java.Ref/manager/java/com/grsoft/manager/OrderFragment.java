package com.grsoft.manager;

import java.util.Date;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.grsoft.dataobjects.impl.RouteResultImpl;
import com.grsoft.util.Util;

public class OrderFragment extends Fragment {
	public RouteResultImpl result = new RouteResultImpl();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_fragment, container, false);

		FragmentTabHost tabhost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
		tabhost.setup(getActivity(), getFragmentManager(), android.R.id.tabcontent);

		Date date = ((SelParam)getActivity()).getDate();
		View doctab = View.inflate(getActivity(), R.layout.doclisttab, null);
		((TextView)doctab.findViewById(R.id.tvTitle)).setText(getString(R.string.doclist, Util.simpleDateFormat.format(date)));
		
		tabhost.addTab(tabhost.newTabSpec(DocListFragment.class.getName()).setIndicator(doctab), DocListFragment.class, null);
		tabhost.addTab(tabhost.newTabSpec(LogFragment.class.getName()).setIndicator(View.inflate(getActivity(), R.layout.logtab, null)), LogFragment.class, null);

		return view;
	}
}
