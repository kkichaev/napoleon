#include "gtkservice.h"
#include "common.h"

using namespace std;

#ifdef WIN32
#include <varargs.h>
#else
#include <stdarg.h>
#include <time.h>
#endif

const char KEEP_ALIVE_CMD[] = "0";

const char NEED_CONNECT_CMD[] = "C"; // to device
const char UPDATE_SCREEN_CMD[] = "U";
const char OK_CMD[] = "O"; // to device
const char SEND_STRINGS_CMD[] = "2"; // from device
const char SEND_SCREEN_CMD[] = "3"; // from device

#ifndef WIN32

typedef int64_t __int64;

void TmToSystemTime(const tm& tme, SYSTEMTIME* st) {
    st->wMilliseconds = 0;
    st->wDay = tme.tm_mday;
    st->wDayOfWeek = tme.tm_wday;
    st->wHour = tme.tm_hour;
    st->wMinute = tme.tm_min;
    st->wMonth = tme.tm_mon + 1;
    st->wYear = tme.tm_year + 1900;
    st->wSecond = tme.tm_sec;
}

void GetLocalTime(SYSTEMTIME *st) {
    time_t t = time(NULL);
    tm tme;
    localtime_r(&t, &tme);

    TmToSystemTime(tme, st);
}

BOOL SystemTimeToFileTime(const SYSTEMTIME *st, LPFILETIME ft) {
    tm tme;
    tme.tm_mday = st->wDay;
    tme.tm_hour = st->wHour;
    tme.tm_min = st->wMinute;
    tme.tm_mon = st->wMonth - 1;
    tme.tm_year = st->wYear - 1900;
    tme.tm_sec = st->wSecond;

    time_t t = mktime(&tme);
    __int64 tm = (__int64)t * 10000000 + 116444736000000000;

    ft->dwLowDateTime = (DWORD) tm;
    ft->dwHighDateTime = tm >> 32;

    return TRUE;
}

BOOL FileTimeToSystemTime(const FILETIME* ft, LPSYSTEMTIME st) {
    time_t t = (time_t) ((*(__int64*)ft - 116444736000000000) / 10000000);
    tm tme;
    localtime_r(&t, &tme);
    TmToSystemTime(tme, st);

    return TRUE;
}
#endif

