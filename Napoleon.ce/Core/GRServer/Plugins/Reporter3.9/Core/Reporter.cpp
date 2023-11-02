// Reporter.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "Reporter.h"
#include <ServerDefs.h>

#include "PyObjects.h"
#include <isessobj.h>

#include <io.h>

#include <Psapi.h>
#include <sstream>
#include <mutex_t.h>

using namespace GRServer;
static const char CONFIG_FILE[] = "Reporter.cfg";
static const char FUNC_NAME[] = "run";

GRServer::IServer* gServer;
std::string configFile;

using namespace std;
class MemoryManager
{
	struct MemoryData
	{
		DWORD count;
		size_t size;
		bool fiered;

		MemoryData() : count(0), size(0), fiered(false) {}
	};

public:
	static void Start(size_t maxMemory);
	static void Stop();
	//static bool MemoryLow();

	static void ThreadFinished();

private:
	static void* Malloc(void *ctx, size_t size);
	static void* Calloc(void *ctx, size_t nelem, size_t elsize);
	static void* Realloc(void *ctx, void *ptr, size_t new_size);
	static void Free(void *ctx, void *ptr);

	typedef void* (*TMalloc)(void *ctx, size_t size);
	typedef void* (*TCalloc)(void *ctx, size_t nelem, size_t elsize);
	typedef void* (*TRealloc)(void *ctx, void *ptr, size_t new_size);
	typedef void (*TFree)(void *ctx, void *ptr);

	static TMalloc prevMalloc;
	static TCalloc prevCalloc;
	static TRealloc prevRealloc;
	static TFree prevFree;

	static size_t memoryMax;
	static bool inspecting;

	static bool CanAlloc(size_t size);

	typedef map<DWORD, MemoryData> ThreadMap;
	static ThreadMap threads;
};

MemoryManager::TMalloc MemoryManager::prevMalloc = NULL;
MemoryManager::TCalloc MemoryManager::prevCalloc = NULL;
MemoryManager::TRealloc MemoryManager::prevRealloc = NULL;
MemoryManager::TFree MemoryManager::prevFree = NULL;

size_t MemoryManager::memoryMax = 0;
bool MemoryManager::inspecting = true;
MemoryManager::ThreadMap MemoryManager::threads;
static Mutex threadMutex;

static bool MemoryErrorOccure = false;
static int CheckMemTreshold = 513;
static size_t LowMemory = 100 * 10240; // dont worry about 100 kByte

void MemoryManager::Start(size_t maxMemory)
{
	memoryMax = maxMemory;

	PyMemAllocatorEx memAlloc;
	PyMem_GetAllocator(PYMEM_DOMAIN_RAW, &memAlloc);
	
	prevMalloc = memAlloc.malloc;
	prevCalloc = memAlloc.calloc;
	prevRealloc = memAlloc.realloc;
	prevFree = memAlloc.free;

	memAlloc.malloc = Malloc;
	memAlloc.calloc = Calloc;
	memAlloc.realloc = Realloc;
	memAlloc.free = Free;
	PyMem_SetAllocator(PYMEM_DOMAIN_RAW, &memAlloc);

	threadMutex.Init();
}

static size_t GetUsedMemory()
{
	HANDLE h = GetCurrentProcess();
	PROCESS_MEMORY_COUNTERS mem;

	GetProcessMemoryInfo(h, (PPROCESS_MEMORY_COUNTERS)&mem, sizeof(mem));
	return mem.WorkingSetSize;
}

void MemoryManager::ThreadFinished()
{
	threadMutex.Acquire(1000);
	threads.erase(GetCurrentThreadId());
	threadMutex.Release();
}

//bool MemoryManager::MemoryLow()
//{
//	if (memoryMax == 0) return false;
//
//	return GetUsedMemory() > (memoryMax - LowMemory);
//}

