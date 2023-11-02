package com.grsoft.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.OrderDoc;

public class AssortmentMatrixAdapter extends MatrixBaseAdapter {
	public static String TITLE;
	protected String id = "";
	public static int PERIOD_IN_MONTH = 1;
	public static int PERIOD_IN_DAY = 0;
	public static DocType MATRIX_DOC = null;
	public Map<String, MatrixItem> matrixMap = new HashMap<>();


	public AssortmentMatrixAdapter(Warehouse warehouse, String id) {
		super(warehouse);

		this.id = id;
		resetCache();
	}

	@Override
	protected List<MatrixItem> getMatrixItems() {
		matrixMap.clear();
		List<MatrixItem> result = new ArrayList<MatrixItem>();

		try {
			collectItems(id, result);
		} catch (Exception e) {

		}

		for(MatrixItem mi : result)
			matrixMap.put(mi.id, mi);

		return result;
	}

	public static boolean hasAssortiment(String id) {
		List<MatrixItem> result = new ArrayList<MatrixItem>();
		try {
			collectItems(id, result);
		} catch (Exception e) {
		}

		return result.size() > 0;
	}

	public static void collectItems(String id, final List<MatrixItem> result) {
		collectItems(id, result, null);
	}
	
	public static void collectItems(String id, final List<MatrixItem> result, AssortimenMatrixDocIterator iterator) {
		final Set<String> priceIds = new HashSet<String>();
		if( iterator == null )
			iterator = new AssortimenMatrixDocIterator();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		Date end = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.MONTH, -PERIOD_IN_MONTH);
		calendar.add(Calendar.DAY_OF_MONTH, -PERIOD_IN_DAY);
		Date begin = calendar.getTime();
		DatePeriod dp = new DatePeriod(begin, end);
		dp.periodType = DatePeriod.CREATED;

		if (MATRIX_DOC == null)
			MATRIX_DOC = OrderDoc.instance();
		DocList dl = MATRIX_DOC.docList(id, "created desc", dp);

		for (int i = 0; i < dl.getCount(); i++) {
			final Document<?> d = dl.get(i);
			iterator.iterItems(d, new IterFunc(){@Override public void process(String id) { addItem(result, priceIds, id, d);}});
		}
	}

	public AssortmentMatrixAdapter.AssortmentMatrixItem getMatrixItem(String id) {
		if (matrixMap.containsKey(id))
			return (AssortmentMatrixItem) matrixMap.get(id);
		return null;
	}

	public interface IterFunc { void process(String id); }
	
//	@SuppressWarnings("unchecked")
//	private static void iterItems(Document<?> doc, IterFunc func){
//		try{
//			Field f = doc.getData().getClass().getField("items");
//			List<DataObject> items = (List<DataObject>) f.get(doc.getData());
//			Field fid = null;
//			for(DataObject i : items){
//				if(fid == null)
//					fid = i.getClass().getField("id");
//				func.process(fid.get(i).toString());	
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}

	static void addItem(List<MatrixItem> result, Set<String> priceIds, String id, Document<?> doc) {
		if (!priceIds.contains(id)) {
			priceIds.add(id);
			AssortmentMatrixItem mi = new AssortmentMatrixItem();
			mi.id = id;

			if (doc.getData() instanceof CreateDocDataObject)
				mi.created = ((CreateDocDataObject)doc.getData()).created;

			result.add(mi);
		}
	}

	@Override
	public String getName() {
		return "AssortmentMatrixAdapter";
	}

	public static class AssortmentMatrixItem extends MatrixItem
	{
		public Date created;
	}
}
