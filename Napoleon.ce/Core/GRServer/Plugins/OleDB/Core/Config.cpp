/*
 * Copyright (C), 2009 - 2012, Денис Мосягин
 *
 * OleDB plugin
 *
 * ert   16/06/2012   creating
 */
#include "stdafx.h"
#include <Windowsx.h>
#include "OleDB.h"
#include <atldbcli.h>
#include "resource.h"

using namespace GRServer;
static Configurator* cfg;

static const wchar_t DATA_CONNECTION[] = L"DataConnection";
static const wchar_t PROVIDER[] = L"Provider";
static const wchar_t SERVER[] = L"Server";
static const wchar_t BASE[] = L"Base";
static const wchar_t TRUSTED_CONNECTION[] = L"TrustedConnection";
static const wchar_t LOGIN[] = L"Login";
static const wchar_t PASSWORD[] = L"Password";
static const wchar_t IS_INTERNAL_BASE[] = L"IsInternal";

Config::Config() : useTrustedConnection(false), provider(L"SQLOLEDB"), useAsInternalBase(false)
{
}

bool Config::MakeConnectionString(std::wstring* connStr)
{
   if( login.empty() && !useTrustedConnection )
      return false;

   connStr->assign(L"Provider=");
   connStr->append(provider);

   connStr->append(L";Data Source=");
   if( !server.empty() )
      connStr->append(server);
   else
      connStr->append(L"(local)");
   if( !instance.empty() )
   {
      connStr->append(L"\\");
      connStr->append(instance);
   }
   connStr->append(L";");

   if( !initialBase.empty() )
   {
      connStr->append(L"Initial Catalog=");
      connStr->append(initialBase);
      connStr->append(L";");
   }

   if( useTrustedConnection )
   {
      connStr->append(L"Integrated Security=SSPI");
   } else
   {
      connStr->append(L"User ID=");
      connStr->append(login);
      connStr->append(L";Password=");
      connStr->append(password);
   }
   connStr->append(L";");

   return true;
}

static void Encode(std::wstring* res, const std::wstring& src)
{
   unsigned int mask = 0xF0F0;
   std::wstring::const_reverse_iterator i = src.rbegin();
   int ctr = 0;
   res->clear();

   while( i != src.rend() )
   {
      wchar_t buf[30];
      wchar_t sym = (*i);
      sym ^= mask;
      wsprintf(buf, L"%04X", sym);
      res->append(buf);
      if( ++ctr > 3)
      {
         mask = 0xF0F0;
         ctr = 0;
      } else
         mask >>= 1;
      i++;
   }
}

static bool Decode(std::wstring* res, const std::wstring& src)
{
   if( (src.length() %4) != 0 )
      return false;

   unsigned int mask = 0xF0F0;

   unsigned i = 0;
   int ctr = 0;
   res->clear();

   while( i < src.length() )
   {
      wchar_t *ep;
      wchar_t sym = (wchar_t)wcstol(src.substr(i, 4).c_str(), &ep, 16);
      sym ^= mask;
      res->insert(res->begin(), sym);
      if( ++ctr > 3)
      {
         mask = 0xF0F0;
         ctr = 0;
      } else
         mask >>= 1;
      i += 4;
   }

   return true;
}

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

static void Trim(std::wstring* res, const std::wstring& _src, int offset, int size)
{
   const std::wstring& src = _src.substr(offset, size);

   int es = 0, ss = 0;
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
   else if( key.compare(SERVER) == 0 )
      server = value.c_str();
   else if( key.compare(BASE) == 0 )
      initialBase = value.c_str();
   else if( key.compare(LOGIN) == 0 )
      login = value.c_str();
   else if( key.compare(PASSWORD) == 0 )
      Decode(&password, value.c_str());
   else if( key.compare(TRUSTED_CONNECTION) == 0 )
      ReadBoolean(&useTrustedConnection, value.c_str());
   else if( key.compare(IS_INTERNAL_BASE) == 0 )
      ReadBoolean(&useAsInternalBase, value.c_str());
}

