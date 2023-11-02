package com.grsoft.ads.documents;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.grsoft.ads.OrderTabActivity;
import com.grsoft.ads.R;
import com.grsoft.ads.UserOrderEdit;
import com.grsoft.ads.dataobjects.UserOrder;
import com.grsoft.ads.dataobjects.impl.UserOrderImpl;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.DataBaseAdapter;
import com.grsoft.napoleon.util.LinesCountController;

public class UserOrderDoc extends AdapterListDocType {
	static public final String DOC_NAME = "Заявки пользователя";
	static public final String OBJ_NAME = "UserOrder";
	static public UserOrderDoc instance = null;
	
	static public DocType instance() {
		if( instance == null )
			instance = new UserOrderDoc();
		return instance;
	}
	
	protected UserOrderDoc() {
		super(OBJ_NAME, UserOrderImpl.class);
	}
	
	@Override
	public DataBaseAdapter<? extends DataObject> createAdapter(Context context, 
			LinesCountController linesControlles) {
		UserOrderAdapter result = null;
		try{
			result = new UserOrderAdapter(context, linesControlles);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Override
	public OnItemClickListener getListener() {
		return (OnItemClickListener) adapter;
	}
	
	@Override
	public void deleteItem(int position) {
		((UserOrderAdapter) adapter).deleteItem(position);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument<?> d = (CreatableDocument<?>)create();
		
		String where = "(([params] & " + ParamState.ofExported + " ) = 0)";
		
		DocList docList =  new DocList((Class<? extends CreatableDocument<?>>)d.getClass(), where, null);
		return new DocSendListner(OBJ_NAME, docList);
	}
	
	public Class<?> getSummary(){
		return UserOrderEdit.class;
	}
	
	public String getTitle(){
		return "Работа с заказом";
	}
	
	public int getSummaryIndicator(){
		return R.drawable.user_order;
	}
	
	public String getSummaryTitle(){
		return "Заказ";
	}
	
	public boolean hasAddress(){
		return true;
	}
}

class UserOrderAdapter extends DataBaseAdapter<UserOrder>
implements OnItemClickListener{
	private LinesCountController controller;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
	@SuppressWarnings("unchecked")
	public UserOrderAdapter(Context context, 
			LinesCountController linesController)
			throws IllegalAccessException, InstantiationException {
		super(context,(DbObject<UserOrder>) UserOrderDoc.instance().create(), 
				"", "date");
		this.controller = linesController;
	}

	protected void applyLineController(TextView textView){
		controller.prepareTextView(textView);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if(convertView == null)
			convertView = View.inflate(context, R.layout.ads_list_row, null);
		
		UserOrderImpl userOrderImpl = (UserOrderImpl) getItem(position);
		
		if (userOrderImpl != null){
			TextView tvClmn1 = (TextView)convertView.findViewById(R.id.tvClmn1);
			tvClmn1.setText(userOrderImpl.getData().remark);
			TextView tvClmn2 = (TextView)convertView.findViewById(R.id.tvClmn2);
			tvClmn2.setText(sdf.format(userOrderImpl.getData().date));
			setBackground(convertView, userOrderImpl);
			applyLineController(tvClmn1);
		}
		
		return convertView;
	}

	protected void setBackground(View convertView, UserOrderImpl userOrderImpl) {
		if(userOrderImpl.getData().params != 0)
			convertView
				.setBackgroundResource(R.drawable.list_grey_selector);
		else
			convertView.setBackgroundResource(R.drawable.list_selector);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UserOrderImpl userOrderImpl = (UserOrderImpl) getItem(arg2);
		
		if(userOrderImpl != null)
			OrderTabActivity.open(context, userOrderImpl.getRowid());
	}
	
	public void deleteItem(int position){
		UserOrderImpl userOrderImpl = (UserOrderImpl) getItem(position);
		userOrderImpl.delete();
		notifyDataSetChanged();
	}
}
