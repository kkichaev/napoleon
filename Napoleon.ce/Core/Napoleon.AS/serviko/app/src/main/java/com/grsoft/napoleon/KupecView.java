package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.ActionResult;
import com.grsoft.dataobjects.KupecAction;
import com.grsoft.dataobjects.KupecItem;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PrcTypes;
import com.grsoft.dataobjects.impl.KupecImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.KupecDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class KupecView extends Activity implements SendResultListener {
    KupecImpl doc;
    ExpandableListView list;
    View btnSend;

    public static void open(Context context, String id){
        Intent intent = new Intent(context, KupecView.class);
        intent.putExtra(ExtrasConst.ORG_ID_STR, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.kupec_view);
        list = findViewById(R.id.list);
        btnSend = findViewById(R.id.btnSend);

        OrgImpl org = new OrgImpl();
        org.read("id", getIntent().getStringExtra(ExtrasConst.ORG_ID_STR));

        doc = KupecImpl.getLastDoc(this, org.getData().id, GPSUtilNew.getLastKnownLocation());

        TextView tv = findViewById(R.id.tvOrgInfo);
        tv.setText(org.getData().name);

        list.setAdapter(new Adapter(this,  (OrgEx)org.getData(), doc));

        btnSend.setOnClickListener(this::send);
    }

    private void send(View view) {
        new DocumentSender(this, btnSend,
                KupecDoc.instance().getObjectName(), doc,
                doc.getRowid(), this).execute((Void[])null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (doc.isEmpty()){
            doc.delete();
            doc.close();
        }
    }

    @Override
    public void postSendExecute(boolean result) {
        if (result) finish();
    }

    static class Adapter extends BaseExpandableListAdapter{
        private HashMap<String, List<KupecAction>> data;
        private Context context;
        private List<String> groups = new ArrayList<>();
        private PriceImpl price = new PriceImpl();
        private OrderImpl order = new OrderImpl();
        private KupecImpl doc;

        public Adapter(Context context, OrgEx org, KupecImpl doc){
            this.context= context;
            this.doc = doc;

            OrderEx o = (OrderEx) order.getData();
            o.id = doc.getId();
            o.prcType = org.prcType;
            o.whCode = org.whCode;
            o.firmCode = org.firmCode;

            List<PrcTypes> prc = DbReader.fetch(PrcTypes.class);

            for(int i = 0; i < prc.size(); i++){
                if (o.prcType.equals(prc.get(i).id)){
                    o.sumType = i;
                    break;
                }
            }

            List<KupecAction> actions = DbReader.fetch(KupecAction.class, String.format("regionID='%s'",org.regionID));
            data = new HashMap<>();

            for (KupecAction ka : actions){
                if (price.read("id", ka.id)) {
                    if (!data.containsKey(ka.contract))
                        data.put(ka.contract, new ArrayList<>());

                    data.get(ka.contract).add(ka);
                }
            }

            for (String g : data.keySet()) {
                groups.add(g);

                Collections.sort(data.get(g), (o1, o2) -> o1.promoID.compareTo(o2.promoID));
            }

            Collections.sort(groups);
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            List<?> childs = data.get(groups.get(groupPosition));
            return childs.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            List<?> childs = data.get(groups.get(groupPosition));
            return childs.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
            if (view == null)
               view =  View.inflate(context, R.layout.kupec_view_group_row,null);

            String group = (String) getGroup(groupPosition);
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(group);

            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
            if (view == null)
                view = View.inflate(context, R.layout.kupec_view_child_row,null);


            KupecAction action = (KupecAction) getChild(groupPosition, childPosition);
            price.read("id", action.id);

            ImageView iv = view.findViewById(R.id.ivSelect);
            iv.setTag(action.promoID);
            iv.setOnClickListener(this::selectClick);
            iv.setEnabled(true);

            boolean inDoc = doc.hasID(action.promoID);

            int textColor = context.getResources().getColor(inDoc ? R.color.green : R.color.black );
            TextView tv = view.findViewById(R.id.tvName);
            tv.setText(price.getData().name);

            long cost = getCost();

            tv = view.findViewById(R.id.tvCost);
            tv.setText(Util.IntToScaleStr(cost, Consts.SUM_SCALE));

            tv = view.findViewById(R.id.tvCost2);
            tv.setText(Util.IntToScaleStr(action.price, Consts.SUM_SCALE));

            tv = view.findViewById(R.id.tvCost3);
            tv.setText(Util.IntToScaleStr(action.shelfPrice, Consts.SUM_SCALE));

            tv = view.findViewById(R.id.tvAction);
            tv.setText(action.promoID);

            View header = view.findViewById(R.id.header);
            header.setVisibility(View.VISIBLE);
            boolean vis = true;

            if (childPosition > 0){
                KupecAction prev = (KupecAction) getChild(groupPosition, childPosition - 1);
                if (prev.promoID.equals(action.promoID)) {
                    header.setVisibility(View.GONE);
                    vis = false;
                }
            }

            if (vis){
                iv.setImageResource(inDoc ? R.drawable.btn_check_on : R.drawable.btn_check_off);

                if (checkCostEqual(groupPosition, action.promoID)) {
                    iv.setImageResource(R.drawable.btn_check_disabled);
                    iv.setEnabled(false);
                    textColor = context.getResources().getColor(R.color.grey);
                }
            }else {
                if (checkCostEqual(groupPosition, action.promoID)) {
                    textColor = context.getResources().getColor(R.color.grey);
                }
            }

            tv = view.findViewById(R.id.tvName);
            tv.setTextColor(textColor);
            tv = view.findViewById(R.id.tvCost);
            tv.setTextColor(textColor);
            tv = view.findViewById(R.id.tvCost2);
            tv.setTextColor(textColor);
            tv = view.findViewById(R.id.tvCost3);
            tv.setTextColor(textColor);
            tv = view.findViewById(R.id.tvAction);
            tv.setTextColor(textColor);

            return view;
        }

        private long getCost() {
            long cost = ((CostStrategyEx)CostStrategy.getInstance(order.getClass())).getPriceCost(price.getData(), order);
            ActionResult res = ((CostStrategyEx)CostStrategy.defaultInstance).getOrderItemCost(price.getData(), order);

            if (res != null)
                cost = res.cost;
            return cost;
        }

        private boolean checkCostEqual(int groupPosition, String promoID) {
            for(int x = 0; x < getChildrenCount(groupPosition); x++ ){
                KupecAction a = (KupecAction) getChild(groupPosition, x );
                if (a.promoID.equals(promoID)) {
                    price.read("id", a.id);
                    long cost = getCost();

                    if (cost == a.price)
                        return true;
                }
            }

            return false;
        }

        private void selectClick(View view) {
            doc.addID(view.getTag().toString());
            doc.write();
            doc.close();
            notifyDataSetChanged();
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
