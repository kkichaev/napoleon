/*
* Copyright (C), 2009 - 2016, Денис Мосягин
*
* Нити.
*
* ert   29/08/2016   creating
*/
#ifndef __GR_MUTEX_H
#define __GR_MUTEX_H

namespace GRServer {

	class Mutex
	{
	public:
		Mutex() : inited(false)
		{
#ifdef UNIX
#else
			handle = INVALID_HANDLE_VALUE;
#endif
		}
		~Mutex()
		{
#ifdef UNIX
			if (inited)
				pthread_mutex_destroy(&handle);
#else
			if (handle != INVALID_HANDLE_VALUE)
			{
				CloseHandle(handle);
				handle = INVALID_HANDLE_VALUE;
			}
#endif
			inited = false;
		}

		bool Init()
		{
#ifdef UNIX
			if (!inited)
			{
				pthread_mutex_init(&handle, NULL);
				inited = true;
			}
			return true;
#else
			if (!inited)
			{
				handle = CreateMutex(NULL, FALSE, NULL);
				inited = true;
			}
			return handle != INVALID_HANDLE_VALUE;
#endif
		}

		bool Acquire(DWORD wait)
		{
#ifdef UNIX
			return inited && pthread_mutex_lock(&handle) != 0;
#else
			return inited && WaitForSingleObject(handle, wait) == WAIT_OBJECT_0;
#endif
		}

		void Release()
		{
#ifdef UNIX
			if (inited)
				pthread_mutex_unlock(&handle);
#else
			if (handle != INVALID_HANDLE_VALUE)
				ReleaseMutex(handle);
#endif
		}

	protected:
		bool inited;

#ifdef UNIX
		pthread_mutex_t handle;
#else
		HANDLE handle;
#endif
	};

} //namespace GRServer

#endif