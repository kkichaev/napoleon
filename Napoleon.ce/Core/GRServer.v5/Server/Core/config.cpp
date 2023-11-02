/*
 * Copyright (C), 2009 - 2022, Denis Mosaigin
 *
 *
 * ert   02/05/2009   creating
 */
#include "stdafx.h"
#include "server.h"
#include "srvdata.h"

#include <fstream>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#ifdef UNIX
#else
#include "resource.h"
#include <shlobj.h> // set default path for exchnagefolder
#endif
#include "srvutility.h"
#include "grftrs.h"
#include <iomanip>

using namespace GRServer;
using namespace std;

const char CONFIG_FILE_PARAM[] = "config-file";
const char DEFS_FILE_PARAM[] = "serverDefs";
const char ADD_DEFS_FILE_PARAM[] = "addDefs";
// const char UPDATE_FOLDER[] = "updateFolder";
const char EXCHANGE_FOLDER[] = "exchangeFolder";
const char SERVER_BASE[] = "serverBase";
// const char PLUGINS_FOLDER[] = "pluginsFolder";
const char FEATURE_TAG[] = "featuresFile";
const char SERVER_KEY_TAG[] = "serverKey";
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
const char BLOCKED[] = "blocked";

const char DEFAULT_FEATURES_FILE[] = "GRServer.ftr";

const char DEFAULT_CONFIG_FILE[] = "GRServer.ini";
const char DEFS_FILE[] = "serverDefs.xml";
const DWORD DEFAULT_PORT = 8888;

const char CONFIG_SUBKEY[] = "SOFTWARE\\Ert\\Napoleon";
const char FOLDER_VALUE_NAME[] = "ExchangeFolder";
const char PORT_VALUE_NAME[] = "Port";
const char HTTP_SOCKET_NAME[] = "httpSocket";

const char DEBUG_KEY_NAME[] = "Debug";
const char LOG_SIZE_KEY_NAME[] = "logSize";

const char CONC_STMT_STR[] = "ConcurentConnections";
const char SEND_LIMIT[] = "SendObjectLimit";
const char SESSION_LIMIT[] = "SessionMemoryLimit";
const char MEMORY_LIMIT[] = "MemoryLimit";
const char UPLOAD_LIMIT[] = "UploadLimit";

bool ServerConfig::ftrLoaded = false;

extern "C" int DBF_CODE_PAGE = CP_OEMCP;

struct BkcpHeader
{
	char tag[5];
	char bkTime[16];
};

const char *ServerConfig::BackupExtention = ".bkp";
const char *ServerConfig::BackupPrefix = "grs";
const char *ServerConfig::BackupFileTag = "GRBKP";
const char *ServerConfig::MakeBackupName(std::string *outName, const std::string &folder, FILETIME &ft)
{
	char buf[30];
	sprintf(buf, "%08X%08X", ft.dwHighDateTime, ft.dwLowDateTime);

	outName->append(folder).append(BackupPrefix).append(buf).append(BackupExtention);
	return outName->c_str();
}

#ifdef UNIX
#include "AES.h"
Key uploadKey;
#endif

//
// --------------------------- ServerConfig --------------------------
//
ServerConfig::ServerConfig()
{
	SetDefault();
}

ServerConfig::ServerConfig(const ServerConfig &src)
{
	this->operator=(src);
}

