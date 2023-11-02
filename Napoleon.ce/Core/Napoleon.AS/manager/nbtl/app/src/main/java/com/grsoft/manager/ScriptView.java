package com.grsoft.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.MOrderImplBase;
import com.grsoft.dataobjects.impl.MScriptImpl;
import com.grsoft.manager.documents.MDocType;
import com.grsoft.manager.documents.MOrderDoc;
import com.grsoft.manager.documents.MScriptDoc;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.script.dataobjects.ScriptDef;
import com.grsoft.script.dataobjects.ScriptItem;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class ScriptView extends DocDetail {
    private final static String USERID = "userid";
    private ScriptDef scriptDef;

    public static void open(Context context, long rowid, String userid){
        Intent intent = new Intent(context, ScriptView.class);
        intent.putExtra(DocDetailDecorator.DOCTYPE, MScriptImpl.class);
        intent.putExtra(DocDetailDecorator.ROWID, rowid);
        intent.putExtra(USERID, userid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.tvQtyTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvCost).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvCommentTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvOverallTitle).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvQty).setVisibility(View.INVISIBLE);
        findViewById(R.id.tvSum).setVisibility(View.INVISIBLE);

        ((ListView)findViewById(R.id.list)).setOnItemClickListener((pr,v,pos,id)->{
            ScriptItem si = (ScriptItem) pr.getItemAtPosition(pos);

            DocTypeBase dtb = MDocType.getDocType(si.type.equals("Visit") ? "VisitInfo" : si.type);

            if (dtb != null) {
                Document<?> d = dtb.create();

                if (d != null) {
                    d.read(si.date.getTime());
                    d.close();
                    d.open(this);
                }
            }
        });
    }

    @Override
    public String getTitle(CreateDocDataObject exdata) {
        String title = getString(MScriptDoc.instance().getDocTitle());

        if (scriptDef == null){
            for(ScriptDef def:  DbReader.fetch(ScriptDef.class, String.format("id=%d",((MScriptImpl)getDocument()).getData().scriptId))){
                scriptDef = def;
                break;
            }
        }

        if (scriptDef.name.length() > 0)
            title = String.format("%s: %s", title, scriptDef.name);

        return title;

    }

    @Override
    public ListAdapter createAdapter() {
        return new BaseAdapter(){

            @SuppressWarnings("unchecked")
            @Override
            public int getCount() {	return ((MScriptImpl)getDocument()).getData().items.size();}

            @SuppressWarnings("unchecked")
            @Override
            public Object getItem(int pos) { return ((MScriptImpl)getDocument()).getData().items.get(pos); }

            @Override
            public long getItemId(int pos) { return 0; }

            @Override
            public View getView(int pos, View view, ViewGroup arg2) {
                ScriptItem item = (ScriptItem)getItem(pos);

                if(item != null)
                    view = getItemView(view, item, pos);

                view.setBackgroundResource(pos % 2 != 0 ? R.drawable.list_selector
                        : R.drawable.even_row_selector);

                return view;
            }
        };
    }

    private View getItemView(View view, ScriptItem item, int pos){
        if(view == null)
            view = View.inflate(this, R.layout.docitems_row, null);

        String title = item.type;

        if (scriptDef != null && pos < scriptDef.items.size())
            title = scriptDef.items.get(pos).name;

        ((TextView) view.findViewById(R.id.tvName)).setText(title);
        ((TextView) view.findViewById(R.id.tvQty)).setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.tvCost)).setVisibility(View.GONE);

        return view;
    }
}
