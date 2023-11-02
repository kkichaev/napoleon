package com.grsoft.napoleon.dostavka;

import java.util.ArrayList;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class DWaybillItemEdit extends BaseDialogFragment {
	private DWaybillDocumentImpl<?> doc;
	private DWaybillDocumentItem item;
	private TextView tvInQty;
	private EditText edQty;
	private Spinner spCause;
	private EditText edRemark;
	private View btnOk;
	private View btnCancel;
	private ImageView ivPic;
	private TextView tvPrice;
	
	public static String ITEM_POS = "ITEM_POS";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.dlvitemedit, container, false);
		tvInQty = (TextView) view.findViewById(R.id.tvInQty);
		edQty = (EditText) view.findViewById(R.id.edQty);
		spCause = (Spinner) view.findViewById(R.id.spCause);
		edRemark = (EditText) view.findViewById(R.id.edRemark);
		btnOk = view.findViewById(R.id.btnOK);
		btnCancel = view.findViewById(R.id.btnCancel);
		ivPic = (ImageView) view.findViewById(R.id.ivPic);
		tvPrice = (TextView) view.findViewById(R.id.tvPrice);
		
		doc = ((DWaybillEdit)getActivity()).doc;
		int pos = getArguments().getInt(ITEM_POS);
		
		if(doc != null && pos < doc.getData().items.size()){
			item = doc.getData().items.get(pos);
			tvInQty.setText(getString(R.string.inqtyval, Util.IntToScaleStr(item.inqty, Consts.QTY_SCALE)));
			edQty.setText(Util.IntToScaleStr(item.outqty, Consts.QTY_SCALE));
			edRemark.setText(item.remark);
			
			DialogHelper.loadSpinnerFromConfig(new ConfigImpl(), "ДоставкаПричины", new ArrayList<CharSequence>(), spCause, item.cause);
			btnOk.setOnClickListener(okClick);
			
			android.database.Cursor cursor = null;
			PriceImpl price = new PriceImpl();
			price.read("id", item.id);
			tvPrice.setText(price.getData().name);

			try {
				String id = price.getData().id;
				final String CLMN_NAME = "photoPath";
				DbWriter.checkDBTable(DbObject.getDataType(Present.class));
				cursor = DataBaseManager.getDataBase().query(DataObjectInfo.getInstance().getTableName(Present.class),
						new String[] { CLMN_NAME }, "id=?", new String[] { id }, null, null, null);
	
				if (cursor.moveToFirst()) {
					String path = cursor.getString(cursor.getColumnIndex(CLMN_NAME));
					ivPic.setTag(path);
					Bitmap bitmap = BitmapUtils.createBitmap(path, 50, 50);
					ivPic.setImageBitmap(bitmap);
	
					ivPic.setOnClickListener(new OnClickListener() {
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
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
		}
		
		btnCancel.setOnClickListener(cancelClick);
		return view;
	}
	
	OnClickListener okClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int val = Util.StrToScale(edQty.getText().toString().trim(), Consts.QTY_SCALE);
			
			if(val <= item.inqty){
				item.outqty = val;
			
				Object selCause = spCause.getSelectedItem();
				
				if(selCause != null)
					item.cause = selCause.toString();
				
				item.remark = edRemark.getText().toString().trim();
				doc.write();
				doc.close();
				
				((DWaybillEdit)getActivity()).notityDataSetChanged();
				dismiss();
			}else
				Toast.makeText(getActivity(), R.string.fact_qty_invalid, Toast.LENGTH_SHORT).show();
		}
	};
	
	OnClickListener cancelClick = new OnClickListener() { @Override public void onClick(View v) { dismiss(); } };
	
}
