package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DocDataObject;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DocUserStatusImpl;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.Consts;
import com.grsoft.util.view.dialog_helper.DialogHelper;


@SuppressLint("UseSparseArrays")
public class DocStatus {
	private static DocStatus instance = new DocStatus();
	private static final String SHIPPED = "отгружено";
	private static final String RESERVED = "зарезервировано";
	private static final String INPROCEED = "в обработке";
	private static final String SENT = "отправлен";
	private static final String NOTSENT = "не отправлен";
	
	/*статус - имя ресурса*/
	private static Map<String, String> status = new HashMap<String, String>();
	/*имя ресурса - id */
	private static Map<String, Integer> resources = new HashMap<String, Integer>();
	private static Map<Integer, String> nameresources = new HashMap<Integer, String>();
	
	private static final String SENT_IMG = "sent";
	private static final String INPROCEED_IMG = "pcd";
	private static final String RESERVED_IMG = "dlvstatus";
	private static final String NOTSENT_IMG = "notsend";
	private static final String SHIPPED_IMG = "s09";
	
	private static DocUserStatusImpl statusImpl = new DocUserStatusImpl();
	
	static{
		status.put(NOTSENT, NOTSENT_IMG);
		status.put(SENT, SENT_IMG);
		status.put(INPROCEED, INPROCEED_IMG);
		status.put(RESERVED, RESERVED_IMG);
		status.put(SHIPPED, SHIPPED_IMG);
	}
	
	public static void initResources(Context context){
		Resources res = context.getResources();
		
		if (res != null){
			final String FOLDER = "drawable";
			
			for(String img : getAllImages()){
				int id = res.getIdentifier(img, FOLDER, context.getPackageName()); 
				resources.put(img, id);
				nameresources.put(id, img);
			}
		}
	}

	protected static String[] getAllImages() {
		return new String[]{
				NOTSENT_IMG, SENT_IMG, INPROCEED_IMG, RESERVED_IMG, "s01", "s02", "s03", "s04", "s05", "s06", "s07", "s08", "s09", "s10", "s11", "s12", "s13", "s14", "s15", "s16",
				"s17", "s18", "s19", "s20", "s21", "s22", "s23", "s24"
		};
	}
	
	public static DocStatus getInstance(){
		return instance;
	}
	
	public static int getImage(Document<?> doc ){
		String s = getStatusStr(doc);
		return getImage(s);
	}
	
	public static int getImage(String s ){
		int result = Consts.INVALID_ID;

		String pic = INPROCEED_IMG;
		
		if(statusImpl.read("name", s))
			pic = statusImpl.getData().pic;
		else if (status.containsKey(s))
			pic = status.get(s);
		
		if (resources.containsKey(pic))
			result = resources.get(pic);
			
		return result;
	}

	public static String getStatusStr(Document<? extends DocDataObject> doc){
		if(doc != null){
			if(doc instanceof OrderImplEx){
				OrderEx ord = (OrderEx) doc.getData();
				
				if(ord.number != null && ord.number.trim().length() > 0)
					return SHIPPED;
				else if (ord.orderNumber != null && ord.orderNumber.trim().length() > 0)
					return RESERVED;
			}
			
			Object data = doc.getData();
			
			if(data instanceof CreateDocDataObject){
				CreateDocDataObject cddo = (CreateDocDataObject) data;
				if (cddo.podRemark != null & cddo.podRemark.trim().length() > 0)
					return cddo.podRemark;
			}
			
			if(doc instanceof CreatableDocument){
				 CreatableDocument<?> cd = (CreatableDocument<?>)doc;
				 
				 if(cd.isProceeded())
					 return INPROCEED;
				 else if (cd.isExported())
					 return SENT;
			}
		}
		
		return NOTSENT;
	}
	
	
	public static Map<String, Integer> getResources(){ return resources; }
	
	public static Set<Entry<String, String>> getMyStatuses(){ return status.entrySet();	}
	
	public static String getResName(int id){
		String result = "";
		
		if(nameresources.containsKey(id))
			result = nameresources.get(id);
		
		return result;
	}
	
	public static void collect(List<String> out){
		Set<String> hash = new HashSet<String>();
		
		for(Entry<String, String> s: DocStatus.getMyStatuses()){
			if(!hash.contains(s.getKey())){
				hash.add(s.getKey());
				out.add(s.getKey());
			}
		}
		
		final String STATUSES = "Статусы";
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		
		if(cfg.getValue(sb, STATUSES)){
			List<CharSequence> list = new ArrayList<CharSequence>();
			DialogHelper.makeList(sb.toString(), list);
			
			for(CharSequence s : list){
				if(!hash.contains(s)){
					String ss = s.toString();
					hash.add(ss);
					out.add(ss);
				}
			}
		}
	}
}
