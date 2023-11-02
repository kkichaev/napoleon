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
      const char* ptr = W2A(value.str->c_str());
      ret = Py_BuildValue("s", ptr);
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
      ret = PyString_FromStringAndSize(pb, len);
      break;
   }
   default: break;
   }

   if( ret == NULL )
   {
	   Py_INCREF(Py_None);
      ret = Py_None;
   }
   return ret;
}

static PyObject* GetObjFields(PythonObject* self)
{
	USES_CONVERSION;

	PyObject* ret = PyList_New(0);

	if (self->src != NULL)
	{
		std::vector<MemberFormat>::const_iterator i = self->src->format.begin();
		for (; i != self->src->format.end(); i++)
		{
			PyObject* str = PyString_FromString(W2A(i->name.c_str()));
			PyList_Append(ret, str);
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
		const char *p = "";
		if( obj->src != NULL )
			p = W2A(obj->src->format.name.c_str());
		return PyString_FromString(p);
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
   const char* sname;
   PyObject* strObj = PyObject_Str(name);

   if( strObj == NULL || (sname = PyString_AsString(strObj)) == NULL )
      PyErr_SetString(PyExc_RuntimeError, "bad arguments");
   else
      res = GetAttr(obj, sname);

   if( strObj != NULL )
      Py_DECREF(strObj);
   return res;
}

static void ToString(std::string* str, const Member& m, const MemberFormat& f);
static void MembersPrint(std::string* str, const GRServer::Object& obj)
{
   USES_CONVERSION;

   Format::const_iterator fi = obj.format.begin();
   Object::const_iterator oi = obj.begin();

   str->append("{");
   for( ; fi != obj.format.end(); fi++, oi++ )
   {
      str->append(W2A(fi->name.c_str()));
      str->append(":");
      ToString(str, *oi, *fi);
      str->append(",");
   }

   str->assign(str->substr(0, str->size()-1));
   str->append("}");
}

static void ToString(std::string* str, const Member& m, const MemberFormat& f)
{
   char buf[100];
   USES_CONVERSION;
   switch(f.type)
   {
   case MemberFormat::mtString:
      str->append(1, '"');
      str->append(W2A(m.str->c_str()));
      str->append(1, '"');
      break;

   case MemberFormat::mtNumber:
      //if( f.format.fraction == 0 )
      //   sprintf(buf, "%d", (int)m.number);
      //else
         sprintf(buf, "%.*f", f.format.fraction, m.number);
      str->append(buf);
      break;

   case MemberFormat::mtDateTime:
      {
         SYSTEMTIME st;
         FileTimeToSystemTime(&m.datetime, &st);
         sprintf(buf, "%02d/%02d/%d %02d:%02d:%02d", st.wDay, st.wMonth, st.wYear, st.wHour, st.wMinute, st.wSecond);
         str->append(buf);
         break;
      }

   case MemberFormat::mtBinary:
      {
         sprintf(buf, "length %d", (m.binary) ? m.binary->Size() : 0);
         str->append(buf);
         break;
      }

   case MemberFormat::mtObject:
      {
         str->append("[");
         if( m.object )
         {
            ServObject::const_iterator so = m.object->begin();
            for( ; so != m.object->end(); so++ )
            {
               MembersPrint(str, *(*so));
               str->append(",");
            }
            str->assign(str->substr(0, str->size()-1));
         }
         str->append("]");
         break;
      }
   }
}

static PyObject* ObjToString(PythonObject * obj)
{
   USES_CONVERSION;

   const Format& f = obj->src->format;
   std::string str;
   str += W2A(f.name.c_str());
   MembersPrint(&str, *obj->src);

   return PyString_FromString(str.c_str());
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
			wchar_t* str = PyUnicode_AsUnicode(obj);
			value.str->assign((str==NULL) ? L"" : str);
		} else 
		{
			PyObject *po = (PyString_Check(obj)) ? obj : PyObject_Repr(obj);
			if( po != NULL )
			{
				const char* ptr = PyString_AsString(po);
				if( ptr != NULL )
				{
					int len = (int)strlen(ptr);
					if( len < 10000 )
						value.str->assign(A2W(ptr));
					else
					{
						wchar_t *dest = (wchar_t*)malloc((len + 1) * sizeof(wchar_t));
						MultiByteToWideChar(CP_ACP, 0, ptr, -1, dest, len+1);
						value.str->assign(dest);
						free(dest);
					}
				}
				else
					res = -1;
				if( !(PyString_Check(obj)) )
					Py_DECREF(po);
			} else
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
      if( PyString_Check(obj) )
      {
         DWORD len = (DWORD)PyString_GET_SIZE(obj);
         SetBinary(value, len, PyString_AS_STRING(obj));
      } else if( PyByteArray_Check(obj) )
      {
         DWORD len = (DWORD)PyByteArray_Size(obj);
         SetBinary(value, len, PyByteArray_AS_STRING(obj));
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

   obj->ob_type->tp_free(obj);
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
   const char* sname;
   PyObject* strObj = PyObject_Str(name);

   if( strObj == NULL || (sname = PyString_AsString(strObj)) == NULL )
      PyErr_SetString(PyExc_RuntimeError, "bad arguments");
   else
      res = SetAttr(obj, sname, value);

   if( strObj != NULL )
      Py_DECREF(strObj);
   return res;
}


PyTypeObject PythonObject::type = 
{
   PyObject_HEAD_INIT(NULL)
    0,                       /* ob_size */
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
	USES_CONVERSION;
	const StrSet& uu = self->server->session->AllowedUID();
	PyObject* ret = PyList_New(0);

	StrSet::const_iterator i = uu.begin();
	for (; i != uu.end(); i++)
	{
		PyObject* str = PyString_FromString(W2A((*i).c_str()));
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
	const char* sname;
	PyObject* strObj = PyObject_Str(name);

	if (strObj == NULL || (sname = PyString_AsString(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = GetUserAttr(obj, sname);

	if (strObj != NULL)
		Py_DECREF(strObj);
	return res;
}

PyTypeObject UserObject::u_type =
{
	PyObject_HEAD_INIT(NULL)
	0,                       /* ob_size */
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
