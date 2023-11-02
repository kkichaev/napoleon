#include <vector>

#include "packet.h"
#include "common.h"
#include "gtkservice.h"

const char CMD_TAG[] = "CMD";
const char DATALEN_TAG[] = "DATALEN";
const char DEVICEID_TAG[] = "DEVICEID";
const char SESSIONID_TAG[] = "SESSIONID";
const char VERSION_TAG[] = "SERVER_VERSION";
const char LAST_TAG[] = "LAST";

const char SEP_SYM = ';';

// ANSI encondig.
//
//  start command       data                 data
//   tag                length               tag
//
// 0000 0AD3CMD=<Command>;DATALEN=<DataLength>;DEVICEID=<id>;<DATA>
// 8 byte zero padded hex length of header without first 8 bytes
//

class NetStream {
public:
    NetStream(int timeout);
    ~NetStream();

    void SetSocket(SOCKET socket) {
        this->socket = socket;
    }

    char GetChar();
    bool ReadUntil(std::string* buf, char stopSym);
    bool Read(unsigned char* buf, int len);

    int DataLength() const {
        return size - cp;
    }

    bool HaveData() const {
        return cp < size;
    }

    void SetTimeout(int newTimeout) { timeout = newTimeout; }
    
    bool ReadData();
private:
    SOCKET socket;
    int timeout;

    char* buf;
    int size, bufferSize;
    int cp;
};

void DataBuffer::Alloc(int cb) {
    free(data);
    size = cb;
    if (cb != 0)
        data = (unsigned char*) malloc(cb);
    else
        data = NULL;
}

DataBuffer& DataBuffer::operator=(const DataBuffer& src) {
    if (this != &src) {
        Alloc(src.size);
        if (size > 0)
            memcpy(data, src.data, size);
    }
    return *this;
}

ConnectionData::ConnectionData(const ConnectionData& src) {
    (*this) = src;
}

ConnectionData& ConnectionData::operator=(const ConnectionData& src) {
    if (this != &src) {
        command = src.command;
        deviceID = src.deviceID;
        data = src.data;
        options = src.options;
        sessionID = src.sessionID;
        isLastPacket = src.isLastPacket;
        dataSize = src.dataSize;
    }
    return *this;
}

Packet::Packet(const Config& _config) : config(_config) {
}

Packet::~Packet() {
}

bool DoSend(SOCKET socket, const char* data, int len) {
    int sent = 0;
    while (sent < len) {
        int result = send(socket, (const char*) data + sent, len - sent, 0);

        if (result == SOCKET_ERROR)
            return false;

        sent += result;
    }
    return true;
}

void Packet::PrepareHeader(std::string *header, const ConnectionData& data, unsigned long dataLen, bool isLast) {
    char buf[20];
    snprintf(buf, sizeof (buf), "%d", dataLen);

    header->append(CMD_TAG).append(1, '=').append(data.command).append(1, SEP_SYM);
    header->append(DEVICEID_TAG).append(1, '=').append(data.deviceID).append(1, SEP_SYM);

    header->append(LAST_TAG).append(1, '=').append(1, (isLast) ? '1' : '0').append(1, SEP_SYM);

    header->append(DATALEN_TAG).append(1, '=').append(buf).append(1, SEP_SYM);
    if (!data.IsDevice()) {
        header->append(SESSIONID_TAG).append(1, '=').append(data.sessionID).append(1, SEP_SYM);
    }

    header->append(VERSION_TAG).append(1, '=').append(GKL_SERVER_VERSION).append(1, SEP_SYM);

    std::map<std::string, std::string>::const_iterator i = data.options.begin();
    for (; i != data.options.end(); i++)
        header->append(i->first).append(1, '=').append(i->second).append(1, SEP_SYM);

    snprintf(buf, sizeof (buf), "%08X", header->size());
    header->insert(0, buf);
}

bool Packet::Send(SOCKET socket, const ConnectionData& data) {
    if (socket < 0)
        return false;

    const char* dataP = (const char*)data.data.data;
    unsigned long dataSize = data.data.size;
    
    if(data.IsDevice() && dataSize > config.maxDataSize) {
        // divide data on small packets        
        const char *p = dataP;
        unsigned long curSize = dataSize, packetSize = config.maxDataSize;
        
        while(curSize > 0) {
            std::string header;
            if(packetSize > curSize)
                packetSize = curSize;
            
            PrepareHeader(&header, data, packetSize, (packetSize >= curSize));

            if (!DoSend(socket, header.c_str(), header.size()))
                return false;

            if(!DoSend(socket, (const char*) p, packetSize))
                return false;
            
            // whait answ
            Packet waitP = Packet(config);
            ConnectionData wd;
            if(!waitP.Read(&wd, socket))
                return false;
            
            curSize -= packetSize;
            p += packetSize;
        }
        
        return true;
    }
    
    std::string header;
    PrepareHeader(&header, data, dataSize, true);

    if (!DoSend(socket, header.c_str(), header.size()))
        return false;

    return DoSend(socket, (const char*) dataP, dataSize);
}

#ifdef WIN32

bool WaitData(SOCKET socket, int timeout) {
    DWORD cb = 0;
    ioctlsocket(socket, FIONREAD, &cb);
    if (cb != 0)
        return true;

    HANDLE evRead = WSACreateEvent();
    WSAEventSelect(socket, evRead, FD_READ);

    DWORD res = WaitForMultipleObjects(1, &evRead, FALSE, timeout);
    WSACloseEvent(evRead);
    return (res == WAIT_OBJECT_0);
}

int AvailRead(SOCKET socket) {
    u_long cb;
    ioctlsocket(socket, FIONREAD, &cb);
    return cb;
}
#else