ServerConfig &ServerConfig::operator=(const ServerConfig &src)
{
	if (this != &src)
	{
		configFile = src.configFile;
		exchangeFolder = src.exchangeFolder;
		port = src.port;
		debugLevel = src.debugLevel;

		defsFile = src.defsFile;
		serverBase = src.serverBase;
		addDefsFile = src.addDefsFile;
		featuresFile = src.featuresFile;

		configFolder = src.configFolder;
		progFolder = src.progFolder;

		imageFolder = src.imageFolder;
		jsLogin = src.jsLogin;
		jsPassword = src.jsPassword;
		useGRJS = src.useGRJS;

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

		sendObjectSizeLimit = src.sendObjectSizeLimit;
		sessionMemoryLimit = src.sessionMemoryLimit;
		memoryLimit = src.memoryLimit;

		uploadLimit = src.uploadLimit;

		blocked = src.blocked;
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
	openConsole = false;

	sendObjectSizeLimit = 50 * 1024 * 1024;
	sessionMemoryLimit = 50; // *1024 * 1024;
	memoryLimit = 1024;		 // *1024 * 1024;
	uploadLimit = 200; // in kBytes

#ifdef UNIX
	imageFolder = "./";
	backupFolder = "./";
#else
	imageFolder = ".\\";
	backupFolder = ".\\";
#endif
}

void ServerConfig::ParseCmdLine(DWORD argc, const char *argv[])
{
	progFolder = argv[0];
	size_t pos = progFolder.find_last_of(DIR_SEP);
	progFolder = progFolder.substr(0, pos + 1);

	for (DWORD i = 1; i < argc; i++)
	{
		const char *key = argv[i];
		if (*key == '-' && key[1] == '-' && i < argc - 1)
		{
			std::string val(argv[i + 1]);
			if (*val.begin() == L'"')
				val = val.substr(1, val.size() - 2);
			if (SetValue(key + 2, val))
				i++;
		}
	}
	pos = configFile.find_last_of(DIR_SEP);
	if (pos != string::npos)
		configFolder = configFile.substr(0, pos + 1);
	else
		configFile = progFolder;
}

static int GetCodePage(const std::string &value)
{
	return (_stricmp(value.c_str(), "ANSI") == 0) ? CP_ACP : CP_OEMCP;
}

static bool ToBool(const std::string &value)
{
	return (_stricmp(value.c_str(), "true") == 0);
}

static char Hex(char val)
{
	if ((signed)val < 0)
		return 0;

	val = toupper(val);
	return val >= '0' && val <= '9' ? val - '0' : 
			val >= 'A' && val <= 'F' ? (val - 'A') + 0xA : 
			0;
}

#ifdef UNIX
static void SetupKey(Key uploadKey, const std::string &value)
{
	int idx = 0;
	std::string::const_iterator i = value.begin();
	for (; i != value.end() && idx < sizeof(Key);)
	{
		char val = (Hex(*i++) << 4);
		if (i == value.end())
			break;
		val |= Hex(*i++);
		uploadKey[idx++] = val;
	}
}
#endif

bool ServerConfig::SetValue(const std::string &key, const std::string &value)
{
	bool res = true;

	const char *k = key.c_str();
	char *ep;

	if (strcmp(k, CONFIG_FILE_PARAM) == 0)
		MakeFullFileName(&configFile, value, progFolder);
	else if (strcmp(k, DEFS_FILE_PARAM) == 0)
		defsFile = value;
	else if (strcmp(k, HTTP_SOCKET_NAME) == 0)
		webSocket = value;
	else if (strcmp(k, ADD_DEFS_FILE_PARAM) == 0)
		addDefsFile.push_back(value);
	else if (strcmp(k, SERVER_BASE) == 0)
		MakeFullFileName(&serverBase, value, configFolder);
	else if (strcmp(k, FEATURE_TAG) == 0)
		MakeFullFileName(&featuresFile, value, progFolder);
	else if (strcmp(k, PORT_VALUE_NAME) == 0)
		port = atoi(value.c_str());
	// else if( strcmp(k, UPDATE_FOLDER) == 0 ) SetFolder(&updateFolder, value);
	// else if( strcmp(k, PLUGINS_FOLDER) == 0 ) SetFolder(&pluginsFolder, value);
	else if (strcmp(k, EXCHANGE_FOLDER) == 0)
		exchangeFolder = value;
	else if (strcmp(k, IMAGE_FOLDER) == 0)
		imageFolder = value;
	else if (strcmp(k, BACKUP_FOLDER) == 0)
		backupFolder = value;
	else if (strcmp(k, DBF_CP) == 0)
		DBF_CODE_PAGE = GetCodePage(value);
	else if (strcmp(k, DEBUG_KEY_NAME) == 0)
		SetDebugLevel(value);
	else if (strcmp(k, LOG_SIZE_KEY_NAME) == 0)
		logLength = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, CONC_STMT_STR) == 0)
		concurentConnections = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, MAKE_DUMP) == 0)
		makeDumpOnException = ToBool(value);
	else if (strcmp(k, MAKE_BACKUP) == 0)
		makeBackup = ToBool(value);
	else if (strcmp(k, BACKUP_COPIES) == 0)
		backupCopies = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, BACKUP_RUN_DAYS) == 0)
		backupRunDays = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, BACKUP_START_TIME) == 0)
		backupStartTime = strtoul(value.c_str(), &ep, 10);
	else if (strcmp(k, NO_CHECK_FORMAT) == 0)
		noCheckFormat = ToBool(value);
	else if (strcmp(k, JS_LOGIN) == 0)
		jsLogin = value;
	else if (strcmp(k, JS_PASSWORD) == 0)
		jsPassword = value;
	else if (strcmp(k, USE_GRJS) == 0)
		useGRJS = ToBool(value);
	else if (strcmp(k, SEND_LIMIT) == 0)
		sendObjectSizeLimit = strtoul(value.c_str(), &ep, 10) * 1024 * 1024;
	else if (strcmp(k, SESSION_LIMIT) == 0)
		sessionMemoryLimit = ((size_t)strtoul(value.c_str(), &ep, 10)); // *1024 * 1024;
	else if (strcmp(k, MEMORY_LIMIT) == 0)
		memoryLimit = ((size_t)strtoul(value.c_str(), &ep, 10)); // * 1024 * 1024;
	else if (strcmp(k, UPLOAD_LIMIT) == 0)
		uploadLimit = ((size_t)strtoul(value.c_str(), &ep, 10)); // * 1024 * 1024;
	else if (strcmp(k, BLOCKED) == 0) 
	{
		std::stringstream ss(value);
		std::string str;
		while(getline(ss, str, ','))
		{
			blocked.insert(str);
		}
	}
