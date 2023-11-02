package com.grsoft.napoleon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.IncomeItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.IncomeImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;
import com.grsoft.util.gps.GPSUtilNew;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeEdit extends Activity {
    String TAG = "IncomeEdit";
    BarcodeScannerHelper scanner;

    Uri failTone;
    Uri goodTone;
    MediaPlayer mediaPlayer = null;

    PriceEx price = new PriceEx();
    DbReader reader = new DbReader();

    IncomeImpl doc = new IncomeImpl();
    Adapter adapter;

    public static void open(Context context, IncomeImpl doc) {
        Intent i = new Intent(context, IncomeEdit.class);
        i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc == null ? ExtrasConst.INVALID_ROWID : doc.getRowid());
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.income_edit);
        Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR);
        if(rid == ExtrasConst.INVALID_ROWID)
            doc.init(this, "", GPSUtilNew.getLastKnownLocation());
        else
            doc.read(rid);

        ListView lv = findViewById(R.id.lvItems);
        adapter = new Adapter();
        lv.setAdapter(adapter);
        updateTotals();

        registerForContextMenu(lv);

        CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
        if(cfg.uriGood.length() > 0)
            goodTone = Uri.parse(cfg.uriGood);
        if(cfg.uriFail.length() > 0)
            failTone = Uri.parse(cfg.uriFail);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.income_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        IncomeItem ii = (IncomeItem) adapter.getItem(menuInfo.position);
        if(item.getItemId() == R.id.itDel) {
            doc.getData().items.remove(ii);
            doc.write();
            updateTotals();
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
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
    protected void onDestroy() {
        super.onDestroy();
        reader.close();
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

        mediaPlayer = MediaPlayer.create(IncomeEdit.this, u);
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

    BarcodeScannerHelper.Event handler = new BarcodeScannerHelper.Event() {
        @Override
        public void onRead(final String barcode) {
            Log.d(TAG, "Read " + barcode);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                MessageBox.show(SalesDetailTabak.this, "info", barcode);
                    boolean res = doc.addBarcode(barcode);
                    if(!res) {
                        Toast.makeText(IncomeEdit.this, "В документе нет такого DataMatrix кода", Toast.LENGTH_LONG).show();
                        playSound(failTone);
                    } else {
                        if(doc.isGood()) {
                            playSound(goodTone);
                        }
                        int idx = adapter.addBarcode(barcode);
                        if(idx >= 0) {
                            ListView lv = findViewById(R.id.lvItems);
                            lv.setSelection(idx);
                        }
                        updateTotals();
                    }
                }
            });
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        doc.close();
        if(mediaPlayer != null)
            mediaPlayer.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(doc.isEditable() && doc.isEmpty())
            doc.delete();
    }

    void updateTotals() {
        String text = String.format("Всего позиций %d / найдено %d", doc.getData().items.size(), doc.haveCount());
        ((TextView)findViewById(R.id.tvTotalSum)).setText(text);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
    }

    static class Item {
        PriceEx item;
        int qty = 0;
        int packQty = 0;
        int boxQty = 0;

        int fact = 0;
        int packFact = 0;
        int boxFact = 0;

        boolean isCompleete() {
            return qty == fact && packQty == packFact && boxQty == boxFact;
        }
    }

    class Adapter extends BaseAdapter {

        List<Item> data = new ArrayList<>();

        public Adapter() {
            Map<String, Item> items = new HashMap<>();

            for(IncomeItem ii : doc.getData().items) {
                PriceEx pe = new PriceEx();
                BarcodeData bd = findItem(ii.code, pe);
                if(bd != null) {
                    Item item = items.get(pe.id);
                    if(item == null) {
                        item = new Item();
                        item.item = pe;
                        items.put(pe.id, item);
                    }
                    if(bd.isBox) {
                        item.boxQty ++;
                        if(ii.have > 0)
                            item.boxFact++;
                    } else if (!bd.isItemCode) {
                        item.packQty++;
                        if(ii.have > 0)
                            item.packFact++;
                    } else {
                        item.qty++;
                        if(ii.have > 0)
                            item.fact++;
                    }
                }
            }

            data.addAll(items.values());
        }

        int addBarcode(String code) {
            int idx = -1;
            PriceEx pe = new PriceEx();
            BarcodeData bd = findItem(code, pe);
            if(bd != null) {
                for(Item i : data) {
                    if(i.item.id.equals(pe.id)) {
                        if(bd.isBox) {
                            i.boxFact++;
                        } else if (!bd.isItemCode) {
                            i.packFact++;
                        } else {
                            i.fact++;
                        }
                        idx = data.indexOf(i);
                        notifyDataSetChanged();
                        break;
                    }
                }
            }
            return idx;
        }

        BarcodeData findItem(String code, PriceEx pe) {
            BarcodeData bd = new BarcodeData(code);
            boolean readed = false;
            if(bd.isBox) {
                readed = reader.select(pe, pe.getTableName(), "bcBox LIKE '%" + bd.itemBC + "%'");
            } else {
                boolean bdo = reader.select(pe, pe.getTableName(), "barcode like '%" + bd.itemBC + "%'");
                while(bdo) {
                    int inpack = pe.qtyInPack;
                    if(inpack == 0)
                        inpack = Consts.QTY_SCALE;
                    int checkCost = bd.isItemCode ? bd.cost : (int) ((long) bd.cost * Consts.QTY_SCALE / inpack);
                    int itemCost = pe.mrc;
                    if (checkCost == itemCost) {
                        readed = true;
                        break;
                    }
                    bdo = reader.selectNext(pe);
                }
            }

            return readed ? bd : null;
        }

        @Override public int getCount() { return data.size(); }
        @Override public Object getItem(int i) { return data.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if(view == null) {
                view = View.inflate(IncomeEdit.this, R.layout.income_edit_row, null);
            }

            Item item = (Item) getItem(i);
            TextView tv = view.findViewById(R.id.tvName);

            String text = item.item.name + " " + Util.IntToScaleStr(item.item.mrc, Consts.SUM_SCALE, Util.DEC_DELIM, false);
            tv.setText(Html.fromHtml(text));

            text = "";
            if(item.boxQty > 0) {
                text += Util.IntToScaleStr(item.boxQty, 0) + " кор.";
            }
            if(item.packQty > 0) {
                if(text.length() > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.packQty, 0) + " уп.";
            }
            if(item.qty > 0) {
                if(text.length() > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.qty, 0) + " шт";
            }

            tv = view.findViewById(R.id.tvPlan);
            tv.setText(Html.fromHtml(text));

            text = "";
            if(item.boxFact > 0) {
                text += Util.IntToScaleStr(item.boxFact, 0) + " кор.";
            }
            if(item.packFact > 0) {
                if(text.length() > 0 && item.boxQty > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.packFact, 0) + " уп.";
            }
            if(item.fact > 0) {
                if(text.length() > 0 && item.packQty > 0) {
                    text += "<br/>";
                }
                text += Util.IntToScaleStr(item.fact, 0) + " шт";
            }
            tv = view.findViewById(R.id.tvFact);
            tv.setText(Html.fromHtml(text));

            int bk = R.drawable.list_selector;
            int vsbl = View.INVISIBLE;
            if(item.isCompleete()) {
                vsbl = View.VISIBLE;
                bk = R.drawable.list_grey_selector;
            }
            view.findViewById(R.id.ivDone).setVisibility(vsbl);
            view.setBackgroundResource(bk);

            return view;
        }
    }
}
