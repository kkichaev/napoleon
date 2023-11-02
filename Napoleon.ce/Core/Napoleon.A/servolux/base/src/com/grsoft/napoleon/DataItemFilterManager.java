package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.grsoft.dataobjects.Brands;
import com.grsoft.dataobjects.FirmEx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

public class DataItemFilterManager {
	public interface DataEvent {
		void dataFiltred(List<FiltrableDataItem> newList);
		void askShowDialog(int id);
	}
	
	Activity context = null;
	List<FiltrableDataItem> allItems;
	DataEvent handler;
	boolean clearingFilter = false;
	
	List<FirmEx> firms;
	List<Brands> brands;
	List<FiltrableDataItem> folders;
	
	FirmDataFilter firmFilter;
	BrandDataFilter brandFilter = new BrandDataFilter();
	PrefixDataFilter prefixFilter = new PrefixDataFilter();
	FolderDataFilter folderFilter = new FolderDataFilter();
	QtyDataFilter qtyFilter = new QtyDataFilter();
	
	
	int firmId = 0;
	int brandId = 0;
	int prefixId = 0;
	int folderId = 0;
	int qtyId = R.id.qty_data_filter;
	
	@SuppressLint("UseSparseArrays")
	Map<Integer, DataItemFilter> filters = new HashMap<Integer, DataItemFilter>();

	public DataItemFilterManager(TextView firms, TextView brands, TextView items, TextView prefix, List<FiltrableDataItem> allItems, DataEvent handler) {
		this.handler = handler;
		this.allItems = allItems;
		
		firmFilter = new FirmDataFilter();
		List<FiltrableDataItem> usedFolders = new ArrayList<FiltrableDataItem>();
		Set<String> usedBrands = new HashSet<String>();

		for(FiltrableDataItem fdi : allItems) {
			if(fdi.isFolder())
				usedFolders.add(fdi);
			if(fdi.brand.length()> 0) {
				usedBrands.add(fdi.brand);
				firmFilter.addBrand(fdi);
			}
		}
		
		if(firms != null) {
			firmId = firms.getId();
			
			context = (Activity) firms.getContext();
			firms.setOnClickListener(showDialog);
			firms.setOnLongClickListener(clearFilter);
			firms.setTypeface(null, Typeface.NORMAL);
		}
		if(brands != null) {
			brandId = brands.getId();
			
			if(context == null)
				context = (Activity) brands.getContext();
			brands.setOnClickListener(showDialog);
			brands.setOnLongClickListener(clearFilter);
			brands.setTypeface(null, Typeface.NORMAL);
			this.brands = new ArrayList<Brands>(); 
			for(Brands b : Brands.get().values()) {
				if(usedBrands.contains(b.id))
					this.brands.add(b);
			}
			Collections.sort(this.brands);
		}
		if(items != null) {
			folderId = items.getId();
			
			if(context == null)
				context = (Activity) items.getContext();
			items.setOnClickListener(showDialog);
			items.setOnLongClickListener(clearFilter);
			items.setTypeface(null, Typeface.NORMAL);
			
			folders = usedFolders;
		}
		if(prefix != null) {
			prefixId = prefix.getId();
			
			if(context == null)
				context = (Activity) prefix.getContext();
			prefix.setOnClickListener(showDialog);
			prefix.setOnLongClickListener(clearFilter);
			prefix.setTypeface(null, Typeface.NORMAL);
		}
	}
	
