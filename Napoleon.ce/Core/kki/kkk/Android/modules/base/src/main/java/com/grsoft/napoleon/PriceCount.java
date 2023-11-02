/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Установка количества для прайса
 *
 * kki   27/11/2010   creating
 */
package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Present;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.QtyItem;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OffTakeHistory;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.SalesHistory;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.BitmapUtils;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FPOperation;
import com.grsoft.util.InputNumber;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.KeypadHelper;
import com.grsoft.view.SimpleMessageBox;

public class PriceCount extends BaseActivity
{
	public static Class<? extends Activity> activity = PriceCount.class;
	public static PriceMover PriceMover = null;
	
	protected PriceImpl price = new PriceImpl();
	protected CreatableDocument<?> document = null;
	
	private TextView tvPriceName;
	protected ImageButton btnOK;
	protected EditText edCount;
	protected TextView tvSum;
	protected CheckBox cbPackets;
	protected int priceVal;
	public int qtyInPack;
	protected int qtyItems;
	protected OffTakeHistory.Item lastItem = null;
	
	protected EditText edRest;
	protected RemnantsImpl rdoc;
	protected OffTakeHistory history;
	protected TextView firstView = null;
	protected ImageView ivPresent;
	
	KeypadHelper keypadHelper = null;
	protected LinearLayout llKeyboard;
	private CfgNpl config;
	ImageView ivPresent2;

	@Override
	protected void onStop() {
		super.onStop();
		price.close();
		if( document != null )
			document.close();

	}
	
	protected int getContentViewId() { return R.layout.pricecount; }
	
	protected String getItemName(Price p) {
		return p.name;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());

		config = (CfgNpl) ConfigManager.getConfig();
		
		if(config.keepAwayInOrder)
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		price.read(getIntent().getLongExtra(ExtrasConst.PRICE_ROW_ID_STR, ExtrasConst.INVALID_ID));
		price.close();
		
		boolean editable = false;
		long rid = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		if( rid != ExtrasConst.INVALID_ID ) {
			document = (CreatableDocument<?>) DocType.getCurDoc().create();
			document.read(rid);
			document.close();
			editable = document.isEditable();
		}
		
		tvPriceName = (TextView) findViewById(R.id.tvPriceName);
		btnOK = (ImageButton) findViewById(R.id.btnOK);
		btnOK.setEnabled(editable);
		btnOK.setOnClickListener(new BtnOKClickListenet());
				
		cbPackets = (CheckBox) findViewById(R.id.cbPackets);

		if (cbPackets != null)
			cbPackets.setOnCheckedChangeListener(createPacketChangeListener());
		
		tvSum = (TextView) findViewById(R.id.tvSum);

		edCount = (EditText) findViewById(R.id.edCount);

