/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Загрузчики компонентов. Здесь описываются загрузчики и инициализаторы для всех подсистем
 *
 * ert   02/05/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "srvdata.h"
#include "joinsrv.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#ifdef UNIX
#include <atlconv.h>
#else
#include "resource.h"
#include <shlobj.h> // set default path for exchnagefolder
#endif
#include "srvutility.h"
#include "grftrs.h"

using namespace GRServer;
using namespace std;

const char CONFIG_FILE_PARAM[] = "config-file";
const char DEFS_FILE_PARAM[] = "serverDefs";
const char ADD_DEFS_FILE_PARAM[] = "addDefs";
const char UPDATE_FOLDER[] = "updateFolder";
const char EXCHANGE_FOLDER[] = "exchangeFolder";
const char SERVER_BASE[] = "serverBase";
const char PLUGINS_FOLDER[] = "pluginsFolder";
const char FEATURE_TAG[] = "featuresFile";
const char DBF_CP[] = "dbfCodePage";
const char IMAGE_FOLDER[] = "imageFolder";
const char MAKE_DUMP[] = "dumpOnException";
const char MAKE_BACKUP[] = "makeBackup";
const char BACKUP_FOLDER[] = "backupFolder";
const char BACKUP_COPIES[] = "backaupCopies";
const char BACKUP_RUN_DAYS[] = "backupRunDays";
const char BACKUP_START_TIME[] = "backupStartTime";
const char NO_CHECK_FORMAT[] = "noCheckFormat";
const char JS_LOGIN[] = "jsLogin";
const char JS_PASSWORD[] = "jsPassword";
const char USE_GRJS[] = "useGRJS";

const char DEFAULT_FEATURES_FILE[] = "GRServer.ftr";

const char DEFAULT_CONFIG_FILE[] = "GRServer.ini";
const char DEFS_FILE[] = "serverDefs.xml";
const DWORD DEFAULT_PORT = 8888;

const char CONFIG_SUBKEY[]     = "SOFTWARE\\Ert\\Napoleon";
const char FOLDER_VALUE_NAME[] = "ExchangeFolder";
const char PORT_VALUE_NAME[]   = "Port";

const char DEBUG_KEY_NAME[]   = "Debug";
const char LOG_SIZE_KEY_NAME[] = "logSize";

const char CONC_STMT_STR[] = "ConcurentConnections";


bool ServerConfig::ftrLoaded = false;

extern "C" int DBF_CODE_PAGE = CP_OEMCP;

struct BkcpHeader
{
	char tag[5];
	char bkTime[16];
};

const char* ServerConfig::BackupExtention = ".bkp";
const char* ServerConfig::BackupPrefix = "grs";
const char* ServerConfig::BackupFileTag = "GRBKP";
const char* ServerConfig::MakeBackupName(std::string* outName, const std::string& folder, FILETIME& ft)
{
	char buf[30];
	sprintf(buf, "%08X%08X", ft.dwHighDateTime, ft.dwLowDateTime);

	outName->append(folder).append(BackupPrefix).append(buf).append(BackupExtention);
	return outName->c_str();
}


//
// --------------------------- ServerConfig --------------------------
//
ServerConfig::ServerConfig()
{
   SetDefault();
}

ServerConfig::ServerConfig(const ServerConfig& src)
{
   this->operator=(src);
}

ServerConfig& ServerConfig::operator= (const ServerConfig& src)
{
   if( this != &src )
   {
      configFile = src.configFile;
      exchangeFolder = src.exchangeFolder;
      port = src.port;
      debugLevel = src.debugLevel;

      defsFile = src.defsFile;
      updateFolder = src.updateFolder;
      serverBase = src.serverBase;
      addDefsFile = src.addDefsFile;
      pluginsFolder = src.pluginsFolder;
      featuresFile = src.featuresFile;

      feFolder = src.feFolder;
      fuFolder = src.fuFolder;
      fpFolder = src.fpFolder;

      items = src.items;
		logLength = src.logLength;
		concurentConnections = src.concurentConnections;

		makeDumpOnException = src.makeDumpOnException;

		makeBackup = src.makeBackup;
		backupFolder = src.backupFolder;
		backupCopies = src.backupCopies;
		backupStartTime = src.backupStartTime;
		backupRunDays = src.backupRunDays;

		noCheckFormat = src.noCheckFormat;
	}
   return *this;
}

void ServerConfig::SetDefault()
{
   defsFile = DEFS_FILE;

   configFile = DEFAULT_CONFIG_FILE;
   port = DEFAULT_PORT;

   debugLevel = IErrorLogger::None;
   featuresFile = DEFAULT_FEATURES_FILE;

	logLength = 100;
	concurentConnections = 0;
	makeDumpOnException = false;

	makeBackup = false;
	backupCopies = 0;
	backupStartTime = 0;
	backupRunDays = 0;
	noCheckFormat = false;
	useGRJS = false;

#ifdef UNIX
   pluginsFolder = "./";
	backupFolder = "./";
	imageFolder = "./agents/";
#else
   pluginsFolder = ".\\";
	imageFolder = ".\\agents\\";
	backupFolder = ".\\";
	HKEY hKey;
   if( RegOpenKeyExA(HKEY_LOCAL_MACHINE, CONFIG_SUBKEY, 0, KEY_READ, &hKey) == ERROR_SUCCESS )
   {
      char buf[MAX_PATH];
      DWORD cb = sizeof(buf)/sizeof(buf[0]);

      if( RegQueryValueExA(hKey, FOLDER_VALUE_NAME, NULL, NULL, (LPBYTE)buf, &cb) == ERROR_SUCCESS )
      {
         exchangeFolder.assign(buf);
         if( *exchangeFolder.rbegin() != '\\' )
            exchangeFolder.append(1, '\\');
      }

      cb = sizeof(port);
      RegQueryValueExA(hKey, PORT_VALUE_NAME, NULL, NULL, (LPBYTE)&port, &cb);

      RegCloseKey(hKey);
   }
#endif
}

void ServerConfig::UpdateFullNames()
{
   FullFileName(&feFolder, exchangeFolder.c_str());
   FullFileName(&fuFolder, updateFolder.c_str());
   FullFileName(&fpFolder, pluginsFolder.c_str());
}

