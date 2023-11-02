package com.grsoft.napoleon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Outcome;
import com.grsoft.dataobjects.OutcomeItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.IncomeImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.OutcomeImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.OutcomeDoc;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OutcomeEdit extends FragmentActivity implements Outcome.AddEvents {
    static final String TAG = "OutcomeEdit";

    static List<PriceEx> priceItems;
    BarcodeData barcodeData;
    String barcode;

    BarcodeScannerHelper scanner;

    Uri failTone;
    Uri goodTone;
    MediaPlayer mediaPlayer = null;

    Outcome doc = new Outcome();
    String number, id;
    PriceImpl pi = new PriceImpl();

    Adapter adapter;

    static final String DOC_NUMBER = "docNumber";

    int bcTestIndex = 0;

    public static void open(Context context, String orgId, String number) {
        Intent i = new Intent(context, OutcomeEdit.class);
        i.putExtra(ExtrasConst.ORG_ID_STR, orgId);
        i.putExtra(DOC_NUMBER, number);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outcome_edit);

        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        id = b.getString(ExtrasConst.ORG_ID_STR);
        number = b.getString(DOC_NUMBER);

        OrgImpl oi = new OrgImpl();
        Org o = oi.getData();
        o.id = id;

        oi.read();
        oi.close();

        String where = "number='" + number + "' and id='" + id +"'";
        Delivery d = new Delivery();
        DbReader r = new DbReader();
        r.select(d, d.getTableName(), where);

        boolean readed = r.select(doc, doc.getTableName(), where);
        r.close();
        if(!readed) {
            doc.initFrom(this, d);
        }
        String text = o.name + " " + Util.simpleDateFormat.format(d.date) + " " + d.number;
        ((TextView)findViewById(R.id.tvName)).setText(Html.fromHtml(text));

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if(cfg.uriGood.length() > 0)
            goodTone = Uri.parse(cfg.uriGood);
        if(cfg.uriFail.length() > 0)
            failTone = Uri.parse(cfg.uriFail);

        ListView lv = findViewById(R.id.lvItems);
        adapter = new Adapter();
        lv.setAdapter(adapter);

        if(doc.isEditable() == false) {
            View v = findViewById(R.id.btnSend);
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View view) { send(); }
            });
        }

        if(BuildConfig.DEBUG) {
            final String[] codes = new String[] {
                    "02900046794513IV/bI9RAAAA1Sff",
                    "011460026601465510683220212168181HN07441436250024010117852914022169",
                    "010460026601465821y3g3E538005185000939X9v24010117852",
                    "00000046233257saV,>E+AC/Uz19I",
                    "00000046233257N>MJ*L0AC/UDjbd",
                    "0114640091920530132110012101110031",
                    "010464009192053321BTlj2Gu800511000093gTmY",
                    "04640091920526aDJ=QBuAB5oq0GE",
                    "04640091920526D0kf<7ZAB5oPIN+",
            };
            View v = findViewById(R.id.btnTest);
            v.setVisibility(View.VISIBLE);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(bcTestIndex >= codes.length)
                        bcTestIndex = 0;

                    doc.addBarcode(codes[bcTestIndex++], OutcomeEdit.this);
                }
            });
        }
    }

    private void send() {
        OutcomeImpl impl = new OutcomeImpl();
        impl.getData().created = doc.created;
        impl.read();
        impl.close();

        DocExportListener de = new DocSendListner(OutcomeDoc.instance().getObjectName(), impl);
        List<DocExportListener> send = new ArrayList<>();
        send.add(de);

        new SyncProcess(this, null, send).execute((Void[])null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if (doc.isEditable() && cfg.scannerAddress.length() > 0) {
            scanner = new BarcodeScannerHelper();
            scanner.scanning(this, cfg.scannerAddress, handler);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scanner != null) {
            scanner.close();
            scanner = null;
        }
    }

    void playSound(Uri u) {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }

        if(!BuildConfig.DEBUG) {
            mediaPlayer = MediaPlayer.create(OutcomeEdit.this, u);
            mediaPlayer.setLooping(false);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer = null;
                    mp.reset();
                    mp.stop();
                }
            });
            mediaPlayer.start();
        }
    }

    BarcodeScannerHelper.Event handler = new BarcodeScannerHelper.Event() {
        @Override
        public void onRead(final String barcode) {
            Log.d(TAG, "Read " + barcode);
            if(doc.isEditable() == false)
                return;

            doc.addBarcode(barcode, OutcomeEdit.this);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        pi.close();
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onBackPressed() {
        if(!doc.isCompleete()) {
            Toast.makeText(this, "Ввод документа незавершен", Toast.LENGTH_LONG).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ExtrasConst.ORG_ID_STR, id);
        outState.putString(DOC_NUMBER, number);
    }

    @Override
    public void added(final OutcomeItem item, BarcodeData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(item.isCompleete()) {
                    playSound(goodTone);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void fail(final BarcodeData data, final String bc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if(data != null) {
//                    String text = "ШК <b>" + data.itemBC + "</b>";
//                    if(data.checkItemCode.length() > 0)
//                        text += "<br/>ШК пач: <b>" + data.checkItemCode + "</b>";
//                    text += "<br/>МРЦ: <b>" + Util.IntToScaleStr(data.cost, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
//                    text += "<br/>Скан код: <b>" + bc + "</b>";
//                    MessageBox.show(OutcomeEdit.this, "Ошибка добавления", text);
//                }
                Toast.makeText(OutcomeEdit.this, "Нельзя добваить этот GTIN в документ", Toast.LENGTH_LONG).show();
                playSound(failTone);
            }
        });
    }

    @Override
    public void needSelect(List<PriceEx> items, BarcodeData data, String bc) {
        List<PriceEx> choose = doc.makeIntersect(items);

        if(choose.size() == 0) {
            fail(data, bc);
            return;
        }
        if(choose.size() == 1) {
            doc.tryAddBC(choose.get(0), data, bc, this);
            return;
        }

        Collections.sort(choose);
        priceItems = choose;
        barcodeData = data;
        barcode = bc;

        ChooseItem dlg = new ChooseItem();
        dlg.show(getSupportFragmentManager(), "");
    }

    public void onItemSelect(int index) {
        doc.tryAddBC(priceItems.get(index), barcodeData, barcode, this);
    }

    public static class ChooseItem extends DialogFragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.choose_item, null);
            ListView lv = v.findViewById(R.id.lvItems);

            ArrayAdapter<PriceEx> aa = new ArrayAdapter<PriceEx>(getContext(), R.layout.simple_spinner_layout, priceItems);
            lv.setAdapter(aa);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ((OutcomeEdit)getActivity()).onItemSelect(i);
                    dismiss();
                }
            });

            getDialog().setTitle("Выберите товар");
            return v;
        }
    }

    class Adapter extends BaseAdapter {
        @Override public int getCount() { return doc.items.size(); }
        @Override public Object getItem(int i) { return doc.items.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(OutcomeEdit.this, R.layout.outcome_edit_row, null);
            }

            OutcomeItem item = (OutcomeItem) getItem(i);
            TextView tv;
            String text;

            Price p = pi.getData();
            p.id = item.id;
            if(pi.read())
                text = p.name;
            else
                text = p.id;

            tv = view.findViewById(R.id.tvName);
            tv.setText(text);

            text = "";
            if(item.boxQty > 0) {
                text += Util.IntToScaleStr(item.boxQty, Consts.QTY_SCALE) + " кор.";
            }
            if(item.packQty > 0) {
                if(text.length() > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.packQty, Consts.QTY_SCALE) + " уп.";
            }
            if(item.qty > 0) {
                if(text.length() > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.qty, Consts.QTY_SCALE) + " шт";
            }

            tv = view.findViewById(R.id.tvPlan);
            tv.setText(Html.fromHtml(text));

            text = "";
            if(item.inputBoxQty > 0) {
                text += Util.IntToScaleStr(item.inputBoxQty, Consts.QTY_SCALE) + " кор.";
            }
            if(item.inputPackQty > 0) {
                if(text.length() > 0 && item.boxQty > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.inputPackQty, Consts.QTY_SCALE) + " уп.";
            }
            if(item.inputQty > 0) {
                if(text.length() > 0 && item.packQty > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.inputQty, Consts.QTY_SCALE) + " шт";
            }
            tv = view.findViewById(R.id.tvFact);
            tv.setText(Html.fromHtml(text));

            if(item.isCompleete())
                view.setBackgroundResource(R.drawable.compleete_item);
            else
                view.setBackgroundResource(R.drawable.list_selector);
            return view;
        }
    }
}
