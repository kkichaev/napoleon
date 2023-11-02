// Reporter.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "Reporter.h"
#include "PyObjects.h"

#include <structmember.h>
#include <isessobj.h>
#include "sch_service.h"
#include <datetime.h>

using namespace GRServer;

int NoSetter(void *self, PyObject *value, void *closure)
{
	return 0;
}

static PyObject* GetParams(PyGRServer *self, void *closure)
{
	ExchangeList* el = self->session->Ack();
	if (el != NULL && el->size() >= 2)
	{
		self->haveParams = true;
		ServObject* so = el->at(1);
		return PyObjList::Create(NULL, so, self);
		//if( so->size() > 0 )
		//   return PythonObject::Create(so->at(0), self);
	}

	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* FindServObject(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName;
	if (!PyArg_ParseTuple(args, "u", &objName))
		return NULL;

	ISessionObject *iso = self->session->GetObject(objName, NULL);
	if (iso != NULL)
		return PyObjList::Create(iso, NULL, self);

	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* GetDictValue(PyObject* dict, const wchar_t* wkey)
{
	PyObject* key = PyUnicode_FromWideChar(wkey, -1);
	PyObject* val = PyDict_GetItem(dict, key);
	Py_DECREF(key);
	return val;
}

static bool LoadSchEntries(Scheduler* schDest, PyObject* entries)
{
	bool ret = true;

	size_t size = PyList_Size(entries);
	for (size_t i = 0; i < size; i++)
	{
		PyObject* el = PyList_GetItem(entries, i);
		if (el != NULL && PyDict_Check(el))
		{
			Scheduler::Entry dest;
			Scheduler::Entry::Field *flds = Scheduler::Entry::fields();

			for (int mi = 0; flds[mi].name && *flds[mi].name ; mi++)
			{
				Scheduler::Entry::Field& mel = flds[mi];
				PyObject* val = GetDictValue(el, mel.name);
				if (val != NULL && PyLong_Check(val))
				{
					dest.*mel.ptr = PyLong_AsLong(val);
				}
			}

			PyObject* val = GetDictValue(el, L"cycle");
			if (val != NULL && PyBool_Check(val))
			{
				dest.cycle = val == Py_True;
			}

			val = GetDictValue(el, L"starting");
			if (val != NULL)
			{
				if (PyFloat_Check(val))
				{
					dest.starting = (int64_t)PyFloat_AS_DOUBLE(val);
				}
				else if (PyLong_Check(val))
				{
					dest.starting = PyLong_AsLongLong(val);
				} else if (PyDateTime_Check(val))
				{
					::tm date;
					date.tm_isdst = -1;
					date.tm_year = PyDateTime_GET_YEAR((PyDateTime_DateTime*)val) - 1900;
					date.tm_mon = PyDateTime_GET_MONTH((PyDateTime_DateTime*)val) - 1;
					date.tm_mday = PyDateTime_GET_DAY((PyDateTime_DateTime*)val);
					date.tm_hour = PyDateTime_DATE_GET_HOUR((PyDateTime_DateTime*)val);
					date.tm_min = PyDateTime_DATE_GET_MINUTE((PyDateTime_DateTime*)val);
					date.tm_sec = 0;

					dest.starting = mktime(&date);
				}
			}

			schDest->entries.push_back(dest);
		}
	}

	return ret;
}

static bool LoadScheduler(Scheduler* dest, PyObject* src)
{
	bool ret = true;
	Scheduler::Field* fields = Scheduler::fields();
	for (int i = 0; fields[i].name && *fields[i].name; i++)
	{
		const Scheduler::Field& mel = fields[i];
		PyObject* val = GetDictValue(src, mel.name);
		if (val != NULL && PyUnicode_Check(val))
		{
			dest->*mel.ptr = PyUnicode_AsWideCharString(val, NULL);
		}
	}

	PyObject* val = GetDictValue(src, L"entries");
	if (val != NULL && PyList_Check(val))
	{
		LoadSchEntries(dest, val);
	}
	else
	{
		ret = false;
	}

	return ret;
}

static PyObject* Schedule(PyGRServer* self, PyObject* args)
{
	PyObject* ret = Py_False;

	IScheduler* sch = (IScheduler*)gServer->GetService(SCHEDULE_SERVICE);
	if (sch != NULL)
	{
		PyObject* schDic;
		PyObject* toDB = Py_False;
		if (PyArg_ParseTuple(args, "O!|O!"
			, &PyDict_Type, &schDic
			, &PyBool_Type, &toDB))
		{
			Scheduler task;
			if (LoadScheduler(&task, schDic))
			{
				if (sch->Put(task, toDB == Py_True))
				{
					ret = Py_True;
				}
			}
		}
	}

	Py_INCREF(ret);
	return ret;
}

// 0 - not found
// 1 - scheduled
// 2 - running
static PyObject* ScheduleTaskStatus(PyGRServer* self, PyObject* args)
{
	int res = 0;
	IScheduler *sch = (IScheduler * )gServer->GetService(SCHEDULE_SERVICE);
	if (sch != NULL)
	{
		const wchar_t* id;
		if (PyArg_ParseTuple(args, "u", &id))
		{
			if (sch->IsRunning(id))
				res = 2;
			else if (sch->Get(id) != NULL)
				res = 1;
		}
	}

	return PyLong_FromLong(res);
}

static PyObject* EntryToDic(const Scheduler::Entry& src)
{
	PyObject* res = PyDict_New();

	Scheduler::Entry::Field* flds = Scheduler::Entry::fields();

	for (int mi = 0; flds[mi].name && *flds[mi].name; mi++)
	{
		Scheduler::Entry::Field& mel = flds[mi];
		PyObject* key = PyUnicode_FromWideChar(mel.name, -1);
		PyObject* value = PyLong_FromLong(src.*mel.ptr);
		PyDict_SetItem(res, key, value);
	}

	PyObject* key = PyUnicode_FromWideChar(L"cycle", -1);
	PyObject* value = src.cycle ? Py_True : Py_False;
	Py_INCREF(value);
	PyDict_SetItem(res, key, value);

	::tm tm;
#ifdef UNIX
	localtime_r(&tm, &src.starting);
#else
	localtime_s(&tm, &src.starting);
#endif
	value = PyDateTime_FromDateAndTime(tm.tm_year + 1900, tm.tm_mon + 1
		,tm.tm_mday, tm.tm_hour, tm.tm_min, tm.tm_sec, 0);
	key = PyUnicode_FromWideChar(L"starting", -1);
	PyDict_SetItem(res, key, value);

	return res;
}

static PyObject* LoadEntries(const Scheduler& task)
{
	PyObject* res = PyList_New(0);

	auto ei = task.entries.begin();
	for (; ei != task.entries.end(); ei++)
	{
		PyObject* item = EntryToDic(*ei);
		if(item)
			PyList_Append(res, item);
	}

	return res;
}

static PyObject* ScheduleToDic(const Scheduler& task)
{
	PyObject* res = PyDict_New();
	Scheduler::Field* flds = Scheduler::fields();

	for (int mi = 0; flds[mi].name && *flds[mi].name; mi++)
	{
		Scheduler::Field& mel = flds[mi];
		PyObject* key = PyUnicode_FromWideChar(mel.name, -1);
		PyObject* value = PyUnicode_FromWideChar((task.*mel.ptr).c_str(), -1);
		PyDict_SetItem(res, key, value);
	}

	PyObject* val = LoadEntries(task);
	PyObject* key = PyUnicode_FromWideChar(L"entries", -1);
	PyDict_SetItem(res, key, val);	

	return res;
}

static PyObject* ScheduleGetTask(PyGRServer* self, PyObject* args)
{
	PyObject* res = NULL;
	IScheduler* sch = (IScheduler*)gServer->GetService(SCHEDULE_SERVICE);
	if (sch != NULL)
	{
		const wchar_t* id;
		if (PyArg_ParseTuple(args, "u", &id))
		{
			Scheduler* task = sch->Get(id);
			if (task != NULL)
				res = ScheduleToDic(*task);
		}
	}

	if (res != NULL)
		return res;

	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* GetServObject(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName, *whereStr = NULL, *key = NULL;
	if (!PyArg_ParseTuple(args, "u|uu", &objName, &whereStr, &key))
		return NULL;

	ISessionObject *iso = self->session->CreateObject(objName, true);
	if (iso != NULL)
	{
#ifdef SESSION_MEMORY_DEBUG
		USES_CONVERSION;
		const char *aObjName = W2A(objName);
		gServer->AddLog(IErrorLogger::Full, "Socket (%d) reading %s:%s", self->session->GetSocket(), aObjName, ((whereStr == NULL) ? "" : W2A(whereStr)));
#endif

		PyThreadState *_save = PyEval_SaveThread();
		bool readed = iso->Reading(((whereStr == NULL) ? L"" : whereStr), true, true);
		PyEval_RestoreThread(_save);

#ifdef SESSION_MEMORY_DEBUG
		std::string out;
		gServer->AddLog(IErrorLogger::Full, "Socket (%d) readed %s:%d %s", self->session->GetSocket(), aObjName, iso->Self()->size(), readed ? "OK": "Fail");
		self->session->MemoryStat(&out, false );
		gServer->AddLog(IErrorLogger::Full, "Socket (%d) session stat %s", self->session->GetSocket(), out.c_str());
#endif

		if (!readed)
		{
			if (self->session->MemoryLimitExceeded())
			{
				PyErr_NoMemory();
				return NULL;
			}
			iso = NULL;
		}
	}

	if (iso == NULL)
	{
		Py_INCREF(Py_None);
		return Py_None;
	}

	//ISessionObject *iso = self->session->LoadObject(A2W(objName), NULL, (whereStr == NULL) ? L"" : A2W(whereStr));
 //  if( iso == NULL )
 //  {
 //     Py_INCREF(Py_None);
 //     return Py_None;
 //  }

	if (key == NULL)
		return PyObjList::Create(iso, NULL, self);
	return PyObjDict::Create(iso, NULL, self, key);
}

static PyObject* GetCurrentUser(PyGRServer* self, PyObject* args)
{
	ISessionObject *iso = self->session->GetObject(L"User", NULL);
	if (iso == NULL)
	{
		Py_INCREF(Py_None);
		return Py_None;
	}

	ServObject *so = iso->Self();
	if (so->size() == 0)
	{
		Py_INCREF(Py_None);
		return Py_None;
	}

	return UserObject::Create(so->at(0), self);
}

static PyObject* Remove(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName, *whereStr = NULL;
	if (!PyArg_ParseTuple(args, "u|u", &objName, &whereStr))
		return NULL;

	PyObject* ret = Py_False;

	PyThreadState *_save = PyEval_SaveThread();
	ISessionObject *iso = self->session->GetObject(objName, NULL);
	if (iso != NULL && iso->Removing((whereStr == NULL) ? L"" : whereStr))
		ret = Py_True;
	PyEval_RestoreThread(_save);

	Py_INCREF(ret);
	return ret;
}

static PyObject* PostServObject(PyGRServer* self, PyObject* args)
{
	PyObject *obj;
	if (PyArg_ParseTuple(args, "O", &obj))
	{
		Py_INCREF(obj);
		if (PyObject_TypeCheck(obj, &PyObjList::type) != 0)
		{
			if (((PyObjList*)obj)->src != NULL)
				self->session->PostObject(((PyObjList*)obj)->src);
		}
		else if (PyObject_TypeCheck(obj, &PyObjDict::type) != 0)
		{
			if (((PyObjDict*)obj)->src != NULL)
				self->session->PostObject(((PyObjDict*)obj)->src);
		}
		Py_DECREF(obj);
	}

	Py_INCREF(Py_None);
	return Py_None;
}

#include <comdef.h>
static PyObject* CreateCOMObject(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName;
	if (PyArg_ParseTuple(args, "u", &objName))
	{
		USES_CONVERSION;
		CLSID clsid;
		HRESULT res = CLSIDFromProgID(objName, &clsid);
		if (SUCCEEDED(res))
		{
			PyThreadState *_save = PyEval_SaveThread();

			IDispatch* prog;
			//res = CoCreateInstance(clsid, NULL, CLSCTX_ALL, IID_IDispatch, (LPVOID*)&prog);

			int ctx[] = { CLSCTX_INPROC_HANDLER, CLSCTX_INPROC_SERVER, CLSCTX_LOCAL_SERVER };
			for (int i = 0; i < sizeof(ctx) / sizeof(ctx[0]); i++)
			{
				res = CoCreateInstance(clsid, NULL, ctx[i], IID_IDispatch, (LPVOID*)&prog);
				if (SUCCEEDED(res)) {
					break;
				}
			}

			PyEval_RestoreThread(_save);

			if (SUCCEEDED(res))
				return PyComObject::Create(prog);

			_com_error err(res);
			const wchar_t* errMsg = err.ErrorMessage();
			gServer->AddLog("CoCreateInstance %s error %s", objName, W2A(errMsg));
		}
		else
		{
			_com_error err(res);
			const wchar_t* errMsg = err.ErrorMessage();
			gServer->AddLog("CLSIDFromProgID %s error %s", objName, W2A(errMsg));
		}
	}

	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* PutServObject(PyGRServer* self, PyObject* args)
{
	PyObject *obj;
	if (PyArg_ParseTuple(args, "O", &obj))
	{
		Py_INCREF(obj);
		if (PyObject_TypeCheck(obj, &PyObjList::type) != 0)
		{
			if (((PyObjList*)obj)->src != NULL)
				self->session->AddToAnswer(((PyObjList*)obj)->src->Self());
		}
		else if (PyObject_TypeCheck(obj, &PyObjDict::type) != 0)
		{
			if (((PyObjDict*)obj)->src != NULL)
				self->session->AddToAnswer(((PyObjDict*)obj)->src->Self());
		}
		Py_DECREF(obj);
	}

	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* WriteObject(PyGRServer* self, PyObject* args)
{
	PyObject *obj;
	PyObject* ret = Py_False;
	if (PyArg_ParseTuple(args, "O", &obj))
	{
		Py_INCREF(obj);

		PyThreadState *_save = PyEval_SaveThread();
		if (PyObject_TypeCheck(obj, &PyObjList::type) != 0)
		{
			if (((PyObjList*)obj)->src != NULL && ((PyObjList*)obj)->src->Writing(NULL))
				ret = Py_True;
		}
		else if (PyObject_TypeCheck(obj, &PyObjDict::type) != 0)
		{
			if (((PyObjDict*)obj)->src != NULL && ((PyObjDict*)obj)->src->Writing(NULL))
				ret = Py_True;
		}
		PyEval_RestoreThread(_save);

		Py_DECREF(obj);
	}

	Py_INCREF(ret);
	return ret;
}

static PyObject* RegisterType(PyGRServer* self, PyObject* args)
{
	bool res = false;
	const wchar_t* type;
	if (PyArg_ParseTuple(args, "u", &type))
	{
		std::wstring def(type);
		res = (self->session->RegisterType(def, false) != NULL);
	}

	PyObject* ret = (res) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

static PyObject* NewDictObject(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName, *keyField;
	if (!PyArg_ParseTuple(args, "uu", &objName, &keyField))
		return NULL;

	ISessionObject *iso = self->session->CreateObject(objName, true);
	if (iso == NULL)
	{
		Py_INCREF(Py_None);
		return Py_None;
	}

	return PyObjDict::Create(iso, NULL, self, keyField);
}

static PyObject* NewObject(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName;
	if (!PyArg_ParseTuple(args, "u", &objName))
		return NULL;

	ISessionObject *iso = self->session->CreateObject(objName, true);
	if (iso == NULL)
	{
		Py_INCREF(Py_None);
		return Py_None;
	}

	return PyObjList::Create(iso, NULL, self);
}

static PyObject* ChangeUser(PyGRServer* self, PyObject* args)
{
	bool res = false;
	const wchar_t* userid, *pwd = NULL;
	if (PyArg_ParseTuple(args, "u|u", &userid, &pwd))
	{
		res = self->session->Impresonate(userid, false, pwd);
	}

	PyObject* ret = (res) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

static PyObject* RestoreUser(PyGRServer* self, PyObject* args)
{
	self->session->RestoreUser(true);

	PyObject* ret = Py_None;
	Py_INCREF(ret);
	return ret;
}

#ifdef USE_CURL
#include "curl_service.h"
static PyObject* GetCurl(PyGRServer* self, PyObject* args)
{
	PyObject* ret = PyCurl::Create((CurlService*)gServer->GetService(CURL_SERVICE));
	//Py_INCREF(ret);
	return ret;
}
#endif

static PyObject* Execute(PyGRServer* self, PyObject* args)
{
	bool res = false;
	wchar_t* stmt;
	if (PyArg_ParseTuple(args, "u", &stmt))
	{
		PyThreadState *_save = PyEval_SaveThread();
		res = self->session->Execute(stmt);
		PyEval_RestoreThread(_save);
	}

	PyObject* ret = (res) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

static void ExtractGroupExpr(std::wstring *group, std::wstring* typdef)
{
	size_t start = 0;
	while (true)
	{
		size_t pos = typdef->find_first_of(L'[', start);
		if (pos == std::wstring::npos)
			break;

		if (typdef->at(pos - 1) != L')')
		{
			start = pos + 1;
		}
		else 
		{
			std::wstring cg;
			size_t cp = pos - 2;
			while (cp > start)
			{
				wchar_t sym = typdef->at(cp--);
				if (sym != L'(')
					cg.insert(0, 1, sym);
				else
				{
					typdef->erase(cp + 1, (pos - cp) -1);
					group->append(cg).append(L";");
					break;
				}
			}
		}
	}

	if (!group->empty())
		group->erase(group->size() - 1, 1);
}

static PyObject* GetExchangeFolder(PyGRServer* self, PyObject* args)
{
	const IServerConfig &cfg = self->session->Config();
	USES_CONVERSION;
	const wchar_t* p = A2W(cfg.ExchangeFolder());
	return PyUnicode_FromKindAndData(sizeof(wchar_t), p, wcslen(p));
}

static PyObject* GetImageFolder(PyGRServer* self, PyObject* args)
{
	const IServerConfig &cfg = self->session->Config();
	const char *ap = cfg.ImageFolder();
	USES_CONVERSION;
	const wchar_t* p = A2W(cfg.ImageFolder());
	return PyUnicode_FromKindAndData(sizeof(wchar_t), p, wcslen(p));
}

//QueryTypeName[date:dt,created:dt,items(created)[id@id_i:s]]
static PyObject* Query(PyGRServer* self, PyObject* args)
{
	PyObject *ret = NULL;
	const wchar_t* wstmt, *name;
	if (PyArg_ParseTuple(args, "uu", &wstmt, &name))
	{
		std::wstring typeDef(name), group;

		ExtractGroupExpr(&group, &typeDef);

		PyThreadState *_save = PyEval_SaveThread();
		ISessionObject* so = self->session->Query(wstmt, typeDef.c_str(), group.c_str());
		PyEval_RestoreThread(_save);

		if (so != NULL)
			ret = PyObjList::Create(so, NULL, self);

		}

	if (ret == NULL)
	{
		ret = Py_None;
		Py_INCREF(ret);
	}
	return ret;
}

static PyObject* GetConfig(PyGRServer* self, PyObject* args)
{
	PyObject *ret = NULL;
	char* key;
	if (PyArg_ParseTuple(args, "s", &key))
	{
		USES_CONVERSION;
		const std::map<std::string, std::string> &cfg = self->reporter->ReporterConfig().configs;
		std::map<std::string, std::string>::const_iterator fnd = cfg.find(key);
		if (fnd != cfg.end())
		{
			wchar_t* p = A2W(fnd->second.c_str());
			size_t cb = wcslen(p);
			ret = PyUnicode_FromKindAndData(sizeof(wchar_t), p, cb);
		}
	}

	if (ret == NULL)
	{
		ret = Py_None;
		Py_INCREF(ret);
	}
	return ret;
}

static PyObject* GetCachedCOM(PyGRServer* self, PyObject* args)
{
	PyObject *ret = NULL;
	char* tag;
	if (PyArg_ParseTuple(args, "s", &tag))
	{
		IDispatch* obj = self->reporter->GetCOMObject(tag);
		if (obj != NULL)
		{
			ret = PyComObject::Create(obj);
		}
	}

	if (ret == NULL)
	{
		ret = Py_None;
		Py_INCREF(ret);
	}
	return ret;
}

static PyObject* PutCOMToCache(PyGRServer* self, PyObject* args)
{
	char* tag;
	PyObject *src = NULL;
	if (PyArg_ParseTuple(args, "sO", &tag, &src) && src != NULL && src->ob_type == &PyComObject::type)
	{
		self->reporter->PutCOMObject(tag, ((PyComObject*)src)->src);
	}

	PyObject *ret = Py_None;
	Py_INCREF(ret);
	return ret;
}

#define _USE_MATH_DEFINES
#include <math.h>

const double DC_FACTOR = 1852; // distance convert factor
const double A = 6378.137 / 1.852; // ellipse
const double F = 1 / 298.257223563; // ellipse
const int MAXITER = 100;
const double EPS = 0.00000000005;
const double GPS_SCALE = 1;
const double PI_2 = 2 * M_PI;

inline double modcrs(double x)
{
	return fmod(x, PI_2);
}

static double crsdist_ell(double lat1, double lon1, double lat2, double lon2)
{
	double r, tu1, tu2, cu1, su1, cu2, s1, b1, f1;
	double x, sx = 0, cx = 0, sy = 0, cy = 0, y = 0, sa, c2a = 0, cz = 0, e = 0, c, d;
	double faz, baz, s;
	int iter = 1;

	if ((lat1 + lat2 == 0) && (fabs(lon1 - lon2) == M_PI))
	{
		lat1 += 0.00001; // allow algorithm to complete
	}

	if (lat1 == lat2 && (lon1 == lon2 || fabs(fabs(lon1 - lon2) - PI_2) < EPS))
	{
		return 0;
	}

	r = 1 - F;
	tu1 = r * tan(lat1);
	tu2 = r * tan(lat2);
	cu1 = 1 / sqrt(1 + tu1 * tu1);
	su1 = cu1 * tu1;
	cu2 = 1 / sqrt(1 + tu2 * tu2);
	s1 = cu1 * cu2;
	b1 = s1 * tu2;
	f1 = b1 * tu1;
	x = lon2 - lon1;
	d = x + 1; // force one pass

	while ((fabs(d - x) > EPS) && (iter < MAXITER))
	{
		iter++;
		sx = sin(x);
		cx = cos(x);
		tu1 = cu2 * sx;
		tu2 = b1 - su1 * cu2 * cx;
		sy = sqrt(tu1 * tu1 + tu2 * tu2);
		cy = s1 * cx + f1;
		y = atan2(sy, cy);
		sa = s1 * sx / sy;
		c2a = 1 - sa * sa;
		cz = f1 + f1;
		if (c2a > 0)
			cz = cy - cz / c2a;

		e = cz * cz * 2 - 1;
		c = ((-3 * c2a + 4) * F + 4) * c2a * F / 16;
		d = x;
		x = ((e * cy * c + cz) * sy * c + y) * sa;
		x = (1 - c) * x * F + lon2 - lon1;
	}

	faz = modcrs(atan2(tu1, tu2));
	baz = modcrs(atan2(cu1 * sx, b1 * cx - su1 * cu2) + M_PI);
	x = sqrt((1 / (r * r) - 1) * c2a + 1);
	x += 1;
	x = (x - 2) / x;
	c = 1 - x;
	c = (x * x / 4 + 1) / c;
	d = (0.375 * x * x - 1) * x;
	x = e * cy;
	s = ((((sy * sy * 4 - 3) * (1 - e - e) * cz * d / 6 - x) * d / 4 + cz) * sy * d + y) * c * A * r;

	return s;
}

static PyObject* EathDistance(PyGRServer* self, PyObject* args)
{
	double lat1, lat2, lon1, lon2, res=-1;
	if (PyArg_ParseTuple(args, "dddd", &lat1, &lon1, &lat2, &lon2))
	{
		lat1 = M_PI / 180 * (double)lat1;
		lat2 = M_PI / 180 * (double)lat2;
		lon1 = M_PI / 180 * (double)lon1;
		lon2 = M_PI / 180 * (double)lon2;

		res = crsdist_ell(lat1, -lon1, lat2, -lon2) * DC_FACTOR;
	}

	return PyFloat_FromDouble(res);
}

static PyMemberDef members[] = {
	//{"first", T_OBJECT_EX, offsetof(Noddy, first), 0,
	// "first name"},
	//{"last", T_OBJECT_EX, offsetof(Noddy, last), 0,
	// "last name"},
	//{"number", T_INT, offsetof(Noddy, number), 0,
	// "noddy number"},
	{NULL}  /* Sentinel */
};

static PyMethodDef methods[] = {
	{"Schedule", (PyCFunction)Schedule, METH_VARARGS,
	"Schedule(scheduleDic, writeToTb=false)"
	},
	{"TaskStatus", (PyCFunction)ScheduleTaskStatus, METH_VARARGS,
	"TaskStatus(taskId)"
	},
	{"GetTask", (PyCFunction)ScheduleGetTask, METH_VARARGS,
	"GetTask(taskId)"
	},
	{"Get", (PyCFunction)GetServObject, METH_VARARGS,
   "Return base object Get(objectName[, whereStr])"
   },
   {"Put", (PyCFunction)PutServObject, METH_VARARGS,
   "Put object to response Put(object)"
   },
   {"RegisterType", (PyCFunction)RegisterType, METH_VARARGS,
   "Create new type RegisterType(typeDef)"
   },
   {"New", (PyCFunction)NewObject, METH_VARARGS,
   "Create new object(objectName)"
   },
	{ "NewDict", (PyCFunction)NewDictObject, METH_VARARGS,
	"Create new object(objectName, keyField)"
	},
	{ "Remove", (PyCFunction)Remove, METH_VARARGS,
   "Remove objects from base(objectName[, whereStr])"
   },
   {"Write", (PyCFunction)WriteObject, METH_VARARGS,
   "Write object to server base Write(object)"
   },
   {"ChangeUser", (PyCFunction)ChangeUser, METH_VARARGS,
	"Change user to newUser ChangeUser(newUserID [,password]) password to impersonate as admin"
   },
   {"RestoreUser", (PyCFunction)RestoreUser, METH_NOARGS,
   "Restore user back RestoreUser()"
   },
   {"Execute", (PyCFunction)Execute, METH_VARARGS,
   "Execute statment Execute(stmt)"
   },
   {"Query", (PyCFunction)Query, METH_VARARGS,
   "Do query Query(stmt, objectName[, groupExpr])\ngroupExp items:f1,f2;items:f3,f4"
   },
	{"ExchangeFolder", (PyCFunction)GetExchangeFolder, METH_NOARGS,
   "Get exchange folder"
   },
	{ "ImageFolder", (PyCFunction)GetImageFolder, METH_NOARGS,
	"Get image folder"
	},
	{ "Post", (PyCFunction)PostServObject, METH_VARARGS,
   "Post object to client. File fields will create after python exit"
   },
	{"CurrentUser", (PyCFunction)GetCurrentUser, METH_NOARGS,
   "Get current user"
   },
	{"CreateObject", (PyCFunction)CreateCOMObject, METH_VARARGS,
   "Create com object"
   },
	{ "Config", (PyCFunction)GetConfig, METH_VARARGS,
	"Get python config"
	},
	{ "GetCachedCOM", (PyCFunction)GetCachedCOM, METH_VARARGS,
	"Get python config"
	},
	{ "PutCOMToCache", (PyCFunction)PutCOMToCache, METH_VARARGS,
	"Get python config"
	},
	{ "Find", (PyCFunction)FindServObject, METH_VARARGS,
	"Return base object Find(objectName)"
	},
	{ "EathDistance", (PyCFunction)EathDistance, METH_VARARGS,
	"Return double EathDistance(lat1, lon1, lat2, lon2)"
	},
#ifdef USE_CURL
	{ "Curl", (PyCFunction)GetCurl, METH_NOARGS,
	"Get CURL object()"
	},
#endif
	{ NULL }  /* Sentinel */
};

static PyGetSetDef getset[] = {
	{"Params",
	 (getter)GetParams, (setter)NoSetter,
	 "get paramters object",
	 NULL},
	 //{"last", 
	 // (getter)Noddy_getlast, (setter)Noddy_setlast,
	 // "last name",
	 // NULL},
	 {NULL}  /* Sentinel */
};

static void FreeObject(PyGRServer *obj)
{
#ifdef DEBUG_OBJECTS
	delete obj->objects;
	delete obj->lists;
	delete obj->dicts;
#endif

	obj->ob_base.ob_type->tp_free(obj);
}

PyTypeObject PyGRServer::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	"grServer",              /* tp_name */
	sizeof(PyGRServer),      /* tp_basicsize */
	0,                       /* tp_itemsize */
	(destructor)FreeObject,  /* tp_dealloc */
	0,                       /* tp_print */
	0,                       /* tp_getattr */
	0,                       /* tp_setattr */
	0,                       /* tp_compare */
	0,                       /* tp_repr */
	0,                       /* tp_as_number */
	0,                       /* tp_as_sequence */
	0,                       /* tp_as_mapping */
	0,                       /* tp_hash */
	0,                       /* tp_call */
	0,                       /* tp_str */
	0,                       /* tp_getattro */
	0,                       /* tp_setattro */
	0,                       /* tp_as_buffer */
	Py_TPFLAGS_DEFAULT,      /* tp_flags */
	0,                       /* tp_doc */
	0,                       /* tp_traverse */
	0,                       /* tp_clear */
	0,                       /* tp_richcompare */
	0,                       /* tp_weaklistoffset */
	0,                       /* tp_iter */
	0,                       /* tp_iternext */
	methods,                 /* tp_methods */
	members,                 /* tp_members */
	getset,                  /* tp_getset */
	0,                       /* tp_base */
	0,                       /* tp_dict */
	0,                       /* tp_descr_get */
	0,                       /* tp_descr_set */
	0,                       /* tp_dictoffset */
	0,                       /* tp_init */
	0,                       /* tp_alloc */
	0,                       /* tp_new */
};

void PyGRServer::Init()
{
	PyDateTime_IMPORT;
	PyType_Ready(&type);
}

#ifdef DEBUG_OBJECTS
void PyGRServer::Remove(PyObjList* list)
{
	std::vector<PyObjList*>::iterator i = lists->begin();
	for (; i != lists->end(); i++)
		if (*i == list)
		{
			lists->erase(i);
			break;
}
}

void PyGRServer::Remove(PyObjDict* dict)
{
	std::vector<PyObjDict*>::iterator i = dicts->begin();
	for (; i != dicts->end(); i++)
		if (*i == dict)
		{
			dicts->erase(i);
			break;
		}
}

void PyGRServer::Remove(PythonObject* obj)
{
	std::vector<PythonObject*>::iterator i = objects->begin();
	for (; i != objects->end(); i++)
		if (*i == obj)
		{
			objects->erase(i);
			break;
		}
}

#endif

PyObject* PyGRServer::Create(ISession* session, GRServer::ReporterPlugin *reporter)
{
	PyGRServer* object = (PyGRServer*)type.tp_alloc(&type, 0);
	object->session = session;
	object->haveParams = false;
	object->reporter = reporter;

#ifdef DEBUG_OBJECTS
	object->objects = new std::vector<PythonObject*>();
	object->lists = new std::vector<PyObjList*>();
	object->dicts = new std::vector<PyObjDict*>();
#endif

	Py_INCREF(object);
	return (PyObject*)object;
}