#ifdef UNIX
	else if (strcmp(k, SERVER_KEY_TAG) == 0)
		SetupKey(uploadKey, value);
#endif
	else
	{
		items[key].push_back(value);
	}

	return res;
}

void ServerConfig::SetDebugLevel(const std::string &value)
{
	const char *str = value.c_str();
	if (_stricmp(str, "SHORT") == 0)
		debugLevel = IErrorLogger::Short;
	else if (_stricmp(str, "FULL") == 0)
		debugLevel = IErrorLogger::Full;
	else if (_stricmp(str, "CONSOLE") == 0)
	{
		debugLevel = IErrorLogger::Full;
		openConsole = true;
	}
}

inline void CheckFolder(string& f) {
	if(*f.rend() != DIR_SEP) f.append(1, DIR_SEP);
}

bool ServerConfig::Load()
{
	if (configFile.empty())
		configFile = DEFAULT_CONFIG_FILE;
	MakeFullFileName(&configFile, configFile, progFolder);

	size_t pos = configFile.find_last_of(DIR_SEP);
	configFolder = configFile.substr(0, pos + 1);

	fstream f(configFile, std::ios_base::in);
	if (f.fail())
	{
		gServer->AddError(false, "Can't open config file '%s'", configFile.c_str());
		return false;
	}

	std::string line;
	while (std::getline(f, line))
	{
		size_t pos = line.find('=');
		if (pos != std::string::npos)
		{
			std::string key = line.substr(0, pos);
			std::string value = line.substr(pos + 1);
			SetValue(trim(key), trim(value));
		}
	}
	
	MakeFullFileName(&exchangeFolder, exchangeFolder, configFolder);
	CheckFolder(exchangeFolder);

	if (imageFolder.empty())
		imageFolder = exchangeFolder;
	else
		MakeFullFileName(&imageFolder, imageFolder, configFolder);
	CheckFolder(imageFolder);

	if (backupFolder.empty())
		backupFolder = exchangeFolder;
	else
		MakeFullFileName(&backupFolder, backupFolder, configFolder);
	CheckFolder(backupFolder);

	return true;
}

