// Reporter.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "Reporter.h"
#include "PyObjects.h"
#include <isessobj.h>

#include <structmember.h>

using namespace GRServer;

static PyObject* NewObj(PyObjList* self, PyObject* param)
{
   Object *o = self->servObj->AddObject();
	PyObject* ret = PythonObject::Create(o, self->server);
	if (ret == NULL)
		return NULL;

	if (PyList_Append((PyObject*)self, ret) < 0)
	{
		Py_DECREF(ret);
		ret = NULL;
	}

   return ret;
}

static PyObject* NewObjDict(PyObjDict* self, PyObject* param)
{
	Object *o = self->servObj->AddObject();
	PyObject* obj = PythonObject::Create(o, self->server);

	return obj;
}

static PyObject* RemoveObjDict(PyObjDict* self, PyObject* args)
{
	PythonObject *res = NULL;
	PyObject *param = NULL;
	if (PyArg_ParseTuple(args, "O", &param) )
		res = (PythonObject*)PyDict_GetItem((PyObject*)self, param);

	if (res == NULL)
	{
		Py_INCREF(Py_False);
		return Py_False;
	}

	Object* rmvd = res->src;

	PyDict_DelItem((PyObject*)self, param);

	ServObject *so = self->src->Self();
	ServObject::iterator i = so->begin();
	for (; i != so->end(); i++)
	{
		if (i->p == rmvd)
		{
			so->erase(i);
			delete rmvd;
			break;
		}
	}

	Py_INCREF(Py_True);
	return Py_True;
}

static PyObject* AddObj(PyObjList* self, PyObject* args)
{
	PythonObject *po;
   if ( PyArg_ParseTuple(args, "O!", &PythonObject::type, &po) )
   {
		if( po->src != NULL )
		{
	      Py_INCREF(po);
		   Object *o = self->servObj->AddObject();
			po->src->Copy(o);

			PyObject* ret = PythonObject::Create(o, self->server);
			if (ret != NULL)
			{
				int res = PyList_Append((PyObject*)self, ret);
				Py_DECREF(po);

				if (res < 0)
				{
					Py_DECREF(ret);
					ret = NULL;
				}
			}
			return ret;
		}
   }

   Py_INCREF(Py_None);
   return Py_None;
}

static PyObject* Clear(PyObjList* self)
{
	self->servObj->clear();
	PyList_SetSlice((PyObject*)self, 0, PyList_Size((PyObject*)self), NULL);

	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* ListHaveField(PyObjList* self, PyObject* args)
{
	PyObject* ret = Py_False;

	char *fieldName;
	if (PyArg_ParseTuple(args, "s", &fieldName))
	{
		if (self->servObj != NULL)
		{
			USES_CONVERSION;
			wchar_t* fName = A2W(fieldName);
			if (self->servObj->format->FindMember(fName) >= 0)
				ret = Py_True;
		}

	}
	Py_INCREF(ret);
	return ret;
}


static void FreeObject(PyObjList *obj)
{
#ifdef DEBUG_OBJECTS_OBJECTS
   obj->server->Remove(obj);
#endif
	Py_DECREF(obj->name);
   PyList_Type.tp_dealloc((PyObject*)obj);
}

static PyObject* GetFields(Format* fmt)
{
	USES_CONVERSION;
	PyObject *ret = PyList_New(0);
	std::vector<MemberFormat>::const_iterator i = fmt->begin();
	for (; i != fmt->end(); i++)
	{
		PyObject* f = PyObjMemberFormat::Create(*i);
		if (f == NULL || PyList_Append(ret, f) < 0)
		{
			Py_XDECREF(f);
			Py_DECREF(ret);

			ret = NULL;
			break;
		}
	}

	return ret;
}

static PyObject* ListGetField(PyObjList* self)
{
	PyObject* ret = NULL;
	if (self->servObj != NULL)
		ret = GetFields(self->servObj->format);

	//if (ret == NULL)
	//{
	//	ret = Py_None;
	//	Py_INCREF(ret);
	//}
	return ret;
}

static PyMemberDef members[] = {
    {"GetName", T_OBJECT_EX, offsetof(PyObjList, name), 0,
     "Name of the server object"},
    {NULL}  /* Sentinel */
};

static PyMethodDef methods[] = {
    { "New", (PyCFunction)NewObj, METH_NOARGS, "Add new object to collection" },
    { "Add", (PyCFunction)AddObj, METH_VARARGS, "Add existing object to collection" },
	 { "Clear", (PyCFunction)Clear, METH_NOARGS, "Clear collection" },
	 { "HaveField", (PyCFunction)ListHaveField, METH_VARARGS, "True if object have filedName" },
	 { "Fields", (PyCFunction)ListGetField, METH_NOARGS, "Get list of the fields" },
	 { NULL }  /* Sentinel */
};

PyTypeObject PyObjList::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	"grserver.PyObjList",
	 sizeof(PyObjList),       /* tp_basicsize */
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
	 Py_TPFLAGS_DEFAULT |
		Py_TPFLAGS_BASETYPE,   /* tp_flags */
	 0,                       /* tp_doc */
	 0,                       /* tp_traverse */
	 0,                       /* tp_clear */
	 0,                       /* tp_richcompare */
	 0,                       /* tp_weaklistoffset */
	 0,                       /* tp_iter */
	 0,                       /* tp_iternext */
	 methods,                 /* tp_methods */
	 members,                 /* tp_members */
	 0,                       /* tp_getset */
	 0,                       /* tp_base */
	 0,                       /* tp_dict */
	 0,                       /* tp_descr_get */
	 0,                       /* tp_descr_set */
	 0,                       /* tp_dictoffset */
	 0,                       /* tp_init */
	 0,                       /* tp_alloc */
	 0,                       /* tp_new */ 
};