bool MemoryManager::CanAlloc(size_t size)
{
	DWORD tid = GetCurrentThreadId();

	threadMutex.Acquire(1000);
	MemoryData& md = threads[tid];
	threadMutex.Release();

	md.count++;
	md.size += size;

	if (!inspecting || size < (size_t)CheckMemTreshold || memoryMax == 0 || md.size < LowMemory || GetUsedMemory() < memoryMax || md.fiered)
		return true;

	bool canAlloc = false;

	threadMutex.Acquire(1000);
	ThreadMap::const_iterator i = threads.begin();
	for (; i != threads.end(); i++)
	{
		const MemoryData &data = i->second;
		if (!data.fiered && data.size > md.size) // we find thread with more allocated data
		{
			canAlloc = true;
			break;
		}
	}
	threadMutex.Release();

	if (!canAlloc)
		md.fiered = true;

	return canAlloc;
}

void MemoryManager::Stop()
{
	PyMemAllocatorEx memAlloc;
	PyMem_GetAllocator(PYMEM_DOMAIN_RAW, &memAlloc);

	memAlloc.malloc = prevMalloc;
	memAlloc.calloc = prevCalloc;
	memAlloc.realloc = prevRealloc;
	memAlloc.free = prevFree;
	PyMem_SetAllocator(PYMEM_DOMAIN_RAW, &memAlloc);
}

void* MemoryManager::Malloc(void *ctx, size_t size)
{
	if (!CanAlloc(size))
	{
		return PyErr_NoMemory();
	}

	return prevMalloc(ctx, size);
}

void* MemoryManager::Calloc(void *ctx, size_t nelem, size_t elsize)
{
	size_t size = nelem * elsize;

	if (!CanAlloc(size))
	{
		return PyErr_NoMemory();
	}

	return prevCalloc(ctx, nelem, elsize);
}

void* MemoryManager::Realloc(void *ctx, void *ptr, size_t new_size)
{
	if (!CanAlloc(new_size))
	{
		return PyErr_NoMemory();
	}

	return prevRealloc(ctx, ptr, new_size);

}
void MemoryManager::Free(void *ctx, void *ptr)
{
	prevFree(ctx, ptr);

	DWORD tid = GetCurrentThreadId();

	threadMutex.Acquire(1000);
	MemoryData& md = threads[tid];
	threadMutex.Release();
	if (md.count > 1)
	{
		md.size -= md.size / md.count;
		md.count--;
	}
	else
	{
		md.size = 0;
		md.count = 0;
	}
}


ReporterPlugin::ReporterPlugin() : debugable(false)
{
	InitializeCriticalSection(&comSec);
}

ReporterPlugin::~ReporterPlugin()
{
	DeleteCriticalSection(&comSec);
}

static wchar_t PyHome[_MAX_PATH];