void ServerConfig::Save()
{
	if (serverBase.empty())
		return;

	// std::string fileName;
	// FullFileName(&fileName, configFile.c_str());
	FILE *wr = fopen(configFile.c_str(), "wt");
	if (wr)
	{
		fprintf(wr, "%s = %s\n", DEFS_FILE_PARAM, defsFile.c_str());
		if (addDefsFile.size())
		{
			std::vector<std::string>::const_iterator i = addDefsFile.begin();
			for (; i != addDefsFile.end(); i++)
				fprintf(wr, "%s = %s\n", ADD_DEFS_FILE_PARAM, i->c_str());
		}
		// if( addDefsFile.empty() == false )
		//    fprintf(wr, "%s = %s\n", ADD_DEFS_FILE_PARAM, addDefsFile.c_str());
		fprintf(wr, "%s = %s\n", SERVER_BASE, serverBase.c_str());
		// fprintf(wr, "%s = %s\n", UPDATE_FOLDER, updateFolder.c_str());
		fprintf(wr, "%s = %s\n", EXCHANGE_FOLDER, exchangeFolder.c_str());
		// fprintf(wr, "%s = %s\n", PLUGINS_FOLDER, pluginsFolder.c_str());
		fprintf(wr, "%s = %d\n", PORT_VALUE_NAME, port);
		fprintf(wr, "%s = %s\n", FEATURE_TAG, featuresFile.c_str());
		fprintf(wr, "%s = %s\n", IMAGE_FOLDER, imageFolder.c_str());

		if (DBF_CODE_PAGE == CP_ACP)
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

		if (sendObjectSizeLimit)
			fprintf(wr, "%s = %u\n", SEND_LIMIT, (unsigned)sendObjectSizeLimit / (1024 * 1024));

		if (sessionMemoryLimit)
			fprintf(wr, "%s = %u\n", SESSION_LIMIT, (unsigned)sessionMemoryLimit);

		if (memoryLimit)
			fprintf(wr, "%s = %u\n", MEMORY_LIMIT, (unsigned)memoryLimit);

		if (debugLevel != IErrorLogger::None)
			fprintf(wr, "%s = %s\n", DEBUG_KEY_NAME, (debugLevel == IErrorLogger::Short) ? "short" : "full");

		std::map<std::string, Values>::const_iterator i = items.begin();
		for (; i != items.end(); i++)
		{
			Values::const_iterator ci = i->second.begin();
			for (; ci != i->second.end(); ci++)
				fprintf(wr, "%s = %s\n", i->first.c_str(), ci->c_str());
		}

		fclose(wr);
	}
}

bool ServerConfig::HaveFeature(const std::wstring &ftrExpr) const
{
	if (!ftrLoaded)
	{
		ftrLoaded = true;
		std::string ff;
		MakeFullFileName(&ff, featuresFile, configFolder);
		LoadFeatures(ff);
	}

	return ::HaveFeature(ftrExpr);
}

#ifdef UNIX
#else
static ServerConfig *curEditConfig;
static ServerRunMode runMode;
static HINSTANCE hInstance;
const UINT SET_TEXT = WM_USER + 10;

#ifdef JOIN_SERVER
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
static JSHandler *jsHandler;

const char *JSStatusToString(JoinServer::Status status)
{
	switch (status)
	{
	case JoinServer::None:
		return "";
	case JoinServer::Connecting:
		return "����������� � JoinServer";
	case JoinServer::Working:
		return "��������� �  JoinServer";
	case JoinServer::Error:
		return "������ ��� ����������� � JoinServer";
	}
	return "";
}

#ifndef PROJECT_NAME
#define PROJECT_NAME L"Test"
#endif

static void MakeAddrText(std::string *tstr, DWORD addr)
{
	char addrBuf[100];
	*addrBuf = 0;
	if (addr != 0)
		wsprintfA(addrBuf, "����� �������: GRJS.%d.%d.%d.%d", (addr >> 24) & 0xFF, (addr >> 16) & 0xFF, (addr >> 8) & 0xFF, addr & 0xFF);
	tstr->assign(addrBuf);
}

static void SetJSAddress(HWND hWnd, DWORD addr)
{
	std::string addrBuf;
	MakeAddrText(&addrBuf, addr);
	SetDlgItemTextA(hWnd, IDC_JS_ADDRESS, addrBuf.c_str());
}

