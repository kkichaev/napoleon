package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.CommonIncass;
import com.grsoft.dataobjects.impl.CommonIncassImpl;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class CommonIncassList extends FragmentActivity {
	private ImageButton btnNew;
	ListView list;

	public static void open(Context context) {
		Intent intent = new Intent(context, CommonIncassList.class);
		context.startActivity(intent);
	}
	
	@SuppressWarnings("rawtypes")
	protected Class<? extends CommonIncassImplBase> documentType() { return CommonIncassImpl.class; }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.common_incass_ist);
		btnNew = (ImageButton) findViewById(R.id.btnNew);
		list = (ListView) findViewById(R.id.list);

		btnNew.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				@SuppressWarnings("rawtypes")
				CommonIncassImplBase impl = null;
				try {
					impl = documentType().newInstance();
				} catch(Exception e) {
					e.printStackTrace();
				}
				impl.init();
				impl.open(v.getContext());
				impl.close();
			}
		});

		list.setAdapter(new CILAdapter(this, documentType()));
		list.setDividerHeight(0);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				long rowid = (Long) parent.getItemAtPosition(position);
				editItem(rowid);
			}
		});

		registerForContextMenu(list);
	}

	@Override
	protected void onResume() {
		super.onResume();

		((CILAdapter) list.getAdapter()).reload();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.common_incass_edit_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int itemid = item.getItemId();
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		final long rowid = (Long) list.getItemAtPosition(pos);
		
		if( itemid == R.id.itDelete) {
			AskToDelDlg askToDelDlg = new AskToDelDlg(new Runnable() {
				
				@Override
				public void run() {
					DbWriter writer = new DbWriter();
					@SuppressWarnings("rawtypes")
					CommonIncassImplBase impl = null;
					try {
						impl = documentType().newInstance();
					} catch(Exception e) {
						e.printStackTrace();
					}
					writer.deleteRecord(impl.getData(), rowid);
					((CILAdapter)list.getAdapter()).reload();					
				}
			});
			
			askToDelDlg.show(getSupportFragmentManager(), askToDelDlg.getClass().toString());
			return true;
		}
		else if( itemid == R.id.itEdit ) {
			editItem(rowid);
			return true;
		}
		return false;
	}

	protected void editItem(long rowid) {
		@SuppressWarnings("rawtypes")
		CommonIncassImplBase cii = null;
		try {
			cii = documentType().newInstance();
		} catch(Exception e) {
			e.printStackTrace();
		}
		cii.read(rowid);
		cii.close();
		cii.open(this);
	}
}

class CILAdapter extends BaseAdapter {
	private List<Long> data = new ArrayList<Long>();
	@SuppressWarnings("rawtypes")
	private CommonIncassImplBase cii;
	private Context context;
	private SimpleDateFormat dft = new SimpleDateFormat("dd.MM.yyyy");
	private SimpleDateFormat tft = new SimpleDateFormat("HH:mm");

	@SuppressWarnings("rawtypes")
	public CILAdapter(Context context, Class<? extends CommonIncassImplBase> srcClass) {
		this.context = context;
		try {
			cii = srcClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.common_incass_list_row, null);

		Long rowid = (Long) getItem(position);
		cii.read(rowid, false);
		cii.close();

		TextView tv = (TextView) convertView.findViewById(R.id.tvStatus);
		if (cii.isExported())
			tv.setText(context.getString(R.string.sent));
		else
			tv.setText("");

		CommonIncass ci = (CommonIncass) cii.getData();
		tv = (TextView) convertView.findViewById(R.id.tvDate);
		tv.setText(dft.format(ci.created));

		tv = (TextView) convertView.findViewById(R.id.tvTime);
		tv.setText(tft.format(ci.created));

		tv = (TextView) convertView.findViewById(R.id.tvSum);
		tv.setText(Util.IntToScaleStr(cii.sum(), Consts.SUM_SCALE));

		convertView.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector : R.drawable.list_selector);
		return convertView;
	}

	public void reload() {
		data = DbReader.readIds(cii.getTableName(), null, "created DESC");
		notifyDataSetChanged();
	}
}

class AskToDelDlg extends DialogFragment{
	Runnable delPrc;
	
	public AskToDelDlg(Runnable delPrc) {
		this.delPrc = delPrc;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.question);
		builder.setMessage(R.string.ask_to_del_record);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) { delPrc.run(); }

		});
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}
}
