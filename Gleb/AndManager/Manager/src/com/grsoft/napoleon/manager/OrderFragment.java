package com.grsoft.napoleon.manager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.Hitching;
import com.grsoft.database.VisitSelectHitching;
import com.grsoft.dataobjects.RouteResult2Item;
import com.grsoft.dataobjects.RouteResultItem;
import com.grsoft.dataobjects.impl.RouteResultImpl;
import com.grsoft.napoleon.UpdateProcess;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderFragment extends Fragment {
	public RouteResultImpl result = new RouteResultImpl();
	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"dd.MM.yy HH:mm");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.order_fragment, container, false);
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(new BaseAdapter() {

			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				if (view == null)
					view = View
							.inflate(getActivity(), R.layout.route_row, null);

				RouteResultItem item = (RouteResultItem) getItem(pos);

				if (item != null) {
					((TextView) view.findViewById(R.id.tvName))
							.setText(item.name);
					((TextView) view.findViewById(R.id.tvDate)).setText(sdf
							.format(item.date));
					((TextView) view.findViewById(R.id.tvOrgName))
							.setText(item.org);
					((TextView) view.findViewById(R.id.tvIdx)).setText(item.idx
							+ ")");
					((TextView) view.findViewById(R.id.tvSum)).setText(Util
							.IntToScaleStr(item.sum, Consts.SUM_SCALE));
				}

				return view;
			}

			@Override
			public long getItemId(int arg0) {
				return 0;
			}

			@Override
			public Object getItem(int arg0) {
				return ((AgentRoute) getActivity()).getItems().get(arg0);
			}

			@Override
			public int getCount() {
				return ((AgentRoute) getActivity()).getItems().size();
			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int pos, long arg3) {
				final RouteResultItem item = (RouteResultItem) adapterView
						.getAdapter().getItem(pos);

				if (!item.name.equals("Посещение")) {
					if (item.iitems != null && item.iitems.size() > 0) {
						Bundle bundle = new Bundle();
						bundle.putParcelable(AgentRoute.ITEMS, new Basket(
								item.iitems));
						getActivity().showDialog(AgentRoute.ITEMS_DLG, bundle);
					}
				} else {
					List<Hitching> ret = new ArrayList<Hitching>();
					ret.add(new VisitSelectHitching(item.date));
					UpdateProcess upp = new UpdateProcess((Activity) view
							.getContext(), new UpdateProcessOwner() {

						@Override
						public void onFinish() {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									Bundle bundle = new Bundle();
									bundle.putLong(AgentRoute.VISIT,
											item.date.getTime());
									getActivity().showDialog(
											AgentRoute.VISIT_DLG, bundle);
								}
							});
						}

						@Override
						public void enableControlButton(boolean enabled) {
						}
					}, ret);
					upp.execute((Void[]) null);
				}
			}
		});
		return view;
	}
}

class Basket implements Parcelable {
	List<RouteResult2Item> data;

	public Basket(List<RouteResult2Item> data) {
		this.data = data;
	}

	public Basket(Parcel in) {
		in.readList(data, data.getClass().getClassLoader());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(data);
	}

	public static final Parcelable.Creator<Basket> CREATOR = new Parcelable.Creator<Basket>() {
		public Basket createFromParcel(Parcel in) {
			return new Basket(in);
		}

		public Basket[] newArray(int size) {
			return new Basket[size];
		}
	};

}
