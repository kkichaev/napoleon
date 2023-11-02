#include "gtkservice.h"

#include <vector>

#include <stdlib.h>
#include <stdio.h>
//#include <io.h>
#include <fcntl.h>
#include <cairo/cairo.h>


#include "controls.h"
//#include <atlconv.h>

struct BuffChunk {
    unsigned cb;
    void* bytes;

    BuffChunk() {
        bytes = NULL;
        cb = 0;
    }

    void Free() {
        free(bytes);
        bytes = NULL;
    }

    void Alloc(unsigned cb) {
        this->cb = cb;
        bytes = malloc(cb);
    }
};

//static bool opened = false;
//static void Dump(const void* data, unsigned len) {
//    FILE *wr = fopen("./log.dmp", (opened) ? "ab" : "wb");
//    if(wr) {
//        opened = true;
//        //fprintf(wr, "\nLen %d\n", len);
//        fwrite(data, sizeof(char), len, wr);
//        fclose(wr);
//    }    
//}

class BufChunks {
protected:
    std::vector<BuffChunk*> chunks;
public:

    ~BufChunks() {
        std::vector<BuffChunk*>::iterator i = chunks.begin();
        for (; i != chunks.end(); i++)
            (*i)->Free();

        chunks.clear();
    }

    void Add(const void* data, unsigned len) {
        BuffChunk* bc = new BuffChunk();
        bc->Alloc(len);
        memcpy(bc->bytes, data, len);
        chunks.push_back(bc);
        
//        PutLog("Alloc %d", len);
    }

    unsigned Size() const {
        unsigned len = 0;
        std::vector<BuffChunk*>::const_iterator i = chunks.begin();
        for (; i != chunks.end(); i++)
            len += (*i)->cb;

//        PutLog("Totals %d", len);
        return len;
    }

    void CopyTo(DataBuffer* b) const {
        b->Alloc(Size());
        unsigned char* cp = b->data;
        std::vector<BuffChunk*>::const_iterator i = chunks.begin();
        for (; i != chunks.end(); i++) {
            memcpy(cp, (*i)->bytes, (*i)->cb);
            cp += (*i)->cb;
        }
//        Dump(b->data, b->size);
    }

    static cairo_status_t WriteData(void *closure, const unsigned char *data, unsigned int length) {
        BufChunks* buf = (BufChunks*) closure;
        buf->Add(data, length);

        return CAIRO_STATUS_SUCCESS;
    }

};

//static gboolean DrawOnSurface(GdkWindow* window, cairo_t *cr) {
//    gint width = gdk_window_get_width(window);
//    gint height = gdk_window_get_height(window);
//
//    // set background
//    cairo_rectangle(cr, 0, 0, width, height);
//    cairo_set_source_rgb(cr, 0.8, 0.8, 0.8);
//    cairo_fill(cr);
//
//    int top = 80;
//    height = 30;
//
//    int wdh = 150;
//    DrawTextWithFont(43, cr, 0xFF, L"test 1", 10, top, wdh, height, 0, 0);
//
//    top += height + 10;
//    DrawTextWithFont(45, cr, 0xFF00, L"test 2", 10, top, wdh, height, 0, 0);
//
//    top += height + 10;
//    DrawTextWithFont(55, cr, 0xFF0000, L"ان   مئات ", 10, top, wdh, height, 0, 0);
//
//    //    PutLog("drawing...");
//    return FALSE;
//}
//
//GtkWidget* makeWindow() {
//    GtkWidget *window;
//
//    window = gtk_fixed_new();
//    gtk_widget_set_size_request(window, 800, 600);
//
//    GtkWidget *lbl1;
//    lbl1 = gtk_label_new(NULL);
//    gtk_label_set_markup(GTK_LABEL(lbl1), "<b>Hello</b> <i>Raspberry</i>");
//    gtk_label_set_justify(GTK_LABEL(lbl1), GTK_JUSTIFY_CENTER);
//
//    gtk_fixed_put(GTK_FIXED(window), lbl1, 0, 0);
//    gtk_widget_set_size_request(lbl1, 140, 40);
//
//    lbl1 = gtk_label_new(NULL);
//    gtk_label_set_markup(GTK_LABEL(lbl1), "<b>Well</b> <i>do it!</i>");
//    gtk_label_set_justify(GTK_LABEL(lbl1), GTK_JUSTIFY_CENTER);
//
//    gtk_fixed_put(GTK_FIXED(window), lbl1, 0, 40);
//    gtk_widget_set_size_request(lbl1, 140, 40);
//
//    return window;
//}
//
//void MakeScreenShot(GtkWidget *window, DataBuffer* out) {
//    // add window to offscreen
//    GtkWidget* offWnd = gtk_offscreen_window_new();
//    gtk_container_add(GTK_CONTAINER(offWnd), window);
//    gtk_widget_show_all(offWnd);
//
//    // create surface
//    cairo_surface_t *surface;
//    cairo_t *cr;
//    GdkWindow *gdk_window = gtk_widget_get_window(offWnd);
//    gint width = gdk_window_get_width(gdk_window);
//    gint height = gdk_window_get_height(gdk_window);
//    surface = cairo_image_surface_create(CAIRO_FORMAT_RGB24, width, height);
//    cr = cairo_create(surface);
//
//
//    // draw window
//    BufChunks *bf = new BufChunks();
//    DrawOnSurface(gdk_window, cr);
//    gtk_widget_draw(window, cr);
//    cairo_surface_write_to_png_stream(surface, WriteData, bf);
//    bf->CopyTo(out);
//    delete bf;
//
//    // cleanup
//    cairo_destroy(cr);
//    cairo_surface_destroy(surface);
//    g_object_ref(window);
//    gtk_container_remove(GTK_CONTAINER(offWnd), window);
//    gtk_widget_destroy(offWnd);
//}