		if (edCount != null) {
			edCount.addTextChangedListener(new CountTextWatcher());
			edCount.setInputType(InputType.TYPE_NULL);
			edCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						keypadHelper.setTargetID(R.id.edCount);
						edCount.selectAll();
					}
				}
			});
		}
		
		keypadHelper = createKeypadHelper();
		llKeyboard = (LinearLayout) findViewById(R.id.llKeyboard);
		
		if (!editable)
			llKeyboard.setVisibility(View.GONE);
		
		View vRest = findViewById(R.id.trRest);
		rdoc = (RemnantsImpl) RemnantsDoc.instance().create();
		if(document != null && isComplexSalesHistory() && vRest != null){
			keypadHelper.setTargetID(R.id.edRest);
			
			vRest.setVisibility(View.VISIBLE);
			
			long rc = RemnantsImpl.find(document.getId(), document.getData().created);
			if( rc != ExtrasConst.INVALID_ID ){
				rdoc.read(rc);
				rdoc.close();
			}else
				rdoc.init(document);
			
			edRest = (EditText) findViewById(R.id.edRest);
			TextWatcher tv = getRestUpdateHandler();
			if( tv != null )
				edRest.addTextChangedListener(tv);
			
			edRest.setOnFocusChangeListener(new View.OnFocusChangeListener() {			
				@Override public void onFocusChange(View v, boolean hasFocus) {
					if( hasFocus ) {
						keypadHelper.setTargetID(R.id.edRest);
						edRest.selectAll();
					}
				}
			});
			
			edRest.setInputType(InputType.TYPE_NULL);

			if (edCount != null)
				edCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override public void onFocusChange(View v, boolean hasFocus) {
						if( hasFocus ) {
							keypadHelper.setTargetID(R.id.edCount);
							edCount.selectAll();
						}
					}
				});
		}
		
		if( havePriceMover() && PriceMover != null) {
			View v;
			v = findViewById(R.id.btnMoveNext);
			if( v != null ) {
				v.setVisibility(View.VISIBLE);
				v.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { movePrice(true); }
				});
			}

			v = findViewById(R.id.btnMovePrev);
			if( v != null ) {
				v.setVisibility(View.VISIBLE);
				v.setOnClickListener(new View.OnClickListener() {
					@Override public void onClick(View v) { movePrice(false); }
				});
			}
		}
		
		ivPresent = (ImageView) findViewById(R.id.ivPresent);
		if( ivPresent != null)
			ivPresent.setVisibility(View.GONE);

		ivPresent2 = (ImageView) findViewById(R.id.ivPresent2);
		if( ivPresent2 != null )
			ivPresent2.setVisibility(View.INVISIBLE);

		postOnCreate();		
		refreshData();
	}


	
	protected boolean havePriceMover() { return Features.HAVE_PRICE_MOVER; } 

	protected KeypadHelper createKeypadHelper() {
		return new KeypadHelper(this, R.id.edCount);
	}

	protected void postOnCreate() {}

	void setTopImage(final String fileName){
		try{
			final int PICSZ = 200;
			ivPresent.setImageDrawable(BitmapUtils.createBitmap(fileName, PICSZ));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected void setItemImage(final String fileName) {


		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		switch(cfg.imagePosInPriceCount){
			case 0:
				if( ivPresent != null )
					ivPresent.setVisibility(View.VISIBLE);
				setTopImage(fileName);
				break;
			case 1:
				if( ivPresent2 != null )
					ivPresent2.setVisibility(View.VISIBLE);
				setCenterImage(fileName);
				break;
		}
	}

	protected void setCenterImage(final String fileName) {
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			Bitmap src = BitmapFactory.decodeFile(fileName, opt);
			ivPresent2.setImageDrawable(new BitmapDrawable(src));
			ivPresent2.setVisibility(View.VISIBLE);
			ivPresent2.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { PricePresentation.open(v.getContext(), fileName, price.getRowid());	} });
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	protected OnCheckedChangeListener createPacketChangeListener() {
		return new PacketCliskListener();
	}
	
	class MoveNextR implements Runnable {
		public MoveNextR(boolean next) { this.next = next; }
		
		boolean next;
		
		@Override
		public void run() {
			PriceImpl p = PriceMover.move(price, next);
			if( p != null ) {
				updateOrder();
				price = p;
				refreshData();
			}
		}		
	}
	
	protected void movePrice(boolean next) {
		MoveNextR r = new MoveNextR(next);
		if( isInputValid(r) )
			r.run();
		else
			invalidInputValueHandler();
	}
	
	protected void updateCost() {
		String value = Util.IntToScaleStr(getInputCost(price.getData()), 
				Consts.SUM_SCALE, Util.DEC_DELIM, false);
		TextView tv = (TextView)findViewById(R.id.tvPrice);
		if( canChangeCost() ) {
			SpannableString ss = new SpannableString(value);
			ss.setSpan(new UnderlineSpan(), 0, ss.length(), 0);
			tv.setTextColor(Color.BLUE);
			tv.setText(ss);
		} else {
			tv.setText(value);
			tv.setTextColor(Color.BLACK);
		}
	}
	
	protected void onChangeCost( int newCost ) {
		priceVal = newCost;
		updateCost();
		updateSumTextView();
	}

	protected boolean canChangeCost() { 
		return Features.CAN_CHANGE_COST && (document != null && document instanceof OrderImplBase<?>); 
	}
	
	protected void doCostChange() {
		InputNumberDlg.open(this, new InputNumber() {
			@Override public void applayInput(int value, Object... params) { onChangeCost(value); }
			@Override public int getValue() { return priceVal; }		
		}, Consts.SUM_SCALE, false, getString(R.string.cost)); 
	}
	
	protected boolean isShowImage(){
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		return cfg.showImageInPriceCount;
	}
	
	protected void refreshData() {
		if( ivPresent != null )
			ivPresent.setVisibility(View.GONE);
		if( ivPresent2 != null )
			ivPresent2.setVisibility(View.INVISIBLE);

		Price p = price.getData();
		tvPriceName.setText(Html.fromHtml(getItemName(p)));
		
		if(ivPresent != null)
			if(isShowImage())
				showItemImage();
			else
				hideItemImage();
		
		@SuppressWarnings("unchecked")
		CostStrategy costStrategy = CostStrategy
			.getInstance((Class<? extends Document<?>>) ((document == null) 
					? null : document.getClass())); 
		priceVal = costStrategy.getItemCost(p, document);
		
		qtyInPack = getQtyInPack(p);
		if( qtyInPack == 0 ) qtyInPack = Consts.QTY_SCALE;
		
		if( canChangeCost() ) {
			findViewById(R.id.tvPrice).setOnClickListener(new View.OnClickListener() {				
				@Override public void onClick(View v) { doCostChange(); }
			});
		}
		updateCost();
				
		TextView tvQtyInPack = (TextView) findViewById(R.id.tvQtyInPack);
		tvQtyInPack.setText(Util.IntToScaleStr(qtyInPack, Consts.QTY_SCALE));
		
		long qty = getStartValue();
		int whQty;
		boolean setFocusOnQty = false;
		boolean inPack = getStartInPack();
		if( document != null && document instanceof Itemsable ) {
			Itemsable id = (Itemsable)document;
			whQty = id.getItemValue(p);

			QtyItem item = (QtyItem) getDocItem(p);
			if (item != null) {
				qty = item.getQty();
				inPack = ((item.getFlags() & OrderItem.IN_PACK) != 0);
				if( inPack )
					qty = FPOperation.itemMul((int)qty, Consts.QTY_SCALE, qtyInPack);
			}
			makeSaleHistory(p);	

			View vRest = findViewById(R.id.trRest);
			if(isComplexSalesHistory() && vRest != null){
				RemnantItem ri = (RemnantItem)rdoc.findItem(price.getData().id);
				
				String text = "";
				if( ri != null) {
					long rest = 0;
					rest = ri.qty;
					
					if(Features.REST_IN_PACK){
						rest = FPOperation.itemMul((int)rest, Consts.QTY_SCALE, qtyInPack);
					}
					text = getRestText(rest, ri);
					setFocusOnQty = true;
				}
				edRest.setText(text);
				edRest.selectAll();						
			}
		} else {
			Itemsable doc = (Itemsable) OrderDoc.instance().create();
			whQty = doc.getItemValue(p);
		}
		
		if(setFocusOnQty || edRest == null)
			if (edCount != null)
				edCount.requestFocus();
		else if (edRest != null)
			edRest.requestFocus();

		if (cbPackets != null)
			cbPackets.setChecked(inPack);

		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNplW)ConfigManager.getConfig()).isPackView )
			whQty = (int)((long)whQty * Consts.QTY_SCALE / p.qtyInPack);
		TextView tvQty = (TextView) findViewById(R.id.tvQty);

		if (tvQty != null)
			tvQty.setText(Util.IntToScaleStr(whQty, Consts.QTY_SCALE));

		qtyItems = (int)qty;

		if (edCount != null) {
			edCount.setText(Util.IntToScaleStr((int) qty, Consts.QTY_SCALE));
			edCount.selectAll();
		}
	}

	protected void showItemImage() {
		android.database.Cursor cursor = null;
		
		try{
			final String CLMN_NAME = "photoPath";
			DbWriter.checkDBTable(DbObject.getDataType(Present.class));
			cursor = DataBaseManager.getDataBase().query(
					DataObjectInfo.getInstance().getTableName(Present.class), new String[]{CLMN_NAME}, 
					"id=?", new String[]{price.getData().id}, null, null, null);
			
			if(cursor.moveToFirst()){
				final String path = cursor.getString(cursor.getColumnIndex(CLMN_NAME));
//				ivPresent.setVisibility(View.VISIBLE);
				ivPresent.setOnClickListener(new OnClickListener() { @Override public void onClick(View v) { PricePresentation.open(v.getContext(), path, price.getRowid());	} });
				setItemImage(path);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (cursor != null)
				cursor.close();
		}
	}

	protected String getRestText(long rest, RemnantItem ri) {
		String text= Util.IntToScaleStr(rest, Consts.QTY_SCALE, Util.DEC_DELIM, true);

		if( Features.PUT_REST_BEFORE_QTY ) {
			if( ri == null )
				text = "";

			if (edCount != null)
				edCount.setEnabled(ri != null);
		}
		return text;
	}

	protected void hideItemImage() {
		ivPresent.setVisibility(View.GONE);
	}

	protected DataObject getDocItem(Price p) {
		return ((Itemsable)document).findItem(p.id);
	}
	
	protected boolean getStartInPack() { return Features.INPUT_QTY_IN_PACK; }
	
	protected int getQtyInPack(Price p) { return p.qtyInPack; }

	protected int getStartValue() {	return config.priceQty;	}

	protected int getInputCost(Price p) {
		return priceVal;
	}
	
	protected boolean isComplexSalesHistory() { return ((CfgNplW)ConfigManager.getConfig()).isComplexSalesHistory; }
	
	protected OffTakeHistory getHistory(String docId, boolean fromOrders) {
		return new OffTakeHistory(docId, fromOrders);
	}
	
	protected int getHistoryLines() { return 4; }
	
	protected void makeSaleHistory(Price p) {
		if( document == null )
			return;
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.llSilesHistory);
		ll.removeAllViews();
		
		if (isComplexSalesHistory()) {
			createComplexHistoryView(p, ll);
		}else{
			createSimpleHistory(p, ll);
		}
	}

	protected void createComplexHistoryView(Price p, LinearLayout ll) {
		history = getHistory(document.getId(), Features.SALES_FROM_ORDERS);

		SimpleDateFormat sf = new SimpleDateFormat("dd.MM", Locale.getDefault());

		ArrayList<Date> labels = history.getLabels();
		ArrayList<OffTakeHistory.Item> items = history.getHistory(p.id);
		
		for( int i=0; i<labels.size(); i++ ) {
			Date cd = labels.get(i);
			String text = sf.format(cd);
			OffTakeHistory.Item item = items.get(i);
			TextView tv = new TextView(this);
			
			tv.setGravity(Gravity.RIGHT);
			tv.setTextColor(Color.BLACK);
			tv.setPadding(5, 3, 5, 3);

			text += "<br>" + item.makeText((i==0));			
			tv.setText(Html.fromHtml(text));
			
			tv.setLines(getHistoryLines());
			ll.addView(tv);
			
			if( i == 0 ) {
				firstView = tv;
				lastItem = item;
			}
		}
	}

	protected void createSimpleHistory(Price p, LinearLayout ll) {
		String historyItems[] = SalesHistory.getHistory(document.getData().id, p.id, Features.SALES_FROM_ORDERS);
		
		for (int i = 0; i < historyItems.length -1; i += 2)
		{
			TextView tvSaleItem = new TextView(this);
			tvSaleItem.setText(Html.fromHtml(
					String.format("%s<br>%s", historyItems[i], historyItems[i+1])));
			tvSaleItem.setLines(2);
			tvSaleItem.setTextColor(getResources().getColor(R.color.black));
			tvSaleItem.setPadding(5, 3, 5, 3);
			ll.addView(tvSaleItem);
			
			Log.d("makeSaleHistory", tvSaleItem.getText().toString());
		}
	}
	
