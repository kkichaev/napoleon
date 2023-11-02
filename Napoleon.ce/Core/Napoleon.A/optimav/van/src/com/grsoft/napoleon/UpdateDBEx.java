package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.widget.CheckBox;

import com.grsoft.database.DbWriter;
import com.grsoft.database.HandledDocumentsHitching;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.database.PODelHitching;
import com.grsoft.database.PotenzialOrgRcv;
import com.grsoft.database.PriceHitching;
import com.grsoft.database.PriceHitchingW;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.database.TaskHitching;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FocusedGroup;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.HandledDocuments;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.Movement;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDiscount;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.Pa;
import com.grsoft.dataobjects.Payment;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.Question;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.Task;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.FocusedGroupImpl;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.impl.ScriptDefImpl;

public class UpdateDBEx extends UpdateDBPrint {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> result = new ArrayList<Hitching>();
		
		if(((CheckBox)findViewById(R.id.cbGenData)).isChecked()) {		
			result.add(new OrgHitchingEx());
			result.add(new PotenzialOrgRcv());

			CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
			
			boolean loadFullPrice = cbRemains.isChecked();
			Hitching ph = getPriceHitching(loadFullPrice);
			if( ph instanceof PriceHitchingW)
				((PriceHitchingW)ph).setPriceFilter(loadFullPrice);
			result.add( ph );

			result.add(new Hitching(com.grsoft.dataobjects.Config.class, "Config"));
			result.add(new Hitching(com.grsoft.dataobjects.Config.class, "ServerConfig"));
			result.add(new FolderHitching());
			result.add(new OrgFoldersHitching());
			result.add(new MatrixHitching());
			result.add(new TaskHitchingEx());
			
			// должно идти после PotenzialOrgRcv
			result.add(new PODelHitching());

			if( Features.FOCUSED_GROUP )
				result.add(new FocusedGroupHitching());
			
			if( Features.SCRIPT_DOC )
				result.add(new RcvNewHitching(DbObject.getDataType(ScriptDef.class), ScriptDefImpl.OBJECT_NAME));

			if( Features.QUESTION )
				result.add(new RcvNewHitching(Question.class, "Question"));
			
			result.add(new FirmHitching());
			result.add(new AgentPrefixHitching());
			
			result.add(new OrgDiscountHitching());
			result.add(new HandledDocumentsHitching());
		}
		
		HandledDocuments.clearCache();
		CostStrategyEx.clearCache();
		
		return result;
	}
	
	protected Hitching getPriceHitching(boolean rcvRemains) {
		return new PriceHitchingEx();
	}
	
	class PriceHitchingEx extends PriceHitching
	{
		boolean inited = false;
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Price.class)));
				DbWriter.checkDBTable(DbObject.getDataType(Price.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	
	class OrgHitchingEx extends OrgHitching{
		boolean inited = false;
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Org.class)));
				DbWriter.checkDBTable(DbObject.getDataType(Org.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class FolderHitching extends Hitching{
		boolean inited = false;
		
		public FolderHitching() {
			super(Folder.class, "Folder");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Folder.class)));
				DbWriter.checkDBTable(DbObject.getDataType(Folder.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class OrgFoldersHitching extends Hitching{
		boolean inited = false;
		
		public OrgFoldersHitching() {
			super(OrgFolders.class, "OrgFolder");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(OrgFolders.class)));
				DbWriter.checkDBTable(DbObject.getDataType(OrgFolders.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class MatrixHitching extends Hitching{
		boolean inited = false;
		
		public MatrixHitching() {
			super(Matrix.class, "Matrix");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Matrix.class)));
				DbWriter.checkDBTable(DbObject.getDataType(Matrix.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class TaskHitchingEx extends TaskHitching{
		boolean inited = false;
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Task.class)));
				DbWriter.checkDBTable(DbObject.getDataType(Task.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class FocusedGroupHitching extends Hitching{
		boolean inited = false;
		
		public FocusedGroupHitching() {
			super(DbObject.getDataType(FocusedGroup.class), FocusedGroupImpl.OBJECT_NAME);
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(FocusedGroup.class)));
				DbWriter.checkDBTable(DbObject.getDataType(FocusedGroup.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class FirmHitching extends Hitching{
		boolean inited = false;
		
		public FirmHitching() {
			super(DbObject.getDataType(Firm.class), "Firm");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(Firm.class)));
				DbWriter.checkDBTable(DbObject.getDataType(Firm.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class AgentPrefixHitching extends Hitching{
		boolean inited = false;
		
		public AgentPrefixHitching() {
			super(AgentPrefix.class, "AgentPrefix");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(AgentPrefix.class)));
				DbWriter.checkDBTable(DbObject.getDataType(AgentPrefix.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	class OrgDiscountHitching extends Hitching{
		boolean inited = false;
		
		public OrgDiscountHitching() {
			super(OrgDiscount.class, "OrgDiscount");
		}
		
		@Override
		public void onRead(RawObject rawObject) throws RuntimeException {
			if(!inited){
				DbWriter.dropTable(DataObjectInfo.getInstance()
						.getTableName(DbObject.getDataType(OrgDiscount.class)));
				DbWriter.checkDBTable(DbObject.getDataType(OrgDiscount.class));
				inited = true;
			}
				
			super.onRead(rawObject);
		}
	}
	
	@Override
	protected void postExported(boolean docExported) {
		if(docExported){
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Order.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Delivery.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Payment.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Incass.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Remnants.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Sales.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Pko.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Pa.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(Movement.class)));
			DbWriter.dropTable(DataObjectInfo.getInstance()
					.getTableName(DbObject.getDataType(OrgSum.class)));
		}
	}
 }