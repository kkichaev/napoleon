package com.grsoft.ads.documents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.ads.R;
import com.grsoft.ads.dataobjects.Client;
import com.grsoft.ads.dataobjects.Order;
import com.grsoft.ads.dataobjects.impl.ClientImpl;
import com.grsoft.ads.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Util;

public class OrderDoc extends AdapterListDocType {
	
	static public final String DOC_NAME = "Заявки";
	static public final String OBJ_NAME = "Order";
	static public OrderDoc instance = null;
	static public final String CLIENT_N_A_STR = "заказчик не установлен.";
	
	
	protected OrderDoc() { super(DOC_NAME, OrderImpl.class);}
	
	protected OrderDoc(String name,
			Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new OrderDoc();
		return instance;
	}
	
	@Override
	public DataBaseAdapter<? extends DataObject> createAdapter(Context context,
			LinesCountController countController){
		DataBaseAdapter<? extends DataObject> result = null; 
		
		try{
			result = new OrderTreeAdapter(context, "planbegin ASC");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public OnItemClickListener getListener(){
		return ((OrderTreeAdapter)adapter).new ItemClickListener() ;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		
		String where = "([params] != 0) and " +
				"(([params] & " + ParamState.ofExported + " ) = 0)";
		
		DocList docList =  new DocList((Class<? extends CreatableDocument<?>>)d.getClass(), where, null);
		return new DocSendListner(OBJ_NAME, docList);
	}
}

class OrderTreeAdapter extends DataBaseAdapter<Order>{
	protected Class<? extends OrderImpl> instanceType = OrderImpl.class;
	
	private Map<String, List<Long>> tree = new TreeMap<String, List<Long>>(
			new Comparator<String>() {

				@Override
				public int compare(String lhs, String rhs) {
					return lhs.compareTo(rhs);
				}
			});
	
	private List<Long> selected;
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
	
	public OrderTreeAdapter(Context context, String order)
			throws IllegalAccessException, InstantiationException {
		super(context, new OrderImpl(), "", order);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String today = sdf.format(Util.getDate());
		
		for(int i = 0; i < cursor.getCount(); i++){
			OrderImpl orderImpl  = (OrderImpl) cursor.get(i);
			
			if(orderImpl != null){ 
				Date planbegin = orderImpl.getData().planbegin;
				String str = sdf.format(planbegin);
				
				if (tree.containsKey(str)){
					List<Long> list = tree.get(str);
					list.add(orderImpl.getRowid());
					
				} else {
					List<Long> list = new ArrayList<Long>();
					list.add(orderImpl.getRowid());
					tree.put(str, list);
				}
				
				if (selected == null && str.equals(today))
					selected = tree.get(str);
			}
		}
		
		View view = ((Activity)context).findViewById(R.id.llTop);
		view.setVisibility(selected == null ? View.GONE : View.VISIBLE);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				v.findViewById(R.id.llTop).setVisibility(View.GONE);
				selected = null;
				notifyDataSetChanged();
			}
		});
		
		if (selected != null)
			((TextView)view.findViewById(R.id.tvDate))
				.setText(today.toString());
		
		if (tree.size() == 0){
			Toast.makeText(context, Html.fromHtml("В базе данных нет заявок, для " +
					"синхронизации с сервером выполните:" +
					"<br><u>меню -> синхронизация.</u>"), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public int getCount() {
		if (selected != null)
			return selected.size();
		else
			return tree.size();
	}
	
	@Override
	public Object getItem(int pos) {
		if (selected != null){
			try{
				OrderImpl orderIml = instanceType.newInstance();
				boolean result = orderIml.read(selected.get(pos));
				orderIml.close();
				return result ? orderIml : null;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}else{
			ArrayList<String> list = new ArrayList<String>(tree.keySet());
			return list.get(pos);
		}
	}
	
	protected String clnm1StrGet(ClientImpl client, Order order){
		return client.getHtmlNameAddress(order.number);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = View.inflate(context, R.layout.ads_list_row, null);
		
		convertView.setBackgroundResource(
				position % 2 != 0 ? R.drawable.even_row_selector:  
								R.drawable.list_selector);
		
		Object item = getItem(position);
		String clmn1Str = "";
		String clmn2Str = "";
		
		if (item instanceof OrderImpl){
			ClientImpl clientImpl = new ClientImpl();
			Client client = clientImpl.getData(); 
			client.id = ((OrderImpl)item).getData().client;
			
			Order order = ((OrderImpl)item).getData();
			
			if (clientImpl.read())
				clmn1Str = clnm1StrGet(clientImpl, order);
			else
				clmn1Str = OrderDoc.CLIENT_N_A_STR;
			
			clientImpl.close();
			
			if (order != null){
				StringBuilder sb = new StringBuilder(timeFormat.format(order.planbegin));
				sb.append(" - ").append(timeFormat.format(order.planend));
				clmn2Str = sb.toString();
			
				if (((OrderImpl)item).isDone())
					convertView.setBackgroundResource(R.drawable.list_grey_selector);
				else if (((OrderImpl)item).isDoing())
					convertView.setBackgroundResource(R.drawable.list_yellow_selector);
			}
		}else{ 
			clmn1Str = item.toString();
			clmn2Str = Integer.toString(tree.get(item).size());
		}
		
		
		TextView tvClmn1Str = (TextView) convertView.findViewById(R.id.tvClmn1);
		tvClmn1Str.setText(Html.fromHtml(clmn1Str));
		
		TextView tvClmn2Str = (TextView) convertView.findViewById(R.id.tvClmn2);
		tvClmn2Str.setText(clmn2Str);
		
		return convertView;
	}
	
	class ItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Object item = getItem(position);
			
			if (item instanceof OrderImpl)
				((OrderImpl)item).open(context);
			else{
				selected = tree.get(item);
				View view = ((Activity)context).findViewById(R.id.llTop);
				view.setVisibility(View.VISIBLE);
				((TextView)view.findViewById(R.id.tvDate)).setText(item.toString());
				notifyDataSetChanged();
			}
		}
	}
}