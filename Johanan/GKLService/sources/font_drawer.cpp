#include "common.h"
#include "bidi/bidirefp.h"
#include "fonts/GUI_FontIntern.h"

#include <vector>
#include <stdlib.h>
#include <string.h>
#include <wchar.h>
#include <cairo/cairo.h>

//const char* bidiFolder = "/var/lib/gklservice/bidi/";
const char* bidiFolder = "./";
extern "C" {
    UBACTXTPTR br_ConstructContext(int len, U_Int_32 *text, Paragraph_Direction paraDirection);
    void br_DropContext(UBACTXTPTR ctxt);
};

// fonts defs

struct FontDefInt {
    const char* name;
    const GUI_FONT *font;
    BOOL isRTL;
};

struct POINT {
    unsigned x;
    unsigned y;
};


static std::vector<FontDefInt> fonts;

void RegisterFont(const char* name, const GUI_FONT* font, BOOL isRTL) {
    FontDefInt fd;
    fd.name = name;
    fd.font = font;
    fd.isRTL = isRTL;

    fonts.push_back(fd);
}

#define REGISTER_FONT(x, isRTL) RegisterFont(# x, &x, isRTL)

void RegisterAvailFonts() {
    //	&GUI_Font8_ASCII,
    //	&GUI_Font10_ASCII,
    //	&GUI_Font13_ASCII,
    //	&GUI_Font13B_ASCII,
    //	&GUI_Font16_ASCII,
    //	&GUI_Font16B_ASCII,
    //	&GUI_Font24_ASCII,
    //	&GUI_Font24B_ASCII,
    //	&GUI_Font32_ASCII,
    //	&GUI_Font32B_ASCII,
    //	&GUI_FontArabic24,
    //	&GUI_FontDigitsAriel72,
    //	&GUI_FontDigitsAriel84,
    //	&GUI_FontDengXian24,
    //	&GUI_FontFontSymbol

    REGISTER_FONT(GUI_Font8_ASCII, FALSE);
    REGISTER_FONT(GUI_Font10_ASCII, FALSE);
    REGISTER_FONT(GUI_Font13_ASCII, FALSE);
    REGISTER_FONT(GUI_Font13B_ASCII, FALSE);
    REGISTER_FONT(GUI_Font16_ASCII, FALSE);
    REGISTER_FONT(GUI_Font16B_ASCII, FALSE);
    REGISTER_FONT(GUI_Font24_ASCII, FALSE);
    REGISTER_FONT(GUI_Font24B_ASCII, FALSE);
    REGISTER_FONT(GUI_Font32_ASCII, FALSE);
    REGISTER_FONT(GUI_Font32B_ASCII, FALSE);
    REGISTER_FONT(GUI_FontArabic24, TRUE);
    REGISTER_FONT(GUI_FontDIgitsArial72, TRUE);
    REGISTER_FONT(GUI_FontDIgitsArial72, TRUE); // miss GUI_FontDigitsAriel84
    REGISTER_FONT(GUI_FontDengXian24, TRUE);
    REGISTER_FONT(GUI_FontFontSymbol, TRUE);

    //    REGISTER_FONT(GUI_Font8_1, FALSE);
    //
    //    REGISTER_FONT(GUI_Font8x10_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font8x12_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font8x13_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font8x13_1, FALSE);
    //    REGISTER_FONT(GUI_Font8x15B_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font8x15B_1, FALSE);
    //
    //    REGISTER_FONT(GUI_Font8x16, FALSE);
    //    REGISTER_FONT(GUI_Font8x17, FALSE);
    //    REGISTER_FONT(GUI_Font8x18, FALSE);
    //
    //    REGISTER_FONT(GUI_Font8x16x1x2, FALSE);
    //    REGISTER_FONT(GUI_Font8x16x2x2, FALSE);
    //    REGISTER_FONT(GUI_Font8x16x3x3, FALSE);
    //
    //    REGISTER_FONT(GUI_Font4x6, FALSE);
    //    REGISTER_FONT(GUI_Font6x8, FALSE);
    //    REGISTER_FONT(GUI_Font6x9, FALSE);
    //    REGISTER_FONT(GUI_Font8x8, FALSE);
    //    REGISTER_FONT(GUI_Font8x9, FALSE);
    //
    //    REGISTER_FONT(GUI_FontD24x32, FALSE);
    //    REGISTER_FONT(GUI_FontD32, FALSE);
    //    REGISTER_FONT(GUI_FontD36x48, FALSE);
    //    REGISTER_FONT(GUI_FontD48, FALSE);
    //    REGISTER_FONT(GUI_FontD48x64, FALSE);
    //    REGISTER_FONT(GUI_FontD64, FALSE);
    //    REGISTER_FONT(GUI_FontD60x80, FALSE);
    //    REGISTER_FONT(GUI_FontD80, FALSE);
    //
    //    REGISTER_FONT(GUI_FontComic18B_ASCII, FALSE);
    //    REGISTER_FONT(GUI_FontComic18B_1, FALSE);
    //    REGISTER_FONT(GUI_FontComic24B_ASCII, FALSE);
    //    REGISTER_FONT(GUI_FontComic24B_1, FALSE);
    //
    //
    //    REGISTER_FONT(GUI_Font10S_1, FALSE);
    //    REGISTER_FONT(GUI_Font10S_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font10_1, FALSE);
    //    REGISTER_FONT(GUI_Font13_1, FALSE);
    //    REGISTER_FONT(GUI_Font13B_1, FALSE);
    //    REGISTER_FONT(GUI_Font13H_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font13H_1, FALSE);
    //    REGISTER_FONT(GUI_Font13HB_ASCII, FALSE);
    //    REGISTER_FONT(GUI_Font13HB_1, FALSE);
    //    REGISTER_FONT(GUI_Font16_1, FALSE);
    //    REGISTER_FONT(GUI_Font16B_1, FALSE);
    //    //REGISTER_FONT(GUI_Font20_ASCII, FALSE);
    //    //REGISTER_FONT(GUI_Font20_1, FALSE);
    //    //REGISTER_FONT(GUI_Font20B_ASCII, FALSE);
    //    //REGISTER_FONT(GUI_Font20B_1, FALSE);
    //    REGISTER_FONT(GUI_Font24_1, FALSE);
    //    REGISTER_FONT(GUI_Font24B_1, FALSE);
    //    REGISTER_FONT(GUI_Font32_1, FALSE);
    //    REGISTER_FONT(GUI_Font32B_1, FALSE);

}

