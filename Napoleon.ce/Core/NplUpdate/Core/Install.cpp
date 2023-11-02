/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Установка обновлений
 *
 * ert   24/10/2009   creating
 */
#include "stdafx.h"
#include "NplUpdate.h"
#include "States.h"
#include <tlhelp32.h>
#include <AES.h>
#include <Compress.h>
#include "Util.h"
#include <StdFuncs.h>
#include <algorithm>

DecodeState decodeState;
Restore restoreState;
Install installState;
MoveTo moveTo;
RemoveProc removeProc;


DecodeState::DecodeState() : FNState(L"decode") {}

extern Key uploadKey;
State* DecodeState::Execute()
{
   State* next = NULL;
   std::wstring dFile(fileName + L".dcd");

   FILE *r = _wfopen(fileName.c_str(), L"rb");
   FILE* df = _wfopen(dFile.c_str(), L"wb");

   if( r == NULL || df == NULL )
   {
      //errorState.SetText(L"Ошибка открытия файлов для декодрования. Повторить загрузку обновления?");
      //next = &errorState;

      if( df ) fclose(df);
      if( r ) fclose(r);

      updateState.Clear();   
      next = &updateState;
   } else
   {

      DWORD offset;
      fread(&offset, 1, sizeof(offset), r);
      fseek(r, offset, SEEK_SET);

      bool res = AESDecodeFile(df, r, uploadKey);
      fclose(df);
      fclose(r);

      if( res )
      {
         DeleteFile(fileName.c_str());

         restoreState.SetFileName(dFile);
         next = &restoreState;
      } else
      {
         //errorState.SetText(L"Ошибка при расшифровки файлов обновления. Повторить загрузку обновления?");
         //next = &errorState;

         updateState.Clear();   
         next = &updateState;
      }
   }

   return next;
}

//
//--------------------------------------------- Restore --------------------------------------------
//
Restore::Restore() : FNState(L"restore") {}

static bool CopyString(char *buf, DWORD bufSize, FILE *rd)
{
   char *ep = buf + bufSize;
   while( buf < ep )
   {
      if( feof(rd) ) return false;
      char sym = fgetc(rd);

      *buf = sym;
      if( sym == '\0' )
         break;
      buf++;
   }

   return (buf < ep);
}

static void CheckFolders(const std::wstring& folder, const wchar_t* fileName)
{
   std::wstring curFolder(folder);
   std::wstring fn(fileName);

   int sp = 0;
   while( true )
   {
      int pos = fn.find(L'\\', sp);
      if( pos < 0 ) break;
      curFolder.append(fn.substr(sp, pos - sp + 1));
      CreateDirectory(curFolder.c_str(), NULL);
      sp = pos + 1;
   }
}

static bool Decompress(const std::wstring& folder, const wchar_t* fileName, DWORD fileSize, FILE *rd)
{
   std::wstring fn(folder);
   fn.append(fileName);

   CheckFolders(folder, fileName);
   FILE *wr = _wfopen(fn.c_str(), L"wb");
   bool res = DecompressFile(rd, wr, fileSize);
   fclose(wr);

   return res;
}

static bool RestoreFiles(std::vector<std::wstring> *files, FILE *rd, const std::wstring &destFolder)
{
   DWORD dataOffset;
   DWORD headOffset;
   WORD  fileCount;

   fseek(rd, 0, SEEK_SET);
   fread(&dataOffset, sizeof(dataOffset), 1, rd);
   fread(&fileCount, sizeof(fileCount), 1, rd);
   headOffset = ftell(rd);

   bool done = false;
   DWORD bufSize = dataOffset - sizeof(DWORD) * 2 - sizeof(WORD);
   char* buf = (char*)malloc(bufSize);
   wchar_t* fileNameW = (wchar_t*)malloc(bufSize * sizeof(wchar_t));
   for( ; fileCount > 0; fileCount-- )
   {
      done = false;

      DWORD fileSize;
      fseek(rd, headOffset, SEEK_SET);
      fread(&fileSize, sizeof(fileSize), 1, rd);
      if( !CopyString(buf, bufSize, rd) )
         break;

      headOffset = ftell(rd);
      fseek(rd, dataOffset, SEEK_SET);

      mbstowcs(fileNameW, buf, strlen(buf)+1);
      if( !Decompress(destFolder, fileNameW, fileSize, rd) )
         break;

      files->push_back(fileNameW);

      dataOffset = ftell(rd);
      done = true;
   }

   free(buf);
   free(fileNameW);
   return (fileCount == 0 && done);
}

State* Restore::Execute()
{
   State* next = NULL;
   FILE* rd = _wfopen(fileName.c_str(), L"rb");
   if( rd != NULL )
   {
      std::vector<std::wstring> files;
      std::wstring folder;

      app.GetUpdateFolder(&folder);
      bool res = RestoreFiles(&files, rd, folder);
      fclose(rd);

      if( res )
      {
         installState.SetUpdatedFiles(files);
         DeleteFile(fileName.c_str());
         next = &installState;
      }
   } else
   {
      errorState.SetText(L"Ошибка при распаковки файлов обновления. Повторить загрузку обновления?");
      next = &errorState;
   }

   return next;
}

