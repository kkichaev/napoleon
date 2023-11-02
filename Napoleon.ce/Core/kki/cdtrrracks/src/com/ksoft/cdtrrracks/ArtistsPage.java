package com.ksoft.cdtrrracks;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.ksoft.ksoftlib.ui.WaitDialog;

public class ArtistsPage extends Fragment {
	private GridView grid;
	private FillGridThread fillGridThread;
	GridAdapter adapter = new GridAdapter();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.artistspage, container, false);
		grid = (GridView) view.findViewById(R.id.grid);

		if (adapter.getCount() == 0) {
			fillGridThread = new FillGridThread();
			fillGridThread.execute((Object[]) null);
		}else
			grid.setAdapter(adapter);
		
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1,
					int pos, long arg3) {
				String text = adapterView.getItemAtPosition(pos).toString();
				fillGridThread = new FillGridThread();
				fillGridThread.execute(text);

			}
		});

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();

		if (fillGridThread != null) {
			fillGridThread.cancel(false);
		}
	}

	class FillGridThread extends AsyncTask<Object, Void, List<String>> {
		WaitDialog waitDialog = new WaitDialog();
		@Override
		protected List<String> doInBackground(Object... params) {
//			try {
//				Thread.sleep(2000);
//			} catch (Exception e) {
//			}

			List<String> data = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			String input = "";

			if (params != null && params.length >= 1)
				input = params[0].toString();

			for (char i = 'A'; i < 'Z' && !isCancelled(); i++) {
				sb.setLength(0);
				sb.append(input).append(i);
				data.add(sb.toString());
			}

			sb.setLength(0);
			sb.append(input).append("Z");
			data.add(sb.toString());
			if (isCancelled())
				data.clear();

			return data;
		}

		protected void onPreExecute() {
			waitDialog.setCancelable(false);
			waitDialog.show(getActivity().getSupportFragmentManager(),
					waitDialog.getClass().toString());
		};

		@Override
		protected void onPostExecute(List<String> result) {
			waitDialog.dismiss();

			if (result.size() > 0) {
				adapter.data = result;
				grid.setAdapter(adapter);
			}
		}

		@Override
		protected void onCancelled() {
			waitDialog.dismiss();
		}
	}

	class GridAdapter extends BaseAdapter {
		List<String> data = new ArrayList<String>();

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
			view = View.inflate(ArtistsPage.this.getActivity(),
					R.layout.griditem, null);

			StringBuilder sb = new StringBuilder();
			sb.append("<u>").append(getItem(position).toString())
					.append("</u>");

			((TextView) view.findViewById(R.id.text)).setText(Html.fromHtml(sb
					.toString()));
			return view;
		}

	}
}