bool WaitData(SOCKET socket, int timeout) {
    timeval to;
    to.tv_sec = timeout / 1000;
    to.tv_usec = timeout % 1000;

    fd_set fd;
    FD_ZERO(&fd);
    FD_SET(socket, &fd);

    int ready = select(socket + 1, &fd, NULL, NULL, &to);

    return (ready > 0 && FD_ISSET(socket, &fd) != 0);
}

int AvailRead(SOCKET socket) {
    int cb;
    ioctl(socket, FIONREAD, &cb);
    return cb;
}
#endif

NetStream::NetStream(int timeout) : buf(NULL), size(0), cp(0), bufferSize(0) {
    this->socket = -1;
    this->timeout = timeout;
}

NetStream::~NetStream() {
    free(buf);
}

char NetStream::GetChar() {
    if (cp >= size && !ReadData())
        return 0;

    return buf[cp++];
}

bool NetStream::ReadUntil(std::string* dest, char stopSym) {
    char sym;

    dest->clear();
    while ((sym = GetChar()) != 0) {
        if (sym == stopSym)
            break;
        dest->append(1, sym);
    }
    return sym == stopSym;
}

bool NetStream::ReadData() {
    size = 0;
    cp = 0;

    if (!WaitData(socket, timeout))
        return false;

    size = AvailRead(socket);
    if (size > 0) {
        //PutLog("Avail %d", size);

        if (size > bufferSize) {
            bufferSize = size;
            buf = (char*) malloc(size);
        }
        size = recv(socket, buf, size, 0);
    }
    return (size > 0);
}

bool NetStream::Read(unsigned char* data, int len) {
    if (len == 0)
        return true;

    do {
        if (cp >= size && ReadData() == false)
            return false;
        int rest = size - cp;
        if (rest > len)
            rest = len;

        memcpy(data, buf + cp, rest);
        data += rest;
        len -= rest;
        cp += rest;
    } while (len > 0);

    return true;
}

void ConnectionData::LoadOption(const char* key, const char* value) {
    if (strcmp(key, DATALEN_TAG) == 0)
        dataSize = atoi(value);
    else if (strcmp(key, DEVICEID_TAG) == 0)
        deviceID = value;
    else if (strcmp(key, CMD_TAG) == 0)
        command = value;
    else if (strcmp(key, SESSIONID_TAG) == 0) {
        if (*value == '\0') {
            char buf[50];
            _ui64toa(GetTickCount64(), buf, 16);
            sessionID = buf;
        } else
            sessionID = value;
    } else if(strcmp(key, LAST_TAG) == 0) {
        isLastPacket = (atoi(value) == 1);
    } else
        options[key] = value;
}

static bool ReadHeader(NetStream& stream, ConnectionData* data, SOCKET socket) {
    char headerLength[9], *ep, *sp;

    stream.SetSocket(socket);
    if (!stream.Read((unsigned char*) headerLength, 8)) {
        return false;
    }

    headerLength[8] = '\0';
    int headerSize = strtol(headerLength, &ep, 16);
    char *header = (char*) malloc(headerSize + 1);

    if (!stream.Read((unsigned char*) header, headerSize))
        return false;

    header[headerSize] = '\0';
    sp = header;

    while (true) {
        ep = strchr(sp, SEP_SYM);
        if (ep != NULL)
            *ep = '\0';

        char *eqS = strchr(sp, '=');
        if (eqS != NULL) {
            *eqS = '\0';
            data->LoadOption(sp, eqS + 1);
        }
        if (ep == NULL)
            break;
        sp = ep + 1;
    }
    free(header);
    return true;
}

bool Packet::Read(ConnectionData* data, SOCKET socket, int timeout) {
    NetStream stream(config.readtimeout);
    if(timeout >= 0)
        stream.SetTimeout(timeout);

    if( !ReadHeader(stream, data, socket))
        return false;

    if(data->isLastPacket == false) {
        bool fail = false;
        unsigned long totalSize = 0;
        std::vector<DataBuffer*> chunks;
        do {
            DataBuffer *buf = new DataBuffer();
            buf->Alloc(data->dataSize);
            totalSize += data->dataSize;
            chunks.push_back(buf);
            
            if(!stream.Read(buf->data, buf->size)) {
                fail = true;
                break;
            }
            
            if(data->isLastPacket)
                break;
            
            // we don't ack last packet 
            Packet pkt(config);
            ConnectionData cd;
            cd.deviceID = data->deviceID;
            cd.command = OK_CMD;
            if( !pkt.Send(socket, cd) ) {
                fail = true;
                break;
            }
            
            if( !ReadHeader(stream, data, socket)) {
                fail = true;
                break;
            }            
        } while(true);
        
        if(!fail)
            data->data.Alloc(totalSize);        
        unsigned char* cp = data->data.data;
        
        std::vector<DataBuffer*>::iterator i = chunks.begin();
        for( ; i != chunks.end(); i++) {
            if(!fail) {
                memcpy(cp, (*i)->data, (*i)->size);
                cp += (*i)->size;
            }
            delete (*i);
        }
        return !fail;
    }

    bool ret = true;
    if (data->dataSize > 0) {
        data->data.Alloc(data->dataSize);
        ret = stream.Read(data->data.data, data->data.size);
    }
    
    return ret;
}

bool Packet::SendBuf(SOCKET socket, const DataBuffer& data) {
    return DoSend(socket, (const char*) data.data, data.size);
}

bool Packet::ReadBuf(DataBuffer *data, NetStream& stream) {
    if (!stream.HaveData() && !stream.ReadData())
        return false;

    int size = stream.DataLength();
    data->Alloc(size);

    return stream.Read(data->data, size);
}