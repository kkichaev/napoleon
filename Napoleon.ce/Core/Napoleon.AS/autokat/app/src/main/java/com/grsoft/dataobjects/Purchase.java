package com.grsoft.dataobjects;

import com.grsoft.database.ListTypeConvertor;
import com.grsoft.database.TableInfo;
import com.grsoft.database.ServerInfo;

@TableInfo(name="Purchase", keyFields = "created")
@ServerInfo(name="Purchase")
public class Purchase extends Order  implements ListTypeConvertor {
    static final int CUR_LENGTH = 66 + 4 + 4 + 4 + 4; // id,cost,flags,qty,weight
    static final int NEW_LENGTH = 66 + 8 + 4 + 4 + 8; // id,cost,flags,qty,weight
    static final int MIDDLE_LENGTH = 66 + 8 + 4 + 4 + 4; // id,cost,flags,qty,weight
    static final int HEAD_LENGTH = 14;
    static final int FIRST_REC = 0x60;
    static final int FIRST_REC_NEW = 0x68;
    static final int FIRST_REC_MIDDLE = 0x64;
    // 6 bytes header
    // 4 size
    // 4 version

    public String payType = "";


    static private int getInt(byte[] stream, int idx){
        return (stream[idx++] << 24) |
                ((stream[idx++] << 16) & 0xFF0000) |
                ((stream[idx++] << 8) & 0xFF00) |
                (stream[idx++] & 0xFF);
    }

    void putInt(byte[] stream, int offset, int val) {
        stream[offset++] = (byte)((val & 0xFF000000) >> 24);
        stream[offset++] = (byte)((val & 0xFF0000) >> 16);
        stream[offset++] = (byte)((val & 0xFF00) >> 8);
        stream[offset++] = (byte)((val & 0xFF));
    }

    //static byte[] SCND_REC = new byte[] { 0x30, 0, 0x61, 0 };

    boolean containsParten(byte[] src, int offset, int len) {
        for(int i=0; i<len; i++) {
            byte el = src[i+offset];
            if((i % 2) == 0) {
                if(el == 0)
                    return false;
            } else {
                if(el != 0)
                    return false;
            }
        }

        return true;
    }

    @Override
    public byte[] convert(byte[] src, String fieldName) {
        if(!fieldName.equals("items") || src.length < FIRST_REC) {
            return src;
        }

        int count = getInt(src, 6);
        int totLength = src.length - HEAD_LENGTH;
        if(CUR_LENGTH * count == totLength) {
            return src;
        }
        if(containsParten(src, FIRST_REC, 6)) {
            count = totLength / CUR_LENGTH;
            putInt(src, 6, count);
            return src;
        }

        boolean isMiddle = containsParten(src, FIRST_REC_MIDDLE, 6);
        boolean isNew = !isMiddle && containsParten(src, FIRST_REC_NEW, 6);
        if(!isNew && !isMiddle) {
            return src;
        }

        int clen = isNew ? NEW_LENGTH : MIDDLE_LENGTH;

        count = totLength / clen;
        putInt(src, 6, count);

        int newcb = count * CUR_LENGTH + HEAD_LENGTH;
        byte[] dest = new byte[newcb];
        System.arraycopy(src, 0, dest, 0, HEAD_LENGTH);
        int offsetSrc = HEAD_LENGTH;
        int offsetDest = HEAD_LENGTH;
        int i;
        for(i=0; i<count; i++) {
            int rest = src.length - offsetSrc;
            if(rest < MIDDLE_LENGTH)
                break;

            isNew = false;
            isMiddle = rest == MIDDLE_LENGTH || containsParten(src, offsetSrc + MIDDLE_LENGTH, 6);
            if(rest >= NEW_LENGTH)
                isNew = !isMiddle && (rest == NEW_LENGTH || containsParten(src, offsetSrc + NEW_LENGTH, 6));

            System.arraycopy(src, offsetSrc, dest, offsetDest, 66); // id
            offsetSrc += 66 + 4;
            offsetDest += 66;
            System.arraycopy(src, offsetSrc, dest, offsetDest, 4); // cost
            offsetSrc += 4;
            offsetDest += 4;
            System.arraycopy(src, offsetSrc, dest, offsetDest, 4); //flags
            offsetSrc += 4;
            offsetDest += 4;
            System.arraycopy(src, offsetSrc, dest, offsetDest, 4); // qty
            offsetSrc += 4;
            if(isNew)
                offsetSrc += 4;
            offsetDest += 4;
            System.arraycopy(src, offsetSrc, dest, offsetDest, 4); // weight
            offsetSrc += 4;
            offsetDest += 4;
        }
        if(count > i) {
            count = i;
            putInt(src, 6, count);
        }
        return dest;
    }
}
