#include "mutex.h"

#ifdef WIN32
Mutex::Mutex()
{
	handle = CreateMutex(0, FALSE, 0);
}

Mutex::~Mutex()
{
	CloseHandle(handle);
}

bool Mutex::Lock()
{
	return (WaitForSingleObject(handle, INFINITE) == WAIT_FAILED ? 1 : 0);
}

bool Mutex::Unlock()
{
	return (ReleaseMutex(handle) == 0);
}

#else
Mutex::Mutex()
{
	pthread_mutex_init(&handle, NULL);
}

Mutex::~Mutex()
{
	pthread_mutex_destroy(&handle);
}

bool Mutex::Lock()
{
	return (pthread_mutex_lock(&handle) == 0);
}

bool Mutex::Unlock()
{
	return (pthread_mutex_unlock(&handle) == 0);
}
#endif