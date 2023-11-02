// Reporter.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "Reporter.h"
#include "PyObjects.h"

#include <structmember.h>
#include <isessobj.h>

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

static PyObject* GetServObject(PyGRServer* self, PyObject* args)
{
	const wchar_t *objName, *whereStr = NULL, *key = NULL;
	if (!PyArg_ParseTuple(args, "u|uu", &objName, &whereStr, &key))
		return NULL;

	ISessionObject *iso = self->session->CreateObject(objName, true);
	if (iso != NULL)
	{
		PyThreadState *_save = PyEval_SaveThread();
		if (!iso->Reading(((whereStr == NULL) ? L"" : whereStr), true, true))
			iso = NULL;
		PyEval_RestoreThread(_save);
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
	int start = 0;
	while (true)
	{
		size_t pos = typdef->find_first_of(L'(', start);
		if (pos == std::wstring::npos)
			break;

		size_t lp = typdef->find_first_of(L')', pos);
		group->append(typdef->substr(pos + 1, lp - pos - 1));
		group->append(L";");
		typdef->erase(pos, lp - pos + 1);
	}
	if (!group->empty())
		group->erase(group->size() - 1);
}

static PyObject* GetExchangeFolder(PyGRServer* self, PyObject* args)
{
	const IServerConfig &cfg = self->session->Config();
#ifdef Python3
	USES_CONVERSION;
	const wchar_t* p = A2W(cfg.ExchangeFolder());
	return PyUnicode_FromKindAndData(sizeof(wchar_t), p, wcslen(p));
#else
	return PyString_FromString(cfg.ExchangeFolder());
#endif
}

static PyObject* GetImageFolder(PyGRServer* self, PyObject* args)
{
	const IServerConfig &cfg = self->session->Config();
#ifdef Python3
	const char *ap = cfg.ImageFolder();
	USES_CONVERSION;
	const wchar_t* p = A2W(cfg.ImageFolder());
	return PyUnicode_FromKindAndData(sizeof(wchar_t), p, wcslen(p));
#else
	return PyString_FromString(cfg.ImageFolder());
#endif
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

#ifdef Python3
	obj->ob_base.ob_type->tp_free(obj);
#else
	obj->ob_type->tp_free(obj);
#endif
}

#ifdef Python3
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
#else
PyTypeObject PyGRServer::type =
{
   PyObject_HEAD_INIT(NULL)
   0,                       /* ob_size */
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
#endif
void PyGRServer::Init()
{
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