bool Config::Load(const std::wstring& fileName)
{
   FILE *f = _wfopen(fileName.c_str(), L"rt");
   if( f == NULL )
      return false;

   std::wstring line;
   while(ReadLine(&line, f))
   {
      int pos = line.find('=');
      if( pos >= 0 )
      {
         std::wstring key, value;

         Trim(&key, line, 0, pos);
         Trim(&value, line, pos+1, -1);

         SetValue(key, value);
      }
   }
   fclose(f);
   return true;

   //bool err = false;
   //if( fread(&useTrustedConnection, sizeof(useTrustedConnection), 1, f) != 1 )
   //   err = true;
   //else
   //{
   //   std::wstring pwd;

   //   err = ( !ReadString(&server, f) ||
   //      !ReadString(&instance, f) ||
   //      !ReadString(&initialBase, f) ||
   //      !ReadString(&pwd, f) ||
   //      !ReadString(&login, f) );

   //   if( !err )
   //      Decode(&password, pwd);
   //}

   //fclose(f);
   //return !err;
}

static void PutKey(FILE* f, const wchar_t* key, const std::wstring& value)
{
   USES_CONVERSION;
   fprintf(f, "%s = %s\n", W2A(key), W2A(value.c_str()));
}

bool Config::Save(const std::wstring& fileName)
{
   FILE *f = _wfopen(fileName.c_str(), L"wt");
   if( f == NULL )
      return false;

   PutKey(f, DATA_CONNECTION, L"default");
   PutKey(f, PROVIDER, provider);
   PutKey(f, SERVER, server);
   PutKey(f, BASE, initialBase);
   PutKey(f, TRUSTED_CONNECTION, (useTrustedConnection) ? L"true" : L"false");
   PutKey(f, IS_INTERNAL_BASE, (useAsInternalBase) ? L"true" : L"false");
   if(!useTrustedConnection)
   {
      PutKey(f, LOGIN, login);

      std::wstring pwd;
      Encode(&pwd, password);
      PutKey(f, PASSWORD, pwd);
   }

   fclose(f);
   return true;
}

static void LoadProviders(HWND hCombo, const std::wstring& selected)
{
   CEnumerator prv;
   HRESULT hr;
   hr = prv.Open();

   if(SUCCEEDED(hr))
   {
      while(prv.MoveNext() == S_OK)
      {
         int i = ComboBox_AddString(hCombo, prv.m_szDescription);
         wchar_t* id = (wchar_t*)malloc((wcslen(prv.m_szName) + 1) * sizeof(wchar_t));
         wcscpy(id, prv.m_szName);
         ComboBox_SetItemData(hCombo, i, id);

         if( selected.compare(id) == 0 )
            ComboBox_SetCurSel(hCombo, i);
      }
   }

   prv.Close();
}

static void FreeProviders(HWND hCombo)
{
   int c = ComboBox_GetCount(hCombo);
   for( int i=0; i<c; i++ )
      free((wchar_t*)ComboBox_GetItemData(hCombo, i));
}

