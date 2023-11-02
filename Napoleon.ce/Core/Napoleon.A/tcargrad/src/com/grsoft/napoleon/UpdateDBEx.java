package com.grsoft.napoleon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.Accounts;
import com.grsoft.dataobjects.CostTypes;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DlvTypes;
import com.grsoft.dataobjects.FocusedItemsTC;
import com.grsoft.dataobjects.OrderProps;
import com.grsoft.dataobjects.OrgInfo;
import com.grsoft.dataobjects.OrgMatrix;
import com.grsoft.dataobjects.OrgTypeSend;
import com.grsoft.dataobjects.OrgTypes;
import com.grsoft.dataobjects.PayTypes;
import com.grsoft.dataobjects.PriceActions;
import com.grsoft.dataobjects.ShipTypes;
import com.grsoft.dataobjects.WHCost;
import com.grsoft.dataobjects.impl.OrgTypeSendImpl;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.network.ObjectListener;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// вытаскиваем прайс запросом - всегда полный SetQtyFilter(false) - не используется
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
		cbRemains.setVisibility(View.GONE);
	}
	
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();

		ret.add(new RcvNewHitching(OrderProps.class, "OrderProps"));
		ret.add(new RcvNewHitching(OrgTypes.class, "OrgTypes"));
		ret.add(new RcvNewHitching(DlvTypes.class, "DlvTypes"));
		ret.add(new RcvNewHitching(PayTypes.class, "PayTypes"));
		ret.add(new RcvNewHitching(ShipTypes.class, "ShipTypes"));
		ret.add(new ActionHitching());
		ret.add(new RcvNewHitching(FocusedItemsTC.class, "FocusedItems"));
		ret.add(new RcvNewHitching(CostTypes.class, "CostTypes"));
		ret.add(new RcvNewHitching(WHCost.class, "WHCost"));
		ret.add(new RcvNewHitching(Accounts.class, "Accounts"));
		ret.add(new RcvNewHitching(OrgMatrix.class, "OrgMatrix"));
		ret.add(new RcvNewHitching(OrgInfo.class, "OrgInfo"));

		return ret;
	}

	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> ret = super.getExported();
		ret.add(new OrgTypeHitching());
		return ret;
	}
}

class ActionHitching extends HitchOnSelect {
	public ActionHitching() {
		super(PriceActions.class, "PriceActions");
	}

	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
	}
	
	@Override
	protected String getCondition() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		Date d = new Date();
		return "\"end\" is null or \"end\" > ToDate('" + sdf.format(d) + "')";
	}
}

class OrgTypeHitching extends Hitching implements ObjectExportListener {
	private List<Long> list;
	OrgTypeSendImpl impl = new OrgTypeSendImpl();
	
	public OrgTypeHitching() {
		super(OrgTypeSend.class, "OrgNewType");
	
		list = new ArrayList<Long>();
		list = DbReader.readIds(DataObjectInfo.getInstance().getTableName(OrgTypeSend.class), "", "");
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public DataObject get(int i) {
		impl.read(list.get(i));
		return impl.getData();
	}
	

	@Override
	public void onEnd() {
		String table = DataObjectInfo.getInstance().getTableName(OrgTypeSend.class);
		String sql = "DELETE FROM \"" + table + "\"";
		DataBaseManager.getDataBase().execSQL(sql);
		
		impl.close();
	}
}