static const char *JSErrorToText(const std::string &error)
{
	if (error.compare("already_registred") == 0)
	{
		return "����� ����� ��� ����������";
	}

	if (error.compare("no_login_password") == 0)
	{
		return "������������ �� ������";
	}

	if (error.compare("no_id") == 0)
	{
		return "������ �� ���������������";
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

static bool GetAccountData(std::string *login, std::string *pwd, HWND hWnd)
{
	USES_CONVERSION;

	wchar_t buf[100];

	if (GetDlgItemText(hWnd, IDC_JS_LOGIN, buf, sizeof(buf) / sizeof(wchar_t) - 1) > 0)
		(*login) = W2A_CP(buf, CP_UTF8);
	else
	{
		MessageBox(hWnd, L"�� �������� �����", L"������", MB_OK | MB_ICONSTOP);
		return false;
	}

	if (GetDlgItemText(hWnd, IDC_JS_PWD, buf, sizeof(buf) / sizeof(wchar_t) - 1) > 0)
		(*pwd) = W2A_CP(buf, CP_UTF8);
	else
	{
		MessageBox(hWnd, L"�� �������� ������", L"������", MB_OK | MB_ICONSTOP);
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
	JoinServer::Start(login, pwd, W2A_CP(PROJECT_NAME, CP_UTF8), curEditConfig->port, true);
	SetDlgItemTextA(hWnd, IDC_JS_STATUS, "");
	SetJSAddress(hWnd, 0);
}

static void CreateJSAccont(HWND hWnd)
{
	std::string login, pwd, error;
	if (!GetAccountData(&login, &pwd, hWnd))
		return;

	USES_CONVERSION;
	DWORD res = JoinServer::Register(&error, login, pwd, W2A_CP(PROJECT_NAME, CP_UTF8));
	SetJSAddress(hWnd, res);
	if (res == 0)
	{
		SetDlgItemTextA(hWnd, IDC_JS_STATUS, "������ �����������");
		const char *errStr = JSErrorToText(error);
		SetDlgItemTextA(hWnd, IDC_JS_ADDRESS, errStr);
	}
	else
	{
		ConnectToJS(hWnd);
		MessageBox(hWnd, L"������ ���������������", L"����������", MB_OK | MB_ICONINFORMATION);
	}
}
#endif

static void SettingsInit(HWND hWnd, ServerConfig *config)
{
	curEditConfig = config;

	wchar_t buf[50];

#ifdef JOIN_SERVER
	jsHandler = new JSHandler(hWnd);
	JoinServer::SetHandler(jsHandler);
#endif

	HWND runType = GetDlgItem(hWnd, IDC_RUN_TYPE);
	LoadString(hInstance, IDS_RUN_UNDEF, buf, sizeof(buf) / sizeof(buf[0]));
	SendMessage(runType, CB_ADDSTRING, 0, (LPARAM)(buf));
	LoadString(hInstance, IDS_RUN_TRAY, buf, sizeof(buf) / sizeof(buf[0]));
	SendMessage(runType, CB_ADDSTRING, 0, (LPARAM)(buf));
	LoadString(hInstance, IDS_RUN_SERVICE, buf, sizeof(buf) / sizeof(buf[0]));
	SendMessage(runType, CB_ADDSTRING, 0, (LPARAM)(buf));
	runMode = gServer->RunMode();
	SendMessage(runType, CB_SETCURSEL, (runMode == srmService) ? 2 : (runMode == srmTray) ? 1
																						  : 0,
				0);

	SetDlgItemInt(hWnd, IDC_PORT, curEditConfig->port, FALSE);
	SetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, curEditConfig->ExchFolderInt().c_str());

#ifdef JOIN_SERVER
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
			const char *errStr = JSErrorToText(JoinServer::GetError());
			SetDlgItemTextA(hWnd, IDC_JS_STATUS, errStr);
		}
	}
#endif
}

