package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgGroup;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.GroupNode.GroupInfo;
import com.grsoft.napoleon.OrgGroupAdapter.OnChangeListener;
import com.grsoft.util.Filter;
import com.grsoft.util.TreeNodeCmp;

public class NapoleonEx extends Napoleon {
	private OrgGroupAdapter adapter;
	public boolean buildingProcess = false;
	private static final int DLG_WAIT = 3;
	private OrgImpl orgImpl = new OrgImpl();
	public static final String EXPAND_ORG_PREF = "expand_org";
	protected OnItemClickListener oldClick = new OrglListOnClickListener();
	protected OnItemClickListener newClick = new OrgTreeOnClickListener();
	protected TextView tvMainDocValColTitle;
	protected ImageView ivExpand;
	
	protected OnClickListener expdandClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			adapter.expandSwitch();
			
			if (adapter != null){
				boolean isExpanded = adapter.isExpanded();
				ivExpand.setImageResource(isExpanded ? R.drawable.ic_menu_add : R.drawable.ic_menu_more);
				Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
				editor.putBoolean(EXPAND_ORG_PREF, isExpanded);
				editor.commit();
			}
		}
	};
	protected OnClickListener setTopLevel = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			setTopLevelForTableHeader();
		}
	};
	
	class OrgTreeOnClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			adapter.onClick(position);
		}
		
	}
	
	OnClickListener goUpListenr = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			adapter.upLevel();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvMainDocValColTitle = (TextView) findViewById(R.id.tvMainDocValColTitle);
		ivExpand = (ImageView) findViewById(R.id.ivExpand);
		
		ivExpand.setImageResource(R.drawable.ic_menu_more);
		adapter = new OrgGroupAdapter(this);
		adapter.setOnChangeListener(adapterOnChangeListener);
		adapter.setExpanded(isExpanded());
		ivExpand.setOnClickListener(expdandClick);
		
		setListMode(listViewMode);
	}

	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}
	
	public boolean isExpanded() {
		return getPreferences(Context.MODE_PRIVATE).getBoolean(EXPAND_ORG_PREF, true);
	}
	
	public View getGroupView(GroupNode node, View view, int pos) {
		int id = R.layout.itemselectrow; ;
		View result;
		if( view != null && view.getTag(id) != null)
			result = view;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}

		TextView tvOrgName = (TextView)result.findViewById(R.id.tvItemSelectRowName);
		tvOrgName.setText(node.name);
		linesController.prepareTextView(tvOrgName);
		tvOrgName.setTag(node);
		
		return result;
	}

	public View getOrgView(Node node, View view, int pos) {
		orgImpl.read(node.getRowid());
		View result;
		int id = R.layout.main_list_row;
		if( view != null && view.getTag(id) != null)
			result = view;
		else {
			result = View.inflate(this, id, null);
			result.setTag(id, true);
		}
		
		if(orgImpl != null){
			setOrgBackground(pos, orgImpl, result);
			result.setTag(orgImpl.getRowid());
			drawOrg(orgImpl, result);
			ImageView ivFolder = (ImageView) result.findViewById(R.id.ivFolder);
			ivFolder.setVisibility(View.GONE);
		}
		
		return result;
	}

	public void editItem(long rowid) {
		Documents.open(NapoleonEx.this, rowid, isPotencialOrg(rowid));
	}

	public void sortingPriceList(ArrayList<TreeNode> childs) {}

	private OrgGroupAdapter.OnChangeListener adapterOnChangeListener = new OnChangeListener() {
		@Override
		public void startBuildSet(OrgGroupAdapter adapter) {
			buildingProcess = true;
			btnFind.setEnabled(false);
			edFind.setEnabled(false);
			showDialog(DLG_WAIT);
		}
		
		@Override
		public void endBuildSet(OrgGroupAdapter adapter) {
			try {
				btnFind.setEnabled(true);
				edFind.setEnabled(true);
				dismissDialog(DLG_WAIT);
				buildingProcess = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAdapterChange(OrgGroupAdapter adapter) {
			ivGoUp.setVisibility(adapter.isTop() ? View.GONE : View.VISIBLE);
		}

		@Override
		public void setSelection(int position) {
			lvMainOrgs.setSelection(position);
		}
	};
	
	@Override
	protected void setListMode(ListViewMode mode){
		View tvFirstColumnCaption = findViewById(R.id.tvFirstColumnCaption));
		switch(mode)
		{
			case ORG_LIST : lvMainOrgs.setAdapter(adapter);
				lvMainOrgs.setOnItemClickListener(newClick);
				ivExpand.setVisibility(View.VISIBLE);
				tvFirstColumnCaption.setOnClickListener(goUpListenr);
				break;
			case ROUTE_LIST: lvMainOrgs.setAdapter(orgFoldersAdapter);
				lvMainOrgs.setOnItemClickListener(oldClick);
				tvFirstColumnCaption.setOnClickListener(setTopLevel);
				ivExpand.setVisibility(View.INVISIBLE);
				break;
		}
		
		listViewMode = mode;
		updateModeImage();
		setPrefValue(LIST_MODE, mode.val);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case DLG_WAIT:
			return createWaitDlgDialog();
		default:
			return super.onCreateDialog(id);
		}
	}
	
	private Dialog createWaitDlgDialog() {
		ProgressDialog result = new ProgressDialog(this);
		result.setMessage(getString(R.string.org_loading));
		
		result.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(adapter != null)
					adapter.close();
			}
		});
		
		return result;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(listViewMode == ListViewMode.ORG_LIST){
			adapter.resetFilter();
			adapter.buildSet();
		}
	}
}