bool ReporterPlugin::Init(IServer* server)
{
	gServer = server;

	const IServerConfig& config = server->GetConfig();

	configFile = config.PluginsFolder();
	configFile += CONFIG_FILE;

	this->config.Load(configFile);

	debugable = this->config.debug;
	if (this->config.debug)
	{
		if (AllocConsole() == FALSE)
			gServer->AddLog(IErrorLogger::Full, "Python can't alloc debug console %d", GetLastError());

		//SetConsoleTitle(L"GRServer Python Debug");
		SetConsoleTitle(gServer->ServerName());
		SetConsoleOutputCP(1251);
		SetConsoleCP(1251);

		freopen("CONOUT$", "wt", stdout);
		freopen("CONOUT$", "wt", stderr);
		freopen("CONIN$", "rt", stdin);
	}
	else
	{
		if (this->config.debugFile.empty() == false)
		{
			const char *str = this->config.debugFile.c_str();
			char dirName[500];
			GetModuleFileNameA(NULL, dirName, sizeof(dirName) / sizeof(dirName[0]));
#ifdef UNIX
			if (*str != '\0' && (*str == '/' || *str == '~'))
			{
			}
#else
			// проверка на полное имя
			if (!(*str != '\0' && str[1] != '\0' && ((*str == '\\' && str[1] == '\\') || str[1] == ':')))
			{
				char *p = strrchr(dirName, '\\');
				strcpy(p + 1, str);
				str = dirName;
			}
#endif
			freopen(str, "at", stdout);
			freopen(str, "at", stderr);
		}
	}


	if (!this->config.pythonHome.empty())
	{
		const char *str = this->config.pythonHome.c_str();
		char dirName[500];
		GetModuleFileNameA(NULL, dirName, sizeof(dirName) / sizeof(dirName[0]));
#ifdef UNIX
		if (*str != '\0' && (*str == '/' || *str == '~'))
		{
		}
#else
		// проверка на полное имя
		if (!(*str != '\0' && str[1] != '\0' && ((*str == '\\' && str[1] == '\\') || str[1] == ':')))
		{
			char *p = strrchr(dirName, '\\');
			strcpy(p + 1, str);
			str = dirName;
		}
		CoInitializeEx(NULL, COINIT_MULTITHREADED /*COINIT_APARTMENTTHREADED*/);
#endif

		USES_CONVERSION;
		wcsncpy(PyHome, A2W(str), sizeof(PyHome) / sizeof(PyHome[0]));
		Py_SetPythonHome(PyHome);
		Py_InitializeEx(0);
	}
	gServer->AddLog(IErrorLogger::Full, "python home %s initing...", PyHome);


	PyThreadState* mainState = PyGILState_GetThisThreadState();

	PyGRServer::Init();
	PyObjList::Init();
	PyObjDict::Init();
	PythonObject::Init();
	PyComObject::Init();
	PyComMethodWrapper::Init();
	PyObjMemberFormat::Init();
	UserObject::Init();

#ifdef USE_CURL
	PyCurl::Init();
	PyCurlResult::Init();
#endif

#ifndef _WIN64
	MemoryManager::Start(config.MemoryLimit());
#endif
	gServer->AddLog(IErrorLogger::Full, "python done", PyHome);

	PyObject* pModule = PyImport_ImportModule("site");
	PyObject* pFunc = PyObject_GetAttrString(pModule, "addsitedir");
	PyObject* param = PyUnicode_FromString("./Lib/site-pacakges");
	PyObject *pArgs = PyTuple_New(1);
	PyTuple_SetItem(pArgs, 0, param);
	PyObject_CallObject(pFunc, pArgs);
	Py_DECREF(pArgs);
	Py_DECREF(pFunc);
	Py_DECREF(pModule);

	debugable = this->config.debug;
	PyEval_ReleaseThread(mainState);
	return true;
}

bool ReporterPlugin::Connect(Socket *socket, const wchar_t* password)
{
	return true;
}

static void DoFlush(PyObject* sys, const char* fn)
{
	PyObject* file = PyObject_GetAttrString(sys, fn);
	if (file != NULL)
	{
		PyObject* pFlush = PyObject_GetAttrString(file, "flush");
		PyObject_CallNoArgs(pFlush);
		Py_DECREF(pFlush);
		Py_DECREF(file);
	}
}

static void SetExceptionData(Object* o, PyObject *exc, PyObject *val, PyObject *tb)
{
	Member *m;
	if (exc && PyType_Check(exc))
	{
		USES_CONVERSION;
		m = (*o)[L"type"];
		m->str->assign(A2W(((PyTypeObject*)exc)->tp_name));
	}

	std::wstring res;

	PyObject* tbm = PyImport_ImportModule("traceback");
	PyObject* pFunc = PyObject_GetAttrString(tbm, "format_exception");
	Py_DECREF(tbm);

	PyObject *pArgs = PyTuple_New(3);
	if (!exc)
		exc = Py_None;
	if (!val)
		val = Py_None;
	if (!tb)
		tb = Py_None;

	Py_INCREF(exc);
	PyTuple_SetItem(pArgs, 0, exc);
	Py_INCREF(val);
	PyTuple_SetItem(pArgs, 1, val);
	Py_INCREF(tb);
	PyTuple_SetItem(pArgs, 2, tb);

	PyErr_Clear();
	PyObject *plist = PyObject_CallObject(pFunc, pArgs);

	if (plist)
	{
		Py_ssize_t  count = PyList_Size(plist);
		for (Py_ssize_t i = 0; i < count; i++)
		{
			wchar_t* tstr = PyUnicode_AsWideCharString(PyList_GetItem(plist, i), NULL);
			if (tstr)
			{
				res.append(tstr);
				PyMem_Free(tstr);
			}
		}
		Py_DECREF(plist);
	}
	Py_DECREF(pArgs);
	Py_DECREF(pFunc);

	m = (*o)[L"stackTrace"];
	m->str->assign(res);
}