void ServerConfig::ParseCmdLine(DWORD argc, const char* argv[])
{
   for( DWORD i=1; i<argc; i++ )
   {
      const char *key = argv[i];
      if( *key == '-' && key[1] == '-' && i < argc-1)
      {
         std::string val(argv[i+1]);
         if( *val.begin() == L'"' )
            val = val.substr(1, val.size()-2);
         if( SetValue(key+2, val) )
            i++;
      }
   }

   UpdateFullNames();
}

static void SetFolder(std::string* str, const std::string& value)
{
   *str = value;
#ifdef UNIX
   if( str->empty() || *str->rbegin() != '/' )
      str->append(1, '/');
   ConvertPath(*str, str);
#else
   if( str->empty() || *str->rbegin() != '\\' )
      str->append(1, '\\');
#endif
}

static int GetCodePage(const std::string& value)
{
   return ( _stricmp(value.c_str(), "ANSI") == 0 ) ? CP_ACP : CP_OEMCP;
}

static IErrorLogger::DebugLevel GetDebugLevel(const std::string& value)
{
   const char* str = value.c_str();
   if( _stricmp(str, "SHORT") == 0 ) return IErrorLogger::Short;
   if( _stricmp(str, "FULL") == 0 ) return IErrorLogger::Full;

   return IErrorLogger::None;
}

static bool ToBool(const std::string& value)
{
	return (_stricmp(value.c_str(), "true") == 0);
}

bool ServerConfig::SetValue(const std::string& key, const std::string& value)
{
   bool res = true;

   const char *k = key.c_str();
	char *ep;

   if( strcmp(k, CONFIG_FILE_PARAM) == 0 ) configFile = value;
   else if( strcmp(k, DEFS_FILE_PARAM) == 0 ) defsFile = value;
   else if( strcmp(k, ADD_DEFS_FILE_PARAM) == 0 ) addDefsFile.push_back(value);
   else if( strcmp(k, SERVER_BASE) == 0 ) serverBase = value;
   else if( strcmp(k, FEATURE_TAG) == 0 ) featuresFile = value;
   else if( strcmp(k, PORT_VALUE_NAME) == 0 ) port = atoi(value.c_str());
   else if( strcmp(k, UPDATE_FOLDER) == 0 ) SetFolder(&updateFolder, value);
   else if( strcmp(k, EXCHANGE_FOLDER) == 0 ) SetFolder(&exchangeFolder, value);
   else if( strcmp(k, PLUGINS_FOLDER) == 0 ) SetFolder(&pluginsFolder, value);
   else if( strcmp(k, DBF_CP) == 0 ) DBF_CODE_PAGE = GetCodePage(value);
   else if( strcmp(k, DEBUG_KEY_NAME) == 0 ) debugLevel = GetDebugLevel(value);
	else if (strcmp(k, LOG_SIZE_KEY_NAME) == 0) logLength = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, IMAGE_FOLDER) == 0) SetFolder(&imageFolder, value);
	else if (strcmp(k, CONC_STMT_STR) == 0) concurentConnections = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, MAKE_DUMP) == 0) makeDumpOnException = ToBool(value);
	else if (strcmp(k, MAKE_BACKUP) == 0) makeBackup = ToBool(value);
	else if (strcmp(k, BACKUP_FOLDER) == 0) SetFolder(&backupFolder, value);
	else if (strcmp(k, BACKUP_COPIES) == 0) backupCopies = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, BACKUP_RUN_DAYS) == 0) backupRunDays = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, BACKUP_START_TIME) == 0) backupStartTime = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, NO_CHECK_FORMAT) == 0) noCheckFormat = ToBool(value);
	else if (strcmp(k, JS_LOGIN) == 0) jsLogin = value;
	else if (strcmp(k, JS_PASSWORD) == 0) jsPassword = value;
	else if (strcmp(k, USE_GRJS) == 0) useGRJS = ToBool(value);
	else
   {
      items[key].push_back(value);
   }

   return res;
}

bool ServerConfig::Load()
{
   std::string fileName;
   FullFileName(&fileName, configFile.c_str());

	FILE *rd = fopen(fileName.c_str(), "rt");
   if( rd != NULL )
   {
		makeBackup = false;

		std::string line;
      while( ReadLine(&line, rd) )
      {
         size_t pos = line.find('=');
			if( pos != std::string::npos )
         {
            std::string key, value;

            Trim(&key, line, 0, pos);
            Trim(&value, line, pos+1, -1);

            SetValue(key, value);
         }
      }
      fclose(rd);
   } else
      gServer->AddError(false, "Не могу открыть файл конфигурации '%s'", configFile.c_str());

#ifdef UNIX
   UpdateFullNames();
#else
   if( exchangeFolder.empty() )
   {
      char path[MAX_PATH];
      SHGetSpecialFolderPathA(NULL, path, CSIDL_COMMON_DOCUMENTS, TRUE);
      exchangeFolder.assign(path);
#ifdef UNIX
      if( *exchangeFolder.rbegin() != '/' ) exchangeFolder += "/";
#else
      if( *exchangeFolder.rbegin() != '\\' ) exchangeFolder += "\\";
#endif
      exchangeFolder.append("Exp");

      CreateDirectoryA(exchangeFolder.c_str(), NULL);

      Save();
   } else
	{
      UpdateFullNames();
	}
#endif
   return true;
}

