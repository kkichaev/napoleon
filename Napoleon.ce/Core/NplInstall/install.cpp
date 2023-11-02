/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Программа установки
 *
 *  ert   21/12/2007   creating
 */
#include "stdafx.h"
#include <rapi.h>

#include <shlobj.h>
#include <shlwapi.h>
#include <objbase.h>
#include <tchar.h>

#include "resource.h"

#include <string>
#include <vector>

using namespace std;

bool runOnVista;
TCHAR fileName[MAX_PATH];

HINSTANCE hInstance;
ATOM regClass;
HWND hMainWnd;
HANDLE hLoopThread = INVALID_HANDLE_VALUE;

static bool AskFileName(char *fn, int cb);
static void ConvertToFullPath(char *fn);
static bool IsFileExist(const char *fn);
static bool CheckActiveSync(char *mpath, DWORD cb);

// work with full file name
static void Install(const char *fileName);

static LPITEMIDLIST PidlBrowse(HWND hwnd, int nCSIDL, LPSTR pszDisplayName);
static void CopyCabsToDevice(LPCTSTR iniFile);

static void InstallOnDevice(const vector<wstring> &cabFiles);

static bool AskFileName(LPTSTR fn, int cb)
{
   OPENFILENAME ofn = {0};
   ofn.lStructSize = sizeof(ofn);
   ofn.hInstance = hInstance;
   ofn.lpstrFilter = _T("Файлы установки (*.ini, *.cab)\0*.ini;*.cab\0");

   *fn = '\0';
   ofn.lpstrFile = fn;
   ofn.nMaxFile = cb;
   ofn.lpstrTitle = _T("Выберите файл для установки на КПК");
   ofn.Flags = OFN_FILEMUSTEXIST | OFN_EXPLORER;

   return (GetOpenFileName(&ofn) != FALSE);
}

static void ConvertToFullPath(LPTSTR fn)
{
   // first convert to windows file name
   LPTSTR p = _tcschr(fn, '/');
   while( p != NULL )
   {
      *p = _T('\\');
      p = _tcschr(p, _T('/'));
   }

   if( *fn == _T('\\') || fn[1] == _T(':') )
      return;

   TCHAR buf[MAX_PATH];
   GetCurrentDirectory(sizeof(buf)/sizeof(buf[0]), buf);
   _tcscat(buf, _T("\\"));
   _tcscat(buf, fn);
   _tcscpy(fn, buf);
}

static bool IsFileExist(LPCTSTR fn)
{
   HANDLE hFile = CreateFile(fn, GENERIC_READ, FILE_SHARE_READ, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0);
   
   if( hFile == INVALID_HANDLE_VALUE )
      return false;

   CloseHandle(hFile);
   return true;
}

static bool CheckActiveSync(LPTSTR mpath, DWORD cb)
{
   HKEY hk;
   
   if( RegOpenKeyEx(HKEY_LOCAL_MACHINE, _T("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\CEAppMgr.exe"),
      0, KEY_READ, &hk) != ERROR_SUCCESS )
   {
      return false;
   }

   bool retVal = (RegQueryValueEx(hk, NULL, NULL, NULL, (BYTE*)mpath, &cb) == ERROR_SUCCESS);
   RegCloseKey(hk);
   return retVal;
}

