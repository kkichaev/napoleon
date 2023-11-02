package com.grsoft.napoleon;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Brand;
import com.grsoft.database.DbReader;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.PriceWh;
import com.grsoft.dataobjects.Supplier;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.RemnantsEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.dataobjects.impl.RemnantsImplEx;
import com.grsoft.napoleon.documents.DeliveryDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.CfgNplW;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.WarehouseManager;
import com.grsoft.util.ZeroPositionFilter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WarehouseEx extends Warehouse  {
    WhFilter filter = new WhFilter();

    @Override
    protected int getLayoutId() { return R.layout.warehousex; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (DocType.getCurDoc() == RemnantsDoc.instance()) {
            btnFind.setVisibility(View.GONE);
        } else {
            btnFind.setVisibility(View.VISIBLE);
            btnFind.setOnClickListener(v -> openFilter());
        }
    }

    void openFilter() {
        filter.show(this, (clear) -> {
            boolean expand = true;
            if(clear) {
                adapter.deleteFilter(filter.getName());
                expand = isPriceExpand();
            } else {
                if (adapter.getFilter(filter.getName()) == null)
                    adapter.putFilter(filter);
                adapter.setExpanded(true);
            }
            adapter.buildSet(expand);
        });
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(DocType.getCurDoc() == RemnantsDoc.instance()) {
            refreshDisplayPercent();
        }
    }

    @Override
    protected void readDocument() {
        super.readDocument();
        boolean isStock = (document instanceof RemnantsImplEx);

        findViewById(R.id.llDisplay).setVisibility(isStock ? View.VISIBLE : View.GONE);
        if(isStock) {
            int prevDisplay = ((RemnantsImplEx)document).prevDisplay();

            ((TextView)findViewById(R.id.tvPrevDisplay)).setText(Integer.toString(prevDisplay));
            refreshDisplayPercent();
        }
    }

//    @Override
//    protected void createDocument() {
//        document = DocType.getCurDoc().create();
//        if (!(document instanceof Itemsable))
//            document = RemnantsDoc.instance().create();
//    }


    public void refreshDisplayPercent() {
        int ac = ((RemnantsImplEx)document).countAikos();
        RemnantsEx d = (RemnantsEx) document.getData();
        String pc = (d.display == 0) ? "0" : Integer.toString(ac * 100 / d.display);
        ((TextView)findViewById(R.id.tvPrcDisplay)).setText(pc + "%");
        ((TextView)findViewById(R.id.edCurDisplay)).setText(Integer.toString(d.display));
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        if (DocType.getCurDoc() == RemnantsDoc.instance()) {
            PriceEx p = new PriceEx();
            p.id = node.getId();
            p.name = node.getName();

            View view;
            int id = getItemLayoutId();
            if (convertView != null && convertView.getTag(id) != null)
                view = convertView;
            else {
                view = View.inflate(this, id, null);
                view.setTag(id, true);
            }

            setName(view, p, 1, node);
            TextView tvClmn1 = (TextView) view.findViewById(R.id.tvClmn1);
            TextView tvClmn2 = (TextView) view.findViewById(R.id.tvClmn2);

            WindowManager wm = (WindowManager) view.getContext().getSystemService(
                    Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);

            int cellWidth = calcCellWidth(metrics);

            tvClmn1.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
            tvClmn2.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);

            LinearLayout llQuant = (LinearLayout) view.findViewById(R.id.llQuant);
            llQuant.setVisibility(inItemSelectMode ? View.GONE : View.VISIBLE);
            layoutColumns(tvClmn1, tvClmn2, cellWidth, llQuant);

            CfgNplW config = (CfgNplW) ConfigManager.getConfig();

            setTextColumnValue(tvClmn1, COLUMN_QTY_ORD, p);
            setTextColumnValue(tvClmn2, COLUMN_NONE, p);

            return view;
        }
        return super.getPriceView(node, convertView);
    }

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter.resetCache();
        if (DocType.getCurDoc() == RemnantsDoc.instance())
            return new RemnantsAdapter(this);
        if (DocType.getCurDoc() == ReturnDoc.instance()) {
            return new ReturnAdapter(this);
        }
        return new FoldersAdapterEx(this);
    }

    class ReturnAdapter extends FoldersAdapter {

        HashSet<String> ids = new HashSet<String>();

        public ReturnAdapter(WarehouseManager warehouse) {
            super(warehouse);

            String orgId = document.getId();
            com.grsoft.napoleon.documents.DocList dl = DeliveryDoc.instance().docList(orgId);
            for(Document<?> d : dl) {
                for(DeliveryItem di : ((DeliveryImpl)d).getData().items)
                    ids.add(di.id);
            }

            dl.close();
        }

        @Override public boolean inset(long rowid, String id) { return ids.contains(id); }
    }

    @Override
    protected void updateTotalSum() {
        if(document instanceof RemnantsImpl) {
            findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
        } else
            super.updateTotalSum();
    }

    public void editRemnantItem(String id) {
        if(document instanceof RemnantsImplEx) {
            ((RemnantsImplEx)document).edit(id, this);
        }
    }

    public void onRead(PriceEx item) {
        filter.add(item);
    }

    @Override
    protected Filter createZeroPositionFilter() {
        return new ZFilter();
    }

    static class FoldersAdapterEx extends FoldersAdapter {

        public FoldersAdapterEx(WarehouseEx warehouse) {
            super(warehouse);
        }

        @Override
        protected void fillPriceIds(SQLiteDatabase database) {
            try{
                fprice.clear();

                for(PriceWh pi : DbReader.fetch(PriceWh.class)) {
                    ((WarehouseEx)warehouse).onRead(pi);
                }

                List<PriceWh> src = DbReader.fetch(PriceWh.class, getWhereStr());
                for(PriceWh p : src) {
                    long rowid = p.rowid;
                    String id = p.id;
                    int folderid = p.folderID;

                    if( !inset( rowid, id, folderid ) )
                        continue;

                    if(!fprice.containsKey(folderid))
                        fprice.put(folderid, new ArrayList<PriceInfo>());

                    PriceInfo pi = new PriceInfo(rowid, p.name, id);
                    fprice.get(folderid).add(pi);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    static class RemnantsAdapter extends FoldersAdapter {
        public RemnantsAdapter(WarehouseManager warehouse) {
            super(warehouse);
        }

        @Override
        protected void fillPriceIds(SQLiteDatabase database) {
            super.fillPriceIds(database);
        }

        @Override
        protected String getFolderTableName() {
            return new Brand().getTableName();
        }

        @Override
        public void onClick(int pos) {
            TreeNode node = (TreeNode) getItem(pos);
            if(node instanceof PriceTreeNode) {
                ((WarehouseEx)warehouse).editRemnantItem(((PriceTreeNode) node).getId());
                return;
            }
            super.onClick(pos);
        }

        @Override
        public synchronized void buldProcess(AsyncTask<?, ?, ?> task) {
            root.getChilds().clear();
            fprice.clear();

            List<Supplier> supl = DbReader.fetch(Supplier.class, "", "pos");
            List<Brand> brands = DbReader.fetch(Brand.class, "", "pos");
            int ci = 1;
            for(Brand b : brands) {
                FolderTreeNode ftn = createFoldersTreeNode(root);
                ftn.level = 1;
                ftn.id = b.pos + 1;
                ftn.name = b.name;
                ftn.setLeaf(true);

                ArrayList<PriceInfo> items = new ArrayList<>();
                for(Supplier spl : supl) {
                    String id = b.id + '\t' + spl.id;
                    items.add(new PriceInfo(ci++, spl.name, id));
                }
                fprice.put(ftn.id, items);

                root.insert(ftn);
            }
            warehouse.afterBuildSet();
        }
    }

    static class ZFilter extends Filter {
        public ZFilter() {
            super(ZeroPositionFilter.NAME);
        }

        @Override
        public String getWhereStr() {
            return "(qty > 0 or (whStates <> 0))";
        }
    }
}
