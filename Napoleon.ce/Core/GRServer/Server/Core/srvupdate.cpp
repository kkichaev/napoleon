/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Do Update
 *
 * ert   17/10/2009   creating
 */
#include "stdafx.h"
#include "sessobj.h"
#include "session.h"
#include "token.h"
#include "server.h"
#include "updpacket.h"
#include <ServerDefs.h>

#include "srvdata.h"
#include "objects.h"
#include "srvutility.h"
#ifdef UNIX
#else
#include "resource.h"
#include <io.h>
#endif
#include "dispatcher.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

const char SERVER_CATEGORY[]  = "grserver";
const char LICENSE_CATEGORY[] = "grlicense";
const char UPDATE_FILE_NAME[] = "upd.data";
static const wchar_t AUTH_UPD_ERROR[] = L"Не достаточно прав для загрузки обновления";
static const wchar_t UPD_FORMAT_ERROR[] = L"Неправильный формат пакета обновления";
static const wchar_t UPD_LOADED[] = L"Обнвление загружено на сервер";
static const wchar_t NO_UPDATE[] = L"Нет обновлений";
static const wchar_t UPDATE_SERVER_ERROR[] = L"Ошибка при обновлении сервера";

using namespace GRServer;

#ifdef UNIX
#else
struct Version
{
   Version() { w1 = w2 = w3 = w4 = 0; }

   unsigned w1, w2, w3, w4;

   bool operator < (const Version& src) const
   {
      if( w1 < src.w1 ) return true;
      if( w1 > src.w1 ) return false;

      if( w2 < src.w2 ) return true;
      if( w2 > src.w2 ) return false;

      if( w3 < src.w3 ) return true;
      if( w3 > src.w3 ) return false;

      if( w4 < src.w4 ) return true;
      return false;
   }

   void Read(const wchar_t* str)
   {
      swscanf(str, L"%d.%d.%d.%d", &w1, &w2, &w3, &w4);
   }

};
bool FindLastUpdate(std::wstring *version, std::string *fileName, const std::string& folder, const std::wstring& category)
{
   USES_CONVERSION;

   std::string tfld;
   FullFileName(&tfld, folder.c_str());

   std::wstring tname(A2W(tfld.c_str()));

   Version current;
   unsigned pos = (unsigned)category.find(L'/');
   size_t offset = category.size();
   if( pos != std::wstring::npos )
   {
      offset = pos;
      tname.append(category, 0, pos);
      version->assign(category, pos+1, -1);
      current.Read(category.substr(pos+1).c_str());
   } else
   {
      tname.append(category);
      version->clear();
   }
   tname += L"*.upd";

   WIN32_FIND_DATA data;
   HANDLE h = FindFirstFile(tname.c_str(), &data);
   if( h == INVALID_HANDLE_VALUE )
   {
      return false;
   }

   bool res = false;
   DWORD size = 0;
   do
   {
      const wchar_t* fn = data.cFileName;
      Version check;
      check.Read(fn + offset);
      if( current < check )
      {
         current = check;
         const wchar_t* ep = wcsrchr(fn, L'.');

         res = true;
         size = data.nFileSizeLow;
         version->assign(fn, offset, (ep) ? ep - fn - offset : -1);
         if( fileName != NULL ) fileName->assign(W2A(fn));
      }
   } while( FindNextFile(h, &data) );

   FindClose(h);

   //std::string tfname = folder + (*fileName);
   //FILE* f = fopen(tfname.c_str(), "rb");
   //if( f != NULL )
   //{
   //   DWORD dataOffset;
   //   fread(&dataOffset, sizeof(dataOffset), 1, f);
   //   fclose(f);

   //   size -= dataOffset;
   //}

   if( res )
   {
      wchar_t buf[20];
      wsprintfW(buf, L"/%d", size);
      version->append(buf);
   }
   return res;
}

Binary* LoadUpdatePacket(const std::string& fileName, DWORD offset, DWORD packetSize)
{
   bool retVal = false;

   Binary *dest = NULL;
   FILE *f = fopen(fileName.c_str(), "rb");
   if( f != NULL )
   {
      //DWORD dataOffset, fileSize;
      //fileSize = _filelength(_fileno(f));
      //fread(&dataOffset, sizeof(dataOffset), 1, f);
      //fileSize -= dataOffset;

      //offset += dataOffset;
      DWORD fileSize = _filelength(_fileno(f));
      if( fileSize > offset )
      {
         fseek(f, offset, SEEK_SET);
         dest = new Binary();
         BYTE *p = dest->Alloc(packetSize);
         DWORD cb = (DWORD)fread(p, sizeof(BYTE), packetSize, f);

         dest->ReduceSize(cb);
      }

      fclose(f);
   }
   return dest;
}

