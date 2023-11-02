#include "stdafx.h"
#include "postgre.h"

#include <mutex_t.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

GRServer::IServer* gServer;

static const char CONFIG_FILE[] = "Postgre.cfg";

typedef std::map<const ISession*, PGConnection*> SessionMap;
static SessionMap connections;
static Mutex cnctMutex;
static Config cfg;

PGConnection::PGConnection() :
	conn(NULL)
	, transactionStarted(false)
{

}

PGConnection::~PGConnection()
{
	Close();
}

void PGConnection::StartTransaction()
{
	if (!transactionStarted && conn != NULL)
	{
		transactionStarted = true;
		::Execute(conn, "BEGIN;");
	}
}

void PGConnection::FinishTransaction(bool commit)
{
	if (transactionStarted && conn != NULL)
	{
		::Execute(conn, commit ? "COMMIT;" : "ROLLBACK;");
	}
	transactionStarted = false;
}


PGconn* PGConnection::GetConnection()
{
	if (conn == NULL)
	{
		std::string cs;
		cfg.GetConnectionString(&cs);

		conn = PQconnectdb(cs.c_str());
	}

	if (PQstatus(conn) != CONNECTION_OK)
	{
		PQreset(conn);
	}

	return PQstatus(conn) == CONNECTION_OK ? conn : NULL;
}

void PGConnection::Close()
{
	if (transactionStarted && conn != NULL)
	{
		::Execute(conn, "COMMIT;");
	}
	transactionStarted = false;
	if (conn != NULL)
	{
		PQfinish(conn);
		conn = NULL;
	}
}

struct SessionHandler : public ISession::IHandler
{
	virtual void SessionClosed(ISession* sender)
	{
		cnctMutex.Acquire(1000);
		SessionMap::iterator fnd = connections.find(sender);
		if (fnd != connections.end())
		{
			delete fnd->second;
			connections.erase(fnd);
		}
		cnctMutex.Release();

		delete this;
	}
};

PGconn* GRServer::GetConnection(const ISessionObject& object, PGConnection** connection)
{
	const ISession* session = &object.GetSession();
	SessionMap::iterator fnd = connections.find(session);
	if (fnd != connections.end())
	{
		if (connection != NULL)
			*connection = fnd->second;
		return fnd->second->GetConnection();
	}

	cnctMutex.Acquire(1000);

	PGConnection* pgc = new PGConnection();
	connections.insert(SessionMap::value_type(session, pgc));
	const_cast<ISession*>(session)->AddHandler(new SessionHandler());
	cnctMutex.Release();

	if (connection != NULL)
		*connection = pgc;
	return pgc->GetConnection();
}

PostgrePlugin::PostgrePlugin()
{
	cnctMutex.Init();
}

PostgrePlugin::~PostgrePlugin()
{

}

bool PostgrePlugin::Init(IServer* server)
{
	gServer = server;

	const IServerConfig& config = server->GetConfig();

	std::string configFile = config.PluginsFolder();
	configFile += CONFIG_FILE;

#ifdef _DEBUG
	//MessageBox(NULL, L"!", L"!", MB_OK);
#endif

	if (cfg.Load(configFile) == false)
	{
		server->AddError(false, "Нет настроек PosgreSQL plugin");
		return false;
	}

	IDataSourceRegister* dsr = (IDataSourceRegister*)server->GetService(SOURCE_SERVICE);

	SQTable* sqsrc = new SQTable();

	dsr->RegisterInternalSource(new InternalSource(sqsrc));
	dsr->AddSource(sqsrc);
	dsr->AddSource(new QuerySourceCreator());
	//dsr->AddSource(new SQLCostSource());

	return true;
}

bool PostgrePlugin::Connect(Socket* socket, const wchar_t* password)
{
	return true;
}

void PostgrePlugin::Close()
{

}

// have only config file
IPluginConfig* PostgrePlugin::GetConfig() const
{
	return NULL;
}
