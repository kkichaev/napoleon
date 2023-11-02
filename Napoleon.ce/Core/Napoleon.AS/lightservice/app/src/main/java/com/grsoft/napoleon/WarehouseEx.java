package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PricePhotoHitching;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.ReportHitching;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.MasterOrder;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.PhotoLoaderParam;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImplEx;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.UpdateProcess;
import com.grsoft.util.Consts;
import com.grsoft.util.Filter;
import com.grsoft.util.FolderTree;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.List;

public class WarehouseEx extends Warehouse {
    List<PriceEx> analogs = new ArrayList<>();

    final int ANALOG_DIALOG = 0x4352;
    static int whIndex = 0;
    public static String masterOrder = "";
    View llMatrixOrder, ibNextPrice;

    @Override
    protected int getLayoutId() {
        return R.layout.warehouseex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        llMatrixOrder = findViewById(R.id.llMatrixOrder);
        ibNextPrice = findViewById(R.id.ibNextPrice);
        ibNextPrice.setOnClickListener((v)->{
            removeFilterMasterOrder();});

        linesController.setMinLines(3);
        findViewById(R.id.btnLines).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                linesController.setVariable();
                return true;
            }
        });
    }

    private void removeFilterMasterOrder() {
        llMatrixOrder.setVisibility(View.GONE);
        adapter.deleteFilter(MASTER_ORDER_FILTER);
        adapter.buildSet();
    }

    @Override protected int getItemLayoutId() { return R.layout.priceiterowex; }

    @Override
    protected void updateChildPriceView(View view, Price p) {
        super.updateChildPriceView(view, p);
        TextView tv = view.findViewById(R.id.tvOrderQty);
        int qty = 0;
        if(document instanceof OrderImpl) {
            qty = ((OrderImpl)document).getItemQty(p);
        }
        String text = qty == 0 ? "" : Util.IntToScaleStr(qty, Consts.QTY_SCALE);
        tv.setText(text);
    }

    @Override
    public void setColor(TextView textView, Price price) {
        super.setColor(textView, price);
        int bk = ((PriceEx)price).clrbak;
        if(bk != 0) {
            bk = Color.rgb( (bk & 0x00ff0000) >> 16,
                    (bk & 0x0000ff00) >> 8,
                    bk & 0x000000ff);
            textView.setBackgroundColor(bk);
        }
    }

    @Override
    protected BaseAdapter createListAdapter() {
        FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
        if(!(document instanceof ReturnImplEx))
            ret.putFilter(new DelistFilter());
        return ret;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(menuInfo instanceof AdapterView.AdapterContextMenuInfo) {
            TreeNode tn = (TreeNode)adapter.getItem(((AdapterView.AdapterContextMenuInfo)menuInfo).position);
            if(tn instanceof PriceTreeNode) {
                getMenuInflater().inflate(R.menu.warehouse_context_menu, menu);
                return;
            }
            if(tn instanceof FolderTreeNode && !(inItemSelectMode && canSelectFolder)) {
                getMenuInflater().inflate(R.menu.warehouse_folder_menu, menu);
                return;
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int iid = item.getItemId();
        if(iid == R.id.itPresentRcv) {
            FolderTreeNode tn = (FolderTreeNode)adapter.getItem(((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position);
            loadPresentation(tn.id);
        } else if(iid == R.id.idAnaloge) {
            PriceTreeNode tn = (PriceTreeNode)adapter.getItem(((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position);
            PriceImpl pi = new PriceImpl();
            PriceEx p = (PriceEx) pi.getData();
            p.id = tn.getId();
            pi.read();
            pi.close();

            analogs.clear();
            if(p.analog.length() > 0) {
                DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>(true) {
                    @Override
                    public boolean travel(DataTraveler<PriceEx> item) {
                        analogs.add(item.data);
                        return true;
                    }
                }, "analog='" + p.analog + "' and id <> '" + p.id + "'");
            }
            if(analogs.size() > 0) {
                showDialog(ANALOG_DIALOG);
            } else {
                Toast.makeText(this, "Аналоги не найдены", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void loadPresentation(int folderId) {
        FolderTree ft = new FolderTree();
        ft.load();
        List<Folder> fa = ft.getWithDescendats(folderId);
        String where = "";
        for(Folder f : fa) {
            if(where.length() > 0) where += ",";
            where += Integer.toString(f.id);
        }
        where = "folderID in (" + where + ")";

        PhotoLoaderParam param = new PhotoLoaderParam();

//        List<String> ids = new ArrayList<>();
        try {
            String stmt = "select id from " + new Price().getTableName() + " where " + where;
            Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
            while(c.moveToNext()) {
                if(param.ids.length() > 0)
                    param.ids += ",";
                param.ids += c.getString(0);
//                ids.add(c.getString(0));
            }
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        UpdateProcess up = new UpdateProcess(this) {
            @Override
            protected void onPreExecute() {
                showDialog(R.id.wait_dlg);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dismissDialog(R.id.wait_dlg);
                if (result) {
                    PresentationFolderW.items.fill(false);
                    openPresentation();
                }
            }
        };


        Config cfg = ConfigManager.getConfig();
        UpdateProcess.Params arg = new UpdateProcess.Params();
        arg.login = cfg.login;
        arg.pass = cfg.passw;
        arg.ip1 = cfg.address;
        arg.ip2 = cfg.address2;
        arg.port1 = cfg.port;

        ReportHitching rh = new ReportHitching("photo_loader", param, new PricePhotoHitching(this, true));
        arg.indata.add(rh);
        up.execute(arg);
    }

    private Dialog createWaitDlg() {
        ProgressDialog dlg = new ProgressDialog(this);
        dlg.setMessage(getString(R.string.please_wait));
        return dlg;
    }

//    @Override
//    protected Filter createZeroPositionFilter() {
//        if( document instanceof OrderImplEx) {
//            if( whIndex != ((OrderEx)document.getData()).whIndex ) {
//                whIndex = ((OrderEx)document.getData()).whIndex;
//                FoldersAdapter.resetCache();
//            }
//        } else if( whIndex != 0 ) {
//            whIndex = 0;
//            FoldersAdapter.resetCache();
//        }
//        return new ZeroFilter();
//    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == R.id.wait_dlg)
            return createWaitDlg();

        if(id == ANALOG_DIALOG) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Выберите аналог");
            final AnalogAdapter aa = new AnalogAdapter(analogs);
            b.setSingleChoiceItems(aa, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(which >= 0) {
                        final PriceEx sel = (PriceEx) aa.getItem(which);
                        runOnUiThread(new Runnable() {
                            @Override public void run() { adapter.setFolder(sel.folderID); }
                        });


                        PriceImpl pi = new PriceImpl();
                        PriceEx p = (PriceEx) pi.getData();
                        p.id = sel.id;
                        pi.read();
                        pi.close();

                        ((Itemsable) document).editItem(pi.getRowid(), WarehouseEx.this);
                    }
                    dialog.dismiss();
                }
            });
            return b.create();
        }
        return super.onCreateDialog(id);
    }

    class AnalogAdapter extends BaseAdapter {

        CostStrategy cs;
        List<PriceEx> analogs;

        public AnalogAdapter(List<PriceEx> analogs) {
            this.analogs = analogs;
            cs = CostStrategy.getInstance((Class<? extends Document<?>>) document.getClass());
        }

        @Override public int getCount() { return analogs.size(); }
        @Override public Object getItem(int position) { return analogs.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = View.inflate(WarehouseEx.this, R.layout.analog_row, null);
            }

            PriceEx p = (PriceEx) getItem(position);
            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(p.name);

            Itemsable idoc = null;
            if(document instanceof Itemsable)
                idoc = (Itemsable)document;

            String text = "";
            tv = view.findViewById(R.id.tvQty);
            if(idoc != null)
                text = Util.IntToScaleStr(idoc.getItemValue(p), Consts.QTY_SCALE);
            tv.setText(text);

            tv = view.findViewById(R.id.tvCost);
            text = Util.IntToScaleStr(cs.getItemCost(p, document), Consts.SUM_SCALE);
            tv.setText(text);
            return view;
        }
    }

//    class ZeroFilter extends ZeroPositionFilter {
//
//        @Override public String getWhereStr() { return ""; }
//
//        @Override
//        public boolean inset(long priceRowID, String id) {
//            if( !(document instanceof Itemsable) )
//                return super.inset(priceRowID, id);
//
//            boolean result = false;
//            if(price.read(priceRowID))
//                result = (((Itemsable)document).getItemValue(price.getData()) > 0);
//            return result;
//        }
//    }

    static class DelistFilter extends Filter {
        public static String NAME = "Delist";

        public DelistFilter() {
            super(NAME);
        }

        @Override
        public String getWhereStr() {
            return "delist=0";
        }
    }

    @Override
    void packetInsert(OrderImplBase<?> o, PriceImpl p, int qty, boolean inPack, CostStrategy cs) {
        super.packetInsert(o, p, qty, inPack, cs);

        OrderItemEx ix = (OrderItemEx) o.findItem(p.getData().id);

        if (ix != null) {
            ix.discount = ((OrderEx) o.getData()).discount;
            o.write();
            o.close();
        }
    }

    private final static String MASTER_ORDER_FILTER = "master_order_filter";

    @Override
    protected void postAdapterInit() {
        if (!editMode) {
            if (masterOrder.length() == 0)
                loadMasterOrder();

            if (masterOrder.length() > 0) {
                adapter.putFilter(new Filter(MASTER_ORDER_FILTER) {
                    @Override
                    public String getWhereStr() {
                        return String.format("id in (%s)", masterOrder);
                    }
                });

                llMatrixOrder = findViewById(R.id.llMatrixOrder);
                llMatrixOrder.setVisibility(View.VISIBLE);
            }
        }

        super.postAdapterInit();
    }

    private void loadMasterOrder() {
        final StringBuilder sb = new StringBuilder();
        DataTraveler.travel(MasterOrder.class, new DataTraveler.Travel<MasterOrder>() {
            @Override
            public boolean travel(DataTraveler<MasterOrder> item) {
                if(sb.length() > 0)
                    sb.append(",");

                sb.append("'").append(item.data.id).append("'");

                return true;
            }
        }, null);

        masterOrder = sb.toString();
    }
}
