#include "grsmanager.h"
#include <fstream>

using namespace GRSManager;
using namespace std;

const char* GRSManager::WhiteSpaces = " \t\n\r\f\v";

static const std::string DB_SRV_TAG("dbServer");
static const std::string DB_PORT_TAG("dbPort");
static const std::string DB_NAME_TAG("dbName");
static const std::string DB_USER_TAG("dbUser");
static const std::string DB_PWD_TAG("dbPwd");

static const std::string SRV_DIR_TAG("serverFolder");
static const std::string CLI_DIR_TAG("clientsFolder");

static const char* ENV_GRS_SOCK = "GRS_FCGI_SOCK";
static const char* ENV_CMD_SOCK = "GRS_CMD_SOCK";
static const char* ENV_PAGE_PREFIX = "GRS_PAGE_PREFIX";

Config::Config()
{
}

static bool ReadEnv(std::string* out, const char* env)
{
    const char *p = std::getenv(env);
    if(p == NULL)
    {
        Log("No %s env variable", env);
        return false;
    }
    out->assign(p);
    return true;
}

bool Config::Load(const std::string& fileName)
{
    fstream f(fileName, ios_base::in);
    if(f.fail())
    {
        Log("Can't open config file %s", fileName.c_str());
        return false;
    }

    string line;
    while(getline(f, line)) 
    {
        size_t pos = line.find('=');
        if(pos == string::npos) continue;

        std::string key(line.substr(0, pos));
        std::string value(line.substr(pos+1));

        Set(trim(key), trim(value));
    }

    const char *p = std::getenv(ENV_GRS_SOCK);
    if(!ReadEnv(&fcgiSocket, ENV_GRS_SOCK))
    {
        return false;
    }

    if(!ReadEnv(&cmdSocket, ENV_CMD_SOCK))
    {
        return false;
    }

    if(!ReadEnv(&pagePrefix, ENV_PAGE_PREFIX))
    {
        return false;
    }

    return true;
}

void Config::Set(const std::string& key, const std::string& value)
{
    if(key.compare(DB_SRV_TAG) == 0) dbServer = value;
    else if(key.compare(DB_USER_TAG) == 0) dbUser = value;
    else if(key.compare(DB_PORT_TAG) == 0) { dbPort.assign(1,':').append(value); }
    else if(key.compare(DB_NAME_TAG) == 0) { database.assign(1, '/').append(value); }
    else if(key.compare(DB_PWD_TAG) == 0) { dbPassword.assign(1, ':').append(value); }

    else if(key.compare(SRV_DIR_TAG) == 0) serverFolder = value;
    else if(key.compare(CLI_DIR_TAG) == 0) clientsFolder = value;
}

void Config::MakeConnectionStr(std::string* out) const
{
    out->assign("postgresql://").append(dbUser).append(dbPassword).append(1, '@').append(dbServer).append(dbPort).append(database);
}