	View.OnClickListener showDialog = new View.OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			if(clearingFilter) {
				clearingFilter = false;
				return;
			}
			if(handler != null)
				handler.askShowDialog(arg0.getId());
		}
	};

	View.OnLongClickListener clearFilter = new View.OnLongClickListener() {		
		@Override public boolean onLongClick(View arg0) {
			clearFilter(arg0.getId());
			((TextView)arg0).setTypeface(null, Typeface.NORMAL);
			return false;
		}
	};
	
	public void setFirms(Set<String> usedFirms) {
		firms = new ArrayList<FirmEx>();
		
		Map<String, FirmEx> frms = FirmEx.get();
		for(String id : usedFirms) {
			FirmEx f = frms.get(id);
			if( f != null)
				firms.add(f);
		}
		
		Collections.sort(firms);
	}
	
	public boolean isQtyFilterSet() { return filters.containsKey(qtyId); }
	
	public void setQtyFilter(boolean setFilter) {
		int sz = filters.size();
		if(setFilter) {
			filters.put(qtyId, qtyFilter);
		} else {
			filters.remove(qtyId);
		}

		if( sz != filters.size())
			updateData();
	}
	
	protected void clearFilter(int filterId) {
		clearingFilter = true;
		TextView tv = (TextView)context.findViewById(filterId);
		if(tv != null)
			tv.setTypeface(null, Typeface.NORMAL);
		
		DataItemFilter f = filters.get(filterId);
		filters.remove(filterId);
		if(f != null)
			f.clear();
		updateData();
	}

	private void updateData() {
		List<FiltrableDataItem> newItems = new ArrayList<FiltrableDataItem>();
		for(FiltrableDataItem fi : allItems) {
			if(!fi.isFolder())
				continue;
			
			FiltrableDataItem di = null;
			for(FiltrableDataItem src : fi.getChilds()) {
				if(inSet(src, fi)) {
					if(di == null)
						di = fi.createFolderItem();
					di.addChild(src);
				}
			}
			if(di != null && di.isFolder()) {
				newItems.add(di);
				for(FiltrableDataItem fdi : di.getChilds())
					newItems.add(fdi);
			}
		}
		
		if(handler != null)
			handler.dataFiltred(newItems);
	}
	
	boolean inSet(FiltrableDataItem item, FiltrableDataItem parent) {
		for(DataItemFilter f : filters.values())
			if(f.inSet(item, parent) == false)
				return false;
		
		return true;
	}

	public Dialog createDialog(int filterId) {
		if(filterId == firmId)
			return createSelectFirmDialog();
		if(filterId == brandId)
			return createSelectBrandDialg();
		if(filterId == prefixId)
			return createSelectPrefixDialg();
		if(filterId == folderId)
			return createSelectFolderDialog();
		return null;
	}
	
	public boolean isFirmChoosed(String firmId) {
		FirmDataFilter f = (FirmDataFilter)filters.get(this.firmId);
		return f == null || f.haveItem(firmId);
	}

	private Dialog createSelectFirmDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Производители");

		filters.remove(firmId);
		
		CharSequence[] cs = new CharSequence[firms.size()];
		boolean[] checked = new boolean[firms.size()];
		
		int index = 0;
		for(FirmEx f : firms) {
			cs[index] = f.name;
			checked[index] = firmFilter.haveItem(f.id);
			index++;
		}		
		b.setMultiChoiceItems(cs, checked, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
				firmFilter.setItem(firms.get(arg1).id, arg2);
			}
		});
		
		b.setPositiveButton(android.R.string.ok, new DialogOnClick(firmId, firmFilter)); 
		
		return b.create();
	}

	private Dialog createSelectPrefixDialg() {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Признак");

		filters.remove(prefixId);
		
		final CharSequence[] cs = new CharSequence[] {
			"", "A", "M", "!"	
		};
		boolean[] checked = new boolean[cs.length];
		
		int index = 0;
		for(CharSequence f : cs) {
			checked[index] = prefixFilter.haveItem(f.toString());
			index++;
		}
		
		b.setMultiChoiceItems(cs, checked, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
				prefixFilter.setItem(cs[arg1].toString(), arg2);
			}
		});		
		b.setPositiveButton(android.R.string.ok, new DialogOnClick(prefixId, prefixFilter)); 
		
		return b.create();
	}

	private Dialog createSelectBrandDialg() {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Бренды");

		filters.remove(brandId);
		
		boolean haveFirmFilter = filters.containsKey(firmId);
		
		final List<Brands> activeBrand = new ArrayList<Brands>();
		List<String> usedNames = new ArrayList<String>();
		List<Boolean> checkedArray = new ArrayList<Boolean>();
		for(Brands f : brands) {
			if(haveFirmFilter && firmFilter.haveBrand(f) == false) {
				brandFilter.setItem(f.id, false);
				continue;
			}
			activeBrand.add(f);
			usedNames.add(f.name);
			checkedArray.add(brandFilter.haveItem(f.id));
		}

		CharSequence[] cs = new CharSequence[usedNames.size()]; 
		cs = usedNames.toArray(cs);
		boolean[] checked = new boolean[checkedArray.size()];
		int i = 0;
		for(Boolean bv : checkedArray)
			checked[i++] = bv;
		
		b.setMultiChoiceItems(cs, checked, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
				brandFilter.setItem(activeBrand.get(arg1).id, arg2);
			}
		});
		
		b.setPositiveButton(android.R.string.ok, new DialogOnClick(brandId, brandFilter)); 
		
		return b.create();
	}

	private Dialog createSelectFolderDialog() {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("Папки товаров");

		filters.remove(folderId);
		
		final List<FiltrableDataItem> activeFolders = new ArrayList<FiltrableDataItem>();
		List<String> usedNames = new ArrayList<String>();
		List<Boolean> checkedArray = new ArrayList<Boolean>();
		
		for(FiltrableDataItem f : folders) {
			boolean haveFolder = true;
			for(DataItemFilter dif : filters.values()) {
				if(!dif.haveFolder(f)) {
					haveFolder = false;
					break;
				}
			}
			if(!haveFolder)
				continue;
			
			usedNames.add(f.name);
			checkedArray.add(folderFilter.haveItem(f.id));
			activeFolders.add(f);
		}
		
		CharSequence[] cs = new CharSequence[usedNames.size()]; 
		cs = usedNames.toArray(cs);
		boolean[] checked = new boolean[checkedArray.size()];
		int i = 0;
		for(Boolean bv : checkedArray)
			checked[i++] = bv;

		b.setMultiChoiceItems(cs, checked, new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
				folderFilter.setItem(activeFolders.get(arg1).id, arg2);
			}
		});
		
		b.setPositiveButton(android.R.string.ok, new DialogOnClick(folderId, folderFilter)); 
		
		return b.create();
	}
	
	class DialogOnClick implements DialogInterface.OnClickListener {
		int id;
		DataItemFilter filter;
		
		public DialogOnClick(int id, DataItemFilter filter) {
			this.id = id;
			this.filter = filter;
		}
		
		@Override public void onClick(DialogInterface arg0, int arg1) {
			arg0.dismiss();
			
			filters.put(id, filter);
			TextView tv = (TextView)context.findViewById(id);
			if(tv != null)
				tv.setTypeface(null,  Typeface.BOLD);
			updateData();
		}
	}
}

