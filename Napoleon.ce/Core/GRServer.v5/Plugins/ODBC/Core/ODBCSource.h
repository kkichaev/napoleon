#pragma once

#define DEFINE_PLUGIN
#include <vector>
#include <iplugin.h>
#include <iserver.h>
#include <idatasource.h>

#include <sql.h>
#include <sqlext.h>

namespace GRServer {

struct Config
{
   std::wstring provider;
   std::wstring connStr;

	//DWORD concurentStmtCount;

   bool useAsInternalBase;

   Config();

   bool MakeConnectionString(std::wstring* connStr);

   bool Load(const std::wstring& fileName);
   bool Save(const std::wstring& fileName);
   void SetValue(const std::wstring& key, const std::wstring& value);
};

class Configurator : public IPluginConfig
{
public:
   Configurator();

   virtual bool Configure(IServer* server, HWND owner);

   void InitDialog(HWND hDlg);
   void Save(HWND hDlg);
   void LoadSettings(HWND hDlg);
   void TestConnection(HWND hDlg);
   void Dispose();
   void OnProviderChanged(HWND hWnd);

protected:
   std::wstring fileName;
   std::wstring connStr;

   SQLHENV hEnv;

   void LoadProviders(HWND hCombo, const std::wstring& selected);
};

class OleDBPlugin : public IPlugin
{
public:
   OleDBPlugin();
   ~OleDBPlugin();

   virtual const wchar_t* Name() const { return L"ƒрайвер ODBC"; }
   virtual const wchar_t* Version() const { return L"1.0.0.1"; }

   virtual bool Init(IServer* server);
   virtual bool Connect(Socket *socket, const wchar_t* password);
   virtual void Close();

   // этот метод сервер вызывает много раз (определ€€ можно ли конфигурировать plugin
   // конструктор должен быть "легкий"
   virtual IPluginConfig* GetConfig() const { return new Configurator(); }
};

IInternalDataSource* CreateInternalDS(bool noCheckFormat);
} // namespace GRServer

extern HINSTANCE hInstance;
extern std::wstring configFile;
extern GRServer::IServer* gServer;