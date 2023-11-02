/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Класс диспетчера сервера
 *
 * ert   27/03/2009   creating
 */

#include "stdafx.h"
#include <malloc.h>
#include "dispatcher.h"
#include "srvutility.h"
#include "sessobj.h"
#include "parse.h"
#include "objdef.h"
#include "stdobjs.h"
#include "AES.h"
#include <cost.h>

#include "srvdata.h"

#ifdef USE_CURL
#include "curl_service.h"
#endif

#ifdef UNIX
#else
#include "service.h" // IsInstalled
#include "tray.h"    // IsInstalled
#endif

#include "packet.h" // Decompress here

#include "folderset.h"

#include "joinsrv.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#ifdef UNIX
#else
#include <resource.h>
#endif

using namespace GRServer;
using namespace std;

const char GRServer::SERVER_MUTEX[] = "GRServer_mutex";

DWORD Dispatcher::MAX_THREADS = 0xFFFF;


const DWORD TEST_JSID = 0x82131415; // 130.19.20.21

Dispatcher::Dispatcher() : runMode(srmUndef)
   //, config(NULL)
{
   stopMutex = INVALID_HANDLE_VALUE;
	lcReqSending = false;
}

Dispatcher::~Dispatcher()
{
}

bool Dispatcher::OpenSocket()
{
   sockaddr_in sockaddr;
   sockaddr.sin_family = AF_INET;
   sockaddr.sin_addr.s_addr = INADDR_ANY;
   sockaddr.sin_port = htons(dataCtrl.Config().port);

   socket = ::socket(AF_INET, SOCK_STREAM, 0);
   if( socket == INVALID_SOCKET )
   {
      //AddError(true, "открытия порта %d, код ошибки %d", dataCtrl.Config().port, WSAGetLastError());
      return false;
   }

	int bufSize = 300 * 1024;
	setsockopt(socket, SOL_SOCKET, SO_RCVBUF, (const char*)&bufSize, sizeof(bufSize));
	setsockopt(socket, SOL_SOCKET, SO_SNDBUF, (const char*)&bufSize, sizeof(bufSize));

   if( bind(socket, (struct sockaddr*)&sockaddr, sizeof(sockaddr)) == SOCKET_ERROR )
   {
      //AddError(true, "bind %d", WSAGetLastError());
      return false;
   }

   if( listen(socket, 10) == SOCKET_ERROR )
   {
      //AddError(true, "listen %d", WSAGetLastError());
      return false;
   }

   return true;
}

bool Dispatcher::Init(DWORD argc, const char **argv, IProgramLoader *loader)
{
   this->loader = loader;

   InitLog();

#ifdef DEBUG
	MessageBox(NULL, L"", L"", MB_OK);
#endif
   //NULL(MessageBox, L"!", L"", MB_OK);

	ServObject::InitLocale();

#ifdef UNIX
#else
   WSADATA wsaData;
   int res = WSAStartup(MAKEWORD(2,2), &wsaData);
   if( res )
   {
      AddError(true, "WSAStartup %d", res);
      return false;
   }
#endif

   ServerConfig& config = dataCtrl.Config();

   config.ParseCmdLine(argc, argv);
   if( !config.Load() )
   {
      AddError(true, "Ошибка загрузки файла конфигурации '%s'", config.configFile.c_str());
      return false;
   } else
      AddLog("Starting");

	InitSemaphore(config.concurentConnections);

	InitFunctions();

	if (!LoadInternalObjects() || !ObjectDef::Load(config.defsFile))
   {
      return false;
   }

	if (config.addDefsFile.size())
   {
      std::vector<std::string>::const_iterator i = config.addDefsFile.begin();
      for( ; i != config.addDefsFile.end(); i++ )
         if( !ObjectDef::Load(*i) )
            return false;
   }

	ObjectDef::ModifyObjects();
	{
		CRITICAL_SECTION cs[12];
		int i;
		for (i = 0; i < 12; i++)
			InitializeCriticalSection(&cs[i]);

	}

   //if( config->addDefsFile.empty() == false )
   //{
   //   if( !ObjectDef::Load(config->addDefsFile) )
   //      return false;
   //}

	LoadPlugins();

   if( !DataSource::Init(config, this) )
   {
#ifdef UNIX
#else
      if( runMode == srmTray )
      {
         if( MessageBox(NULL, L"Работа сервера невозможна - ошибка инициализаци базы данных.\nВывести окно настроек сервера?", L"Вопрос", MB_YESNO | MB_ICONEXCLAMATION) == IDYES )
         {
            config.Edit(GetModuleHandle(NULL));
            return false;
         }
      }
#endif
      return false;
   }
	
   for( DWORD ic=1; ic<argc; ic++ )
	{
		if( !strcmp(argv[ic], "GetTableDef") )
		{
			if( ic < argc-1 )
				PrintTableDef(argv[ic+1]);

			Cleanup();
			return false;
		}
	}
   // start server
   if( !OpenSocket() )
   {
#ifdef UNIX
         AddError(true, "открытия порта %d, код ошибки %d", dataCtrl.Config().port, errno);
#else
      if( runMode == srmTray )
      {
         if( MessageBox(NULL, L"Работа сервера невозможна - порт занят.\nВывести окно настроек сервера?", L"Вопрос", MB_YESNO | MB_ICONEXCLAMATION) == IDYES )
         {
            config.Edit(GetModuleHandle(NULL));
            return false;
         }
      } else
         AddError(true, "открытия порта %d, код ошибки %d", dataCtrl.Config().port, WSAGetLastError());
#endif

      Cleanup();
      return false;
   }

   dataCtrl.RefreshUsers();

#ifdef UNIX
#else
   evStop = CreateEvent(NULL, TRUE, FALSE, NULL);
   evAccept = WSACreateEvent();
   WSAEventSelect(socket, evAccept, FD_ACCEPT);
#endif

	if (config.useGRJS && !config.jsLogin.empty() && !config.jsPassword.empty())
	{
		USES_CONVERSION;
		JoinServer::Start(config.jsLogin, config.jsPassword, W2A(PROJECT_NAME), config.port, false);
	}

	StartBackupThread();
	return AddOnInit();
}

