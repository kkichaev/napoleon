package com.ashberrysoft.leadertask.modern.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData;
import com.ashberrysoft.leadertask.data_providers.DbHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmailsAdapter extends BaseAdapter {
    private Context context;
    private List<Data> data = new ArrayList<Data>();
    public Set<String> checked = new HashSet<String>();

    private static class Data{
        public String email = "";
        public String name = "";
    }

    public EmailsAdapter(Context context){
        this.context = context;

        loadData();
    }

    private void loadData() {
        data.clear();

        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        Cursor c = null;

        String cu = LTSettings.getInstance().getUserName();

        try {
            c = db.query(LeaderTaskProviderMetaData.EmployeeContract.TABLE_NAME, new String[]{LeaderTaskProviderMetaData.EmployeeContract.NAME,
                    LeaderTaskProviderMetaData.EmployeeContract.EMAIL}, null, null, null, null, LeaderTaskProviderMetaData.EmployeeContract.NAME);

            while(c.moveToNext()){
                Data d = new Data();
                d.email = c.getString(c.getColumnIndex(LeaderTaskProviderMetaData.EmployeeContract.EMAIL));
                d.name = c.getString(c.getColumnIndex(LeaderTaskProviderMetaData.EmployeeContract.NAME));

                if (d.email.equals(cu))
                    continue;

                data.add(d);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (c != null)
                c.close();
        }
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
        if (view == null)
            view = View.inflate(context, R.layout.list_item_sliding_menu_for_emails_dialog, null);

        Data d = (Data) getItem(position);

        TextView tv = view.findViewById(R.id.txt_title);
        tv.setText(d.name);

        CheckBox cb = view.findViewById(R.id.checkbox);
        cb.setChecked(checked.contains(d.email));

        return view;
    }

    public void setChecked(int position) {
        Data d = (Data) getItem(position);
        if (checked.contains(d.email))
            checked.remove(d.email);
        else
            checked.add(d.email);
    }
}
