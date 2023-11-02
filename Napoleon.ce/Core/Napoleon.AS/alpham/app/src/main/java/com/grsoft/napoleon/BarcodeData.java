package com.grsoft.napoleon;

public class BarcodeData {
	public String itemBC = "";
	
    public int cost = 0;
    public boolean isItemCode = true;
    public boolean isBox = false;
    public  boolean haveError = false;

    public String incomeCode = "";

    public BarcodeData(String bc) {
        isItemCode = !bc.startsWith("01");
        try {
            if(isItemCode) {
                itemBC = bc.substring(0, 14);
                if(bc.length() >= 26)
                    cost = decodeCost(bc.substring(21, 25));
            } else {
                itemBC = bc.substring(2, 16);
                int idx = bc.indexOf("8005");
                if(idx > 0) {
                    String costCode = bc.substring(idx + 4, idx + 10);
                    incomeCode = itemBC + costCode;
                    cost = Integer.parseInt(costCode);
                } else {
                    idx = bc.indexOf("240");
                    if(idx > 0 && bc.length() >= idx + 12) {
                        String boxCode = bc.substring(idx + 3, idx + 11);
                        incomeCode = itemBC + boxCode;
                        itemBC = incomeCode;
                        isBox = true;
                    } else {
                        // 010468006229125500RRCXIPI0000000000010756
                        // 
//                        isItemCode = true;
                        cost = 0;
                    }
                }
            }
        } catch (Exception e) {
            haveError = true;
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