static bool SettingsSave(HWND hWnd)
{
	bool ret = true;
	char buf[MAX_PATH];
	GetDlgItemTextA(hWnd, IDC_EXCHANGE_FOLDER, buf, sizeof(buf));

	curEditConfig->ExchFolderInt().assign(buf);
	if (*curEditConfig->ExchFolderInt().rbegin() != '\\')
		curEditConfig->ExchFolderInt() += "\\";
	curEditConfig->port = GetDlgItemInt(hWnd, IDC_PORT, NULL, FALSE);

#ifdef JOIN_SERVER
	if (GetDlgItemTextA(hWnd, IDC_JS_LOGIN, buf, sizeof(buf) - 1) > 0)
		curEditConfig->jsLogin = buf;
	if (GetDlgItemTextA(hWnd, IDC_JS_PWD, buf, sizeof(buf) - 1) > 0)
		curEditConfig->jsPassword = buf;
	curEditConfig->useGRJS = ((SendMessage(GetDlgItem(hWnd, IDC_USE_GRJS), BM_GETSTATE, 0, 0) == BST_CHECKED) && !curEditConfig->jsLogin.empty() && !curEditConfig->jsPassword.empty());
#endif

	LRESULT cs = SendMessage(GetDlgItem(hWnd, IDC_RUN_TYPE), CB_GETCURSEL, 0, 0);
	ServerRunMode curMode = (cs == 1) ? srmTray : (cs == 2) ? srmService
															: srmUndef;
	if (curMode != runMode)
	{
		if (!gServer->Install(curMode, false))
		{
			char *buf;
			wchar_t *bufW;
			FormatMessageA(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
						   NULL, GetLastError(), 0, (LPSTR)&buf, 0, NULL);

			gServer->AddError(false, buf);

			size_t len = strlen(buf) + 1;
			bufW = (wchar_t *)alloca(sizeof(wchar_t) * len);
			MultiByteToWideChar(CP_ACP, 0, buf, (int)len, bufW, (int)len);
			LocalFree(buf);

			ret = false;

			std::wstring msg = L"������ ��� ����������� �������:\n";
			msg += bufW;
			MessageBox(NULL, msg.c_str(), L"������", MB_OK);
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

	if (len1 == len2)
	{
		wchar_t *buf1 = (wchar_t *)alloca(len1 * sizeof(wchar_t));
		wchar_t *buf2 = (wchar_t *)alloca(len1 * sizeof(wchar_t));

		GetWindowText(hEdit1, buf1, len1);
		GetWindowText(hEdit2, buf2, len1);

		if (wcscmp(buf1, buf2) == 0)
		{
			password->assign(buf1);
			ret = true;
		}
	}

	return ret;
}

INT_PTR CALLBACK SetPasswordProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
	switch (uMsg)
	{
	case WM_INITDIALOG:
#ifdef X64
		SetWindowLong(hWnd, GWLP_USERDATA, (LONG)lParam);
#else
		SetWindowLong(hWnd, GWL_USERDATA, lParam);
#endif
		break;
	case WM_COMMAND:
		switch (LOWORD(wParam))
		{
		case IDOK:
		{
			std::wstring password;
			if (IsPasswordEqual(hWnd, &password))
			{
#ifdef X64
				DWORD wndVal = GetWindowLong(hWnd, GWLP_USERDATA);
#else
				DWORD wndVal = GetWindowLong(hWnd, GWL_USERDATA);
#endif
				if (wndVal == 0)
					ServerData::SetAdminPassword(password);
				else if (wndVal == 1)
					ServerData::SetCOMPassword(password);

				EndDialog(hWnd, IDOK);
			}
			else
			{
				wchar_t msgBuf[500], error[50];
				LoadString(hInstance, IDS_PWD_MISMATCH, msgBuf, sizeof(msgBuf) / sizeof(msgBuf[0]));
				LoadString(hInstance, IDS_ERROR, error, sizeof(error) / sizeof(error[0]));
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

static int CALLBACK InitialSetFolder(HWND hWnd, UINT iMsg, LPARAM, LPARAM lData)
{
	if (iMsg == BFFM_INITIALIZED)
		SendMessage(hWnd, BFFM_SETSELECTION, TRUE, lData);

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
	LoadString(hInstance, idsTitle, title, sizeof(title) / sizeof(title[0]));

	SHGetMalloc(&pMalloc);

	lpBuf = (LPWSTR)pMalloc->Alloc(MAX_PATH * sizeof(wchar_t));

	bi.hwndOwner = NULL;
	bi.pidlRoot = NULL;
	bi.pszDisplayName = lpBuf;
	bi.lpszTitle = title;
	bi.ulFlags = 0;

	GetDlgItemText(hwndDlg, id, lpBuf, MAX_PATH);
	if (*lpBuf)
	{
		bi.lpfn = (BFFCALLBACK)InitialSetFolder;
		bi.lParam = (LPARAM)lpBuf;
	}
	else
	{
		bi.lpfn = NULL;
		bi.lParam = 0;
	}

	pidlBrowse = SHBrowseForFolder(&bi);
	if (pidlBrowse != NULL)
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

static void TimeToString(char *buf, int time)
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
	int ids[] = {IDC_EXCHANGE_FOLDER, IDC_BROWSE_FOLDER, IDC_COPIES, IDC_RUN_TIME,
				 IDC_CB_MN, IDC_CB_TH, IDC_CB_WD, IDC_CB_TU, IDC_CB_FR, IDC_CB_ST, IDC_CB_SU};

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

	int data[] = {IDC_CB_MN, ServerConfig::Monday, IDC_CB_TU, ServerConfig::Tuesday, IDC_CB_WD, ServerConfig::Wednesday,
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
		int data[] = {IDC_CB_MN, ServerConfig::Monday, IDC_CB_TU, ServerConfig::Tuesday, IDC_CB_WD, ServerConfig::Wednesday,
					  IDC_CB_TH, ServerConfig::Thursday, IDC_CB_FR, ServerConfig::Friday, IDC_CB_ST, ServerConfig::Saturday, IDC_CB_SU, ServerConfig::Sunday};

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
		std::string *fStr = (std::string *)SendMessage(hList, LB_GETITEMDATA, 0, 0);
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
					LRESULT idx = SendMessageA(hList, LB_ADDSTRING, 0, (LPARAM)str);
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
	switch (uMsg)
	{
	case WM_INITDIALOG:
		SettingsInit(hWnd, (ServerConfig *)lParam);
		break;
	case WM_DESTROY:
	{
#ifdef JOIN_SERVER
		JoinServer::SetHandler(NULL);
		delete jsHandler;
#endif
		PostQuitMessage(0);
		break;
	}

	case WM_COMMAND:
		switch (LOWORD(wParam))
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

#ifdef JOIN_SERVER
		case IDC_JS_CREATE:
			CreateJSAccont(hWnd);
			break;
		case IDC_JS_OPEN:
			ConnectToJS(hWnd);
			break;
#endif

		case IDC_SET_COM_PWD:
			ChangeCOMPassword(hInstance, hWnd);
			break;
#ifdef _Project_PavlovStore
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

			MessageBox(NULL, L"���������� ������������� ������ ��� �������� ���������", L"��������������", MB_OK | MB_ICONWARNING);
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
	switch (uMsg)
	{
	case WM_COMMAND:
		switch (LOWORD(wParam))
		{
		case IDOK:
		{
			HWND hEdit = GetDlgItem(hWnd, IDC_PASSWORD);
			int len = GetWindowTextLength(hEdit) + 1;
			wchar_t *buf = (wchar_t *)alloca(len * sizeof(wchar_t));
			GetWindowText(hEdit, buf, len);
			std::wstring pwd(buf);

			if (!ServerData::CheckAdminPassword(pwd))
			{
				wchar_t msgBuf[500], error[50];
				LoadString(hInstance, IDS_PWD_ERROR, msgBuf, sizeof(msgBuf) / sizeof(msgBuf[0]));
				LoadString(hInstance, IDS_ERROR, error, sizeof(error) / sizeof(error[0]));
				MessageBox(NULL, msgBuf, error, MB_OK);

				if (pwdTryCount++ < 2)
				{
					SetFocus(hEdit);
					break;
				}
				else
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

static DWORD LoopFoo(void *param)
{
#ifdef JOIN_SERVER
	HWND hWnd = CreateDialogParam(hInstance, MAKEINTRESOURCE(IDD_SETTINGS_JS), NULL, SettingsProc, (LPARAM)param);
#else
	HWND hWnd = CreateDialogParam(hInstance, MAKEINTRESOURCE(IDD_SETTINGS), NULL, SettingsProc, (LPARAM)param);
#endif
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
	if (CheckPassword(hInstance))
	{
		// res = (DialogBoxParam(hInstance, MAKEINTRESOURCE(IDD_SETTINGS), NULL, SettingsProc, (LPARAM)this) == IDOK);
		// if( res )
		//    Save();

		HANDLE hTh = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)LoopFoo, this, 0, NULL);
		WaitForSingleObject(hTh, INFINITE);
		CloseHandle(hTh);
	}
	return res;
}
#endif
