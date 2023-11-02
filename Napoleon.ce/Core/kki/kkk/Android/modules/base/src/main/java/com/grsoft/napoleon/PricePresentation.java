/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * kki   16/07/2011   creating
 */
package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.dataobjects.CostItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

/***
 * Презентация прайса. Выводит прайс с фотографиями
 * @author kki
 *
 */
public class PricePresentation extends BaseActivity 
//	implements ViewSwitcher.ViewFactory
	{
	@SuppressWarnings("unused")
	private static final String TAG = "PricePresentation"; 
	public static Class<? extends Activity> activity = PricePresentation.class;
	private Document<?> document;
	public final static int PRICE_CHANGE_DLG = 1;
	private static final String CAN_OPEN_QTY = "can_open_qty";
	String photoPath;
	String[] priceIds = new String[0];
	String selection;
	public PriceListAdapter priceListAdapter;
	       
//    public ImageSwitcher mSwitcher;

    public static void open(Context context, String path, long orderId, String condition){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, orderId);
		intent.putExtra(ExtrasConst.PRICE_PHOTO_PATH, path);
		intent.putExtra(ExtrasConst.FOLDERS_LIST_STR, condition);
		context.startActivity(intent);
	}
	
	public static void open(Context context, String path, long priceRowid){
		Intent intent = new Intent(context, activity);
		intent.putExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		intent.putExtra(ExtrasConst.PRICE_PHOTO_PATH, path);
		intent.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRowid);
		intent.putExtra(CAN_OPEN_QTY, false);
		context.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.price_present);
		initUI();
		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getExtras();
		
		long orderRowId = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		selection = b.getString(ExtrasConst.FOLDERS_LIST_STR);
		
		document = DocType.getCurDoc().create();
		if (!(document instanceof Itemsable)) {
			document = OrderDoc.instance().create();
		}
		
		if (orderRowId != ExtrasConst.INVALID_ID)
			document.read(orderRowId);
			
		priceListAdapter = new PriceListAdapter(this); 
		
		photoPath = savedInstanceState != null ? savedInstanceState.getString(ExtrasConst.PRICE_PHOTO_PATH) : 
			getIntent().getStringExtra(ExtrasConst.PRICE_PHOTO_PATH);
	
		if (photoPath != null){
			new ShowPresentation(this).execute(photoPath);
		}
		
		findViewById(R.id.btnMoveNext).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { moveToNext(true) ;}
		});
		
		findViewById(R.id.btnMovePrev).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { moveToNext(false) ;}
		});
	
//        mSwitcher = (ImageSwitcher) findViewById(R.id.isPresent);
//        mSwitcher.setFactory(this);
//        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
//                android.R.anim.slide_out_right));
//        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
//                android.R.anim.slide_in_left));
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, document.getRowid());
		outState.putString(ExtrasConst.FOLDERS_LIST_STR, selection);
		outState.putString(ExtrasConst.PRICE_PHOTO_PATH, photoPath);
	};
	
	ArrayList<String> paths; 
	protected void moveToNext(boolean next) {
		if( selection == null )
			return;
		
		if( paths == null ) {
			paths = new ArrayList<String>();
			
			SQLiteDatabase db = DataBaseManager.getDataBase();
			String stmt = Presentation.makeSelectStmt(selection);
			Cursor cursor= db.rawQuery(stmt, null);
//			Cursor cursor = db.query(true, Presentation.TABLE_NAME,  new String[]{Presentation.PHOTO_PATH}, selection, null, null, 
//				null, null, null);
			 
			while( cursor.moveToNext() ) {
				paths.add(cursor.getString(0));
			}
			cursor.close();
		}
		
		int index = 0;
		for(String v : paths) {
			if( v.equals(photoPath) ) {
				int newI = index;
				if( next ) {
					if( index < paths.size() - 1 )
						newI = index + 1;
				} else {
					if( index > 0 )
						newI = index - 1;
				}
				
				photoPath = paths.get(newI);
				new ShowPresentation(this).execute(photoPath);
				break;
			}
			index ++;
		}
	}

	private void initUI(){
		Intent intent = getIntent();
		
		if(intent == null || intent.getBooleanExtra(CAN_OPEN_QTY, true))
			findViewById(R.id.ivPresent)
				.setOnClickListener(new PresentationClick(this));
		
		((TextView)findViewById(R.id.tvPriceItems))
			.setMovementMethod(ScrollingMovementMethod.getInstance());

	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	public Document<?> getDocument() {
		return document;
	}
	
	private Dialog createPriceChangeDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_price);
		builder.setSingleChoiceItems(priceListAdapter, -1, 
				new PriceListClick(this));
		return builder.create();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case PRICE_CHANGE_DLG:
			return createPriceChangeDialog();
		default: return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id){
		case PRICE_CHANGE_DLG:
			((BaseAdapter)((AlertDialog)dialog).getListView().getAdapter()).notifyDataSetChanged();
			break;
		}
	}

	/**
	 * Можно использоват HTML
	 * @param p
	 * @return
	 */
	protected void postAppendPrice(StringBuilder sb, Price p) {}

	
//	@Override
//	public View makeView() {
//        ImageView i = new ImageView(this);
//        i.setBackgroundColor(0xFF000000);
//        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        i.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        return i;
//	}
}

