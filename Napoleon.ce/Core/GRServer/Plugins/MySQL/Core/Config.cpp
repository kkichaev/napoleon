/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * MySQL plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include <Windowsx.h>
#include "MySqlDrv.h"
#include <atldbcli.h>
#include "resource.h"

using namespace GRServer;
static Configurator* cfg;

static DWORD DEFAULT_PORT = 3306;
static WORD DEFAULT_LENGTH = 250;

static const char HOST[] = "Host";
static const char PORT[] = "Post";
static const char BASE[] = "Base";
static const char LOGIN[] = "Login";
static const char PASSWORD[] = "Password";
static const char IS_INTERNAL_BASE[] = "IsInternal";
static const char STRING_LENGTH[] = "StringLength";

Config::Config() : useAsInternalBase(false)
{
   host = "localhost";
   port = DEFAULT_PORT;
   defaultStringLength = DEFAULT_LENGTH;
}

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

static bool ReadLine(std::string *line, FILE* file)
{
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

      line->append(buf);
   
      if( newLine != NULL )
         break;
   }

   return true;
}

static void Trim(std::string* res, const std::string& _src, int offset, int size)
{
   const std::string& src = _src.substr(offset, size);

   int es = 0, ss = 0;
   std::string::const_reverse_iterator rb = src.rbegin();
   while( rb != src.rend() )
   {
      if( *rb != ' ' ) break;
      rb++;
      es++;
   }

   std::string::const_iterator b = src.begin();
   while( b != src.end() )
   {
      if( *b != ' ' ) break;
      b++;
      ss++;
   }

   res->assign(src.substr(ss, src.size() - es - ss));
}

static void ReadBoolean(bool *dest, const std::string& value)
{
   char *str = _strdup(value.c_str());
   CharLowerA(str);
   *dest = (strcmp(str, "true") == 0);
   free(str);
}

void Config::SetValue(const std::string& key, const std::string& value)
{
   if( key.compare(HOST) == 0 )
      host = value.c_str();
   else if( key.compare(PORT) == 0 )
      port = (DWORD)atoi(value.c_str());
   else if( key.compare(STRING_LENGTH) == 0 )
      defaultStringLength = (WORD)atoi(value.c_str());
   else if( key.compare(BASE) == 0 )
      base = value.c_str();
   else if( key.compare(LOGIN) == 0 )
      login = value.c_str();
   else if( key.compare(PASSWORD) == 0 )
      password = value.c_str();
   else if( key.compare(IS_INTERNAL_BASE) == 0 )
      ReadBoolean(&useAsInternalBase, value.c_str());
}

bool Config::Load(const std::string& fileName)
{
   FILE *f = fopen(fileName.c_str(), "rt");
   if( f == NULL )
      return false;

   std::string line;
   while(ReadLine(&line, f))
   {
      int pos = line.find('=');
      if( pos >= 0 )
      {
         std::string key, value;

         Trim(&key, line, 0, pos);
         Trim(&value, line, pos+1, -1);

         SetValue(key, value);
      }
   }
   fclose(f);
   return true;
}

static inline void PutKey(FILE* f, const char* key, const std::string& value)
{
   fprintf(f, "%s = %s\n", key, value.c_str());
}

static inline void PutKey(FILE* f, const char* key, DWORD value)
{
   fprintf(f, "%s = %u\n", key, value);
}

bool Config::Save(const std::string& fileName)
{
   FILE *f = fopen(fileName.c_str(), "wt");
   if( f == NULL )
      return false;

   PutKey(f, HOST, host);
   PutKey(f, PORT, port);
   PutKey(f, BASE, base);
   PutKey(f, IS_INTERNAL_BASE, (useAsInternalBase) ? "true" : "false");
   PutKey(f, LOGIN, login);
   PutKey(f, PASSWORD, password);
   PutKey(f, STRING_LENGTH, defaultStringLength);

   fclose(f);
   return true;
}

