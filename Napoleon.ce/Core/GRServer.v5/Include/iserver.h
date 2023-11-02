/*
 * Copyright (C), 2009 - 2010, ����� �������
 *
 * �������� �������
 *
 * ert   21/08/2010   creating
 */
#ifndef __ISERVER_H
#define __ISERVER_H

#include <string>
#include <map>

#include <ierrlog.h>

namespace GRServer {

struct IThreadWorker;
struct IPlugin;
class Socket;

class IServerConfig
{
public:
   virtual const char* ExchangeFolder() const = 0;
	virtual const char* ImageFolder() const = 0;
   virtual const char* ConfigFolder() const = 0;
   virtual const char* ProgFolder() const = 0;

   virtual IErrorLogger::DebugLevel Debug() const = 0;

   virtual const char* Option(const std::string& key) const = 0;
   virtual bool HaveFeature(const std::wstring& ftrExpr) const = 0;
	virtual bool NoCheckFormat() const = 0;
	virtual size_t MemoryLimit() const = 0;
   virtual bool OpenConsole() const = 0;
   virtual size_t UploadLimit() const = 0;
};

enum ServerRunMode { srmUndef, srmTray, srmService };
struct IServer : public IErrorLogger
{
   virtual const IServerConfig& GetConfig() const = 0;

   //
   // ------------- ��������� ��� update ---------------
   //
   // �������� ������������� ������, ����� �����������, ������ �������������
   virtual void Stopping(const char *mutextName) = 0;

   // ������ ��� ������� �������
   virtual const char* ExecString() const = 0;

   virtual ServerRunMode RunMode() const = 0;

   virtual bool Install(ServerRunMode mode, bool runAfterInstall ) const = 0;

	virtual const wchar_t* ServerName() const = 0;


   //
   // ------------- ������ ������� ---------------
   //
   virtual void* GetService(const wchar_t* name) = 0;

   virtual bool Execute(IThreadWorker *thread) = 0;

   //
   // // ------------- ������ � ��������� ---------------
   // //
   // virtual bool ConnectPlugin(const wchar_t* name, Socket* src) = 0;

   // virtual void PluginClosed(IPlugin* plugin) = 0;

#ifdef UNIX
#else
   virtual void PluginConfigure(HWND owner) = 0;
#endif

};

} // namespace GRServer

#endif
