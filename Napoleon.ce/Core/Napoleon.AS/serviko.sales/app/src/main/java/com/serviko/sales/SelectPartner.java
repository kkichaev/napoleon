package com.serviko.sales;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.serviko.dataobjects.Partner;
import com.serviko.dataobjects.PartnerList;

public class SelectPartner extends AppCompatActivity {

    public static int REQ_CODE = R.id.select_partner;

    Partner cursel;
    public static boolean open(Activity context, boolean showList) {
        if(!showList && PartnerList.partners().size() == 1) {
            PartnerList.setCurrent(PartnerList.partners().get(0));
            return false;
        }

        Intent i = new Intent(context, SelectPartner.class);
        context.startActivityForResult(i, REQ_CODE);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_partner);

        cursel = PartnerList.getCurrent();

        ListView lv = findViewById(R.id.lvItems);
        final Adapter a = new Adapter();
        lv.setAdapter(a);
        lv.setOnItemClickListener((adapterView, view, i, l) -> {
            cursel = (Partner) a.getItem(i);
            a.notifyDataSetChanged();
        });

        findViewById(R.id.doButton).setOnClickListener(v -> {
            if(cursel != null) {
                PartnerList.setCurrent(cursel);

                setResult(RESULT_OK, null);
                finish();
            } else {
                Toast.makeText(SelectPartner.this, "Выберите магазин", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return PartnerList.partners().size();
        }

        @Override
        public Object getItem(int i) {
            return PartnerList.partners().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(SelectPartner.this, R.layout.select_partner_row, null);
            }
            Partner p = (Partner) getItem(i);
            TextView tv;
            tv = view.findViewById(R.id.tvName);
            tv.setText(p.name);
            tv = view.findViewById(R.id.tvAddress);
            tv.setText(p.address);

            ImageView iv = view.findViewById(R.id.ivOrgSel);
            iv.setImageResource(cursel == p ? R.drawable.ic_org_dot : R.drawable.ic_org_no_dot);
            return view;
        }
    }
}