static void TestConnection(HWND hDlg);
static void GetText(std::string* res, HWND hDlg, WORD id);
static INT_PTR CALLBACK CfgProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      cfg = (Configurator*)lParam;
      cfg->InitDialog(hWnd);
      break;
   case WM_DESTROY:
      break;

   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDC_INTERNAL_BASE:
         if(IsDlgButtonChecked(hWnd, IDC_INTERNAL_BASE) == BST_CHECKED)
         {
            std::string base;
            GetText(&base, hWnd, IDC_BASE);
            if( base.empty() )
            {
               CheckDlgButton(hWnd, IDC_INTERNAL_BASE, 0);
               MessageBox(hWnd, L"Не обходимо указать базу", L"Ошибка", MB_ICONSTOP | MB_OK);
            }
         }
         break;
      case IDC_TEST_CONNECT:
         TestConnection(hWnd);
         break;
      case IDOK:
         cfg->Save(hWnd);
      case IDCANCEL:
         EndDialog(hWnd, LOWORD(wParam));
         break; 
      }
   }

   return FALSE;
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
   Config c;
   c.Load(configFile);

   SetWindowTextA(GetDlgItem(hDlg, IDC_LOGIN), c.login.c_str());
   SetWindowTextA(GetDlgItem(hDlg, IDC_PASSWORD), c.password.c_str());

   SetDlgItemInt(hDlg, IDC_PORT, c.port, FALSE);
   SetDlgItemInt(hDlg, IDC_STRING_LENGTH, c.defaultStringLength, FALSE);

   SetWindowTextA(GetDlgItem(hDlg, IDC_BASE), c.base.c_str());
   SetWindowTextA(GetDlgItem(hDlg, IDC_SERVER), c.host.c_str());

   if( c.useAsInternalBase )
      CheckDlgButton(hDlg, IDC_INTERNAL_BASE, BST_CHECKED);
}

static void GetText(std::string* res, HWND hDlg, WORD id)
{
   res->clear();
   HWND ctl = GetDlgItem(hDlg, id);
   if( ctl == NULL )
      return;

   int len = GetWindowTextLength(ctl) + 1;
   char* buf = (char*)alloca(sizeof(char) * len);
   GetWindowTextA(ctl, buf, len);
   res->assign(buf);
}

static void LoadConfig(Config *c, HWND hDlg)
{
   c->useAsInternalBase = (IsDlgButtonChecked(hDlg, IDC_INTERNAL_BASE) == BST_CHECKED);

   GetText(&c->host, hDlg, IDC_SERVER);
   GetText(&c->base, hDlg, IDC_BASE);
   GetText(&c->login, hDlg, IDC_LOGIN);
   GetText(&c->password, hDlg, IDC_PASSWORD);

   c->port = GetDlgItemInt(hDlg, IDC_PORT, NULL, FALSE);
   c->defaultStringLength = GetDlgItemInt(hDlg, IDC_STRING_LENGTH, NULL, FALSE);
}

void Configurator::Save(HWND hDlg)
{
   Config c;
   LoadConfig(&c, hDlg);
   c.Save(configFile);
}

static void ShowError(MYSQL *conn)
{
   char buf[1000];
   sprintf(buf, "Ошибка %u: %s ", mysql_errno(conn), mysql_error(conn));
   MessageBoxA(NULL, buf, "Ошибка", MB_ICONSTOP | MB_OK);
}

static void TestConnection(HWND hDlg)
{
   MYSQL *conn;
   conn = mysql_init(NULL);
   if( conn == NULL )
   {
      ShowError(conn);
      return;
   }
   
   Config c;
   LoadConfig(&c, hDlg);
   if (mysql_real_connect(conn, c.host.c_str(), c.login.c_str(), c.password.c_str(), NULL, c.port, NULL, 0) == NULL)
   {
      ShowError(conn);
   }
   else
   {
      MessageBox(NULL, L"Подключение к базе прошло успешно!", L"Информация", MB_ICONINFORMATION | MB_OK);
   }

   mysql_close(conn);
}
