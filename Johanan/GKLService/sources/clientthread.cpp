#include "gtkservice.h"
#include "common.h"
#include "controls.h"
#include <time.h>

const char DRAW_DEVICE_CMD[] = "DRAW_DEVICE";
const char BYE_CMD[] = "BYE";
const char SCREEN_IMAGE_CMD[] = "SCREEN_IMAGE";
const char DEVICE_LIST_CMD[] = "DEVICE_LIST";
const char MOUSE_CLICK_CMD[] = "MOUSE_CLICK";

const int WAIT_MOUSE_TIMEOUT = 5000; // 5 sec

BaseClientThread::BaseClientThread(ServerData* data, SOCKET client, ConnectionData* connectionData) {
    this->data = data;
    this->clientSocket = client;
    this->connectionData = connectionData;
}

void BaseClientThread::CloseConnection() {
    int cs = clientSocket;
    clientSocket = -1;
    if ((int) cs > 0) {
        PutLog("Close connection %d", cs);
        closesocket(cs);
    }
}

BaseClientThread::~BaseClientThread() {
    PutLog("Thread deleted '%X'", (long) this);

    data->ThreadDeleted(this);

    delete connectionData;
    CloseConnection();
}

//
// id:key=val;key=val\n
//

void PutToString(std::string* str, const DeviceData& device) {
    str->append(device.ID()).append(":");

    FILETIME connect;
    str->append("lastConnect=");
    if (device.GetLastConnect(&connect)) {
        SYSTEMTIME st;
        FileTimeToSystemTime(&connect, &st);

        char buf[100];
        wsprintfA(buf, "%d%02d%02d%02d%02d%02d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
        str->append(buf);
    } else {
        str->append("none");
    }

    ValuesMap::const_iterator i = device.Values().begin();
    for (; i != device.Values().end(); i++) {
        str->append(";");
        char buf[30];
        wsprintfA(buf, "%u", i->first);
        str->append(buf).append("=").append(i->second);
    }

}

void ClientThread::SendDeviceScreen(DeviceHandleThread *device) {
    ConnectionData cd;
    cd.deviceID = connectionData->deviceID;
    cd.command = SCREEN_IMAGE_CMD;
    cd.sessionID = connectionData->sessionID;
    
    device->Device().RenderScreen(&cd.data);
    Packet pkt(data->config);
    pkt.Send(clientSocket, cd);
}

void ClientThread::Execute() {
    Packet *packet = new Packet(data->config);
    do {
        const std::string& cmd = connectionData->command;
        if (cmd.compare(DRAW_DEVICE_CMD) == 0) {
            DeviceHandleThread *d = data->GetDevice(connectionData->deviceID);


            if (d != NULL) {
                bool connected = d->RequestConnect(this);
                PutLog("Draw device '%s'", connectionData->deviceID.c_str());
                if (connected) {
                    SendDeviceScreen(d);
                }
            }
        } else if (cmd.compare(BYE_CMD) == 0) {
            PutLog("Bye device '%s'", connectionData->deviceID.c_str());
            CloseConnection();
        } else if (cmd.compare(DEVICE_LIST_CMD) == 0) {
            std::string str;
            const std::map<std::string, DeviceHandleThread*>& devices = data->Devices();
            std::map<std::string, DeviceHandleThread*>::const_iterator i = devices.begin();
            for (; i != devices.end(); i++) {
                PutToString(&str, i->second->Device());
                str.append("\n");
            }

            ConnectionData cd;
            cd.deviceID = "";
            cd.command = DEVICE_LIST_CMD;
            cd.data.Alloc(str.size());
            cd.sessionID = connectionData->sessionID;
            memcpy(cd.data.data, str.c_str(), str.size());
            Packet pkt(data->config);
            pkt.Send(clientSocket, cd);
        } else if( cmd.compare(MOUSE_CLICK_CMD) == 0) {
            DeviceHandleThread *d = data->GetDevice(connectionData->deviceID);
            if(connectionData->data.size > 0) {
                char* ep;
                unsigned x, y;
                std::string coord((const char*)connectionData->data.data, connectionData->data.size);
                x = strtoul(coord.c_str(), &ep, 10);
                if( ep != NULL ) {
                    y = strtoul(ep+1, &ep, 10);
                    PutLog("Mouse click %d, %d", x, y);
                    WaitEvent* we = d->SendMouseCommand(x, y);
                    we->Waiting(WAIT_MOUSE_TIMEOUT);
                }
            }

            SendDeviceScreen(d);
        }

        if (!Connected())
            break;

        if (!packet->Read(connectionData, clientSocket))
            break;
    } while (true);
    delete packet;
}