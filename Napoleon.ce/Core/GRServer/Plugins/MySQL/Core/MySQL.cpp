#include "stdafx.h"
#include "MySQLDrv.h"
#include "Binder.h"
#include "QuerySource.h"

using namespace GRServer;
static const char CONFIG_FILE[] = "MySQLPlugin.cfg";


std::string configFile;
GRServer::IServer* gServer;

MySQLPlugin::MySQLPlugin()
{
   CoInitializeEx(NULL, COINIT_MULTITHREADED /*COINIT_APARTMENTTHREADED*/);
}

MySQLPlugin::~MySQLPlugin()
{
   CoUninitialize();
}

bool MySQLPlugin::Init(IServer* server)
{
   gServer = server;	

   const IServerConfig& config = server->GetConfig();

   configFile = config.PluginsFolder();
   configFile += CONFIG_FILE;

   Config cfg;
   if( cfg.Load(configFile) == false )
   {
      server->AddError(false, "Нет настроек для драйвера MySQL - запустите настройку модуля в Plugins");
      return false;
   }

   IDataSourceRegister* dsr = (IDataSourceRegister*)server->GetService(SOURCE_SERVICE);
   if( cfg.useAsInternalBase )
   {
      dsr->RegisterInternalSource(CreateInternalDS());
      dsr->AddSource(new SQTable());
   }
   dsr->AddSource(new SQLSource());
   dsr->AddSource(new QuerySourceCreator());
   dsr->AddSource(new SQLFolderCreator());

   return true;
}

bool MySQLPlugin::Connect(Socket *socket, const wchar_t* password)
{
   return true;
}

void MySQLPlugin::Close()
{
}