package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigHelper {
	private static final String RCV_REMNANTS_KEY = "ПриниматьОстаткиПриОтправке";
	private static final String BGK_RCV_REMNANTS = "ОстаткиФономМин";
	private static final String DLV_DATE = "ДатаДоставки";
	private static final String DISPOSITION = "Disposition";
	public static final String ORG_RADIUS = "OrgRadius";
	private static final String ORG_DISPOSITION_KEY = "ORG_DISPOSITION_DOCS";
	
	public static DlvDateType DEFAULT_DATE_TYPE = DlvDateType.today;
	
	/***
	 * Принимать остатки при отправке документов
	 * @return
	 */
	public static boolean isRcvRemnants(){
		boolean result = Features.RECEIVE_REMNANTS_WHEN_SENDING;
		
		if(!result){
			ConfigImpl cfg = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			
			if(cfg.getValue(sb, RCV_REMNANTS_KEY)){
				String v = sb.toString();
				
				if(v.trim().length() > 0){
					try{
						result = Integer.parseInt(v) == 1;
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		
		return result;
	}
	
	/***
	 * Период для фонового обновления остатков в милисекундах
	 * @return
	 */
	public static int getBkgRemnantsPeriod(){
		int result = 0;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if(cfg.getValue(sb, BGK_RCV_REMNANTS)){
			String v = sb.toString();
			
			if(v.trim().length() > 0){
				try{
					result = Integer.parseInt(v) * Consts.ONE_SECOND * Consts.SEC_PER_MIN;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		} else
			result = 15 * Consts.ONE_SECOND * Consts.SEC_PER_MIN;
		
		return result;
	}
	
	public enum DlvDateType { today, nextday, workday } 
	
	public static final DlvDateType getDateType(){
		DlvDateType result = DEFAULT_DATE_TYPE;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if(cfg.getValue(sb, DLV_DATE)){
			String v = sb.toString();
			
			if(v.trim().length() > 0){
				if (v.trim().equals("0"))
					result = DlvDateType.today;
				else if (v.trim().equals("1"))
					result = DlvDateType.nextday;
				else if (v.trim().equals("2"))
					result = DlvDateType.workday;
			}
		}
		
		return result;
	}
	
	static final String VALID_ORG_PREF_TAG = "ValidOrgTime";
	static final String VALID_ORG_ID_TAG = "OrgID";
	static final String VALID_ORG_TIME_TAG = "OrgTime";
	public static boolean isValidOrgTime(Context context, String orgId){
		boolean result = false;
		
		SharedPreferences sp =  context.getSharedPreferences(VALID_ORG_PREF_TAG, Context.MODE_PRIVATE);
		String id = sp.getString(VALID_ORG_ID_TAG, "invalid_id_for_organization");
		
		if(id.equals(orgId)){
			long time = sp.getLong(VALID_ORG_TIME_TAG, Consts.INVALID_ID);
			
			if(time != Consts.INVALID_ID){
				long now = new Date().getTime();
				CfgNplW config = (CfgNplW) ConfigManager.getConfig();
				
				result =  (now - time) < config.gps_valid_in_org; 
			}
		}
		
		return result;
	}
	
	public static void saveValidOrgTime(Context context, String orgId){
		Editor ed = context.getSharedPreferences(VALID_ORG_PREF_TAG, Context.MODE_PRIVATE).edit();
		ed.putString(VALID_ORG_ID_TAG, orgId);
		ed.putLong(VALID_ORG_TIME_TAG, new Date().getTime());
		ed.commit();
	} 
	
	public static String getCostType(){
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "ВидЦены";
		config.read();
		return c.value;
	}
	
	public static boolean isDisposition(String docName) {
		boolean result = Features.ORG_DISPOSITION;
		
		if(result){
			ConfigImpl cfg = new ConfigImpl();
			StringBuilder sb = new StringBuilder();
			
			if(cfg.getValue(sb, DISPOSITION))
				result = sb.toString().length() > 0;
			else
				result = false;
			
			if(result && docName != null) {
				result = false;
				sb = new StringBuilder();
				if(cfg.getValue(sb, ORG_DISPOSITION_KEY )) {
					for(String val : sb.toString().split(",")) {
						if(val.compareTo(docName) == 0) {
							result = true;
							break;
						}
					}
				}
			}
		}
		
		return result;
	}
	
	private static final double DEFAULT_POINT_RADIUS = 400.0;
	
	public static double getOrgRaduis() {
		double result = DEFAULT_POINT_RADIUS;
		
		ConfigImpl cfg = new ConfigImpl();
		StringBuilder sb = new StringBuilder();
		
		if(cfg.getValue(sb, ORG_RADIUS)) {
			try {
				result = Double.parseDouble(sb.toString());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return result;
	}
}