void ServerConfig::Save()
{
	if (serverBase.empty())
		return;

	std::string fileName;
	FullFileName(&fileName, configFile.c_str());
	
	FILE *wr = fopen(fileName.c_str(), "wt");
   if( wr )
   {
      fprintf(wr, "%s = %s\n", DEFS_FILE_PARAM, defsFile.c_str());
      if( addDefsFile.size() )
      {
         std::vector<std::string>::const_iterator i = addDefsFile.begin();
         for( ; i != addDefsFile.end(); i++ )
            fprintf(wr, "%s = %s\n", ADD_DEFS_FILE_PARAM, i->c_str());
      }
      //if( addDefsFile.empty() == false )
      //   fprintf(wr, "%s = %s\n", ADD_DEFS_FILE_PARAM, addDefsFile.c_str());
      fprintf(wr, "%s = %s\n", SERVER_BASE, serverBase.c_str());
      fprintf(wr, "%s = %s\n", UPDATE_FOLDER, updateFolder.c_str());
      fprintf(wr, "%s = %s\n", EXCHANGE_FOLDER, exchangeFolder.c_str());
      fprintf(wr, "%s = %s\n", PLUGINS_FOLDER, pluginsFolder.c_str());
      fprintf(wr, "%s = %d\n", PORT_VALUE_NAME, port);
      fprintf(wr, "%s = %s\n", FEATURE_TAG, featuresFile.c_str());
		fprintf(wr, "%s = %s\n", IMAGE_FOLDER, imageFolder.c_str());

      if( DBF_CODE_PAGE == CP_ACP )
         fprintf(wr, "%s = %s\n", DBF_CP, "ANSI");

		if (!jsLogin.empty())
		{
			fprintf(wr, "%s = %s\n", JS_LOGIN, jsLogin.c_str());
			fprintf(wr, "%s = %s\n", JS_PASSWORD, jsPassword.c_str());
			fprintf(wr, "%s = %s\n", USE_GRJS, (useGRJS ? "true" : "false"));
		}

		if (makeBackup)
		{
			fprintf(wr, "%s = %s\n", MAKE_BACKUP, "true");
			fprintf(wr, "%s = %s\n", BACKUP_FOLDER, backupFolder.c_str());
			fprintf(wr, "%s = %d\n", BACKUP_COPIES, backupCopies);
			fprintf(wr, "%s = %d\n", BACKUP_RUN_DAYS, backupRunDays);
			fprintf(wr, "%s = %d\n", BACKUP_START_TIME, backupStartTime);
		}


      if( debugLevel != IErrorLogger::None )
         fprintf(wr, "%s = %s\n", DEBUG_KEY_NAME, (debugLevel == IErrorLogger::Short) ? "short" : "full");

      std::map<std::string, Values>::const_iterator i = items.begin();
      for( ; i != items.end(); i ++ )
      {
         Values::const_iterator ci = i->second.begin();
         for( ; ci != i->second.end(); ci++ )
            fprintf(wr, "%s = %s\n", i->first.c_str(), ci->c_str());
      }


      fclose(wr);
   }

   UpdateFullNames();
}

bool ServerConfig::HaveFeature(const std::wstring& ftrExpr) const
{
   if( !ftrLoaded )
   {
		ftrLoaded = true;
      LoadFeatures(featuresFile);
   }

   return ::HaveFeature(ftrExpr);
}

#ifdef UNIX
#else
static ServerConfig *curEditConfig;
static ServerRunMode runMode;
static HINSTANCE hInstance;

const UINT SET_TEXT = WM_USER + 10;

class JSHandler : public JoinServerStatusHandler
{
	HWND hWnd;
	DWORD threadID;
	std::string tAdr;

public:
	JSHandler(HWND hWnd)
	{
		this->hWnd = hWnd;
		threadID = GetCurrentThreadId();
	}

	virtual void OnStatusChange(JoinServer::Status status);
};
static JSHandler* jsHandler;

const char* JSStatusToString(JoinServer::Status status)
{
	switch (status)
	{
	case JoinServer::None:
		return "";
	case JoinServer::Connecting:
		return "Подключение к JoinServer";
	case JoinServer::Working:
		return "Соединено с  JoinServer";
	case JoinServer::Error:
		return "Ошибка при подключении к JoinServer";
	}
	return "";
}

#ifndef PROJECT_NAME
#define PROJECT_NAME L"Test"
#endif

static void MakeAddrText(std::string* tstr, DWORD addr)
{
	char addrBuf[100];
	*addrBuf = 0;
	if (addr != 0)
		wsprintfA(addrBuf, "Адрес сервера: GRJS.%d.%d.%d.%d", (addr >> 24) & 0xFF, (addr >> 16) & 0xFF, (addr >> 8) & 0xFF, addr & 0xFF);
	tstr->assign(addrBuf);
}

static void SetJSAddress(HWND hWnd, DWORD addr)
{
	std::string addrBuf;
	MakeAddrText(&addrBuf, addr);
	SetDlgItemTextA(hWnd, IDC_JS_ADDRESS, addrBuf.c_str());
}

static const char* JSErrorToText(const std::string& error)
{
	if (error.compare("already_registred") == 0)
	{
		return "Такой логин уже существует";
	}

	if (error.compare("no_login_password") == 0)
	{
		return "Пользователь не найден";
	}

	if (error.compare("no_id") == 0)
	{
		return "Сервер не зарегистрирован";
	}
	return error.c_str();
}

void JSHandler::OnStatusChange(JoinServer::Status status)
{
	HWND hItem = GetDlgItem(hWnd, IDC_JS_STATUS);
	PostThreadMessageA(threadID, SET_TEXT, (WPARAM)hItem, (LPARAM)JSStatusToString(status));
	if (status == JoinServer::Error)
	{
		hItem = GetDlgItem(hWnd, IDC_JS_ADDRESS);
		PostThreadMessageA(threadID, SET_TEXT, (WPARAM)hItem, (LPARAM)JSErrorToText(JoinServer::GetError()));
	}
	else
	{
		hItem = GetDlgItem(hWnd, IDC_JS_ADDRESS);
		MakeAddrText(&tAdr, JoinServer::GetID());
		PostThreadMessageA(threadID, SET_TEXT, (WPARAM)hItem, (LPARAM)tAdr.c_str());
	}
}

static bool GetAccountData(std::string* login, std::string* pwd, HWND hWnd)
{
	char buf[100];

	if (GetDlgItemTextA(hWnd, IDC_JS_LOGIN, buf, sizeof(buf) - 1) > 0)
		(*login) = buf;
	else
	{
		MessageBox(hWnd, L"Не заполнен логин", L"Ошибка", MB_OK | MB_ICONSTOP);
		return false;
	}

	if (GetDlgItemTextA(hWnd, IDC_JS_PWD, buf, sizeof(buf) - 1) > 0)
		(*pwd) = buf;
	else
	{
		MessageBox(hWnd, L"Не заполнен пароль", L"Ошибка", MB_OK | MB_ICONSTOP);
		return false;
	}

	return true;
}

static void ConnectToJS(HWND hWnd)
{
	std::string login, pwd;
	if (!GetAccountData(&login, &pwd, hWnd))
		return;

	USES_CONVERSION;
	JoinServer::Start(login, pwd, W2A(PROJECT_NAME), curEditConfig->port, true);
	SetDlgItemTextA(hWnd, IDC_JS_STATUS, "");
	SetJSAddress(hWnd, 0);
}

