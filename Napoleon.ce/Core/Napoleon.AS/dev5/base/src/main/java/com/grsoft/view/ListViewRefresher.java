package com.grsoft.view;
import com.grsoft.aceteam.R;

import android.os.AsyncTask;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ListViewRefresher extends AsyncTask<Void, Void, Void> {

	private ListView listView;
	private int waitTime;

	ListViewRefresher(ListView listView, int waitTime) {
		this.listView = listView;
		this.waitTime = waitTime;		
	}
	
	/**
	 * 
	 * @param listView
	 * @param waitTime время ожидания в мс
	 */
	public static void refresh(ListView listView, int waitTime) {
		ListViewRefresher lv = new ListViewRefresher(listView, waitTime);
		lv.execute((Void[])null);
	}

	public static void refresh(ListView listView) {
		ListViewRefresher lv = new ListViewRefresher(listView, 500);
		lv.execute((Void[])null);
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) { ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged(); }
}
