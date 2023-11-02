package com.grsoft.napoleon;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Incass;
import com.grsoft.dataobjects.Invent;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.SenegOutputDoc;
import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.util.OrgInfoClickListener;
import com.grsoft.util.Consts;
import com.grsoft.util.MenuHandler;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainEx extends Main {

    OrgEx selectedOrg;

    Set<String> todayOrgs = new HashSet<String>();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        View v = findViewById(R.id.btnBack);
        v.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { onBackPressed(); }
        });
        v.setVisibility(View.GONE);

        v = findViewById(R.id.llTop);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedOrg != null) {
                    OrgInfoClickListener oc = new OrgInfoClickListener(selectedOrg, R.layout.org_detail_info_row, null);
                    oc.onClick(v);
                }
            }
        });
    }

    @Override
    protected ArrayList<MenuHandler> createDocMenuList() {
        docMenu = new ArrayList<MenuHandler>();

        docMenu.add(new MenuHandler(getString(R.string.doc_list), new Runnable() {
            @Override public void run() { DocList.open(MainEx.this); }
        }));
        docMenu.add(new MenuHandler(getString(R.string.price_list), new Runnable() {
            @Override public void run() { Warehouse.open(MainEx.this); }
        }));
        docMenu.add(new MenuHandler(getString(R.string.bank), new Runnable() {
            @Override public void run() { Bank.open(MainEx.this); }
        }));
        return docMenu;
    }

    @Override public void updateTotalSum(long sum, int weight) {}
    @Override public void updateTotalSum(long sum, int weight, int count) {  }

    private void updateView() {
        View v = findViewById(R.id.btnBack);
        int vsb = (selectedOrg == null) ? View.GONE : View.VISIBLE;
        v.setVisibility(vsb);

        TextView tv = findViewById(R.id.tvFirstColumnCaption);
        tv.setText(selectedOrg == null ? getString(R.string.caption) : selectedOrg.name);

        ((SolidMainAdapter)solidMainAdapter).resetFilter();
    }

    @Override
    public void onBackPressed() {
        if(selectedOrg == null)
            super.onBackPressed();
        else {
            selectedOrg = null;
            updateView();
        }
    }

    @Override
    protected BaseAdapter createSolidMainAdapter() {
        return new SolidMainAdapterEx(this);
    }

    @Override
    protected void adjustViewForDocType(DocType docType) {
        super.adjustViewForDocType(docType);

        findViewById(R.id.tvMainDocValColTitle).setVisibility(View.GONE);
        setTitle("Сенеж");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date end = calendar.getTime();

        OrgImpl oi = new OrgImpl();
        OrgEx o = (OrgEx) oi.getData();
        String where = "created >= " + Long.toString(now.getTime()) + " and created < " + Long.toString(end.getTime());
        for(CreateDocDataObject doc : new CreateDocDataObject[]{new SenegOutputDoc(), new OrderEx(), new VisitEx(), new ReturnEx(), new VisitEx(), new Invent(), new Incass()}) {
            DbReader r = new DbReader();
            boolean bdo = r.select(doc, doc.getTableName(), where);
            while(bdo) {
                todayOrgs.add(doc.id);
                o.id = doc.id;
                if(oi.read()) {
                    todayOrgs.add(o.ido);
                }
                bdo = r.selectNext(doc);
            }
        }
        oi.close();
    }

    @Override
    protected void drawOrg(Org org, View view) {
        super.drawOrg(org, view);

        TextView tv = view.findViewById(R.id.tvOrgSum);
        if(selectedOrg == null) {
            tv.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setText(Util.IntToScaleStr(((OrgEx)org).balance, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        }
        tv = (TextView)view.findViewById(R.id.tvOrgName);
        int color = (todayOrgs.contains(org.id)) ? view.getResources().getColor(R.color.item_highlight) :
            Util.GrServerColorToSystem(org.color);
        tv.setTextColor(color);

    }

    class SolidMainAdapterEx extends SolidMainAdapter {

        public SolidMainAdapterEx(Main main) {
            super(main);
        }

        @Override
        protected String getWhereStr() {
            String ret = super.getWhereStr();
            if(ret.length() > 0)
                ret += " and ";
            if(selectedOrg == null) {
                ret += "(ido = '' or ido is null)";
            } else {
                ret += "(ido = '" + selectedOrg.id + "')";
            }
            return ret;
        }

        @Override
        public void click(int position) {
            OrgEx org = (OrgEx) getItem(position);
            if(org.ido.length() > 0)
                super.click(position);
            else {
                selectedOrg = org;
                updateView();
            }
        }
    }
}
