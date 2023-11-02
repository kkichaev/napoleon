package com.grsoft.napoleon.printsources;

import android.content.Context;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.modules.print.DataSource;
import com.grsoft.napoleon.modules.print.util.Dig2Str;
import com.grsoft.types.DateFormat;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderPrint {
    public static String SUM_TEXT_FORMAT = "%s руб. %02d коп.";

    protected SupplSource supplSource;
    protected OrderPrintItems items;
    protected Order order;

    public String created;
    public String number;

    @DateFormat(format="dd.MM.yyyy")
    public Date date;
    public String inn = "";
    public String address = "";
    public String name = "";
    public String phone = "";
    public String bank = "";
    public String factAddress = "";
    public String okpo = "";

    public String payAddress = "";
    public String payName = "";
    public String payPhone = "";
    public String payBank = "";
    public String payInn = "";

    public String sumText = "";
    public String qtyText = "";
    public String taxText = "";
    public String sum = "";
    public String numText = "";
    public String brutto = "";
    public String weight = "";
    public String bruttoText = "";
    public String nettoText = "";
    public String linesFromTo = "";

    public int totalPage = 1;
    public String TotalPageText;
    @Scale(value= Consts.QTY_SCALE, hideRest=true)
    public int packQty = 0;

    protected int totalSum = 0;
    protected int totalPack = 0;

    public OrderPrint(Order order){
        this.order = order;

        items = createPrintItems();

        initSupplyer(order);

        int index = 1;
        int totalWeight = 0;
        int totalBrutto = 0;

        Map<Integer, Integer> nds = new HashMap<Integer, Integer>();
        linesFromTo = "1";
        if(order.items.size() > 1) {
            linesFromTo += "-" + Integer.toString(order.items.size());
        }

        for(OrderItem item : order.items){
            OrderItemPrint dip = createItemPrint(order, index, item);
            items.add(dip);
            packQty += dip.pack;
            totalSum += dip.isum;
            totalPack += dip.iqty;
            totalWeight += dip.iweight;
            totalBrutto += dip.ibrutto;

            if (dip.itax > 0) {
                if (nds.containsKey(dip.itax))
                    nds.put(dip.itax, nds.get(dip.itax) + dip.isumtax);
                else
                    nds.put(dip.itax, dip.isumtax);
            }
            index++;
        }

        weight = Util.IntToScaleStr(totalWeight, Consts.WEIGHT_SCALE);
        brutto = Util.IntToScaleStr(totalBrutto, Consts.WEIGHT_SCALE);

        if( totalWeight > 0 )
            nettoText = getWeightText(totalWeight);
        if( totalBrutto > 0 )
            bruttoText = getWeightText(totalBrutto);

        OrgImpl org = new OrgImpl();
        Org op = (Org) org.getData();
        op.id = order.id;
        org.read();
        org.close();

        initOrg(op);

        sum = Util.IntToScaleStr(totalSum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
        sumText = String.format(SUM_TEXT_FORMAT, Dig2Str.digToText(totalSum /  Consts.SUM_SCALE),
                totalSum % Consts.SUM_SCALE);
        qtyText = Dig2Str.digToText(totalPack / Consts.QTY_SCALE);

        for(Map.Entry<Integer, Integer> entry : nds.entrySet()){
            taxText += String.format(
                    "НДС %d%%: %s,", entry.getKey(),
                    Util.IntToScaleStr(entry.getValue(), Consts.SUM_SCALE, Util.DEC_DELIM, false));
        }

        if (taxText.length() > 0)
            taxText = taxText.substring(0, taxText.length()-1);

        numText = Dig2Str.digToText(index -1);
        TotalPageText = Dig2Str.digToText(totalPage);
    }

    protected String getWeightText(int totalWeight) {
        return String.format(Locale.getDefault(), "%s кг. %d г.", Dig2Str.digToText(totalWeight / Consts.WEIGHT_SCALE),
                totalWeight % Consts.WEIGHT_SCALE);
    }

    protected OrderItemPrint createItemPrint(Order order, int index,
                                             OrderItem item) {
        return new OrderItemPrint(item, index, order.sumType);
    }

    protected OrderPrintItems createPrintItems() {
        return new OrderPrintItems(this);
    }

    protected void initOrg(Org op) {
        // адрес грузополучателя - фактический
        address = op.address; //(op.legalAddress != null && op.legalAddress.length() > 0) ? op.legalAddress : op.address;
        factAddress = op.address;
        name = (op.fullName != null && op.fullName.length() > 0) ? op.fullName : op.name;
        phone = op.phone;
        bank = op.bank;
        inn = op.inn;
        okpo = op.okpo;

        // плательщик - берем юр.адрес
        payAddress = (op.legalAddress != null && op.legalAddress.length() > 0) ? op.legalAddress : op.address;;
        payName = (op.fullName != null && op.fullName.length() > 0) ? op.fullName : op.name;;
        payPhone = phone;
        payBank = bank;
        payInn = inn;
    }

    protected void initSupplyer(Order order) {
        supplSource = createSupplSource();
        supplSource.setSupplyer(order.firmCode);
    }

    protected SupplSource createSupplSource() {
        return new SupplSource();
    }

    public void initSource(Context context, int res) {
        totalPage = 1;
        items.init(context, res);
    }

    public void init(){
        created = Util.simpleDateFormat.format(order.created);
        number = ((OrderEx)order).pnum;
        date = order.date;
    }

    public boolean getValue(StringBuilder value, String name, String format) {
//		return SilentReflector.getFieldValue(value, name, this);
        return SilentReflector.getFieldValue(value, name, items, format) ||
                SilentReflector.getFieldValue(value, name, this, format);
    }

    public SupplSource getSupplSorce(){
        return supplSource;
    }

    public DataSource getItems(){
        return items;
    }

    public void startPage() {
        totalPage++;
        TotalPageText = Dig2Str.digToText(totalPage);
    }
    public void calculate(OrderItemPrint sip) {}
}
