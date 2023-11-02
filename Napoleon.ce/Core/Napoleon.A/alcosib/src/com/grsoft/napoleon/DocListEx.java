package com.grsoft.napoleon;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DatePeriod;
import com.grsoft.util.Util;

public class DocListEx extends DocList {
	private TextView tvDocSum;
	private TableLayout tlItog;

	@Override
	protected int getViewID() {
		return R.layout.doclistex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	protected DocListAdapter createListAdapter(DocType docType){
		return new DocListAdapterEx(this, docType, saveDatePeriod);
	}
	
	@Override
	protected void refreshTotalSum(boolean useFilter) {
		if(tvDocSum == null)
			tvDocSum = (TextView) findViewById(R.id.tvDocSum);
		
		if(tlItog == null)
			tlItog = (TableLayout) findViewById(R.id.tlItog);
		
		if (DocType.getCurDoc().getClass() == OrderDoc.class) {
			tvDocSum.setVisibility(View.GONE);
			tlItog.setVisibility(View.VISIBLE);
			
			int sum = 0;
			int cubature = 0;
			int count = 0;
			int weight = 0;

			for (int i = 0; i < adapter.getCount(); i++) {
				Document<?> d = (Document<?>) adapter.getItem(i);
				sum += getDocSum(d);

				if (d instanceof OrderImplEx) {
					cubature += ((OrderImplEx)d).cubature();
					count += ((OrderImplEx)d).count();
					weight += ((OrderImplEx)d).weight();
				}
			}

			if (count > 0 || cubature > 0 || weight > 0) {
				((TextView)findViewById(R.id.tvSumSum)).setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE,
						Util.DEC_DELIM, false));
				((TextView)findViewById(R.id.tvQtySum)).setText(Integer.toString(count) + getString(R.string.sht));
				((TextView)findViewById(R.id.tvCubSum)).setText(Util.IntToScaleStr(cubature, 100000,
						Util.DEC_DELIM, false));
				((TextView)findViewById(R.id.tvWeightSum)).setText(Integer.toString(
						(weight + Consts.WEIGHT_SCALE/2) / Consts.WEIGHT_SCALE) + getString(R.string.kg));
			}
		} else {
			tvDocSum.setVisibility(View.VISIBLE);
			tlItog.setVisibility(View.GONE);
			super.refreshTotalSum(useFilter);
		}
	}
	
	class DocListAdapterEx extends DocListAdapter{

		public DocListAdapterEx(Context context, DocType docType,
				DatePeriod filter) {
			super(context, docType, filter);
			viewId = R.layout.docs_list_row2ex;
		}
		
		@Override
		protected void setData(View view, Document<?> doc, int position) {
			
			if( doc != null ) {
				Org o = org.getData();
				o.id = doc.getId();
				org.read();
				TextView tvCub = (TextView)view.findViewById(R.id.tvCub);
				tvCub.setVisibility(View.GONE);
				
				ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
				ivStatus.setImageResource(getDocStatusResource((CreatableDocument<?>)doc));
				
				if (!Features.CANT_CHANGE_SEND_FLAG && doc instanceof CreatableDocument<?>){
					ivStatus.setOnClickListener(sendStatusClickListener);
					ivStatus.setTag(position);
				}
				
				TextView tvName = (TextView) view.findViewById(R.id.tvName);
				tvName.setText(o.name);
				
				TextView tvDate = (TextView)view.findViewById(R.id.tvDate);
				tvDate.setText(Util.simpleDateFormat.format(doc.getDate()));
				
				TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
				int costScale = DataObjectInfo.getInstance().getScale(OrderItem.class, "cost");
				tvSum.setText(Util.IntToScaleWStr(getDocSum(doc), costScale, 2, false));
				
				if(doc instanceof OrderImplEx){
					tvCub.setVisibility(View.VISIBLE);
					tvCub.setText(Util.IntToScaleStr(((OrderImplEx)doc).cubature(), 100000,
							Util.DEC_DELIM, false));
				}
			}
		}
		
	}
}
