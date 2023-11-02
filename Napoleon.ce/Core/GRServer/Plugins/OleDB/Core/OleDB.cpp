// OleDB.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "OleDB.h"
#include <atldbcli.h>
#include "QUerySource.h"

using namespace GRServer;
static const wchar_t CONFIG_FILE[] = L"OleDBPlugin.cfg";

//class Connection
//{
//};
//
//class OleTableSource : public IDataSource::ICreator
//{
//public:
//   virtual const wchar_t* Name() const { return L"OleTable"; }
//
//   virtual IReader*    CreateReader(const ParamList& parameters, const ISessionObject& object) const
//   {
//      return GRServer::CreateReader(
//   }
//   virtual IWriter*    CreateWriter(IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
//   virtual IRemover*   CreateRemover(IRemover* parent, const ParamList& parameters, const ISessionObject& object) const;
//   virtual IObjSource* CreateObjSource(const ParamList& parameters, const ISessionObject& object) const { return NULL; }
//   virtual ISelector*  CreateSelector(const ParamList& parameters, const ISessionObject& object) const;
//};

std::wstring configFile;
GRServer::IServer* gServer;

OleDBPlugin::OleDBPlugin()
{
   CoInitializeEx(NULL, COINIT_MULTITHREADED /*COINIT_APARTMENTTHREADED*/);
}

OleDBPlugin::~OleDBPlugin()
{
   CoUninitialize();
}

bool OleDBPlugin::Init(IServer* server)
{
   gServer = server;

   const IServerConfig& config = server->GetConfig();

   USES_CONVERSION;
   configFile = A2W(config.PluginsFolder());
   configFile += CONFIG_FILE;

   Config cfg;
   if( cfg.Load(configFile) == false )
   {
      server->AddError(false, "Нет настроек для драйвера OleDB - запустите настройку модуля в Plugins");
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
   //dsr->AddSource(new OleTableSource());

   return true;
}

bool OleDBPlugin::Connect(Socket *socket, const wchar_t* password)
{
   return true;
}

void OleDBPlugin::Close()
{
}