static void CreateJSAccont(HWND hWnd)
{
	std::string login, pwd, error;
	if (!GetAccountData(&login, &pwd, hWnd))
		return;

	USES_CONVERSION;
	DWORD res = JoinServer::Register(&error, login, pwd, W2A(PROJECT_NAME));
	SetJSAddress(hWnd, res);
	if (res == 0)
	{
		SetDlgItemTextA(hWnd, IDC_JS_STATUS, "Ошибка регистрации");
		const char* errStr = JSErrorToText(error);
		SetDlgItemTextA(hWnd, IDC_JS_ADDRESS, errStr);
	}
	else
	{
		ConnectToJS(hWnd);
		MessageBox(hWnd, L"Сервер зарегестрирован", L"Информация", MB_OK | MB_ICONINFORMATION);
	}
}

static void SettingsInit(HWND hWnd, ServerConfig* config)
{
   curEditConfig = config;

   wchar_t buf[50];

	jsHandler = new JSHandler(hWnd);
	JoinServer::SetHandler(jsHandler);

   HWND runType = GetDlgItem(hWnd, IDC_RUN_TYPE);
   LoadString(hInstance, IDS_RUN_UNDEF, buf, sizeof(buf)/sizeof(buf[0]));
   SendMessage(runType, CB_ADDSTRING, 0, (LPARAM)(buf));
   LoadString(hInstance, IDS_RUN_TRAY, buf, sizeof(buf)/sizeof(buf[0]));
   SendMessage(runType, CB_ADDSTRING, 0, (LPARAM)(buf));
   LoadString(hInstance, IDS_RUN_SERVICE, buf, sizeof(buf)/sizeof(buf[0]));
   SendMessage(runType, CB_ADDSTRING, 0, (LPARAM)(buf));
   runMode = gServer->RunMode();
   SendMessage(runType, CB_SETCURSEL, (runMode == srmService) ? 2 : (runMode == srmTray) ? 1 : 0, 0);

   SetDlgItemInt(hWnd, IDC_PORT, curEditConfig->port, FALSE);
   SetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, curEditConfig->ExchFolderInt().c_str());

	SendMessage(GetDlgItem(hWnd, IDC_USE_GRJS), BM_SETCHECK, (config->useGRJS ? BST_CHECKED : BST_UNCHECKED), 0);
	if (config->useGRJS)
	{
		SetDlgItemTextA(hWnd, IDC_JS_LOGIN, config->jsLogin.c_str());
		SetDlgItemTextA(hWnd, IDC_JS_PWD, config->jsPassword.c_str());

		SetDlgItemTextA(hWnd, IDC_JS_STATUS, JSStatusToString(JoinServer::GetStatus()));

		if (JoinServer::GetStatus() == JoinServer::Working)
		{
			DWORD addr = JoinServer::GetID();
			SetJSAddress(hWnd, addr);
		}
		else
		{
			const char* errStr = JSErrorToText(JoinServer::GetError());
			SetDlgItemTextA(hWnd, IDC_JS_STATUS, errStr);
		}
	}
	

#ifdef PavlovStore
   ShowWindow(GetDlgItem(hWnd, IDC_REGISTER), SW_SHOW);
#endif
}

static bool SettingsSave(HWND hWnd)
{
   bool ret = true;
   char buf[MAX_PATH];
   GetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, buf, sizeof(buf));

   curEditConfig->ExchFolderInt().assign(buf);
   if( *curEditConfig->ExchFolderInt().rbegin() != '\\' ) curEditConfig->ExchFolderInt() += "\\";
   curEditConfig->port = GetDlgItemInt(hWnd, IDC_PORT, NULL, FALSE);
	
	if (GetDlgItemTextA(hWnd, IDC_JS_LOGIN, buf, sizeof(buf)-1) > 0)
		curEditConfig->jsLogin = buf;
	if (GetDlgItemTextA(hWnd, IDC_JS_PWD, buf, sizeof(buf) - 1) > 0)
		curEditConfig->jsPassword = buf;
	curEditConfig->useGRJS = ((SendMessage(GetDlgItem(hWnd, IDC_USE_GRJS), BM_GETSTATE, 0, 0) == BST_CHECKED) && !curEditConfig->jsLogin.empty() && !curEditConfig->jsPassword.empty());

   LRESULT cs = SendMessage(GetDlgItem(hWnd, IDC_RUN_TYPE), CB_GETCURSEL, 0, 0);
   ServerRunMode curMode = (cs==1) ? srmTray : (cs==2) ? srmService : srmUndef;
   if( curMode != runMode)
   {
      if( !gServer->Install(curMode, false) )
      {
         char* buf;
         wchar_t *bufW;
         FormatMessageA(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
            NULL, GetLastError(), 0,  (LPSTR) &buf, 0, NULL );

         gServer->AddError(false, buf);

         size_t len = strlen(buf) + 1;
         bufW = (wchar_t*)alloca(sizeof(wchar_t) * len);
         MultiByteToWideChar(CP_ACP, 0, buf, (int)len, bufW, (int)len);
         LocalFree(buf);

         ret = false;

         std::wstring msg = L"Ошибка при инсталляции сервера:\n";
         msg += bufW;
         MessageBox(NULL, msg.c_str(), L"Ошибка", MB_OK);
      }
   }

   return ret;
}

static bool IsPasswordEqual(HWND hWnd, std::wstring *password)
{
   bool ret = false;

   HWND hEdit1 = GetDlgItem(hWnd, IDC_PASSWORD);
   HWND hEdit2 = GetDlgItem(hWnd, IDC_PASSWORD2);

   int len1 = GetWindowTextLength(hEdit1) + 1;
   int len2 = GetWindowTextLength(hEdit2) + 1;

   if( len1 == len2 )
   {
      wchar_t *buf1 = (wchar_t*)alloca(len1 * sizeof(wchar_t));
      wchar_t *buf2 = (wchar_t*)alloca(len1 * sizeof(wchar_t));

      GetWindowText(hEdit1, buf1, len1);
      GetWindowText(hEdit2, buf2, len1);

      if( wcscmp(buf1, buf2) == 0 )
      {
         password->assign(buf1);
         ret = true;
      }
   }

   return ret;
}

