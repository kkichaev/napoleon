package com.grsoft.napoleon;

import android.content.Context;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.PurchaseItem;
import com.grsoft.dataobjects.ScriptEx;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.PurchaseImpl;
import com.grsoft.dataobjects.impl.VisitImplEx;
import com.grsoft.napoleon.modules.print.util.Dig2Str;
import com.grsoft.napoleon.script_wizard.ScriptProp;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PrintData {
    public String agent_fio = "";
    public String region = "";
    public String date = "";
    public List<PrintDataItem> items = new ArrayList<>();
    public String firm_full_name = "";
    public String firm_ogrn = "";
    public String firm_inn = "";
    public String firm_okpo = "";
    public String fio = "";
    public String passport_seria = "";
    public String passport_number = "";
    public String passport_issue_date = "";
    public String passport_issue_org = "";
    public String passport_issue_code = "";
    public String agent_fio_short  = "";
    public String fio_short = "";
    public String number = "<b>1</b>";
    public String pay_type = "";
    public String rqty = "";
    public String rcost = "";
    public String rsum = "";
    public String firm_short_name = "";
    public String firm_address = "";
    public Date created;
    public String firm_name = "";
    public String firm_division_name = "";
    public String sumText = "";
    public String sign_path = "";

    public PrintData(Context context){
        date = Util.simpleDateFormat.format(Calendar.getInstance().getTime());
        AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();

        if(ap != null){
            region = ap.region.trim();
            firm_full_name = ap.firm_name.trim() + ", " + ap.firm_address.trim();
            firm_inn = innStr(context, ap.firm_inn.trim());
            firm_ogrn = ap.firm_ogrn.trim();
            agent_fio_short = fio_short(ap.name);
            agent_fio = ap.name;
            firm_short_name = ap.firm_short_name;
            firm_address = ap.firm_address;
            firm_name = ap.firm_name;
            firm_okpo = ap.firm_okpo;
            firm_division_name = ap.firm_division_name;
        }
    }

    private String innStr(Context context, String str) {
        StringBuilder sb = new StringBuilder();

        String vals[] = str.split("/");

        if (vals.length > 0)
            sb.append(context.getString(R.string.inn)).append(" ").append(vals[0]);

        if (vals.length > 1){
            if (sb.length() > 0)
                sb.append(" / ");
            sb.append(context.getString(R.string.kpp)).append(" ").append(vals[1]);
        }

        return sb.toString();
    }

    public void setPurchase(PurchaseImpl purchase) {
        long rqty = 0;
        double rcost = 0;
        long rsum = 0;
        DecimalFormat df = new DecimalFormat("#.##");

        if (purchase != null) {
            int pos = 1;
            PriceImpl price = new PriceImpl();

            PrintDataItem pca = new PrintDataItem();

            int weight = 0;

            for (OrderItem i : purchase.getData().items) {
                if (i.qty == 0) continue;
                PurchaseItem pi = (PurchaseItem) i;
                price.read("id", i.id);
                price.close();
                weight += pi.weight;
                rsum += pi.cost;

                if (items.size() == 0)
                    items.add(pca);
            }

            double qty = 1;
            double dw = ((double)weight) /  Consts.QTY_SCALE;

            if (dw > 0.5)
                qty = Math.round(dw / 0.8);

            double cost = Math.round(((double)rsum / Consts.SUM_SCALE) / qty);
            rqty = (long) qty;
            rcost = cost;
            pca.qty = df.format(rqty);
            pca.cost = df.format(rcost);
            pca.sum = Util.IntToScaleStr((int)rsum, Consts.SUM_SCALE);

        }

        this.rqty = df.format(rqty);
        this.rcost = df.format(rcost);
        this.rsum = Util.IntToScaleStr((int)rsum, Consts.SUM_SCALE);

        this.sumText = String.format("%s рублей", Dig2Str.digToText((long) rsum / Consts.SUM_SCALE));
    }

    public String fio_short(String fullName){
        String res = "";

        String[] arrFio = fullName.split(" ");
        if (arrFio.length > 0)
            res = arrFio[0];

        if (arrFio.length > 1)
            res = res + " " + arrFio[1].toCharArray()[0] + ".";

        if (arrFio.length > 2)
            res = res + " " + arrFio[2].toCharArray()[0] + ".";

        return res;
    }
    public void setScript(ScriptEx scr) {
        fio = scr.fio;
        fio_short = fio_short(fio);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        passport_seria = scr.passportSeria;
        passport_number = scr.passportNumber;
        passport_issue_date = sdf.format(scr.passportIssue);
        passport_issue_org = scr.issueOrg;
        passport_issue_code = scr.issueCode;
        pay_type = scr.payType;
        created = scr.created;

        VisitImplEx visit = new VisitImplEx();
        visit.read(scr.visitDoc.getTime());

        for(VisitItem i : visit.getData().items)
            if (((VisitItemEx)i).tag.equals(ScriptProp.SIGN_TAG)) {
                sign_path = new String(i.id);
                break;
            }
    }
}
