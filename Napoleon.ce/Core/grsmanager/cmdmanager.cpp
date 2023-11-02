#include "grsmanager.h"
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/stat.h>
#include <unistd.h>

using namespace GRSManager;
using namespace std;


#include <algorithm>
#include <iostream>
#include <iterator>
#include <streambuf>
#include <cstddef>
#include <unistd.h>

class fdbuf
    : public std::streambuf
{
private:
    enum { bufsize = 1024 };
    char outbuf_[bufsize];
    char inbuf_[bufsize + 16 - sizeof(int)];
    int  fd_;
public:
    typedef std::streambuf::traits_type traits_type;

    fdbuf(int fd);
    ~fdbuf();
    void open(int fd);
    void close();

protected:
    int overflow(int c);
    int underflow();
    int sync();
};

fdbuf::fdbuf(int fd)
  : fd_(-1) {
    this->open(fd);
}

fdbuf::~fdbuf() {
    this->close();
}

void fdbuf::open(int fd) {
    this->close();
    this->fd_ = fd;
    this->setg(this->inbuf_, this->inbuf_, this->inbuf_);
    this->setp(this->outbuf_, this->outbuf_ + bufsize - 1);
}

void fdbuf::close() {
    if (!(this->fd_ < 0)) {
        this->sync();
        ::close(this->fd_);
    }
}

int fdbuf::overflow(int c) {
    if (!traits_type::eq_int_type(c, traits_type::eof())) {
        *this->pptr() = traits_type::to_char_type(c);
        this->pbump(1);
    }
    return this->sync() == -1
        ? traits_type::eof()
        : traits_type::not_eof(c);
}

int fdbuf::sync() {
    if (this->pbase() != this->pptr()) {
        std::streamsize size(this->pptr() - this->pbase());
        std::streamsize done(::write(this->fd_, this->outbuf_, size));
        // The code below assumes that it is success if the stream made
        // some progress. Depending on the needs it may be more
        // reasonable to consider it a success only if it managed to
        // write the entire buffer and, e.g., loop a couple of times
        // to try achieving this success.
        if (0 < done) {
            std::copy(this->pbase() + done, this->pptr(), this->pbase());
            this->setp(this->pbase(), this->epptr());
            this->pbump(size - done);
        }
    }
    return this->pptr() != this->epptr()? 0: -1;
}

int fdbuf::underflow()
{
    if (this->gptr() == this->egptr()) {
        std::streamsize pback(std::min(this->gptr() - this->eback(),
                                       std::ptrdiff_t(16 - sizeof(int))));
        std::copy(this->egptr() - pback, this->egptr(), this->eback());
        int done(::read(this->fd_, this->eback() + pback, bufsize));
        this->setg(this->eback(),
                   this->eback() + pback,
                   this->eback() + pback + std::max(0, done));
    }
    return this->gptr() == this->egptr()
        ? traits_type::eof()
        : traits_type::to_int_type(*this->gptr());
}


CMDManager::CMDManager(FCGIManager& _fcgim, DBManager& _dbm) :
    fcgim(_fcgim)
    ,dbm(_dbm)
    ,socket(-1)
{
}

CMDManager::~CMDManager()
{
    if(socket != -1)
    {
        shutdown(socket, SHUT_RDWR);
        close(socket);
    }
}

class CMDRunner : public ThreadRunner
{
public:
    CMDRunner(CMDManager& _mgr) : mgr(_mgr) {}
    ~CMDRunner() {}
    virtual void Run(ThreadParam* p) { mgr.Run(); }

private:
    CMDManager& mgr;
};


bool CMDManager::Start(const Config& cfg)
{
    if(cfg.cmdSocket.size() > sizeof(((sockaddr_un*)0)->sun_path))
    {
        Log("Cmd socket too long");
        return false;
    }

    int backlog = 10;
    socket = ::socket(AF_UNIX, SOCK_STREAM, 0);
    if(socket < 0)
    {
        Log("Can't create socket errno %d", errno);
        return false;
    }

    struct  sockaddr_un	sockAddr;
    const char* bindPath = cfg.cmdSocket.c_str();
    unlink(bindPath);

    memset((char *) &sockAddr, 0, sizeof(sockAddr));
    sockAddr.sun_family = AF_UNIX;
    memcpy(sockAddr.sun_path, bindPath, cfg.cmdSocket.size());
    int len = sizeof(sockAddr.sun_family) + cfg.cmdSocket.size();

    if (bind(socket, (struct sockaddr *) &sockAddr, len) < 0 || listen(socket, backlog) < 0) 
    {
        Log("Can't bind to socket <%s> errno %d", bindPath, errno);
        return false;
    }

    chmod(bindPath, 0666);

    ThreadParam::StartThread(new CMDRunner(*this));
    return true;
}


void CMDManager::Run()
{
    sockaddr_un un;
    socklen_t len = sizeof(un);
    bool exit = false;

    while(!exit)
    {
        int rcvsock = accept(socket, (struct sockaddr*)&un, &len);
        if(rcvsock > 0)
        {
            fdbuf fd(rcvsock);

            iostream s(&fd);
            std::string command;

            while(std::getline(s, command))
            {
                Log("Get command %s", command.c_str());
            }

            Log("Close command session");
        }
    }
}