static const GUI_CHARINFO* FindLetter(const GUI_FONT_PROP* charProps, wchar_t sym) {
    const GUI_CHARINFO *ret = 0;
    while (charProps != NULL) {
        if ((U16P) sym >= charProps->First && (U16P) sym <= charProps->Last) {
            ret = &charProps->paCharInfo[(U16P) sym - charProps->First];
            break;
        }

        charProps = charProps->pNext;
    }
    return ret;
}

static int GetLetterWidth(const GUI_FONT_PROP* charProps, wchar_t sym) {
    const GUI_CHARINFO* ci = FindLetter(charProps, sym);
    return ( ci != NULL) ? ci->XDist : 0;
}

static const GUI_CHARINFO_EXT* FindLetter(const GUI_FONT_PROP_EXT* charProps, wchar_t sym) {
    const GUI_CHARINFO_EXT* ret = NULL;
    while (charProps != NULL) {
        if ((U16P) sym >= charProps->First && (U16P) sym <= charProps->Last) {
            ret = &charProps->paCharInfo[(U16P) sym - charProps->First];
            break;
        }

        charProps = charProps->pNext;
    }
    return ret;
}

static int GetLetterWidth(const GUI_FONT_PROP_EXT* charProps, wchar_t sym) {
    const GUI_CHARINFO_EXT* ci = FindLetter(charProps, sym);
    return (ci != NULL) ? ci->XDist : 0;
}

static const unsigned char* FindLetter(const GUI_FONT_MONO* charProps, wchar_t sym, int height) {
    if (sym >= charProps->FirstChar && sym <= charProps->LastChar)
        return charProps->pData + (sym - charProps->FirstChar) * (charProps->BytesPerLine * height);
    return 0;
}

static int GetLetterWidth(const GUI_FONT_MONO* charProps, wchar_t sym) {
    if (sym >= charProps->FirstChar && sym <= charProps->LastChar)
        return charProps->XDist;
    return 0;
}

inline int GetLetterWidth(const FontDefInt& fd, wchar_t sym) {
    switch (fd.font->type) {
        case GUI_FONTTYPE_PROP:
            return GetLetterWidth((GUI_FONT_PROP*) fd.font->charProps, sym);
        case GUI_FONTTYPE_PROP_EXT:
            return GetLetterWidth((GUI_FONT_PROP_EXT*) fd.font->charProps, sym);
        case GUI_FONTTYPE_MONO:
            return GetLetterWidth((GUI_FONT_MONO*) fd.font->charProps, sym);
    }
    return 0;
}

