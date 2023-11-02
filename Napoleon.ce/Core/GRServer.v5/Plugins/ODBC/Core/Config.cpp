/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include <Windowsx.h>
#include "ODBCSource.h"
#include "resource.h"

#include <atldbcli.h>

using namespace GRServer;
static Configurator* cfg;

// строки в верхнем регистре
static const wchar_t* DRIVER_FILTER[] = { L"SQL", L"ORACLE", L"FIREBIRD", NULL };

static const wchar_t PROVIDER[] = L"Provider";
static const wchar_t CONN_STR[] = L"ConnStr";
static const wchar_t IS_INTERNAL_BASE[] = L"IsInternal";

static const wchar_t DATA_CONNECTION[] = L"DataConnection";
static const wchar_t SERVER[] = L"Server";
static const wchar_t BASE[] = L"Base";
static const wchar_t TRUSTED_CONNECTION[] = L"TrustedConnection";
static const wchar_t LOGIN[] = L"Login";
static const wchar_t PASSWORD[] = L"Password";

Config::Config() : useAsInternalBase(false) //, concurentStmtCount(0)
{
}

bool Config::MakeConnectionString(std::wstring* connStr)
{
   if( this->connStr.empty() )
      return false;

   connStr->assign(this->connStr);
   return true;
}
//
//static void Encode(std::wstring* res, const std::wstring& src)
//{
//   unsigned int mask = 0xF0F0;
//   std::wstring::const_reverse_iterator i = src.rbegin();
//   int ctr = 0;
//   res->clear();
//
//   while( i != src.rend() )
//   {
//      wchar_t buf[30];
//      wchar_t sym = (*i);
//      sym ^= mask;
//      wsprintf(buf, L"%04X", sym);
//      res->append(buf);
//      if( ++ctr > 3)
//      {
//         mask = 0xF0F0;
//         ctr = 0;
//      } else
//         mask >>= 1;
//      i++;
//   }
//}
//
//static bool Decode(std::wstring* res, const std::wstring& src)
//{
//   if( (src.length() %4) != 0 )
//      return false;
//
//   unsigned int mask = 0xF0F0;
//
//   unsigned i = 0;
//   int ctr = 0;
//   res->clear();
//
//   while( i < src.length() )
//   {
//      wchar_t *ep;
//      wchar_t sym = (wchar_t)wcstol(src.substr(i, 4).c_str(), &ep, 16);
//      sym ^= mask;
//      res->insert(res->begin(), sym);
//      if( ++ctr > 3)
//      {
//         mask = 0xF0F0;
//         ctr = 0;
//      } else
//         mask >>= 1;
//      i += 4;
//   }
//
//   return true;
//}

static bool ReadLine(std::wstring *line, FILE* file)
{
   USES_CONVERSION;
   line->clear();

   if( file == NULL || feof(file) ) return false;

   while( !feof(file) )
   {
      char buf[200];
      if( fgets(buf, sizeof(buf), file) == NULL )
         break;

      char *newLine = strchr(buf, '\n');
      if( newLine != NULL )
         *newLine = '\0';

      line->append(A2W(buf));
   
      if( newLine != NULL )
         break;
   }

   return true;
}

static void Trim(std::wstring* res, const std::wstring& _src, size_t offset, size_t size)
{
   const std::wstring& src = _src.substr(offset, size);

   size_t es = 0, ss = 0;
   std::wstring::const_reverse_iterator rb = src.rbegin();
   while( rb != src.rend() )
   {
      if( *rb != L' ' ) break;
      rb++;
      es++;
   }

   std::wstring::const_iterator b = src.begin();
   while( b != src.end() )
   {
      if( *b != L' ' ) break;
      b++;
      ss++;
   }

   res->assign(src.substr(ss, src.size() - es - ss));
}

static void ReadBoolean(bool *dest, const std::wstring& value)
{
   wchar_t *str = _wcsdup(value.c_str());
   CharLower(str);
   *dest = (wcscmp(str, L"true") == 0);
   free(str);
}

void Config::SetValue(const std::wstring& key, const std::wstring& value)
{
   if( key.compare(PROVIDER) == 0 )
      provider = value.c_str();
   else if( key.compare(CONN_STR) == 0 )
      connStr = value.c_str();
   //else if( key.compare(BASE) == 0 )
   //   initialBase = value.c_str();
   //else if( key.compare(LOGIN) == 0 )
   //   login = value.c_str();
   //else if( key.compare(PASSWORD) == 0 )
   //   Decode(&password, value.c_str());
   //else if( key.compare(TRUSTED_CONNECTION) == 0 )
   //   ReadBoolean(&useTrustedConnection, value.c_str());
	else if (key.compare(IS_INTERNAL_BASE) == 0)
		ReadBoolean(&useAsInternalBase, value.c_str());
	//else if (key.compare(CONC_STMT_STR) == 0)
	//	concurentStmtCount = _wtoi(value.c_str());
}

