package com.novotek.sales;

import android.app.Activity;
import android.widget.Toast;

import com.novotek.dataobjects.ws.ErrResult;
import com.novotek.dataobjects.ws.JSONFault;

public class ErrorHandler {
    public static boolean handleError(final Activity context, boolean result, Object response) {
        final String text;
        if(!result) {
            text = ((JSONFault) response).message;
        } else {
            ErrResult res = (ErrResult) response;
            if(res.error != 0)
                text = res.message;
            else
                text = null;
        }

        if(text != null) {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                }
            });
            return true;
        }

        return false;
    }
}
