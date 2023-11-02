package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentsEx;
import com.grsoft.dataobjects.DeliveryItem;
import com.grsoft.dataobjects.FBTransfer;
import com.grsoft.dataobjects.FBTransferCommit;
import com.grsoft.dataobjects.FBTransferReject;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Sklad;
import com.grsoft.dataobjects.impl.FBTransferCommitImpl;
import com.grsoft.dataobjects.impl.FBTransferImpl;
import com.grsoft.dataobjects.impl.FBTransferRejectImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.FBTransferDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

import java.util.List;
import java.util.Map;

public class FBTransferDetail extends OrderDeliveryDetail {

    FBTransferCommit answer;
    FBTransferReject reject;
    Map<Object, Sklad> sklads;
    Map<Object, AgentsEx> agents;

    enum States {
        WaitCommit, // создан ждем подтверждения
        NeedAccept, // ждут подтверждения от нас
        CommittedByAgent,
        Rejected,
        CommittedBy1c,
        NeedCommit1c,  // подтверждение документа из 1с
    };
    States docState = States.WaitCommit;
    String docNumber = "";

    static public void open(Context context, FBTransferImpl doc) {
        Intent i = new Intent(context, FBTransferDetail.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.transfer_detail);
    }

    @Override
    protected void setDocType() {
        docType = FBTransferDoc.instance();
    }

