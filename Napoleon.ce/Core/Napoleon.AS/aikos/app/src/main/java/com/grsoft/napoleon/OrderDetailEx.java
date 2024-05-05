package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.grsoft.database.DataObjectSendHitching;
import com.grsoft.database.DbReader;
import com.grsoft.database.DocResultHitching;
import com.grsoft.database.HitchOnSelect;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.BillRequest;
import com.grsoft.dataobjects.FileStorage;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.UpdateProcess;
import com.grsoft.util.MessageBox;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

public class OrderDetailEx extends OrderDetail{

    String attacheFilter = "";
    DocResultHitching resultHitching = new DocResultHitching();

    static public String billId(Order doc) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        return sdf.format(doc.created) + doc.id.replaceAll("\\p{C}", "");
    }

    static String filterStr(Order doc) {
        String id = billId(doc);
        return String.format("id='%s'", id);
    }

    @Override
    protected List<OrderItem> docItems() {
        return ((OrderImplEx)doc).group();
    }

    @Override protected void setContentView() { setContentView(R.layout.order_detail_ex);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btnReqBill).setOnClickListener(v -> {
            requestBill();
        });
    }

    public static class Upd extends UpdateProcess {

        Events handler;

        public interface Events {
            void execute(boolean start, boolean result);
        }

        public Upd(Context context, Events handler) {
            super(context);
            this.handler  = handler;
        }

        @Override
        protected void onPreExecute() {
            handler.execute(true, false);
        }

        @Override
        protected void onPostExecuteWork(Boolean result) {
            handler.execute(false, result);
        }
    }

    boolean showBill() {
        boolean res = false;
        if(attacheFilter.length() == 0)
            attacheFilter = filterStr(doc.getData());

        for(FileStorage fs : DbReader.fetch(FileStorage.class, attacheFilter)) {
            try {
                res = true;
                String fn = new String(fs.data);
                File file = new File(fn);
                Uri uri = FileProvider.getUriForFile(this, getString(R.string.fileprovider_authorities), file);

                Intent intent = new Intent("android.intent.action.VIEW")
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .setDataAndType(uri, fs.type)
                        .setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(intent);
            } catch(Exception e) {
//                MessageBox.show(OrderDetailEx.this, "Ошибка", e.getLocalizedMessage());
                Toast.makeText(this, "Нет программы для просмотра счета", Toast.LENGTH_SHORT).show();
            }
            break;
        }
        return res;
    }

    void requestBill() {
        if(doc.isEmpty()) {
            Toast.makeText(this, "Необходимо добавить товар", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!doc.isExported()) {
            Toast.makeText(this, "Необходимо отправить заказ перед запросом счета", Toast.LENGTH_SHORT).show();
            return;
        }

        if(showBill()) {
            return;
        }

        UpdateProcess up = new Upd(this, (start, result) -> {
            runOnUiThread(() -> {
                findViewById(R.id.progress).setVisibility(start ? View.VISIBLE : View.GONE);
                if(result) {
                    if(!showBill()) {
                        if(resultHitching.result().message.length() > 0)
                            MessageBox.show(OrderDetailEx.this, "Ошибка", resultHitching.result().message);
                    }
                }
            });
        });

        Config cfg = ConfigManager.getConfig();
        UpdateProcess.Params arg = new UpdateProcess.Params();
        arg.login = cfg.login;
        arg.pass = cfg.passw;
        arg.ip1 = cfg.address;
        arg.ip2 = cfg.address2;
        arg.port1 = cfg.port;

        if(attacheFilter.length() == 0)
            attacheFilter = filterStr(doc.getData());

        BillRequest br = new BillRequest();
        br.created = doc.getData().created;
        br.fileId = billId(doc.getData());

        if(!doc.isExported()) {
            arg.outdata.add(new DocSendListner(OrderDoc.instance().getObjectName(), doc));
        }

        DataObjectSendHitching dosh = new DataObjectSendHitching(br, "BillRequest");
        arg.outdata.add(dosh);
        arg.indata.add(new HitchOnSelect(FileStorage.class, "FileStorage", attacheFilter));
        arg.rcvdata.add(resultHitching);


        up.execute(arg);
    }
}