INT_PTR CALLBACK SetPasswordProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
	case WM_INITDIALOG:
		SetWindowLong(hWnd, GWL_USERDATA, lParam);
		break;
   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDOK:
         {
            std::wstring password;
            if( IsPasswordEqual(hWnd, &password) )
            {
					DWORD wndVal = GetWindowLong(hWnd, GWL_USERDATA);
					if(wndVal == 0)
						ServerData::SetAdminPassword(password);
					else if (wndVal == 1)
						ServerData::SetCOMPassword(password);

					EndDialog(hWnd, IDOK);
            } else
            {
               wchar_t msgBuf[500], error[50];
               LoadString(hInstance, IDS_PWD_MISMATCH, msgBuf, sizeof(msgBuf)/sizeof(msgBuf[0]));
               LoadString(hInstance, IDS_ERROR, error, sizeof(error)/sizeof(error[0]));
               MessageBox(NULL, msgBuf, error, MB_OK);
            }
            break;
         }
      case IDCANCEL:
         EndDialog(hWnd, LOWORD(wParam));
         break;
      }
      break;
   }

   return FALSE;
}

static void ChangePassword(HINSTANCE hInstance, HWND hWnd)
{
   DialogBoxParam(hInstance, MAKEINTRESOURCE(IDD_CHANGE_PASSWORD), NULL, SetPasswordProc, 0l);
}

static void ChangeCOMPassword(HINSTANCE hInstance, HWND hWnd)
{
	DialogBoxParam(hInstance, MAKEINTRESOURCE(IDD_CHANGE_PASSWORD), NULL, SetPasswordProc, 1l);
}

static int CALLBACK InitialSetFolder(HWND hWnd, UINT iMsg, LPARAM , LPARAM lData)
{
   if( iMsg == BFFM_INITIALIZED )
      SendMessage(hWnd,BFFM_SETSELECTION, TRUE, lData);

   return 0;
}

static bool SetFolder(HWND hwndDlg, int id, int idsTitle)
{
	bool ret = false;

   LPMALLOC pMalloc;
   LPWSTR lpBuf;
   BROWSEINFO bi;
   LPITEMIDLIST pidlBrowse;

   wchar_t title[100];
   LoadString(hInstance, idsTitle, title, sizeof(title)/sizeof(title[0]));

   SHGetMalloc(&pMalloc);

   lpBuf = (LPWSTR)pMalloc->Alloc(MAX_PATH * sizeof(wchar_t));

   bi.hwndOwner = NULL;
   bi.pidlRoot = NULL;
   bi.pszDisplayName = lpBuf;
   bi.lpszTitle = title;
   bi.ulFlags = 0;

   GetDlgItemText(hwndDlg, id, lpBuf, MAX_PATH);
   if( *lpBuf )
   {
      bi.lpfn = (BFFCALLBACK)InitialSetFolder;
      bi.lParam = (LPARAM)lpBuf;
   } else
   {
      bi.lpfn = NULL;
      bi.lParam = 0;
   }

   pidlBrowse = SHBrowseForFolder(&bi);
   if( pidlBrowse != NULL )
   {
		if (SHGetPathFromIDList(pidlBrowse, lpBuf))
		{
			SetDlgItemText(hwndDlg, id, lpBuf);
			ret = true;
		}

      pMalloc->Free(pidlBrowse);
   }

   pMalloc->Free(lpBuf);
   pMalloc->Release();

	return ret;
}

#ifdef PavlovStore
#include "srvdata.h"
#include "idatasource.h"
#include "AES.h"
#include <wbemidl.h>
# pragma comment(lib, "wbemuuid.lib")

const int RegID = 0x352;
const __int64 MAX_SERVER_DEMO = (__int64)14 * 24 * 3600 * 10000000;
const DWORD CHECK_INTERVAL = 4000; //4032 * 1000;
static bool IsRegistred = true;
static HANDLE hRegThread;
struct RegisterData
{
   BYTE rep0[0x10];
   FILETIME started;
   BYTE rep1[0x10];
   __int64 request;
   __int64 response;
   BYTE rep2[0x10];
};

static bool PutData(const RegisterData& rd);
static __int64 Hash(__int64 a);
bool IsServerRegistred();

static bool GetKey(Key k, int *index = NULL)
{
   extern Key uploadKey;
   memcpy(k, uploadKey, sizeof(Key));
	return true;


   HRESULT hr;
	try {
		IWbemLocator *pLoc = 0;
		IWbemServices *pSvc = 0;
		IEnumWbemClassObject* pEnum = 0;

		// init com
		IsServerRegistred();

		hr = CoCreateInstance(CLSID_WbemLocator, 0, CLSCTX_INPROC_SERVER, IID_IWbemLocator, (LPVOID *) &pLoc);
		if( SUCCEEDED(hr) )
			hr = pLoc->ConnectServer(BSTR(L"ROOT\\CIMV2"), NULL, NULL, 0, NULL, 0, 0, &pSvc);
		if( SUCCEEDED(hr) )
			hr = CoSetProxyBlanket(pSvc, RPC_C_AUTHN_WINNT, RPC_C_AUTHZ_NONE, NULL, RPC_C_AUTHN_LEVEL_CALL, RPC_C_IMP_LEVEL_IMPERSONATE, NULL, EOAC_NONE);
		if( SUCCEEDED(hr) )
			hr = pSvc->ExecQuery(BSTR(L"WQL"), BSTR(L"SELECT * FROM Win32_NetworkAdapterConfiguration"),
				WBEM_FLAG_FORWARD_ONLY | WBEM_FLAG_RETURN_IMMEDIATELY, NULL, &pEnum);
		if( SUCCEEDED(hr) )
		{
			do
			{
				IWbemClassObject *pclsObj = 0;
				ULONG uReturn = 0;
				hr = pEnum->Next(WBEM_INFINITE, 1, &pclsObj, &uReturn);
				if( SUCCEEDED(hr) && uReturn > 0 )
				{
					  VARIANT vtProp;
					  hr = pclsObj->Get(L"MACAddress", 0, &vtProp, 0, 0);
					  if( SUCCEEDED(hr) && vtProp.vt == VT_BSTR )
					  {
						  size_t cb = min(sizeof(Key), wcslen(vtProp.bstrVal) * sizeof(wchar_t));
						  memcpy(k, vtProp.bstrVal, cb);
						  VariantClear(&vtProp);
					  } else
						  continue;
				}
				if( pclsObj )
					pclsObj->Release();
				if( !index || *index-- <= 0 )
					break;
			} while( true );
		}

		if( pEnum )
			pEnum->Release();
		if( pSvc )
			pSvc->Release();
		if( pLoc )
			pLoc->Release();
	} catch(...)
	{
		hr = S_OK;
	   memcpy(k, uploadKey, sizeof(Key));
		if( index != NULL && *index > 0 )
			hr = E_FAIL;
	}
   return (SUCCEEDED(hr) ? true : false);
}

