package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.DlvHighlight;
import com.grsoft.dataobjects.NoDiscount;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgHighlight;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.network.exception.RuntimeException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new RcvNewHitching(NoDiscount.class, "NoDiscountPrice"));
		return ret;
	}
	
	@Override
	protected UpdateProcess getUpdateProcess() {
		return new UpdateProcess(this){
			@Override
			protected void customSyncProcess() throws RuntimeException {
				super.customSyncProcess();
				
				CheckBox cbDebt = (CheckBox) findViewById(R.id.cbDebt);
				
				if(cbDebt != null && cbDebt.isChecked()){
					SQLiteDatabase db = DataBaseManager.getDataBase();
					DataObjectInfo doi = DataObjectInfo.getInstance();
					
					String orgtable = doi.getTableName(OrgHighlight.class);
					String dlvtable = doi.getTableName(DlvHighlight.class); 
							
					DbWriter.dropTable(orgtable);
					DbWriter.dropTable(dlvtable);
					
					DbWriter.checkDBTable(OrgHighlight.class);
					DbWriter.checkDBTable(DlvHighlight.class);
					
					final SQLiteStatement insOrg = db.compileStatement("insert into " + orgtable + " values(?)");
					final SQLiteStatement insDlv = db.compileStatement("insert into " + dlvtable + " values(?)");
					
					final List<String> orgs = new ArrayList<String>();
					final List<String> dlvs = new ArrayList<String>();

					final OrderImpl order = new OrderImpl();
					
					DataTraveler.travel(Delivery.class, new DataTraveler.Travel<Delivery>() {

						@Override
						public boolean travel(DataTraveler<Delivery> item) {
							
							if(order.read(item.data.created.getTime(), false))
							{
								Map<String, Integer> m = new HashMap<String, Integer>();
								
								for(OrderItem i : order.getData().items)
									if(!m.containsKey(i.id))
										m.put(i.id, i.qty);
								
								boolean equ = true;
								
								for(DeliveryItem i : item.data.items){
									if (!m.containsKey(i.id)){
										equ = false;
										break;
									}
									
									int	qty = m.get(i.id);
									
									if(i.qty != qty){
										equ = false;
										break;
									}
									
									m.remove(i.id);
								}
								
								if(equ)
									equ = m.size() == 0;
								
								if(!equ){
									if(!orgs.contains(item.data.id)){
										orgs.add(item.data.id);
										
										insOrg.clearBindings();
										insOrg.bindString(1, item.data.id);
										insOrg.execute();
									}
									
									if(!dlvs.contains(item.data.number)){
										dlvs.add(item.data.number);
										
										insDlv.clearBindings();
										insDlv.bindLong(1, item.data.created.getTime());
										insDlv.execute();
									}
								}
							}
							
							return true;
						}}, null);
					
					order.close();
					insOrg.close();
					insDlv.close();
				}
			}
		};
	}
}
