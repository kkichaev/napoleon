#ifndef __GTK_H
#define __GTK_H

#include "common.h"

#ifdef WIN32
// link with Ws2_32.lib
#pragma comment(lib,"Ws2_32.lib")

#include <winsock2.h>
#include <ws2tcpip.h>

#else



#endif

#include <stdio.h>
#include <stdlib.h>   // Needed for _wtoi

//#include <mysql.h>
#include "thread.h"
#include "packet.h"
#include "mutex.h"

class Packet;
class BaseClientThread;
class ClientThread;
class DeviceHandleThread;
class Screen;
class DeviceData;

struct Config {
    int port;
    int readtimeout;
    int devicePingTimeout;
    int maxDataSize;

    std::string dbHost;
    std::string dbDatabase;
    std::string dbLogin;
    std::string dbPassword;
    int dbPort;

    std::string deviceLogTable;
    std::string deviceConfigTable;

    bool Read();

    void SetValue(const std::string& key, const std::string& value);
    
    void SetDefaults();
};

struct DeviceSettings {
    std::string id;
    int port;
};

struct DataBuffer {
    unsigned long size;
    unsigned char* data;

    DataBuffer() : data(NULL), size(0) {
    }

    ~DataBuffer() {
        Clear();
    }

    void Clear() {
        free(data);
        data = NULL;
        size = 0;
    }

    DataBuffer& operator=(const DataBuffer& src);

    void Alloc(int cb);
};

struct ConnectionData {

    ConnectionData() : isLastPacket(true), dataSize(0) {
    }
    ConnectionData(const ConnectionData& src);

    ConnectionData& operator=(const ConnectionData& src);

    std::string command;
    std::string deviceID;
    std::string sessionID;
    
    bool isLastPacket;
    unsigned long dataSize;
    
    DataBuffer data;

    std::map<std::string, std::string> options;

    bool IsDevice() const {
        return sessionID.empty();
    }

    void LoadOption(const char* key, const char* value);
};

class ServerData {
public:
    Config config;

protected:
    Mutex threadMutex;

    //	MYSQL *mysql;
    SOCKET socket;

    std::map<std::string, ClientThread*> threads;
    std::map<std::string, DeviceHandleThread*> devices;

public:
    ServerData();
    ~ServerData();

    bool Start();
    void Close();

    SOCKET Accept(sockaddr_in* addr);

    //bool UpdateDeviceData(const Packet& packet);
    //bool ReadDeviceSettings(const std::string& id, DeviceSettings* settings);

    void ThreadDeleted(BaseClientThread* thread);
    void StartClientThread(SOCKET clientSock, ConnectionData* data);

    void StartDeviceThread(SOCKET clientSock, ConnectionData* data);

    DeviceHandleThread* GetDevice(const std::string& id);
    const std::map<std::string, DeviceHandleThread*>& Devices() const { return devices; }
};

class BaseClientThread : public Thread {
public:
    BaseClientThread(ServerData* data, SOCKET client, ConnectionData* connectionData);
    virtual ~BaseClientThread();

    void CloseConnection();

    bool Connected() const {
        return ((int) clientSocket > 0);
    }

    ServerData* Server() {
        return data;
    }

protected:
    ServerData* data;
    SOCKET clientSocket;
    ConnectionData* connectionData;

};

class ClientThread : public BaseClientThread {
public:

    ClientThread(ServerData* data, SOCKET client, ConnectionData* connectionData) : BaseClientThread(data, client, connectionData) {
    }

    virtual void Execute();

private:
    void SendDeviceScreen(DeviceHandleThread *device);
};

class DeviceData {
public:
    DeviceData();
    ~DeviceData();

    bool RenderScreen(DataBuffer* out) const;

    void SetID(const std::string& newID) { id = newID; }
    
    const std::string& ID() const {
        return id;
    }


    bool GetLastConnect(FILETIME* ft) const;

    bool RequestConnect(ClientThread* thread);
    
    
    // return true if screen regenerated
    bool LoadData(const ConnectionData& data);

    void UpdateLastConnect();
    
    const char* GetString(long offset) const;
    
    bool IsNeedConnect() const;
    
    bool NoScreenData() const;
    
    const ValuesMap& Values() const { return values; }
    
private:
    std::string id;
    Screen *screen;

    FILETIME lastConnect;

    Mutex* waitConnect;
    ValuesMap values;
    
    bool requestConnect;
};

static int ALIGN_LEFT = 0;
static int ALIGN_RIGHT = 1;
static int ALIGN_CENTER = 2;
static int ALIGN_TOP = 0;
static int ALIGN_BOTTOM = 1;
static int ALIGN_MIDDLE = 2;

void RegisterAvailFonts();
bool DrawTextWithFont(int index, cairo_t* sfc, DWORD color, const wchar_t* text, 
        unsigned textOffset, unsigned left, unsigned top, unsigned width, unsigned height,
        int alignh, int alignv);

wchar_t* ToUnicode(const std::string& utf8);
// fonts defs

class DeviceHandleThread : public BaseClientThread {
public:

    DeviceHandleThread(ServerData* data, SOCKET client, ConnectionData* connectionData) : BaseClientThread(data, client, connectionData) {
        device.SetID(connectionData->deviceID);
        needDelete = false;
    }

    void CopyClients(DeviceHandleThread* dest);
    
    virtual void Execute();
    bool RequestConnect(ClientThread* client);
    void StopThread();

    const DeviceData& Device() const { return device; }

    WaitEvent* SendMouseCommand(unsigned x, unsigned y);
    
private:
    DeviceData device;
    bool needDelete;
    WaitEvent waitDevice;
};

#endif