static void Install(LPCTSTR fileName)
{
   TCHAR appMgrPath[MAX_PATH*2];
   if( !CheckActiveSync(appMgrPath, sizeof(appMgrPath)/sizeof(appMgrPath[0])) )
   {
      if( !runOnVista )
      {
         MessageBox(NULL, _T("Для установки программы на КПК необходимо установить ActiveSync\n")
            _T("http://www.microsoft.com/windowsmobile/activesync"),
            _T("Ошибка"), MB_ICONSTOP|MB_OK|MB_SETFOREGROUND|MB_TOPMOST);
      } else
         CopyCabsToDevice(fileName);
      return;
   }

   LPCTSTR p = _tcsrchr(fileName, _T('.'));
   if( _tcsicmp(p+1, _T("cab")) == 0 )
   {
      vector<wstring> files;
      files.push_back(fileName);
      InstallOnDevice(files);
   } else
   {
      STARTUPINFO si = {0};
      PROCESS_INFORMATION pi = {0};
      si.cb = sizeof(si);

      TCHAR fileDir[MAX_PATH], *dest = fileDir;
      LPCTSTR start = fileName, end = _tcsrchr(fileName, _T('\\'));

      while( start < end )
         *dest++ = *start++;
      *dest = _T('\0');

      _tcscat(appMgrPath, _T(" \""));
      _tcscat(appMgrPath, fileName/*end+1*/);
      _tcscat(appMgrPath, _T("\""));
      CreateProcess(NULL, appMgrPath, NULL, NULL, FALSE, 0, NULL, fileDir, &si, &pi);
   }
}

void AddFile(vector<wstring> *files, LPCTSTR dir, const char *file)
{
   const char *sp = file, *ep = file + strlen(file) - 1;

   while( *sp == ' ' ) sp++;
   while( ep > sp && (signed char) *ep > 0 && isspace(*ep) ) ep--;
   if( ep <= sp ) return;

   ep++;
   wchar_t fnW[MAX_PATH];
   wcscpy(fnW, dir);
   int len = wcslen(fnW);

   wchar_t *fP = fnW+len;
   len = MultiByteToWideChar(CP_ACP, 0, sp, (ep-sp), fP, sizeof(fnW)/sizeof(fnW[0])-len);
   fP[len] = 0;
   files->push_back(fnW);
}

void GetCabFiles(LPCTSTR iniFile, vector<wstring> *files)
{
   // special case for cab files
   LPCTSTR ext = _tcsrchr(iniFile, _T('.'));
   if( _tcsicmp(ext, _T(".cab")) == 0 )
   {
      files->push_back(wstring(iniFile));
      return;
   }

   TCHAR iniDir[MAX_PATH], *p;
   FILE *rd = _wfopen(iniFile, _T("rt"));
   if( rd == NULL )
      return;

   _tcscpy(iniDir, iniFile);
   p = _tcsrchr(iniDir, _T('\\'));
   p[1] = '\0';

   char buf[1000];
   while( fgets(buf, sizeof(buf)/sizeof(buf[0]), rd) != NULL )
   {
      if( strncmp(buf, "CabFiles", sizeof("CabFiles")-1) == 0 )
      {
         char *tp = strchr(buf, '=');
         if( tp != NULL )
         {
            do
               tp++;
            while( (signed char)*tp > 0 && isspace(*tp) );
            
            if( *tp != '\0' )
            {
               char *ep = strchr(tp, ',');
               while( ep != NULL )
               {
                  *ep = '\0';
                  AddFile(files, iniDir, tp);
                  tp = ep + 1;
                  ep = strchr(tp, ',');
               }
               AddFile(files, iniDir, tp);
            }
         }
         break;
      }
   }
   fclose(rd);
}

//
//---------------------- window ------------------------------------
//
LRESULT CALLBACK WindowProc(HWND hWnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_CLOSE:
      DestroyWindow(hWnd);
      return 0;

   case WM_DESTROY:
      PostQuitMessage(0);
      hMainWnd = NULL;
      return 0;

   default:
      break;
   }
   return DefWindowProc(hWnd, uMsg, wParam, lParam);
}

DWORD WINAPI MessageLoop(void*)
{
   MSG msg;
   while( GetMessage(&msg, NULL, 0, 0) )
   {
      TranslateMessage(&msg);
      DispatchMessage(&msg);
   }
   return 0;
}