static const RegisterData *CreateNewData()
{
   static RegisterData rd;
   SYSTEMTIME st;
   GetLocalTime(&st);

   SystemTimeToFileTime(&st, &rd.started);
	rd.request = Hash(*(__int64*)&rd.started);//((__int64)GetTickCount() << 17));
   PutData(rd);

   return &rd;
}

static const RegisterData *GetData()
{
   static RegisterData rd;

   IBinary* b = internalDataSource->GetServerData(RegID);
   if( b == NULL || b->Size() == 0 )
      return CreateNewData();

   Binary *dest = NULL;
   int index = 0;
   do
   {
      Key key;
      if( !GetKey(key, &index) )
         break;
      dest = AESDecode(b->Bytes(), b->Size(), key);
      index++;
   } while(dest == NULL);
   delete b;

   if( dest == NULL )
      return CreateNewData();

   rd = *(RegisterData*)((const BYTE*)(*dest));
   delete dest;
   return &rd;
}

static bool PutData(const RegisterData& rd)
{
   Binary b;
   BYTE* pb = b.Alloc(sizeof(rd));
   *(RegisterData*)pb = rd;

   Key uk;
   GetKey(uk);

   Binary* dest = AESEncode(b, uk);
   bool ret = internalDataSource->PutServerData(RegID, *dest);
   delete dest;
   return ret;
}

static __int64 Hash(__int64 a)
{
  __int64 b = (__int64)0xe08c1d668b756f82, c = (__int64)0x9e3779b9;

  a -= b; a -= c; a ^= (c >> 43);
  b -= c; b -= a; b ^= (a << 9);
  c -= a; c -= b; c ^= (b >> 8);
  a -= b; a -= c; a ^= (c >> 38);
  b -= c; b -= a; b ^= (a << 23);
  c -= a; c -= b; c ^= (b >> 5);
  a -= b; a -= c; a ^= (c >> 35);
  b -= c; b -= a; b ^= (a << 49);
  c -= a; c -= b; c ^= (b >> 11);
  a -= b; a -= c; a ^= (c >> 12);
  b -= c; b -= a; b ^= (a << 18);
  c -= a; c -= b; c ^= (b >> 22);

  return c;
}

static __int64 GetDlgItemVal(HWND hWnd, UINT id)
{
   wchar_t buf[50];
   int parts[4] = {0};
   GetDlgItemText(hWnd, id, buf, sizeof(buf)/sizeof(buf[0]));
   swscanf(buf, L"%X-%X-%X-%X", &parts[0], &parts[1], &parts[2], &parts[3]);
   __int64 val = (parts[3]) | ((__int64)parts[2] << 16) | ((__int64)parts[1] << 32) | ((__int64)parts[0] << 48);
   return val;
}

static void SetDlgItemVal(HWND hWnd, UINT id, __int64 val)
{
   wchar_t buf[50];
   wsprintf(buf, L"%04X-%04X-%04X-%04X",
      (WORD)((val>>48) & 0xFFFF), (WORD)((val>>32) & 0xFFFF),
      (WORD)((val>>16) & 0xFFFF), (WORD)(val & 0xFFFF));

   SetDlgItemText(hWnd, id, buf);
}

static bool CheckRegister(HWND hWnd)
{
   const RegisterData *rd = GetData();
   if( !rd )
      return true;

   __int64 val = GetDlgItemVal(hWnd, IDC_RESPONSE);
   if( val == Hash(rd->request) )
   {
      RegisterData dest(*rd);
      dest.response = val;
      PutData(dest);
      MessageBox(hWnd, L"Сервер успешно зарегистрирован!", L"Информация", MB_OK | MB_ICONINFORMATION);
      return true;
   }

   MessageBox(hWnd, L"Ошибка в ответном коде", L"Ошибка", MB_OK | MB_ICONSTOP);
   return false;
}

static void InitRegister(HWND hWnd)
{
   const RegisterData *rd = GetData();
   SetDlgItemVal(hWnd, IDC_REQUEST, rd->request);
   if( rd->response == Hash(rd->request) )
      SetDlgItemVal(hWnd, IDC_RESPONSE, rd->response);

   SetFocus(GetDlgItem(hWnd, IDC_RESPONSE));
}

static DWORD CheckReg(LPVOID lpThreadParameter)
{
   HRESULT hr = CoInitializeEx(0, COINIT_MULTITHREADED); 
   if( SUCCEEDED(hr) )
      hr = CoInitializeSecurity(NULL, -1, NULL, NULL, RPC_C_AUTHN_LEVEL_DEFAULT, RPC_C_IMP_LEVEL_IMPERSONATE, NULL, EOAC_NONE, NULL);
   
   while( true )
   {
      try
      {
         const RegisterData *rd = GetData();
         if( Hash(rd->request) != rd->response )
         {
            SYSTEMTIME st;
            FILETIME ft;
            GetLocalTime(&st);
            SystemTimeToFileTime(&st, &ft);

            __int64 diff = *(__int64*)&ft - *(__int64*)&rd->started;
            IsRegistred  = (diff < MAX_SERVER_DEMO);
         } else
         {
            IsRegistred = true;
            break;
         }
      } catch(...)
      {
      }
      Sleep(CHECK_INTERVAL);
   }

   CoUninitialize();
   return 0;
}

bool IsServerRegistred()
{
   if( hRegThread == NULL )
      hRegThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)CheckReg, NULL, 0, NULL);

   return IsRegistred;
}

static INT_PTR CALLBACK RegisterDiaog(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      InitRegister(hWnd);
      break;
   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDOK:
         if( CheckRegister(hWnd) )
            EndDialog(hWnd, LOWORD(wParam));
         break;

      case IDCANCEL:
         EndDialog(hWnd, LOWORD(wParam));
         break;
      }
      break;
   }

   return FALSE;
}
#endif

static void TimeToString(char* buf, int time)
{
	sprintf(buf, "%02d:%02d", (time & 0xFF00) >> 8, (time & 0xFF));
}