bool Config::Load(const std::wstring& fileName)
{
   FILE *f = _wfopen(fileName.c_str(), L"rt");
   if( f == NULL )
      return false;

   std::wstring line;
   while(ReadLine(&line, f))
   {
      size_t pos = line.find('=');
      if( pos != std::string::npos )
      {
         std::wstring key, value;

         Trim(&key, line, 0, pos);
         Trim(&value, line, pos+1, -1);

         SetValue(key, value);
      }
   }
   fclose(f);
   return true;
}

static void PutKey(FILE* f, const wchar_t* key, const std::wstring& value)
{
   USES_CONVERSION;
   fprintf(f, "%s = %s\n", W2A_CP(key, CP_UTF8), W2A_CP(value.c_str(), CP_UTF8));
}

//static void PutKey(FILE* f, const wchar_t* key, DWORD value)
//{
//	USES_CONVERSION;
//	fprintf(f, "%s = %d\n", W2A(key), value);
//}

bool Config::Save(const std::wstring& fileName)
{
   FILE *f = _wfopen(fileName.c_str(), L"wt");
   if( f == NULL )
      return false;

   PutKey(f, PROVIDER, provider);
   PutKey(f, CONN_STR, connStr);
   PutKey(f, IS_INTERNAL_BASE, (useAsInternalBase) ? L"true" : L"false");
	//PutKey(f, CONC_STMT_STR, concurentStmtCount);

   fclose(f);
   return true;
}

static void GetText(std::wstring* res, HWND hDlg, WORD id);
static INT_PTR CALLBACK CfgProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      cfg = (Configurator*)lParam;
      cfg->InitDialog(hWnd);
      break;

   case WM_DESTROY:
      cfg->Dispose();
      break;

   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDC_SETTNGS:
         cfg->LoadSettings(hWnd);
         break;
      case IDC_TEST_CONNECT:
         cfg->TestConnection(hWnd);
         break;
      case IDOK:
         cfg->Save(hWnd);
      case IDCANCEL:
         EndDialog(hWnd, LOWORD(wParam));
         break; 
      case IDC_PROVIDERS:
         if( HIWORD(wParam) == CBN_SELCHANGE )
            cfg->OnProviderChanged(hWnd);
         break;
      }
   }

   return FALSE;
}

static void ShowError(const wchar_t* error)
{
   MessageBox(NULL, error, L"Ошибка", MB_ICONSTOP | MB_OK);
}

void Configurator::OnProviderChanged(HWND hDlg)
{
   if( connStr.empty() )
      return;

   HWND hList = GetDlgItem(hDlg, IDC_PROVIDERS);
   int cs = ComboBox_GetCurSel(hList);
   if( cs < 0 )
      return;

   int rc = MessageBox(hDlg, L"Изменился провайдер. Сохранить настроки базы?", L"Вопрос", MB_YESNO | MB_ICONQUESTION);
   if( rc == IDNO )
   {
      connStr.clear();
      return;
   }

   int len = ComboBox_GetLBTextLen(hList, cs);
   wchar_t *driver = (wchar_t*)alloca((len+10) * sizeof(wchar_t));
   ComboBox_GetLBText(hList, cs, driver);
   wcscat(driver, L";");
   size_t pos = connStr.find(L"DRIVER=");
   if( pos != std::wstring::npos )
   {
      pos += 7;
      size_t pos2 = connStr.find_first_of(L';', pos);
      connStr.erase(pos, pos2-pos+1);
      connStr.insert(pos, driver);
   }
}

Configurator::Configurator()
{
}

bool Configurator::Configure(IServer* server, HWND owner)
{
   return (DialogBoxParam(hInstance, MAKEINTRESOURCE(IDD_SETTINGS), NULL, CfgProc, (LPARAM)this) == IDOK);
}

void Configurator::InitDialog(HWND hDlg)
{
   if( SQLAllocHandle(SQL_HANDLE_ENV, SQL_NULL_HANDLE, &hEnv) != SQL_SUCCESS )
   {
      ShowError(L"Не могу загрузить ODBC");
      return;
   }
   SQLSetEnvAttr(hEnv, SQL_ATTR_ODBC_VERSION, (SQLPOINTER)SQL_OV_ODBC3, 0);

   Config c;

   bool loaded = c.Load(configFile);

   LoadProviders(GetDlgItem(hDlg, IDC_PROVIDERS), c.provider);

   if( loaded )
   {
      connStr = c.connStr;
      if( c.useAsInternalBase )
         CheckDlgButton(hDlg, IDC_INTERNAL_BASE, BST_CHECKED);
   }
}

static bool InFilter(const wchar_t* drv)
{
   bool ret = false;
   wchar_t *drvUp = _wcsdup(drv);
   CharUpper(drvUp);
   for(int idx = 0; DRIVER_FILTER[idx]; idx++ )
      if( wcsstr(drvUp, DRIVER_FILTER[idx]) != NULL )
      {
         ret = true;
         break;
      }

   free(drvUp);
   return ret;
}

