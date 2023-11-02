// Reporter.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "Reporter.h"
#include <ServerDefs.h>

#include "PyObjects.h"
#include <isessobj.h>

#include <io.h>

using namespace GRServer;
static const char CONFIG_FILE[] = "Reporter.cfg";
static const char FUNC_NAME[] = "run";

GRServer::IServer* gServer;
std::string configFile;

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


	PyThreadState* mainState = PyEval_SaveThread();
	PyEval_RestoreThread(mainState);

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

		PyObject* pyStdOut = PyFile_FromFd(_fileno(stdout), "CONOUT$", "wt", -1, NULL, NULL, NULL, 0);
		PyObject* pyStdIn = PyFile_FromFd(_fileno(stdin), "CONIN$", "rt", -1, NULL, NULL, NULL, 0);
		PyObject* pyStdErr = PyFile_FromFd(_fileno(stderr), "CONOUT$", "wt", -1, NULL, NULL, NULL, 0);


		PyObject* sys = PyImport_ImportModule("sys");
		PyObject_SetAttrString(sys, "stdout", pyStdOut);
		PyObject_SetAttrString(sys, "stderr", pyStdErr);
		PyObject_SetAttrString(sys, "stdin", pyStdIn);

		Py_DECREF(sys);
		Py_DECREF(pyStdOut);
		Py_DECREF(pyStdErr);
		Py_DECREF(pyStdIn);
	}
	else
	{
		FILE* dbgLog = NULL;
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
			dbgLog = _fsopen(str, "at", _SH_DENYWR);
		}
		else
		{
#ifdef UNIX
			dbgLog = fopen("/dev/null", "w");
#else
			dbgLog = fopen("nul", "w");
#endif
		}
		if (dbgLog != NULL)
		{
			FILE *dbg2 = _fdopen(_dup(_fileno(dbgLog)), "at");

			setbuf(dbgLog, NULL);
			setbuf(dbg2, NULL);
			PyObject* pyStdOut = PyFile_FromFd(_fileno(dbgLog), NULL, "a", -1, NULL, NULL, NULL, 0);
			PyObject* pyStdErr = PyFile_FromFd(_fileno(dbg2), NULL, "a", -1, NULL, NULL, NULL, 0);

			PyObject* sys = PyImport_ImportModule("sys");
			PyObject_SetAttrString(sys, "stdout", pyStdOut);
			PyObject_SetAttrString(sys, "stderr", pyStdErr);

			Py_DECREF(sys);
			Py_DECREF(pyStdOut);
			Py_DECREF(pyStdErr);
		}
	}

	PyEval_ReleaseThread(mainState);
	mainState = NULL;
	//PyEval_RestoreThread(mainState);
	return true;
}

bool ReporterPlugin::Connect(Socket *socket, const wchar_t* password)
{
	return true;
}

static void DoFlush(PyObject* sys, const char* fn)
{
	PyObject* file = PyObject_GetAttrString(sys, fn);

	PyObject* pFlush = PyObject_GetAttrString(file, "flush");
	PyObject_CallNoArgs(pFlush);
	Py_XDECREF(pFlush);

	Py_DECREF(file);
}

bool ReporterPlugin::Handle(const wchar_t* command, const Member* param, ISession* session)
{
	if (wcscmp(command, GET_REPORT))
		return false;

	USES_CONVERSION;

	const char* module = W2A(param->str->c_str());
	gServer->AddLog(IErrorLogger::Short, "Start reporter (%d) '%s'", session->GetSocket(), module);
	PyGILState_STATE gstate = PyGILState_Ensure();

	PyObject *pModule;
	if (debugable)
	{
		PyObject *pDict = PyImport_GetModuleDict();
		PyObject* keys = PyDict_Keys(pDict);
		int count = PyList_Size(keys);
		for (int i = 0; i < count; i++)
		{
			PyObject* key = PyList_GetItem(keys, i);
			PyObject* mod = PyDict_GetItem(pDict, key);
			if (mod != NULL)
			{
				PyObject* un = PyModule_GetFilenameObject(mod);
				if (un != NULL)
				{
					const wchar_t* fn = PyUnicode_AS_UNICODE(un);
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
				}
			}
		}


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
				PyErr_Print();

			PyObject* str = PyUnicode_FromString("sys");
			PyObject* sys = PyImport_GetModule(str);
			DoFlush(sys, "stdout");
			DoFlush(sys, "stderr");

			Py_DECREF(sys);
			Py_DECREF(str);
		}

		Py_XDECREF(pFunc);
		Py_DECREF(pModule);
		Py_DECREF(pServer);
	}
	else
	{
		gServer->AddLog(IErrorLogger::Short, "No reporter module '%s'", module);
		if (PyErr_Occurred())
			PyErr_Print();
	}

	PyGILState_Release(gstate);

#ifdef _DEBUG
#endif

	if (haveParams)
	{
		ExchangeList *ack = session->Ack();
		ack->EraseFront();
	}

	gServer->AddLog(IErrorLogger::Short, "Done reporter (%d)", session->GetSocket());
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
