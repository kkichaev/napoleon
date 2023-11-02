package com.grsoft.napoleon.documents;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

public class DocType extends DocTypeBase{
	
	public interface CountTextResolver {
		String getCountText();
	}
	
	protected DocType(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
	}
	
	protected DocType(String name, String objName, Class<? extends Document<?>> docClass) { 
		super(name, objName, docClass);
	}
	
	public void setView(Adapter adapter, View view, Document<?> doc) {
		int color = getViewTextColor(view.getContext(), doc);
			updTextItem(view, R.id.tvDate, doc.getDate() == null ? view.getContext().getString(R.string.doc_error) : getDateDocText(doc), color, null);
		updTextItem(view, R.id.tvSum, Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false), color, new ViewUpdater() {
			@Override public void update(View v) { v.setVisibility(View.VISIBLE); }});
		updTextItem(view, R.id.tvOther, Html.fromHtml(doc.getDescription(view.getContext())), color, null);		
	}

	public int getViewTextColor(Context context, Document<?> doc) { return Color.BLACK; }
	
	protected void updTextItem(View view, int id, CharSequence text, int txtclr, ViewUpdater vu){
		TextView tv = (TextView) view.findViewById(id);
		
		if(tv != null){
			tv.setText(text);
			tv.setTextColor(txtclr);
			
			if(vu != null)
				vu.update(tv);
		}
	}
	
	protected interface ViewUpdater{ void update(View v); }
	
	public String getDateDocText(Document<?> doc) {
		return Util.simpleDateFormat.format(doc.getDate());
	}
	
	
	public void viewOpened(Activity documentsView) {
		TextView tv = (TextView) documentsView.findViewById(R.id.tvMainDocValColTitle);
		
		if (tv != null){
			tv.setVisibility(View.VISIBLE);
			tv.setText(R.string.sum);
		}
		
		tv = (TextView) documentsView.findViewById(R.id.tvFirstColumnCaption);
		
		if (tv != null)
			tv.setText(R.string.caption);
		
		tv = (TextView) documentsView.findViewById(R.id.SumColumnTitle);
		
		if (tv != null)
			tv.setVisibility(View.VISIBLE);
		
		tv = (TextView) documentsView.findViewById(R.id.NameTitle);
		
		if (tv != null)
			tv.setVisibility(View.GONE);
		
		tv = (TextView) documentsView.findViewById(R.id.DateTitle);
		
		if (tv != null)
			tv.setVisibility(View.VISIBLE);
		
	}
	
	/***
	 * Отображение документа в Napoleon
	 * @param view
	 * @param orgImpl
	 * @param orgSum
	 */
	public void setMainView(View view, LinesCountController linesController,
			OrgImpl orgImpl, OrgSumImpl orgSumImpl){
		Org org = orgImpl.getData();
		setMainView(view, linesController, org, orgSumImpl);
	}
	
	public void setMainView(View view, LinesCountController linesController,
			Org org, OrgSumImpl orgSumImpl){
		OrgSum osd = orgSumImpl.getData();
		
		TextView tvOrgName = (TextView)view.findViewById(R.id.tvOrgName);
		linesController.prepareTextView(tvOrgName);
		
		TextView tvOrgSum = (TextView)view.findViewById(R.id.tvOrgSum);
		tvOrgSum.setVisibility(View.VISIBLE);
		
		if (isHasCreatedToday(org.id))
			tvOrgName.setTextColor(view.getResources().getColor(R.color.item_highlight));
		else
			tvOrgName.setTextColor(Util.GrServerColorToSystem(org.color));
		
		String str = "<b>" + org.name + "</b><br>" + org.address;
		tvOrgName.setText(Html.fromHtml(str));
//		tvOrgName.setText(org.name);
		
		osd.id = org.id;
		osd.type = getName();
		
		tvOrgSum.setText(Html.fromHtml(getValueFromOrgSum(orgSumImpl)));
		tvOrgSum.setTextColor(view.getContext().getResources().getColor(R.color.black));
	}
	
	public void updateTotalSum(Activity activity, long sum, int weight, int count){
		updateTotalSum(activity, sum, weight, count, R.id.tvTotalSum);
	}
	
	public static DocType getCurDoc() {
		return (DocType) DocTypeBase.getCurDoc(); 
	}
	
	/**
	 * Обновить сумму в таблице документов для организации
	 * @param orgId
	 * @throws RuntimeException
	 */
	public void refreshDocSum(String orgId){
		try{
			DbWriter.checkDBTable(OrgSum.class);
			long sum = 0;
			DocList list = docList(orgId, null);
			for( int i=0; i<list.getCount(); i++ )
			{
				Document<?> d = list.get(i);
				if( d != null ) sum += d.sum();
			}
			list.close();
			
			OrgSum os = new OrgSum();
			os.id = orgId;
			os.sum = sum;
			os.type = this.name;
			
			DbWriter w = new DbWriter();
			w.insertRecord(os);
			w.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void writeSumMap(Map<String, Long> sums) {
		writeSumMap(sums, true);
	}
	protected void writeSumMap(Map<String, Long> sums, boolean clearSums) {
		OrgSum os = new OrgSum();
		
		if(clearSums)
			DataBaseManager.getDataBase().execSQL(String.format("DELETE FROM '%s' WHERE type='%s'", 
					DataObjectInfo.getInstance().getTableName(os.getClass()), name));
			
		DbWriter w = new DbWriter();
		os.type = this.name;
		for( Entry<String, Long> v : sums.entrySet() ) {
			os.id = v.getKey();
			os.sum = v.getValue();
			w.insertRecord(os);
		}
		w.close();
	}
	
	/**
	 * Обновить сумму в таблице документов для всех организаций
	 * @throws RuntimeException
	 */
	public void refreshDocSum() throws RuntimeException {
		DbWriter.checkDBTable(OrgSum.class);
		Map<String, Long> sums = new HashMap<String, Long>();
		DocList list = docList(null, null);
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			String id = d.getId();
			long sum = d.sum();
			if( sums.containsKey(id))
				sum += sums.get(id);
			
			sums.put(id, sum);
		}
		list.close();
		
		writeSumMap(sums);
	}
	
	/**
	 * Удаляет документы + пересчитывает сумму документов
	 * @param tillDate
	 * @return
	 */
	public boolean removeTill(Date tillDate) {
		if( isCreatable() ) {
			Document<?> d = create();
			String table = DataObjectInfo.getInstance().getTableName(d.getData().getClass());
			if( DocDeleteHelper.deleteTill(tillDate, table, "date")) {
				try {
					refreshDocSum();
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
				return true;
			}
			return false;
		}
		return false;
	}
	
	public long getSum(OrgSumImpl orgSumImpl){
		long sum = 0;
		
		if(orgSumImpl != null){
			if(OrgSumImpl.periodSum != null && OrgSumImpl.periodSum.containsKey(orgSumImpl.getData().id))
				sum = OrgSumImpl.periodSum.get(orgSumImpl.getData().id); 
			else if (OrgSumImpl.periodSum == null && orgSumImpl.read()){
				sum = orgSumImpl.getData().sum;
				orgSumImpl.close();
			}
		}
		
		return sum;
	}
	
	protected String getValueFromOrgSum(OrgSumImpl orgSumImpl){
		return Util.IntToScaleStr(getSum(orgSumImpl), Consts.SUM_SCALE, Util.DEC_DELIM, false);
	}
	
	public String weightToString(long weight, String kgStr) {
		String str = "";
		int scale = Features.WEIGHT_SCALE;
		if( scale == 0 ) // округляю до килограмм
			str += Long.toString((weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE) + "" + kgStr;
		else {
			// переводим масштаб
			if( scale != Consts.WEIGHT_SCALE )
				weight = (int)(((long)weight * scale + scale/2)/ Consts.WEIGHT_SCALE);
			str += Util.IntToScaleStr(weight, scale, Util.DEC_DELIM, false) + " " + kgStr;
		}
		
		return str;
	}
	
	public static TotalSumConvertor SumConverter = new TotalSumConvertor();
	
	public String getCountText(Activity activity) {
		if( activity instanceof CountTextResolver )
			return ((CountTextResolver)activity).getCountText();
		return activity.getString(R.string.sht);
	}
	
	public void updateTotalSum(Activity activity, long sum, int weight, int count, int textViewId){
		TextView tvTotalSum = (TextView) activity.findViewById(textViewId);		
		if (tvTotalSum != null)
		{
			tvTotalSum.setVisibility(View.VISIBLE);
			String s = getTotalSumStr(activity, sum, weight, count);			
			tvTotalSum.setText(Html.fromHtml(s));
		}
	}

	public String getTotalSumStr(Activity activity, long sum, int weight, int count) {
		StringBuilder sb = new StringBuilder();
		
		if( weight != 0 || count != 0 ) {
			sb.append("<i>");
			
			if( count != 0 )
				sb.append(Integer.toString(count));
				sb.append(" ");
				sb.append(getCountText(activity));
			if( weight != 0 ) {
				if( sb.length() > 0 ) 
					sb.append(", ");
				
				sb.append(weightToString(weight, activity.getString(R.string.kg)));
			}
			
			sb.append("</i><br>");				
		}
		
		sb.append("<b>");
		sb.append(SumConverter.toString(sum));
		sb.append("</b>");
		
		return sb.toString();
	}
	
	/***
	 * Картинка для документа
	 * @return
	 */
	public int getResurceId() { return R.drawable.icon; };
	
	/***
	 * Картинка 2 для документа
	 * @return
	 */
	public int getResurce2Id() { return getResurceId(); };
}
