#pragma once

#define DEFINE_PLUGIN
#include <vector>
#include <iplugin.h>
#include <iserver.h>
#include <idatasource.h>

#include <Python.h>
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

   virtual IPluginConfig* GetConfig() const { return NULL; }

   virtual bool Handle(const wchar_t* command, const Member* param, ISession* session);

#ifdef UNIX
#else
	void PutCOMObject(const std::string& tag, IDispatch* obj);
	IDispatch* GetCOMObject(const std::string& tag);
#endif

	const Config& ReporterConfig() const { return config; }

protected:
	bool debugable;
	Config config;

#ifdef UNIX
#else
	struct COMData
	{
		std::string tag;
		IDispatch* obj;
		FILETIME outTime;

		bool operator< (const COMData& src) const
		{
			return (size_t)obj < (size_t)src.obj;
		}
	};

	std::set<COMData> comObjects;
	CRITICAL_SECTION comSec;

	void RemoveOutLeaveCOM(const FILETIME& ft);
#endif

};

} // namespace GRServer

#ifdef UNIX
#else
extern HINSTANCE hInstance;
#endif

extern std::string configFile;
extern GRServer::IServer* gServer;