void PutLog(const char *str, ...) {
    SYSTEMTIME st;
    GetLocalTime(&st);

    va_list args;
    va_start(args, str);
    fprintf(stdout, "%02d:%02d:%02d.%03d ", st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
    vfprintf(stdout, str, args);
    fprintf(stdout, "\n");
    va_end(args);

    fflush(stdout);
}

ServerData::ServerData() : /* mysql(NULL), */socket(-1) {

}

ServerData::~ServerData() {
    threadMutex.Lock();

    std::map<std::string, DeviceHandleThread*>::iterator i = devices.begin();
    for (; i != devices.end(); i++) {
        DeviceHandleThread *dh = i->second;
        devices.erase(i);
        dh->CloseConnection();
    }
    devices.clear();

    map<string, ClientThread*>::iterator ci = threads.begin();
    for (; ci != threads.end(); ci++) {
        ClientThread *ct = ci->second;
        threads.erase(ci);
        ct->CloseConnection();
    }
    threads.clear();

    threadMutex.Unlock();
}

bool ServerData::Start() {
    config.Read();

    socket = ::socket(AF_INET, SOCK_STREAM, 0);

    sockaddr_in sockaddr;
    sockaddr.sin_family = AF_INET;
    sockaddr.sin_addr.s_addr = INADDR_ANY;
    sockaddr.sin_port = htons(config.port);

    if (bind(socket, (struct sockaddr*) &sockaddr, sizeof (sockaddr)) == SOCKET_ERROR) {
        PutLog("Cant bind to %d", config.port);
        return false;
    }

    if (listen(socket, 5) == SOCKET_ERROR) {
        return false;
    }

    return true;
}

void ServerData::Close() {
    if (socket > 0) {
        closesocket(socket);
        socket = -1;
    }
}

SOCKET ServerData::Accept(sockaddr_in* addr) {
    socklen_t len = sizeof (sockaddr_in);
    SOCKET clientSock = accept(socket, (sockaddr*) addr, &len);
    return clientSock;
}

const char* ReadKeyValue(const char *p, const char* ep, unsigned int *key, unsigned int *value) {
    while (p < ep && !isdigit(*p))
        p++;

    if (p >= ep)
        return NULL;

    *key = strtoul(p, (char**) &p, 10);

    while (p < ep && !isdigit(*p))
        p++;

    if (p >= ep)
        return NULL;

    *value = strtoul(p, (char**) &p, 10);
    return p;
}

void ServerData::ThreadDeleted(BaseClientThread* thread) {
    bool deleting = false;

    threadMutex.Lock();

    map<string, ClientThread*>::iterator i = threads.begin();
    for (; i != threads.end(); i++) {
        if (i->second == thread) {
            threads.erase(i);
            deleting = true;
            break;
        }
    }

    if( !deleting) {
        std::map<std::string, DeviceHandleThread*>::iterator di = devices.begin();
        for( ; di != devices.end(); di++) {
            if(di->second == thread) {
                devices.erase(di);
                deleting = true;
                break;
            }
        }
    }
    threadMutex.Unlock();
}

void ServerData::StartClientThread(SOCKET clientSock, ConnectionData* data) {
    threadMutex.Lock();
    map<string, ClientThread*>::iterator fnd = threads.find(data->sessionID);
    if (fnd != threads.end()) {
        PutLog("Have client thread. Stopping it...");
        ClientThread *ct = fnd->second;
        threads.erase(fnd);
        ct->CloseConnection();
    }
    threadMutex.Unlock();

    ClientThread* ct = new ClientThread(this, clientSock, data);
    PutLog("Thread started %X", (long) ct);

    threadMutex.Lock();
    threads[data->sessionID] = ct;
    threadMutex.Unlock();

    ct->Start();
}

void ServerData::StartDeviceThread(SOCKET clientSocket, ConnectionData* data) {
    DeviceHandleThread* dd = new DeviceHandleThread(this, clientSocket, data);
    map<string, DeviceHandleThread*>::iterator fnd = devices.find(data->deviceID);
    if (fnd != devices.end()) {
        DeviceHandleThread* prev = fnd->second;
        fnd->second = dd;

        prev->CopyClients(dd);
        PutLog("Prev device thread %X, %s", (long) prev, data->deviceID.c_str());
        
        prev->StopThread();
    } else {
        threadMutex.Lock();
        devices[data->deviceID] = dd;
        threadMutex.Unlock();
    }
    PutLog("Device thread started %X", (long) dd);
    dd->Start();
}

DeviceHandleThread* ServerData::GetDevice(const std::string& id) {
    std::map<std::string, DeviceHandleThread*>::iterator fnd = devices.find(id);
    return (fnd == devices.end()) ? NULL : fnd->second;
}

//static void show_parsing_error (GtkCssProvider *provider, GtkCssSection  *section, const GError   *error, GtkTextBuffer  *buffer)
//{
//    PutLog("Error %s", error->message);
//}

static void DoTest() {
    GtkWidget* offWnd = gtk_window_new(GTK_WINDOW_TOPLEVEL);// gtk_offscreen_window_new();
    gtk_window_set_default_size (GTK_WINDOW (offWnd), 400, 600);
    
    GtkWidget* fix = gtk_fixed_new();

    GdkRGBA wcolor;
    wcolor.alpha = 1;
    wcolor.red = (double)0.7;
    wcolor.blue = (double)0;
    wcolor.green = (double)0;

    GtkWidget* wnd = gtk_color_button_new_with_rgba(&wcolor);
    gtk_widget_set_size_request(wnd, 80, 60);
    gtk_fixed_put(GTK_FIXED(fix), wnd, 20, 20);
    
    GtkStyleProvider *provider = GTK_STYLE_PROVIDER (gtk_css_provider_new ());
    gtk_css_provider_load_from_data((GtkCssProvider*)provider, ".button \n{ padding: 1px; }", -1, NULL);
    gtk_style_context_add_provider (gtk_widget_get_style_context (wnd), provider, G_MAXUINT);
    
    gtk_container_add(GTK_CONTAINER(offWnd), fix);

    gtk_widget_show_all(offWnd);

        while (gtk_events_pending ())
          gtk_main_iteration ();    
//    gtk_main();
}

int main(int argc, char* argv[]) {
#ifdef WIN32
    WSADATA wsaData = {0};
    WSAStartup(MAKEWORD(2, 2), &wsaData);
#else
    gtk_init(&argc, &argv);
#endif    
    
//    DoTest();
    
    ServerData server;
    if (server.Start()) {
        PutLog("Starting");
        RegisterAvailFonts();

        do {
            sockaddr_in addr;
            SOCKET clientSock = server.Accept(&addr);
            if ((int) clientSock <= 0)
                break;

            PutLog("Accept client (%d)", clientSock);

            ConnectionData *cdata = new ConnectionData();
            Packet *pkt = new Packet(server.config);
            if (pkt->Read(cdata, clientSock)) {
                //PutLog("Got packet id=%s, cmd=%s", pkt->deviceID.c_str(), pkt->command.c_str());
                if (cdata->IsDevice()) {
                    server.StartDeviceThread(clientSock, cdata);
                } else
                    server.StartClientThread(clientSock, cdata);
            } else {
                PutLog("Error while read packet");
                closesocket(clientSock);
                delete cdata;
            }

            delete pkt;
        } while (true);
    }

    server.Close();

#ifdef WIN32
    WSACleanup();
#endif
    return 0;
}