void Dispatcher::Cleanup()
{
	JoinServer::Stop();

	CloseSemaphore();

   FreePlugins();

   CloseFunctions();

   dataCtrl.Close();
   DataSource::Cleanup();

   ObjectDef::Clear();
   AESFree();

#ifdef UNIX
#else
	AddLog("Stopped");
#endif

#ifdef UNIX
   Thread::KillingThreads();
#else
   WSACleanup();
   if( stopMutex != INVALID_HANDLE_VALUE )
   {
      ReleaseMutex(stopMutex);
      CloseHandle(stopMutex);
      stopMutex = INVALID_HANDLE_VALUE;
   }
#endif
	StopLog();
}

void Dispatcher::Kill()
{
   Thread::KillingThreads();
}

void Dispatcher::Stopping(const char *mutexName)
{
   if( loader != NULL )
      loader->Stopping(*this);
}

const char* Dispatcher::ExecString() const
{
   if( loader != NULL )
      return loader->ExecString(*this);
   return "";
}

const wchar_t* Dispatcher::Name() const
{
   static std::wstring value;
   if( value.empty() )
   {
      const char* opt = dataCtrl.Config().Option("serviceName");
      if( opt == NULL || *opt == '\0' )
         value = L"GRServer";
      else
      {
         USES_CONVERSION;
         value = A2W(opt);
      }
   }
   return value.c_str();
}

const wchar_t* Dispatcher::DisplayName() const
{
   static std::wstring value;
   const char* opt = dataCtrl.Config().Option("displayName");
   if( opt == NULL || *opt == '\0' )
   {
      value = L"Сервер комплекса \"Наполеон\" ";
      value += PROJECT_NAME;
   }
   else
   {
      USES_CONVERSION;
      value = A2W(opt);
   }
   return value.c_str();
}

void Dispatcher::FreePlugins()
{
	dataCtrl.PluginsClear();

	PluginList::iterator i = connectedPlugins.begin();
	for (; i != connectedPlugins.end(); i++)
	{
		if (i->inited)
			i->plugin->Close();
	}
	connectedPlugins.clear();
	
	i = plugins.begin();
	for (; i != plugins.end(); i++)
	{
		if (i->inited)
			i->plugin->Close();
		delete i->plugin;
	}
	plugins.clear();
}

void* Dispatcher::GetService(const wchar_t* name)
{
   if( wcscmp(name, OBJDEF_SERVICE) == 0 )
      return ObjectDef::GetService();

   if( wcscmp(name, SOURCE_SERVICE) == 0 )
      return DataSource::GetService();

   if( wcscmp(name, FOLDER_ID_SERVICE) == 0 )
      return &folderHolder;

	if (wcscmp(name, COST_SERVICE_NAME) == 0)
		return &costService;

#ifdef USE_CURL
	if (wcscmp(name, CURL_SERVICE) == 0)
		return CurlService::GetService();
#endif

   return NULL;
}

bool Dispatcher::Execute(IThreadWorker *thread)
{
	
	return Thread::Starting(thread, this, MAX_THREADS, ((ServerConfig&)GetConfig()).makeDumpOnException);
}

bool Dispatcher::HandleCommand(const wchar_t* command, const Member* param, ISession* session)
{
   bool res = false;

   PluginList::iterator i = plugins.begin();
   for( ; i != plugins.end(); i++ )
      if( i->plugin->Handle(command, param, session) )
      {
         res = true;
         break;
      }

   return res;
}

