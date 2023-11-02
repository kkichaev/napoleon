package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DanaAction;
import com.grsoft.dataobjects.OrderAction;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionAdapter extends BaseAdapter {
    List<DanaAction> actions = new ArrayList<>();
    Context context;
    public ActionAdapter(Context context) {
        this.context = context;
    }

    public void refresh(OrderEx doc) {
        Map<Object, DanaAction> adic = DbReader.fetchDic(DanaAction.class, "id");
        actions.clear();
        for(OrderAction oa : doc.actions) {
            DanaAction da = adic.get(oa.id);
            if(da != null) {
                actions.add(da);
            }
        }
        notifyDataSetChanged();
    }

    @Override public int getCount() {  return actions.size();  }
    @Override public Object getItem(int i) {  return actions.get(i); }
    @Override public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = View.inflate(context, R.layout.action_order_row, null);
        }

        DanaAction da = (DanaAction) getItem(i);
        TextView tv = view.findViewById(R.id.tvName);
        tv.setText(da.name);
        return view;
    }
}
