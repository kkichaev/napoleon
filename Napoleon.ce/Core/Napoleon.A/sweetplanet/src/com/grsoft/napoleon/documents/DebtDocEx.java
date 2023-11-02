package com.grsoft.napoleon.documents;

import java.util.Date;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class DebtDocEx extends DebtDoc {
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}

	
	@Override
	public void setMainView(View view, LinesCountController linesController, Org org, OrgSumImpl orgSumImpl) {
		OrgSum osd = orgSumImpl.getData();
		
		TextView tvOrgName = (TextView)view.findViewById(R.id.tvOrgName);
		linesController.prepareTextView(tvOrgName);
		
		
		
		if (isHasCreatedToday(org.id))
			tvOrgName.setTextColor(Color.GREEN);
		else
			tvOrgName.setTextColor(Util.GrServerColorToSystem(org.color));
		
		String str = "<b>" + org.name + "</b><br>" + org.address;
		tvOrgName.setText(Html.fromHtml(str));
		
		osd.id = org.id;
		osd.type = getName();
		
		DocList list = docList(org.id, "date", "");
		
		int sum = 0;
		int debt = 0;
		
		if (list != null) {
			for(Document<?> d : list) {
				if (d instanceof DeliveryImpl) {
					DeliveryImpl dlv = (DeliveryImpl)d;
					
					sum += dlv.sum();
					
					if (dlv.getData().payDate.compareTo(new Date()) < 0)
						debt += dlv.sum();
				}
			}
		}
		
		TextView tv = (TextView)view.findViewById(R.id.tvDebtSum);
		tv.setVisibility(View.VISIBLE);
		tv.setText(debt != 0 ? Util.IntToScaleStr(debt, Consts.SUM_SCALE) : "");
		tv.setTextColor(view.getContext().getResources().getColor(R.color.red));
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		tv.setText(sum != 0 ? Util.IntToScaleStr(sum, Consts.SUM_SCALE) : "");
		tv.setTextColor(view.getContext().getResources().getColor(R.color.black));
	}
}