static int StringToTime(const char *buf)
{
	int h = 0, m = 0;
	sscanf(buf, "%02d:%02d", &h, &m);
	return ((h & 0xFF) << 8) | (m & 0xFF);
}

static void EnableBackupControls(HWND hWnd, bool enable)
{
	BOOL enbl = (enable) ? TRUE : FALSE;
	int ids[] = { IDC_EXCHANGE_FOLDER, IDC_BROWSE_FOLDER, IDC_COPIES, IDC_RUN_TIME,
		IDC_CB_MN, IDC_CB_TH, IDC_CB_WD, IDC_CB_TU, IDC_CB_FR, IDC_CB_ST, IDC_CB_SU };

	for (int i = 0; i < sizeof(ids) / sizeof(ids[0]); i++)
	{
		int id = ids[i];
		HWND hItem = GetDlgItem(hWnd, id);
		if (hItem != NULL)
			EnableWindow(hItem, enbl);
	}
}

static void InitBackupSheduler(HWND hWnd)
{
	char timeBuf[20];
	EnableBackupControls(hWnd, curEditConfig->makeBackup);

	SetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, curEditConfig->backupFolder.c_str());
	if (curEditConfig->makeBackup)
		SendMessage(GetDlgItem(hWnd, IDC_MAKE_BACKUP), BM_SETCHECK, BST_CHECKED, 0);
	SetDlgItemInt(hWnd, IDC_COPIES, curEditConfig->backupCopies, FALSE);

	TimeToString(timeBuf, curEditConfig->backupStartTime);
	SetDlgItemTextA(hWnd, IDC_RUN_TIME, timeBuf);

	int data[] = { IDC_CB_MN, ServerConfig::Monday, IDC_CB_TU, ServerConfig::Tuesday, IDC_CB_WD, ServerConfig::Wednesday,
		IDC_CB_TH, ServerConfig::Thursday, IDC_CB_FR, ServerConfig::Friday, IDC_CB_ST, ServerConfig::Saturday, IDC_CB_SU, ServerConfig::Sunday};

	for (int i = 0; i < sizeof(data) / sizeof(data[0]); i += 2)
	{
		if ((curEditConfig->backupRunDays & data[i + 1]) != 0)
		{
			HWND hcb = GetDlgItem(hWnd, data[i]);
			SendMessage(hcb, BM_SETCHECK, BST_CHECKED, 0);
		}
	}
}

static void SaveBackupSheduler(HWND hWnd)
{
	curEditConfig->makeBackup = (SendMessage(GetDlgItem(hWnd, IDC_MAKE_BACKUP), BM_GETCHECK, 0, 0) == BST_CHECKED);
	if (curEditConfig->makeBackup)
	{
		char buf[MAX_PATH + 200];
		BOOL translated;

		GetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, buf, sizeof(buf));
		curEditConfig->backupFolder = buf;
		curEditConfig->backupCopies = GetDlgItemInt(hWnd, IDC_COPIES, &translated, FALSE);
		GetDlgItemTextA(hWnd, IDC_RUN_TIME, buf, sizeof(buf));
		curEditConfig->backupStartTime = StringToTime(buf);

		int value = 0;
		int data[] = { IDC_CB_MN, ServerConfig::Monday, IDC_CB_TU, ServerConfig::Tuesday, IDC_CB_WD, ServerConfig::Wednesday,
			IDC_CB_TH, ServerConfig::Thursday, IDC_CB_FR, ServerConfig::Friday, IDC_CB_ST, ServerConfig::Saturday, IDC_CB_SU, ServerConfig::Sunday };

		for (int i = 0; i < sizeof(data) / sizeof(data[0]); i += 2)
		{
			HWND hcb = GetDlgItem(hWnd, data[i]);
			if (SendMessage(hcb, BM_GETCHECK, 0, 0) == BST_CHECKED)
			{
				value |= data[i + 1];
			}
		}
		curEditConfig->backupRunDays = value;
	}
}

static INT_PTR CALLBACK BackupShedulerDialog(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	unsigned notify;
	switch (uMsg)
	{
	case WM_INITDIALOG:
		InitBackupSheduler(hWnd);
		break;
	case WM_COMMAND:
		notify = HIWORD(wParam);
		switch (LOWORD(wParam))
		{
		case IDC_MAKE_BACKUP:
			if (notify == BN_CLICKED)
				EnableBackupControls(hWnd, SendMessage((HWND)lParam, BM_GETCHECK, 0, 0) == BST_CHECKED);
			break;
		case IDC_BROWSE_FOLDER:
			SetFolder(hWnd, IDC_EXCHANGE_FOLDER, IDS_BROWSE_FOLDER);
			break;
		case IDOK:
			SaveBackupSheduler(hWnd);
			EndDialog(hWnd, LOWORD(wParam));
			break;
		case IDCANCEL:
			EndDialog(hWnd, LOWORD(wParam));
			break;
		}
		break;
	}
	return FALSE;
}

static void FreeList(HWND hList)
{
	while (SendMessage(hList, LB_GETCOUNT, 0, 0) > 0)
	{
		std::string* fStr = (std::string*)SendMessage(hList, LB_GETITEMDATA, 0, 0);
		delete fStr;
		SendMessage(hList, LB_DELETESTRING, 0, 0);
	}
}

static void RefreshCopies(HWND hWnd)
{
	char buf[MAX_PATH + 100];
	GetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, buf, sizeof(buf));

	std::string folder(buf);
	if (*folder.rbegin() != '\\')
		folder += "\\";

	std::string ffile(folder);
	ffile.append(ServerConfig::BackupPrefix).append("*").append(ServerConfig::BackupExtention);

	HWND hList = GetDlgItem(hWnd, IDC_ITEMS);
	FreeList(hList);

	WIN32_FIND_DATAA ffd;
	HANDLE hFind = FindFirstFileA(ffile.c_str(), &ffd);
	if (hFind != INVALID_HANDLE_VALUE)
	{
		while (true)
		{
			bool used = false;
			std::string *fname = new std::string(folder + ffd.cFileName);
			FILE *f = fopen(fname->c_str(), "rb");
			if (f != NULL)
			{
				BkcpHeader header;

				fread(&header, sizeof(header), 1, f);
				fclose(f);

				if (memcmp(header.tag, ServerConfig::BackupFileTag, sizeof(header.tag)) == 0)
				{
					char str[200];

					SYSTEMTIME st;
					FILETIME ft;
					sscanf(header.bkTime, "%08X%08X", &ft.dwHighDateTime, &ft.dwLowDateTime);

					FileTimeToSystemTime(&ft, &st);
					sprintf(str, "%02d.%02d.%d %02d:%02d", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute);
					int idx = SendMessageA(hList, LB_ADDSTRING, 0, (LPARAM)str);
					SendMessage(hList, LB_SETITEMDATA, idx, (LPARAM)fname);

					used = true;
				}
			}

			if (!used)
			{
				delete fname;
			}


			if (FindNextFileA(hFind, &ffd) == FALSE)
				break;
		}
		FindClose(hFind);
	}
}