static void TestConnection(HWND hDlg);
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
      FreeProviders(GetDlgItem(hWnd, IDC_PROVIDERS));
      break;

   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDC_INTERNAL_BASE:
         //if(IsDlgButtonChecked(hWnd, IDC_INTERNAL_BASE) == BST_CHECKED)
         //{
         //   std::wstring base;
         //   GetText(&base, hWnd, IDC_BASE);
         //   if( base.empty() )
         //   {
         //      CheckDlgButton(hWnd, IDC_INTERNAL_BASE, 0);
         //      MessageBox(hWnd, L"Не обходимо указать базу", L"Ошибка", MB_ICONSTOP | MB_OK);
         //   }
         //}
         break;
      case IDC_TRUSTED_CONNECTION:
         {
            BOOL enable = (IsDlgButtonChecked(hWnd, IDC_TRUSTED_CONNECTION) == BST_CHECKED) ? FALSE : TRUE;
            EnableWindow(GetDlgItem(hWnd, IDC_LOGIN), enable);
            EnableWindow(GetDlgItem(hWnd, IDC_PASSWORD), enable);
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
   bool loaded = c.Load(configFile);
   LoadProviders(GetDlgItem(hDlg, IDC_PROVIDERS), c.provider);

   if( loaded )
   {

      SetWindowText(GetDlgItem(hDlg, IDC_LOGIN), c.login.c_str());
      SetWindowText(GetDlgItem(hDlg, IDC_PASSWORD), c.password.c_str());

      SetWindowText(GetDlgItem(hDlg, IDC_BASE), c.initialBase.c_str());
      SetWindowText(GetDlgItem(hDlg, IDC_SERVER), c.server.c_str());

      if( c.useTrustedConnection )
      {
         CheckDlgButton(hDlg, IDC_TRUSTED_CONNECTION, BST_CHECKED);
         EnableWindow(GetDlgItem(hDlg, IDC_LOGIN), FALSE);
         EnableWindow(GetDlgItem(hDlg, IDC_PASSWORD), FALSE);
      }

      if( c.useAsInternalBase )
         CheckDlgButton(hDlg, IDC_INTERNAL_BASE, BST_CHECKED);
   }
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

static void LoadConfig(Config *c, HWND hDlg)
{
   c->useTrustedConnection = (IsDlgButtonChecked(hDlg, IDC_TRUSTED_CONNECTION) == BST_CHECKED);
   c->useAsInternalBase = (IsDlgButtonChecked(hDlg, IDC_INTERNAL_BASE) == BST_CHECKED);

   GetText(&c->server, hDlg, IDC_SERVER);
   GetText(&c->initialBase, hDlg, IDC_BASE);
   GetText(&c->login, hDlg, IDC_LOGIN);
   GetText(&c->password, hDlg, IDC_PASSWORD);

   HWND hp = GetDlgItem(hDlg, IDC_PROVIDERS);
   int cur = ComboBox_GetCurSel(hp);
   if( cur >= 0 )
   {
      c->provider = (const wchar_t*)ComboBox_GetItemData(hp, cur);
   }
}

void Configurator::Save(HWND hDlg)
{
   Config c;
   LoadConfig(&c, hDlg);
   c.Save(configFile);
}

static void ShowError(const wchar_t* error)
{
   MessageBox(NULL, error, L"Ошибка", MB_ICONSTOP | MB_OK);
}

static void ShowPropErrors(const ATL::CDataSource &source)
{
   ULONG nProps = 0;
   DBPROPIDSET propSet;
   DBPROPSET*  pProps;
   propSet.guidPropertySet = DBPROPSET_PROPERTIESINERROR;
   propSet.cPropertyIDs = 0;

   HRESULT hr;
   hr = source.GetProperties(1, &propSet, &nProps, &pProps);

	CComPtr<IMalloc>  mem;
   std::wstring buf;
   if( SUCCEEDED(hr) )
   {      
      source.m_spInit->QueryInterface(IID_IMalloc, (void**)&mem);
      while( nProps-- > 0 )
      {
         DBPROPSET *cur = pProps + nProps;
         for( ULONG i=0; i<cur->cProperties; i++ )
         {
            wchar_t vbuf[50];
            wsprintf(vbuf, L"%d", cur->rgProperties[i].dwPropertyID);
            if( buf.empty() )
               buf += L"Ошибка в свойствах: ";
            else 
               buf += L",";
            buf += vbuf;
            VariantClear(&cur->rgProperties[i].vValue);
         }

         mem->Free(cur->rgProperties);
      }
      mem->Free(pProps);
   }

   ShowError(buf.c_str());
}

static void TestConnection(HWND hDlg)
{
   std::wstring connectionString;
   Config c;
   LoadConfig(&c, hDlg);
   c.initialBase.clear();
   if( !c.MakeConnectionString(&connectionString) )
   {
      ShowError(L"Ошибка в параметрах: не введен логин");
      return;
   }

   CDataConnection dc;
   HRESULT hr;
   hr = dc.Open(connectionString.c_str());
   if (!SUCCEEDED(hr))
   {
      if( hr == DB_SEC_E_AUTH_FAILED )
      {
         ShowError(L"Ошибка аутентификации");
      } else
      {
         if( (hr == DB_S_ERRORSOCCURRED || hr == DB_E_ERRORSOCCURRED) && ((const ATL::CDataSource &)dc).m_spInit != NULL )
         {
            ShowPropErrors((const ATL::CDataSource &)dc);
         } else
         {
            wchar_t buf[100];
            wsprintf(buf, L"Ошибка с кодом %X", hr);
            ShowError(buf);
         }
      }
   } else
   {
      MessageBox(NULL, L"Подключение к базе прошло успешно!", L"Информация", MB_ICONINFORMATION | MB_OK);
   }

   dc.CloseDataSource();
}
