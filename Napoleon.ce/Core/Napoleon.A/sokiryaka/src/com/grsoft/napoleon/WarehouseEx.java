package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

public class WarehouseEx extends WarehouseNew {
	static String idStore = ""; 
	PriceImpl pi = new PriceImpl();
	
	public static void resetCache() { idStore = ""; }

	@Override
	protected Filter createZeroPositionFilter() {
		if(document instanceof OrderImplEx) {
			OrderEx o = (OrderEx) document.getData();
			if(idStore.equals(o.whCode) == false ) {
				FoldersAdapter.resetCache();
				idStore = o.whCode;
			}
			return new ZeroFilter();
		}
		return super.createZeroPositionFilter();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pi.close();
	}
	
	class ZeroFilter extends ZeroPositionFilter {
		
		@Override public String getWhereStr() { return ""; }
		
		@Override
		public boolean inset(long priceRowID, String id) {
			boolean result = false; 
			
			Price p = pi.getData();
			p.id = id;
			pi.read();
			result = (((OrderImplEx)document).getItemValue(p) > 0);			
			return result;
		}
	}

	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);

		ImageView iv = (ImageView) result.findViewById(R.id.ivShowPresent);
		android.database.Cursor cursor = null;

		try {
			final String CLMN_NAME = "photoPath";
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			cursor = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(Present.class),
					new String[] { CLMN_NAME }, "id=?", new String[] { price.getData().id }, null, null, null);

			if (cursor.moveToFirst()) {
				iv.setVisibility(View.VISIBLE);
				iv.setTag(cursor.getString(cursor.getColumnIndex(CLMN_NAME)));

				iv.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							Context context = v.getContext();
							AlertDialog.Builder builder = new AlertDialog.Builder(context);
							View dialogView = View.inflate(context, R.layout.image_show, null);
							ImageView preview = (ImageView) dialogView.findViewById(R.id.imageView1);
							Bitmap bm = BitmapFactory.decodeFile((String) v.getTag());
							preview.setImageBitmap(bm);
							builder.setView(dialogView);
							builder.create().show();
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(v.getContext(), getString(R.string.cant_loading_img), Toast.LENGTH_SHORT).show();;
						}
					}
				});
			} else
				iv.setVisibility(View.INVISIBLE);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return result;
	}

	@Override
	protected int getItemLayoutId() {
		return R.layout.priceitemrowex;
	}

}