class ShowPresentation extends AsyncTask<Object, Void, Boolean>{
	private static String TAG = "ShowPresentattion";
	private String text = "";
	private Bitmap bitmap = null;
	private PricePresentation activity;
	private final int COST_SCALE;
	private final int QTY_SCALE ;
	
	public ShowPresentation(PricePresentation activity){
		this.activity = activity;
		DataObjectInfo dataObjectInfo = DataObjectInfo.getInstance();
		COST_SCALE = dataObjectInfo.getScale(CostItem.class, "cost");
		QTY_SCALE = dataObjectInfo.getScale(Price.class, "qty");
	}
	
	@Override
	protected Boolean doInBackground(Object... params) {
		Log.d(TAG, "doInBackground");
		
		String picSrc = (String)params[0];
		
		if (picSrc == null)
			return false;

		SQLiteDatabase db = DataBaseManager.getDataBase();
		Cursor cursor = db.query("presentation", new String[]{"id"}, 
				"photoPath=?", new String[] {picSrc}, null, null, null);
		
		StringBuilder sb = new StringBuilder();
		PriceImpl priceImpl = new PriceImpl();
		List<String> pIds = new ArrayList<String>();
		
		while(cursor.moveToNext()){
			priceImpl.getData().id = cursor.getString(cursor.getColumnIndex("id"));
			
			if (priceImpl.read()){
				pIds.add(priceImpl.getData().id);
				Price p = priceImpl.getData();
				sb.append(p.name)
				.append(" ").append(getQty(p)).append(" ").append(getCost(p));
				activity.postAppendPrice(sb, p);
				sb.append("<br>");
			}
		}
		
		activity.priceIds = new String[0];
		activity.priceIds = pIds.toArray(activity.priceIds);
		
		text = sb.toString();
		
		priceImpl.close();
		cursor.close();
		
		try{
        	BitmapFactory.Options opt = new BitmapFactory.Options();
        	bitmap = BitmapFactory.decodeFile(picSrc, opt);
        }
        catch (Exception e){
        	e.printStackTrace();
        	return false;
        }
        
		return true;
	}
	
	protected void onPostExecute(Boolean result) {
		if (result){
			try{
				((TextView)activity.findViewById(R.id.tvPriceItems))
					.setText(Html.fromHtml(text));
//				activity.mSwitcher.setImageDrawable(new BitmapDrawable(activity.getResources(), bitmap));
				((ImageView)activity.findViewById(R.id.ivPresent))
					.setImageBitmap(bitmap);
				activity.priceListAdapter.notifyDataSetChanged();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};
	
	private String getCost(Price p){
			int sumType = activity.getDocument().getSumType();
			int cost = (p.cost.size() > sumType && sumType >= 0) ? p.cost.get(sumType).cost : 0;
			return Util.IntToScaleStr(cost, COST_SCALE, Util.DEC_DELIM, false);
	}
	
	private String getQty(Price p){
		int qty = ((Itemsable)activity.getDocument()).getItemValue(p); 
		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNplW)ConfigManager.getConfig()).isPackView )
			qty = (int)((long)qty * Consts.QTY_SCALE / p.qtyInPack);
		
		return Util.IntToScaleStr(qty, QTY_SCALE);
	}
}

class PresentationClick implements OnClickListener{
	private static String TAG = "PresentationClick"; 
	private PricePresentation activity;

	public PresentationClick(PricePresentation activity){
		this.activity = activity;
	}
	
	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick");
		
		if (activity.priceIds.length == 0)
			return;
		
		if (activity.priceIds.length == 1){
			PriceImpl pi =  new PriceImpl();
			pi.getData().id = activity.priceIds[0];
			
			if (pi.read())
				((Itemsable)activity.getDocument())
					.editItem(pi.getRowid(), v.getContext());
			
			pi.close();
		}else if (activity.priceIds.length > 1){
			activity.showDialog(PricePresentation.PRICE_CHANGE_DLG);
		}
		
		Log.d(TAG, Integer.toString(activity.priceIds.length));
	}
}

class PriceListAdapter extends BaseAdapter{
	private static final String TAG = "PriceListAdapter"; 
	private PricePresentation activity;
	
	public PriceListAdapter(PricePresentation activity){
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return activity.priceIds.length;
	}

	@Override
	public Object getItem(int position) {
		return activity.priceIds[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = new TextView((Context) activity);
		
		((TextView) convertView).setTextSize(28);
		String priceId = (String)getItem(position);
		PriceImpl pi = new PriceImpl();
		pi.getData().id = priceId;
		
		if (pi.read()){
			((TextView)convertView).setText(pi.getData().name);
			((TextView)convertView).setTextColor(Color.BLACK);
			pi.close();
		}
		
		Log.d(TAG, ((TextView)convertView).getText().toString());
		return convertView;
	}
}

class PriceListClick implements DialogInterface.OnClickListener{
	private PricePresentation activity;
	private static String TAG = "PriceListClick";
	
	public PriceListClick(PricePresentation activity){
		this.activity = activity;
	}
	
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
			((Itemsable)activity.getDocument())
				.editItem(pi.getRowid(), 
						((AlertDialog)dialog).getContext());
			
		pi.close();
		dialog.dismiss();
		//activity.finish();
	}
}