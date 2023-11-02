/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Нити.
 *
 * ert   31/03/2009   creating
 */
#ifndef __GR_THREAD_H
#define __GR_THREAD_H

#include <vector>
#include <ithread.h>
#include <ierrlog.h>
#include "mutex_t.h"

#ifdef UNIX
#include <pthread.h>

typedef pthread_t ThreadHandle;
typedef void* ThreadExitType;

#else

typedef HANDLE ThreadHandle;
typedef DWORD ThreadExitType;

#endif

namespace GRServer {

class Thread
{
public:
   // delete worker, after Execute()
   static bool Starting(IThreadWorker *worker, IErrorLogger* logger, DWORD maxThreads, bool makeDump);

   static DWORD ThreadCount();

   static HANDLE StopEvent() { return evStopped; }

   static void KillingThreads();

protected:
   struct ThreadParam
   {
      IThreadWorker *worker;
      IErrorLogger* logger;
      ThreadHandle hThread;
		bool dumpOnException;
   };

   static std::vector<ThreadParam*> threads;
	static std::vector<ThreadParam*> waiting;
	static Mutex mutex;
   static bool mutexInited;
   static HANDLE evStopped;

   static ThreadExitType RunThread(ThreadParam *param);
   static void Stopping(ThreadParam *param);

   static void Closing();
};

} // namespace GRServer

#endif