bool ReadToken(std::wstring* val, const wchar_t *sp, const wchar_t **ep)
{
   if( *sp == L'\0' ) return false;
   while( *sp != L'\0' )
   {
      if( *sp == L'/' )
      {
         sp++;
         break;
      }

      val->append(1, *sp);
      sp++;
   }

   *ep = sp;
   return true;
}

// param category/upd_version/offset/packet_size
Binary* LoadUpdatePacket(const std::wstring& param)
{
   USES_CONVERSION;

   Binary *dest = NULL;
   const wchar_t *p = param.c_str();

   std::wstring tval, cat, ver;
   DWORD offset, packetSize;

   if( ReadToken(&cat, p, &p) && ReadToken(&ver, p, &p) && ReadToken(&tval, p, &p) )
   {
      offset = _wtoi(tval.c_str());
      tval.clear();
      if( ReadToken(&tval, p, &p) )
      {
         packetSize = _wtoi(tval.c_str());

         std::string fileName;
         FullFileName(&fileName, gServer->GetConfig().UpdateFolder());
         fileName.append(W2A(cat.c_str()));
         fileName.append(W2A(ver.c_str()));
         fileName.append(".upd");
         dest = ::LoadUpdatePacket(fileName, offset, packetSize);
      }
   }

   return dest;
}
#endif

bool Session::CheckUpdate(const Member* category)
{
#ifdef UNIX
   return false;
#else
   if( category == NULL ) return false;
   outStream.Clear();

   std::wstring version;
   std::string fileName;
   std::string updateFolder(gServer->GetConfig().UpdateFolder());

   if( answer != NULL )
      answer->clear();

   if( !updateFolder.empty() && FindLastUpdate(&version, &fileName, updateFolder, (const std::wstring&)*category->str) )
      AddAnswer(true, version.c_str());
   else
      AddAnswer(false, NO_UPDATE);

   return true;
#endif
}

void Session::UpdateToStream(Binary* dest)
{
#ifdef UNIX
#else
   SessionObject* so = Build(L"UpdatePacket", false);

   Object *o = so->AddObject();
   Member mv;
   mv.binary = new MemoryBinary();
   mv.binary->Assign(dest);
   o->Assign(mv, L"packet");

   WriteToStream(*so, true);

   delete so;
#endif
}

bool Session::GetUpdate(const Member* category)
{
#ifdef UNIX
   return false;
#else
   if( category == NULL ) return false;
   outStream.Clear();

   if( answer != NULL )
      answer->clear();

   std::string updateFolder(gServer->GetConfig().UpdateFolder());
   if( !updateFolder.empty() )
   {
      size_t pos = category->str->find_last_of(L'/');
      if( pos != std::wstring::npos )
      {
         std::wstring version;
         std::wstring tval(category->str->substr(0, pos));
         std::string fileName;

         DWORD packetSize = _wtoi(category->str->substr(pos+1).c_str());
         if( FindLastUpdate(&version, &fileName, updateFolder, (const std::wstring&)*category->str) )
         {
            std::string tfld;
            FullFileName(&tfld, updateFolder.c_str());
            fileName.insert(0, tfld);
            Binary *dest = ::LoadUpdatePacket(fileName, 0, packetSize);
            if( dest != NULL )
            {
               AddAnswer(true, version.c_str());
               UpdateToStream(dest);
            }
         }
      }
   }

   if( outStream.Size() == 0 )
      AddAnswer(false, NO_UPDATE);
   else
      outStream.NeedCompress(false);

   return true;
#endif
}


bool Session::GetUpdatePacket(const Member* category)
{
#ifdef UNIX
   return false;
#else
   if( category == NULL ) return false;
   outStream.Clear();

   if( answer != NULL )
      answer->clear();

   Binary* dest = ::LoadUpdatePacket((const std::wstring&)*category->str);
   if( dest != NULL )
   {
      AddAnswer(true, L"");
      UpdateToStream(dest);
   }

   if( outStream.Size() == 0 )
      AddAnswer(false, NO_UPDATE);
   else
      outStream.NeedCompress(false);

   return true;
#endif
}

