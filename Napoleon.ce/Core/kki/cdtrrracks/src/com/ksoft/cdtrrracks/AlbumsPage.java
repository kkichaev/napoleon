package com.ksoft.cdtrrracks;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ksoft.cdtrrracks.AlbumAdapter.Info;

public class AlbumsPage extends Fragment {
	ListView list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.albumspage, container, false);
		list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(new AlbumAdapter(getActivity()));

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1,
					int pos, long arg3) {
				AlbumAdapter.Info info = (Info) adapterView
						.getItemAtPosition(pos);
				Youtube.open(getActivity(), info.link);

			}
		});
		return view;
	}

}

class AlbumAdapter extends BaseAdapter {
	ArrayList<Info> data = new ArrayList<AlbumAdapter.Info>();

	public static class Info {
		public String title;
		public String link;
		public String time;

		public Info(String title, String link, String time) {
			this.title = title;
			this.link = link;
			this.time = time;
		}
	}

	Context context;

	public AlbumAdapter(Context context) {
		this.context = context;
		data.add(new Info("Mustapha", "Oq6OoPB5FIM", "3:02"));
		data.add(new Info("Fat Bottomed Girls", "VMnjF1O4eH0", "4:12"));
		data.add(new Info("Jealousy", "Vk9g9HIbZdc", "3:14"));
		data.add(new Info("Bicycle Race", "GugsCdLHm-Q", "3:04"));
		data.add(new Info("If You Can't Beat Them", "aFtsWGaeAu0", "4:15"));
		data.add(new Info("Let Me Entertain You", "hKFueMePWj0", "3:02"));
		data.add(new Info("Dead on Time", "OgmsTJhjWII", "3:23"));
		data.add(new Info("In Only Seven Days", "aDvUBA7kCYs", "2:30"));
		data.add(new Info("Fun It", "4oNo8r8-8ro", "3:30"));
		data.add(new Info("Leaving Home Ain't Easy", "tCG9x6AEKg0", "3:15"));
		data.add(new Info("Don't Stop Me Now", "HgzGwKwLmgM", "3:30"));
		data.add(new Info("More of That Jazz", "OnWXEZaguyo", "4:17"));
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.albumlistitem, null);

		((TextView) view.findViewById(R.id.tvNum)).setText(Integer
				.toString(position));

		Info info = (Info) getItem(position);
		((TextView) view.findViewById(R.id.tvTitle)).setText(info.title);
		((TextView) view.findViewById(R.id.tvTime)).setText(info.time);
		((TextView) view.findViewById(R.id.tvInfo)).setText("");

		return view;
	}

}
