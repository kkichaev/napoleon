package com.grsoft.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.grsoft.database.DataObjectSendHitching;
import com.grsoft.database.DbWriter;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.AgentInfo;
import com.grsoft.dataobjects.ManagerAgent;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.ReturnInfo;
import com.grsoft.dataobjects.ReturnItemEx;
import com.grsoft.dataobjects.ReturnResponse;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.ObjectList;
import com.grsoft.network.ObjectListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReturnRequest extends DrawerActivity {

    PriceImpl pi = new PriceImpl();
    ReturnImpl rdoc = new ReturnImpl();
    ReturnInfo curDoc = null;
    boolean doc_mode = false;

    public static void open(Context context) {
        Intent i = new Intent(context, ReturnRequest.class);
        context.startActivity(i);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.return_requests;
    }

    @Override
    protected int getOptionsMenuID() {return R.menu.rr_option_menu; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.accept).setOnClickListener(v -> {
            acceptDoc(true);
        });

        findViewById(R.id.reject).setOnClickListener(v -> {
            acceptDoc( false);
        });
    }

    void acceptDoc(boolean accepting) {
        if(curDoc != null && curDoc.newRequest() && rdoc.getRowid() != ExtrasConst.INVALID_ID) {
            Config c = ConfigManager.getConfig();
            final String prevImp = c.impersonate;
            c.impersonate = curDoc.userid;

            ReturnEx re = (ReturnEx) rdoc.getData();
            ReturnResponse rr = new ReturnResponse();
            rr.created = curDoc.created;
            rr.userid = curDoc.userid;
            rr.id = re.id;
            rr.sum = rdoc.sum();
            rr.remark = "";
            rr.response = accepting ? ReturnInfo.REQUEST_CONFIRM : ReturnInfo.REQUEST_REJECT;

            List<ObjectListener> tosend = new ArrayList<>();
            DataObjectSendHitching dh = new DataObjectSendHitching(rr, "ReturnResponse");
            tosend.add(dh);
            if(accepting) {
                DataObjectSendHitching rdh = new DataObjectSendHitching(rdoc.getData(), "Returns");
                tosend.add(rdh);
            }
            List<Hitching> ret = new ArrayList<>();
            UpdateProcess upp = new UpdateProcess(this, new UpdateCtrl() {
                @Override public void updateCtrl(boolean enabled) {}

                @Override
                public void onFinish(boolean success) {
                    c.impersonate = prevImp;
                    if( success ) {
                        DbWriter wr = new DbWriter();
                        wr.insertRecord(rr);
                        wr.close();
                        postSyncUpdate();
                    }
                }
            }, ret);
            upp.setSending(tosend);
            upp.execute((Void[]) null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        rdoc.close();
        pi.close();
    }

    void switchMode() {
        doc_mode = !doc_mode;
        findViewById(R.id.docs).setVisibility(doc_mode ? View.GONE : View.VISIBLE);
        findViewById(R.id.ret_doc).setVisibility(doc_mode ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(doc_mode) {
            switchMode();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void initHitchings(List<Hitching> list) {
        String uids = "";

        for(String key : ManagerAgent.getAgents().keySet()) {
            uids += String.format("'%s',", key);
        }
        if(uids.length() == 0)
            return;

        uids = uids.substring(0, uids.length() - 1);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String where = String.format("created >= ToDate('%s') and userid in (%s)"
                ,sdf.format(c.getTime())
                ,uids
                );

        list.add(new RcvNewHitching(ReturnEx.class, "ReturnRequest"));
        list.add(new RcvNewHitching(ReturnResponse.class, "ReturnResponse"));
        list.add(new RcvNewHitching(AgentInfo.class, "AgentInfo"));
        list.add(new HitchOnSelect(Price.class, "Price", "SetQtyFilter(false)"));
        list.add(new HitchOnSelect(OrgEx.class, "Org", String.format("userid in (%s)", uids)));
        super.initHitchings(list);
    }

    @Override
    protected void postSyncUpdate() {
        loadDocs();
        if(doc_mode)
            switchMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDocs();
    }

    void loadDocs() {
        RecyclerView rc = findViewById(R.id.docs);
        rc.setAdapter(new Adapter());
        rc.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.rreq_title);
    }

    void openDoc(ReturnInfo doc) {
        curDoc = doc;

        ReturnEx re = (ReturnEx) rdoc.getData();
        re.userid = doc.userid;
        re.created = doc.created;
        rdoc.read();

        TextView tv;
        tv = findViewById(R.id.doc_org);
        tv.setText(doc.org);
        tv = findViewById(R.id.doc_agent);
        tv.setText(doc.agent);
        tv = findViewById(R.id.doc_date);
        tv.setText(Util.simpleDateFormat.format(doc.created));
        tv = findViewById(R.id.doc_address);
        tv.setText(doc.address);
        tv = findViewById(R.id.doc_org_limit);
        tv.setText(Util.IntToScaleStr(doc.orglimit, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        tv = findViewById(R.id.doc_agent_limit);
        tv.setText(Util.IntToScaleStr(doc.agentlimit, Consts.SUM_SCALE, Util.DEC_DELIM, false));

        RecyclerView rv = findViewById(R.id.items);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(new ItemAdapter(re));

        if(!doc_mode)
            switchMode();
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {

        public ItemRowHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(ReturnItemEx item, boolean evenRow) {
            TextView tv;
            Price p = pi.getData();
            p.id = item.id;
            if(!pi.read()) {
                p.name = p.id;
            }

            tv = itemView.findViewById(R.id.item_name);
            tv.setText(p.name);

            tv = itemView.findViewById(R.id.item_remark);
            tv.setText(item.cause);

            tv = itemView.findViewById(R.id.item_qty);
            tv.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE, Util.DEC_DELIM, false));

            long sum = (long)item.qty * item.cost / Consts.QTY_SCALE;
            tv = itemView.findViewById(R.id.item_sum);
            tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            
            itemView.setBackgroundResource(evenRow ? R.drawable.even_row_selector : R.drawable.list_selector);
        }
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemRowHolder> {
        ReturnEx doc;
        public ItemAdapter(ReturnEx src) {
            doc = src;
        }

        @NonNull
        @Override
        public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.return_item_row, parent, false);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemRowHolder holder, int position) {
            holder.update((ReturnItemEx) doc.items.get(position), ((position %2) == 0 ));
        }

        @Override
        public int getItemCount() {
            return doc.items.size();
        }
    }

    class DocRowHolder extends RecyclerView.ViewHolder {
        public DocRowHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void update(ReturnInfo doc, boolean evenRow) {
            int style = doc.newRequest() ? R.style.RetDocNew :
                    doc.confirmed() ? R.style.RetDocAccept :
                    R.style.RetDocReject;

            ReturnEx re = (ReturnEx) rdoc.getData();
            re.userid = doc.userid;
            re.created = doc.created;
            long sum = rdoc.read() ? rdoc.sum() : 0;

            TextView tv;
            tv = itemView.findViewById(R.id.org);
            tv.setText(doc.org);
            tv.setTextAppearance(ReturnRequest.this, style);

            tv = itemView.findViewById(R.id.agent);
            tv.setText(doc.agent);
            tv.setTextAppearance(ReturnRequest.this, style);

            tv = itemView.findViewById(R.id.agent_limit);
            tv.setText(Util.IntToScaleStr(doc.agentlimit, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            tv.setTextAppearance(ReturnRequest.this, style);
            tv.setTypeface(null, Typeface.ITALIC);

            tv = itemView.findViewById(R.id.address);
            tv.setText(doc.address);
            tv.setTextAppearance(ReturnRequest.this, style);
            tv.setTypeface(null, Typeface.ITALIC);

            tv = itemView.findViewById(R.id.date);
            tv.setText(Util.simpleDateFormat.format(doc.created));
            tv.setTextAppearance(ReturnRequest.this, style);

            tv = itemView.findViewById(R.id.limit);
            tv.setText(Util.IntToScaleStr(doc.orglimit, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            tv.setTextAppearance(ReturnRequest.this, style);
            tv.setTypeface(null, Typeface.ITALIC);

            tv = itemView.findViewById(R.id.sum);
            tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
            tv.setTextAppearance(ReturnRequest.this, style);

            itemView.setOnClickListener(v -> {
                openDoc(doc);
            });

            itemView.setBackgroundResource(evenRow ? R.drawable.list_selector : R.drawable.even_row_selector);
        }
    }
    class Adapter extends RecyclerView.Adapter<DocRowHolder> {

        List<ReturnInfo> retDocs;
        public Adapter() {
            DbWriter.checkDBTable(ReturnResponse.class);
            retDocs = ReturnInfo.get();
        }

        @NonNull
        @Override
        public DocRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.return_doc_row, parent, false);
            return new DocRowHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DocRowHolder holder, int position) {
            holder.update(retDocs.get(position), (position % 2) == 0);
        }

        @Override
        public int getItemCount() {
            return retDocs.size();
        }
    }
}
