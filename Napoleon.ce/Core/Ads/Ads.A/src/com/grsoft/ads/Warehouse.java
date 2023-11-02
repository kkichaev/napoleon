package com.grsoft.ads;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.ads.documents.OrderItemsDocument;
import com.grsoft.ads.utils.ItemsBaseAdapter;
import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.ads.R;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.InputNumber;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class Warehouse extends BaseActivity {
	private PriceAdapter priceAdapter;
	private ListView lvOrderItemSelect;
	private long rowid;
	private LinesCountController linesController;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wh_ads);
		rowid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		lvOrderItemSelect = ((ListView)findViewById(R.id.lvOrderItemSelect));
		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				lvOrderItemSelect, btnLines, this);
		linesController = linesOnClickListener.getController();
	}
	
	public static void open(Context context, long rowid){
		Intent intent = new Intent(context, Warehouse.class);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(intent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (priceAdapter != null)
			priceAdapter.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		priceAdapter = new PriceAdapter(this, rowid, linesController);
		lvOrderItemSelect.setAdapter(priceAdapter);
		lvOrderItemSelect.setOnItemClickListener(priceAdapter);
		findViewById(R.id.llTop).setOnClickListener(priceAdapter);
		findViewById(R.id.ivGoUp).setVisibility(View.GONE);
	}
}

