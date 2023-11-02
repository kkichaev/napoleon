#include "gtkservice.h"
#include "common.h"
#include <time.h>
#include "controls.h"

const int FORMAT_DATE = 7;

const char SEND_DRAW_OBJECTS_CMD[] = "1";
const char SEND_SCREEN_OBECTS_CMD[] = "2";
const char SEND_SCREEN_STRINGS_CMD[] = "3";
const char MOUSE_CMD[] = "M";

static inline unsigned ChangeByteOrder(unsigned val) {
    return val;
    //return ((val & 0xFF00) >> 8) | ((val & 0xFF) << 8) | ((val & 0xFF0000) << 8) | ((val & 0xFF000000) >> 8);
}

bool DeviceData::GetLastConnect(FILETIME* ft) const {
    if (lastConnect.dwHighDateTime == 0)
        return false;

    *ft = lastConnect;
    return true;
}

bool DeviceData::NoScreenData() const {
    return screen->NoScreenData();
}

bool DeviceData::IsNeedConnect() const
{ 
    return requestConnect;
}

bool DeviceData::LoadData(const ConnectionData& data) {
    bool ret = false;
    
    if( data.command.compare(SEND_DRAW_OBJECTS_CMD) == 0 ) 
        screen->SetDrawObjects(data.data);
    else if (data.command.compare(SEND_SCREEN_OBECTS_CMD) == 0) {
        screen->SetObjects(data.data);
        ret = true;
    } else if (data.command.compare(SEND_SCREEN_STRINGS_CMD) == 0)
        screen->SetStrings(data.data);
    else {
        if (data.data.size > 0) {
            values.clear();
            
//            PutLog("Got values command - %s", data.command.c_str());
            
            const char * p = (const char*) data.data.data;
            const char * ep = (char*) (data.data.data + data.data.size);

            while (p < ep) {
                unsigned id = *(unsigned*)p; p += 4;

                std::string val;
                while( *p && p < ep )
                    val.append(1, *p++);
                values[id] = val;
                if(*p == '\0')
                    p++;
                
//                PutLog("%u = %s", id, val.c_str());
            }

            screen->SetValues(values);
            ret = true;
        }
    }
    return ret;
}

void DeviceData::UpdateLastConnect() {
    SYSTEMTIME st;
    GetLocalTime(&st);
    SystemTimeToFileTime(&st, &lastConnect);
}

bool DeviceData::RequestConnect(ClientThread* thread) {
    requestConnect = true;
    //	if (deviceSocket == -1)
    //		return false;
    //
    //	ConnectionData cd;
    //	cd.deviceID = id;
    //	cd.command = NEED_CONNECT_CMD;
    //	Packet pkt(thread->Server()->config);
    //	pkt.Send(deviceSocket, cd);
    //
    //	PutLog("Send command to device '%s' waiting answer...", id.c_str());
    //
    //	if (pkt.Read(&cd, deviceSocket))
    //	{
    //		PutLog("Got packet command=%s, data length= %d", cd.command.c_str(), cd.data.size);
    //	}
    //	else
    //	{
    //		PutLog("No answer");
    //	}
    return true;
}

//static void SplitLine(char* buf, int pos, char sym) {
//    int divpos = strlen(buf) - pos;
//    memcpy(buf + divpos + 1, buf + divpos, (pos + 1) * sizeof (sym));
//    buf[divpos] = '.';
//}
//
//bool DeviceData::ValueToStr(std::string* dest, unsigned src, unsigned key) {
//    char buf[30];
//    bool ret = false;
//
//    int format = (int) ((key >> 24) & 0x1F);
//    if (format >= 0 && format <= 4) {
//        wsprintfA(buf, "%d", src);
//        if (format >= 1)
//            SplitLine(buf, format, L'.');
//        ret = true;
//    } else if (format == 5 || format == 6) {
//        wsprintfA(buf, "%X", src);
//        if (format == 5)
//            buf[4] = '\0';
//
//        SplitLine(buf, 2, ':');
//        if (format == 6)
//            SplitLine(buf, 5, ':');
//        ret = true;
//    } else if (format == FORMAT_DATE) {
//        struct tm time = {0};
//        time.tm_year = 100 + (src >> 16);
//        time.tm_mon = ((src >> 8) & 0xFF) - 1;
//        time.tm_mday = src & 0xFF;
//
//        strftime(buf, sizeof (buf) / sizeof (buf[0]), "%b %d %Y", &time);
//        ret = true;
//    }
//
//    if (ret)
//        dest->assign(buf);
//    else
//        dest->clear();
//
//    return ret;
//}

void DeviceHandleThread::CopyClients(DeviceHandleThread* dest) {
}

bool DeviceHandleThread::RequestConnect(ClientThread* client) {
    return device.RequestConnect(client);
}

WaitEvent* DeviceHandleThread::SendMouseCommand(unsigned x, unsigned y)
{
    Packet pkt(data->config);
    ConnectionData cd;
    cd.deviceID = device.ID();
    cd.command = MOUSE_CMD;
    cd.data.Alloc(8);
    *(unsigned*)cd.data.data = x;
    *((unsigned*)cd.data.data + 1) = y;
    pkt.Send(clientSocket, cd);
    
    waitDevice.ResetEvent();
    return &waitDevice;
}

static bool opened = false;

void DeviceHandleThread::Execute() {
    do {
        if( device.LoadData(*connectionData) )
            waitDevice.SetEvent();
        
        device.UpdateLastConnect();

        Packet pkt(data->config);
        ConnectionData cd;
        cd.deviceID = device.ID();
//        cd.command = UPDATE_SCREEN_CMD; //OK_CMD;
        cd.command = OK_CMD;
        if(device.NoScreenData())
            cd.command = NEED_CONNECT_CMD;
        else if(device.IsNeedConnect())
            cd.command = NEED_CONNECT_CMD;
//        PutLog("Send command %s", cd.command.c_str());
        
//        cd.command = NEED_CONNECT_CMD;
//        PutLog("Send command (%X) %s", (long)this, cd.command.c_str());
        
        pkt.Send(clientSocket, cd);
        
        if(!pkt.Read(connectionData, clientSocket, data->config.devicePingTimeout))
            break;
        
//        PutLog("Receive command (%X) %s", (long)this, connectionData->command.c_str());
        
//        if(connectionData->data.size > 0) {
//            FILE *wr = fopen("./log", (opened) ? "ab" : "wb");
//            if(wr) {
//                opened = true;
//                fprintf(wr, "\ncmd=%s,DataSize %d\n", connectionData->command.c_str(), connectionData->data.size);
//                fwrite(connectionData->data.data, sizeof(char), connectionData->data.size, wr);
//                fclose(wr);
//            }
//        }
    } while(true);
    
    if(needDelete)
        delete this;
}

void DeviceHandleThread::StopThread() {
    needDelete = true;
    CloseConnection();
}