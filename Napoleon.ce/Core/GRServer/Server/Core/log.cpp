/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * Write Log
 *
 * ert   07/06/2010   creating
 */
#include "stdafx.h"
#include "dispatcher.h"
#include "member.h"
#include "datasource.h"
#include "sessobj.h"
#include "objdef.h"
#ifdef UNIX
#include <stdarg.h>
#else
#include <varargs.h>
#endif
#include "srvutility.h"

#include <sys/stat.h>  

#include "mutex_t.h"
#include <list>

#include <dbghelp.h>
#include <shellapi.h>
#include <shlobj.h>
//#include <strsafe.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace std;
using namespace GRServer;

const wchar_t LOG_EXT[] = L"log";

#ifdef UNIX
#include <pthread.h>

typedef pthread_t ThreadHandle;
typedef void* ThreadExitType;

#else

typedef HANDLE ThreadHandle;
typedef DWORD ThreadExitType;

#endif

class LogWrEntry
{
public:
	LogWrEntry()
	{
		buffer = NULL;
		//stack = 0;
		GetLocalTime(&st);
		cb = 0;
	}

	virtual ~LogWrEntry() { free(buffer); }

	char * buffer;
	size_t cb;
	SYSTEMTIME st;
	//DWORD stack;
};

static Mutex logMutex;
static std::list<LogWrEntry*> logBuf;
static bool thInited = false;
static ThreadHandle hLogThread;
static bool stoped = false;

class LogReader : public IDataSource::IReader
{
public:
   LogReader(const SessionObject& object);
   virtual ~LogReader();

   virtual bool MoveNext(Object *parentObject) ;
   virtual bool Get(Object* o) const;
   virtual bool SetFilter(const wchar_t* filter, const ISessionObject& object);
   virtual void Remove();
   virtual void Close();

   virtual const MemberFormat* Type(const wchar_t* name) const;
   virtual const Member* Value(const wchar_t* name) const;

protected:
   struct Entry
   {
      FILETIME date;
      CString text;
   };

   typedef std::vector<Entry> EntryList;
   EntryList entries;
   Entry current;

   int idate, itext;

   mutable Member member;
   mutable MemberFormat format;

   bool loaded;
   bool LoadEntries();
};

class LogCreator : public IDataSource::ICreator
{
public:
   LogCreator();
   ~LogCreator();

   virtual const wchar_t* Name() const { return L"LogSource"; }
   virtual IDataSource::IReader*  CreateReader(const ParamList& parameters, const ISessionObject& object) const;

   virtual IDataSource::IWriter*  CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
   virtual IDataSource::IRemover* CreateRemover(IDataSource::IRemover* parent, const ParamList& parameters, const ISessionObject& object) const { return NULL; }
};

//
//------------------------------- LogReader ---------------------------------
//

LogReader::LogReader(const SessionObject& object) : loaded(false)
{
   Format* f = object.format;
   idate = f->FindMember(L"date");
   itext = f->FindMember(L"text");
}

LogReader::~LogReader()
{
   Close();
}

bool LogReader::MoveNext(Object *parentObject)
{
	return false;

   bool retVal = false;

   if( !loaded )
   {
      loaded = true;
      LoadEntries();
   }

   if( entries.size() > 0 )
   {
      current = entries.front();
      entries.erase(entries.begin());
      retVal = true;
   }

   return retVal;
}

bool LogReader::Get(Object* o) const
{
   if( idate >= 0 )
   {
      Member& m = o->at(idate);
      m.datetime = current.date;
   }

   if( itext >= 0 )
   {
      Member& m = o->at(itext);
      m.str->assign(current.text);
   }

   return true;
}

bool LogReader::SetFilter(const wchar_t* filter, const ISessionObject& object)
{
   return true;
}

void LogReader::Remove()
{
}

void LogReader::Close()
{
   entries.clear();
}

