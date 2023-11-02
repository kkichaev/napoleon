package com.grsoft.napoleon;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.grsoft.database.PriceTreeNode;
import com.grsoft.dataobjects.DanaAction;
import com.grsoft.dataobjects.DanaActionItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.util.Filter;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.ZeroPositionFilter;

import java.util.HashSet;
import java.util.Set;

public class WarehouseEx extends Warehouse {
    static int whIndex = 0;

    PriceImpl pi = new PriceImpl();
    Set<String> actions = new HashSet<>();

    @Override
    protected BaseAdapter createListAdapter() {
        int newIndex = 0;
        ProjectFilter pf = null;
        CostFilter cs = null;
        AlcoFilter af = null;

        if( document instanceof OrderImpl) {
            OrderEx oe = (OrderEx)document.getData();
            newIndex = oe.whIndex;
            pf = new ProjectFilter(oe.project);
            //cs = new CostFilter(document, pi);

//            OrgImpl oi = new OrgImpl();
//            oi.read("id", oe.id);
//            if(((OrgEx)oi.getData()).canBuyAlco == 0) {
//                af = new AlcoFilter();
//            }
        }
        if(whIndex != newIndex) {
            whIndex = newIndex;
            FoldersAdapter.resetCache();
        }

        FoldersAdapter ret = (FoldersAdapter) super.createListAdapter();
        if(pf != null)
            ret.putFilter(pf);
        if(cs != null)
            ret.putFilter(cs);
        if(af != null)
            ret.putFilter(af);
        return ret;
    }

    @Override
    public void afterBuildSet() {
        super.afterBuildSet();

        if(document instanceof OrderImpl) {
            OrgImpl oi = new OrgImpl();
            oi.read("id", document.getId());
            for (DanaAction da : DanaAction.active(true, (OrgEx)oi.getData())) {
                for (DanaActionItem di : da.items) {
                    actions.add(di.id);
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pi.close();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.priceitemrowex;
    }

    @Override
    public View getPriceView(PriceTreeNode node, View convertView) {
        View v = super.getPriceView(node, convertView);
        int res = R.drawable.empty;
        if(document instanceof OrderImpl) {
            if(actions.contains(price.getData().id)) {
                res = R.drawable.action;
            }
        }
        ((ImageView)v.findViewById(R.id.iAction)).setImageResource(res);
        return v;
    }}

class CostFilter extends Filter {
    static final String NAME = "CostFilter";
    CostStrategy cs;
    PriceImpl pi;
    Document doc;

    public CostFilter(Document doc, PriceImpl pi) {
        super(NAME);
        cs = CostStrategy.getInstance((Class<? extends Document<?>>) doc.getClass());
        this.pi = pi;
        this.doc = doc;
    }

    @Override
    public boolean inset(long priceRowID, String id) {
        Price p = pi.getData();
        p.id = id;
        pi.read();
        return cs.getItemCost(p, doc) > 0;
    }
}

class ProjectFilter extends Filter {
    static public String NAME = "Project";
    String project = "";

    public ProjectFilter(String project) {
        super(NAME);
        this.project = project;
    }

    @Override
    public String getWhereStr() {
        return project == null ||project.length() == 0 ?  "" : "project='" + project + "'";
    }
}

class AlcoFilter extends Filter {
    static public String NAME = "AlcoFilter";

    public AlcoFilter() {
        super(NAME);
        where = "alcoLicense = 0";
    }
}

