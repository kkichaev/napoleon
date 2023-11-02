#pragma once
#include "common.h"
#include <vector>

class DeviceData;

enum TcType {
    eTcUNDEF = 0,
    eTcBUTTON = 1,
    eTcTextView,
    eTcEditText,
    eTcRadioButton,
    eTcCheckBox,
    eTcProgBar,
    eTcGraph,
    eTCIconView,
    eTcListbox,
    eTcVerticalProgressBar,
    eTcSlider,
    eTcDropdown,
    eTcLed,
    eTcVerticalSlider,
    ///-- grphic drawing objet at end of list
    eTcLine,
    eTcRec,
    eTcCircle,
    eTcImage,
    eTcStateDisplay,
    eTcMultiTextState,
};

enum TcAction {
    eNo_Action, eSendValue, eJmpToScreen, eRunProc, eReturnTabIndex
};

typedef struct {
    uint16_t type;
    uint8_t tabOrder;
    uint8_t fontIx;
    uint8_t textAlign;
    uint8_t valid : 1;
    uint8_t updated : 1;
    uint16_t x;
    uint16_t y;
    uint16_t w;
    uint16_t h;
    uint16_t action;
    uint32_t registerId;
    char *text;
    int32_t value;
    uint32_t color;
    uint32_t image;
    uint32_t backColor;
    //	Register_t * regPtr ;
    uint16_t extra[12];
} TcObject;

typedef struct {
    uint16_t type; //  0 the object type
    uint8_t fill; // true or false
    uint8_t penWidth;
    uint16_t x; // 5 x position
    uint16_t y; // 7 yposition
    uint16_t w; // 9 width
    uint16_t h; // 11 hieght
    uint32_t penColor;
    uint32_t fillColor;
} TcDrawObject;

struct BaseControl {
    unsigned left, top;
    unsigned width, height;

    unsigned regID;

    DWORD bkColor;

    //    std::string name;
    std::string value;

    BaseControl() {
        left = top = width = height = 0;
        regID = 0;
    }

    virtual ~BaseControl() {
    }

    virtual void OnValueChanged() {
    }
};

class Control : public BaseControl {
public:

    Control() {
    }

    Control(const TcObject& src);

    virtual ~Control() {
    }

    virtual bool MakeWindow(GtkWidget* parent) {
        return false;
    }

    virtual void Paint(cairo_t* surface) const {
    }

    static Control* Create(const TcObject& src);
};

class Screen : public Control {
public:

    Screen(const DeviceData* device) {
        this->device = device;
        this->screen = NULL;
    }

    virtual ~Screen();

    void AddControl(Control* control) {
        controls.push_back(control);
    }

    bool MakingScreen();

    void Paint(GtkWidget* window, cairo_t* surface) const;

    bool Render(DataBuffer* out) const;

    //    Control* FindControl(const std::string& name) const;

    void SetValues(const ValuesMap& values);

    void SetDrawObjects(const DataBuffer& data);
    void SetObjects(const DataBuffer& data);
    void SetStrings(const DataBuffer& data);

    bool NoScreenData() const { return (objects.size() == 0) && (controls.size() == 0); }
    
protected:
    void ClearControls();

//    static gboolean OnDraw(GtkWidget *widget, cairo_t *cr, gpointer user_data);

protected:
    std::vector<Control*> controls;

    const DeviceData* device;

    std::vector<TcDrawObject> drawObjects;
    std::vector<TcObject> objects;
    std::map<uint32_t, std::string> strings;

    const char* name;
    DWORD bkColor;
    GtkWidget* screen;
    
    Mutex screenMutex;
};

COLORREF DecodeColor(const char *value, bool fontColor);

