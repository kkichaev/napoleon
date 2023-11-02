package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrgTaskM;
import com.grsoft.dataobjects.TaskDoneInfoM;
import com.grsoft.dataobjects.impl.OrgTaskExecMImpl;
import com.grsoft.dataobjects.impl.OrgTaskMImpl;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class OrgTaskListM extends Activity {
	private static Class<? extends Activity> activity = OrgTaskListM.class;
	private String orgid = "";
	private OrgTaskExecMImpl doc = new OrgTaskExecMImpl();
	protected String selectedTaskId = "";
	private static final int INPUT_REMARK_DLG = 1;
	private ListView list;

	public static void open(Context context, String id, long rowid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.ORG_ID_STR, id);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orgtasklist);
		list = (ListView) findViewById(R.id.list);
		Bundle bundle = getIntent().getExtras();
		orgid = bundle.getString(ExtrasConst.ORG_ID_STR);
		doc.read(bundle.getLong(ExtrasConst.DOC_ROW_ID_STR));
		doc.close();
		list.setAdapter(new Adapter());

		if (!doc.isExported()) {
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
					long rowid = (Long) adapter.getItem(position);
					OrgTaskMImpl taskImpl = new OrgTaskMImpl();

					if (taskImpl.read(rowid)) {
						doc.recheckItem(taskImpl.getData().id);
						adapter.notifyDataSetChanged();
					}

					taskImpl.close();
				}
			});

			list.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {
					BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
					long rowid = (Long) adapter.getItem(position);
					OrgTaskMImpl taskImpl = new OrgTaskMImpl();

					if (taskImpl.read(rowid)) {
						selectedTaskId = taskImpl.getData().id;
						showDialog(INPUT_REMARK_DLG);
					}

					taskImpl.close();
					return true;
				}
			});
		}
	}

	public static List<Long> getTaskList(String orgid, boolean notExec) {
		final String QRY_FOR_TASK = "select rowid from agentOrgTask where orgid = ? and (finish >= ? "
				+ (notExec ? "and" : "or")
				+ " not id in (select id from orgtaskdone))";

		ArrayList<Long> result = new ArrayList<Long>();
		SQLiteDatabase db = DataBaseManager.getDataBase();
		DbWriter.checkDBTable(OrgTaskM.class);
		DbWriter.checkDBTable(TaskDoneInfoM.class);
		Cursor c = db
				.rawQuery(
						QRY_FOR_TASK,
						new String[] { orgid,
								Long.toString(Util.getDate().getTime()) });

		while (c.moveToNext())
			result.add(c.getLong(c.getColumnIndex("rowid")));

		c.close();

		return result;
	}

	class Adapter extends BaseAdapter {
		private ArrayList<Long> data = new ArrayList<Long>();
		private OrgTaskMImpl impl = new OrgTaskMImpl();
		private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM");

		public Adapter() {
			refresh();
		}

		public void refresh() {
			data.clear();
			data.addAll(getTaskList(orgid, doc.getData().items.size() == 0));
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
				view = View.inflate(OrgTaskListM.this, R.layout.orgtasklist_row,
						null);
			TextView tvText = (TextView) view.findViewById(R.id.tvText);
			impl.read((Long) getItem(position));
			OrgTaskM task = impl.getData();

			tvText.setText(task.text);
			Drawable pic = getResources()
					.getDrawable(
							doc.isTaskDone(impl.getData().id) ? android.R.drawable.checkbox_on_background
									: android.R.drawable.checkbox_off_background);
			tvText.setCompoundDrawablesWithIntrinsicBounds(null, null, pic,
					null);
			TextView tvRange = (TextView) view.findViewById(R.id.tvRange);
			StringBuilder range = new StringBuilder();
			range.append(sdf.format(task.start)).append(" - ")
					.append(sdf.format(task.finish));
			tvRange.setText(range.toString());

			TextView tvRemark = (TextView) view.findViewById(R.id.tvRemark);
			tvRemark.setText(doc.getRemark(task.id));

			return view;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case INPUT_REMARK_DLG:
			return createInputRemarkDlg();
		default:
			return super.onCreateDialog(id);
		}
	}

	private Dialog createInputRemarkDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(View.inflate(this, R.layout.input_remark, null));
		builder.setTitle(R.string.message);
		builder.setPositiveButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				doc.inputRemark(selectedTaskId,
						((EditText) ((AlertDialog) dialog)
								.findViewById(R.id.edRemark)).getText()
								.toString());
				((BaseAdapter) list.getAdapter()).notifyDataSetChanged();
			}
		});

		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case INPUT_REMARK_DLG:
			prepareInputRemarkDlg(dialog);
		default:
			super.onPrepareDialog(id, dialog);
		}
	}

	private void prepareInputRemarkDlg(Dialog dialog) {
		EditText ed = (EditText) dialog.findViewById(R.id.edRemark);
		ed.setText(doc.getRemark(selectedTaskId));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (doc.getData().items.size() == 0)
				doc.delete();
		}

		return super.onKeyDown(keyCode, event);
	}
}
