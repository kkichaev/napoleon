package com.grsoft.napoleon;

public class BarcodeData {
    public String itemBC = "";
    public int cost = 0;
    public boolean isItemCode = true;
    public  boolean havError = false;

    public BarcodeData(String bc) {
        isItemCode = !bc.startsWith("01");
        try {
            if(isItemCode) {
                itemBC = bc.substring(0, 14);
                cost = decodeCost(bc.substring(21, 25));
            } else {
                itemBC = bc.substring(2, 16);
                int idx = bc.indexOf("8005");
                if(idx > 0) {
                        cost = Integer.parseInt(bc.substring(idx + 4, idx + 10));
                }
            }
        } catch (Exception e) {
            havError = true;
            e.printStackTrace();
        }
    }

    public int decodeCost(String costStr) {
        String symbase = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!\"%&'*+-./_,:;=<>?";

        int res = 0;
        for(int i=0; i<4; i++) {
            int idx = symbase.indexOf(costStr.substring(i,i+1));
            if(idx >= 0) {
                res += (int)Math.pow(80, (3- i)) * idx;
            }
        }
        return res;
    }
}