interface NodeFactory {
	TreeNode createTreeNode(TreeNode parent, long groupRowid, String name);
	String getWhereStr();
	boolean isValid(TreeNode node);
}

class Node extends TreeNode{
	private String name;
	private long priceRowid;
	
	public Node(TreeNode parent, long rid, String name){
		super(parent);
		this.priceRowid = rid;
		this.name = name;
	}
	
	@Override
	public String toString() { return name;	}

	@Override
	public boolean isLeaf()	{ return false;	}
	
	@Override
	public boolean hasChilds() { return false;	}
	
	@Override
	public int compareTo(TreeNode treeNode) {
		if (this.getClass() != treeNode.getClass())
			return 1;
		else
			return super.compareTo(treeNode);
	}
	
	@Override
	public long getRowid() { return priceRowid; }

	@Override
	public void onClick(View v) {}
}

class GroupNode extends TreeNode implements NodeFactory
{
	public int id;
	public int level;
	public String name;
	public boolean priceLoaded;
	private boolean nodeIsLeaf;
	private NodeFactory foldersTree;
	
	public static class GroupInfo{
		public long rowid;
		public String name;
		
		public GroupInfo(long rowid, String name){
			this.rowid = rowid;
			this.name = name;
		}
	}
	
	public GroupNode(NodeFactory foldersTree, GroupNode parent){
		super(parent);
		this.foldersTree = foldersTree;
	}
	
	public void insert(TreeNode child)
	{
		child.setParent(this);
		getChilds().add(child);
	}