const MemberFormat* LogReader::Type(const wchar_t* name) const
{
   const MemberFormat *mf = NULL;

   if( wcscmp(name, L"date") == 0 )
   {
      format.name = name;
      format.type = MemberFormat::mtDateTime;
      format.format.dateFormat = MemberFormat::Stamp;

      mf = &format;
   } else if( wcscmp(name, L"text") == 0 )
   {
      format.name = name;
      format.type = MemberFormat::mtString;

      mf = &format;
   }

   return mf;
}

const Member* LogReader::Value(const wchar_t* name) const
{
   const Member *mv = NULL;

   if( wcscmp(name, L"date") == 0 )
   {
      member.datetime = current.date;
      mv = &member;
   } else if( wcscmp(name, L"text") == 0 )
   {
      member.str = const_cast<CString*>(&current.text);
      mv = &member;
   }

   return mv;
}

bool LogReader::LoadEntries()
{
   USES_CONVERSION;

   CString fileName;
   GetLogFileName(&fileName);

   bool ret = false;
#if UNIX
   FILE *file = fopen(W2A(fileName.c_str()), "rt");
#else
   FILE *file = _wfopen(fileName.c_str(), L"rt");
#endif

   if( file )
   {
      SYSTEMTIME st = { 0 };
      char text[500], *p, *ep;
      while( true )
      {
         if( fgets(text, sizeof(text), file) == NULL )
            break;

         p = strchr(text, '\t');
         if( p == NULL )
            continue;

         *p++ = '\0';
         sscanf(text, "%d.%d.%d %d:%d:%d", (int*)&st.wYear, (int*)&st.wMonth, (int*)&st.wDay, (int*)&st.wHour, (int*)&st.wMinute, (int*)&st.wSecond);

         SystemTimeToFileTime(&st, &current.date);

         current.text.clear();
         while( (ep = strchr(p, '\n')) == NULL )
         {
            current.text.append(A2W(p));
            if( fgets(text, sizeof(text), file) == NULL )
            {
               *p = '\0';
               break;
            }
            p = text;
         }

         if( ep != NULL ) *ep = '\0';
         if( *p != '\0' ) current.text.append(A2W(p));

         entries.push_back(current);
      }
      fclose(file);
   }

   return ret;
}

//
//------------------------------- LogCreator ---------------------------------
//

LogCreator::LogCreator()
{
}

LogCreator::~LogCreator()
{
}

IDataSource::IReader* LogCreator::CreateReader(const ParamList& parameters, const ISessionObject& iobject) const
{
   const SessionObject& object = *(const SessionObject*)iobject.Self();
   return new LogReader(object);
}

//
//------------------------------- Dispatcher ---------------------------------
//

void GRServer::GetLogFileName(CString *fileName)
{
#ifdef UNIX
   char buf[PATH_MAX];
   getcwd(buf, sizeof(buf));

   USES_CONVERSION;
   fileName->assign(A2W(buf));
   fileName->append(L"/log.log");
#else
   wchar_t buf[MAX_PATH], *p;
   GetModuleFileName(NULL, buf, sizeof(buf) / sizeof(buf[0]));

   p = wcsrchr(buf, L'.');
   if( p ) wcscpy(p+1, LOG_EXT);
   else wcscat(p, LOG_EXT);

   fileName->assign(buf);
#endif
}

/*
// rewrite log
static void WriteLog(std::wstring *fileName, const char* prefix, const char* msg, va_list args)
{
   std::wstring tmp;
   SYSTEMTIME st;
   GetLocalTime(&st);

   GetLogFileName(fileName);
   tmp = (*fileName);
   tmp += L".tmp";

   FILE *file = _wfopen(tmp.c_str(), L"wt");
   if( file != NULL )
   {
      fprintf(file, "%d.%02d.%02d %02d:%02d:%02d\t", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond);
      if( prefix != NULL )
         fputs(prefix, file);

      vfprintf(file, msg, args);
      fputs("\n", file);

      FILE *rd = _wfopen(fileName->c_str(), L"rt");
      if( rd != NULL )
      {
         int readed;
         char buf[500];
         while( (readed = fread(buf, sizeof(buf[0]), sizeof(buf)/sizeof(buf[0]), rd)) > 0 )
         {
            fwrite(buf, sizeof(buf[0]), readed, file);
         }

         fclose(rd);
         DeleteFile(fileName->c_str());
      }
      fclose(file);
      MoveFile(tmp.c_str(), fileName->c_str());
   }
}
*/