static POINT GetTextSize(const FontDefInt& fd, const wchar_t* text) {
    POINT ret = {0, 0};
    ret.y = fd.font->height;

    const wchar_t* sp = text;
    for (; *sp != 0; sp++) {
        int x = GetLetterWidth(fd, *sp);
        ret.x += x;
    }

    return ret;
}

static int RenderSym(const FontDefInt& fd, wchar_t sym, COLORREF color, unsigned char* bits, int curX, int lineSize, int height) {
    int cp = curX;
    unsigned char r = GetRValue(color);
    unsigned char b = GetBValue(color);
    unsigned char g = GetGValue(color);
    
    if (fd.font->type == GUI_FONTTYPE_PROP) {
        const GUI_CHARINFO* ci = FindLetter((const GUI_FONT_PROP*) fd.font->charProps, sym);
        if (ci != NULL) {
            if ((ci->XSize + cp) * 4 > lineSize)
                return -1;

            const unsigned char *data = ci->pData;

            for (int line = 0; line < fd.font->height && line < height; line++) {
                unsigned char *dest = bits + cp * 4 + line * lineSize;
                for (int bpl = 0; bpl < ci->BytesPerLine; bpl++) {
                    int mask = 0x80;
                    for (; mask > 0; mask >>= 1) {
                        if ((*data & mask) != 0) {
                            *dest++ = b;
                            *dest++ = g;
                            *dest++ = r;
                            *dest++ = 0xFF;
                        } else {
                            dest += 4;
                        }
                    }
                    data++;
                }
            }

            cp += ci->XDist;
        }
    } else if (fd.font->type == GUI_FONTTYPE_PROP_EXT) {
        const GUI_CHARINFO_EXT* ci = FindLetter((const GUI_FONT_PROP_EXT*) fd.font->charProps, sym);
        if (ci != NULL) {
            if ((ci->XSize + cp) * 4 > lineSize || ci->YPos >= height)
                return -1;

            const unsigned char *data = ci->pData;
            int bytesPerLine = (ci->XSize + 7) / 8;

            for (int line = 0; line < ci->YSize && line < height; line++) {
                unsigned char *dest = bits + (cp + ci->XPos) * 4 + line * lineSize;
                //                unsigned char *dest = bits + (cp + ci->XPos) * 3 + (height - line - 1 - ci->YPos) * lineSize;
                int cnt = 0;
                for (int bpl = 0; bpl < bytesPerLine; bpl++) {
                    int mask = 0x80;
                    for (; mask > 0 && cnt < ci->XSize; mask >>= 1, cnt++) {
                        if ((*data & mask) != 0) {
                            *dest++ = b;
                            *dest++ = g;
                            *dest++ = r;
                            *dest++ = 0xFF;
                        } else {
                            dest += 4;
                        }
                    }
                    data++;
                }
            }

            cp += ci->XDist;
        }
    } else if (fd.font->type == GUI_FONTTYPE_MONO) {
        const GUI_FONT_MONO *fontProps = (const GUI_FONT_MONO *) fd.font->charProps;
        const unsigned char* data = FindLetter(fontProps, sym, fd.font->height);
        if (data != NULL) {
            if ((fontProps->XSize + cp) * 4 > lineSize || fd.font->height > height)
                return -1;

            int bytesPerLine = (fontProps->XSize + 7) / 8;

            for (int line = 0; line < fd.font->height && line < height; line++) {
                unsigned char *dest = bits + cp * 4 + line * lineSize;
                int cnt = 0;
                for (int bpl = 0; bpl < bytesPerLine; bpl++) {
                    int mask = 0x80;
                    for (; mask > 0 && cnt < fontProps->XSize; mask >>= 1, cnt++) {
                        if ((*data & mask) != 0) {
                            *dest++ = b;
                            *dest++ = g;
                            *dest++ = r;
                            *dest++ = 0xFF;
                        } else {
                            dest += 4;
                        }
                    }
                    data++;
                }
            }

            cp += fontProps->XDist;
        }
    }

    return cp;
}

