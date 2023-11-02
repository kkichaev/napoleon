package com.grsoft.napoleon.debet_data;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.grsoft.dataobjects.OrgBalanceData;
import com.grsoft.napoleon.BalanceHelper;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.Date;

public class DocData  implements Comparable<DocData> {
    public String number;
    public long sum;
    public Date date;
    public Date payDate;
    public int overdueDays;

    public DocData(OrgBalanceData data, Date dueDate) {
        sum = data.sumD;
        number = data.number;
        payDate = data.payDate;
        date = data.date;
        overdueDays = 0;

        long pd = payDate.getTime();
        long cd = dueDate.getTime();
        if(pd < cd) {
            overdueDays = (int)((cd - pd) / (24 * 3600 * 1000));
        }
    }

    public int getColor() {
        return sum <= 0 ? Color.BLACK : BalanceHelper.getColorFromDueDays(overdueDays);
    }


    @Override
    public int compareTo(DocData another) {
        int cmp = another.date.compareTo(date);
        if( cmp != 0 )
            return cmp;

        return another.number.compareTo(number);
    }

    public void update(View convertView) {
        int color = getColor();
        TextView tv;
        String text = "";

        tv = (TextView)convertView.findViewById(R.id.tvText);
        tv.setText(Html.fromHtml(number));
        tv.setTextColor(color);

        text = overdueDays > 0 ? Integer.toString(overdueDays) : "";
        tv = (TextView)convertView.findViewById(R.id.tvDue);
        tv.setText(Html.fromHtml(text));
        tv.setTextColor(color);

        tv = (TextView)convertView.findViewById(R.id.tvDate);
        text = Util.simpleDateFormat.format(date);
        if( payDate != null )
            text += "<br/>" + Util.simpleDateFormat.format(payDate);
        tv.setText(Html.fromHtml(text));
        tv.setTextColor(color);

        tv = (TextView)convertView.findViewById(R.id.tvSum);
        tv.setText(Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false));
        tv.setTextColor(color);
    }
}
