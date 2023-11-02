package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.SalesChannel;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class SalesParams extends Activity implements OnClickListener {
	private TextView tvStart;
	private TextView tvFinish;
	private Params params = new Params();
	private ListView orgsList;
	private View btnOK;
	private View btnCancel;
	private ListView salesChannelList;
	private Spinner spUnits;
	private Spinner spStatus;
	private View btnCheck;
	private View btnReset;

	public static class Params implements Parcelable {
		public Date start = Util.getDate();
		public Date finish = Util.getDate();
		public List<String> slsch = new ArrayList<String>();
		public List<String> orgs = new ArrayList<String>();
		public int unit = 0;
		public int status = 0;

		public Params() {

		}

		public Params(Parcel in) {
			readFromParcel(in);
		}

		private void readFromParcel(Parcel in) {
			start = new Date(in.readLong());
			finish = new Date(in.readLong());
			in.readList(slsch, String.class.getClassLoader());
			in.readList(orgs, String.class.getClassLoader());
			unit = in.readInt();
			status = in.readInt();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeLong(start.getTime());
			dest.writeLong(finish.getTime());
			dest.writeList(slsch);
			dest.writeList(orgs);
			dest.writeInt(unit);
			dest.writeInt(status);
		}
		
		public static final Parcelable.Creator<Params> CREATOR = new Parcelable.Creator<Params>() {
			public Params createFromParcel(Parcel in) {
				return new Params(in);
			}

			public Params[] newArray(int size) {
				return new Params[size];
			}
		};

	}

	public static void open(Context context, Params args) {
		Intent intent = new Intent(context, SalesParams.class);
		
		if (args != null)
			intent.putExtra(SalesReport.PARAMS, args);
		
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salesreportparams);

		tvStart = (TextView) findViewById(R.id.tvStart);
		tvFinish = (TextView) findViewById(R.id.tvFinish);
		orgsList = (ListView) findViewById(R.id.orgs);
		btnOK = findViewById(R.id.btnOK);
		btnCancel = findViewById(R.id.btnCancel);
		salesChannelList = (ListView) findViewById(R.id.salesChaneel);
		spUnits = (Spinner) findViewById(R.id.spUnits);
		spStatus = (Spinner) findViewById(R.id.spStatus);
		btnCheck = findViewById(R.id.btnCheck);
		btnReset = findViewById(R.id.btnReset);
		
		tvStart.setOnClickListener(this);
		tvFinish.setOnClickListener(this);
		btnOK.setOnClickListener(this);
		btnCheck.setOnClickListener(this);
		btnReset.setOnClickListener(this);

		orgsList.setAdapter(createOrgAdapter());
		salesChannelList.setAdapter(createChannelAdapetr());

		if (getIntent() != null) {
			Params args = getIntent().getParcelableExtra(SalesReport.PARAMS);
			
			if (args != null) {
				params = args;
				
				CheckedAdapter a = (CheckedAdapter) orgsList.getAdapter();
				a.setCheckAll(false);
				a.setCheckedItems(params.orgs);
				a.notifyDataSetChanged();
				
				a = (CheckedAdapter) salesChannelList.getAdapter();
				a.setCheckAll(false);
				a.setCheckedItems(params.slsch);
				a.notifyDataSetChanged();
				
				spUnits.setSelection(params.unit, true);
				spStatus.setSelection(params.status, true);
			}
		}
		
		refreshDate();
	}

	protected BaseAdapter createOrgAdapter() {
		return new CheckedAdapter(this) {
			@Override
			protected void loadData() {
				Org o = new Org();
				String table = DataObjectInfo.getInstance().getTableName(o.getClass());
				DbReader r = new DbReader();
				r.setReadingFields("id,name,address");
				boolean bdo = r.select(o, table, null, "name");
				while (bdo) {
					CheckedItem i = new CheckedItem();
					i.checked = true;
					i.text = o.name + " (" + o.address + ")";
					i.id = o.id;
					data.add(i);
					bdo = r.selectNext(o);
				}
				r.close();
				
				super.loadData();
			}
		};
	}

	protected BaseAdapter createChannelAdapetr() {
		return new CheckedAdapter(this) {
			@Override
			protected void loadData() {
				DataTraveler.travel(SalesChannel.class, new DataTraveler.Travel<SalesChannel>() {

					@Override
					public boolean travel(DataTraveler<SalesChannel> item) {
						CheckedItem i = new CheckedItem();
						i.checked = true;
						i.text = item.data.name;
						i.id = item.data.id;
						data.add(i);
						return true;
					}
				}, null);
				
				super.loadData();
			}
		};
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tvStart:
			setStartTime();
			break;
		case R.id.tvFinish:
			setFinishTime();
			break;
		case R.id.btnOK:
			setOKResult();
			break;
		case R.id.btnCancel:
			finish();
			break;
		case R.id.btnCheck:
			doCheck();
			break;
		case R.id.btnReset:
			doReset();
		default:

		}
	}

	private void doReset() {
		((CheckedAdapter)orgsList.getAdapter()).setCheckAll(false);
		((CheckedAdapter)orgsList.getAdapter()).notifyDataSetChanged();
		
	}

	private void doCheck() {
		((CheckedAdapter)orgsList.getAdapter()).setCheckAll(true);
		((CheckedAdapter)orgsList.getAdapter()).notifyDataSetChanged();
	}

	private void setOKResult() {
		params.slsch = ((CheckedAdapter)salesChannelList.getAdapter()).getCheckedId();
		params.orgs = ((CheckedAdapter)orgsList.getAdapter()).getCheckedId();
		params.unit = spUnits.getSelectedItemPosition();
		params.status = spStatus.getSelectedItemPosition();
		
		SalesReport.open(this, params);
	}

	private void setFinishTime() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, params.start.getTime());
		startActivityForResult(i, R.id.tvFinish);

	}

	private void setStartTime() {
		Intent i = new Intent(this, CalendarActivity.class);
		i.putExtra(ExtrasConst.DATE_TAG, params.finish.getTime());
		startActivityForResult(i, R.id.tvStart);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null
				&& (requestCode == R.id.tvStart || requestCode == R.id.tvFinish)) {
			Date curDate = new Date();
			long ct = data.getExtras().getLong(ExtrasConst.DATE_TAG, curDate.getTime());
			Date newDate = new Date(ct);

			if (requestCode == R.id.tvStart)
				params.start = newDate;
			else if (requestCode == R.id.tvFinish)
				params.finish = newDate;

			refreshDate();
		}
	}

	private void refreshDate() {
		tvStart.setText(Html.fromHtml(String.format("<u>%s</u>", Util.simpleDateFormat.format(params.start))));
		tvFinish.setText(Html.fromHtml(String.format("<u>%s</u>", Util.simpleDateFormat.format(params.finish))));
	}
}
