package com.ashberrysoft.leadertask.modern.dialog;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.android.vending.billing.IInAppBillingService;
import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static com.ashberrysoft.leadertask.application.Config.IN_APP_ID;
import static com.ashberrysoft.leadertask.modern.activity.SlidingActivity.mAmount;
import static com.ashberrysoft.leadertask.modern.activity.SlidingActivity.mCurrency;

public class BuyDlg {
    public void buyForExtension(Activity context, IInAppBillingService billing) {
        final View v = LayoutInflater.from(context).inflate(R.layout.buy_for_extend_dialog, null);
        LTSettings setting = LTSettings.getInstance();
        View button = v.findViewById(R.id.want_to_buy);
        //
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LTSettings setting = LTSettings.getInstance();
                if (billing != null && setting.iCanBuyLeadertask) {
                    ArrayList skuList = new ArrayList();
                    skuList.add(IN_APP_ID);
                    Bundle querySkus = new Bundle();
                    querySkus.putStringArrayList("ITEM_ID_LIST", skuList);
                    Bundle skuDetails;
                    try {
                        Bundle ownedItems = billing.getPurchases(3, context.getPackageName(), "inapp", null);
                        // Check response
                        int responseCode = ownedItems.getInt("RESPONSE_CODE");
                        if (responseCode != 0) {
                        }
                        // Get the list of purchased items
                        ArrayList<String> purchaseDataList =
                                ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
                        for (String purchaseData : purchaseDataList) {
                            JSONObject o = new JSONObject(purchaseData);
                            String purchaseToken = o.optString("token", o.optString("purchaseToken"));
                            // Consume purchaseToken, handling any errors
                            billing.consumePurchase(3,  context.getPackageName(), purchaseToken);
                        }
                        skuDetails = billing.getSkuDetails(3,  context.getPackageName(), "inapp", querySkus);
                        int response = skuDetails.getInt("RESPONSE_CODE");
                        if (response == 0) {
                            ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");
                            for (String thisResponse : responseList) {
                                JSONObject object = new JSONObject(thisResponse);
                                String sku = object.getString("productId");
                                String amount = object.getString("price");
                                StringBuilder sb = new StringBuilder();
                                for (int i=0; i < amount.length(); i++) {
                                    char c = amount.charAt(i);
                                    if (Character.isDigit(c)) {
                                        sb.append(c);
                                    } else {
                                        if (c == ",".charAt(0)){
                                            sb.append(c);
                                        } else {
                                            if (c == ".".charAt(0)){
                                                sb.append(",");
                                            }
                                        }
                                    }
                                }
                                mAmount = sb.toString();
                                mCurrency = object.getString("price_currency_code");
                                if (sku.equals(IN_APP_ID)) {
                                    Bundle buyIntentBundle = billing.getBuyIntent(3, context.getPackageName(), sku, "inapp", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                                    if ((int) buyIntentBundle.get("RESPONSE_CODE") == 0) { // если можно купить
                                        PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                                        context.startIntentSenderForResult(pendingIntent.getIntentSender(), 1002, new Intent(), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {

                    }
                } else {
                    Utils.openBrowserToBuy(setting, context);
                }
            }
        });
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(v);
        builder.show();
    }
}
