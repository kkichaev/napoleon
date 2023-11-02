package com.grsoft.dlc;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

public class AppList extends ListActivity {
	public final static String OPEN_COMMAND = "com.grsoft.dls.APP_LIST";
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.applist);
		ArrayList<ApplicationInfo> list = new ArrayList<ApplicationInfo>(((DLCApp)getApplication()).getAppList().values());
		setListAdapter(new AppListAdapter(this, list));
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ApplicationInfo info = (ApplicationInfo) parent.getItemAtPosition(position);
				info.setAllowed(!info.isAllowed());
				CheckedTextView ctv = (CheckedTextView) 
						view.findViewById(R.id.ctvApp);
				ctv.setChecked(info.isAllowed());
			}
		});
	}
	
	class AppListAdapter extends ArrayAdapter<ApplicationInfo>{
		 public AppListAdapter(Context context, List<ApplicationInfo> apps) {
		        super(context, 0, apps);
		    }
		 
		 @Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(getContext(), R.layout.applist_row, null);
			
			ApplicationInfo appInfo = getItem(position);
			
			if (appInfo != null){
				ImageView ivPic = (ImageView) convertView.findViewById(R.id.ivPic);
				ivPic.setImageDrawable(appInfo.icon);
				CheckedTextView ctvApp = (CheckedTextView) convertView.findViewById(R.id.ctvApp);
				ctvApp.setText(appInfo.title);
				ctvApp.setChecked(appInfo.isAllowed());
			}
			
			return convertView;
		}
	}
}