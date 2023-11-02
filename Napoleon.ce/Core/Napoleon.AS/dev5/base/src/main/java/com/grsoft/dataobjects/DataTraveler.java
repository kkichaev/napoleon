package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.impl.DbObject;

public class DataTraveler<T extends DataObject> {
	public T data;

	public abstract static class Travel<T extends DataObject> {
		boolean newDataInstance = false;
		
		public Travel(){
			this(false);
		}
		
		public Travel(boolean newDataInstance){
			this.newDataInstance = newDataInstance;
		}
		
		/**
		 * 
		 * @param item
		 * @return true - продолжаем движение, false - выходим из цикла
		 */
		public abstract boolean travel(DataTraveler<T> item);
		
		/**
		 * item.data = (T) dataType.newInstance();
		 * @return 
		 */
		public boolean isDataNewInstance() { return newDataInstance; }
	}

	public static <T extends DataObject> void travel(Class<? extends DataObject> dataType, Travel<T> travel, String where) {
		travel(dataType, travel, where, null);
	}

	@SuppressWarnings("unchecked")
	public static <T extends DataObject> void travel(Class<? extends DataObject> dataType, Travel<T> travel, String where, String order) {
		try {
			DataTraveler<T> item = new DataTraveler<T>();
			
			DbWriter.checkDBTable(dataType);
			dataType = DbObject.getDataType(dataType);
			item.data = (T) dataType.newInstance();

			DbReader reader = new DbReader();
			boolean bdo = reader.select(item.data, DataObjectInfo.getInstance().getTableName(dataType), where, order);
			
			while(bdo){
				bdo = travel.travel(item);
				
				if(bdo) {
					if (travel.isDataNewInstance())
						item.data = (T) dataType.newInstance();
					
					bdo = reader.selectNext(item.data);
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
