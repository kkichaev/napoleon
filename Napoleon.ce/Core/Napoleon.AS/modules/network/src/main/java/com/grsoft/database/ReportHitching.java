package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectPool;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class ReportHitching extends Hitching {
	
	List<DataObject> params;
	List<Hitching> result;
	

	/**
	 * «апускаем обработку на сервере и получаем результаты
	 * @param reportName название модул€ запускаемого на сервере
	 * @param paramObj параметры
	 * @param result результаты
	 */
	public ReportHitching(String reportName, DataObject paramObj, List<Hitching> result) {
		super(null, reportName);
		params = new ArrayList<DataObject>();
		params.add(paramObj);
		
		dataObject = paramObj.getClass();
		this.result = result; 
	}
	
	public ReportHitching(String reportName, DataObject paramObj) {
		this(reportName, paramObj, new ArrayList<Hitching>());
	}
	
	/**
	 * «апускаем обработку на сервере и получаем результаты
	 * @param reportName название модул€ запускаемого на сервере
	 * @param paramObj параметры
	 * @param result результаты
	 */
	public ReportHitching(String reportName, DataObject paramObj, Hitching result) {
		super(null, reportName);

		params = new ArrayList<DataObject>();
		params.add(paramObj);
		
		dataObject = paramObj.getClass();
		this.result = new ArrayList<Hitching>(); 
		this.result.add(result); 
	}

	/**
	 * «апускаем обработку на сервере и получаем результаты
	 * @param reportName название модул€ запускаемого на сервере
	 * @param paramObj параметры
	 * @param result результаты
	 */
	public ReportHitching(String reportName, List<DataObject> params, List<Hitching> result) {
		super(null, reportName);
		this.params = params;
		
		dataObject = params.get(0).getClass();
		this.result = result; 
	}
	
	@Override
	public String getCommand() {
		return "Get Report";
	}
	
	public List<Hitching> getResultList() { return result; }
	
	public void addReport(DataObjectPool pool, List<Hitching> rcvHitching) {
		String name = "ParamObj" + Integer.toHexString(params.hashCode());
		for(DataObject o : params) {
			try {
				pool.add(o, name);
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		
		if( result.size() > 0 )
			rcvHitching.addAll(result);
	}
	
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {}
}