void CloseWindow()
{
   if( hMainWnd != NULL )
   {
      DestroyWindow(hMainWnd);
      hMainWnd = NULL;
   }

   if( hLoopThread != INVALID_HANDLE_VALUE )
   {
      TerminateThread(hLoopThread, 0);
      CloseHandle(hLoopThread);
      hLoopThread = INVALID_HANDLE_VALUE;
   }
}

HWND CreateMainWindow()
{
   CloseWindow();

   if( regClass == 0 )
   {
      WNDCLASS cls = {0};
      cls.lpfnWndProc = WindowProc;
      cls.hInstance = hInstance;
      cls.hIcon = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_ICON1));
      cls.hbrBackground = (HBRUSH)(COLOR_WINDOW + 1);
      cls.lpszClassName = _T("CopyWindow");

      regClass = RegisterClass(&cls);
   }

   hMainWnd = CreateWindowEx(0, _T("CopyWindow"), _T("Копирование"), WS_OVERLAPPEDWINDOW, 
      CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, CW_USEDEFAULT, NULL, NULL, hInstance, NULL);

   ShowWindow(hMainWnd, SW_SHOW);
   UpdateWindow(hMainWnd);

   //hLoopThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)MessageLoop, NULL, 0, NULL);
   return hMainWnd;
}

//
//--------------- RAPI --------------------
//
bool ConnectToDevice()
{
   RAPIINIT ri;
   ri.heRapiInit = CreateEvent(NULL, TRUE, FALSE, NULL);
   ri.cbSize = sizeof(ri);

   CeRapiInitEx(&ri);
   WaitForSingleObject(ri.heRapiInit, 10000);

   return (ri.hrRapiInit == S_OK);
}

struct FolderParam
{
   LPCE_FIND_DATA data;
   DWORD count;
   LPWSTR selected;
};

static LPWSTR fpSelected;
HICON fdIcon;
void LoadList(HWND hDlg, FolderParam *param)
{
   HWND hList = GetDlgItem(hDlg, IDC_FOLDER_LIST);
   for( DWORD i=0; i<param->count; i++ )
      SendMessageW(hList, LB_ADDSTRING, 0, (LPARAM)param->data[i].cFileName);
}

void FolderDlgResize(HWND hDlg)
{
   const int offset = 5;
   HWND hList = GetDlgItem(hDlg, IDC_FOLDER_LIST);
   WORD wdh, hgh;
   RECT rc;
   LONG bwdh, bhgh, buttonTop;
   HWND hb = GetDlgItem(hDlg, IDOK);

   GetClientRect(hDlg, &rc);
   wdh = (WORD)(rc.right-rc.left);
   hgh = (WORD)(rc.bottom-rc.top);
   
   GetWindowRect(hb, &rc);
   bwdh = (rc.right - rc.left);
   bhgh = (rc.bottom - rc.top);

   buttonTop = hgh - bhgh - offset;

   SetWindowPos(hList, NULL, offset, offset, wdh - offset*2, buttonTop - 2*offset, 
      SWP_NOZORDER|SWP_NOOWNERZORDER);
   
   MoveWindow(hb, wdh - offset - bwdh, buttonTop, bwdh, bhgh, TRUE);
   MoveWindow(GetDlgItem(hDlg, IDCANCEL), offset, buttonTop, bwdh, bhgh, TRUE);
}

