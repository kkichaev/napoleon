package com.grsoft.napoleon.manager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.RouteResult2Item;
import com.grsoft.dataobjects.RouteResultItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.impl.RouteResultImpl;
import com.grsoft.napoleon.util.debug.Path;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class AgentRoute extends FragmentActivity {
	public static Class<? extends AgentRoute> activity = AgentRoute.class;
	public static final String ITEMS = "items";
	public static final String VISIT = "visit";
	private static String USERID = "userid";
	public static final int ITEMS_DLG = R.id.items_dlg;
	public static final int VISIT_DLG = R.id.visit_dlg;
	private RouteResultImpl result = new RouteResultImpl();
	private ImageButton btnMap;
	private ImageButton btnOrder;

	public static void open(Context context, String id) {
		Intent intent = new Intent(context, activity);
		intent.putExtra(USERID, id);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		result.getData().id = getIntent().getStringExtra(USERID);
		result.read();
		result.close();
		setContentView(getContentViewId());
		btnMap = (ImageButton) findViewById(R.id.btnMap);
		btnOrder = (ImageButton) findViewById(R.id.btnOrder);

		btnMap.setOnClickListener(new ReCh(this, R.id.mapFragment));
		btnOrder.setOnClickListener(new ReCh(this, R.id.orderFragment));
	}

	private int getContentViewId() {
		return R.layout.route;
	}

	public String getHtml() {
		return result.getData().html;
	}

	public List<RouteResultItem> getItems() {
		return result.getData().items;
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch (id) {
		case ITEMS_DLG:
			return createItemsDlg(args);
		case VISIT_DLG:
			return createVisitDlg(args);
		default:
			return super.onCreateDialog(id, args);
		}
	}

	private Dialog createVisitDlg(Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.visit_dlg_title);
		builder.setView(View.inflate(this, R.layout.visit_dlg, null));
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		switch (id) {
		case ITEMS_DLG:
			prepareItemsDlg(dialog, bundle);
			break;
		case VISIT_DLG:
			prepareVisitDlg(dialog, bundle);
		default:
			super.onPrepareDialog(id, dialog, bundle);
		}
	}

	private void prepareVisitDlg(Dialog dialog, Bundle bundle) {
		long created = bundle.getLong(VISIT);
		final Visit data = new Visit();
		DbReader reader = new DbReader();

		if (reader.select(data,
				DataObjectInfo.getInstance().getTableName(data.getClass()),
				"created=" + created)) {
			LinearLayout layout = (LinearLayout) dialog
					.findViewById(R.id.layout);
			TextView tvRemark = (TextView) dialog.findViewById(R.id.tvRemark);
			tvRemark.setText(data.remark);

			for (int i = 0; i < data.items.size(); i++) {
				VisitItem item = data.items.get(i);
				ImageView image = new ImageView(this);
				Bitmap bm = BitmapFactory.decodeByteArray(item.id, 0,
						item.id.length);
				int size = 300;
				double coef = (double) size
						/ Math.max(bm.getWidth(), bm.getHeight());

				BitmapDrawable b;
				if (coef == 1.0)
					b = new BitmapDrawable(bm);
				else
					b = new BitmapDrawable(Bitmap.createScaledBitmap(bm,
							(int) (bm.getWidth() * coef + 0.5),
							(int) (bm.getHeight() * coef + 0.5), true));

				b.setBounds(0, 0, size, size);

				image.setImageDrawable(b);
				image.setPadding(0, 0, 20, 0);
				image.setTag(i);
				image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						int index = (Integer) v.getTag();
						VisitItem item = data.items.get(index);
						try {
							File f = new File(Path.getCacheDir(v.getContext()), String.format(
									"%d.jpg", index));
							FileOutputStream fos = new FileOutputStream(f);
							BufferedOutputStream bos = new BufferedOutputStream(
									fos);
							bos.write(item.id);
							bos.close();

							Preview.open(v.getContext(), f.getAbsolutePath());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				layout.addView(image);
			}
		}
	}

	private void prepareItemsDlg(Dialog dialog, Bundle bundle) {
		ListView list = (ListView) dialog.findViewById(R.id.list);
		Basket basket = bundle.getParcelable(ITEMS);
		list.setAdapter(new ItemsAdapter(this, basket.data));
	}

	private Dialog createItemsDlg(Bundle bundle) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.items_dlg, null);
		builder.setView(view);
		return builder.create();
	}
}

class ItemsAdapter extends BaseAdapter {
	List<RouteResult2Item> data;
	Context context;

	public ItemsAdapter(Context context, List<RouteResult2Item> data) {
		this.data = data;
		this.context = context;
	}

	public void setData(List<RouteResult2Item> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		if (view == null)
			view = View.inflate(context, R.layout.items_dlg_row, null);

		RouteResult2Item item = (RouteResult2Item) getItem(pos);

		((TextView) view.findViewById(R.id.tvName)).setText(item.name);
		((TextView) view.findViewById(R.id.tvQty)).setText(Util.IntToScaleStr(
				item.qty, Consts.QTY_SCALE));
		((TextView) view.findViewById(R.id.tvCost)).setText(Util.IntToScaleStr(
				item.cost, Consts.SUM_SCALE));
		return view;
	}

}

class ReCh implements OnClickListener {
	private int fid;
	private FragmentActivity fa;
	private boolean b = false;

	public ReCh(FragmentActivity fa, int fid) {
		this.fa = fa;
		this.fid = fid;
	}

	@Override
	public void onClick(View v) {
		FragmentManager fm = fa.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f = fm.findFragmentById(fid);

		if (b)
			ft.show(f);
		else
			ft.hide(f);

		b = !b;
		ft.commit();
	}
}
