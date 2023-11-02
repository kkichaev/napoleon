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

class Mutex {
public:
    Mutex();
    ~Mutex();

    bool Lock();
    bool Unlock();
    bool TryLock();

private:
    MUTEX handle;
};

class WaitEvent {
public:
    WaitEvent();
    ~WaitEvent();
    
    bool Waiting(unsigned timeout);
    void SetEvent();
    void ResetEvent();
    
private:
    MUTEX handle;
    int value;
    pthread_cond_t cond;    
};

#endif