/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Класс диспетчера сервера
 *
 * ert   27/03/2009   creating
 */
#ifndef __GR_SERVER_DISPATCH_H
#define __GR_SERVER_DISPATCH_H

#include "server.h"
#include <iplugin.h>
#include "progloader.h"
#include "datactrl.h"

#include "thread.h"
#include "socket.h"

#include <string>
#include <sstream>

namespace GRServer {

class ServerConfig;
class Dispatcher : public IRunnableModule, public IProgramNotify, public IServer
{
public:
   Dispatcher();
   ~Dispatcher();

   void SetRunMode(ServerRunMode runMode) { this->runMode = runMode; }

   DataController& Controller() { return dataCtrl; }

   //  ------------  IRunnableModule -------------------
   virtual bool Init(DWORD argc, const char **argv, IProgramLoader *loader);
   virtual void WINAPI Run();

   virtual void Stop();
   virtual void Kill();

   virtual const wchar_t* Name() const;
   virtual const wchar_t* DisplayName() const;

   //  -----------------  IProgramNotify -------------------
   virtual void ProgramNotify(IProgramLoader *loader, HWND hWnd);

   // -------------------- IServer -----------------------
   virtual const IServerConfig& GetConfig() const { return dataCtrl.Config(); }
   virtual void Stopping(const char *mutextName);
   virtual const char* ExecString() const;
   virtual ServerRunMode RunMode() const;
   virtual bool Install(ServerRunMode mode, bool runAfterInstall) const;

   virtual void AddLog(const char* msg, ... );
   virtual void AddLog(DebugLevel level, const char* msg, ... );
   virtual void AddError(bool critical, const char* msg, ... );

   // ------------- Методы сервера ---------------
	virtual const wchar_t* ServerName() const { return DisplayName(); }
   virtual void* GetService(const wchar_t* name);
   virtual bool Execute(IThreadWorker *thread);

   virtual bool ConnectPlugin(const wchar_t* name, Socket* src);
   virtual void PluginClosed(IPlugin* plugin);
   virtual void PluginConfigure(HWND owner);

   bool HandleCommand(const wchar_t* command, const Member* param, ISession* session);
	
	bool ReqLicenseData(CString* log);
	void LicenseRequestDone(bool commit);

	void GetActiveUsers(std::vector<ActiveUsersData>* data) { dataCtrl.GetActiveUsers(data); }

   struct PluginData
   {
      IPlugin* plugin; // may be null if plugin not inited
      bool inited;
   };
   typedef std::vector<PluginData> PluginList;

	static void RequestSemahore();
	static void ReleaseSemaphore();
	static void InitSemaphore(int count);
	static void CloseSemaphore();

protected:
   class RequestHandler : public IThreadWorker
   {
      // для остановки используется evStop у диспетчера
   public:
      RequestHandler();
      ~RequestHandler();

      bool Accept(Dispatcher *dispatcher, SOCKET srvSock);
      virtual DWORD Execute();

   protected:
      Dispatcher *dispatcher;
      Socket socket;
   };

protected:
   void InitLog();
   void Cleanup();

   bool OpenSocket();

   void LoadPlugins();
   void FreePlugins();
	void StopLog();

	bool PrintTableDef(const char* tableName);

   bool LoadInternalObjects();

	void StartBackupThread();

public:
	HANDLE evStop;

protected:
   ServerRunMode runMode;
   //ServerConfig *config;
   IProgramLoader *loader;

   DataController dataCtrl;

	bool lcReqSending;
   SOCKET socket;
   HANDLE evAccept;
   HANDLE stopMutex;

   PluginList plugins;
   PluginList connectedPlugins;

	static DWORD MAX_THREADS;
};

} // namespace GRServer

#endif