//#include <processthreadsapi.h>
//typedef void(WINAPI *TGetCurrentThreadStackLimits)(PULONG_PTR LowLimit, PULONG_PTR HighLimit);
//TGetCurrentThreadStackLimits CheckStack;

//static int AddDump(struct _EXCEPTION_POINTERS *pExceptionPointers)
//{
//	BOOL bMiniDumpSuccessful;
//	WCHAR szPath[MAX_PATH];
//	WCHAR szFileName[MAX_PATH];
//	WCHAR* szAppName = L"GRServer";
//	DWORD dwBufferSize = MAX_PATH;
//	HANDLE hDumpFile;
//	SYSTEMTIME stLocalTime;
//	MINIDUMP_EXCEPTION_INFORMATION ExpParam;
//
//	GetLocalTime(&stLocalTime);
//	GetTempPath(dwBufferSize, szPath);
//
//	wsprintfW(szFileName, L"%s%s", szPath, szAppName);
//	CreateDirectory(szFileName, NULL);
//
//	wsprintfW(szFileName, L"%s%s\\%04d%02d%02d-%02d%02d%02d-%ld-%ld.dmp",
//		szPath, szAppName,
//		stLocalTime.wYear, stLocalTime.wMonth, stLocalTime.wDay,
//		stLocalTime.wHour, stLocalTime.wMinute, stLocalTime.wSecond,
//		GetCurrentProcessId(), GetCurrentThreadId());
//	hDumpFile = CreateFile(szFileName, GENERIC_READ | GENERIC_WRITE,
//		FILE_SHARE_WRITE | FILE_SHARE_READ, 0, CREATE_ALWAYS, 0, 0);
//
//	ExpParam.ThreadId = GetCurrentThreadId();
//	ExpParam.ExceptionPointers = pExceptionPointers;
//	ExpParam.ClientPointers = TRUE;
//
//	bMiniDumpSuccessful = MiniDumpWriteDump(GetCurrentProcess(), GetCurrentProcessId(),
//		hDumpFile, MiniDumpWithDataSegs, &ExpParam, NULL, NULL);
//
//	CloseHandle(hDumpFile);
//
//	return EXCEPTION_EXECUTE_HANDLER;
//}