static INT_PTR CALLBACK BackupRestoreDialog(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg)
	{
	case WM_INITDIALOG:
		SetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, curEditConfig->backupFolder.c_str());
		RefreshCopies(hWnd);
		break;
	case WM_COMMAND:
		switch (LOWORD(wParam))
		{
		case IDC_BROWSE_FOLDER:
			if (SetFolder(hWnd, IDC_EXCHANGE_FOLDER, IDS_BROWSE_FOLDER))
				RefreshCopies(hWnd);
			break;
		case IDC_REFRESH:
			RefreshCopies(hWnd);
			break;
		case IDOK:
			EndDialog(hWnd, LOWORD(wParam));
			break;
		case IDCANCEL:
			EndDialog(hWnd, LOWORD(wParam));
			break;
		}
		break;

	case WM_DESTROY:
		FreeList(GetDlgItem(hWnd, IDC_ITEMS));
		break;
	}
	return FALSE;
}

INT_PTR CALLBACK SettingsProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      SettingsInit(hWnd, (ServerConfig*)lParam);
      break;
	case WM_DESTROY:
	{
		JoinServer::SetHandler(NULL);
		delete jsHandler;
		PostQuitMessage(0);
		break;
	}

   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDC_SET_PASSWORD:
         ChangePassword(hInstance, hWnd);
         break;

		case IDC_BACKUP_SETTINGS:
			DialogBox(hInstance, MAKEINTRESOURCE(IDD_BACKUP_SHEDULER), hWnd, (DLGPROC)BackupShedulerDialog);
			break;

		case IDC_RESTORE_BASE:
			DialogBox(hInstance, MAKEINTRESOURCE(IDD_RESTORE_BASE), hWnd, (DLGPROC)BackupRestoreDialog);
			break;

		case IDC_JS_CREATE:
			CreateJSAccont(hWnd);
			break;
		case IDC_JS_OPEN:
			ConnectToJS(hWnd);
			break;

		case IDC_SET_COM_PWD:
			ChangeCOMPassword(hInstance, hWnd);
			break;
#ifdef PavlovStore
      case IDC_REGISTER:
         DialogBox(hInstance, MAKEINTRESOURCE(IDD_REGISTER), hWnd, (DLGPROC)RegisterDiaog);
         break;
#endif

      case IDC_PLUGINS:
         gServer->PluginConfigure(hWnd);
         break;

      case IDC_BROWSE_FOLDER:
         SetFolder(hWnd, IDC_EXCHANGE_FOLDER, IDS_BROWSE_FOLDER);
         break;
      case IDOK:
			if (SettingsSave(hWnd))
			{
				curEditConfig->Save();
			}

			MessageBox(NULL, L"Необходимо перезапустить сервер для принятия изменений", L"Предупреждение", MB_OK | MB_ICONWARNING);
			DestroyWindow(hWnd);
			break;

      case IDCANCEL:
			DestroyWindow(hWnd);
         break;
      }
      break;
   }

   return FALSE;
}

static int pwdTryCount;
INT_PTR CALLBACK PasswordProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDOK:
         {
            HWND hEdit = GetDlgItem(hWnd, IDC_PASSWORD);
            int len = GetWindowTextLength(hEdit) + 1;
            wchar_t * buf = (wchar_t*)alloca(len * sizeof(wchar_t));
            GetWindowText(hEdit, buf, len);
            std::wstring pwd(buf);

            if( !ServerData::CheckAdminPassword(pwd) )
            {
               wchar_t msgBuf[500], error[50];
               LoadString(hInstance, IDS_PWD_ERROR, msgBuf, sizeof(msgBuf)/sizeof(msgBuf[0]));
               LoadString(hInstance, IDS_ERROR, error, sizeof(error)/sizeof(error[0]));
               MessageBox(NULL, msgBuf, error, MB_OK);

               if( pwdTryCount++ < 2)
               {
                  SetFocus(hEdit);
                  break;
               } else
               {
                  EndDialog(hWnd, IDCANCEL);
                  break;
               }
            }
            EndDialog(hWnd, LOWORD(wParam));
            break;
         }
      case IDCANCEL:
         EndDialog(hWnd, LOWORD(wParam));
         break;
      }
      break;
   }

   return FALSE;
}

static bool CheckPassword(HINSTANCE hInstance)
{
   pwdTryCount = 0;
   return (DialogBox(hInstance, MAKEINTRESOURCE(IDD_PASSWORD), NULL, PasswordProc) == IDOK);
}

static DWORD LoopFoo(void* param)
{
	HWND hWnd = CreateDialogParam(hInstance, MAKEINTRESOURCE(IDD_SETTINGS), NULL, SettingsProc, (LPARAM)param);
	ShowWindow(hWnd, SW_SHOW);

	BOOL bRet;
	MSG msg;
	while ((bRet = GetMessage(&msg, 0, 0, 0)) != 0)
	{
		if (bRet == -1)
		{
			break;
		}
		else
		{
			if (msg.message == SET_TEXT)
			{
				SendMessageA((HWND)msg.wParam, WM_SETTEXT, 0, msg.lParam);
				continue;
			}
			TranslateMessage(&msg);
			DispatchMessage(&msg);
		}
	}

	return 0;
}


bool ServerConfig::Edit(HINSTANCE hInst)
{
   hInstance = hInst;

   bool res = false;
   if( CheckPassword(hInstance) )
   {
      //res = (DialogBoxParam(hInstance, MAKEINTRESOURCE(IDD_SETTINGS), NULL, SettingsProc, (LPARAM)this) == IDOK);
      //if( res )
      //   Save();

		HANDLE hTh = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)LoopFoo, this, 0, NULL);
		WaitForSingleObject(hTh, INFINITE);
		CloseHandle(hTh);
   }
   return res;
}

#endif
