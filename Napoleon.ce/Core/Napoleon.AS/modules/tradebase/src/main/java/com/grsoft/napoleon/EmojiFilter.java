package com.grsoft.napoleon;

import android.text.InputFilter;
import android.text.Spanned;

public class EmojiFilter {
    public static InputFilter[] getFilter()
    {
        InputFilter EMOJI_FILTER = (source, start, end, dest, dstart, dend) -> {
            for (int index = start; index < end; index++) {

                int type = Character.getType(source.charAt(index));

                if (type == Character.SURROGATE || type==Character.NON_SPACING_MARK
                        || type==Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        };
        return new InputFilter[]{EMOJI_FILTER};
    }
}