static void DoLog(wchar_t* fileName)
{
	DWORD logLength = ((ServerConfig&)gServer->GetConfig()).logLength * 1024 * 1024;

	while (true)
	{
		LogWrEntry* logEntry = NULL;
		try
		{
			logMutex.Init();
			if (logMutex.Acquire(10000))
			{
				if (logBuf.size() > 0)
				{
					logEntry = logBuf.front();
					logBuf.pop_front();
				}
				logMutex.Release();
			}
		}
		//__except (AddDump(GetExceptionInformation()))
		catch(...)
		{
			logMutex.Release();
		}

		if (logEntry == NULL)
		{
			Sleep(100);
			continue;
		}
		HANDLE file = INVALID_HANDLE_VALUE;
		try
		{

			file = CreateFile(fileName, GENERIC_WRITE, 0, NULL, OPEN_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);
			if (file != INVALID_HANDLE_VALUE)
			{
				char buf[100];
				int wr = sprintf(buf, "%d.%02d.%02d %02d:%02d:%02d.%03d\t", logEntry->st.wYear, logEntry->st.wMonth, logEntry->st.wDay,
					logEntry->st.wHour, logEntry->st.wMinute, logEntry->st.wSecond, logEntry->st.wMilliseconds);

				DWORD dwPos = SetFilePointer(file, 0, NULL, FILE_END);
				DWORD writed;

				WriteFile(file, buf, wr, &writed, NULL);
				WriteFile(file, logEntry->buffer, (DWORD)logEntry->cb, &writed, NULL);
				WriteFile(file, "\r\n", 2, &writed, NULL);

				CloseHandle(file);
				file = INVALID_HANDLE_VALUE;

				if (logLength != 0 && dwPos != INVALID_SET_FILE_POINTER) {
					dwPos += (DWORD)(wr + logEntry->cb + 1);
					if (dwPos >= logLength) {
						wchar_t logName[MAX_PATH];
						SYSTEMTIME st;
						GetLocalTime(&st);
						wchar_t buf[100];
						wsprintf(buf, L".%d%02d%02d%02d%02d%02d%03d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
						wcscpy(logName, fileName);
						wcscat(logName, buf);
						_wrename(fileName, logName);
					}
				}
			}
			delete logEntry;
		}
		catch (...)
		{
		}
		if (file != INVALID_HANDLE_VALUE)
			CloseHandle(file);
		if (stoped)
			break;
	}
}

static ThreadExitType LogginThread(void*)
{
	wchar_t fileName[MAX_PATH];
	CString _fileName;
	GetLogFileName(&_fileName);
	wcscpy(fileName, _fileName.c_str());

	DoLog(fileName);

//		FILE *file = NULL;
//		try
//		{
//			USES_CONVERSION;
//			struct _stat logStat;
//			long curLength = 0;
//			if (_stat(W2A(fileName.c_str()), &logStat) == 0) {
//				curLength = logStat.st_size / (1024 * 1024);
//			}
//
//			if ((DWORD)curLength >= ((ServerConfig&)gServer->GetConfig()).logLength) {
//				SYSTEMTIME st;
//				GetLocalTime(&st);
//				wchar_t buf[100];
//				wsprintf(buf, L".%d%02d%02d%02d%02d%02d%03d", st.wYear, st.wMonth, st.wDay, st.wHour, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
//				std::wstring logName((const std::wstring&)fileName);
//				logName += buf;
//				rename(W2A(fileName.c_str()), W2A(logName.c_str()));
//			}
//
//#if UNIX
//			file = fopen(W2A(fileName->c_str()), "at");
//#else
//			file = _wfopen(fileName.c_str(), L"at");
//#endif
//
//			if (file != NULL)
//			{
//				fprintf(file, "%d.%02d.%02d %02d:%02d:%02d.%03d\t", logEntry->st.wYear, logEntry->st.wMonth, logEntry->st.wDay, 
//					logEntry->st.wHour, logEntry->st.wMinute, logEntry->st.wSecond, logEntry->st.wMilliseconds);
//				fputs(logEntry->buffer, file);
//				fputs("\n", file);
//
//				ULONG_PTR stackBase = 0;
//				ULONG_PTR stackLimit = 0;
//				if (CheckStack)
//					CheckStack(&stackLimit, &stackBase);
//				////GetCurrentThreadStackLimits(&stackLimit, &stackBase);
//				fprintf(file, "LogSt=%X, ThSt=%X\n", stackBase - stackLimit, logEntry->stack);
//
//				fclose(file);
//			}
//			delete logEntry;
//
//			file = NULL;
//		}
//		catch (...)
//		{
//			if (file)
//				fclose(file);
//		}

	return 0;
}


static const int TIME_LENGTH = 40;

bool inited = false;

// append data
static void WriteLog(const char* prefix, const char* msg, bool printOut, va_list args)
{
	if (stoped)
		return;

   if (printOut)
   {
      SYSTEMTIME st;
      GetLocalTime(&st);
      printf("%d.%02d.%02d %02d:%02d:%02d.%03d\t", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
      vprintf(msg, args);
      printf("\n");
      return;
   }
	//if (!inited)
	//{
	//	inited = true;
	//	HMODULE h = GetModuleHandle(L"KERNEL32");
	//	if (h)
	//		CheckStack = (TGetCurrentThreadStackLimits)GetProcAddress(h, "GetCurrentThreadStackLimits");
	//}

	//if (fileName != NULL)
	//{
	//	GetLogFileName(fileName);
	//}

	int cb = vsnprintf(NULL, 0, msg, args);
	if (cb <= 0)
		return;

	LogWrEntry *buf = new LogWrEntry();
	try
	{
		size_t prefixLength = 0;
		if (prefix != NULL)
			prefixLength = strlen(prefix);

		size_t bytes = cb + prefixLength + TIME_LENGTH + 200;
		buf->buffer = (char*)malloc(bytes);
		char *p = buf->buffer;

		//ULONG_PTR stackBase = 0;
		//ULONG_PTR stackLimit = 0;
		//if (CheckStack)
		//	CheckStack(&stackLimit, &stackBase);
		//buf->stack = stackBase - stackLimit;
		//GetCurrentThreadStackLimits(&stackLimit, &stackBase);

		//DWORD val = stackBase - stackLimit;
		//int wr = sprintf_s(p, TIME_LENGTH + 200 - 1, "%d.%02d.%02d %02d:%02d:%02d.%03d\tst=%X\t", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds, val);
		//int wr = sprintf_s(p, TIME_LENGTH + 200 - 1,  "%d.%02d.%02d %02d:%02d:%02d.%03d\t", st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond, st.wMilliseconds);
		//if (wr > 0)
		//	p += wr;

		if (prefix != NULL)
		{
			strcpy(p, prefix);
			p += prefixLength;
		}
		int wr = vsnprintf(p, cb+2, msg, args);
		if (wr > 0)
			p[wr] = '\0';

		buf->cb = wr + prefixLength;
	}
	catch (...)
	{
		delete buf;
		buf = NULL;
	} 

	if (buf == NULL)
		return;

	logMutex.Init();
	if (!logMutex.Acquire(500))
	{
		delete buf;
		return;
	}
	logBuf.push_back(buf);
	logMutex.Release();


	if (!thInited)
	{
		thInited = true;
#ifdef UNIX
		pthread_create(&hLogThread, NULL, (void *(*) (void *))LogginThread, NULL);
#else
		hLogThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)LogginThread, NULL, 0, NULL);
#endif
	}
}

