package com.grsoft.napoleon;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.dataobjects.impl.SalesImplEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.MessageBox;
import com.grsoft.util.Util;

import java.util.Collections;
import java.util.List;

public class SalesDetailTabak extends SalesDetailEx implements SalesImplEx.AddEvents {
    private static final String TAG = "MainEx";
    static List<PriceEx> priceItems;
    BarcodeData barcodeData;
    String barcode;

    BarcodeScannerHelper scanner;
    Adapter adapter;
    Uri failTone;
    Uri goodTone;
    MediaPlayer mediaPlayer = null;

    static public void open(Context context, SalesImplEx order) {
        Intent i = new Intent(context, SalesDetailTabak.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, order.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if(cfg.uriGood.length() > 0)
            goodTone = Uri.parse(cfg.uriGood);
        if(cfg.uriFail.length() > 0)
            failTone = Uri.parse(cfg.uriFail);

        if(BuildConfig.DEBUG) {
            View vt = findViewById(R.id.btnTest);
            vt.setVisibility(View.VISIBLE);
            vt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doTest();
                }
            });
        }
    }

    int ctr = 0;
    private void doTest() {
        if(ctr == 0) {
            handler.onRead("010400639609812221RU11120524025172419");
//            handler.onRead("02900046794476tuBEi<kAAAA0fbB");
//            handler.onRead("010460564802437921l/wnTl=934vlU");
//            ctr++;
        } else {
            handler.onRead("00000046233387Yf.mOm4AB\"8Wpl0");

        }
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

    @Override
    protected void doPrint() {
        if(!((SalesImplEx)doc).isCompleete()) {
            Toast.makeText(this, "Не все товары отсканированы в документе", Toast.LENGTH_LONG).show();
            return;
        }
        super.doPrint();
    }

    BarcodeScannerHelper.Event handler = new BarcodeScannerHelper.Event() {
        @Override
        public void onRead(final String barcode) {
            Log.d(TAG, "Read " + barcode);
            if (!doc.isEditable()) {
                return;
            }
            ((SalesImplEx) doc).addBarcode(barcode, SalesDetailTabak.this);
        }
    };

    void playSound(Uri u) {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }

        if(!BuildConfig.DEBUG) {
            mediaPlayer = MediaPlayer.create(SalesDetailTabak.this, u);
            if(mediaPlayer != null) {
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
    }

    @Override
    protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
        SalesItemEx se = (SalesItemEx)item;
        String text = "";
        if(se.packQty > 0)
            text += "уп." + Util.IntToScaleStr(se.packQty, Consts.QTY_SCALE);

        if(se.itemQty > 0) {
            if(text.length() > 0) {
                text += "<br/>";
            }
            text += "шт " + Util.IntToScaleStr(se.itemQty, Consts.QTY_SCALE);
        }
        tvQty.setText(Html.fromHtml(text));
    }

    void drawFactQty(int color, OrderItem item, TextView tvQty) {
        SalesItemEx se = (SalesItemEx)item;
        String text = "";
        int qty = se.factPack();
        if(qty > 0)
            text += "уп." + Util.IntToScaleStr(qty, Consts.QTY_SCALE);
        qty = se.factQty();
        if(qty > 0) {
            if(se.packQty > 0) {
                text += "<br/>";
            }
            text += "шт " + Util.IntToScaleStr(qty, Consts.QTY_SCALE);
        }
        tvQty.setText(Html.fromHtml(text));
    }

    @Override protected void setAdapter() {
        adapter = new Adapter();
        lvItems.setAdapter(adapter);
    }

    @Override protected void setContentView() {
        setContentView(R.layout.salesdetail_tabak);
    }

    @Override
    public void added(SalesItemEx item, BarcodeData data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void fail(BarcodeData data, String bc) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SalesDetailTabak.this, "Нельзя добваить этот GTIN в документ: " + bc, Toast.LENGTH_LONG).show();
                playSound(failTone);
            }
        });
    }

    @Override
    public void needSelect(List<PriceEx> items, BarcodeData data, String bc) {
        List<PriceEx> choose = ((SalesImplEx)doc).makeIntersect(items);

        if(choose.size() == 0) {
            fail(data, bc);
            return;
        }
        if(choose.size() == 1) {
            ((SalesImplEx)doc).tryAddBC(choose.get(0), data, bc, this);
            return;
        }

        Collections.sort(choose);
        priceItems = choose;
        barcodeData = data;
        barcode = bc;

        ChooseItem dlg = new ChooseItem();
        dlg.show(getFragmentManager(), "");
    }

    public void onItemSelect(int index) {
        ((SalesImplEx)doc).tryAddBC(priceItems.get(index), barcodeData, barcode, this);
    }

    public static class ChooseItem extends DialogFragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.choose_item, null);
            ListView lv = v.findViewById(R.id.lvItems);

            ArrayAdapter<PriceEx> aa = new ArrayAdapter<PriceEx>(inflater.getContext(), R.layout.simple_spinner_layout, priceItems);
            lv.setAdapter(aa);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ((SalesDetailTabak)getActivity()).onItemSelect(i);
                    dismiss();
                }
            });

            getDialog().setTitle("Выберите товар");
            return v;
        }
    }

    class Adapter extends OrderItemsAdapter {
        @Override int getResourceID() { return R.layout.salestabak_row; }

        @Override
        protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
            super.drawInternal(view, name, color, item, pos);
            drawFactQty(color, item, (TextView)view.findViewById(R.id.tvQtyFact));
        }
    }
}
