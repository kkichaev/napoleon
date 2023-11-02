package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgTask;
import com.grsoft.dataobjects.impl.OrgTaskExecImplW;
import com.grsoft.dataobjects.impl.OrgTaskImpl;
import com.grsoft.napoleon.documents.TaskDoneDocW;
import com.grsoft.napoleon.util.OrgTaskListHelper;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.ExtrasConst;

public class OrgTaskList extends Activity {
	public static Class<? extends Activity> activity = OrgTaskList.class;
	protected String orgid = "";
	protected OrgTaskExecImplW doc = new OrgTaskExecImplW();
	protected String selectedTaskId = "";
	private static final int INPUT_REMARK_DLG = 1;
	protected ListView list;

	public static void open(Context context, String id, long rowid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.ORG_ID_STR, id);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		list = (ListView) findViewById(R.id.list);
		Bundle bundle = getIntent().getExtras();
		orgid = bundle.getString(ExtrasConst.ORG_ID_STR);
		doc.read(bundle.getLong(ExtrasConst.DOC_ROW_ID_STR));
		list.setAdapter(new Adapter());

		if (doc.isEditable()) {
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					BaseAdapter adapter = (BaseAdapter) parent.getAdapter();
					long rowid = (Long) adapter.getItem(position);
					OrgTaskImpl taskImpl = new OrgTaskImpl();

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
					OrgTaskImpl taskImpl = new OrgTaskImpl();

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
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}

	protected int getLayoutId() {
		return R.layout.orgtasklist;
	}

	public List<Long> getTaskList(String orgid, boolean notExec) {
		return new OrgTaskListHelper().getTaskList(orgid, notExec);
	}

	class Adapter extends BaseAdapter {
		private ArrayList<Long> data = new ArrayList<Long>();
		private OrgTaskImpl impl = new OrgTaskImpl();
		private SimpleDateFormat sdf = new SimpleDateFormat("dd:MM", Locale.getDefault());

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
				view = View.inflate(OrgTaskList.this, R.layout.orgtasklist_row,
						null);
			TextView tvText = (TextView) view.findViewById(R.id.tvText);
			impl.read((Long) getItem(position));
			OrgTask task = impl.getData();

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
	public void onBackPressed() {
		if (doc.getData().items.size() == 0) {
			if( !(Features.SCRIPT_DOC && 
					ScriptImpl.containsDocument(TaskDoneDocW.instance().getObjectName(), doc.getData().created, doc.getId())))
				doc.delete();
		}

		super.onBackPressed();
	}
}
