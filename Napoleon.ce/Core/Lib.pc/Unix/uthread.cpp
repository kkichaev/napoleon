/*
 * Copyright (C), 2009, Денис Мосягин
 *
 * Нити.
 *
 * ert   31/03/2009   creating
 */
#include "stdafx.h"
#include <thread.h>
#include <signal.h>

using namespace GRServer;
using namespace std;

vector<Thread::ThreadParam*> Thread::threads;
Mutex Thread::mutex;
bool Thread::mutexInited = false;
HANDLE Thread::evStopped = 0;

DWORD Thread::ThreadCount()
{
	if (mutex.Acquire(1000))
		return 0;

   DWORD size = threads.size();

	mutex.Release();

   return size;
}

void Thread::Closing()
{
	mutex.Release();
}

void Thread::KillingThreads()
{
	if (mutex.Acquire(1000))
		return;

   try
   {
      vector<ThreadParam*>::iterator i = threads.begin();
      for( ; i != threads.end(); i++ )
      {
         ThreadParam *tp = (*i);
         pthread_cancel(tp->hThread);
         delete tp->worker;
         delete tp;
      }

      threads.clear();
   }
   catch(...)
   {
   }

	mutex.Release();
}

bool Thread::Starting(IThreadWorker *worker, IErrorLogger* logger)
{
	mutex.Init();

//   if( evStopped == NULL )
//      evStopped = CreateEvent(NULL, TRUE, FALSE, NULL);

   ThreadParam *param = new ThreadParam();
   param->worker = worker;
   param->logger = logger;

   bool ret = false;
	if (mutex.Acquire(1000))
	{
      threads.push_back(param);
		mutex.Release();

      int res = pthread_create(&param->hThread, NULL, (void *(*) (void *))RunThread, param);
      ret = (res == 0);
   }

   return ret;
}

void Thread::Stopping(ThreadParam *param)
{
   // даже если недождались - надо закрывать
	mutex.Acquire(5000);

   vector<ThreadParam*>::iterator i = threads.begin();
   for( ; i != threads.end(); i++ )
   {
      if( (*i) == param )
      {
         try
         {
            threads.erase(i);

//            pthread_cancel(param->hThread);
            delete param;
         }
         catch(...)
         {
         }
         break;
      }
   }

   pthread_mutex_unlock(&mutex);
   if( threads.size() == 0 )
   {
//      SetEvent(evStopped);

		mutex.Release();
	}

//   pthread_exit(NULL);
}

static void ThreadSignalHandler(int sig, siginfo_t *s1, void* unused)
{
   throw sig;
}

ThreadExitType Thread::RunThread(ThreadParam *param)
{
   struct sigaction sa;
   sa.sa_flags = SA_SIGINFO;
   sigemptyset(&sa.sa_mask);
   sa.sa_sigaction = ThreadSignalHandler;
   sigaction(SIGSEGV, &sa, NULL);
   sigaction(SIGILL, &sa, NULL);
   sigaction(SIGFPE, &sa, NULL);
   sigaction(SIGBUS, &sa, NULL);
   sigaction(SIGTRAP, &sa, NULL);

   sigset_t set;
   sigemptyset(&set);
   sigaddset(&set, SIGINT);
   sigaddset(&set, SIGTSTP);
   pthread_sigmask(SIG_BLOCK, &set, NULL);

   ThreadExitType result = 0;
   try
   {
      param->worker->Execute();
      IThreadWorker* wrk = param->worker;
      param->worker = NULL;
      delete wrk;
   }
   catch(...)
   {
      if( param->logger != NULL )
      {
         param->logger->AddLog(IErrorLogger::Short, "Exception in thread");
      }
      if( param->worker != NULL )
      {
         delete param->worker;
         param->worker = NULL;
      }
//      result = -1;
      //gServer->AddError(false, "Thread Run exception");
   }

   try
   {
      if( param->logger != NULL )
      {
         param->logger->AddLog(IErrorLogger::Short, "Stopping thread");
      }
      Stopping(param);
   }
   catch(...)
   {
   }
   return result;
}
