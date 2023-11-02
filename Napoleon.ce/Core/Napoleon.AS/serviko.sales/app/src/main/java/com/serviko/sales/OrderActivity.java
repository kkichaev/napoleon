package com.serviko.sales;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.serviko.dataobjects.Order;
import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;
import com.serviko.dataobjects.ws.ReqCodeParam;
import com.serviko.dataobjects.ws.ReqOrdersParam;
import com.serviko.dataobjects.ws.ReqOrdersResult;
import com.serviko.dataobjects.ws.WSExchange;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderActivity extends BaseActivityOld implements SwipeRefreshLayout.OnRefreshListener {
    Adapter adapter;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    public static void open(Context context) {
        Intent i = new Intent(context, OrderActivity.class);
        context.startActivity(i);
    }

    @Override protected int getLayoutID() { return R.layout.order_activity; }
    @Override protected int getBottomMenuID() { return 0; } // R.id.itOrder; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((SwipeRefreshLayout)findViewById(R.id.swpRefresh)).setOnRefreshListener(this);
        findViewById(R.id.btnPrice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Partner cp = PartnerList.getCurrent();
                if(cp == null) {
                    Toast.makeText(OrderActivity.this, getString(R.string.select_partner), Toast.LENGTH_LONG).show();
                    return;
                }

                PriceCatalog.open(OrderActivity.this);
            }
        });

        findViewById(R.id.btnActive).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openState(Order.ORDER_STATE_ACTIVE); }
        });

        findViewById(R.id.btnComplete).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openState(Order.ORDER_STATE_DONE); }
        });

        findViewById(R.id.btnUnpayed).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { openState(Order.ORDER_STATE_DEBT); }
        });

        adapter = new Adapter();
        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Order o = (Order) adapter.getItem(position);
                OrderDetailActivity.open(OrderActivity.this, o);
            }
        });

        openState(Order.ORDER_STATE_ACTIVE);
    }

    void openState(int state) {
        int vsbl = 0, hdn = 0;

        adapter.refresh(state);

        if(adapter.getCount() == 0) {
            vsbl = R.id.llEmpty;
            hdn = R.id.swpRefresh;
        } else {
            hdn = R.id.llEmpty;
            vsbl = R.id.swpRefresh;
        }

        findViewById(vsbl).setVisibility(View.VISIBLE);
        findViewById(hdn).setVisibility(View.GONE);
    }

    @Override
    public void onRefresh() {
        WSExchange ws = new WSExchange(this);
        ReqOrdersParam prm = new ReqOrdersParam();
        ReqCodeParam rp =  MainActivityOld.getProgParams();
        prm.appId = rp.appId;
        prm.orgId = PartnerList.getCurrent().id;

        ws.setHandler(new WSExchange.Events() {
            @Override
            public void error(Exception e) { }

            @Override
            public void complete(boolean result, Object response, WSExchange exchange) {
                runOnUiThread(new Runnable() {
                    @Override public void run() { ((SwipeRefreshLayout)findViewById(R.id.swpRefresh)).setRefreshing(false); }
                });
                if(result) {
                    ReqOrdersResult res = (ReqOrdersResult) response;
                    if(res.result) {
                        PartnerList.getCurrent().setOrders(res.orders);
                        runOnUiThread(new Runnable() {
                            @Override public void run() { openState(adapter.getCurState()); }
                        });

                    }
                }
            }
        });
        ws.reqOrders(prm);
    }

    static class OrderCmp implements Comparator<Order> {
        boolean reverse;
        public OrderCmp(boolean reverse) {
            this.reverse = reverse;
        }

        @Override
        public int compare(Order o1, Order o2) {
            return reverse ? o2.orderDate.compareTo(o1.orderDate) : o1.orderDate.compareTo(o2.orderDate);
        }
    }

    class Adapter extends BaseAdapter {

        int curState = Order.ORDER_STATE_ACTIVE;
        List<Order> orders = new ArrayList<>();

        public void refresh(int state) {
            orders.clear();
            if(PartnerList.getCurrent() != null) {
                for (Order o : PartnerList.getCurrent().orders) {
                    if(o.inState(state))
                        orders.add(o);
                }
            }

            curState = state;
            Collections.sort(orders, new OrderCmp(state != Order.ORDER_STATE_DEBT));
            notifyDataSetChanged();
        }

        public int getCurState() { return curState; }

        @Override public int getCount() { return orders.size(); }
        @Override public Object getItem(int position) { return orders.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null)
                view = View.inflate(OrderActivity.this, R.layout.order_row_old, null);

            Order o = (Order) getItem(position);
            TextView tv;
            String text;

            text = o.number + "<br/>" + sdf.format(o.orderDate);
            tv = view.findViewById(R.id.tvDoc);
            tv.setText(Html.fromHtml(text));

            text = "����. " + sdf.format(o.deliveryDate);
            tv = view.findViewById(R.id.tvDate);
            tv.setText(Html.fromHtml(text));

            text = String.format("%.2f &#x20bd", o.sumFact());
            if(curState != Order.ORDER_STATE_DEBT) {
                text += "<br/>" + o.status;
            }
            tv = view.findViewById(R.id.tvInfo);
            tv.setText(Html.fromHtml(text));

            return view;
        }
    }
}