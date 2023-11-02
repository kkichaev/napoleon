
#include "stdafx.h"
#include "Reporter.h"
#include <ServerDefs.h>

#include "PyObjects.h"
#include <isessobj.h>

#ifdef UNIX
#else
#include <io.h>
#include <Psapi.h>
#endif

#include <mutex_t.h>

#include <sstream>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;
#ifdef UNIX
static const char CONFIG_FILE[] = "reporter.ini";
#else
static const char CONFIG_FILE[] = "Reporter.cfg";
#endif

static const char FUNC_NAME[] = "run";

GRServer::IServer* gServer;
std::string configFile;

using namespace std;

ReporterPlugin::ReporterPlugin() : debugable(false)
{
#ifdef UNIX
#else
	InitializeCriticalSection(&comSec);
#endif
}

ReporterPlugin::~ReporterPlugin()
{
#ifdef UNIX
#else
	DeleteCriticalSection(&comSec);
#endif
}

// static wchar_t PyHome[_MAX_PATH];

bool ReporterPlugin::Init(IServer* server)
{
	gServer = server;
	USES_CONVERSION;

	const IServerConfig& config = server->GetConfig();

#ifdef UNIX
	MakeFullFileName(&configFile, CONFIG_FILE, config.ProgFolder());
	this->config.Load(configFile, config);
	debugable = this->config.debug;
#else
	MakeFullFileName(&configFile, CONFIG_FILE, config.ConfigFolder());
	this->config.Load(configFile, config);
	debugable = this->config.debug;

	if (this->config.debug)
	{
		if (!config.OpenConsole())
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
	}
	else
	{
		if (this->config.debugFile.empty() == false)
		{
			std::string dbg;
			MakeFullFileName(&dbg, this->config.debugFile, config.ConfigFolder());
			freopen(dbg.c_str(), "at", stdout);
			freopen(dbg.c_str(), "at", stderr);
		}
	}
#endif

	PyStatus status;

	PyPreConfig preconfig;
	PyPreConfig_InitPythonConfig(&preconfig);
	preconfig.utf8_mode = 1;
	status = Py_PreInitialize(&preconfig);

	PyConfig pycfg;
	PyConfig_InitIsolatedConfig(&pycfg);

	if(this->config.pythonHome.empty())
	{
		const char* ph = std::getenv("PYTHONHOME");
		if(ph != NULL)
		{
			this->config.pythonHome = ph;
			if(*this->config.pythonHome.rbegin() != DIR_SEP)
				this->config.pythonHome.append(1, DIR_SEP);
		}
	}
	if(!this->config.pythonHome.empty())
		pycfg.module_search_paths_set = 1;

	status = PyConfig_Read(&pycfg);

	// gServer->AddLog("PYTH %s", this->config.pythonHome.c_str());

	if(!this->config.pythonHome.empty())
	{
		const char *home = this->config.pythonHome.c_str();
		PyConfig_SetBytesString(&pycfg, &pycfg.home, home);
		PyConfig_SetBytesString(&pycfg, &pycfg.pythonpath_env, home);

		PyConfig_SetBytesString(&pycfg, &pycfg.base_exec_prefix, home);
		PyConfig_SetBytesString(&pycfg, &pycfg.base_prefix, home);
		PyConfig_SetBytesString(&pycfg, &pycfg.exec_prefix, home);
		PyConfig_SetBytesString(&pycfg, &pycfg.prefix, home);


		PyWideStringList_Append(&pycfg.module_search_paths, A2W_CP(home, CP_UTF8));
#ifdef UNIX		
		PyWideStringList_Append(&pycfg.module_search_paths, A2W_CP((this->config.pythonHome + "lib-dynload").c_str(), CP_UTF8));
#else
		PyWideStringList_Append(&pycfg.module_search_paths, A2W_CP((this->config.pythonHome + "DLLs").c_str(), CP_UTF8));
#endif
		PyWideStringList_Append(&pycfg.module_search_paths, A2W_CP((this->config.pythonHome + "site-packages").c_str(), CP_UTF8));

		std::vector<std::string>::const_iterator fi = this->config.userSites.begin();
		for (; fi != this->config.userSites.end(); fi++)
		{		
			status = PyWideStringList_Insert(&pycfg.module_search_paths, 0, A2W_CP(fi->c_str(), CP_UTF8));
			if (PyStatus_Exception(status)) {
				PyConfig_Clear(&pycfg);
				return false;
			}
			// gServer->AddLog("Add path %s, totals %d", fi->c_str(), pycfg.module_search_paths.length);
		}
	}

	status = Py_InitializeFromConfig(&pycfg);
	PyConfig_Clear(&pycfg);
	if(PyStatus_Exception(status)) 
	{
		gServer->AddLog(IErrorLogger::Full, "python init error %s", status.err_msg);
		return false;
	}

	// we use penv
	if(this->config.pythonHome.empty())
	{
		std::string str("import sys; ");

		std::vector<std::string>::const_iterator fi = this->config.userSites.begin();
		for (; fi != this->config.userSites.end(); fi++)
		{	
			str.append("sys.path.append('").append(*fi).append("'); ");
		}

		// str.append("")
		PyRun_SimpleString(str.c_str());
	}
#ifdef UNIX
#else
	CoInitializeEx(NULL, COINIT_MULTITHREADED /*COINIT_APARTMENTTHREADED*/);
#endif

	gServer->AddLog(IErrorLogger::Full, "python initing...");

	PyThreadState* mainState = PyGILState_GetThisThreadState();

	PyGRServer::Init();
	PyObjList::Init();
	PyObjDict::Init();
	PythonObject::Init();
#ifdef UNIX
#else
	PyComObject::Init();
	PyComMethodWrapper::Init();
#endif
	PyObjMemberFormat::Init();
	UserObject::Init();

#ifdef USE_CURL
	PyCurl::Init();
	PyCurlResult::Init();
#endif

	// export test
	// PyObject* pModule = PyImport_ImportModule("test_gr");
	// if(pModule != NULL)
	// {
	// 	Py_DECREF(pModule);
	// 	gServer->AddLog("module imported");
	// }
	// else
	// {
	// 	gServer->AddLog("error import");
	// }


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
		if (m == NULL)
			return;
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
	PyErr_GivenExceptionMatches(exc, PyExc_MemoryError);

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

	const char* module = W2A_CP(param->str->c_str(), CP_UTF8);
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
							{
								PyObject *m = PyImport_ReloadModule(mod);
								Py_XDECREF(m);
							}
						}
					}
					PyMem_Free((void*)fn);
				}
			}
		}
		Py_XDECREF(keys);
		PyObject* str = PyUnicode_FromString(module);
		//pModule = PyImport_GetModule(str);
		//Py_DECREF(str);
		//if (pModule == NULL)
		pModule = PyImport_ImportModule(module);
		if(pModule != NULL)
			pModule = PyImport_ReloadModule(pModule);
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
	return true;
}

void ReporterPlugin::Close()
{
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

#ifdef UNIX
#else

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

#endif