    @Override
    protected void init() {
        sklads = DbReader.fetchDic(Sklad.class, "id");
        agents = DbReader.fetchDic(AgentsEx.class, "id");

        FBTransfer src = (FBTransfer) doc.getData();
        loadAnswer(src);

        updateFromState(src);

        findViewById(R.id.btnAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accepting();
            }
        });
        findViewById(R.id.btnReject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejecting();
            }
        });
    }

    private void loadAnswer(FBTransfer src) {
        String where = "created = " + Long.toString(src.created.getTime());
        List<FBTransferCommit> answ = DbReader.fetch(FBTransferCommit.class, where);

        for(FBTransferCommit d : answ) {
            answer = d;
            src.params |= (answer.params & ParamState.ofExported);

            for(OrderItem oi : d.items) {
                DeliveryItem di = new DeliveryItem();
                di.id = oi.id;
                di.qty = oi.qty;
                items.add(di);
            }
            break;
        }

        List<FBTransferReject> rjl = DbReader.fetch(FBTransferReject.class, where);
        for(FBTransferReject ri : rjl) {
            reject = ri;
            src.params |= (reject.params & ParamState.ofExported);
            break;
        }

        if(reject != null) {
            docState = States.Rejected;
        } else if(src.needAccept(answer)) {
            docState = States.NeedAccept;
        } else if(src.needAccept1c(answer)) {
            docState = States.NeedCommit1c;
        } else if(src.commitedByAgent(answer)) {
            docState = States.CommittedByAgent;
        } else if (src.commitedBy1c(answer)) {
            docState = States.CommittedBy1c;
        }

        ((FBTransferImpl)doc).setRefItems(items, docState == States.NeedAccept);
    }

    void refreshText(FBTransfer src) {
        boolean acceptableDoc = src.needAccept(null);
        boolean dirFromMe = src.direction == FBTransfer.DIRECTION_FROM_ME;
        if(acceptableDoc)
            dirFromMe = !dirFromMe;

        String text = dirFromMe ? "С моего на склад" :
                "На мой со склада ";

        if(acceptableDoc) {
            AgentsEx a = agents.get(src.userid);
            if(a != null) {
                text += " агента <b>" + a.name + "</b>";
            }
        } else {
            Sklad s = sklads.get(src.whId);
            if (s != null) {
                text += " <b>" + s.name + "</b>";
            }
        }

        switch (docState) {
            case Rejected:
                text += "<br/>";
                if(acceptableDoc) text += "запрос на перемещение: <b>отказ<b>";
                else text += "отказ " + reject.remark ;
                break;
            case CommittedByAgent:
                text += "<br/>";
                if(acceptableDoc) {
                    text += "запрос на перемещение: <b>подтвержден<b>";
                } else {
                    text += "Подтвержден ";
                    if(answer.remark.length() > 0)
                        text += "<br/>" + answer.remark;
                }
                break;
            case NeedAccept:
                text += "<br/>запрос на перемещение: <b>нужно одобрение</b>";
                break;
            case NeedCommit1c:
                text += "<br/><b>Нужно подтверждение</b>";
                break;
        }

        if(answer != null && answer.number.length() > 0) {
            text += "<br/>1с № " + answer.number + " от " + Util.simpleDateFormat.format(answer.date);
            if(answer.remark.length() > 0)
                text += "<br/>" + answer.remark;
        }
        if(src.remark.length() > 0 && (answer == null || !src.remark.equals(answer.remark)))
            text += "<br/>" + src.remark;

        TextView tv = findViewById(R.id.tvOrg);
        tv.setText(Html.fromHtml(text));
    }

    private void rejecting() {
        if(reject == null || (reject.params & ParamState.ofExported) == 0) {
            reject = FBTransferReject.createFrom((FBTransfer) doc.getData());
            DbWriter w = new DbWriter();
            w.insertRecord(reject);
            w.close();
        }
        FBTransferRejectImpl fi = new FBTransferRejectImpl();
        fi.read(reject.created.getTime());

        new DocumentSender(this, btnSend, "TransferReject", fi, fi.getRowid(), this).execute((Void[])null);
    }

    private void accepting() {
        if(answer == null || (answer.params & ParamState.ofExported) == 0) {
            answer = FBTransferCommit.createFrom((FBTransfer) doc.getData());
            answer.updateQty(items);
            DbWriter w = new DbWriter();
            w.insertRecord(answer);
            w.close();
        }
        FBTransferCommitImpl fi = new FBTransferCommitImpl();
        fi.read(answer.created.getTime());

        new DocumentSender(this, btnSend, "TransferCommit", fi, fi.getRowid(), this).execute((Void[])null);
    }

    @Override
    protected void updateTotalSum() { }


    void updateFromState(FBTransfer src) {
        refreshText(src);

        findViewById(R.id.SumColumnTitle).setVisibility(View.GONE);
        findViewById(R.id.tvTotalSum).setVisibility(View.GONE);

        View unload = findViewById(R.id.UnloadTitle);
        if(docState == States.WaitCommit) {
            unload.setVisibility(View.GONE);
        } else {
            unload.setVisibility(View.VISIBLE);
        }


        int acptVsbl = View.GONE;
        int rjctVsbl = View.GONE;
        int sndVsbl = View.VISIBLE;

        if(answer != null && answer.number.length() > 0)
            sndVsbl = View.GONE;

        TextView tv = findViewById(R.id.QtyTitle);
        View acpt = findViewById(R.id.btnAccept);
        View rjct = findViewById(R.id.btnReject);

        boolean acceptableDoc = src.needAccept(null);
        if(docState == States.CommittedByAgent) {
            if(acceptableDoc)
                acptVsbl = View.VISIBLE;
            sndVsbl = View.GONE;
        }
        if(docState == States.Rejected) {
            if(acceptableDoc)
                rjctVsbl = View.VISIBLE;
            sndVsbl = View.GONE;
        }

        if(docState == States.NeedAccept || docState == States.NeedCommit1c) {
            findViewById(R.id.btnAddItems).setVisibility(View.GONE);
            findViewById(R.id.btnEditOrder).setVisibility(View.GONE);

            sndVsbl = View.GONE;
            acptVsbl = View.VISIBLE;
            if (docState == States.NeedAccept)
                rjctVsbl = View.VISIBLE;
        }
        acpt.setVisibility(acptVsbl);
        rjct.setVisibility(rjctVsbl);
        btnSend.setVisibility(sndVsbl);

        if(docState == States.NeedAccept) {
            tv.setText(R.string.req_qty);
        } else {
            tv.setText(R.string.qty);
        }

        if(docState != States.WaitCommit || !doc.isEditable()) {
            findViewById(R.id.btnAddItems).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FBTransfer src = (FBTransfer) doc.getData();
        updateFromState(src);
    }

    @Override
    public void postSendExecute(boolean result) {
        super.postSendExecute(result);
        FBTransfer src = (FBTransfer) doc.getData();
        if(result) {
            src.needCommit1c = 0;
            doc.setProceeded();
            loadAnswer(src);
        }
        updateFromState(src);
    }

    @Override
    public void send() {
        FBTransfer src = (FBTransfer) doc.getData();
        String objName = src.sendObjectName();
        if(!objName.isEmpty()) {
            new DocumentSender(this, btnSend, objName, doc,
                    doc.getRowid(), this).execute((Void[])null);
        }
    }

    @Override
    protected void setAdapter() {
        lvItems.setAdapter(new Adapter());
    }

    class Adapter extends OrderDeliveryItemsAdapter {
        @Override
        protected int getItemColor(OrderItem item, int defaultColor) {
            if(docState == States.WaitCommit)
                return defaultColor;

            return super.getItemColor(item, defaultColor);
        }

        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
            super.drawInternal(view, name, color, item, pos);

            view.findViewById(R.id.tvSum).setVisibility(View.GONE);
            if(docState == States.WaitCommit)
                view.findViewById(R.id.tvDispatch).setVisibility(View.GONE);
        }
    }

}
