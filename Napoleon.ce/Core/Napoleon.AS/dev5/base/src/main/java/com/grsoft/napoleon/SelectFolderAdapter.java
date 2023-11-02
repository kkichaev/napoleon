package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

class SelectFolderAdapter extends BaseAdapter {
	private final Context context;
	private File root;
	private View view;

	public SelectFolderAdapter(Context context) {
		this.context = context;
		root = Environment.getExternalStorageDirectory();
		fillData(root);
	}

	ArrayList<File> data = new ArrayList<File>();

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

	OnClickListener openFolder = new OnClickListener() {

		@Override
		public void onClick(View v) {
			File folder = (File) v.getTag();

			if (folder == root)
				folder = root.getParentFile();

			if (folder != null) {
				setFolder(folder);
			}
		}
	};

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.select_folder_dlg_row, null);
		File f = (File) getItem(position);
		TextView tvName = ((TextView) view.findViewById(R.id.tvName));
		tvName.setText(f == root ? " ..." : f.getName());
		view.setOnClickListener(openFolder);
		view.setTag(f);

		return view;
	}

	public void fillData(File root) {
		try {
			data.clear();
			File[] fa = root.listFiles();

			for (File f : fa)
				if (f.isDirectory() && !f.isHidden())
					data.add(f);

			Collections.sort(data, new Comparator<File>() {
				@Override
				public int compare(File lhs, File rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});

			data.add(0, root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return root.getPath();
	}

	public void setDialog(View view) {
		this.view = view;
	}

	public void setFolder(File folder) {
		fillData(folder);
		root = folder;
		notifyDataSetChanged();
		if (view != null)
			((TextView) view.findViewById(R.id.tvPath)).setText(getPath());
	}

	public Dialog createSelectFolderDlg(final EditText text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View view = View.inflate(context, R.layout.select_folder_dlg, null);
		ListView list = (ListView) view.findViewById(R.id.list);
		list.setAdapter(this);
		setDialog(view);
		builder.setTitle(R.string.select_folder_title);
		builder.setView(view);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						text.setText(getPath());
					}
				});

		builder.setNegativeButton(R.string.cancel, null);
		((TextView) view.findViewById(R.id.tvPath)).setText(getPath());
		return builder.create();
	}
}