//
//--------------------------------------------- Install --------------------------------------------
//
Install::Install() : State(L"install") {}

bool Install::Load(FILE* rd)
{
   bool ret = false;
   WORD size;
   if( fread(&size, sizeof(size), 1, rd) == 1 )
   {
      ret = true;
      while( size-- > 0 )
      {
         std::wstring val;
         if( !ReadString(rd, &val) )
         {
            Log("Load files error");
            ret = false;
            break;
         }

         files.push_back(val);
      }
   }

   return ret;
}

void Install::Write(FILE* wr) const
{
   WORD size = files.size();
   fwrite(&size, sizeof(size), 1, wr);

   std::vector<std::wstring>::const_iterator i = files.begin();
   for( ; i != files.end(); i++ )
   {
      WriteString(wr, (*i));
   }
}

void Install::SetUpdatedFiles(const std::vector<std::wstring>& files)
{
   this->files = files;
}

bool Install::UpdateSelf(std::wstring* fileName)
{
   bool ret = false;

   // update install program
   std::wstring fn;
   app.GetProgName(&fn);

   const wchar_t *p = fn.c_str() + app.Config().rootFolder.size();
   if( *p == L'\\' ) p++;
   fileName->assign(p);

   std::vector<std::wstring>::iterator i = find(files.begin(), files.end(), *fileName);
   if( i != files.end() )
   {
      std::wstring folder;
      app.GetUpdateFolder(&folder);

      fileName->insert(0, folder);
      ret = IsFileExist(fileName->c_str());
      if( ret )
         files.erase(i);
   }

   return ret;
}

static bool FindInProcs(const std::wstring& fileName, DWORD* procID, std::wstring* procName)
{
   bool retVal = false;
   wchar_t buf[MAX_PATH];

   HANDLE h = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
   if( h != INVALID_HANDLE_VALUE )
   {
      PROCESSENTRY32 pi;
      pi.dwSize = sizeof(pi);
      BOOL doing = Process32First(h, &pi);
      while( doing != FALSE )
      {
         if( wcsicmp(fileName.c_str(), pi.szExeFile) == 0 )
         {
            *procID = pi.th32ProcessID;
            GetModuleFileName((HMODULE)pi.th32ProcessID, buf, MAX_PATH);
            procName->assign(buf);

            retVal = true;
            break;
         }
         doing = Process32Next(h, &pi);
      }
      CloseToolhelp32Snapshot(h);
   }

   return retVal;
}

static bool FindInModules(const std::wstring& fileName, DWORD* procID, std::wstring* procName)
{
   bool retVal = false;
   wchar_t buf[MAX_PATH];

   HANDLE h = CreateToolhelp32Snapshot(TH32CS_SNAPPROCESS, 0);
   if( h != INVALID_HANDLE_VALUE )
   {
      PROCESSENTRY32 pi;
      pi.dwSize = sizeof(pi);
      BOOL doing = Process32First(h, &pi);
      while( doing != FALSE && !retVal )
      {
         HANDLE h1 = CreateToolhelp32Snapshot(TH32CS_SNAPMODULE, pi.th32ProcessID);
         if( h1 != INVALID_HANDLE_VALUE )
         {
            MODULEENTRY32 me;
            me.dwSize = sizeof(me);
            BOOL mdo = Module32First(h1, &me);
            while( mdo != FALSE )
            {
               if( wcsicmp(fileName.c_str(), me.szModule) == 0 )
               {
                  *procID = pi.th32ProcessID;
                  GetModuleFileName((HMODULE)pi.th32ProcessID, buf, MAX_PATH);
                  procName->assign(buf);

                  retVal = true;
                  break;
               }
               mdo = Module32Next(h1, &me);
            }
            CloseToolhelp32Snapshot(h1);
         }
         doing = Process32Next(h, &pi);
      }
      CloseToolhelp32Snapshot(h);
   }

   return retVal;
}

static bool IsProgramLoaded(DWORD* procID, std::wstring* procName, const std::vector<std::wstring>& updateFiles)
{
   const std::wstring *fName = NULL;
   std::vector<std::wstring>::const_iterator i = updateFiles.begin();
   for( ; i != updateFiles.end(); i++ )
   {
      const wchar_t *p = i->c_str() + i->size() - 4;
      if( wcsicmp(p, L".exe") == 0 )
      {
         fName = &(*i);
         break;
      }
   }
   bool res;
   if( fName != NULL )
   {
      res = FindInProcs(*fName, procID, procName);
   } else
      res = FindInModules(updateFiles.at(0), procID, procName);
      
   return res;
}

static BOOL CALLBACK CloseWindowsProc(HWND hWnd, LPARAM procID)
{
   DWORD pID;

   GetWindowThreadProcessId(hWnd, &pID); 
   if( pID == procID )
   {
      PostMessage(hWnd, WM_CLOSE, 0, 0);
   }

   return TRUE;
}