PyObject* PyObjList::Create(GRServer::ISessionObject* obj, GRServer::ServObject* servObj, PyGRServer* server)
{
   PyObject* list = type.tp_alloc(&type, 0);
   ((PyObjList*)list)->server = server;

   ((PyObjList*)list)->src = obj;
	
   ServObject* so = (servObj) ? servObj : obj->Self();
   ((PyObjList*)list)->servObj = so;

	if (((PyObjList*)list)->servObj != NULL)
	{
		const std::wstring& name = ((PyObjList*)list)->servObj->format->name;
		((PyObjList*)list)->name = PyUnicode_FromKindAndData(sizeof(wchar_t), name.c_str(), name.size());
	}
	else
	{
		((PyObjList*)list)->name = PyUnicode_New(0, 1);
	}


   ServObject::iterator i = so->begin();
   for( ; i != so->end(); i++ )
   {
      PyObject* obj = PythonObject::Create(*i, server);
		int res = PyList_Append(list, obj);
      Py_DECREF(obj);
		if (res < 0)
		{
			Py_DECREF(list);
			list = NULL;
			break;
		}
   }

#ifdef DEBUG_OBJECTS
   server->Add((PyObjList*)list);
#endif

   return list;
}


void PyObjList::Init()
{
   type.tp_base = &PyList_Type;
   PyType_Ready(&type);
}


//
//-------------------------------------------- Dict Obj------------------------
//


static void DictFreeObject(PyObjDict *obj)
{
#ifdef DEBUG_OBJECTS
   obj->server->Remove(obj);
#endif
	Py_DECREF(obj->name);
   PyDict_Type.tp_dealloc((PyObject*)obj);
}

static PyObject* DictHaveField(PyObjDict* self, PyObject* args)
{
	PyObject* ret = Py_False;

	char *fieldName;
	if (PyArg_ParseTuple(args, "s", &fieldName))
	{
		if (self->servObj != NULL)
		{
			USES_CONVERSION;
			wchar_t* fName = A2W(fieldName);
			if (self->servObj->format->FindMember(fName) >= 0)
				ret = Py_True;
		}

	}
	Py_INCREF(ret);
	return ret;
}

static PyObject* DictGetField(PyObjDict* self)
{
	PyObject* ret = NULL;
	if (self->servObj != NULL)
		ret = GetFields(self->servObj->format);

	//if (ret == NULL)
	//{
	//	ret = Py_None;
	//	Py_INCREF(ret);
	//}
	return ret;
}

static PyMethodDef dic_methods[] = {
	{ "New", (PyCFunction)NewObjDict, METH_NOARGS,
	"Add new object to collection"
	},
	{ "RemoveObject", (PyCFunction)RemoveObjDict, METH_VARARGS, "Remove object from dictionary by key" },
	{ "HaveField", (PyCFunction)DictHaveField, METH_VARARGS, "True if object have filedName" },
	{ "Fields", (PyCFunction)DictGetField, METH_NOARGS, "Get list of the fields" },
	{ NULL }  /* Sentinel */
};

static PyMemberDef dic_members[] = {
    {"GetName", T_OBJECT_EX, offsetof(PyObjDict, name), 0,
     "Name of the server object"},
    {NULL}  /* Sentinel */
};

