package com.grsoft.util;

import java.util.ArrayList;

import android.view.View;

import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;

/**
 * Методя для работы с WarehouseAdapter 
 * @author 1111
 *
 */
public interface WarehouseManager {

	View getFolderView(FolderTreeNode node, View convertView);
	View getPriceView(PriceTreeNode node, View convertView);
	
	void editItem(long rowid);
	
	void applySearchFilter(String value);
	
	boolean isPriceExpand();
	
	/**
	 * Сортировка прайса в развернутом варианте
	 * @param price
	 */
	void sortingPriceList(ArrayList<TreeNode> childs);
	
	String getString(int price);
	
	boolean useInterlaceBackground();
	
	/**
	 * call-back вызывается после построения адаптера, в потоке построения (не в UI) можно дополнительно построить что-то
	 */
	void afterBuildSet();
}
