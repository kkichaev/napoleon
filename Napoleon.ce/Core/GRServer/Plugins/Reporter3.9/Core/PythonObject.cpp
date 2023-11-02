// Reporter.cpp: определяет экспортированные функции для приложения DLL.
//

#include "stdafx.h"
#include "Reporter.h"
#include "PyObjects.h"

#include <datetime.h>

#include <isessobj.h>

using namespace GRServer;

static PyObject* MakePyField(const MemberFormat& format, Member& value, PythonObject *self)
{
   PyObject* ret = NULL;
   switch(format.type)
   {
   case MemberFormat::mtString:
   {
      USES_CONVERSION;
		int kind = sizeof(wchar_t);
		ret = PyUnicode_FromKindAndData(kind, value.str->c_str(), value.str->size());
      break;
   }
   case MemberFormat::mtNumber:
   {
		// Для больших чисел (8 байт)
      //if( format.format.fraction == 0 )
      //{
      //   int val = (int)((value.number >= 0) ? value.number + 0.0005 : value.number - 0.0005);
      //   ret = Py_BuildValue("i", val);
      //}
      //else
         ret = Py_BuildValue("d", value.number);
      break;
   }
   case MemberFormat::mtDateTime:
   {
      SYSTEMTIME st;
      FileTimeToSystemTime(&value.datetime, &st);
      ret = PyDateTime_FromDateAndTime(st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond, 0);
      break;
   }
   case MemberFormat::mtObject:
      if( value.object == NULL )
      {
         std::wstring fmt(self->src->format.name);
         fmt += L"$";
         fmt += format.name;

         ISessionObject *so = self->server->session->CreateObject(fmt, false);
         value.object = so->Self();
         ret = (PyObject*)PyObjList::Create(so, NULL, self->server);
      } else
         ret = (PyObject*)PyObjList::Create(NULL, value.object, self->server);
      break;
   case MemberFormat::mtBinary:
   {
      DWORD len = 0;
      const char* pb = "";
      if( value.binary != NULL )
      {
         len = value.binary->Size();
         pb = (const char*)value.binary->Bytes();
      }
		ret = PyBytes_FromStringAndSize(pb, len);
      break;
   }
   default: break;
   }

   //if( ret == NULL )
   //{
	  // Py_INCREF(Py_None);
   //   ret = Py_None;
   //}
   return ret;
}

static PyObject* GetObjFields(PythonObject* self)
{

	PyObject* ret = PyList_New(0);

	if (self->src != NULL)
	{
		std::vector<MemberFormat>::const_iterator i = self->src->format.begin();
		for (; i != self->src->format.end(); i++)
		{
			PyObject* str = PyUnicode_FromKindAndData(sizeof(wchar_t), i->name.c_str(), i->name.size());
			if (str == NULL || PyList_Append(ret, str) < 0)
			{
				Py_DECREF(ret);
				ret = NULL;
			}
		}
	}

	return ret;
}


static PyObject* GetAttr(PythonObject *obj, const char *name)
{
   if( obj->src == NULL )
   {
      PyErr_SetString(PyExc_TypeError, "No source in object");
      return NULL;
   }

   USES_CONVERSION;

	if( !strcmp(name, "GetName") )
	{
		return PyUnicode_FromKindAndData(sizeof(wchar_t), obj->src->format.name.c_str(), obj->src->format.name.size());
	}

	if (!strcmp(name, "_Fields"))
	{
		return GetObjFields(obj);
	}

   int idx = obj->src->format.FindMember(A2W(name));
   if( idx < 0 )
   {
      //char buf[2000];
      //sprintf(buf, "no attribute with name '%s'", name);
      //PyErr_SetString(PyExc_TypeError, buf);
		Py_INCREF(Py_None);
		return Py_None;
   }

   return obj->GetValue(idx);
}

PyObject* PythonObject::GetValue(int idx)
{
   PyObject* ret = NULL;
   std::map<int, PyObject*>::iterator fnd = values->find(idx);
   if( fnd != values->end() )
   {
      ret = fnd->second;
   } else
   {
      ret = MakePyField(src->format.at(idx), src->at(idx), this);
      (*values)[idx] = ret;
   }
   if( ret != NULL )
      Py_INCREF(ret);
   return ret;
   //return MakePyField(obj->src->format.at(idx), obj->src->at(idx), obj);
}

