#include <cairo/cairo.h>

#include "gtkservice.h"
#include "controls.h"

//#include <atlconv.h>

inline DWORD RGB(const unsigned char* p) {
    unsigned char r = *p++;
    unsigned char g = *p++;
    unsigned char b = *p++;
    return ((DWORD) r << 16) | ((DWORD) g << 8) | b;
}

inline uint32_t adjColor(uint32_t color) {
    return color;
}

inline uint32_t getImageAddr(uint32_t addr) {
    return 0;
}

static unsigned char* SetTCObject(TcObject* dst, unsigned char *ptr, const std::map<uint32_t, std::string>& strings) {
    uint32_t tmp32;
    
    dst->type = *(uint16_t *) ptr; ptr += 2;
    dst->tabOrder = *ptr; ptr++;
    dst->fontIx = *ptr; ptr++;
    dst->textAlign = *ptr; ptr++;
    ptr++; // dst valid & updated
    dst->x = *(uint16_t *) ptr; ptr += 2;
    dst->y = *(uint16_t *) ptr; ptr += 2;
    dst->w = *(uint16_t *) ptr; ptr += 2;
    dst->h = *(uint16_t *) ptr; ptr += 2;
    dst->action = *(uint16_t *) ptr; ptr += 2;
    dst->registerId = *(uint32_t *) ptr; ptr += 4;   
    
    uint32_t txtPtr = *(uint32_t *) ptr; ptr += 4;
    std::map<uint32_t, std::string>::const_iterator fnd = strings.find(txtPtr);
    if(fnd != strings.end())
        dst->text = (char*)fnd->second.c_str();
    else
        dst->text = (char*)"";
    
    dst->value = *(int32_t *) ptr; ptr += 4;   
    dst->color = RGB(ptr); ptr += 4;   
    dst->image = *(uint32_t *) ptr; ptr += 4;
    dst->backColor = RGB(ptr); ptr += 4;   

    unsigned char* endPtr = ptr + sizeof(((TcObject*)0)->extra); // extra size
    
    switch (dst->type) {
        case eTcCheckBox:
        case eTcBUTTON:
            dst->extra[0] = *(uint16_t *) ptr;
            ptr += 2; // chkbox bit number, button prunProccess number
            dst->extra[1] = dst->value; // store button valu if needed for button sendValue action, run proc parameter, and jmp to screen
            break;
        case eTcRadioButton:
        case eTcProgBar:
        case eTcVerticalProgressBar:
        case eTcSlider:
            dst->extra[0] = *(uint16_t *) ptr;
            ptr += 2; // radio: count, bars: minimum, drop: number of itmes
            dst->extra[1] = *(uint16_t *) ptr;
            ptr += 2; // radio: distance ,bars: maximum drop: number of Max itmes
            // extra 2,3 is left color
            tmp32 = adjColor(*(uint32_t *) ptr);
            ptr += 4;
            *(uint32_t *) (&dst->extra[2]) = tmp32;
            // extra 4,5 is right color
            tmp32 = adjColor(*(uint32_t *) ptr);
            ptr += 4;
            *(uint32_t *) (&dst->extra[4]) = tmp32;
            dst->extra[6] = *(uint16_t *) ptr;
            ptr += 2; // progbar text options
            break;
        case eTcListbox:
        case eTcDropdown:
            dst->extra[0] = *(uint16_t *) ptr;
            ptr += 2; //  dropdown items number, listbox it type of history, alrarmlog..
            break;
        case eTcGraph:
            dst->extra[0] = *(uint16_t *) ptr;
            ptr += 2;
            // if 24 hr , make X size a multiply of 24
            //            if (dst->extra[0] == eDailyTemp) {
            //                dst->w = ((dst->w + DAILY_GR_N / 2) / DAILY_GR_N) * DAILY_GR_N;
            //            }

            break;
        case eTcLed:
            dst->extra[0] = *(uint16_t *) ptr;
            ptr += 2; // Led bit number and blink yes/no at bit 31
            tmp32 = *(uint32_t *) ptr;
            ptr += 4; // index in flash of string that is the offimage file name
            *(uint32_t *)&dst->extra[1] = (uint32_t) getImageAddr(tmp32);
            tmp32 = *(uint32_t *) ptr;
            ptr += 4; // index in flash of string that is the onimage file name
            *(uint32_t *)&dst->extra[3] = (uint32_t) getImageAddr(tmp32);
            break;
        case eTcImage:
            tmp32 = *(uint32_t *) ptr;
            ptr += 4; // file size
            *(uint32_t *)&dst->extra[0] = tmp32;
            dst->extra[2] = *(uint16_t *) ptr;
            ptr += 2; //  bmp  or jpg (0 or 1)
            break;
        case eTcStateDisplay:
        case eTcMultiTextState:
            dst->extra[0] = *(uint16_t *) ptr;
            ptr += 2; // number of itmes
            tmp32 = *(uint32_t *) ptr;
            ptr += 4; // file size
            *(uint32_t *)&dst->extra[1] = tmp32; // string, Scolor, of itme colors " FF0055; ..."

            break;
        default:
            break;
    }
    
    return endPtr;
}

