/*
 * Copyright (C), 2009-2010, Денис Мосягин
 *
 * Программа обновления
 *    параметры запуска
 *     RunArgs - последний аргумент
 *     UpdateFile <ИмяФайлаОбновления> DestFolder <КорневаяПапка> LogFile <LogFile> MutexName <ИмяМутекса> RunArgs <СтрокаЗапускаСервера>
 *
 * Даже если не получилось обновление, необходимо запустить сервер
 *
 * ert   19/01/2010   creating
 */

#include "stdafx.h"
#include <updpacket.h>
#include <string>
#include <AtlConv.h>
#include <io.h>
#include <direct.h>
#include <varargs.h>
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

std::string logFile;

// append data
static void WriteLog(const char* prefix, const char* msg, ...)
{
   if( !logFile.empty() )
   {
      va_list args;
      SYSTEMTIME st;
      GetLocalTime(&st);

      va_start(args, msg);

      FILE *file = fopen(logFile.c_str(), "at");
      if( file != NULL )
      {
         fprintf(file, "%d.%02d.%02d %02d:%02d:%02d\t", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
         if( prefix != NULL )
         {
            fputs(prefix, file);
            fputs(": ", file);
         }

         vfprintf(file, msg, args);
         fputs("\n", file);
         fclose(file);
      }
   }
}

class ReadFileBinary : public IBinary
{
public:
   ReadFileBinary(const char* _fileName) : fileName(_fileName), bytes(NULL)
   {
   }
   ~ReadFileBinary() { Close(); }

   virtual void Assign(Binary* b) {}

   virtual DWORD Size() const
   {
      if( bytes == NULL ) Load();
      return bytes->Size();
   }

   virtual const BYTE* Bytes() const
   {
      if( bytes == NULL ) Load();
      return *bytes;
   }

   virtual void Close()
   {
      if( bytes != NULL )
      {
         delete bytes;
         bytes = NULL;
      }
   }

protected:
   void Load() const
   {
      if( bytes != NULL ) delete bytes;
      bytes = new Binary();

      FILE* f = fopen(fileName.c_str(), "rb");
      if( f != NULL )
      {
         DWORD size = _filelength(_fileno(f));
         if( size > 0 )
         {
            BYTE *b = bytes->Alloc(size);
            fread(b, size, sizeof(BYTE), f);
         }
         fclose(f);
      }
   }

   std::string fileName;
   mutable Binary *bytes;
};


static TCHAR UPDATE_ARG[] = _T("--update_prog");
static TCHAR RESUME_ARG[] = _T("--resume_update");
static const char UPDATE_MUTEX[] = "GrUpdMutex";

HANDLE ghMutex = INVALID_HANDLE_VALUE;

static void RunProgram(const char* args);
static void RunProgram(const wchar_t* args);

static void WriteString(FILE* file, const std::string& str)
{
   WORD len = (WORD)str.size();
   fwrite(&len, sizeof(len), 1, file);
   fwrite(str.c_str(), sizeof(char), len, file);
}

static void WriteString(FILE* file, const char* str)
{
   WORD len = (WORD)strlen(str);
   fwrite(&len, sizeof(len), 1, file);
   fwrite(str, sizeof(char), len, file);
}

static void WriteString(FILE* file, const wchar_t* wstr)
{
   WORD len = (WORD)wcslen(wstr);
   char* str = (char*)alloca(len+1);
   len = WideCharToMultiByte(CP_ACP, 0, wstr, len, str, len+1, NULL, NULL);

   fwrite(&len, sizeof(len), 1, file);
   fwrite(str, sizeof(char), len, file);
}

static bool ReadString(FILE* file, std::string* str)
{
   WORD len;
   if( fread(&len, sizeof(len), 1, file) != 1 ) return false;

   char *buf = (char*)alloca(len);
   if( fread(buf, sizeof(char), len, file) != len ) return false;

   str->assign(buf, len);
   return true;
}

static LPCTSTR ParamStartPtr(LPCTSTR params)
{
   while( *params == _T(' ') ) params++;

   return (*params != _T('\0')) ? params : NULL;
}

static LPCTSTR ParamEndPtr(LPCTSTR params)
{
   bool quoted = (*params == _T('"'));

   params++;
   if( !quoted )
   {
      LPCTSTR ep = _tcschr(params, _T(' '));
      return (ep) ? ep : _tcschr(params, _T('\0'));
   }

   return _tcschr(params, _T('"'));
}

void Assign(std::string *str, const wchar_t* sp, const wchar_t* ep)
{
   if( *sp == L'"' ) sp++;
   unsigned len = (unsigned)(ep - sp);
   char *buf = (char*)alloca(len * 2);

   len = WideCharToMultiByte(CP_ACP, 0, sp, len, buf, len*2, NULL, NULL);
   str->assign(buf, len);
}

void Assign(std::string *str, const char* sp, const char* ep)
{
   if( *sp == '"' ) sp++;
   str->assign(sp, ep-sp);
}


static bool SaveState(const std::string& fileName, const std::string& destFolder, const std::string& runArgs, UpdatePacket& up)
{
   bool res = false;

   FILE *wr = fopen(fileName.c_str(), "wb");
   if( wr )
   {
      WriteString(wr, destFolder);
      WriteString(wr, logFile);
      WriteString(wr, runArgs);
      up.Save(wr);

      fclose(wr);

      res = true;
   }

   return res;
}

static bool LoadState(LPCTSTR args, std::string& stateFile, std::string &destFolder, std::string &runArgs, UpdatePacket& updPacket)
{
   LPCTSTR sp, ep;

   sp = ParamStartPtr(args);
   if( sp == NULL )
      return false;

   ep = ParamEndPtr(sp);
   Assign(&stateFile, sp, ep);

   bool err = false;
   FILE *rd = fopen(stateFile.c_str(), "rb");
   if( rd != NULL )
   {
      err = !ReadString(rd, &destFolder);
      err = (err || !ReadString(rd, &logFile));
      err = (err || !ReadString(rd, &runArgs));
      err = (err || !updPacket.Load(rd));
      fclose(rd);
   }

   if( err )
      WriteLog("Ошибка обновления", "Ошибка загрузки файла состояния '%s'", stateFile.c_str());

   return !err;
}

void DoUpdate(const std::string &updFile, const std::string &destFolder, LPCTSTR runArgs)
{
   ReadFileBinary rfb(updFile.c_str());
   UpdatePacket up;
   std::string tempFolder;

   size_t pos = updFile.find_last_of('\\');
   tempFolder.assign(updFile.substr(0, pos+1) + ".temp\\");

   if( !up.DecodeHead(rfb) || !up.DecodeBody(rfb, tempFolder) ) 
   {
      WriteLog("Ошибка обновления", "не могу распаковать пакет обновления '%s' в папку '%s'", 
         updFile.c_str(), tempFolder.c_str());

      RunProgram(runArgs);
      return;
   }

   // найдем имя файла обновления в destFolder (может быть с путем)
   char fileName[MAX_PATH];
   GetModuleFileNameA(NULL, fileName, sizeof(fileName));
   const char *p = destFolder.c_str(), *pUpdFile = fileName;
   while( *pUpdFile == *p ) { pUpdFile++; p++; }
   if( *pUpdFile == '\\' ) pUpdFile++;

   if( up.HaveFile(pUpdFile) ) // update self
   {
      // save state (destFolder, runArgs, up)
      // run update prog in tempFolder UPDATE_ARG
      // exit

      std::string stateFile(tempFolder + "!state.upd");
      USES_CONVERSION;

      if( sizeof(TCHAR) != sizeof(char) )
         SaveState(stateFile, destFolder, W2A(runArgs), up);
      else
         SaveState(stateFile, destFolder, (const char*)runArgs, up);

      ghMutex = CreateMutexA(NULL, TRUE, UPDATE_MUTEX);

      std::string runProg("\"");
      runProg += tempFolder;
      runProg += pUpdFile;
      runProg += "\" ";
      if( sizeof(TCHAR) != sizeof(char) )
         runProg += W2A(UPDATE_ARG);
      else
         runProg += (const char*)UPDATE_ARG;
      runProg += " \"";
      runProg += stateFile;
      runProg += "\"";

      RunProgram(runProg.c_str());
   } else
   {
      std::string fail;
      if( !up.MoveFiles(destFolder, &fail) )
         WriteLog("Ошибка обновления", "не могу обновить файл '%s'", fail.c_str());
      else
         WriteLog("Информация обновления", "обновление прошло успешно");

      RunProgram(runArgs);
   }
}

static bool WaitProgramExit(const std::string& mutexName)
{
   HANDLE hMutex = CreateMutexA(NULL, FALSE, mutexName.c_str());
   if( hMutex == INVALID_HANDLE_VALUE )
   {
      WriteLog("Ошибка обновления", "Ошибка открытия мутекса '%s'", mutexName.c_str());
      return false;
   }

   bool canDo = true;
   DWORD res;
   if( GetLastError() == ERROR_ALREADY_EXISTS )
   {
      res = WaitForSingleObject(hMutex, INFINITE);
      canDo = (res == WAIT_OBJECT_0);
   }

   ReleaseMutex(hMutex);
   CloseHandle(hMutex);

   if( !canDo )
      WriteLog("Ошибка обновления", "Ошибка ожидания мутекса '%s' %X", mutexName.c_str(), res);

   return canDo;
}

static LPCTSTR ReadParam(std::string *value, LPCTSTR sp)
{
   sp = ParamStartPtr(sp);
   if( sp == NULL )
      return NULL;

   LPCTSTR ep = ParamEndPtr(sp);
   Assign(value, sp, ep);
   return (*ep) ? ep : NULL;
}

static LPCTSTR LoadArgs(LPCTSTR lpCmdLine, std::string &updFile, std::string &destFolder, std::string &mutexName, std::string &logFile)
{
   LPCTSTR runArgs = NULL;
   LPCTSTR sp;

   char buf[MAX_PATH];
   GetModuleFileNameA(NULL, buf, sizeof(buf)/sizeof(buf[0]));

   sp = GetCommandLine();

   while( sp != NULL && runArgs == NULL )
   {
      std::string paramName;
      sp = ReadParam(&paramName, sp);
      if( sp == NULL ) break;
      sp++;
      if( paramName.compare(buf) == 0 )
         continue;

      if( paramName.compare("UpdateFile") == 0 )
         sp = ReadParam(&updFile, sp);
      else if( paramName.compare("DestFolder") == 0 )
         sp = ReadParam(&destFolder, sp);
      else if( paramName.compare("LogFile") == 0 )
         sp = ReadParam(&logFile, sp);
      else if( paramName.compare("MutexName") == 0 )
         sp = ReadParam(&mutexName, sp);
      else if( paramName.compare("RunArgs") == 0 )
         runArgs = ParamStartPtr(sp);

      if( sp != NULL ) sp++;
   }
   return runArgs;
}

static void RunProgram(const wchar_t* args)
{
   USES_CONVERSION;
   WriteLog("Информация обновления", "Запуск программы '%s'", W2A(args));

   STARTUPINFOW si = {0};
   PROCESS_INFORMATION pi;

   si.cb = sizeof(si);
   LPWSTR pBuf = _wcsdup(args);

   if( CreateProcessW(NULL, pBuf, NULL, NULL, FALSE, CREATE_NEW_PROCESS_GROUP, NULL, NULL, &si, &pi) )
   {
      CloseHandle(pi.hThread);
      CloseHandle(pi.hProcess);
   } else
   {
      WriteLog("Ошибка обновления", "Ошибка %d при запуске программы '%s'", GetLastError(), W2A(args));
   }

   free(pBuf);
}

static void RunProgram(const char* args)
{
   WriteLog("Информация обновления", "Запуск программы '%s'", args);

   STARTUPINFOA si = {0};
   PROCESS_INFORMATION pi;

   si.cb = sizeof(si);
   LPSTR pBuf = _strdup(args);

   if( CreateProcessA(NULL, pBuf, NULL, NULL, FALSE, CREATE_NEW_PROCESS_GROUP, NULL, NULL, &si, &pi) )
   {
      CloseHandle(pi.hThread);
      CloseHandle(pi.hProcess);
   } else
   {
      WriteLog("Ошибка обновления", "Ошибка %d при запуске программы '%s'", GetLastError(), args);
   }

   free(pBuf);
}

void UpdateProg(LPCTSTR args)
{
   std::string stateFile, destFolder, runArgs;
   UpdatePacket up;
   USES_CONVERSION;

   if( !LoadState(args, stateFile, destFolder, runArgs, up) ) return;

   if( WaitProgramExit(UPDATE_MUTEX) )
   {
      ghMutex = CreateMutexA(NULL, TRUE, UPDATE_MUTEX);

      const std::string& filesFolder = up.FilesFolder();
      char buf[MAX_PATH], *name;
      const char *sp = filesFolder.c_str();

      GetModuleFileNameA(NULL, buf, sizeof(buf)/sizeof(buf[0]));
      name = buf;

      while( *name == *sp ) { name++; sp++; }
      if( *name  == '\\' ) name++;

      if( up.MoveFile(name, destFolder) )
      {
         SaveState(stateFile, destFolder, runArgs, up);

         runArgs = "\"";
         runArgs += destFolder;
         if( *runArgs.rbegin() != '\\' ) runArgs += '\\';
         runArgs += name;
         runArgs += "\" ";
         if( sizeof(TCHAR) != sizeof(char) )
            runArgs += W2A(RESUME_ARG);
         else
            runArgs += (const char*)RESUME_ARG;
         runArgs += " \"";
         runArgs += stateFile;
         runArgs += "\"";
      } else
         WriteLog("Ошибка обновления", "Не могу скопировать файл '%s' в папку '%s'", name, destFolder.c_str());
   } 

   RunProgram(runArgs.c_str());
}

void RemoveTempFiles(const char *dir)
{
   WIN32_FIND_DATAA fd;

   std::string fn(dir);
   if( *fn.rbegin() != '\\' ) fn += "\\";
   HANDLE h = FindFirstFileA((fn + "*.*").c_str(), &fd);
   if( h != INVALID_HANDLE_VALUE )
   {
      BOOL next;
      do
      {
         if( strcmp(fd.cFileName, ".") != 0 && strcmp(fd.cFileName, "..") != 0 )
         {
            if( fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY )
            {
               std::string dirname(fn);
               dirname += fd.cFileName;
               RemoveTempFiles(dirname.c_str());
            }
            else
               DeleteFileA((fn + fd.cFileName).c_str());
         }
         next = FindNextFileA(h, &fd);
      } while(next == TRUE);

      FindClose(h);
   }
   _rmdir(fn.c_str());
}

void ResumeUpdate(LPCTSTR args)
{
   std::string stateFile, destFolder, runArgs;
   UpdatePacket up;

   if( !LoadState(args, stateFile, destFolder, runArgs, up) ) return;

   bool res = false;
   if( WaitProgramExit(UPDATE_MUTEX) )
   {
      std::string fail;
      if( up.MoveFiles(destFolder, &fail) )
      {
         RemoveTempFiles(up.FilesFolder().c_str());
         DeleteFileA(stateFile.c_str());
         res = true;
      } else
         WriteLog("Ошибка обновления", "не могу обновить файл '%s'", fail.c_str());

   }

   if( res )
      WriteLog("Информация обновления", "обновление прошло успешно");
   RunProgram(runArgs.c_str());
}


int APIENTRY _tWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow)
{
   if( _tcsncmp(lpCmdLine, UPDATE_ARG, sizeof(UPDATE_ARG)/sizeof(UPDATE_ARG[0]) - 1) == 0 )
      UpdateProg(lpCmdLine + sizeof(UPDATE_ARG)/sizeof(UPDATE_ARG[0]));
   else if( _tcsncmp(lpCmdLine, RESUME_ARG, sizeof(RESUME_ARG)/sizeof(RESUME_ARG[0]) - 1) == 0 )
      ResumeUpdate(lpCmdLine + sizeof(RESUME_ARG)/sizeof(RESUME_ARG[0]));
   else
   {
      LPCTSTR runArgs;
      std::string updFile, destFolder, mutexName;

      runArgs = LoadArgs(lpCmdLine, updFile, destFolder, mutexName, logFile);

      WriteLog("Информация обновления", "Запуск программы обновления для '%s'", updFile.c_str());
      if( runArgs != NULL && WaitProgramExit(mutexName) )
         DoUpdate(updFile, destFolder, runArgs);
   }

   if( ghMutex != INVALID_HANDLE_VALUE )
   {
      ReleaseMutex(ghMutex);
      CloseHandle(ghMutex);
   }
   return 0;
}

