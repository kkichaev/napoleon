package com.grsoft.napoleon;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceQtyEx;
import com.grsoft.dataobjects.PriceQtyItem;
import com.grsoft.dataobjects.SpecTask;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.Date;

public class PriceCountEx extends PriceCount{

    int quant = 0;

    @Override
    protected int getContentViewId() {return R.layout.pricecountex;}

    @Override
    protected void refreshData() {
        super.refreshData();

        PriceEx pe = (PriceEx) price.getData();
        int rrc = pe.costRRC;
        ((TextView)findViewById(R.id.tvCostRRC)).setText(Util.IntToScaleStr(rrc, Consts.SUM_SCALE, Util.DEC_DELIM, false));

        String task = pe.task;
        if(task.length() > 0) {
            findViewById(R.id.specTask).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvSpecTask)).setText(SpecTask.get(pe.task));
        } else {
            findViewById(R.id.specTask).setVisibility(View.GONE);
        }

        int idx = 0;
        if(document instanceof OrderImpl) {
            idx = ((OrderImpl)document).getData().whIndex;
        }
        Date arDate = new Date();
        int arQty = 0;
        if(idx == 0) {
            arDate = pe.date;
            arQty = pe.arrival;
        } else if(idx <= pe.whQty.size()) {
            PriceQtyEx pqe = (PriceQtyEx) pe.whQty.get(idx-1);
            arDate = pqe.date;
            arQty = pqe.qty;
        }

//        if(BuildConfig.DEBUG) {
//            arQty = 10 * Consts.QTY_SCALE;
//            quant = 3;
//        }

        quant = pe.quant;
        ((TextView)findViewById(R.id.tvMinQty)).setText(Integer.toString(quant));

        if(arQty == 0) {
            findViewById(R.id.arrivalDate).setVisibility(View.GONE);
        } else {
            findViewById(R.id.arrivalDate).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tvArrivalDate)).setText(Util.simpleDateFormat.format(arDate));
            ((TextView)findViewById(R.id.tvArrivalQty)).setText(Util.IntToScaleStr(arQty, Consts.QTY_SCALE));
        }
    }

    @Override
    protected boolean isInputValid(Runnable r) {
        int qty = qtyItems;
        qty = fixOrderQty(cbPackets.isChecked(), qty, price.getData());
        if(qty < quant * Consts.QTY_SCALE) {
            Toast.makeText(this, "Введенное количество меньше минимальной продажи", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
