package com.grsoft.napoleon.modules.print;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactoryImp;
import com.itextpdf.text.pdf.BaseFont;

class FontProvider extends FontFactoryImp {
    private float texSize;

    public FontProvider(float textSize) {
        this.texSize = textSize;
    }

    public Font getFont(String fontName, String encoding, boolean embedded, float size, int style, BaseColor color, boolean cached) {
        Font res = super.getFont(fontName, encoding, embedded, size, style, color, cached);

        try {
            res = new Font(BaseFont.createFont(PrintForm.FONT_NAME, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), texSize, style, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