class PriceAdapter extends ItemsBaseAdapter 
implements OnItemClickListener, OnClickListener{
	private static final String ROOT_FOLDER_ID = "";
	private SQLiteCursor cursorFolders;
	private SQLiteCursor cursorPrice;
	private List<PriceNode> tree= new ArrayList<PriceNode>();
	private Stack<PriceNode> stack = new Stack<PriceAdapter.PriceNode>(); 
	private OrderItemsDocument<? extends CreateDocDataObject> orderItemsDocument; 
	
	
	abstract class PriceNode{
		public String id = "";
		public String name = "";
		
		public PriceNode(String id, String name){
			this.id = id;
			this.name = name;
		}
		
		public abstract int getResource();
		public abstract void fillView(View view);
		public abstract void click();
	}
	
	class FolderNode extends PriceNode{
		public String parent = "";
		
		public FolderNode(String id, String name, String parent) {
			super(id, name);
			this.parent = parent;
		}

		@Override
		public int getResource() {
			return R.layout.itemselectrow;
		}

		@Override
		public void fillView(View view) {
			TextView tvOrgName = (TextView)view.findViewById(R.id.tvItemSelectRowName);
			tvOrgName.setText(name);
			applyLineController(tvOrgName);
		}

		@Override
		public void click() {
			stack.push(this);
			query(id);
		}
	}
	
	private void query(String folderid){
		cursorFolders.setSelectionArguments(new String[]{folderid});
		cursorFolders.requery();
		cursorPrice.setSelectionArguments(new String[]{folderid});
		cursorPrice.requery();
		adjustTopPanel();
		makeTree();
		notifyDataSetChanged();
	}
	
	class WarehouseNode extends PriceNode{
		public int cost = 0;
		
		public WarehouseNode(String id, String name, int cost) {
			super(id, name);
			this.cost = cost;
		}

		@Override
		public int getResource() {
			return R.layout.priceitemrow;
		}

		@Override
		public void fillView(View view) {
			
			final int textColor = ((Itemsable)orderItemsDocument)
					.findItem(id) == null ? Color.BLACK : Color.GREEN;
			
			TextView tvPriceItemName = (TextView) view.findViewById(R.id.tvPriceItemName);
			tvPriceItemName.setText(name);
			tvPriceItemName.setTextColor(textColor);
			
			TextView tvClmn1 = (TextView) view.findViewById(R.id.tvClmn1);
			tvClmn1.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE));
			tvClmn1.setTextColor(textColor);
			
			applyLineController(tvClmn1);
			
			OrderItem orderItem = getOrderItem(id);
			TextView tvClmn2 = (TextView) view.findViewById(R.id.tvClmn2);
			tvClmn2.setText(Util.IntToScaleStr(orderItem == null ? 0 : orderItem.qty, Consts.QTY_SCALE));
			tvClmn2.setTextColor(textColor);
		}

		@Override
		public void click() {
			InputNumberDlg.open(getContext(), new InputNumber() {
				
				@Override
				public int getValue() {
					OrderItem orderItem = getOrderItem(id); 
					return orderItem == null ? 0 : orderItem.qty;
				}
				
				@Override
				public void applayInput(int value, Object... params) {
					setOrderItem(id, value);
					notifyDataSetChanged();
				}
			});
			
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public PriceAdapter(Context context, long rowid, LinesCountController controller){
		super(context, controller);

		orderItemsDocument = (OrderItemsDocument<? extends CreateDocDataObject>) 
				DocType.getCurDoc().create();
		
		if (orderItemsDocument.read(rowid)){
			SQLiteDatabase dataBase = DataBaseManager.getDataBase();
			FolderNode root = new FolderNode(ROOT_FOLDER_ID, "Прайс", ROOT_FOLDER_ID);
			stack.push(root);
			try{
				cursorFolders = (SQLiteCursor) dataBase.query("folders", null, "parent=?", 
					new String[]{ROOT_FOLDER_ID}, null, null, null);
				cursorPrice = (SQLiteCursor) dataBase.query("warehouse",
						null, "folder=?", new String[]{ROOT_FOLDER_ID}, null, null, null);
				makeTree();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		orderItemsDocument.close();
	}

	private void makeTree() {
		tree.clear();
		
		while(cursorFolders.moveToNext()){
			tree.add(new FolderNode(
					cursorFolders.getString(cursorFolders.getColumnIndex("id")),
					cursorFolders.getString(cursorFolders.getColumnIndex("name")),
					cursorFolders.getString(cursorFolders.getColumnIndex("parent"))));
		}
		
		while(cursorPrice.moveToNext())
			tree.add(new WarehouseNode(
					cursorPrice.getString(cursorPrice.getColumnIndex("id")),
					cursorPrice.getString(cursorPrice.getColumnIndex("name")),
					cursorPrice.getInt(cursorPrice.getColumnIndex("cost"))));
	}

	public void close(){
		if (cursorFolders != null)
			cursorFolders.close();
		
		if (cursorPrice != null)
			cursorPrice.close();
		
		if (orderItemsDocument != null && 
				orderItemsDocument.getRowid() != ExtrasConst.INVALID_ID)
			orderItemsDocument.write();
	}
	
	@Override
	public int getCount() {
		return tree.size();
	}

	@Override
	public Object getItem(int pos) {
		return tree.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		PriceNode node = (PriceNode)getItem(pos);
		view = View.inflate(getContext(), node.getResource(), null);
		node.fillView(view);
		
		if (view != null)
			view.setBackgroundResource(pos % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector);
		
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		PriceNode node = (PriceNode) getItem(pos);
		node.click();
	}

	@Override
	public void onClick(View v) {
		FolderNode top =  (FolderNode) stack.peek();
		
		if (!top.id.equals(ROOT_FOLDER_ID))
			query(((FolderNode)stack.pop()).parent);
	}
	
	private void adjustTopPanel(){
		ImageView ivGoUp =(ImageView) ((Activity)getContext()).findViewById(R.id.ivGoUp);
		TextView tvItemSelectUpLevel = (TextView)((Activity)getContext()).findViewById(R.id.tvItemSelectUpLevel);
		
		FolderNode topStack = (FolderNode) stack.peek();
		
		if (topStack.id.equals(ROOT_FOLDER_ID))
			ivGoUp.setVisibility(View.GONE);
		else
			ivGoUp.setVisibility(View.VISIBLE);
		
		tvItemSelectUpLevel.setText(topStack.name);
		
	}
	
	private void setOrderItem(String itemid, int value){
		OrderItem orderItem = getOrderItem(itemid);
		
		if (orderItem != null){
			if (value > 0)
				orderItem.qty = value;
			else
				orderItemsDocument.getOrderItems().remove(orderItem);
		}else if (orderItem == null && value > 0){
			orderItem = new OrderItem();
			orderItem.priceid = itemid;
			orderItem.qty = value;
			
			orderItemsDocument.getOrderItems().add(orderItem);
		}
	}
	
	private OrderItem getOrderItem(String itemid){
		OrderItem result = null;
		
		for (OrderItem oi : orderItemsDocument.getOrderItems()){
			if (oi.priceid.equals(itemid)){
				result = oi;
				break;
			}
		}
		
		return result;
	}
}
