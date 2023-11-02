package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.FolderTreeNode;
import com.grsoft.database.PriceTreeNode;
import com.grsoft.database.TreeNode;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OfferImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OfferDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.FolderTree;
import com.grsoft.util.FoldersAdapter;
import com.grsoft.util.TreeNodeFactory;
import com.grsoft.util.WarehouseManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfferEdit extends Activity {

    OfferImpl doc = new OfferImpl();
    Adapter adapter;

    public static void open(Context contex, OfferImpl doc) {
        Intent i = new Intent(contex, OfferEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        contex.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offer_edit);

        OrgImpl org = new OrgImpl();
        long orderRowId;
        if( savedInstanceState == null )
            orderRowId = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
        else
            orderRowId = savedInstanceState.getLong(ExtrasConst.DOC_ROW_ID_STR);

        doc.read(orderRowId);
        org.getData().id = doc.getId();
        org.read();
        org.close();

        TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
        tvOrg.setText(Html.fromHtml(getOrgText(org.getData())));

        EditText email = findViewById(R.id.email);
        email.setText(doc.getData().email);
        email.setEnabled(doc.isEditable());

        findViewById(R.id.btnSend).setOnClickListener(v -> {
            if(saveDoc())
                send();
        });

        adapter = new Adapter();
        ListView lv = findViewById(R.id.lvItems);
        lv.setAdapter(adapter);
    }

    boolean saveDoc() {
        if(doc.isEditable()) {
            EditText email = findViewById(R.id.email);
            doc.getData().email = email.getText().toString();
        }

        if(doc.isEmpty()) {
            Toast.makeText(this, "Доумент не полный. Введите email и выберите папки товара", Toast.LENGTH_SHORT).show();
            return false;
        }

        doc.write();
        return true;
    }

    void send() {
        new DocumentSender(this,
                findViewById(R.id.btnSend),
                OfferDoc.instance().getObjectName(),
                doc, doc.getRowid(),
                result -> { if(result) { finish(); } }
        ).execute((Void) null);
    }

    @Override
    public void onBackPressed() {
        if(!saveDoc() && doc.isEditable()) {
            doc.delete();
        }
        super.onBackPressed();
    }

    private String getOrgText(Org o) {
        return o.name;
    }

    @Override
    protected void onSaveInstanceState (Bundle outState) {
        outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
    }

    boolean inToggle = false;

    public View getFolderView(Folder f, View view) {
        if(view == null) {
            view = View.inflate(this, R.layout.offer_folder, null);
        }
        TextView tv = view.findViewById(R.id.tvName);
        tv.setText(f.name);

        boolean contains = doc.contains(f.fid);
        ImageView iv = view.findViewById(R.id.ivSelected);
        if(contains)
            iv.setImageResource(R.drawable.checked);
        else
            iv.setImageDrawable(null);

        iv = view.findViewById(R.id.ivFolder);
        int offset = 32 * f.level;
        iv.setPadding(offset, 0, 0, 0);

        view.setOnClickListener(v -> {
            if(!inToggle) {
                inToggle = true;
                doc.toggle(f);
                adapter.notifyDataSetChanged();
                inToggle = false;
            }
        });
        return view;
    }

    class Adapter extends BaseAdapter {
        List<Folder> items = new ArrayList<>();

        Adapter() {
            load();
        }

        void load() {
            FolderTree ft = CostStrategy.getFolders();
            Set<String> usedFolders = getUsedFolders(ft);

            for (Folder fi : ft) {
                if (!usedFolders.contains(fi.fid))
                    continue;
                items.add(fi);
            }
        }

        Set<String> getUsedFolders(FolderTree ft) {
            Set<String> usedFolders = new HashSet<>();

            String stmt = "select distinct fid from " + new Price().getTableName();
            Cursor c = DataBaseManager.getDataBase().rawQuery(stmt, null);
            while(c.moveToNext()) {
                String curf = c.getString(0);

                Folder f = ft.getFolder(curf);
                while(f != null) {
                    if (usedFolders.contains(f.fid))
                        break;
                    usedFolders.add((f.fid));
                    f = ft.getParent(f);
                }
            }
            c.close();
            return usedFolders;
        }

        @Override public int getCount() {return items.size();}
        @Override public Object getItem(int position) {return items.get(position);}
        @Override public long getItemId(int position) {return position;}

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Folder f = (Folder) getItem(position);
            View v = getFolderView(f, convertView);
            v.setBackgroundResource(position % 2 != 0 ? R.drawable.even_row_selector
                    : R.drawable.list_selector);
            return v;
        }

    }
}