#ifdef UNIX
#else
/*
argumnts for run update program:

updateFile
destFolder
mutexName
[run args]
*/
/*
static bool UpdateServer(const std::string& updateFile, Session* session)
{
   USES_CONVERSION;
   CString logFile;

   char destFolder[MAX_PATH], *ep;
   GetModuleFileNameA(NULL, destFolder, sizeof(destFolder));
   ep = strrchr(destFolder, '\\');
   if( ep != NULL ) ep[1] = '\0';

   const char* params = gServer->ExecString();
   size_t paramLen = strlen(params);

   std::string upProg(destFolder);
   upProg += UPDATE_PROG;

   GetLogFileName(&logFile);

   const char RUN_ARGS[] = " UpdateFile \"%s\" DestFolder \"%s\" LogFile \"%s\" MutexName %s RunArgs %s";

   DWORD argLen = (DWORD)(updateFile.size() + logFile.size() + (ep - destFolder) +
      strlen(SERVER_MUTEX) + paramLen + sizeof(RUN_ARGS)/sizeof(RUN_ARGS[0]));

   char *pBuf = (char*)alloca(argLen);
   wsprintfA(pBuf, RUN_ARGS,  updateFile.c_str(), destFolder, W2A(logFile.c_str()), SERVER_MUTEX, params);

   bool res = false;
   STARTUPINFOA si = {0};
   PROCESS_INFORMATION pi;
   si.cb = sizeof(si);
   if( CreateProcessA(upProg.c_str(), pBuf, NULL, NULL, FALSE, CREATE_NEW_PROCESS_GROUP, NULL, NULL, &si, &pi) )
   {
      CloseHandle(pi.hThread);
      CloseHandle(pi.hProcess);

      session->SetNeedRestart();

      res = true;
   } else
      gServer->AddError(false, "Ошибка %d при старте программы обновления %s %s", GetLastError(), upProg.c_str(), pBuf);

   return res;
}
*/
static void CheckFolders(const std::string& fileName)
{
   std::string curFolder;
   std::string fn(fileName);

   size_t sp = 0;
   while( true )
   {
      size_t pos = fn.find('\\', sp);
      if( pos != std::string::npos ) break;
      curFolder.append(fn.substr(sp, pos - sp + 1));
      CreateDirectoryA(curFolder.c_str(), NULL);
      sp = pos + 1;
   }
}

static bool WriteUpdFile(const std::string& name, const BYTE* data, DWORD cb)
{
   bool done = false;

   CheckFolders(name);
   FILE *wr = fopen(name.c_str(), "wb");
   if( wr != NULL )
   {
      fwrite(data, 1, cb, wr);
      fclose(wr);
      done = true;
   }

   return done;
}
#endif

bool Session::LoadUpdatePacket()
{
   bool done = false;

   if( user->IsAdmin() == false )
   {
      AddAnswer(false, AUTH_UPD_ERROR);
      return false;
   }

   ServObject *updateObject = ack.at(0);
   Member* updateFile = (Member*)(*updateObject->at(0))[PACKET_MEMBER];
   const wchar_t *updMessage = NULL;

   if( updateFile != NULL )
   {
      UpdatePacket up;
      if( up.DecodeHead(*updateFile->binary) )
      {
         if( up.Category().compare(LICENSE_CATEGORY) == 0 )
         {
            Binary *license = up.DecodeFile(*updateFile->binary, 0);
            if( license != NULL )
            {
               done = ServerData::LicenseUpdate(*license);
               if( done )
                  dispatcher->Controller().RefreshUsers();

               delete license;
            }
         } else
         {
#ifdef UNIX
            updMessage = UPDATE_SERVER_ERROR;
#else
            std::string updFile;
            FullFileName(&updFile, gServer->GetConfig().UpdateFolder());
            updFile += up.Category();
            updFile += up.Version();
            updFile += ".upd";

            done = WriteUpdFile(updFile, updateFile->binary->Bytes(), updateFile->binary->Size());
            updateFile->binary->Close();

            if( done && up.Category().compare(SERVER_CATEGORY) == 0 )
            {
/*
               done = UpdateServer(updFile, this);
               if( done )
                  updMessage = UPD_LOADED;
               else
                  updMessage = UPDATE_SERVER_ERROR;
*/
            }
#endif
         }
      }
   }

   if( !done ) AddAnswer(false, (updMessage) ? updMessage : UPD_FORMAT_ERROR);
   else AddAnswer(true, (updMessage) ? updMessage : UPD_LOADED);

   return true;
}
