#ifndef _EXT_THREAD_H
#define _EXT_THREAD_H

#ifdef WIN32
#include <windows.h>
#else
#include <pthread.h>
#include <signal.h>
#endif

#ifdef WIN32
typedef HANDLE ThreadType;
#else
typedef pthread_t ThreadType;
#endif

class Thread {
public:
	Thread() {}
	virtual ~Thread();

	void Start();

	virtual void Execute() = 0;

	void Join();

	void Kill();

private:
	ThreadType __handle;

	// Защита от случайного копирования объекта в C++
	Thread(const Thread&);
	void operator=(const Thread&);
};

#endif