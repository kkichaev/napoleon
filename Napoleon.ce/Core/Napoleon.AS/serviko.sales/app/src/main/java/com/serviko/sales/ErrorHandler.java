package com.serviko.sales;

import android.app.Activity;
import android.widget.Toast;

import com.serviko.dataobjects.ws.ErrResult;
import com.serviko.dataobjects.ws.SOAPFault;

public class ErrorHandler {
    public static boolean handleError(final Activity context, boolean result, Object response) {
        final String text;
        if(!result) {
            text = ((SOAPFault) response).message;
        } else {
            ErrResult res = (ErrResult) response;
            if(!res.result)
                text = res.error;
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