//	static public void open(Context context, OrderItem item, long orderId) {
//		PriceImpl price = new PriceImpl();
//		price.getData().id = item.id;
//		price.read();
//		price.close();
//		
//		open(context, price.getRowid(), orderId);
//	}
	
//	static public void open(Context context, long priceId, long orderId) {
//		Intent i = new Intent(context, PriceCountActivity);
//		
//		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceId);
//		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderId);
//
//		context.startActivity(i);		
//	}
	
//	static public void open(Context context, PriceImpl item, long orderId) {
//		if (item.getRowid() == ExtrasConst.INVALID_ID){
//			item.read();
//			item.close();
//		}
//		open(context, item.getRowid(), orderId);
//	}


	/**
	 * Точка входа для PriceCount - (вызывется через document.editItem)
	 * @param context
	 * @param priceRoid
	 * @param doc
	 */
	public static void open(Context context, long priceRoid, DbObject<? extends DataObject> doc) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());

		context.startActivity(i);		
	}
	
	protected int getCountValue() {
		String text = edCount != null ? edCount.getText().toString() : "";

		return text.length() == 0 
			? 0 
			: Util.StrToScale(text, Consts.QTY_SCALE);
	}

	protected int countWithPack() {
		int value = getCountValue();
		if(cbPackets.isChecked()) {
			value = (int)((long)value * qtyInPack / Consts.QTY_SCALE);
		}
		return value;
	}
	
	protected long getSumValue() {
		int count = getCountValue();
		long sumItems = getSum(count);

		qtyItems = count;
		return sumItems;
	}
	
	protected void updateSumTextView()
	{
		try
		{
			long sumItems = getSumValue();
			tvSum.setText(Util.IntToScaleWStr(sumItems, Consts.SUM_SCALE, Consts.PRICE_DEC_WIDTH, false));
			
		}
		catch(NumberFormatException  e){}
	}

	protected long getSum(int count) {		
		if(cbPackets != null && cbPackets.isChecked())
			count = (int)FPOperation.itemMul(count, qtyInPack, Consts.QTY_SCALE);
		
		long val = (long)getInputCost(price.getData()) * count / Consts.QTY_SCALE;
		return val;
//		return (int)FPOperation.itemMul(getInputCost(price.getData()), count, Consts.QTY_SCALE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent. KEYCODE_MENU){
			switchKeyboardVisible();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	protected void switchKeyboardVisible() {
		LinearLayout llKeyboard = (LinearLayout) findViewById(R.id.llKeyboard);
		
		if (llKeyboard.isShown())
			llKeyboard.setVisibility(View.GONE);
		else
			llKeyboard.setVisibility(View.VISIBLE);
	}
	
	class BtnOkR implements Runnable {

		@Override
		public void run() {
			boolean showAlert = updateOrder();
			
			if( showAlert ) {
				SimpleMessageBox smb = new SimpleMessageBox(getString(R.string.warning), 
						getString(R.string.qty_has_been_reduced), PriceCount.this);
				smb.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						postOKProcess();
					}
				});
				smb.show();					

			} else
				postOKProcess();
		}				
	}
	
	protected void postOKProcess() {
		PriceCount.this.finish();
	}
	
	class BtnOKClickListenet extends OnClickListenerToNotify {
		@Override
		public void onClick(View v) {
			super.onClick(v);
			
			BtnOkR r = new BtnOkR();
			if( !isInputValid(r) ) {
				invalidInputValueHandler();
				return;
			}
			
			r.run();
		}
	}
	
	class PacketCliskListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			updateSumTextView();
		}
	}
	
	/**
	 * При изменениии количества
	 * пересчитать цену
	 * @author kki
	 *
	 */
	class CountTextWatcher implements TextWatcher
	{
		@Override
		public void afterTextChanged(Editable s)
		{
			updateSumTextView();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
		}
	}

	/**
	 * Определяет возможно ли закрытие окна диалога или переход на следующую позицию
	 * @return
	 */
	protected boolean isInputValid(Runnable r) { return true; }
	
	/***
	 * реакция на неверные данные
	 */
	protected void invalidInputValueHandler() {}

	protected boolean updateOrder() {
		boolean inPack = cbPackets != null && cbPackets.isChecked();
		
		if (isComplexSalesHistory()){
			int rest = 0;
			
			if( edRest != null ) {
				Editable txt = edRest.getText();
				updateRest(inPack, rest, txt);
			}
		}
		
		boolean showAlert = false;
		if( document != null ) {
			int qty = qtyItems;
			qty = fixOrderQty(inPack, qty, price.getData());
			
			showAlert = updateQty(inPack, qty); 
		}
		
		return showAlert;
	}

	protected void updateRest(boolean inPack, int rest, Editable txt) {
		if( txt == null || txt.length() == 0 ) {
			rdoc.deleteItem(price.getData());
		} else {
			rest = Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
			if(Features.REST_IN_PACK)
				rest = fixOrderQty(inPack, rest, price.getData());			
			rdoc.updateQty(price, rest, 0, false);
		}
		if(rdoc.isEmpty())
			rdoc.delete();
	}

	protected boolean updateQty(boolean inPack, int qty) {
		return !((Itemsable)document).updateQty(price, 
				qty, getInputCost(price.getData()), inPack);
	}

	protected int fixOrderQty(boolean inPack, int qty, Price price) {
		if( inPack )
			qty = (int)FPOperation.itemMul(qty, qtyInPack, Consts.QTY_SCALE);
		
		return qty;
	}
	
	protected TextWatcher getRestUpdateHandler() { return new RestUpdate(); }
	
	class RestUpdate implements TextWatcher {

		@Override
		public void afterTextChanged(Editable txt) {
			int rest = 0;
			if( txt != null && txt.length() != 0 ) {
				rest = Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
			}
			if( Features.PUT_REST_BEFORE_QTY )
				if (edCount != null)
					edCount.setEnabled((txt != null && txt.length() != 0));
			
			if( firstView != null ) {
				OffTakeHistory.Item item = history.updateRest(price.getData().id, rest, null);
				
				SimpleDateFormat sf = new SimpleDateFormat("dd.MM", Locale.getDefault());
				String text = sf.format(item.date);
				text += "<br>" + item.makeText(true);			
				firstView.setText(Html.fromHtml(text));
			}
		}

		@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override public void onTextChanged(CharSequence s, int start, int before, int count) {}		
	}
}