	@Override
	public void open() {
		loadNodes();
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean isLeaf()
	{
		return nodeIsLeaf;
	}
	
	public void setLeaf(boolean val)
	{
		nodeIsLeaf = val;
	}
	
	class PriceData extends DataObject {
		public String name;
		public long rowid;
	}
	
	public String getWhereStr(){
		StringBuilder result = new StringBuilder();
		result.append("folderid=").append(id);
		
		String selection = foldersTree.getWhereStr();
		
		if (selection.length() > 0)
		{
			result.append(" AND ");
			result.append(selection);
		}
				
		return result.toString();
	}
	
	public void reload() {
		if( priceLoaded ) {
			Iterator<TreeNode> iterator = childs.iterator();
			
			while (iterator.hasNext()) {
				TreeNode node = (TreeNode) iterator.next();
				if (node instanceof Node )
					iterator.remove();
			}
			
			priceLoaded = false;
		}
		loadNodes();
	}
	
	public void loadNodes(){
		if (nodeIsLeaf && !priceLoaded)
		{
			if (childs == null) 
				childs = new ArrayList<TreeNode>();
			
			List<GroupInfo> priceInfoList = ((OrgGroupAdapter)foldersTree).getPriceInfo(id);
			if (priceInfoList != null)
				for(GroupInfo pi : priceInfoList){
						Node node = (Node)foldersTree
								.createTreeNode(this, pi.rowid, pi.name); 
						childs.add(node);
					}
			
			Collections.sort(childs, new TreeNodeCmp());
			
			priceLoaded = true;
		}
	}
	
	
	public String getFoldersIds(){
		StringBuilder result = new StringBuilder();
		result.append(Integer.toString(id));
		result.append(",");
		
		for (TreeNode node : childs) {
			if (node instanceof FolderTreeNode)
				result.append(((FolderTreeNode) node).getFoldersIds())
					.append(",");
		}
		
		result.deleteCharAt(result.length() - 1);
		
		return result.toString();
	}
	
	@Override
	public int compareTo(TreeNode treeNode) {
		if (this.getClass() != treeNode.getClass())
			return -1;
		else
			return id - ((GroupNode)treeNode).id;
	}

	public GroupNode findFolder(int folderId) {
		if( id == folderId )
			return this;
		
		if(childs != null)
			for( TreeNode tn : childs ) {
				if( tn instanceof GroupNode ) {
					GroupNode ret = ((GroupNode)tn).findFolder(folderId);
					if( ret != null )
						return ret;
				}
			}
		
		return null;
	}
	
	@Override
	public boolean isFolderNode() {
		return true;
	}

	@Override
	public void onClick(View v) {}

	@Override
	public TreeNode createTreeNode(TreeNode parent, long groupRowId,
			String name) {
		return new Node(parent, groupRowId, name);
	}

	@Override
	public boolean isValid(TreeNode node) {
		return true;
	}
}

class OrgGroupAdapter extends BaseAdapter implements NodeFactory,
com.grsoft.napoleon.util.FilterAdapter{
	protected NapoleonEx napoleon;
	protected OnChangeListener onChangeListener;
	protected GroupNode root;
	protected GroupNode folderTop;
	protected GroupNode priceTop;
	protected List<Filter> filters = new ArrayList<Filter>();
	protected boolean solidPrice = false;
	protected Map<Integer, ArrayList<GroupInfo>> fprice = 
			new HashMap<Integer, ArrayList<GroupInfo>>();
	private BuildSetThread buildSetThread;
	boolean refreshing = false;
	static GroupNode globalRoot;
	static Map<Integer, ArrayList<GroupInfo>> globalPrice;
	static String filterData = "";
	
	@Override
	public boolean isValid(TreeNode node) {
		return true;
	}
	
	public static String GROUP_ADAPTER = "GroupAdapter";
	
	public String getName() { return GROUP_ADAPTER; }
	
	public interface OnChangeListener {
		void startBuildSet(OrgGroupAdapter adapter);
		void endBuildSet(OrgGroupAdapter adapter);
		void onAdapterChange(OrgGroupAdapter adapter);
		void setSelection(int position);
	}
	
	public OrgGroupAdapter(NapoleonEx napoleon){
		this.napoleon = napoleon;
		root = createFoldersTreeNode(null);
		folderTop = root;
		priceTop = createFoldersTreeNode(null);;
	}
	
	@Override
	public int getCount() {
		if( refreshing )
			return 0;
		
		GroupNode ftn = solidPrice ? priceTop :  folderTop;
		int result = 0;
		
		if (ftn == null || ftn.getChilds() == null)
			result = 0;
		else
			result = ftn.getChilds().size();
		
		return result;
	}

	@Override
	public Object getItem(int position) {
		return solidPrice ? priceTop.getChild(position) : folderTop.getChild(position);
	}

	@Override
	public long getItemId(int position) { return 0; }

	@Override
	public View getView(int pos, View convertView, ViewGroup arg2) {
		TreeNode node = (TreeNode) getItem(pos);
		View view = node.isFolderNode()
				? napoleon.getGroupView((GroupNode)node, convertView, pos)
				: napoleon.getOrgView((Node)node, convertView, pos);
		
		if (view != null)
			view.setBackgroundResource(pos % 2 != 0 ? 
				R.drawable.even_row_selector :
				R.drawable.list_selector);
		
		return view;
	}
	
	public void buildSet(boolean solidPrice) {
		this.solidPrice = solidPrice;
		buildSet();
	}
	
	public void buildSet(){
		buildSetThread = new BuildSetThread(this);
		buildSetThread.execute((Void[])null);
	}
	
	public String getWhereStr(){
		StringBuilder result = new StringBuilder();
		
		for(Filter f : filters) {
			String where = f.getWhereStr();
			if( where.length() > 0 ) {
				if(result.length() != 0)
					result.append(" AND ");
				result.append('(').append(where).append(')');
			}
		}
		
		return result.toString(); 
	}
	
	public boolean inset(long rowid) {
		for(Filter f : filters)
			if( !f.inset(rowid))
				return false;
		
		return true;
	}
	
	protected GroupNode createFoldersTreeNode(GroupNode parent) {
		return new GroupNode(this, parent);
	}

	@Override
	public TreeNode createTreeNode(TreeNode parent, long priceRowId,
			String name) {
		return new Node(root, priceRowId, name);
	}
	
	public void setOnChangeListener(OnChangeListener listener){
		onChangeListener = listener;
	}
	
	protected void fireStartBuildSet(){
		refreshing = true;
		if (onChangeListener != null)
			onChangeListener.startBuildSet(this);
	}
	
	protected void fireSetSelection(int pos){
		if (onChangeListener != null)
			onChangeListener.setSelection(pos);
	}
	
	protected void fireEndBuildSet(final int folderid){
		refreshing = false;
		setFolder(folderid);
		
		if(onChangeListener != null)
			onChangeListener.endBuildSet(this);
	}
	
	public void close(){
		if (buildSetThread != null)
			buildSetThread.cancel(true);
	}

	public void onClick(int pos){
		TreeNode node = (TreeNode) getItem(pos);
		
		if(node != null){
			node.open();
			
			if (node.isFolderNode())
				folderTop = (GroupNode) node;
			else
				napoleon.editItem(node.getRowid());
			
			fireDataSetChanged();
		}
	}
	
	public boolean findFolder(int folderId) {
		GroupNode n = root.findFolder(folderId);
		if( n != null ) {
			folderTop = n;
			return true;
		}
		return false;
	}
	
	public void setFolder(int folderid){
		if( findFolder(folderid) ){
			folderTop.reload();
		} else
			folderTop = root;
		
		fireDataSetChanged();
		
	}
	
	public void fireDataSetChanged(){
		notifyDataSetChanged();
		
		if (onChangeListener != null)
			onChangeListener.onAdapterChange(this);
	}
	
	public boolean isTop(){
		return folderTop == root;
	}
	
	public GroupNode getFolderTop() { return folderTop; } 

	public CharSequence getTitle() {
		return isTop() ? napoleon.getString(R.string.price) : folderTop.toString();
	}
	
	public void upLevel(){
		if(!isTop()){
			TreeNode parent = folderTop.getParent();
			int pos = 0;
			
			if (folderTop != null && parent != null)
				pos = parent.indexOf(folderTop);
			
			folderTop = (GroupNode) (parent == null ? root : parent);
			fireDataSetChanged();
			fireSetSelection(pos);
		}
	}
	
	public void nextFolder(){}
	public void prevFolder(){}
	
	public Filter getFilter(String name){
		Filter result = null;
		
		for(Filter f : filters)
			if(f.getName().equals(name)){
				result = f;
				break;
			}
		
		return result;
	}
	
	public boolean isExpanded(){
		return solidPrice;
	}
	
	public void expandSwitch(){
		folderTop = root;
		solidPrice = !solidPrice;
		buildSet();
	}
	
	public void putFilter(Filter filter){
		deleteFilter(filter.getName());
		filters.add(filter);
	}
	
	public void deleteFilter(String name){
		for(Filter f : filters)
			if(f.getName().equals(name)){
				filters.remove(f);
				break;
			}
	}
	
	public String filterNames() {
		String f = getName();
		for(Filter flt : filters)
			f += flt.getName();
		
		return f;
	}
	
	public static void resetCache() {
		globalPrice = null;
		globalRoot = null;
		filterData = "";
	}
	
	protected void fillNodeIds(SQLiteDatabase database) {
		try{
			fprice.clear();
			String priceTable = DataObjectInfo.getInstance().getTableName(Org.class);
			
			if(DbWriter.isTableExists(priceTable)){
				SQLiteQueryBuilder fOrgQuery = new SQLiteQueryBuilder();
				fOrgQuery.setDistinct(true);
				fOrgQuery.setTables(priceTable);
				
				Cursor cursor = fOrgQuery.query(database, new String[] {"[group]", 
						"rowid", "name"}, getWhereStr(), null, null, null, null);
				
				if (cursor.moveToFirst()) {
					try{
						do{
							long rowid = cursor.getLong(1);
							
							if( !inset( rowid ) )
								continue;
							
							int folderid = cursor.getInt(0);
							
							if(!fprice.containsKey(folderid))
								fprice.put(folderid, new ArrayList<GroupInfo>());
							
							GroupInfo pi = new GroupInfo(rowid, cursor.getString(2));
							fprice.get(folderid).add(pi);
						} while(cursor.moveToNext());
					} finally { 
						cursor.close();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void fillTree(SQLiteDatabase database) {
		try{
			SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
			String folderTable = DataObjectInfo.getInstance().getTableName(OrgGroup.class);
			
			queryBuilder.setTables(folderTable);
			Cursor folders = queryBuilder.query(database, 
					new String[] {"name", "level", "id"}, null, null, null, null, "id");
			
			try{
				if (folders.moveToFirst()){
					root.getChilds().clear();
					makeTree(folders,root);
					sortFullTree(root);
				}
				
			} finally {
				folders.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void makeTree(Cursor cursor, GroupNode parent)
	{
		GroupNode curNode = parent;
		GroupNode curParent = parent;
		
		do
		{
			GroupNode node = createFoldersTreeNode(parent);
			node.id = cursor.getInt(2);
			node.level = cursor.getInt(1);
			node.name = cursor.getString(0);
			node.setLeaf(fprice.containsKey(node.id));
			
			if(node.level == curNode.level)
			{
				curParent.insert(node);
			}
			else if (node.level > curNode.level)
			{
				curNode.insert(node);
				curParent = curNode;
			}
			else
			{
				while(node.level < curNode.level && curNode != parent)
					curNode = (GroupNode) curNode.getParent();
				
				if( node.level > curNode.level )
					curParent = curNode;
				else
					curParent = (curNode == parent) ? curNode : (GroupNode) curNode.getParent();
				curParent.insert(node);
			}
			
			curNode = node;
		}
		while(cursor.moveToNext());
	}
	
	private void sortFullTree(TreeNode node){
		Collections.sort(node.getChilds(), new TreeNodeCmp());

		for (TreeNode child: node.getChilds())
			if (child.getChilds().size() > 0)
				sortFullTree(child);
	}
	
	public void buldProcess(AsyncTask<?, ?, ?> task) {
		String fd = filterNames(); 
		if( fd.compareTo(filterData) != 0 ) {
			resetCache();
			filterData = fd;
		}
		
		SQLiteDatabase database = DataBaseManager.getDataBase();
		
		if (!task.isCancelled()){
			if(!solidPrice) {
				if( globalPrice == null ) {
					fillNodeIds(database);
					globalPrice = fprice;
				} else
					fprice = globalPrice;
				
				if( globalRoot == null ) {
					fillTree(database);
					globalRoot = root;
				} else
					root = globalRoot;
			} else {
				fillNodeIds(database);
				
				if(folderTop.getChilds().size() == 0)
					fillTree(database);
				
				priceTop = createFoldersTreeNode(null);
				Collection<ArrayList<GroupInfo>> piListList = fprice.values();
				
				for(ArrayList<GroupInfo> val : piListList)
					for(GroupInfo pi : val){
							priceTop.getChilds().add(createTreeNode(priceTop, pi.rowid, 
									pi.name));
					}
				
				napoleon.sortingPriceList(priceTop.getChilds());
			}
		}
		
	}
	
	public String getFoldersIds() {
		if(!isTop() && folderTop instanceof GroupNode)
			return ((GroupNode)folderTop).getFoldersIds();
		else
			return "";
	}
	
	public List<GroupInfo> getPriceInfo(int folderid){
		if (fprice.containsKey(folderid))
			return fprice.get(folderid);
		
		return null;
	}

	public void setExpanded(boolean expand) {
		solidPrice = expand;
	}

	public void copyFilters(OrgGroupAdapter adapter) {
		filters.clear();
		filters.addAll(adapter.filters);
	}

	@Override
	public void applyFilter(String value) {
		if (value.trim().length() > 0 ){
			OrgTextFilter filter = (OrgTextFilter) getFilter(OrgTextFilter.NAME);
			
			if (filter == null){
				filter = new OrgTextFilter();
				putFilter(filter);
			}
			
			setExpanded(napoleon.isExpanded());
			filter.build(this, value);
			buildSet(true);
		} else 
			resetFilter();
	}

	@Override
	public void resetFilter() {
		Filter filter = getFilter(OrgTextFilter.NAME);
		
		if (filter != null){
			filters.remove(filter);
			buildSet(napoleon.isExpanded());
		}
	}
}

class BuildSetThread extends AsyncTask<Void, Void, Integer>{
	private OrgGroupAdapter adapter;
	
	public BuildSetThread(OrgGroupAdapter adapter){
		this.adapter = adapter;
	}
	
	@Override
	protected void onPreExecute() {
		adapter.fireStartBuildSet();
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		adapter.fireEndBuildSet(result);
	}
	
	@Override
	protected Integer doInBackground(Void... params) {
		int result = adapter.folderTop.id;
		adapter.buldProcess(this);
		return result;
	}
}

class OrgTextFilter extends Filter {
	public static String NAME = "OrgTextFilter"; 
	
	public OrgTextFilter() {
		super(NAME);
	}

	private void collectFolderID(TreeNode node, List<Integer> fids) {
		addFolderId(node, fids);			
		
		for (TreeNode child: node.getChilds()) {
			if (child.hasChilds())
				collectFolderID(child, fids);
			else
				addFolderId(child, fids);
		}
	}
	
	private void addFolderId(TreeNode node, List<Integer> fids) {
		if (node instanceof FolderTreeNode) {
			FolderTreeNode ftn = (FolderTreeNode)node;
			
			if (!fids.contains(ftn.id))
				fids.add(ftn.id);
		}	
	}
	
	public void build(OrgGroupAdapter adapter, String cond){
		StringBuilder sbWhere = new StringBuilder();
		
		if (cond.trim().length() > 0){
			ArrayList<Integer> fids = new ArrayList<Integer>();
			makeSearchStr(cond, sbWhere);
			
			if(!adapter.isExpanded()){
				collectFolderID(adapter.getFolderTop(), fids);
			
				if (fids.size() > 0){
					String fidsBeforeStr = fids.toString();
					String fidsStr =  fidsBeforeStr.substring(1, fidsBeforeStr.length()-1);
					if( sbWhere.length() > 0 )
						sbWhere.append(" AND ");
					sbWhere.append("folderid IN (").append(fidsStr).append(")");
				}
			}
		}	
		
		where = sbWhere.toString();
	}

	protected void makeSearchStr(String cond, StringBuilder sbWhere) {
		cond = cond.replace ("|", "||").replace("_", "|_");
		sbWhere.append("(").append("srchName LIKE '%").append(cond.toUpperCase()).append("%' ESCAPE '|' )");
	}
}