static POINT RasterBitmap(const FontDefInt& fd, const wchar_t* text, COLORREF color, unsigned char* bits,
        int lineSize, int textOffseet, int width, int height, int alignh, int alignv) {
    POINT textSize = GetTextSize(fd, text);

    int yoffset = 0, xoffset = textOffseet;
    if (alignv == 1) {
        yoffset = height - fd.font->height;
    } else if (alignv == 2) {
        yoffset = (height - fd.font->height) / 2;
    }
    if (yoffset < 0)
        yoffset = 0;

    if (alignh == 1 || (fd.isRTL && alignh == 0)) {
        xoffset = width - textSize.x;
    } else if (alignh == 2) {
        xoffset = (width - textSize.x) / 2;
    }

    memset(bits, 0, lineSize * height);

    int cp = xoffset;
    while (*text != 0) {
        unsigned char* linep = bits + yoffset * lineSize;
        
        int nextX = RenderSym(fd, *text, color, linep, cp, lineSize, height);
        if (nextX < 0)
            break;
        cp = nextX;
        text++;
    }

    return textSize;
}

static void PutSymbol(wchar_t sym, int symNewPos, wchar_t* text, int* orders, int len) {
    wchar_t prevSym = text[symNewPos];
    text[symNewPos] = sym;
    orders[symNewPos] = -1;

    int posPrevSym = -1;
    for (int i = 0; i < len; i++)
        if (orders[i] == symNewPos) {
            posPrevSym = i;
            break;
        }
    if (posPrevSym != -1)
        PutSymbol(prevSym, posPrevSym, text, orders, len);
}

static bool ReorderText(wchar_t* text) {
    int rc = br_Init(UBA63, bidiFolder);
    if (rc == -1)
        return false;

    int i = 0;
    int len = wcslen(text) + 1;
    U_Int_32 *src = (U_Int_32*) malloc(len * sizeof (U_Int_32));
    for (i = 0; i < len; i++) {
        if (text[i] >= 0xD800 && text[i] <= 0xDBFF) {
            U_Int_32 dest = ((U_Int_32) (text[i++] - 0xD7FF)) << 16;
            dest += (text[i] - 0xDC00);
            src[i] = dest;
        } else
            src[i] = text[i];
    }

    bool ret = false;
    UBACTXTPTR ctxt = br_ConstructContext(len - 1, src, Dir_Auto);
    if (ctxt != NULL) {
        rc = br_UBA(ctxt);

        if (rc != -1) {
            int* orders = (int*) malloc((len - 1) * sizeof (int));
            memset(orders, -1, (len - 1) * sizeof (int));

            BIDIUNITPTR bdu = ctxt->theText;
            BIDIUNITPTR endOfText = ctxt->theText + ctxt->textLen;
            i = 0;
            while (bdu < endOfText) {
                if (bdu->order != NOLEVEL)
                    orders[i++] = bdu->order;
                bdu++;
            }
            br_DropContext(ctxt);

            for (i = 0; i < len - 1; i++) {
                int symPos = orders[i];
                if (symPos == -1)
                    continue;

                wchar_t newSym = text[symPos];
                PutSymbol(newSym, i, text, orders, len);
            }
            ret = true;
        }
    }

    free(src);

    return ret;
}

bool DrawTextWithFont(int index, cairo_t* sfc, DWORD color, const wchar_t* text,
        unsigned textOffset, unsigned left, unsigned top, unsigned width, unsigned height, int alignh, int alignv) {
    if (index < 0 || index >= (int) fonts.size())
        return false;

    cairo_surface_t *src_sf = cairo_image_surface_create(CAIRO_FORMAT_ARGB32, width, height);    
    int stride = cairo_image_surface_get_stride(src_sf);
    unsigned char* buf = cairo_image_surface_get_data(src_sf);

    FontDefInt& fd = fonts[index];
    const wchar_t* textSrc = text;
    if (fd.isRTL) {
        textSrc = (wchar_t*)alloca((wcslen(text) + 1) * sizeof (wchar_t));
        wcscpy((wchar_t*)textSrc, text);
        ReorderText((wchar_t*)textSrc);
    }
    POINT textSize = RasterBitmap(fd, textSrc, color, buf, stride, textOffset, width, height, alignh, alignv);
    cairo_surface_mark_dirty(src_sf);
    
    cairo_set_source_surface(sfc, src_sf, left, top);
    cairo_mask_surface(sfc, src_sf, left, top);
    
    cairo_surface_destroy(src_sf);

    return true;
}
