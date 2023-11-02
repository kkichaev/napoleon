package com.grsoft.util;

import java.lang.reflect.Constructor;

import android.os.AsyncTask;

public class BuildSetThread extends AsyncTask<Void, Void, Integer> {
	public static Class<? extends BuildSetThread> type = BuildSetThread.class;

	public int topFolderId = -1;
	protected WarehouseAdapter adapter;

	public static BuildSetThread createInstance(WarehouseAdapter adapter) {
		BuildSetThread result = null;

		try {
			Constructor<? extends BuildSetThread> construct = type
					.getConstructor(WarehouseAdapter.class);
			result = construct.newInstance(adapter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public BuildSetThread(WarehouseAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	protected void onPreExecute() {
		adapter.fireStartBuildSet();
	}

	@Override
	protected void onPostExecute(Integer result) {
		adapter.fireEndBuildSet(result);
	}

	@Override
	protected Integer doInBackground(Void... params) {
		int result = getTopFolderID();
		
		try{
			adapter.buldProcess(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}

	private int getTopFolderID() {
		int result = topFolderId;
		
		if (result == -1 && adapter != null && adapter.folderTop != null)
			result = adapter.folderTop.id;
		
		return result;
	}
}