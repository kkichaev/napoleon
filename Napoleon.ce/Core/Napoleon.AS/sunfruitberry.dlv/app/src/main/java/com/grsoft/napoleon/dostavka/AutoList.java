package com.grsoft.napoleon.dostavka;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AutoInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.impl.AutoWaybillImpl;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.List;

public class AutoList extends Activity {

    public static void open(Context context){
        Intent intent = new Intent(context, AutoList.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView content = new ListView(this);
        setContentView(content);
        content.setAdapter(new Adapter(this));

        content.setOnItemClickListener((a,v,p,i)->{
            new AutoWaybillImpl().init(this, ((AutoInfo)a.getItemAtPosition(p)).id, GPSUtilNew.getLastKnownLocation());
        });
    }

    private static class Adapter extends BaseAdapter{
        private List<AutoInfo> data = new ArrayList<>();
        private Context context;

        public Adapter(Context context){
            this.context = context;

            DataTraveler.travel(AutoInfo.class, new DataTraveler.Travel<AutoInfo>(true) {
                @Override
                public boolean travel(DataTraveler<AutoInfo> item) {
                    data.add(item.data);
                    return true;
                }
            }, "");
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null){
                TextView tv = new TextView(context, null, 0, R.style.AutoInfoRow);
                view = tv;
            }

            AutoInfo info = (AutoInfo) getItem(position);

            ((TextView)view).setText(info.number);
            ((TextView)view).setTextColor(info.color == 0 ? Color.BLACK : info.color);

            return view;
        }
    }
}
