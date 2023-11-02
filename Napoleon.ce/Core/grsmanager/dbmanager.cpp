#include "grsmanager.h"
#include <pthread.h>

using namespace GRSManager;
using namespace std;

static pthread_mutex_t dblock = PTHREAD_MUTEX_INITIALIZER;

DBConnection::DBConnection(DBManager& _owner) :
    owner(_owner)
    ,conn(NULL)
{
}

DBConnection::~DBConnection()
{
    if(conn = NULL)
    {
        PQfinish(conn);
        conn = NULL;
    }
}

PGconn* DBConnection::GetConnection()
{
    if(conn == NULL)
        conn = PQconnectdb(owner.ConnectionString());

    if (PQstatus(conn) != CONNECTION_OK)
		PQreset(conn);

	return PQstatus(conn) == CONNECTION_OK ? conn : NULL;
}

void DBConnection::Close() 
{
    owner.ConnectionClosed(this);
}

DBManager::DBManager()
{
}

DBManager::~DBManager()
{
    Close();
}

void DBManager::Close()
{
    pthread_mutex_lock(&dblock);

    ConnectList::iterator ci = connections.begin();
    for( ; ci != connections.end(); ci++)
    {
        delete (*ci);
    }
    connections.clear();

    pthread_mutex_unlock(&dblock);
}

void DBManager::ConnectionClosed(DBConnection *con)
{
    pthread_mutex_lock(&dblock);

    ConnectList::iterator ci = connections.begin();
    for( ; ci != connections.end(); ci++)
    {
        if(*ci == con)
        {
            connections.erase(ci);
            break;
        }
    }
    connections.clear();

    pthread_mutex_unlock(&dblock);

    delete con;
}

DBConnection* DBManager::GetConnection()
{
    pthread_mutex_lock(&dblock);

    DBConnection *ret = new DBConnection(*this);
    connections.push_back(ret);

    pthread_mutex_unlock(&dblock);
    return ret;
}


bool DBManager::Open(const Config& config)
{
    config.MakeConnectionStr(&connString);
    return true;
}
