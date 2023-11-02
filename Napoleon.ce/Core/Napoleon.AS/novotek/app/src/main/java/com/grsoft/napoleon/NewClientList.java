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

import com.grsoft.dataobjects.NewClient;
import com.grsoft.dataobjects.impl.NewClientImpl;
import com.grsoft.napoleon.documents.DocList;
import com.grsoft.util.ExtrasConst;

public class NewClientList extends Activity {
    public static void open(Context context){
        Intent i = new Intent(context, NewClientList.class);
        context.startActivity(i);
    }

    ListView list;
    Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_client_list);

        list = findViewById(R.id.list);
        findViewById(R.id.btnNewDoc).setOnClickListener((v)->newDoc());


        adapter = new Adapter(this);
        list.setAdapter(adapter);
        list.setOnItemClickListener((p,v,x,i)->((NewClientImpl)p.getItemAtPosition(x)).open(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh();
    }

    private void newDoc() {
        NewClientEdit.open(this, ExtrasConst.INVALID_ROWID);
    }

    static class Adapter extends BaseAdapter {
        Context context;
        DocList data = new DocList();

        public Adapter(Context context){
            this.context = context;
        }

        public void refresh(){
            data = new DocList(NewClientImpl.class,"", "created DESC");
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return data.getCount();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = View.inflate(context, R.layout.client_row, null);

            NewClientImpl cli = (NewClientImpl)getItem(position);
            NewClient cl = cli.getData();

            TextView tv = convertView.findViewById(R.id.tvName);
            tv.setText(cl.name);

            tv = convertView.findViewById(R.id.tvAddress);
            tv.setText(cl.address);

            tv = convertView.findViewById(R.id.tvStatus);
            tv.setText(cli.getDescription(context));

            convertView.setBackgroundResource((position % 2) != 0 ? R.drawable.even_row_selector
                    : R.drawable.list_selector);

            return convertView;
        }
    }
}
