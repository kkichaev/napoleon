package com.grsoft.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.grsoft.dataobjects.RatingActionTemplItem;
import com.grsoft.dataobjects.impl.RatingActionTemplImpl;
import com.grsoft.util.ExtrasConst;

public class RatingActionEdit extends Activity {
	public static Class<? extends Activity> activity = RatingActionEdit.class;

	private static final int ADD_ITEM_DLG = R.id.add_item_dlg;

	private ImageButton btnAdd;
	private ListView list;
	private EditText edName;

	private RatingActionTemplImpl template = new RatingActionTemplImpl();

	public static void open(Context context, long rowid) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_action);

		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		list = (ListView) findViewById(R.id.list);
		edName = (EditText) findViewById(R.id.edName);

		long rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR,
				ExtrasConst.INVALID_ID);

		if (rowid != ExtrasConst.INVALID_ID) {
			template.read(rowid);
			template.close();

			edName.setText(template.getData().name);
		} else {
			template.init();
		}

		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(ADD_ITEM_DLG);
			}
		});

		registerForContextMenu(list);
	}

	@Override
	protected void onPause() {
		super.onPause();

		String name = edName.getText().toString().trim();

		if (name.length() == 0)
			template.delete();
		else {
			template.getData().name = name;
			template.write();
		}

		template.close();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if  (id== ADD_ITEM_DLG)
			return createAddItemDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createAddItemDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.new_parameter);
		builder.setView(View.inflate(this, R.layout.itemdlg, null));
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						RatingActionTemplItem item = new RatingActionTemplItem();
						item.init();
						item.name = ((EditText) ((AlertDialog) dialog)
								.findViewById(R.id.editText)).getText()
								.toString().trim();
						template.getData().items.add(item);
					}
				});

		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
	
	class RatingActionAdapter extends BaseAdapter{

		@Override
		public int getCount() {	return template.getData().items.size(); }

		@Override
		public Object getItem(int position) { return template.getData().items.get(position); }

		@Override
		public long getItemId(int position) { return 0;	}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
