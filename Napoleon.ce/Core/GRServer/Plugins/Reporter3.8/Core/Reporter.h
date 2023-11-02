#pragma once

#define DEFINE_PLUGIN
#include <vector>
#include <iplugin.h>
#include <iserver.h>
#include <idatasource.h>

#include <python.h>
#include "config.h"

namespace GRServer {

class ReporterPlugin : public IPlugin
{
public:
   ReporterPlugin();
   ~ReporterPlugin();

   virtual const wchar_t* Name() const { return L"Reporter-plugin"; }
   virtual const wchar_t* Version() const { return L"1.0.0.1"; }

   virtual bool Init(IServer* server);
   virtual bool Connect(Socket *socket, const wchar_t* password);
   virtual void Close();

   // этот метод сервер вызывает много раз (определ€€ можно ли конфигурировать plugin
   // конструктор должен быть "легкий"
   virtual IPluginConfig* GetConfig() const { return NULL; }

   virtual bool Handle(const wchar_t* command, const Member* param, ISession* session);

	void PutCOMObject(const std::string& tag, IDispatch* obj);
	IDispatch* GetCOMObject(const std::string& tag);

	const Config& ReporterConfig() const { return config; }

protected:
   bool debugable;
	Config config;
	//FILE *dbgLog;

	struct COMData
	{
		std::string tag;
		IDispatch* obj;
		FILETIME outTime;

		bool operator< (const COMData& src) const
		{
			return (long)obj < (long)src.obj;
		}
	};

	std::set<COMData> comObjects;
	CRITICAL_SECTION comSec;

	void RemoveOutLeaveCOM(const FILETIME& ft);
};

IInternalDataSource* CreateInternalDS();
} // namespace GRServer

extern HINSTANCE hInstance;
extern std::string configFile;
extern GRServer::IServer* gServer;