static PyObject* GetAttrO(PythonObject *obj, PyObject *name)
{
   PyObject* res = NULL;
   PyObject* strObj = PyObject_Str(name);

	const wchar_t* sname = NULL;
	if (strObj == NULL || (sname = PyUnicode_AsWideCharString(strObj, NULL)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
	{
		USES_CONVERSION;
		res = GetAttr(obj, W2A(sname));
	}
	PyMem_Free((void*)sname);

   if( strObj != NULL )
      Py_DECREF(strObj);
   return res;
}

static void ToString(std::wstring* str, const Member& m, const MemberFormat& f);
static void MembersPrint(std::wstring* str, const GRServer::Object& obj)
{
	Format::const_iterator fi = obj.format.begin();
	Object::const_iterator oi = obj.begin();

	str->append(L"{");
	for (; fi != obj.format.end() && oi != obj.end(); fi++, oi++)
	{
		str->append(fi->name.c_str());
		str->append(L":");
		ToString(str, *oi, *fi);
		str->append(L",");
	}

	if(*str->rbegin() != L'{')
		str->assign(str->substr(0, str->size() - 1));
	str->append(L"}");
}

static void ToString(std::wstring* str, const Member& m, const MemberFormat& f)
{
	wchar_t buf[100];
	switch (f.type)
	{
	case MemberFormat::mtString:
		str->append(1, L'"');
		str->append(m.str->c_str());
		str->append(1, L'"');
		break;

	case MemberFormat::mtNumber:
		//if( f.format.fraction == 0 )
		//   sprintf(buf, "%d", (int)m.number);
		//else
		swprintf(buf, L"%.*f", f.format.fraction, m.number);
		str->append(buf);
		break;

	case MemberFormat::mtDateTime:
	{
		SYSTEMTIME st;
		FileTimeToSystemTime(&m.datetime, &st);
		swprintf(buf, L"%02d/%02d/%d %02d:%02d:%02d", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute, st.wSecond);
		str->append(buf);
		break;
	}

	case MemberFormat::mtBinary:
	{
		swprintf(buf, L"length %d", (m.binary) ? m.binary->Size() : 0);
		str->append(buf);
		break;
	}

	case MemberFormat::mtObject:
	{
		str->append(L"[");
		if (m.object)
		{
			ServObject::const_iterator so = m.object->begin();
			for (; so != m.object->end(); so++)
			{
				MembersPrint(str, *(*so));
				str->append(L",");
			}
			str->assign(str->substr(0, str->size() - 1));
		}
		str->append(L"]");
		break;
	}
	}
}

static PyObject* ObjToString(PythonObject * obj)
{
	const Format& f = obj->src->format;
	std::wstring str;
	str += f.name.c_str();
	MembersPrint(&str, *obj->src);

	return PyUnicode_FromKindAndData(sizeof(wchar_t), str.c_str(), str.size());
}

static void SetBinary(Member& value, DWORD len, const char* bytes)
{
   Binary *b = new Binary();
   if( len > 0 )
   {
      BYTE *pb = b->Alloc(len);
      memcpy(pb, bytes, len);
   }
   if( value.binary == NULL )
      value.binary = new MemoryBinary();
   value.binary->Assign(b);
}

static int SetFromPy(const MemberFormat& format, Member& value, PyObject* obj)
{
   int res = 0;
   switch(format.type)
   {
   case MemberFormat::mtString:
   {
      USES_CONVERSION;
		if( PyUnicode_Check(obj) )
		{
			wchar_t* str = PyUnicode_AsWideCharString(obj, NULL);
			value.str->assign((str==NULL) ? L"" : str);
			PyMem_Free(str);
		} else 
		{
			PyObject *po = (PyUnicode_Check(obj)) ? obj : PyObject_Repr(obj);
			if (po != NULL)
			{
				const wchar_t* ptr = PyUnicode_AsWideCharString(po, NULL);
				if (ptr != NULL)
				{
					value.str->assign(ptr);
				}
				else
					res = -1;
				PyMem_Free((void*)ptr);
				if (!(PyUnicode_Check(obj)))
					Py_DECREF(po);
			}
			else
				res = -1;
		}
      break;
   }
   case MemberFormat::mtNumber:
   {
      if( PyNumber_Check(obj) )
      {
         PyObject* f = PyNumber_Float(obj);
         double ptr = PyFloat_AsDouble(f);
         value.number = ptr;
         Py_DECREF(f);
      } else
         res = -1;
      break;
   }
   case MemberFormat::mtDateTime:
   {
		SYSTEMTIME st;
		st.wMilliseconds = 0;
		if (!PyDateTime_Check(obj))
		{
			if (!PyDate_Check(obj))
			{
				if (!PyTime_Check(obj))
				{
					res = -1;
				}
				else
				{
					GetLocalTime(&st);
					st.wHour = PyDateTime_TIME_GET_HOUR(obj);
					st.wMinute = PyDateTime_TIME_GET_MINUTE(obj);
					st.wSecond = PyDateTime_TIME_GET_SECOND(obj);
				}
			}
			else
			{
				st.wYear = PyDateTime_GET_YEAR(obj);
				st.wMonth = PyDateTime_GET_MONTH(obj);
				st.wDay = PyDateTime_GET_DAY(obj);
				st.wHour = 0;
				st.wMinute = 0;
				st.wSecond = 0;
			}
		} else
      {
         st.wYear = PyDateTime_GET_YEAR(obj);
         st.wMonth = PyDateTime_GET_MONTH(obj);
         st.wDay = PyDateTime_GET_DAY(obj);
         st.wHour = PyDateTime_DATE_GET_HOUR(obj);
         st.wMinute = PyDateTime_DATE_GET_MINUTE(obj);
         st.wSecond = PyDateTime_DATE_GET_SECOND(obj);
      }
		if (res >= 0)
			SystemTimeToFileTime(&st, &value.datetime);
		break;
   }
   //case MemberFormat::mtObject:
   //   if( PyObject_Type(obj) != &PyServObjType )
   //      res = -1;
   //   else
   //   {
   //      ((PythonObject*)obj)->src->
   //   }
   //   break;
   case MemberFormat::mtBinary:
		if (PyUnicode_Check(obj))
		{
			Py_ssize_t len = 0;
			const wchar_t* bytes = PyUnicode_AsWideCharString(obj, &len);
			SetBinary(value, (DWORD)(len * sizeof(wchar_t)), (const char*)bytes);
			PyMem_Free((void*)bytes);
		}
		else if (PyBytes_Check(obj))
		{
			DWORD len = (DWORD)PyBytes_Size(obj);
			SetBinary(value, len, PyBytes_AsString(obj));
		}
      break;
   default: break;
   }

   return 0;
}

static int SetAttr(PythonObject *obj, const char *name, PyObject *value)
{
   if( obj->src == NULL || value == NULL )
      return -1;

   USES_CONVERSION;
   int idx = obj->src->format.FindMember(A2W(name));
	if (idx < 0)
	{
		char buf[500];
		wsprintfA(buf, "No member '%s' in object '%s'", name, W2A(obj->src->format.name.c_str()));
		PyErr_SetString(PyExc_RuntimeError, buf);
		return -1;
	}

   std::map<int, PyObject*>::iterator fnd = obj->values->find(idx);
   if( fnd != obj->values->end() )
   {
      Py_DECREF(fnd->second);
      obj->values->erase(fnd);
   }
   return SetFromPy(obj->src->format.at(idx), obj->src->at(idx), value);
}

static void FreeObject(PythonObject *obj)
{
   std::map<int, PyObject*>::iterator i = obj->values->begin();
   for( ; i != obj->values->end(); i++ )
      Py_DECREF(i->second);
   delete obj->values;

#ifdef DEBUG_OBJECTS
   obj->server->Remove(obj);
	Py_DECREF(obj->server);
#endif

	obj->ob_base.ob_type->tp_free(obj);
}

static PyObject* CreateObject(PyTypeObject *type, PyObject *args, PyObject *kwds)
{
   PythonObject* self = (PythonObject*)type->tp_alloc(type, 0);
   self->values = new std::map<int, PyObject*>();
   return (PyObject*)self;
}

static int SetAttrO(PythonObject *obj, PyObject *name, PyObject *value)
{
   int res = -1;
   PyObject* strObj = PyObject_Str(name);

	const wchar_t* sname = NULL;
	if (strObj == NULL || (sname = PyUnicode_AsWideCharString(strObj, NULL)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
	{
		USES_CONVERSION;
		res = SetAttr(obj, W2A(sname), value);
	}
	PyMem_Free((void*)sname);

   if( strObj != NULL )
      Py_DECREF(strObj);
   return res;
}


PyTypeObject PythonObject::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	 "grserver.PythonObject",         /* tp_name */
	 sizeof(PythonObject),          /* tp_basicsize */
	 0,                       /* tp_itemsize */
	 (destructor)FreeObject,  /* tp_dealloc */
	 0,                       /* tp_print */
	 (getattrfunc)GetAttr,    /* tp_getattr */
	 (setattrfunc)SetAttr,    /* tp_setattr */
	 0,                       /* tp_compare */
	 (reprfunc)ObjToString,   /* tp_repr */
	 0,                       /* tp_as_number */
	 0,                       /* tp_as_sequence */
	 0,                       /* tp_as_mapping */
	 0,                       /* tp_hash */
	 0,                       /* tp_call */
	 0,                       /* tp_str */
	 (getattrofunc)GetAttrO, /* tp_getattro */
	 (setattrofunc)SetAttrO, /* tp_setattro */
	 0,                       /* tp_as_buffer */
	 Py_TPFLAGS_DEFAULT | Py_TPFLAGS_BASETYPE,      /* tp_flags */
	 0,                       /* tp_doc */
	 0,                       /* tp_traverse */
	 0,                       /* tp_clear */
	 0,                       /* tp_richcompare */
	 0,                       /* tp_weaklistoffset */
	 0,                       /* tp_iter */
	 0,                       /* tp_iternext */
	 0,							/* tp_methods */
	 0,                       /* tp_members */
	 0,                       /* tp_getset */
	 0,                       /* tp_base */
	 0,                       /* tp_dict */
	 0,                       /* tp_descr_get */
	 0,                       /* tp_descr_set */
	 0,                       /* tp_dictoffset */
	 0,                       /* tp_init */
	 0,                       /* tp_alloc */
	 (newfunc)CreateObject,   /* tp_new */
};

PyObject* PythonObject::Create(GRServer::Object* obj, PyGRServer* server)
{
   PyObject* ret = type.tp_alloc(&type, 0);
   ((PythonObject*)ret)->values = new std::map<int, PyObject*>();
   ((PythonObject*)ret)->src = obj;
   ((PythonObject*)ret)->server = server;

#ifdef DEBUG_OBJECTS
	Py_INCREF(server);
   server->Add((PythonObject*)ret);
#endif

   return ret;
}

void PythonObject::Init()
{
   PyDateTime_IMPORT;

   PyType_Ready(&type);
}

static PyObject* SubUsers(UserObject* self)
{
	const StrSet& uu = self->server->session->AllowedUID();
	PyObject* ret = PyList_New(0);

	StrSet::const_iterator i = uu.begin();
	for (; i != uu.end(); i++)
	{
		PyObject* str = PyUnicode_FromKindAndData(sizeof(wchar_t), (*i).c_str(), (*i).size());
		PyList_Append(ret, str);
	}

	return ret;
}

static PyObject* GetUserAttr(UserObject *obj, const char *name)
{
	if (!strcmp(name, "_SubUsers"))
	{
		return SubUsers(obj);
	}

	return GetAttr(obj, name);
}

static PyObject* GetUserAttrO(UserObject *obj, PyObject *name)
{
	PyObject* res = NULL;
	PyObject* strObj = PyObject_Str(name);

	const wchar_t* sname = NULL;
	if (strObj == NULL || (sname = PyUnicode_AsWideCharString(strObj, NULL)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
	{
		USES_CONVERSION;
		res = GetUserAttr(obj, W2A(sname));
	}
	PyMem_Free((void*)sname);

	if (strObj != NULL)
		Py_DECREF(strObj);
	return res;
}

PyTypeObject UserObject::u_type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	"grserver.UserObject",         /* tp_name */
	sizeof(PythonObject),          /* tp_basicsize */
	0,                       /* tp_itemsize */
	(destructor)FreeObject,  /* tp_dealloc */
	0,                       /* tp_print */
	(getattrfunc)GetUserAttr,    /* tp_getattr */
	(setattrfunc)SetAttr,    /* tp_setattr */
	0,                       /* tp_compare */
	(reprfunc)ObjToString,   /* tp_repr */
	0,                       /* tp_as_number */
	0,                       /* tp_as_sequence */
	0,                       /* tp_as_mapping */
	0,                       /* tp_hash */
	0,                       /* tp_call */
	0,                       /* tp_str */
	(getattrofunc)GetUserAttrO, /* tp_getattro */
	(setattrofunc)SetAttrO, /* tp_setattro */
	0,                       /* tp_as_buffer */
	Py_TPFLAGS_DEFAULT | Py_TPFLAGS_BASETYPE,      /* tp_flags */
	0,                       /* tp_doc */
	0,                       /* tp_traverse */
	0,                       /* tp_clear */
	0,                       /* tp_richcompare */
	0,                       /* tp_weaklistoffset */
	0,                       /* tp_iter */
	0,                       /* tp_iternext */
	0,					/* tp_methods */
	0,                       /* tp_members */
	0,                       /* tp_getset */
	0,                       /* tp_base */
	0,                       /* tp_dict */
	0,                       /* tp_descr_get */
	0,                       /* tp_descr_set */
	0,                       /* tp_dictoffset */
	0,                       /* tp_init */
	0,                       /* tp_alloc */
	(newfunc)CreateObject,   /* tp_new */
}; 

void UserObject::Init()
{
	PyType_Ready(&u_type);
}

PyObject* UserObject::Create(GRServer::Object* obj, PyGRServer* server)
{
	PyObject* ret = u_type.tp_alloc(&u_type, 0);
	((PythonObject*)ret)->values = new std::map<int, PyObject*>();
	((PythonObject*)ret)->src = obj;
	((PythonObject*)ret)->server = server;

	return ret;
}
