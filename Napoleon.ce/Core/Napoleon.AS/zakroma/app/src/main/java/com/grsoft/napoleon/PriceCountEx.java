package com.grsoft.napoleon;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.util.Util;

public class PriceCountEx extends PriceCount {
	protected static final int SELECT_COUNT_DLG = 0;
	protected static final int EXPR_HAS_CHANGED_DLG = 1;
	protected static final int PACKET_HAS_CHANGED_DLG = 2;
	private static final String NEW_ORDER_ITEM_ACTION = "com.grsoft.napoleon.PriceCountEx.neworderitemaction";
	private static final String QTY_COUNT = "qty_count";
	private Spinner spCount;
	private ListView lvPrice;
	private LinearLayout tableLayout;
	private LinearLayout layoutPrice;
	private List<OrderItemEx> items = new ArrayList<OrderItemEx>(); 
	private String selectedPriceId = "";
	private int partid;
	private Expression prevExpr = null;
	private Button btnOK2;
	private TextView tvQuant;
	private int startQty = 0;
	
	BroadcastReceiver newOrderItemRcv = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			int qty = intent.getIntExtra(QTY_COUNT, 0);
			OrderItemEx item = new OrderItemEx();
			item.qty = qty * Consts.QTY_SCALE;
			item.id = selectedPriceId;
			items.add(item);
			lvPrice.invalidateViews();
			dismissDialog(SELECT_COUNT_DLG);
		}
	};
	
	private PacketCliskListener packetCliskListener;
	
	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(newOrderItemRcv, new IntentFilter(NEW_ORDER_ITEM_ACTION));
	};
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(newOrderItemRcv);
		
		if(lvPrice != null){
			Adapter a = lvPrice.getAdapter();
			if(a != null){
				((PriceAdapter)a).close();
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvQuant = (TextView) findViewById(R.id.tvQuant);
		btnOK2 = (Button)findViewById(R.id.btnOK2);
		spCount = (Spinner) findViewById(R.id.spCount);
		spCount.setVisibility(View.GONE);
		spCount.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				try{
					Expression e = (Expression) parent.getSelectedItem();
					
					if(prevExpr == null)
						prevExpr = e;
					
					if(!prevExpr.toString().equals(e.toString()) 
							&& items.size() > 0)
						showDialog(EXPR_HAS_CHANGED_DLG);
					else
						prevExpr = e;
					
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		lvPrice = (ListView) findViewById(R.id.lvPrice);
//		lvPrice.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				if(!document.isExported()){
//					PriceImpl priceImpl = (PriceImpl) parent.getAdapter().getItem(position);
//					if(priceImpl != null){
//						String pid = priceImpl.getData().id;
//						OrderItem orderItem = getOrderItem(pid);
//						if(orderItem == null){
//							OrderItemEx item = new OrderItemEx();
//							Expression expr = (Expression) spCount.getSelectedItem();
//							
//							if(items.size() + 1 < expr.part){
//								int iqty = 0;
//								for(OrderItemEx oi : items)
//									iqty += oi.qty;
//								
//								int rest = price.getData().qtyInPack - expr.qty - iqty;
//								
//								if((rest / Consts.QTY_SCALE) % (expr.part - (items.size() + 1)) > 0){
//									selectedPriceId = pid;
//									showDialog(SELECT_COUNT_DLG);
//								}else{
//									item.qty = rest / (expr.part - (items.size() + 1));
//									item.id = pid;
//									items.add(item);
//									lvPrice.invalidateViews();
//								}
//							}else
//								Toast.makeText(view.getContext(), 
//										"Вы не можете выбрать больше в данной партии, снимете позиции", 
//										Toast.LENGTH_SHORT).show();
//						}else{
//							items.remove(orderItem);
//							
//							if(((OrderImpl)document).findItem(pid) != null)
//								((OrderImpl)document).updateQty(priceImpl, 0, 0, false);
//							lvPrice.invalidateViews();
//						}
//					}
//				}
//			}
//		});
		
		tableLayout = (LinearLayout) findViewById(R.id.linearLayout2);
		layoutPrice = (LinearLayout) findViewById(R.id.layoutPrice);
		layoutPrice.setVisibility(View.GONE);
		
		btnOK2.setOnClickListener(new BtnOKClickListenet());
		
//		if( document != null && document instanceof Itemsable){
//			if(!cbPackets.isChecked()) {
//				Price p = price.getData();
//				OrderItemEx item = (OrderItemEx) getDocItem(p);
//				
//				if (item != null) {
//					partid = item.partid;
//					
//					for(OrderItem i : ((OrderImpl)document).getData().items){
//						if(((OrderItemEx)i).partid == partid && 
//								!i.id.equals(p.id))
//							items.add((OrderItemEx)i);
//					}
//					
//					packetCliskListener.onCheckedChanged(cbPackets, false);
//					int qty = item.getQty();
//					CountAdapter adapter = (CountAdapter) spCount.getAdapter();
//					
//					if(adapter != null)
//						for(int i = 0; i < adapter.getCount(); i ++){
//							Expression exp = (Expression) adapter.getItem(i);
//							if(exp.qty == qty){
//								spCount.setSelection(i,true);
//								break;
//							}
//						}
//				}
//			} else if(((OrderImpl)document).getData().items != null){
//				for(OrderItem i : ((OrderImpl)document).getData().items)
//					if(((OrderItemEx)i).partid > partid)
//						partid = ((OrderItemEx)i).partid;
//				
//				partid += 1;	
//			}
//		}	
		
		tvQuant.setText(Util.IntToScaleStr(((PriceEx)price.getData()).quant, Consts.QTY_SCALE));
		startQty = qtyItems;
	}
	
	@Override
	protected void refreshData() {
		super.refreshData();
		PriceEx pe = (PriceEx)price.getData();
		((CfgNpl)ConfigManager.getConfig()).checkPrice = (pe.canMinus == 0);
	}
	
	@Override
	protected boolean isInputValid(Runnable r) {
		boolean result = true;
		Expression exp = (Expression)spCount.getSelectedItem();
		
//		if(!cbPackets.isChecked() && exp != null){
//			if(items.size() < exp.part - 1){
//		
//			result = false;
//			Toast.makeText(this, 
//					"Количество товара должно быть равно количеству в партии", 
//					Toast.LENGTH_SHORT).show();
//			}else{
//				PriceEx pe = (PriceEx)price.getData();
//				if(pe.canMinus == 0) {
//					if(pe.qty < exp.qty){
//						notInPriceToast(price);
//						result = false;
//					}else{
//						PriceImpl p = new PriceImpl();
//						
//						for(OrderItem item : items){
//							p.getData().id = item.id;
//							
//							if(p.read()){
//								if(p.getData().qty < item.qty){
//									result = false;
//									notInPriceToast(p);
//									break;
//								}
//							}
//						}
//						
//						p.close();
//					}
//				}
//			}
//		}else{
//			if( edCount != null ) {
//				Editable txt = edCount.getText();
//				if( txt != null && txt.length() != 0 ){
//					int qty = Util.StrToScale(txt.toString(), Consts.QTY_SCALE);
//					
//					if((qty % Consts.QTY_SCALE) > 0){
//						result = false;
//						Toast.makeText(this, 
//								"Количество упаковками должно быть целым числом", 
//								Toast.LENGTH_SHORT).show();
//					}else
//						result = super.isInputValid(r);
//				}
//			}
//		}
		
		int qty = qtyItems;

		qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
		int quant = ((PriceEx)price.getData()).quant;// * Consts.QTY_SCALE;
		if(quant != 0 && (qty % quant != 0)) {
			Toast.makeText(this, "Заказ не соответствует кратности «число к отгрузке! Заказ должен быть кратен " + Util.IntToScaleStr(quant, Consts.QTY_SCALE), Toast.LENGTH_SHORT).show();
			edCount.setText(Util.IntToScaleStr((int) startQty, Consts.QTY_SCALE));
			edCount.selectAll();
			return false;
		}

		
		return result;
	}

//	public void notInPriceToast(PriceImpl p) {
//		Toast.makeText(this, 
//				"На складе нет товара: " + p.getData().name + " для формирования партии", 
//				Toast.LENGTH_SHORT).show();
//	}
	
//	@Override
//	protected boolean updateQty(boolean inPack, int qty) {
//		boolean result = false;
//		Expression expr = (Expression)spCount.getSelectedItem();
//		
//		if(cbPackets.isChecked() || expr == null)
//			result = super.updateQty(inPack, qty);
//		else{
//			result = super.updateQty(inPack, expr.qty);
//			PriceImpl p = new PriceImpl();
//			
//			for(OrderItem item : items){
//				p.getData().id = item.id;
//				
//				if(p.read()){
//					boolean r = !((Itemsable)document).updateQty(p, 
//							item.qty, getInputCost(p.getData()), inPack);
//					
//					if(r)
//						result = r;
//					
//					OrderItemEx e = (OrderItemEx)((Itemsable)document).findItem(item.id);
//					e.partid = partid;
//				}
//				
//				p.close();
//			}
//			
//			OrderItemEx e = (OrderItemEx)((Itemsable)document).findItem(price.getData().id);
//			e.partid = partid;
//			
//			document.write();
//		}
//		return result;
//	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id){
			case SELECT_COUNT_DLG: return createSelectCountDlg();
			case EXPR_HAS_CHANGED_DLG: return createExprChangedDlg();
			case PACKET_HAS_CHANGED_DLG: return createPacketChangedDlg();
			default: return super.onCreateDialog(id);
		}
	}
	
	private Dialog createPacketChangedDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.ask_for_packets_changed, null);
		builder.setView(view);
		builder.setTitle("Внимание");
		
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PriceImpl priceImpl = new PriceImpl();
				
				for(OrderItem i: items){
					priceImpl.getData().id = i.id;
					
					if(priceImpl.read() &&
							((OrderImpl)document).findItem(i.id) != null)
						((OrderImpl)document).updateQty(priceImpl, 0, 0, false);
				}
				
				priceImpl.close();
				items.clear();
				dismissDialog(PACKET_HAS_CHANGED_DLG);
			}
		});
		
		view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(PACKET_HAS_CHANGED_DLG);
				cbPackets.setChecked(false);
			}
		});
		
		return builder.create();
	}

	private Dialog createExprChangedDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.ask_for_expr_changed, null);
		builder.setView(view);
		builder.setTitle("Внимание");
		
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Expression e = (Expression)spCount.getSelectedItem();
				prevExpr = e;
				PriceImpl priceImpl = new PriceImpl();
				
				for(OrderItem i: items){
					priceImpl.getData().id = i.id;
					
					if(priceImpl.read() &&
							((OrderImpl)document).findItem(i.id) != null)
						((OrderImpl)document).updateQty(priceImpl, 0, 0, false);
				}
				
				priceImpl.close();
				items.clear();
				setPriceAdapter(v.getContext(), e.qty);
				dismissDialog(EXPR_HAS_CHANGED_DLG);
			}
		});
		
		view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismissDialog(EXPR_HAS_CHANGED_DLG);
				int pos = -1;
				Adapter a = spCount.getAdapter();
				for(int i = 0; i < a.getCount(); i++)
					if(((Expression)a.getItem(i)).toString().equals(prevExpr.toString())){
						pos = i;
						break;
					}
						
				if(pos != -1)
					spCount.setSelection(pos, true);
			}
		});
		
		return builder.create();
	}

	private Dialog createSelectCountDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.select_count, null);
		builder.setView(view);
		final Spinner sp = (Spinner)view.findViewById(R.id.spCount);
		
		view.findViewById(R.id.btnOK).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(NEW_ORDER_ITEM_ACTION);
				intent.putExtra(QTY_COUNT, (Integer)sp.getSelectedItem());
				sendBroadcast(intent);
			}
		});
		
		builder.setTitle("Выберите количество");
		return builder.create();
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == SELECT_COUNT_DLG){
			Spinner sp = (Spinner) ((AlertDialog) dialog).findViewById(R.id.spCount);
			Expression exp = (Expression) spCount.getSelectedItem();
			int qty = exp.qty / Consts.QTY_SCALE;
			sp.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, 
					android.R.id.text1, new Integer[]{qty, qty + 1}));
		}else if(id == EXPR_HAS_CHANGED_DLG){
			
		}else
			super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.pricecountex;
	}
	
	@Override
	protected OnCheckedChangeListener createPacketChangeListener() {
		packetCliskListener  = new PacketCliskListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				super.onCheckedChanged(buttonView, isChecked);
				
				if(isChecked){
					llKeyboard.setVisibility(View.VISIBLE);
					
					if(spCount != null)
						spCount.setVisibility(View.GONE);
					
					if(edCount != null)
						edCount.setVisibility(View.VISIBLE);
					
					if(layoutPrice != null)
						layoutPrice.setVisibility(View.GONE);
					
					if(tableLayout != null){
						LinearLayout.LayoutParams params = (LayoutParams) tableLayout.getLayoutParams();
						params.weight = 1.0f;
					}
					
					if(items.size() > 0 && !document.isExported())
						showDialog(PACKET_HAS_CHANGED_DLG);
				}else{
					PriceEx p = (PriceEx) price.getData();
					boolean canPart = true; //p.canMinus == 0;
					
//					if( canPart ) {
//						if(p.partExpr.length() > 0 && p.partExpr.contains("*")){
//							llKeyboard.setVisibility(View.VISIBLE);
//							
//							if(spCount != null)
//								spCount.setVisibility(View.GONE);
//							
//							if(edCount != null)
//								edCount.setVisibility(View.VISIBLE);
//							
//							if(layoutPrice != null)
//								layoutPrice.setVisibility(View.GONE);
//							
//							if(tableLayout != null){
//								LinearLayout.LayoutParams params = (LayoutParams) tableLayout.getLayoutParams();
//								params.weight = 1.0f;
//							}
//						} else if(p.partExpr.length() > 0 && !p.partExpr.contains("1=1")){
//							if(llKeyboard != null)
//								llKeyboard.setVisibility(View.GONE);
//							
//							StringReader stream = new StringReader(p.partExpr);
//							
//							PartExpression partExpr = new PartExpression(p.qtyInPack);
//							
//							if(partExpr.read(stream)){
//								if(tableLayout != null){
//									LinearLayout.LayoutParams params = (LayoutParams) tableLayout.getLayoutParams();
//									params.weight = 0.0f;
//								}
//								
//								if(edCount != null)
//									edCount.setVisibility(View.GONE);
//								
//								if(spCount != null){
//									spCount.setVisibility(View.VISIBLE);
//									if(spCount.getAdapter() == null)
//										spCount.setAdapter(
//												new CountAdapter(buttonView.getContext(), partExpr));
//								}
//								
//								if(lvPrice != null){
//									layoutPrice.setVisibility(View.VISIBLE);
//									setPriceAdapter(buttonView.getContext(), partExpr.qty);
//								}
//							}else{
//								partNoDelim(buttonView, "Ошибка в выражении: " + p.partExpr);
//							}
//						} else
//							canPart = false;
//					}
					
					if(!document.isExported() && !canPart)
						partNoDelim(buttonView, "Данная позиция не может быть разделена");
				}
			}

			protected void partNoDelim(CompoundButton buttonView, String msg) {
				buttonView.setChecked(true);
				super.onCheckedChanged(buttonView, true);
				Toast.makeText(buttonView.getContext(), 
						msg, 
						Toast.LENGTH_LONG).show();
			}
		};
		
		return packetCliskListener;
	}
	
	protected OrderItem getOrderItem(String id) {
		OrderItem orderItem = null;
		
		for(OrderItem oi: items)
			if(oi.id.equals(id)){
				orderItem = oi;
				break;
			}
		return orderItem;
	}
	
	@Override
	protected void switchKeyboardVisible() {
		if(cbPackets.isChecked())
			super.switchKeyboardVisible();
	}
	
	private String getItemsIds(){
		StringBuilder result = new StringBuilder();
		
		if(items.size() > 0){
			result.append(" or id in(");
			for (OrderItemEx o : items) 
				result.append("'").append(o.id).append("'").append(",");
			result.deleteCharAt(result.length()-1);
			result.append(")");
		}
			
		return result.toString();
	}
	
	protected void setPriceAdapter(Context context, int qty) {
		try{
			PriceEx p = (PriceEx) price.getData();
			String where = "(folderID="+p.folderID;
			if( p.canMinus == 0)
				where += " and qty >=" + Integer.toString(qty);
			where += " and rowid<>"+Long.toString(price.getRowid()) + " and qtyInPack="+Integer.toString(p.qtyInPack) + 
					" and partExpr<>'' and partExpr not LIKE '%1=1%' and partExpr not LIKE '%*%')" + 
					getItemsIds();
			
			PriceAdapter adapter = new PriceAdapter(context, where);
			lvPrice.setAdapter(adapter);
			lvPrice.invalidateViews();
			btnOK2.setVisibility(adapter.getCount() > 0 && !document.isExported()
					? View.VISIBLE : View.GONE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	class PriceAdapter extends DataBaseAdapter<Price> {
		public PriceAdapter(Context context, String where) throws IllegalAccessException, InstantiationException {
			super(context, new PriceImpl(), where, "srchName");
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(context, R.layout.price_list_row, null);
			
			PriceImpl priceImpl = (PriceImpl) getItem(position);
			
			if (priceImpl != null){
				Price p = priceImpl.getData();
				
				OrderItem orderItem = getOrderItem(p.id);
				
				TextView tvClmn1 = (TextView) convertView.findViewById(R.id.tvClmn1);
				
				if(tvClmn1 != null){
					tvClmn1.setText(p.name);
					tvClmn1.setTextColor(getTextColor(orderItem));
				}
				
				TextView tvClmn2 = (TextView) convertView.findViewById(R.id.tvClmn2);
				
				if(tvClmn1 != null){
					String text = Util.IntToScaleStr(p.qty, Consts.QTY_SCALE);
					
					if(orderItem != null)
						text = text + "<br><font color='red'>" + 
								Util.IntToScaleStr(orderItem.qty, Consts.QTY_SCALE) + "</font>";
					
					tvClmn2.setText(Html.fromHtml(text));
					tvClmn2.setTextColor(getTextColor(orderItem));
				}
			}
			
			convertView.setBackgroundResource(position % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector);
			
			return convertView;
		}

		protected int getTextColor(OrderItem orderItem) {
			return orderItem == null ? Color.BLACK : Color.GREEN;
		}
	}
}

class PartExpression {
	List<Expression> expr = new ArrayList<Expression>();
	int qty;
	
	public PartExpression(int qty){
		this.qty = qty;
	}
	
	public boolean read(StringReader stream){
		boolean result = false;
		
		try{
			Expression e = null;
			
			while((e = Expression.read(stream)) != null)
				if(!e.hasQty || (e.hasQty && e.limit * Consts.QTY_SCALE == qty)){
					int part = (qty / Consts.QTY_SCALE) / e.part;
					e.qty = part * Consts.QTY_SCALE;
					expr.add(e);
					
					if(((qty / Consts.QTY_SCALE) % e.part) > 0){
						Expression e2 = e.makeClone();
						
						if(e2 != null){
							e2.qty = (part + 1)* Consts.QTY_SCALE;
							expr.add(e2);
						}
					}
				}
			
			if(expr.size() > 0)
				Collections.sort(expr, new Comparator<Expression>() {

					@Override
					public int compare(Expression lhs, Expression rhs) {
						return lhs.qty - rhs.qty;
					}
				});
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int getCount(){
		return expr.size();
	}
	
	public Expression get(int index){
		return expr.get(index); 
	}
}

class Expression implements Cloneable {
	static final char END_OF_EXPRESSION = ',';
	static final char QTY_PACK = ':';
	static final char PART_DIV = '/';
	static final char SPACE = ' ';
	
	public int part = 0;
	public int limit = 0;
	public boolean hasQty = false;
	public int qty = 0;
	
	public static Expression read(StringReader stream) 
			throws NumberFormatException, IOException{
		
		Expression result = null;
		int c = -1;
		StringBuilder term = new StringBuilder();
		
		while ((c = stream.read()) != -1 && 
				c != END_OF_EXPRESSION){
			
			if(result == null)
				result = new Expression();
			
			if(c == QTY_PACK){
				result.limit = Integer.parseInt(term.toString());
				result.hasQty = true;
				term.setLength(0);
				
				while(((c = stream.read()) != -1) &&
						c != PART_DIV);
				
				while(((c = stream.read()) != -1) && 
						Character.isDigit(c))
					term.append((char)c);
				
				result.part = Integer.parseInt(term.toString());
			} else if(c == PART_DIV) {
				term.setLength(0);
				
				while(((c = stream.read()) != -1) &&
						c != QTY_PACK && 
						c != END_OF_EXPRESSION && 
						c != SPACE)
					term.append((char)c);
				
				result.part = Integer.parseInt(term.toString());
			} else 
				term.append((char)c);
			
			if(c == END_OF_EXPRESSION)
				break;
		}
		
		return result;
	}
	
	public Expression makeClone(){
		Expression result = null;
		
		try {
			result = (Expression) clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public String toString() {
		return String.format("%s(1/%d)", 
				Util.IntToScaleStr(qty, Consts.QTY_SCALE), part);
	}
}

class CountAdapter implements SpinnerAdapter{
	PartExpression expr;
	Context context;
	
	public CountAdapter(Context context, PartExpression expr){
		this.expr = expr;
		this.context = context;
	}
	
	@Override
	public void registerDataSetObserver(DataSetObserver observer) {}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {}

	@Override
	public int getCount() {
		return expr.getCount();
	}

	@Override
	public Object getItem(int position) {
		return expr.get(position);
	}

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public boolean hasStableIds() { return false; }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getDropDownView(position, convertView, parent);
	}

	@Override
	public int getItemViewType(int position) { return 0; }

	@Override
	public int getViewTypeCount() { return 0; }

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.simple_spinner_layout, null);
		
		Expression expr = ((Expression) getItem(position));
		
		if(expr != null){
			TextView tvFirmaName = (TextView) convertView.findViewById(R.id.tvFirmaName);
			tvFirmaName.setText(expr.toString());
		}
			
		return convertView;
	}
}



