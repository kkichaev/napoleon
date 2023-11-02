#include "gtkservice.h"

static int get_length(unsigned char ch) {
    int l;
    unsigned char c = ch;
    c >>= 3;
    // 6 => 0x7e
    // 5 => 0x3e
    if (c == 0x1e) {
        l = 4;
    } else {
        c >>= 1;
        if (c == 0xe) {
            l = 3;
        } else {
            c >>= 1;
            if (c == 0x6) {
                l = 2;
            } else {
                l = 1;
            }
        }
    }
    return l;
}

wchar_t* ToUnicode(const std::string& utf8) {
    unsigned char *p = (unsigned char *)utf8.c_str();
    wchar_t* result = (wchar_t*) malloc(sizeof(wchar_t)*(utf8.size() + 1));
    wchar_t *r = result;
    if (!result) {
        return NULL;
    }
    
    while (*p) {
        wchar_t ch;
        int l = get_length(*p);

        switch (l) {
            case 4:
                ch = (*p ^ 0xf0);
                break;
            case 3:
                ch = (*p ^ 0xe0);
                break;
            case 2:
                ch = (*p ^ 0xc0);
                break;
            case 1:
                ch = *p;
                break;
        }
        ++p;
        int y;
        for (y = l; y > 1; y--) {
            ch <<= 6;
            ch |= (*p ^ 0x80);
            ++p;
        }
        *r = ch;
        r++;
    }
    *r = 0x0;
    return result;
}

unsigned char *to_utf8(wchar_t* unicode) {
    unsigned char *utf8 = NULL;
    wchar_t* s = unicode;
    unsigned char *u;
    wchar_t ch;
    int x = 0;
    while (*s) {
        ++s;
        ++x;
    }
    if (x == 0) {
        return NULL;
    }
    utf8 = (unsigned char *) malloc(x * 4);
    if (!utf8)
        return NULL;

    s = unicode;
    u = utf8;
    x = 0;

    while (*s) {
        ch = *s;
        if (*s < 0x80) {
            x = 1;
            *u = *s;
            u++;
        } else if (*s < 0x800) {
            x = 2;
            *u = 0xc0 | (ch >> 6);
            u++;
        } else if (*s < 0x10000) {
            x = 3;
            *u = 0xe0 | (ch >> 12);
            u++;
        } else if (*s < 0x200000) {
            x = 4;
            *u = 0xf0 | (ch >> 18);
            u++;
        }
        if (x > 1) {
            int y;
            for (y = x; y > 1; y--) {
                /*
                unsigned long mask = 0x3f << ((y-2)*6);
                 *u = 0x80 | (ch & mask);
                 */
                *u = 0x80 | (ch & (0x3f << ((y - 2)*6)));
                ++u;
            }
        }
        ++s;
    }
    return utf8;
}



int DrawTextWithFont(int index, HDC hDC, int color, const char* text, int offset, int left, int top, int width, int height, int alignh, int alignv) {
    return 0;
    //    USES_CONVERSION;
    //    wchar_t *dest = A2W_CP(text, CP_UTF8);
    //
    //    if (index < 0)
    //        return 0;
    //    return DrawTextWFont(index, hDC, color, dest, offset, left, top, width, height, alignh, alignv);
}
