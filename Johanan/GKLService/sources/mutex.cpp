#include "mutex.h"

#ifdef WIN32

Mutex::Mutex() {
    handle = CreateMutex(0, FALSE, 0);
}

Mutex::~Mutex() {
    CloseHandle(handle);
}

bool Mutex::Lock() {
    return (WaitForSingleObject(handle, INFINITE) == WAIT_FAILED ? 1 : 0);
}

bool Mutex::Unlock() {
    return (ReleaseMutex(handle) == 0);
}

bool Mutex::TryLock() {
    return (WaitForSingleObject(handle, 0) == WAIT_FAILED ? 1 : 0);
}

#else

Mutex::Mutex() {
    pthread_mutex_init(&handle, NULL);
}

Mutex::~Mutex() {
    pthread_mutex_destroy(&handle);
}

bool Mutex::Lock() {
    return (pthread_mutex_lock(&handle) == 0);
}

bool Mutex::Unlock() {
    return (pthread_mutex_unlock(&handle) == 0);
}

bool Mutex::TryLock() {
    return (pthread_mutex_trylock(&handle) == 0);
}

WaitEvent::WaitEvent() {
    pthread_mutex_init(&handle, NULL);
    pthread_cond_init(&cond, NULL);
    value = 0;
}

WaitEvent::~WaitEvent() {
    pthread_cond_destroy(&cond);
    pthread_mutex_destroy(&handle);
}

bool WaitEvent::Waiting(unsigned timeout) {
    struct timespec tm;
    unsigned sec = timeout / 1000, millisec = timeout % 1000;

    if (pthread_mutex_lock(&handle) != 0)
        return false;

    clock_gettime(CLOCK_REALTIME, &tm);
    tm.tv_nsec += millisec * 1000000;
    tm.tv_sec += sec;
    
    if(tm.tv_nsec >= 1000000000) {
        tm.tv_sec += 1;
        tm.tv_nsec -= 1000000000;
    }
    
    int rc = 0;
    while(value <= 0) {
        rc = pthread_cond_timedwait(&cond, &handle, &tm);
        if(rc != 0)
            break;
    }
    
    if(rc == 0)
        value = 0;
    pthread_mutex_unlock(&handle);
    return (rc == 0);
}

void WaitEvent::SetEvent() {
    pthread_mutex_lock(&handle);
    value = 1;
    pthread_mutex_unlock(&handle);
    pthread_cond_broadcast(&cond);
}

void WaitEvent::ResetEvent() {
    pthread_mutex_lock(&handle);
    value = 0;
    pthread_mutex_unlock(&handle);
}


#endif