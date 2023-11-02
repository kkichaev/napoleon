package com.grsoft.napoleon;

import static com.grsoft.napoleon.documents.DocType.SumConverter;

import android.app.Activity;
import android.text.Html;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.Consts;

public class OrderDetailEx extends OrderDetail {

    public String getTotalSumStr(long sum, long sumStock, int weight, int count) {
        StringBuilder sb = new StringBuilder();

        if( weight != 0 || count != 0 ) {
            sb.append("<i>");

            if( count != 0 )
                sb.append(Integer.toString(count));
            sb.append(" ");
            sb.append(getCountText());
            if( weight != 0 ) {
                if( sb.length() > 0 )
                    sb.append(", ");

                sb.append(DocType.getCurDoc().weightToString(weight, getString(R.string.kg)));
            }

            sb.append("</i><br>");
        }

        sb.append("<b>");
        sb.append(SumConverter.toString(sumStock));
        sb.append(" / ");
        sb.append(SumConverter.toString(sum));
        sb.append("</b>");

        return sb.toString();
    }


    @Override
    public void updateTotalSum(long sum, int weight, int count) {
        long sumStock = countStockSum();
        String text = getTotalSumStr(sum, sumStock, weight, count);
        TextView tv = findViewById(R.id.tvTotalSum);
        tv.setText(Html.fromHtml(text));
    }

    private long countStockSum() {
        long sum = 0;
        PriceImpl pi = new PriceImpl();
        Price p = pi.getData();
        for(OrderItem oi : doc.getData().items) {
            p.id = oi.id;
            if(pi.read()) {
                int qty = doc.getItemValue(p);
                if( qty > oi.qty )
                    qty = oi.qty;

                sum += ((long) oi.cost * qty) / Consts.QTY_SCALE;
            }
        }
        pi.close();

        return sum;
    }
}
