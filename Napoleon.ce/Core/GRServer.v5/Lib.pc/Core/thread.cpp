/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Нити.
 *
 * ert   31/03/2009   creating
 */
#include "stdafx.h"
#include <thread.h>

#include <dbghelp.h>
#include <shellapi.h>
#include <shlobj.h>
#include <strsafe.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
using namespace std;

vector<Thread::ThreadParam*> Thread::threads;
vector<Thread::ThreadParam*> Thread::waiting;
Mutex Thread::mutex;
bool Thread::mutexInited = false;
HANDLE Thread::evStopped = 0;

static DWORD maxThreadCount = 0;

DWORD Thread::ThreadCount()
{
	if (!mutex.Acquire(1000))
		return 0;

	DWORD size = (DWORD)threads.size();

	mutex.Release();

	return size;
}

void Thread::Closing()
{
	mutex.Release();
}

void Thread::KillingThreads()
{
	if (!mutex.Acquire(1000))
		return;

	try
	{
		vector<ThreadParam*>::iterator i = waiting.begin();
		for (; i != waiting.end(); i++)
		{
			ThreadParam *tp = (*i);
			delete tp->worker;
			delete tp;
		}

		i = threads.begin();
		for (; i != threads.end(); i++)
		{
			ThreadParam *tp = (*i);
			TerminateThread(tp->hThread, -1);
			CloseHandle(tp->hThread);
			delete tp->worker;
			delete tp;
		}

		threads.clear();
	}
	catch (...)
	{
	}

	mutex.Release();
}

bool Thread::Starting(IThreadWorker *worker, IErrorLogger* logger, DWORD maxCount, bool makeDump)
{
	mutex.Init();

	if (evStopped == NULL)
		evStopped = CreateEvent(NULL, TRUE, FALSE, NULL);

	ThreadParam *param = new ThreadParam();
	param->worker = worker;
	param->logger = logger;
	param->dumpOnException = makeDump;

	bool ret = false;
	if (mutex.Acquire(1000))
	{
		maxThreadCount = maxCount;

		//if (threads.size() > maxCount)
		//{
		//	// waits free
		//	waiting.push_back(param);
		//	mutex.Release();
		//	return true;
		//}

		threads.push_back(param);
		mutex.Release();

		param->hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)RunThread, param, 0, NULL);
		ret = (param->hThread != INVALID_HANDLE_VALUE);
	}

	return ret;
}

void Thread::Stopping(ThreadParam *param)
{
	// даже если недождались - надо закрывать
	mutex.Acquire(5000);

	vector<ThreadParam*>::iterator i = threads.begin();
	for (; i != threads.end(); i++)
	{
		if ((*i) == param)
		{
			try
			{
				threads.erase(i);

				CloseHandle(param->hThread);
				delete param;
			}
			catch (...)
			{
			}
			break;
		}
	}

	while (waiting.size() > 0)
	{
		if (maxThreadCount > 0 && threads.size() > maxThreadCount)
			break;

		ThreadParam *param = waiting.front();
		waiting.erase(waiting.begin());
		threads.push_back(param);
		param->hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)RunThread, param, 0, NULL);
	}

	if (threads.size() == 0)
	{
		SetEvent(evStopped);
	}
	mutex.Release();
}

#ifdef USE_CURL
#include <processthreadsapi.h>
static int AddDump(struct _EXCEPTION_POINTERS *pExceptionPointers, bool makeDump, wchar_t* szFileName)
{
	if (makeDump)
	{
		BOOL bMiniDumpSuccessful;
		WCHAR szPath[MAX_PATH];
		HANDLE hDumpFile = NULL;
		SYSTEMTIME stLocalTime;
		MINIDUMP_EXCEPTION_INFORMATION ExpParam;

		GetModuleFileNameW(NULL, szPath, MAX_PATH);
		wchar_t *ptr = wcsrchr(szPath, L'.');
		if (ptr)
			*ptr = L'\0';

		GetLocalTime(&stLocalTime);
		//GetTempPath(dwBufferSize, szPath);
		//wsprintfW(szFileName, L"%s%s", szPath, szAppName);
		//CreateDirectory(szFileName, NULL);

		wsprintfW(szFileName, L"%s.%04d%02d%02d-%02d%02d%02d-%lX-%lX.dmp",
			szPath,
			stLocalTime.wYear, stLocalTime.wMonth, stLocalTime.wDay,
			stLocalTime.wHour, stLocalTime.wMinute, stLocalTime.wSecond,
			GetCurrentProcessId(), GetCurrentThreadId());
		hDumpFile = CreateFile(szFileName, GENERIC_READ | GENERIC_WRITE,
			FILE_SHARE_WRITE | FILE_SHARE_READ, 0, CREATE_ALWAYS, 0, 0);

		ExpParam.ThreadId = GetCurrentThreadId();
		ExpParam.ExceptionPointers = pExceptionPointers;
		ExpParam.ClientPointers = TRUE;

		__try {
			bMiniDumpSuccessful = MiniDumpWriteDump(GetCurrentProcess(), GetCurrentProcessId(),
				hDumpFile, MiniDumpWithFullMemory, &ExpParam, NULL, NULL);
		}
		__except (EXCEPTION_EXECUTE_HANDLER) {

		}

		CloseHandle(hDumpFile);
	}
	return EXCEPTION_EXECUTE_HANDLER;
}

DWORD Thread::RunThread(ThreadParam *param)
{
	DWORD result = 0;
	WCHAR szFileName[MAX_PATH];
	bool makeDump = param->dumpOnException;

	__try {
		result = param->worker->Execute();
		delete param->worker;
		param->worker = NULL;

		Stopping(param);
	}
	__except (AddDump(GetExceptionInformation(), makeDump, szFileName))
	{
		if (makeDump)
		{
			USES_CONVERSION;
			param->logger->AddError(false, "Thread Exception Dump in %s", W2A_CP(szFileName, CP_UTF8));
		}
		else
		{
			param->logger->AddError(false, "Thread Run exception");
		}
	}
	return result;
}

#else
DWORD Thread::RunThread(ThreadParam *param)
{
	DWORD result = 0;
	try
	{
		result = param->worker->Execute();
		delete param->worker;
		param->worker = NULL;
	}
	catch (...)
	{
		result = -1;
		param->logger->AddError(false, "Thread Run exception");
	}

	try
	{
		Stopping(param);
	}
	catch (...)
	{
	}
	return result;
}
#endif
