package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.modules.print.ListDataSource;
import com.grsoft.util.Consts;
import com.grsoft.util.FolderTree;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

public class PricePrintHelper {
	private Activity activity;
	
	public PricePrintHelper(Activity activity) {
		this.activity = activity;
	}
	
	public MenuHandler getMenuHandler() {
		return new MenuHandler(activity.getString(R.string.print_price), new Runnable() {
			@Override public void run() { activity.showDialog(R.id.chooseorgdlg); }
		});
	}
	
	public Dialog createrOrgSelector() {
		AlertDialog.Builder b = new AlertDialog.Builder(activity);
		
		final List<KeyValue> values = getOrgList();
		
		ArrayAdapter<KeyValue> a = new ArrayAdapter<KeyValue>(activity, R.layout.simple_spinner_layout, values);
		b.setSingleChoiceItems(a, -1, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				printPrice(values.get(which).key.toString());
			}
		});
		
		return b.create();
	}
	
	@SuppressWarnings("unchecked")
	public void updateOrgList(Dialog dlg) {
		if (dlg instanceof AlertDialog) {
			AlertDialog d = (AlertDialog)dlg;
			ArrayAdapter<KeyValue> aa = (ArrayAdapter<KeyValue>) d.getListView().getAdapter();
			aa.clear();
			
			for(KeyValue kv : getOrgList())
				aa.add(kv);
			
			aa.notifyDataSetChanged();
		}
	}
	
	private List<KeyValue> getOrgList() {
		final List<KeyValue> values = new ArrayList<KeyValue>();
		values.add(new KeyValue("", "<базовый>"));
		
		DataTraveler.travel(Org.class, new DataTraveler.Travel<Org>() {
			@Override
			public boolean travel(DataTraveler<Org> item) {
				KeyValue kv = new KeyValue(item.data.id, item.data.name + " (" + item.data.address + ")");
				values.add(kv);
				return true;
			}
		}, "", "name, address");
		
		return values;
	}

	@SuppressLint("UseSparseArrays")
	protected void printPrice(String orgId) {
		OrgImpl org = new OrgImpl();
		org.read("id", orgId);
		
		final OrderImplEx doc = new OrderImplEx();
		doc.initSilent(orgId, new GpsCoord(0, 0, 0));
		doc.getData().sumType = ((OrgEx)org.getData()).costype;
		
		final CostStrategy cs = CostStrategy.getInstance(doc.getClass());

		final FolderTree ft = new FolderTree();
		final FolderTreeData root = new FolderTreeData();
		ft.load();
		
		final HashMap<Integer, FolderTreeData> folders = new HashMap<Integer, FolderTreeData>();
		
		DataTraveler.travel(Price.class, new DataTraveler.Travel<Price>() {

			@Override
			public boolean travel(DataTraveler<Price> item) {
				PricePrintData pd = new PricePrintData(item.data, cs, doc);
				FolderTreeData cf = loadFolders(item.data.folderID, ft, root, folders);
				cf.add(pd);
				return true;
			}
		}, "", "folderID,name");
		
		List<PricePrintData> source = new ArrayList<PricePrintData>(); 
		addToListSource(source, root.folders);
		SelectPrinFormDlg.createPrintForm(activity, new ListDataSource(source), R.id.wait_for_print_dlg, "price", null);
	}

	private void addToListSource(List<PricePrintData> ret, List<FolderTreeData> folders) {
		for(FolderTreeData fd : folders) {
			ret.add(new PricePrintData(fd));		
			addToListSource(ret, fd.folders);
			ret.addAll(fd.price);
		}
	}

	protected FolderTreeData loadFolders(int folderID, FolderTree ft, FolderTreeData root, HashMap<Integer, FolderTreeData> folders) {
		if(folders.containsKey(folderID))
			return folders.get(folderID);
		
		Folder folder = ft.getFolder(folderID);
		FolderTreeData fd = new FolderTreeData(folder);
		FolderTreeData ret = fd;
		
		folders.put(folderID, fd);
		
		Folder parent;
		while((parent = ft.getParent(folder)) != null) {
			FolderTreeData pd = folders.get(parent.id); 
			if(pd != null) {
				pd.add(fd);
				break;
			}
			pd = new FolderTreeData(parent);
			folders.put(parent.id, pd);
			pd.add(fd);
			
			fd = pd;
			folder = parent;
		}
		if(parent == null)
			root.add(fd);
		
		return ret;
	}
	
	static class PricePrintData {
		public String name;

		public String cost = "";

		public String costPack = "";
		
		public PricePrintData(FolderTreeData data) { name = data.name; }
		
		public PricePrintData(Price data, CostStrategy cs, OrderImplEx doc) {
			name = data.name;
			int cst = cs.getItemCost(data, doc);
			cost = Util.IntToScaleStr(cst, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			
			if(data.qtyInPack == 0)
				data.qtyInPack = Consts.QTY_SCALE;;
				
			costPack = Util.IntToScaleStr((int)((long)cst * data.qtyInPack / Consts.QTY_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false);
		}
	}

	static class FolderTreeData {
		public String name;
		
		public List<FolderTreeData> folders = new ArrayList<FolderTreeData>();
		public List<PricePrintData> price = new ArrayList<PricePrintData>();
		
		public FolderTreeData(Folder f) { name = f.name; }
		public FolderTreeData() { name = ""; }
		
		public void add(FolderTreeData child) {
			this.folders.add(child);
		}

		public void add(PricePrintData pd) {
			price.add(pd);
		}
	}
}
