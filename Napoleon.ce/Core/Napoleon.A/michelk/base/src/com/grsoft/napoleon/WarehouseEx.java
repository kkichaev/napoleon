package com.grsoft.napoleon;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.StoreUtils;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.BitmapUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class WarehouseEx extends WarehouseNew {
	Map<String, WeakReference<Bitmap>> hash = new WeakHashMap<String, WeakReference<Bitmap>>();
	
	@Override
	public View getPriceView(PriceTreeNode node, View convertView) {
		View result = super.getPriceView(node, convertView);

		ImageView iv = (ImageView) result.findViewById(R.id.ivShowPresent);
		android.database.Cursor cursor = null;

		try {
			String id = price.getData().id;
			if(hash.containsKey(id) && hash.get(id).get() != null){
				iv.setImageBitmap(hash.get(id).get());
				iv.setVisibility(View.VISIBLE);
			}else{
				final String CLMN_NAME = "photoPath";
				DbWriter.checkDBTable(DbObject.getDataType(Present.class));
				cursor = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(Present.class),
						new String[] { CLMN_NAME }, "id=?", new String[] { id }, null, null, null);
	
				if (cursor.moveToFirst()) {
					iv.setVisibility(View.VISIBLE);
					String path = cursor.getString(cursor.getColumnIndex(CLMN_NAME));
					iv.setTag(path);
					Bitmap bitmap = BitmapUtils.createBitmap(path, 50, 50);
					iv.setImageBitmap(bitmap);
	
					iv.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								Context context = v.getContext();
								AlertDialog.Builder builder = new AlertDialog.Builder(context);
								View dialogView = View.inflate(context, R.layout.image_show, null);
								ImageView preview = (ImageView) dialogView.findViewById(R.id.imageView1);
								Bitmap bm = BitmapUtils.createBitmap((String)v.getTag(), 800, 600);
								preview.setImageBitmap(bm);
								builder.setView(dialogView);
								builder.create().show();
							} catch (Exception e) {
								e.printStackTrace();
								Toast.makeText(v.getContext(), getString(R.string.cant_loading_img), Toast.LENGTH_SHORT).show();;
							}
						}
					});
				}else
					iv.setVisibility(View.INVISIBLE);
			}

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

	@Override
	public void afterBuildSet() {
		super.afterBuildSet();
		StoreUtils.initPresentation();
	}
}
