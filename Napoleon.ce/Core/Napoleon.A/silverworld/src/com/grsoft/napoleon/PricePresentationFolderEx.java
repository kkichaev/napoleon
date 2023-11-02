package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.Present;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;

public class PricePresentationFolderEx extends PricePresentationFolder {
	public final static int PRICE_CHANGE_DLG = 1;

	@Override
	protected int getFragmentLayoutId() {
		return R.layout.price_present_fragment_ex;
	}

	@Override
	protected void setText(View view, PresentationData pd) {
		super.setText(view, pd);
		PriceImpl impl = new PriceImpl();
		impl.read(pd.rowid);
		impl.close();

		TextView tv = (TextView) view.findViewById(R.id.tvDescription);
		tv.setText(((PriceEx) impl.getData()).desc);
	}

	@Override
	protected Fragment createFragment() {
		return new PriceFragEx();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PRICE_CHANGE_DLG:
			return createPriceChangeDialog();
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case PRICE_CHANGE_DLG:
			ListView list = ((AlertDialog) dialog).getListView(); 
			list.setAdapter(new PriceListAdapterEx(this, ((PriceFragEx)curFragment).priceIds));
			break;
		}
	}

	Fragment curFragment;
	

	private Dialog createPriceChangeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_price);
		builder.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {

				String priceId = (String) ((BaseAdapter) parent.getAdapter()).getItem(position);

				PriceImpl pi = new PriceImpl();
				pi.getData().id = priceId;

				if (pi.read())
					((Itemsable) PricePresentationFolderEx.document).editItem(pi.getRowid(),
							view.getContext());

				pi.close();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		builder.setSingleChoiceItems(new PriceListAdapterEx(this, ((PriceFragEx)curFragment).priceIds), -1,
				new PriceListClickEx());
		return builder.create();
	}

	class PriceFragEx extends PriceFrag {
		public String[] priceIds;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			SQLiteDatabase db = DataBaseManager.getDataBase();
			DbWriter.checkDBTable(Present.class);
			Cursor cursor = db.query("presentation", new String[] { "id" },
					"photoPath=?", new String[] { pd.image }, null, null, null);

			PriceImpl priceImpl = new PriceImpl();
			List<String> pIds = new ArrayList<String>();

			while (cursor.moveToNext()) {
				priceImpl.getData().id = cursor.getString(cursor
						.getColumnIndex("id"));

				if (priceImpl.read())
					pIds.add(priceImpl.getData().id);
			}

			priceIds = new String[0];
			priceIds = pIds.toArray(priceIds);
			priceImpl.close();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View result = super.onCreateView(inflater, container,
					savedInstanceState);

			if (priceIds.length > 0) {
				ImageView iv = ((ImageView) result.findViewById(R.id.ivPresent));
				iv.setTag(this);
				iv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						curFragment = (Fragment) v.getTag();
						
						if(priceIds.length == 1){
							PriceImpl pi = new PriceImpl();
							pi.getData().id = priceIds[0];

							if (pi.read())
								((Itemsable) PricePresentationFolderEx.document).editItem(pi.getRowid(),
										v.getContext());

							pi.close();
						}else
							showDialog(PRICE_CHANGE_DLG);
					}
				});
				
				PriceImpl p = new PriceImpl();
				StringBuilder sb = new StringBuilder();
				int i = 1;
				
				for(String id : priceIds){
					p.getData().id = id;
					p.read();
					sb.append(i++).append(") ").append(p.getData().name).append("<br>");
				}
				
				TextView tv = (TextView)result.findViewById(R.id.tvPriceItems);
				tv.setMovementMethod(ScrollingMovementMethod.getInstance());
				tv.setText(Html.fromHtml(sb.toString()));
				
				p.close();

			}
			return result;
		}
	}
}

class PriceListAdapterEx extends BaseAdapter{
	private String[] ids;
	private Context context;

	public PriceListAdapterEx(Context context, String[] ids) {
		this.ids = ids;
		this.context = context;
	}

	@Override
	public int getCount() {
		return ids.length;
	}

	@Override
	public Object getItem(int position) {
		return ids[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		if (view == null)
			view = View.inflate(context, R.layout.present_folder_item_row, null);

		TextView tvItem = (TextView) view.findViewById(R.id.tvItem); 
		String priceId = (String) getItem(position);
		PriceImpl pi = new PriceImpl();
		pi.getData().id = priceId;
		pi.read();
		pi.close();
		tvItem.setText(pi.getData().name);

		return view;
	}
}

class PriceListClickEx implements DialogInterface.OnClickListener{
	private static String TAG = "PriceListClick";
	
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		Log.d(TAG, "onClick");
		
		String priceId = (String)
			((BaseAdapter)((AlertDialog)dialog)
					.getListView().getAdapter())
					.getItem(which);
		
		PriceImpl pi = new PriceImpl();
		pi.getData().id = priceId;
		
		if (pi.read())
			((Itemsable)PricePresentationFolderEx.document)
				.editItem(pi.getRowid(), 
						((AlertDialog)dialog).getContext());
			
		pi.close();
		dialog.dismiss();
	}
}