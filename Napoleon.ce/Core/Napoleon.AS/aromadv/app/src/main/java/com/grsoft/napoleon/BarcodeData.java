package com.grsoft.napoleon;

public class BarcodeData {
    public String itemBC = "";
    public int cost = 0;
    public boolean isItemCode = true;
    public boolean isBox = false;
//    public boolean mayByBoxed = false;
    public  boolean haveError = false;

    public String checkItemCode = "";

    public String incomeCode = "";

    public BarcodeData(String bc) {
        isItemCode = !bc.startsWith("01") && !bc.startsWith("02");
        try {
            if(isItemCode) {
                itemBC = bc.substring(0, 14);
                cost = decodeCost(bc.substring(21, 25));
            } else {
                itemBC = bc.substring(2, 16);
                int idx = bc.indexOf("8005");
                if(idx > 0 && bc.length() >= idx + 10) {
                    String costCode = bc.substring(idx + 4, idx + 10);
                    incomeCode = itemBC + costCode;
                    cost = Integer.parseInt(costCode);
                } else {
                    idx = bc.indexOf("240");
                    if(idx > 0) {
                        String boxCode = bc.substring(idx + 3, idx + 11);
                        incomeCode = itemBC + boxCode;
                        itemBC = incomeCode;
                        isBox = true;
                    } else {
                        if(haveBlockData(bc, "21") && haveBlockData(bc, "93")) {
//                            isBox = true;
                        } else {
                            isItemCode = true;
//                            mayByBoxed = true; // для выбора между сигариллами и коробками без указания серий
                            checkItemCode = bc.substring(0, 14);
                            cost = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            haveError = true;
            e.printStackTrace();
        }
    }

    boolean haveBlockData(String bc, String blockCode) {
        int idx = bc.indexOf(blockCode);
        return idx >= 0 && bc.length() > idx;
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