Control::Control(const TcObject& src) {
    left = src.x;
    top = src.y;
    width = src.w;
    height = src.h;

    regID = src.registerId;
//    if(regID != 0)
//        PutLog("ctrl %u", regID);
    
    bkColor = src.backColor;
}

static inline int GetAlignH(int align) {
    return ((align & 3) == 0) ? ALIGN_LEFT : 
        ((align & 3) == 1) ? ALIGN_RIGHT :
            ALIGN_CENTER;
}

static inline int GetAignV(int align) {
    return ((align & 0xC) == 0) ? ALIGN_TOP :
        ((align & 0xC) == 8) ? ALIGN_BOTTOM:
            ALIGN_MIDDLE;
}

class TextViewControl : public Control {
public:

    TextViewControl(const TcObject& src) : Control(src) {
        color = src.color;
        font = src.fontIx;
        alignh = GetAlignH(src.textAlign);
        alignv = GetAignV(src.textAlign);
        value = src.text;
    }

    virtual void Paint(cairo_t* surface) const {
//        cairo_set_source_rgb(surface, 0, 0, 0);
//        cairo_rectangle(surface, left, top, width, height);
//        cairo_stroke(surface);

        wchar_t* str = ToUnicode(value);
        DrawTextWithFont(font, surface, color, str, 0, left, top, width, height, alignh, alignv);
        free(str);
    }

protected:
    COLORREF color;
    int font;
    int alignh, alignv;
};

class TextBoxControl : public TextViewControl {
public:

    TextBoxControl(const TcObject& src) : TextViewControl(src) {
        bkColor = 0xFFFFFF;
        alignh = ALIGN_CENTER;
    }

    virtual void Paint(cairo_t* surface) const {
        cairo_set_source_rgb(surface, 0, 0, 0);
        cairo_rectangle(surface, left, top, width, height);
        cairo_stroke(surface);
        
        cairo_set_source_rgb(surface, GetRValue(bkColor)/ 255.0, GetGValue(bkColor)/ 255.0, GetBValue(bkColor) / 255.0);
        cairo_rectangle(surface, left + 1, top + 1, width - 2, height - 2);
        cairo_fill(surface);
        
        int offset = 2;

        wchar_t* str = ToUnicode(value);
        DrawTextWithFont(font, surface, color, str, 1, left + offset, top + offset, width - offset, height - offset, alignh, alignv);

        free(str);
    }

};

class CheckBoxControl : public TextViewControl {
public:

    CheckBoxControl(const TcObject& src) : TextViewControl(src) {
    }

    virtual void Paint(cairo_t* surface) const {
        int offset = 20;
        const_cast<CheckBoxControl*>(this)->left += offset;
        const_cast<CheckBoxControl*>(this)->width -= offset;

        TextViewControl::Paint(surface);

        const_cast<CheckBoxControl*>(this)->width += offset;
        const_cast<CheckBoxControl*>(this)->left -= offset;
    }

    virtual bool MakeWindow(GtkWidget* parent) {
        GtkWidget* wnd = gtk_check_button_new();
        gtk_fixed_put(GTK_FIXED(parent), wnd, left, top);
    }
};

class HSliderControl : public Control {
public:

    HSliderControl(const TcObject& src) : Control(src) {
        min = src.extra[0];
        max = src.extra[1];
    }