void Configurator::LoadProviders(HWND hCombo, const std::wstring& selected)
{
   SQLRETURN rc;

   while( true )
   {
      wchar_t drv[1000], atts[2000];
      SQLSMALLINT drvLen, attsLen;
      rc = SQLDrivers(hEnv, SQL_FETCH_NEXT, drv, sizeof(drv)/sizeof(drv[0]), &drvLen, atts, sizeof(atts)/sizeof(atts[0]), &attsLen);
      if( rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO )
         break;
      if( InFilter(drv) )
      {
         int i = ComboBox_AddString(hCombo, drv);
         if( selected.compare(drv) == 0 )
            ComboBox_SetCurSel(hCombo, i);
      }
   }
}

void Configurator::Dispose()
{
   SQLFreeHandle(SQL_HANDLE_ENV, hEnv);
}

static void GetText(std::wstring* res, HWND hDlg, WORD id)
{
   res->clear();
   HWND ctl = GetDlgItem(hDlg, id);
   if( ctl == NULL )
      return;

   int len = GetWindowTextLength(ctl) + 1;
   wchar_t* buf = (wchar_t*)alloca(sizeof(wchar_t) * len);
   GetWindowText(ctl, buf, len);
   res->assign(buf);
}

void Configurator::LoadSettings(HWND hDlg)
{
   HWND hList = GetDlgItem(hDlg, IDC_PROVIDERS);
   int cs = ComboBox_GetCurSel(hList);
   if( cs < 0 )
      return;

   std::wstring driver;
   GetText(&driver, hDlg, IDC_PROVIDERS);
   driver += L";";

   std::wstring inStr;
   if( connStr.find(driver) != std::wstring::npos )
      inStr = connStr;
   else
   {
      inStr = L"DRIVER="; inStr += driver;
   }
   
   wchar_t cmd[2000];
   SQLSMALLINT cw;
   SQLHDBC hDbc;

   SQLAllocHandle(SQL_HANDLE_DBC, hEnv, &hDbc);
   SQLRETURN rc = SQLDriverConnect(hDbc, hDlg, (wchar_t*)inStr.c_str(), SQL_NTS, cmd, sizeof(cmd)/sizeof(cmd[0]), &cw, SQL_DRIVER_PROMPT);
   if( rc == SQL_SUCCESS || rc == SQL_SUCCESS_WITH_INFO )
      connStr = cmd;
	else
	{
		std::string error;
		char state[10], err[10000];
		SQLSMALLINT cn;
		SQLINTEGER errCode;

		int idx = 1;
		while (true)
		{
			SQLRETURN rc = SQLGetDiagRecA(SQL_HANDLE_DBC, hDbc, idx++, (SQLCHAR*)state, &errCode, (SQLCHAR*)err, sizeof(err) / sizeof(err[0]), &cn);
			if (rc != SQL_SUCCESS)
				break;

			error += err;
		}

		MessageBoxA(NULL, error.c_str(), "Ошибка", MB_OK);

	}

   SQLFreeHandle(SQL_HANDLE_DBC, hDbc);
}

static void LoadConfig(Config *c, HWND hDlg)
{
   c->useAsInternalBase = (IsDlgButtonChecked(hDlg, IDC_INTERNAL_BASE) == BST_CHECKED);

   HWND hp = GetDlgItem(hDlg, IDC_PROVIDERS);
   int cur = ComboBox_GetCurSel(hp);
   if( cur >= 0 )
      GetText(&c->provider, hDlg, IDC_PROVIDERS);
}

void Configurator::Save(HWND hDlg)
{
   if( connStr.empty() )
   {
      ShowError(L"Соединени еще не настроено, нажмите на кнопку настроить для установки параметров");
      return;
   }
   Config c;
   LoadConfig(&c, hDlg);
   c.connStr = connStr;
   c.Save(configFile);
}

static void ShowPropErrors(SQLHENV hEnv, SQLHDBC hdbc)
{
   wchar_t state[10], err[10000];
   SQLSMALLINT cn;
   SQLINTEGER errCode;

   SQLGetDiagRec(SQL_HANDLE_DBC, hdbc, 1, state, &errCode, err, sizeof(err)/sizeof(err[0]),&cn); 
   
   ShowError(err);
}

void Configurator::TestConnection(HWND hDlg)
{
   if( connStr.empty() )
   {
      ShowError(L"Соединени еще не настроено, нажмите на кнопку настроить для установки параметров");
      return;
   }
   Config c;
   LoadConfig(&c, hDlg);
   c.connStr = connStr;

   SQLHDBC hDbc;
   SQLAllocHandle(SQL_HANDLE_DBC, hEnv, &hDbc);
   SQLRETURN rc = SQLDriverConnect(hDbc, hDlg, (wchar_t*)connStr.c_str(), SQL_NTS, NULL, 0, NULL, SQL_DRIVER_NOPROMPT);
   if( rc != SQL_SUCCESS && rc != SQL_SUCCESS_WITH_INFO )
   {
      ShowPropErrors(hEnv, hDbc);
   } else
   {
      MessageBox(hDlg, L"Соединение прошло успешно!", L"Информация", MB_OK| MB_ICONINFORMATION);
   }

   SQLFreeHandle(SQL_HANDLE_DBC, hDbc);
}