static void TerminateProc(DWORD procID)
{
   HANDLE hProc = OpenProcess(0, FALSE, procID);

   EnumWindows(CloseWindowsProc, procID);
   if( WaitForSingleObject(hProc, 1000) != WAIT_OBJECT_0 )
      TerminateProcess(hProc, 0);

   CloseHandle(hProc);
}

static void Commit(const std::vector<std::wstring>& files)
{
   std::vector<std::wstring>::const_iterator i = files.begin();
   for( ; i != files.end(); i++ )
      DeleteFile(i->c_str());
}

static void RollOut(const std::vector<std::wstring>& files)
{
   std::vector<std::wstring>::const_iterator i = files.begin();
   for( ; i != files.end(); i++ )
   {
      const std::wstring& src = (*i);
      std::wstring dest = src.substr(0, src.size()-4);
      DeleteFile(dest.c_str());
      MoveFile(src.c_str(), dest.c_str());
   }
}

static bool MoveFiles(const std::vector<std::wstring>& updateFiles)
{
   std::vector<std::wstring>::const_iterator i = updateFiles.begin();

   std::wstring appFolder, updFolder;
   app.GetUpdateFolder(&updFolder);
   appFolder = app.Config().rootFolder;

   bool ret = true;

   std::vector<std::wstring> updated;

   for( ; i != updateFiles.end(); i++ )
   {
      std::wstring dest(appFolder + (*i)), src(updFolder + (*i));
      std::wstring update(dest + L".tmp");

      if( MoveFile(dest.c_str(), update.c_str()) == FALSE ||
         MoveFile(src.c_str(), dest.c_str()) == FALSE )
      {
         ret = false;
         break;
      }

      updated.push_back(update.c_str());
   }

   if( ret ) Commit(updated);
   else RollOut(updated);

   return ret;
}

State* Install::Execute()
{
   State* next = NULL;
   std::wstring name;
   if( UpdateSelf(&name) )
   {
      moveTo.SetUpdatedFiles(files);
      moveTo.RunProc(name);
      next = &moveTo;
   } else
   {
      bool needStart = false, retVal = true;
      std::wstring procName;
      DWORD procID;

      if( IsProgramLoaded(&procID, &procName, files) )
      {
         retVal = false;
         wchar_t text[] = L"<html><body>Обновление программы загружено. Для установки необходимо перезапустить программу.<p>"
            L"Перезапустить ее сейчас?</body></html>";

         if( app.Alert(text, IDC_DOWNLOAD) )
         {
            retVal = true;
            needStart = true;
            TerminateProc(procID);
         }
      }

      if( retVal )
      {
         if( MoveFiles(files) )
         {
            if( needStart )
            {
               PROCESS_INFORMATION pi;
               CreateProcess(procName.c_str(), NULL, NULL, NULL, FALSE, CREATE_NEW_CONSOLE, NULL, NULL, NULL, &pi);
            }

            app.CommitUpdate();
         } else
         {
            errorState.SetText(L"Не могу обновить программу. Повторить загрузку обновления?");
            next = &errorState;
         }
      } else
         next = this;
   }

   return next;
}

MoveTo::MoveTo() : Install(L"move to")
{
}

bool MoveTo::Load(FILE* rd)
{
   if( Install::Load(rd) )
   {
      bool ret = ReadString(rd, &fileName);
      if( !ret )
         Log("Load fileName error");
      return ret;
   }
   return false;
}

void MoveTo::Write(FILE* wr) const
{
   Install::Write(wr);
   WriteString(wr, fileName);
}

void MoveTo::RunProc(const std::wstring& fn)
{
   app.GetProgName(&fileName);
   app.SaveConfig(this);

   PROCESS_INFORMATION pi;
   std::wstring cmd(L"\"");
   cmd += app.ConfigFileName();
   cmd += L"\"";

   if( !CreateProcess(fn.c_str(), cmd.c_str(), NULL, NULL, FALSE, CREATE_NEW_CONSOLE, NULL, NULL, NULL, &pi) )
   {
      Log("Error run prog %d", GetLastError());
   }

   CloseHandle(pi.hThread);
   CloseHandle(pi.hProcess);
}

State* MoveTo::Execute()
{
   std::wstring fn;
   app.GetProgName(&fn);

   Log("Move To run");
   if( CopyFile(fn.c_str(), fileName.c_str(), FALSE) )
   {
      //app.Alert(L"Test", IDC_UPDATE);

      removeProc.SetUpdatedFiles(files);
      removeProc.RunProc(fileName);

      Log("Copy NplUpdate done (%d)", (int)removeProc.IsSelfUpdate());

      return &removeProc;
   } else
   {
      Log("Copy Error %d", GetLastError());

      errorState.SetText(L"Ошибка при копировании программы обновления. Повторить загрузку обновления?");
      return &errorState;
   }
}

RemoveProc::RemoveProc() : MoveTo(L"remove proc")
{
}

State* RemoveProc::Execute()
{
   if( DeleteFile(fileName.c_str()) )
   {
      installState.SetUpdatedFiles(files);
      return &installState;
   } else
   {
      errorState.SetText(L"Ошибка при удалении программы обновления. Повторить загрузку обновления?");
      return &errorState;
   }
}