    virtual bool MakeWindow(GtkWidget* parent) {
        GtkWidget* wnd = gtk_scale_new_with_range(GTK_ORIENTATION_HORIZONTAL, min, max, (max - min) / 10.0);
        gtk_fixed_put(GTK_FIXED(parent), wnd, left, top);
        gtk_widget_set_size_request(wnd, width, height);
    }

protected:
    uint16_t min, max;
};

class RadioButtonControl : public TextViewControl {
public:

    RadioButtonControl(const TcObject& src) : TextViewControl(src) {
        count = src.extra[0];
        distance = src.extra[1];
    }

    virtual void Paint(cairo_t* surface) const {
        int offset = 20;
        int ctop = top;

        int start = 0;
        int end = value.find(';');
        for (int i = 0; i < count; i++, ctop += distance) {
            wchar_t* str = ToUnicode(value.substr(start, ((end == std::string::npos) ? value.size() : end) - start));
            DrawTextWithFont(font, surface, color, str, 0, left + offset, ctop, width - offset, distance, alignh, 0);
            free(str);
            if (end == std::string::npos)
                break;
            start = end + 1;
            end = value.find(';', start);
        }
    }

    virtual bool MakeWindow(GtkWidget* parent) {
        GSList* list = NULL;
        int ctop = top;
        for (int i = 0; i < count; i++) {
            GtkWidget* wnd = gtk_radio_button_new(list);
            gtk_fixed_put(GTK_FIXED(parent), wnd, left, ctop);

            if (list == NULL)
                list = gtk_radio_button_get_group(GTK_RADIO_BUTTON(wnd));
            ctop += distance;
        }
    }

protected:
    int count, distance;
};

class ButtonControl : public TextViewControl {
public:
    ButtonControl(const TcObject& src) : TextViewControl(src) {
        alignh = ALIGN_CENTER;
        
        wcolor.alpha = 1;
        wcolor.red = (double)GetRValue(src.backColor)/ 255.0;
        wcolor.blue = (double)GetBValue(src.backColor)/ 255.0;
        wcolor.green = (double)GetGValue(src.backColor)/ 255.0;
    }
    
    virtual void Paint(cairo_t* surface) const {
//        gtk_render_background(gtk_widget_get_style_context(wnd), surface, 0, 0, width, height);
        gtk_container_propagate_draw(GTK_CONTAINER(parent), wnd, surface);
        
        wchar_t* str = ToUnicode(value);
        DrawTextWithFont(font, surface, color, str, 0, left, top, width, height, alignh, alignv);
        free(str);
    }
    
    virtual bool MakeWindow(GtkWidget* parent) {
        this->parent = parent;
        
        if(provider == NULL) {
            provider = GTK_STYLE_PROVIDER (gtk_css_provider_new ());
            gtk_css_provider_load_from_data((GtkCssProvider*)provider, "* \n{ padding: 1px; }", -1, NULL);
        }
        
//        GtkStyleProvider *provider = GTK_STYLE_PROVIDER (gtk_css_provider_new ());
//        gtk_css_provider_load_from_data((GtkCssProvider*)provider, "button.color {padding: 1px; }", -1, NULL);
        
        
//        wnd = gtk_button_new();
        wnd = gtk_color_button_new_with_rgba(&wcolor);
        gtk_widget_set_size_request(wnd, width, height);

//        gtk_style_context_reset_widgets (gdk_screen_get_default ());
//        gtk_style_context_add_provider (gtk_widget_get_style_context (wnd), provider, G_MAXUINT);
        
//        gtk_widget_override_color(wnd, GTK_STATE_FLAG_NORMAL, &wcolor);
        
//        gtk_widget_override_background_color(wnd, GTK_STATE_FLAG_NORMAL, &wcolor);
        gtk_fixed_put(GTK_FIXED(parent), wnd, left, top);
        gtk_style_context_add_provider (gtk_widget_get_style_context (wnd), provider, G_MAXUINT);

//        while (gtk_events_pending ())
//          gtk_main_iteration ();    
    }
    
protected:
    GtkWidget* parent;
    GtkWidget* wnd;
    GdkRGBA wcolor;
    
    static GtkStyleProvider *provider;
};

GtkStyleProvider* ButtonControl::provider = NULL;

