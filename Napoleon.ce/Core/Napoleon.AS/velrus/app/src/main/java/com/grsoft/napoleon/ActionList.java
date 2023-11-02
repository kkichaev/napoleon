package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.dataobjects.BonusItem;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class ActionList extends Activity {
    OrderImpl doc = new OrderImpl();
    ListView list;

    public static void open(Context c, long rowid){
        Intent i = new Intent(c, ActionList.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
        c.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionlist);

        list = findViewById(R.id.list);

        doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
        doc.close();

        list.setAdapter(new ActionListAdapter());
    }

    class ActionListAdapter extends BaseAdapter{
        private PriceImpl price = new PriceImpl();
        @Override
        public int getCount() {
            return (((OrderEx)doc.getData()).bonus).size();
        }

        @Override
        public Object getItem(int position) {
            return (((OrderEx)doc.getData()).bonus).get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(ActionList.this, R.layout.actionlist_row, null);

            BonusItem bonus = (BonusItem) getItem(position);
            price.read("id", bonus.bonusID);

            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(price.getData().name);

            tv = convertView.findViewById(R.id.tvQty);
            tv.setText(Util.IntToScaleStr(bonus.qty, Consts.QTY_SCALE));

            return convertView;
        }
    }
}
