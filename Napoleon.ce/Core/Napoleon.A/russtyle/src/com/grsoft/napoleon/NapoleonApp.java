/*
 * Copyright (C), 2011, Гильдия Разработчиков
 * 
 * Демо программа (с базой данных)
 *
 * kki   16/05/2011   creating
 */

package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDocEx;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ServerCommand;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeCmp;

public class NapoleonApp extends NapoleonAppBase {
	
	class OrderEditor implements OrderImpl.PropertiesEditor {
		@Override
		public void edit(Context ctx, OrderImpl order, boolean isOldOrder) {
			CreateOrder.open(ctx, order, isOldOrder);
		}
	}
	
	@Override
	protected void defineNewType() {
		FoldersAdapter.TreeNodeComparator = new NodeCmp();
		DbObject.regNewDataType(Price.class, PriceEx.class);
	}
	
	@Override
	protected void initDocTypes() {
		DebtDocEx.initialize();
		super.initDocTypes();
	}
	
	@Override
	protected void initChildActivity() {
		PriceCount.activity = PriceCountEx.class;
	}
	
	@Override
	public void onCreate() {
		ConfigManager.initConfig(new CfgNpl());
		super.onCreate();
		OrderImpl.OrderEditor = new OrderEditor();
		setProgrammVersion();
		
		//NapoleonChat.init(this);
	}

	private void setProgrammVersion() {
		try{
			ServerCommand.ProgramVersion = getResources().getString(R.string.version);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}

class NodeCmp extends TreeNodeCmp {
	@Override
	public int compare(TreeNode object1, TreeNode object2) {

		if( object1 instanceof FolderTreeNode && object2 instanceof FolderTreeNode )
			return ((FolderTreeNode)object1).name.compareTo(((FolderTreeNode)object2).name);

		return super.compare(object1, object2);
	}
}