bool Dispatcher::ConnectPlugin(const wchar_t* name, Socket* src)
{
   bool res = false;
   bool needAdd = false;
   IPlugin* plugin = NULL;

   PluginList::iterator i = connectedPlugins.begin();
   for( ; i != connectedPlugins.end(); i++ )
   {
      if( i->inited && wcscmp(i->plugin->Name(), name) == 0 )
      {
         plugin = i->plugin;
         plugin->Close();
         dataCtrl.PluginClosed(name);
         break;
      }
   }

   if( plugin == NULL )
   {
      i = plugins.begin();
      for( ; i != plugins.end(); i++ )
      {
         if( i->inited && wcscmp(i->plugin->Name(), name) == 0 )
         {
            needAdd = true;
            plugin = i->plugin;
            break;
         }
      }
   }

   if( plugin != NULL )
   {
      std::wstring password;
      dataCtrl.PluginConnected(&password, name);
      res = plugin->Connect(src, password.c_str());
      if( !res )
         dataCtrl.PluginClosed(name);
      else if( needAdd )
      {
         PluginData pd;
         pd.inited = true;
         pd.plugin = plugin;
         connectedPlugins.push_back(pd);
      // send answer &&
      // src->CopyTo();
      }
   }
   else
   {
      std::wstring msg(L"Не могу найти plugin '");
      msg += name;
      msg += L"'";
      SendAnswer(src, false, msg.c_str());

      // socket closed in RequestHandler
   }

   return res;
}

void Dispatcher::PluginClosed(IPlugin* plugin)
{
   PluginList::iterator i = connectedPlugins.begin();
   for( ; i != connectedPlugins.end(); i++ )
   {
      if( i->plugin == plugin )
      {
         dataCtrl.PluginClosed(plugin->Name());
         connectedPlugins.erase(i);
         break;
      }
   }
}

const char* TypeToString(const IObjectData::Field& field)
{
	switch(field.format.type)
	{
	case MemberFormat::mtBinary:
		return "binary";
	case MemberFormat::mtString:
		return "string";
	case MemberFormat::mtNumber:
		return ((field.flags & IObjectData::Field::Hex) == 0) ? "numeric" : "hex";
	case MemberFormat::mtDateTime:
		return (field.format.format.dateFormat == MemberFormat::Stamp) ? "timestamp" :
			(field.format.format.dateFormat == MemberFormat::Date) ? "date" :
			"time";
	case MemberFormat::mtObject:
		return "object";
	}

	return "undef";
}

bool Dispatcher::PrintTableDef(const char* tableName)
{
	USES_CONVERSION;
	const ObjectDef* od = ObjectDef::Get(A2W(tableName));
	if( od != NULL )
	{
		IObjectData::Fields::const_iterator fi = od->fields.begin();
		for( ; fi != od->fields.end(); fi++ )
		{
			fprintf(stderr, "%s;%s;%s;%d;%d\n", W2A(fi->format.name.c_str()), W2A(fi->data.c_str()), TypeToString(*fi),
				fi->width, (fi->format.type == MemberFormat::mtNumber) ? fi->format.format.fraction : 0);
		}
	}

	return (od != NULL);
}

static HANDLE hStmtSem = NULL;
static DWORD reqStmtCount = 0;
void Dispatcher::InitSemaphore(int count)
{
	if (count > 0)
		hStmtSem = CreateSemaphore(NULL, count, count, NULL);
}

void Dispatcher::RequestSemahore()
{
	if (hStmtSem == NULL)
		return;

	InterlockedIncrement((LONG*)&reqStmtCount);
	gServer->AddLog(IErrorLogger::Full, "req_stmt %d", reqStmtCount);

	WaitForSingleObject(hStmtSem, INFINITE);
}

void Dispatcher::ReleaseSemaphore()
{
	if (hStmtSem == NULL)
		return;

	InterlockedDecrement((LONG*)&reqStmtCount);
	LONG prev;
	::ReleaseSemaphore(hStmtSem, 1, &prev);
	gServer->AddLog(IErrorLogger::Full, "dec_stmt %d", prev);
}

void Dispatcher::CloseSemaphore()
{
	if (hStmtSem == NULL)
		return;

	CloseHandle(hStmtSem);
	hStmtSem = NULL;
}

bool Dispatcher::ReqLicenseData(CString* log)
{
	if (lcReqSending)
		return false;

	if (UserActivityHolder::CanSendRequest(log))
	{
		lcReqSending = true;
		return true;
	}
	return false;
}

void Dispatcher::LicenseRequestDone(bool commit)
{
	if (lcReqSending)
	{
		lcReqSending = false;
		UserActivityHolder::CommitRequest(commit);
	}
}