Control* Control::Create(const TcObject& src) {
    Control* ret = NULL;

    switch (src.type) {
        case eTcTextView:
            ret = new TextViewControl(src);
            break;
        case eTcEditText:
            ret = new TextBoxControl(src);
            break;
        case eTcCheckBox:
            ret = new CheckBoxControl(src);
            break;
        case eTcRadioButton:
            ret = new RadioButtonControl(src);
            break;
        case eTcSlider:
            ret = new HSliderControl(src);
            break;
        case eTcBUTTON:
            ret = new ButtonControl(src);
            break;
    }

    return ret;
}

bool Screen::MakingScreen() {
    if(controls.size() > 0)
        return true;
    
    if(!screenMutex.TryLock())
        return false;
    
    ClearControls();
    
    std::vector<TcObject>::const_iterator i = objects.begin();
    for( ; i != objects.end(); i++) {
        Control* ctrl = Control::Create(*i);
        if (ctrl != NULL)
            controls.push_back(ctrl);
    }
    objects.clear();
    strings.clear();
    
    if (screen != NULL)
        gtk_widget_destroy(screen);

    screen = gtk_fixed_new();
    gtk_widget_set_size_request(screen, width, height);

    std::vector<Control*>::iterator ci = controls.begin();
    for (; ci != controls.end(); ci++)
        (*ci)->MakeWindow(screen);

    
    screenMutex.Unlock();
    return true;
}

Screen::~Screen() {
    ClearControls();

    if (screen != NULL) {
        gtk_widget_destroy(screen);
        screen = NULL;
    }
}

void Screen::ClearControls() {
    std::vector<Control*>::iterator i = controls.begin();
    for (; i != controls.end(); i++)
        delete (*i);

    controls.clear();
}

void Screen::SetDrawObjects(const DataBuffer& data) {
    drawObjects.clear();
    unsigned char* p = data.data;
    unsigned char *ep = p + data.size;

    while (p < ep) {
        TcDrawObject dobj;

        dobj.type = *(uint16_t*) p; p += 2;
        dobj.fill = *p; p++;
        dobj.penWidth = *p; p++;
        dobj.x = *(uint16_t*) p; p += 2;
        dobj.y = *(uint16_t*) p; p += 2;
        dobj.w = *(uint16_t*) p - dobj.x; p += 2;
        dobj.h = *(uint16_t*) p - dobj.y; p += 2;
        dobj.penColor = *(uint32_t*) p; p += 4;
        dobj.fillColor = *(uint32_t*) p; p += 4;
        drawObjects.push_back(dobj);
    }
}

void Screen::SetObjects(const DataBuffer& data) {
    objects.clear();
    unsigned char* p = data.data;
    unsigned char *ep = p + data.size;

    while (p < ep) {
        TcObject dobj;

        bool isScreenObject = (p == data.data);
        
        p = SetTCObject(&dobj, p, strings);
        if(isScreenObject) {
            this->bkColor = dobj.backColor;
            this->left = dobj.x;
            this->top = dobj.y;
            this->width = dobj.w;
            this->height = dobj.h;
        } else
            objects.push_back(dobj);
    }
    
    MakingScreen();
}

void Screen::SetStrings(const DataBuffer& data) {
    strings.clear();
    unsigned char* p = data.data;
    unsigned char *ep = p + data.size;

    while (p < ep) {
        uint32_t id = *(uint32_t*) p;
        p += 4;

        std::string val;
        while (*p && p < ep)
            val.append(1, *p++);
        strings[id] = val;
        if (*p == '\0')
            p++;

    }
}
//Control* Screen::FindControl(const std::string& name) const {
//    std::vector<Control*>::const_iterator i = controls.begin();
//    for (; i != controls.end(); i++)
//        if ((*i)->name.compare(name) == 0)
//            return (*i);
//
//    return NULL;
//}

void Screen::SetValues(const ValuesMap& values) {
    std::vector<Control*>::iterator i = controls.begin();
    for (; i != controls.end(); i++) {
        Control* c = (*i);
        if (c->regID == 0)
            continue;

        ValuesMap::const_iterator fnd = values.find(c->regID);
        if (fnd != values.end()) {
            const std::string& value = fnd->second;
            if (c->value.compare(value) != 0) {
                c->value = value;
                c->OnValueChanged();
            }
        }
    }
}