void Dispatcher::StopLog()
{
	stoped = true;
	WaitForSingleObject(hLogThread, 1000);
}

void Dispatcher::AddLog(IErrorLogger::DebugLevel level, const char* msg, ... )
{
   if( (unsigned)dataCtrl.Config().Debug() >= (unsigned)level )
   {
      va_list args;
      va_start(args, msg);
      WriteLog(NULL, msg, dataCtrl.Config().printLog, args);
      va_end(args);
   }
}

void Dispatcher::AddLog(const char* msg, ... )
{
   va_list args;
   va_start(args, msg);
   WriteLog(NULL, msg, dataCtrl.Config().printLog, args);
   va_end(args);
}

void Dispatcher::AddError(bool critical, const char* msg, ... )
{
   va_list args;

   va_start(args, msg);

	if (critical)
	{
		WriteLog("Критическая ошибка: ", msg, dataCtrl.Config().printLog, args);

		wstring msg(L"Произошла критическая ошибка. подробности в файле 'GRServer.log'");

		if (loader != NULL)
			loader->ShowCriticalError(msg.c_str());
	}
	else
		WriteLog("Ошибка: ", msg, dataCtrl.Config().printLog, args);

   va_end(args);
}

void Dispatcher::InitLog()
{
   DataSource::AddCreator(new LogCreator());
}