class FirmDataFilter extends DataItemFilter {

	Map<String, Set<String>> brandsToFirm = new HashMap<String, Set<String>>();
	Set<String> items = new HashSet<String>();
	
	public FirmDataFilter() {}
	public boolean haveItem(String id) { return items.contains(id); }
	
	@Override public void clear() { items.clear(); }

	public void addBrand(FiltrableDataItem fdi) {
		if(fdi.firm.length() > 0) {
			Set<String> fb = brandsToFirm.get(fdi.firm);
			if(fb == null) {
				fb = new HashSet<String>();
				brandsToFirm.put(fdi.firm, fb);
			}
			fb.add(fdi.brand);
		}
	}
	
	public boolean haveBrand(Brands b) {
		boolean ret = false;
		for(String fid : items) {
			Set<String> brnds = brandsToFirm.get(fid);
			if(brnds != null && brnds.contains(b.id)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	@Override
	public boolean haveFolder(FiltrableDataItem fdi) {
		boolean ret = false;
		if(fdi.getChilds() != null)
			for(FiltrableDataItem chi : fdi.getChilds()) {
				if(items.contains(chi.firm)){
					ret = true;
					break;
				}
			}
		return ret;
	}
	
	public void setItem(String id, boolean checked) {
		if(checked)
			items.add(id);
		else
			items.remove(id);
	}	
	
	@Override
	public boolean inSet(FiltrableDataItem item, FiltrableDataItem parent) {
		return items.contains(item.firm);
	}
}

class BrandDataFilter extends FirmDataFilter {
	
	@Override
	public boolean inSet(FiltrableDataItem item, FiltrableDataItem parent) {
		return items.contains(item.brand);
	}

	@Override
	public boolean haveFolder(FiltrableDataItem fdi) {
		boolean ret = false;
		if(fdi.getChilds() != null)
			for(FiltrableDataItem chi : fdi.getChilds()) {
				if(items.contains(chi.brand)){
					ret = true;
					break;
				}
			}
		return ret;
	}
}

class PrefixDataFilter extends FirmDataFilter {
	
	@Override
	public boolean inSet(FiltrableDataItem item, FiltrableDataItem parent) {
		return items.contains(item.prefix);
	}

	@Override
	public boolean haveFolder(FiltrableDataItem fdi) {
		return true;
	}
}

class QtyDataFilter extends DataItemFilter {

	@Override
	public boolean inSet(FiltrableDataItem item, FiltrableDataItem parent) {
		return (item.getQty() > 0);
	}

	@Override
	public boolean haveFolder(FiltrableDataItem fdi) {
		boolean ret = false;
		if(fdi.getChilds() != null)
			for(FiltrableDataItem chi : fdi.getChilds()) {
				if(chi.getQty() > 0){
					ret = true;
					break;
				}
			}
		return ret;
	}	
}

class FolderDataFilter extends FirmDataFilter {
	
	@Override
	public boolean inSet(FiltrableDataItem item, FiltrableDataItem parent) {
		return items.contains(parent.id);
	}

	@Override
	public boolean haveFolder(FiltrableDataItem fdi) {
		return items.contains(fdi.id);
	}
}