static void HandleError(ISession* session)
{
	//MemoryManager::Inspecting(false);

	PyObject *exc = NULL, *val = NULL, *tb = NULL;
	PyErr_Fetch(&exc, &val, &tb);
	MemoryErrorOccure = (PyErr_GivenExceptionMatches(exc, PyExc_MemoryError) != 0);
	if (MemoryErrorOccure)
	{
		std::stringstream ss;
		ss << "memory limit exceeded (" << session->GetSocket() << ") ";
		string str(ss.str());
		session->MemoryStat(&str, true);

		gServer->AddLog(str.c_str());
	}

	PyErr_NormalizeException(&exc, &val, &tb);
	ISessionObject* iso = session->CreateObject(L"PythonExecError", false);
	if (iso != NULL)
	{
		ServObject *so = iso->Self();
		Object *o = so->AddObject();

		SetExceptionData(o, exc, val, tb);
		session->AddToAnswer(so);
		delete so;
	}

	PyErr_Restore(exc, val, tb);
	PyErr_PrintEx(0);
}

static void AddErrorObject(ISession *session, const wchar_t* message)
{
	ISessionObject* iso = session->CreateObject(L"PythonExecError", false);
	if (iso != NULL)
	{
		ServObject *so = iso->Self();
		Object *o = so->AddObject();

		Member* m = (*o)[L"stackTrace"];
		m->str->assign(message);

		session->AddToAnswer(so);
		delete so;
	}
}

bool ReporterPlugin::Handle(const wchar_t* command, const Member* param, ISession* session)
{
	if (wcscmp(command, GET_REPORT))
		return false;

	USES_CONVERSION;

	const char* module = W2A(param->str->c_str());
	gServer->AddLog(IErrorLogger::None, "Start reporter (%d) '%s'", session->GetSocket(), module);
	
	PyGILState_STATE gstate = PyGILState_Ensure();

	PyObject *pModule;
	if (debugable)
	{
		PyObject *pDict = PyImport_GetModuleDict();
		PyObject* keys = PyDict_Keys(pDict);
		Py_ssize_t count = PyList_Size(keys);
		for (Py_ssize_t i = 0; i < count; i++)
		{
			PyObject* key = PyList_GetItem(keys, i);
			PyObject* mod = PyDict_GetItem(pDict, key);
			if (mod != NULL)
			{
				PyObject* un = PyModule_GetFilenameObject(mod);
				if (un != NULL)
				{
					const wchar_t* fn = PyUnicode_AsWideCharString(un, NULL);
					Py_DECREF(un);

					PyErr_Clear();
					if (fn != NULL)
					{
						const wchar_t *lp = wcsrchr(fn, L'\\');
						if (lp == NULL)
							lp = wcsrchr(fn, L'/');
						if (lp != NULL && lp - 13 >= fn)
						{
							const wchar_t* lastFolder = lp - 13;
							if (wcsstr(lastFolder, L"manager") != NULL || wcsstr(lastFolder, L"grsoft") != NULL || wcsstr(lastFolder, L"site-packages") != NULL)
								PyImport_ReloadModule(mod);
						}
					}
					PyMem_Free((void*)fn);
				}
			}
		}
		Py_XDECREF(keys);
		PyObject* str = PyUnicode_FromString(module);
		pModule = PyImport_GetModule(str);
		Py_DECREF(str);
		if (pModule == NULL)
			pModule = PyImport_ImportModule(module);
	}
	else
	{
		pModule = PyImport_ImportModule(module);
	}

	bool haveParams = false;
	if (pModule != NULL)
	{
		PyObject *pServer = PyGRServer::Create(session, this);
		PyObject* pFunc = PyObject_GetAttrString(pModule, FUNC_NAME);
		if (pFunc && PyCallable_Check(pFunc))
		{
			try
			{
				PyObject *pArgs = PyTuple_New(1);
				PyTuple_SetItem(pArgs, 0, pServer);

				PyObject *pValue = PyObject_CallObject(pFunc, pArgs);

				haveParams = ((PyGRServer*)pServer)->haveParams;
				Py_DECREF(pArgs);
				Py_XDECREF(pValue);
			}
			catch (...)
			{
			}
			if (PyErr_Occurred())
				HandleError(session);
		}

		Py_XDECREF(pFunc);
		Py_DECREF(pModule);
		Py_DECREF(pServer);
	}
	else
	{
		gServer->AddLog(IErrorLogger::Short, "No reporter module '%s'", module);
		if (PyErr_Occurred())
			HandleError(session);
	}

	PyGILState_Release(gstate);


	if (haveParams)
	{
		ExchangeList *ack = session->Ack();
		ack->EraseFront();
	}

	gServer->AddLog(IErrorLogger::None, "Done reporter (%d)", session->GetSocket());
	MemoryManager::ThreadFinished();
	return true;
}