BOOL CALLBACK FolderDlgProc(HWND hwndDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      {
         fdIcon = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_ICON1));
         SendMessage(hwndDlg, WM_SETICON, ICON_BIG, (LPARAM)fdIcon);

         LoadList(hwndDlg, (FolderParam*)lParam);
         fpSelected = ((FolderParam*)lParam)->selected;
         FolderDlgResize(hwndDlg);
         return TRUE;
      }
      break;

   case WM_DESTROY:
      DestroyIcon(fdIcon);
      break;

   case WM_COMMAND:
      switch( LOWORD(wParam) )
      {
      case IDCANCEL:
         EndDialog(hwndDlg, IDCANCEL);
         break;

      case IDC_FOLDER_LIST:
         if( HIWORD(wParam) != LBN_DBLCLK )
            break;
         // fall throught
      case IDOK:
         {
            HWND hList = GetDlgItem(hwndDlg, IDC_FOLDER_LIST);
            int selected = SendMessage(hList, LB_GETCURSEL, 0, 0);
            if( selected >= 0 )
            {
               SendMessageW(hList, LB_GETTEXT, selected, (LPARAM)fpSelected);
               EndDialog(hwndDlg, IDOK);
            } else
               EndDialog(hwndDlg, IDCANCEL);
            break;
         }
      }
      break;
   case WM_SIZE:
      FolderDlgResize(hwndDlg);
      break;
   default:
      break;
   }
   return 0;
}

bool PointDestFolder(wchar_t *buf, int cb)
{
   return (CeGetSpecialFolderPath(CSIDL_PROGRAM_FILES, cb, buf) > 0);
/*
   bool retVal = false;
   DWORD count;
   LPCE_FIND_DATA data;

   CeFindAllFiles(L"\\*.*", FAF_NAME|FAF_FOLDERS_ONLY|
      FAF_ATTRIB_NO_HIDDEN|FAF_NO_HIDDEN_SYS_ROMMODULES, &count, &data);

   FolderParam param;
   param.count = count;
   param.data = data;
   param.selected = buf+1;
   *buf = L'\\';

   int ret = DialogBoxParam(hInstance, MAKEINTRESOURCE(IDD_FOLDER_DIALOG), NULL, (DLGPROC)FolderDlgProc, (LPARAM)&param);
   CeRapiFreeBuffer(data);
   return (ret == IDOK);
   */
}

class ProgressWindow
{
public:
   ProgressWindow(HANDLE stopEvent)
   {
      this->stopEvent = stopEvent;
      hWnd = NULL;
   }

   ~ProgressWindow()
   {
      TerminateThread(hThread, 0);
      CloseHandle(hThread);
      DestroyWindow(hWnd);
      DestroyIcon(hIcon);
   }

   bool Create(HWND hParent, const wchar_t *text, int max)
   {
      this->text = text;
      this->max = max;
      this->hParent = hParent;

      hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)MessageLoop, (LPVOID)this, 0, NULL);

      return true;
   }

   void SetText(const wchar_t *text, int max)
   {
      this->text = text;
      this->max = max;

      SetText();
  }

   void SetPos(int pos)
   {
      HWND hp = GetDlgItem(hWnd, IDC_PROGRESS);
      SendMessage(hp, PBM_SETPOS, pos, 0);
   }

   static BOOL CALLBACK DlgProc(HWND hwndDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
   {
      switch( uMsg )
      {
      case WM_INITDIALOG:
         {
            ProgressWindow *dlg = (ProgressWindow*)lParam;
            SetWindowLong(hwndDlg, GWL_USERDATA, lParam);
            SendMessage(hwndDlg, WM_SETICON, ICON_BIG, (LPARAM)dlg->hIcon);
            dlg->hWnd = hwndDlg;
            dlg->SetText();
            SetForegroundWindow(hwndDlg);
         }
         break;

      case WM_COMMAND:
         {
            ProgressWindow *dlg = (ProgressWindow*)GetWindowLong(hwndDlg, GWL_USERDATA);
            switch(LOWORD(wParam))
            {
            case IDCANCEL:
               SetEvent(dlg->stopEvent);
               break;
            }
         }
         break;
      }
      return FALSE;
   }

   static DWORD WINAPI MessageLoop(ProgressWindow *wnd)
   {
      wnd->hIcon = LoadIcon(hInstance, MAKEINTRESOURCE(IDI_ICON1));
      wnd->hWnd = CreateDialogParam(hInstance, MAKEINTRESOURCE(IDD_PROGRESS_DIALOG), 
         wnd->hParent, DlgProc, (LPARAM)wnd);

      ShowWindow(wnd->hWnd, SW_SHOW);
      UpdateWindow(wnd->hWnd);

      MSG msg;
      while( GetMessage(&msg, NULL, 0, 0) )
      {
         if( IsDialogMessage(wnd->hWnd, &msg) == TRUE )
            continue;

         TranslateMessage(&msg);
         DispatchMessage(&msg);
      }

      return 0;
   }

protected:
   void SetText()
   {
      SetWindowText(GetDlgItem(hWnd, IDC_PROGRESS_TEXT), text.c_str());

      HWND hp = GetDlgItem(hWnd, IDC_PROGRESS);
      SendMessage(hp, PBM_SETRANGE32, 0, max);
      SendMessage(hp, PBM_SETPOS, 0, 0);
   }


protected:
   HANDLE stopEvent;
   HANDLE hThread;
   HWND hWnd, hParent;
   HICON hIcon;
   int max;
   wstring text;
};

