#pragma once

#include <string>
#include <vector>
#include <libpq-fe.h>

#include "fcgio.h"

namespace GRSManager {

struct Config
{
public:
    Config();

    bool Load(const std::string& fileName);

    void Set(const std::string& key, const std::string& value);
    
    void MakeConnectionStr(std::string* out) const;

    std::string fcgiSocket;
    std::string cmdSocket;

    std::string dbServer;
    std::string dbPort;
    std::string database;
    std::string dbUser;
    std::string dbPassword;

    std::string pagePrefix;

    std::string serverFolder;
    std::string clientsFolder;
};

class DBConnection;
class DBManager
{
public:
    typedef std::vector<DBConnection*> ConnectList;

    DBManager();
    ~DBManager();

    bool Open(const Config& config);
    void Close();

    DBConnection* GetConnection();
    void ConnectionClosed(DBConnection* con);

    const char* ConnectionString() const { return connString.c_str(); }

private:
    std::string connString;
    ConnectList connections;
};

class DBConnection
{
public:
    DBConnection(DBManager& owner);
    ~DBConnection();

    PGconn* GetConnection();

    void Close();

private:

    PGconn *conn;
    DBManager& owner;
};

class FCGIManager
{
public:
    FCGIManager(DBManager& dbm);
    ~FCGIManager();

    void Closing() { closing = true; }
    void Run(const Config& cfg);

private:
    int socket;
    bool closing;  

    DBManager& dbm;  
};

struct ThreadParam;
struct ThreadRunner
{
    virtual ~ThreadRunner() {}
    virtual void Run(ThreadParam* params) = 0;
};

struct ThreadParam
{
    pthread_t handle;
    ThreadRunner *runner;

    // runner deleted after Run or if exception throwed
    static ThreadParam* StartThread(ThreadRunner* runner);
};

class CMDManager
{
public:
    CMDManager(FCGIManager& fcgim, DBManager& dbm);
    ~CMDManager();

    bool Start(const Config& cfg);
    void Run();

private:
    FCGIManager& fcgim;
    DBManager& dbm;

    int socket;
};

void Log(const char *msg, ...);

extern const char* WhiteSpaces;

inline std::string& rtrim(std::string& s, const char* t = WhiteSpaces) { return s.erase(s.find_last_not_of(t) + 1); }

// trim from beginning of string (left)
inline std::string& ltrim(std::string& s, const char* t = WhiteSpaces) { return s.erase(0, s.find_first_not_of(t)); }

// trim from both ends of string (right then left)
inline std::string& trim(std::string& s, const char* t = WhiteSpaces) { return ltrim(rtrim(s, t), t); }

}
