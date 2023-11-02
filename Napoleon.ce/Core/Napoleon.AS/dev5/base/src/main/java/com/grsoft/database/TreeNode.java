/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Дерево TreeNode
 *
 * kki   27/11/2010   creating
 */
package com.grsoft.database;
import com.grsoft.aceteam.R;

import java.util.ArrayList;

import android.view.View.OnClickListener;

public abstract class TreeNode 
	implements OnClickListener, Comparable<TreeNode>
{
	private TreeNode parent;
	protected ArrayList<TreeNode> childs;
	
	public TreeNode(TreeNode parent)
	{
		this.parent = parent;
	}
	
	public TreeNode getParent()
	{
		return parent;
	}
	
	public void setParent(TreeNode node)
	{
		parent = node;
	}
	
	public ArrayList<TreeNode> getChilds()
	{
		if (childs == null) 
			childs = new ArrayList<TreeNode>();
		
		return childs;
	}
	
	public boolean hasChilds()
	{
		return childs.size() > 0;
	}
	
	public TreeNode getChild(int index)
	{
		return childs.get(index);
	}
	
	public int getChildsCount()
	{
		return childs != null ? childs.size() : 0;
	}
	
	public int indexOf(TreeNode node)
	{
		return childs.indexOf(node);
	}
	
	public TreeNode get(int index)
	{
		return childs.get(index);
	}
	
	public abstract boolean isLeaf();
	
	@Override
	public String toString(){
		return "";
	}
	
	@Override
	public int compareTo(TreeNode treeNode){
		return toString().compareTo(treeNode.toString());
	}
	
	public boolean isFolderNode(){
		return false;
	}
	
	public void open(){}
	public long getRowid(){ return -1; }
}