void ReporterPlugin::Close()
{
#ifndef _WIN64
	MemoryManager::Stop();
#endif

#ifdef UNIX
#else
	std::set<COMData>::const_iterator i = comObjects.begin();
	while (i != comObjects.end())
	{
		i->obj->Release();
		i++;
	}
	comObjects.clear();

	CoUninitialize();
#endif

	PyGILState_Ensure();
	Py_FinalizeEx();
}

void ReporterPlugin::RemoveOutLeaveCOM(const FILETIME& ft)
{
	std::set<COMData>::const_iterator i = comObjects.begin();
	while (i != comObjects.end())
	{
		if (CompareFileTime(&i->outTime, &ft) < 0)
		{
			i->obj->Release();
			EnterCriticalSection(&comSec);
			i = comObjects.erase(i);
			LeaveCriticalSection(&comSec);
		}
		else
			i++;
	}
}

void ReporterPlugin::PutCOMObject(const std::string& tag, IDispatch* obj)
{
	if (config.maxCOMSlots > 0)
	{
		obj->AddRef();

		SYSTEMTIME st;
		FILETIME ft;
		GetLocalTime(&st);
		COMData data;
		SystemTimeToFileTime(&st, &ft);
		*((__int64*)&data.outTime) = *(__int64*)&ft + ((__int64)config.liveTimeCOM * (__int64)10000000);
		data.obj = obj;
		data.tag = tag;

		RemoveOutLeaveCOM(ft);

		if ((int)comObjects.size() >= config.maxCOMSlots)
		{
			std::set<COMData>::const_iterator fnd = comObjects.begin();
			std::set<COMData>::const_iterator i = comObjects.begin();
			while (i != comObjects.end())
			{
				if (CompareFileTime(&fnd->outTime, &i->outTime) > 0)
					fnd = i;
				i++;
			}
			if (fnd != comObjects.end())
			{
				fnd->obj->Release();
				EnterCriticalSection(&comSec);
				comObjects.erase(fnd);
				LeaveCriticalSection(&comSec);
			}
		}

		EnterCriticalSection(&comSec);
		comObjects.insert(data);
		LeaveCriticalSection(&comSec);
	}
}

IDispatch* ReporterPlugin::GetCOMObject(const std::string& tag)
{
	IDispatch* out = NULL;

	SYSTEMTIME st;
	FILETIME ft;
	GetLocalTime(&st);
	SystemTimeToFileTime(&st, &ft);
	RemoveOutLeaveCOM(ft);

	std::set<COMData>::const_iterator i = comObjects.begin();
	while (i != comObjects.end())
	{
		if (i->tag.compare(tag) == 0)
		{
			out = i->obj;

			EnterCriticalSection(&comSec);
			comObjects.erase(i);
			LeaveCriticalSection(&comSec);
			break;
		}
		i++;
	}

	return out;
}
