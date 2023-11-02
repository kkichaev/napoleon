#pragma once

#define DEFINE_PLUGIN
#include <vector>
#include <iplugin.h>
#include <iserver.h>
#include <idatasource.h>

#include <mysql.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

namespace GRServer {

struct Config
{
   std::string host;
   DWORD port;

   WORD defaultStringLength;
   
   std::string base;
   
   bool useAsInternalBase;

   std::string login;
   std::string password;

   Config();

   bool Load(const std::string& fileName);
   bool Save(const std::string& fileName);
   void SetValue(const std::string& key, const std::string& value);
};

class Configurator : public IPluginConfig
{
public:
   Configurator();

   virtual bool Configure(IServer* server, HWND owner);

   void InitDialog(HWND hDlg);
   void Save(HWND hDlg);

protected:
   std::string fileName;
};

class MySQLPlugin : public IPlugin
{
public:
   MySQLPlugin();
   ~MySQLPlugin();

   virtual const wchar_t* Name() const { return L"ƒрайвер MySQL"; }
   virtual const wchar_t* Version() const { return L"1.0.0.1"; }

   virtual bool Init(IServer* server);
   virtual bool Connect(Socket *socket, const wchar_t* password);
   virtual void Close();

   // этот метод сервер вызывает много раз (определ€€ можно ли конфигурировать plugin
   // конструктор должен быть "легкий"
   virtual IPluginConfig* GetConfig() const { return new Configurator(); }
};

IInternalDataSource* CreateInternalDS();

extern const wchar_t* SENDED_FIELDS;
const char ESCAPE_SYM = '`';

// dest string don't clearing
const wchar_t* QuoteString(std::wstring* dest, char sym=ESCAPE_SYM);
const char* QuoteString(std::string* dest, char sym=ESCAPE_SYM);
const char* QuoteString(std::string* dest, const std::wstring& src, char sym=ESCAPE_SYM);
const wchar_t* QuoteString(std::wstring* dest, const std::wstring& src, char sym=ESCAPE_SYM);
const char* QuoteString(std::string* dest, const std::string& src, char sym=ESCAPE_SYM);

bool Execute(MYSQL *mysql, const std::string& sql);
void AddErrorsToLog(bool isCritical, MYSQL* conn, IErrorLogger::DebugLevel level = IErrorLogger::None);
void AddErrorsToLog(MYSQL_STMT * stmt, IErrorLogger::DebugLevel level = IErrorLogger::None);
void PKToList(std::vector<std::wstring>* fields, const std::wstring& _str, bool quoting);
bool ConvertToDate(std::string* dst, const char *src);

IDataSource::IReader* CreateReader(const ParamList& parameters, const ISessionObject& object, MYSQL* connection);
IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object, MYSQL* connection);
IDataSource::IRemover* CreateRemover(const ISessionObject& object, MYSQL* connection);

MYSQL* GetConnection();

} // namespace GRServer

extern HINSTANCE hInstance;
extern std::string configFile;
extern GRServer::IServer* gServer;

#define W2U(lp) W2A_CP(lp, CP_UTF8)
#define U2W(lp) A2W_CP(lp, CP_UTF8)