PyTypeObject PyObjDict::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	 "grserver.PyObjDict",    /* tp_name */
	 sizeof(PyObjDict),       /* tp_basicsize */
	 0,                       /* tp_itemsize */
	 (destructor)DictFreeObject,  /* tp_dealloc */
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
	 Py_TPFLAGS_DEFAULT |
		Py_TPFLAGS_BASETYPE,   /* tp_flags */
	 0,                       /* tp_doc */
	 0,                       /* tp_traverse */
	 0,                       /* tp_clear */
	 0,                       /* tp_richcompare */
	 0,                       /* tp_weaklistoffset */
	 0,                       /* tp_iter */
	 0,                       /* tp_iternext */
	 dic_methods,             /* tp_methods */
	 dic_members,             /* tp_members */
	 0,                       /* tp_getset */
	 0,                       /* tp_base */
	 0,                       /* tp_dict */
	 0,                       /* tp_descr_get */
	 0,                       /* tp_descr_set */
	 0,                       /* tp_dictoffset */
	 0,                       /* tp_init */
	 0,                       /* tp_alloc */
	 0,                       /* tp_new */
};
PyObject* PyObjDict::Create(GRServer::ISessionObject* obj, GRServer::ServObject* servObj, PyGRServer* server, const wchar_t* keyId)
{
   PyObject* arg = PyTuple_New(0);
   PyObject* dict = type.tp_new(&type, arg, Py_None);
   Py_DECREF(arg);
	if (dict == NULL)
	{
		return NULL;
	}

   ((PyObjDict*)dict)->server = server;

   ((PyObjDict*)dict)->src = obj;
   ServObject* so = (servObj) ? servObj : obj->Self();
   ((PyObjDict*)dict)->servObj = so;

	if (((PyObjDict*)dict)->servObj != NULL)
	{
		const std::wstring& name = ((PyObjDict*)dict)->servObj->format->name;
		((PyObjDict*)dict)->name = PyUnicode_FromKindAndData(sizeof(wchar_t), name.c_str(), name.size());
	}
	else
	{
		((PyObjDict*)dict)->name = PyUnicode_New(0, 1);
	}

	((PyObjDict*)dict)->keyIndex = so->format->FindMember(keyId);
	if (((PyObjDict*)dict)->keyIndex >= 0)
   {
      ServObject::iterator i = so->begin();
      for( ; i != so->end(); i++ )
      {
         PyObject* obj = PythonObject::Create(*i, server);
			PyObject* key = ((PythonObject*)obj)->GetValue(((PyObjDict*)dict)->keyIndex);
			if (obj == NULL || key == NULL || PyDict_SetItem(dict, key, obj) < 0)
			{
				Py_XDECREF(obj);
				Py_XDECREF(key);
				Py_XDECREF(dict);
				dict = NULL;
				break;
			}
      }
   }
#ifdef DEBUG_OBJECTS
   server->Add((PyObjDict*)dict);
#endif

   return dict;
}


void PyObjDict::Init()
{
   type.tp_base = &PyDict_Type;
   PyType_Ready(&type);
}

PyObject* PyObjMemberFormat::Create(const MemberFormat &src)
{
	USES_CONVERSION;
	PyObjMemberFormat* ret = (PyObjMemberFormat*)type.tp_alloc(&type, 0);

	if (ret)
	{
		ret->nameBuf = W2A(src.name.c_str());
		ret->name = (char*)ret->nameBuf.c_str();

		ret->memType = (int)src.type;
	}
	return (PyObject*)ret;
}

static PyMemberDef type_members[] = {
	{ "Name", T_STRING, offsetof(PyObjMemberFormat, name), 0, "Name of the field" },
	{ "Type", T_INT, offsetof(PyObjMemberFormat, memType), 0, "Field type" },
	{ NULL }  /* Sentinel */
};

PyTypeObject PyObjMemberFormat::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	"grserver.PyObjMemberFormat",    /* tp_name */
	sizeof(PyObjMemberFormat),       /* tp_basicsize */
	0,                       /* tp_itemsize */
	0,  /* tp_dealloc */
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
	Py_TPFLAGS_DEFAULT |
	Py_TPFLAGS_BASETYPE,   /* tp_flags */
	0,                       /* tp_doc */
	0,                       /* tp_traverse */
	0,                       /* tp_clear */
	0,                       /* tp_richcompare */
	0,                       /* tp_weaklistoffset */
	0,                       /* tp_iter */
	0,                       /* tp_iternext */
	0,             /* tp_methods */
	type_members,             /* tp_members */
	0,                       /* tp_getset */
	0,                       /* tp_base */
	0,                       /* tp_dict */
	0,                       /* tp_descr_get */
	0,                       /* tp_descr_set */
	0,                       /* tp_dictoffset */
	0,                       /* tp_init */
	0,                       /* tp_alloc */
	0,                       /* tp_new */
}; 

void PyObjMemberFormat::Init()
{
	PyType_Ready(&type);
}