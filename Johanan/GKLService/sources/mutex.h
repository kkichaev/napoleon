#ifndef __MUTEX_H
#define __MUTEX_H

#ifdef WIN32
#include <windows.h>
#include <process.h>

#define MUTEX HANDLE
#else

#include <pthread.h>

#define MUTEX pthread_mutex_t
#endif

class Mutex
{
public:
	Mutex();
	~Mutex();

	bool Lock();
	bool Unlock();

private:
	MUTEX handle;
};

#endif