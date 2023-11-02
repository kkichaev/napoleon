package com.grsoft.napoleon;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.QtyItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.FPOperation;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;
import com.grsoft.view.KeypadHelper;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class OrderControllerHelper implements OnItemClickListener {
	private ImageView image;
	private EditText edCount;
	private TextView tvSum;
	private TextView tvQtyInPack;
	private String selected = null;
	private CheckBox cbPackets;
	private ViewGroup container;
	private WarehousePrezent activity;
	private PriceImpl price = new PriceImpl();
	
	public OrderControllerHelper(WarehousePrezent activity) {
		this.activity = activity;
		
		View v = View.inflate(activity, R.layout.ordercontroller, null);
		
		image = (ImageView) v.findViewById(R.id.ivPresent2);
		edCount = (EditText) v.findViewById(R.id.edCount);
		tvSum = (TextView) v.findViewById(R.id.tvSum);
		cbPackets = (CheckBox) v.findViewById(R.id.cbPackets);
		tvQtyInPack = (TextView) v.findViewById(R.id.tvQtyInPack);
		
		container = (ViewGroup) activity.findViewById(R.id.inputLayout);
		container.addView(v);
		
		activity.lvItemSelect.setOnItemClickListener(this);
		edCount.setInputType(InputType.TYPE_NULL);
		edCount.addTextChangedListener(countWatcher);
		cbPackets.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				applyNewQty(Util.StrToScale(edCount.getText().toString(), Consts.QTY_SCALE));
			}
		});
		
		new KeypadHelper(activity, R.id.edCount);
	}
	
	private TextWatcher countWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			applyNewQty(Util.StrToScale(s.toString(), Consts.QTY_SCALE));
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TreeNode tn = (TreeNode) activity.adapter.getItem(position);
		
		if (tn instanceof FolderTreeNode)
			activity.adapter.onClick(position);
		else if (tn instanceof PriceTreeNode)
			selectItem(((PriceTreeNode)tn).getId());
		
	}
	
	protected void applyNewQty(int qty) {
		if (activity.document != null && activity.document instanceof Itemsable && price.read("id", selected)) {
			if(cbPackets.isChecked())
				qty = (int)FPOperation.itemMul(qty, price.getData().qtyInPack, Consts.QTY_SCALE);
			
			int cost = CostStrategy.defaultInstance.getItemCost(price.getData(), activity.document);
			((Itemsable)activity.document).updateQty(price, qty, cost, cbPackets.isChecked());
			tvSum.setText(Util.IntToScaleStr(cost * qty / Consts.QTY_SCALE, Consts.SUM_SCALE) );
			
			activity.notifyDataSetChanged();
		}
	}
	
	void selectItem(String id) {
		selected = id;
		container.setVisibility(id != null ? View.VISIBLE : View.INVISIBLE);
		
		setImage(readPhotoPath(id));
		
		if (activity.document != null && id != null && activity.document instanceof Itemsable && price.read("id", id)) {
			int qty = ((Itemsable)activity.document).getItemQty(price.getData());
			
			int cost = CostStrategy.defaultInstance.getItemCost(price.getData(), activity.document);
			tvSum.setText(Util.IntToScaleStr(cost * qty / Consts.QTY_SCALE, Consts.SUM_SCALE) );
			tvQtyInPack.setText(Util.IntToScaleStr(price.getData().qtyInPack, Consts.QTY_SCALE));
			
			QtyItem item = (QtyItem) ((Itemsable)activity.document).findItem(id);
			
			if (item != null)
				cbPackets.setChecked(((item.getFlags() & OrderItem.IN_PACK) != 0));
			else
				cbPackets.setChecked(false);
			
			if(cbPackets.isChecked())
				qty = (int) FPOperation.itemMul((int)qty, Consts.QTY_SCALE, price.getData().qtyInPack);
			
			setQtySilent(qty);
			edCount.selectAll();
		}	
		
		activity.notifyDataSetChanged();
	}
	
	private void setQtySilent(int qty) {
		edCount.removeTextChangedListener(countWatcher);
		String qtyStr = qty == 0 ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE);
		edCount.setText(qtyStr);
		edCount.addTextChangedListener(countWatcher);
	}
	
	private String readPhotoPath(String id) {
		String result = null;
		
		if(id != null) {
			final String CLMN_NAME = "photoPath";
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			
			Cursor cursor = null;
			
			try {
				cursor = DataBaseManager.getDataBase().query(
						DataObjectInfo.getInstance().getTableName(Present.class), new String[]{CLMN_NAME}, 
						"id=?", new String[]{id}, null, null, null);
				
				if(cursor.moveToFirst())
					result = cursor.getString(cursor.getColumnIndex(CLMN_NAME));
			}finally {
				if (cursor != null)
					cursor.close();
			}
		}
		
		return result;
	}
	
	protected void setImage(final String fileName) {
		if (fileName == null) {
			image.setImageDrawable(null);
			image.setOnClickListener(null);
		}else {
			try{
				BitmapFactory.Options opt = new BitmapFactory.Options();
				Bitmap src = BitmapFactory.decodeFile(fileName, opt);
				image.setImageDrawable(new BitmapDrawable(src));
				image.setVisibility(View.VISIBLE);
				image.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { PricePresentation.open(v.getContext(), fileName, price.getRowid());	} });
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public FoldersAdapter createAdapter() {
		return new FoldersAdapter(activity) {
			@Override
			protected void postUpdateView(View view, TreeNode node) {
				super.postUpdateView(view, node);
				drawSelectBkg(view, node);
			}
		};
	}

	protected void drawSelectBkg(View view, TreeNode node) {
		if (selected != null &&
			node instanceof PriceTreeNode && ((PriceTreeNode)node).getId().equals(selected)) {
				view.setBackgroundResource(R.drawable.list_green_selector);
		}
	}
}