static void PaintDrawObject(const TcDrawObject& obj, cairo_t* surface) {
    switch(obj.type) {
        case eTcRec:
            cairo_rectangle(surface, obj.x, obj.y, obj.w, obj.h);
            cairo_set_source_rgb(surface, (double) GetRValue(obj.fillColor) / 255.0, (double) GetGValue(obj.fillColor) / 255.0, (double) GetBValue(obj.fillColor) / 255.0);
            cairo_fill(surface);
            
//            if(obj.penWidth > 0) {
//                cairo_set_line_width(surface, obj.penWidth);
//                cairo_rectangle(surface, obj.x, obj.y, obj.w, obj.h);
//                cairo_set_source_rgb(surface, (double) GetRValue(obj.penColor) / 255.0, (double) GetGValue(obj.penColor) / 255.0, (double) GetBValue(obj.penColor) / 255.0);
//                cairo_stroke(surface);
//            }
            break;
    }
    
}

bool Screen::Render(DataBuffer* out) const {
    if( screen == NULL )
        return false;
    
    const_cast<Screen*>(this)->screenMutex.Lock();
    
    
    // add window to offscreen
//    GtkWidget* offWnd = gtk_window_new(GTK_WINDOW_TOPLEVEL);// gtk_offscreen_window_new();
    GtkWidget* offWnd = gtk_offscreen_window_new();
    gtk_window_set_default_size (GTK_WINDOW (offWnd), width, height);
    gtk_container_add(GTK_CONTAINER(offWnd), screen);

    GdkRGBA wcolor;
    wcolor.alpha = 1;
    wcolor.red = (double)GetRValue(bkColor) / 255.0;
    wcolor.blue = (double)GetBValue(bkColor) / 255.0;
    wcolor.green = (double)GetGValue(bkColor) / 255.0;
    gtk_widget_override_background_color(offWnd, GTK_STATE_FLAG_NORMAL, &wcolor);
    
//    GdkRGBA wcolor;
        
    gtk_widget_show_all(offWnd);
    
//    gtk_main();
//    while (gtk_events_pending ())
//      gtk_main_iteration ();    

    // create surface
    cairo_surface_t *surface = cairo_image_surface_create(CAIRO_FORMAT_RGB24, width, height);
    cairo_t *cr = cairo_create(surface);

    // draw window
    Paint(offWnd, cr);
        
    cairo_surface_write_to_png(surface, "./test.png");

    BufChunks *bf = new BufChunks();
    cairo_surface_write_to_png_stream(surface, BufChunks::WriteData, bf);
    bf->CopyTo(out);
    delete bf;

    // cleanup
    cairo_destroy(cr);
    cairo_surface_destroy(surface);
    g_object_ref(screen);

    gtk_container_remove(GTK_CONTAINER(offWnd), screen);
    gtk_widget_destroy(offWnd);

    const_cast<Screen*>(this)->screenMutex.Unlock();
    return true;
}

void Screen::Paint(GtkWidget* window, cairo_t* surface) const {
    gtk_widget_draw(window, surface);
    
//    cairo_rectangle(surface, 0, 0, width, height);
//    cairo_set_source_rgb(surface, (double) GetRValue(bkColor) / 255.0, (double) GetGValue(bkColor) / 255.0, (double) GetBValue(bkColor) / 255.0);
//    cairo_fill(surface);
//    
    std::vector<TcDrawObject>::const_iterator di = drawObjects.begin();
    for (; di != drawObjects.end(); di++)
        PaintDrawObject(*di, surface);

    std::vector<Control*>::const_iterator i = controls.begin();
    for (; i != controls.end(); i++)
        (*i)->Paint(surface);
}

