package com.grsoft.napoleon.dostavka;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.impl.DWaybillDocumentImpl;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.util.FilterAdapter;
import com.grsoft.napoleon.util.FindTextWatcher;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;


public abstract class DWaybillEdit extends FragmentActivity {
	public DWaybillDocumentImpl<?> doc;
	public ListView list;
	private TextView tvSum;
	public static final String DOCTYPE = "doctype";
	private Map<String, WeakReference<Bitmap>> hash = new WeakHashMap<String, WeakReference<Bitmap>>();
	private EditText edFind;
	private TextWatcher textWatcher;
	private View btnDelFind;
	protected Adapter adapter;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getLayoutID());
		list = (ListView) findViewById(R.id.list);
		tvSum = (TextView) findViewById(R.id.tvSum);
		edFind = (EditText) findViewById(R.id.edFind);
		btnDelFind = findViewById(R.id.btnDelFind);

		textWatcher = new FindTextWatcher(edFind, list);
		edFind.addTextChangedListener(textWatcher);
		btnDelFind.setOnClickListener(delFindOnClick);

		doc = createDocument((Class<? extends DWaybillDocumentImpl<?>>) getIntent().getSerializableExtra(DOCTYPE));

		if(doc != null){
			doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));

			if (doc.getData().items.size() > 0){
				adapter = createAdapter();
				list.setAdapter(adapter);

				if(doc.isEditable())
					list.setOnItemClickListener(itemClick);
			}else{
				Toast.makeText(this, R.string.waybilnotfound, Toast.LENGTH_SHORT).show();
				doc.delete();
				doc.close();
			}

			updateView();
		}
	}

	@NonNull
	public Adapter createAdapter() {
		return new Adapter();
	}

	private OnClickListener delFindOnClick = new OnClickListener() { @Override public void onClick(View v) { edFind.setText(""); } } ;

	protected abstract int getLayoutID();
	
	protected DWaybillDocumentImpl<?> createDocument(Class<? extends DWaybillDocumentImpl<?>> doctype){
		DWaybillDocumentImpl<?>  result = null;
		
		try{
			result = doctype.newInstance();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	protected abstract DialogFragment createItemEditDialog();
	
	OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
			DWaybillDocumentItem item = (DWaybillDocumentItem) parent.getItemAtPosition(position);
			changeItemQty(item);
		}};
		
	protected void changeItemQty(final DWaybillDocumentItem item) {
		InputNumberDlg.open(DWaybillEdit.this, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				if(value < item.inqty) {
					item.outqty = value;
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public long getValue() {
				return item == null ? 0 : item.outqty;
			}
		});
	}
		
	class Adapter extends BaseAdapter implements FilterAdapter{
		protected PriceImpl price = new PriceImpl();
		private String filter = "";
		private List<DWaybillDocumentItem> data = new ArrayList<DWaybillDocumentItem>();
		
		public Adapter(){
			reload();
		}
		
		@Override public int getCount() { return data.size(); }
		@Override public Object getItem(int position) { return data.get(position); }
		@Override public long getItemId(int position) {	return 0; }

		protected void setView(DWaybillDocumentItem i, View convertView, int color, int position) {
			TextView tv = (TextView) convertView.findViewById(R.id.tvPos);
			tv.setText(Integer.toString(position + 1));
			tv.setTextColor(color);

			String name = "";
			if(price.read("id", i.id)) {
				name = price.getData().name;
			} else {
				name = "<" + i.id + ">";
			}
			tv = (TextView) convertView.findViewById(R.id.tvName);
			tv.setText(name);
			tv.setTextColor(color);

			String text;
			tv = (TextView) convertView.findViewById(R.id.tvQty);
			text = Util.IntToScaleStr(i.outqty, Consts.QTY_SCALE);
			if(i.inqty != i.outqty) {
				text += " / " + Util.IntToScaleStr(i.inqty, Consts.QTY_SCALE);
			}
			tv.setText(text);
			tv.setTextColor(color);

			tv = (TextView) convertView.findViewById(R.id.tvSum);
			tv.setText(Util.IntToScaleStr(FPOperation.itemMul(i.cost, i.outqty, Consts.QTY_SCALE), Consts.SUM_SCALE));
			tv.setTextColor(color);

			loadPhoto(convertView);
		}

		protected void loadPhoto(View convertView) {
			ImageView iv = (ImageView) convertView.findViewById(R.id.ivPic);
			if (iv != null)
				iv.setVisibility(View.GONE);

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

						hash.put(id, new WeakReference<Bitmap>(bitmap));
					}else
						iv.setVisibility(View.GONE);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = View.inflate(DWaybillEdit.this, getItemLayout(), null);
			
			DWaybillDocumentItem i = (DWaybillDocumentItem) getItem(position);
			
			int color = getTextColor(i);

			setView(i, convertView, color, position);
			
			int br = position % 2 == 0 ? R.drawable.even_row_selector : R.drawable.list_selector;
			convertView.setBackgroundDrawable( getResources().getDrawable(br) );
			
			return convertView;
		}

		public int getTextColor(DWaybillDocumentItem i) {
			return i.inqty != i.outqty ? getResources().getColor(R.color.toxic_green) : getResources().getColor(R.color.black);
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			updateView();
		}
		@Override
		public void applyFilter(String value) {
			filter = value;
			reload();
		}
		@Override
		public void resetFilter() {
			filter = "";
			reload();
		}

		public void reload() {
			data.clear();
			
			for(DWaybillDocumentItem i : doc.getData().items){
				if(filter.length() == 0 ||
					(price.read("id", i.id) && 
						price.getData().name.toUpperCase().contains(filter.toUpperCase())))
					data.add(i);
			}
			
			notifyDataSetChanged();
		}
	}

	public int getItemLayout() {
		return R.layout.dlvitemrow;
	}

	public void notityDataSetChanged() {
		BaseAdapter adapter = (BaseAdapter) list.getAdapter();
		if(adapter != null)
			adapter.notifyDataSetChanged();
	}

	public void updateView() {
		tvSum.setText(getString(R.string.sum_doc, Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE)));
	}
}