bool CopyToDevice(const vector<wstring> &files)
{
   bool retVal = true;
   wchar_t base[MAX_PATH];

   if( PointDestFolder(base, sizeof(base)/sizeof(base[0])) == false )
      return true;
   //*base = L'\0';
   
   HANDLE stopEvent = CreateEvent(NULL, TRUE, FALSE, NULL);
   ProgressWindow pw(stopEvent);
   bool created = false;
   int i;

   for( i=0; i<(int)files.size(); i++ )
   {
      const wchar_t *localName = files[i].c_str();
      localName = wcsrchr(localName, L'\\');
      wstring name(base);
      name += localName;

      HANDLE dest = CeCreateFile(name.c_str(), GENERIC_WRITE, 0, NULL, CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
      HANDLE src;
      if( dest == INVALID_HANDLE_VALUE )
      {
         wchar_t buf[1000];
         wsprintfW(buf, L"Не могу создать файл %s на КПК", name.c_str());
         MessageBoxW(GetActiveWindow(), buf, L"Ошибка", MB_OK | MB_ICONSTOP);
      }
      src = CreateFileW(files[i].c_str(), GENERIC_READ, 0, NULL, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
      if( src == INVALID_HANDLE_VALUE )
      {
         wchar_t buf[1000];
         wsprintfW(buf, L"Не могу открыть файл %s", files[i].c_str());
         MessageBoxW(GetActiveWindow(), buf, L"Ошибка", MB_OK | MB_ICONSTOP);
      }

      if( dest != INVALID_HANDLE_VALUE && src != INVALID_HANDLE_VALUE )
      {
         BYTE buf[10000];
         DWORD size = GetFileSize(src, NULL);

         wstring text(L"Копируется файл ");
         text += localName+1;
         if( created == false )
         {
            created = true;
            pw.Create(NULL, text.c_str(), size);
         } else
            pw.SetText(text.c_str(), size);

         DWORD cp = 0;
         while(true)
         {
            if( WaitForSingleObject(stopEvent, 0) == WAIT_OBJECT_0 )
            {
               if( MessageBox(NULL, _T("Отменить копирование?"), _T("Вопрос"), 
                  MB_YESNO|MB_ICONQUESTION|MB_SETFOREGROUND|MB_TOPMOST) == IDYES )
               {
                  CeCloseHandle(dest);
                  dest = INVALID_HANDLE_VALUE;
                  CeDeleteFile(name.c_str());
                  break;
               }
            }
            ResetEvent(stopEvent);

            DWORD readed, writed;
            ReadFile(src, buf, sizeof(buf), &readed, NULL);
            if( readed <= 0 )
               break;

            CeWriteFile(dest, buf, readed, &writed, NULL);

            cp += writed;
            pw.SetPos(cp);
            if( writed != readed )
            {
               retVal = false;
               break;
            }
         }
      } else
         retVal = false;

      if( src != INVALID_HANDLE_VALUE ) CloseHandle(src);
      if( dest != INVALID_HANDLE_VALUE ) CeCloseHandle(dest);
   }

   if( retVal )
   {
      for( i=0; i<(int)files.size(); i++ )
      {
         const wchar_t *localName = files[i].c_str();
         localName = wcsrchr(localName, L'\\');
         wstring name(L"\"");
         name += base;
         name += localName;
         name += L"\"";

         PROCESS_INFORMATION pi;
         CeCreateProcess(L"wceload.exe", name.c_str(), NULL, NULL, FALSE, 0, NULL, NULL, NULL, &pi);
      }
   }

   CloseHandle(stopEvent);
   return retVal;
}

void CloseConnection()
{
   CeRapiUninit();
}
//
// ---------------------- Copy cabs -------------------
//
static void CopyCabsToDevice(LPCTSTR iniFile)
{
   INITCOMMONCONTROLSEX init;
   init.dwSize = sizeof(init);
   init.dwICC = ICC_WIN95_CLASSES;
   InitCommonControlsEx(&init);

   vector<wstring> cabFiles;
   GetCabFiles(iniFile, &cabFiles);
   if( cabFiles.size() == 0 )
   {
      TCHAR msg[1000];
      _tcscpy(msg, _T("Не могу найти название установочных файлов в\n"));
      _tcscat(msg, iniFile);
      MessageBox(NULL, msg, _T("Ошибка"), MB_ICONSTOP|MB_OK|MB_SETFOREGROUND|MB_TOPMOST);
   } else
      InstallOnDevice(cabFiles);
}

static void InstallOnDevice(const vector<wstring> &cabFiles)
{
   if( ConnectToDevice() )
   {
      if( CopyToDevice(cabFiles) )
      {
         MessageBox(NULL, _T("Программа успешно скопирована на КПК.\n")
            _T("Следите за установкой программы на устройстве"), 
            _T("Информация"), MB_OK);
      }
   } else
   {
      MessageBox(NULL, _T("Не могу соединиться с КПК"), _T("Ошибка"), MB_ICONSTOP|MB_OK|MB_SETFOREGROUND|MB_TOPMOST);
   }

   CloseConnection();
}

static BOOL CALLBACK DlgP(HWND hwndDlg, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   switch( uMsg )
   {
   case WM_INITDIALOG:
      SendMessage(GetDlgItem(hwndDlg, IDC_PROGRESS), PBM_SETPOS, 50, 0);
      SetForegroundWindow(hwndDlg);
      break;
   }
   return FALSE;
}

int WINAPI _tWinMain(HINSTANCE hInst, HINSTANCE, LPTSTR cmdLine, int nShowCmd)
{
   OSVERSIONINFO vi = {0};
   vi.dwOSVersionInfoSize = sizeof(vi);
   GetVersionEx(&vi);
   runOnVista = (vi.dwMajorVersion >= 6);

   hInstance = hInst;
   if( *cmdLine == _T('\0') )
   {
      if( AskFileName(fileName, sizeof(fileName)/sizeof(fileName[0])) == false )
         return 0;
   } else
   {
      if( *cmdLine == _T('\"') )
      {
         LPCTSTR src = cmdLine+1;
         LPTSTR dest = fileName;
         while( *src != _T('\"') && *src != _T('\0') )
            *dest++ = *src++;
         *dest = _T('\0');
      } else
         _tcscpy(fileName, cmdLine);
   }

   ConvertToFullPath(fileName);
   if( IsFileExist(fileName) == false )
   {
      TCHAR msg[1000];
      _tcscpy(msg, _T("Не могу найти файл "));
      _tcscat(msg, fileName);
      MessageBox(NULL, msg, _T("Ошибка"), MB_ICONSTOP | MB_OK | MB_SETFOREGROUND | MB_TOPMOST);
      return 0;
   }

   Install(fileName);
   return 0;
}