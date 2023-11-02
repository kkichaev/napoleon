#include "Python.h"
#ifdef UNIX
#include <UnixCompat.h>
#else
#include <windows.h>
#endif
#include <ServerDefs.h>
#include <socket.h>
#include <stdobjs.h>

#include <datetime.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

using namespace GRServer;

const DWORD READ_TIMEOUT = 10 * 1000;

struct ObjCollection
{
   PyListObject list;
};

struct ChildInfo
{
   ObjCollection *pyObj;
   ServObject    *servObj;
};

struct PyServObject
{
   PyObject_HEAD;
   
   Object* src;
   ObjCollection* owner;

   // для объектных полей если их получали в питоне храним объекты
   std::vector<ChildInfo> childObjects;
};

class ObjectHolder : public std::map<ObjCollection*, ServObject*>
{
public:
   ~ObjectHolder()
   {
   }

   void Add(ObjCollection* key, ServObject* value) { this->insert(value_type(key, value)); }
   void Remove(ObjCollection* key)
   {
      iterator fnd = find(key);
      if( fnd != end() )
      {
         delete (fnd->second);
         erase(fnd);
      }
   }

   ServObject* Get(ObjCollection *src)
   {
      iterator fnd = find(src);
      return (fnd == end()) ? NULL : fnd->second;
   }

   void Clear()
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         delete (i->second);
      clear();
   }
};

class ConnectionData
{
public:
   ConnectionData() : duration(0) {}
   ~ConnectionData();

   bool IsConnected() const { return !login.empty(); }
   bool Connect(const char* ip, int port, const char* login, const char* password);
   void Close();

   const char* Error() const { return error.c_str(); }

   PyObject* ReadObject(const wchar_t* cmd, const wchar_t* param);
   bool DoCommand(const wchar_t* cmd, const wchar_t* param);

   bool WriteOrReplace(ServObject* src, ObjCollection* obj, const char* userid, bool replace);

   void ObjectRemoved(ObjCollection* obj) { ObjHolder.Remove(obj); }
   ServObject* GetObject(ObjCollection* obj) { return ObjHolder.Get(obj); }

private:
   std::string ip;
   int port;

   std::wstring login;
   std::wstring password;
   int duration;

   std::string error;

   ObjCreator objCreator;
   ObjectHolder ObjHolder;
private:

   void GetDuration();
   void SaveDuration();

   void PrepareList(ExchangeList *res, const wchar_t* cmd, ServObject* object);
   ServObject* MakeCommandObject(const std::wstring& cmd, const wchar_t* param = L"");

   void SyncObject(ServObject* dest, ObjCollection* obj);

   PyObject* MakePyObject(ServObject* obj);
};

const wchar_t NULL_TAG[] = L"NULL";

ConnectionData ConnData;

static PyObject* MakePyServObject(ObjCollection* owner, Object* obj);
static void ObjCollectionDealloc(ObjCollection* obj);
static void ServObjectDealloc(PyServObject* obj);
static PyObject* GetAttr(PyServObject *obj, const char *name);
static PyObject* GetAttrO(PyServObject *obj, PyObject *name);
static int SetAttr(PyServObject *obj, const char *name, PyObject *value);
static int SetAttrO(PyServObject *obj, PyObject *name, PyObject *value);

static PyObject* ObjColNew(PyObject *self, PyObject *args);
static PyObject* ObjColWrite(PyObject *self, PyObject *args);
static PyObject* ObjColReplace(PyObject *self, PyObject *args);

static PyMethodDef ObjCollectionMethods[] = {
	{"new", ObjColNew, METH_NOARGS, "return new object added to collection"},
   {"write", ObjColWrite, METH_VARARGS, "write([userID]) write object on server"},
   {"replace", ObjColReplace, METH_VARARGS, "replace([userID]) replace object on server"},
//   {"delete", ObjColDelete, METH_VARARGS, "Delete([userID]) delete object on server"},

   {NULL, NULL}
};
static PyTypeObject ObjCollectionType = 
{
   PyObject_HEAD_INIT(NULL)
    0,                       /* ob_size */
    "grserver.ObjCollection",         /* tp_name */
    sizeof(ObjCollection),          /* tp_basicsize */
    0,                       /* tp_itemsize */
    (destructor)ObjCollectionDealloc,    /* tp_dealloc */
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
    ObjCollectionMethods,    /* tp_methods */
    0,                       /* tp_members */
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

static PyTypeObject PyServObjType = 
{
   PyObject_HEAD_INIT(NULL)
    0,                       /* ob_size */
    "grserver.ServObject",         /* tp_name */
    sizeof(PyServObject),          /* tp_basicsize */
    0,                       /* tp_itemsize */
    (destructor)ServObjectDealloc,    /* tp_dealloc */
    0,                       /* tp_print */
    (getattrfunc)GetAttr,    /* tp_getattr */
    (setattrfunc)SetAttr,    /* tp_setattr */
    0,                       /* tp_compare */
    0,                       /* tp_repr */
    0,                       /* tp_as_number */
    0,                       /* tp_as_sequence */
    0,                       /* tp_as_mapping */
    0,                       /* tp_hash */
    0,                       /* tp_call */
    0,                       /* tp_str */
    (getattrofunc)GetAttrO, /* tp_getattro */
    (setattrofunc)SetAttrO, /* tp_setattro */
    0,                       /* tp_as_buffer */
    Py_TPFLAGS_DEFAULT,      /* tp_flags */
    0,                       /* tp_doc */
    0,                       /* tp_traverse */
    0,                       /* tp_clear */
    0,                       /* tp_richcompare */
    0,                       /* tp_weaklistoffset */
    0,                       /* tp_iter */
    0,                       /* tp_iternext */
    0,                       /* tp_methods */
    0,                       /* tp_members */
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
static ObjCollection* ObjCollectionInit(ServObject* so)
{
   ObjCollection* ret = (ObjCollection*)ObjCollectionType.tp_alloc(&ObjCollectionType, 0);
   ServObject::iterator i = so->begin();
   for( ; i != so->end(); i++ )
      PyList_Append((PyObject*)ret, MakePyServObject(ret, *i));

   return ret;
}

static void ObjCollectionDealloc(ObjCollection* obj)
{
   ConnData.ObjectRemoved(obj);
   PyList_Type.tp_dealloc((PyObject*)obj);
}

static void ServObjectDealloc(PyServObject* obj)
{
   ServObject* owner = ConnData.GetObject(obj->owner);
   if( obj->src != NULL && owner != NULL )
   {
      ServObject::iterator i = owner->begin();
      for( ; i != owner->end(); i++ )
      {
         Object* o = (*i);
         if( o == obj->src )
         {
            *i = NULL;
            owner->erase(i);
            delete o;
            break;
         }
      }
   }
}

static PyObject* GetAttr(PyServObject *obj, const char *name);
static PyObject* GetAttrO(PyServObject *obj, PyObject *name)
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

static int SetAttr(PyServObject *obj, const char *name, PyObject *value);
static int SetAttrO(PyServObject *obj, PyObject *name, PyObject *value)
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

static PyObject* MakePyField(const MemberFormat& format, Member& value, PyServObject *owner)
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
      if( value.object != NULL )
      {
         ret = (PyObject*)ObjCollectionInit(value.object);
         ChildInfo ci;
         ci.pyObj = (ObjCollection*)ret;
         ci.servObj = value.object;
         owner->childObjects.push_back(ci);
      }
      break;
   //case MemberFormat::mtBinary:
   //   break;
   default: break;
   }

   if( ret == NULL )
   {
	   Py_INCREF(Py_None);
      ret = Py_None;
   }
   return ret;
}

static int SetFromPy(const MemberFormat& format, Member& value, PyObject* obj)
{
   int res = 0;
   switch(format.type)
   {
   case MemberFormat::mtString:
   {
      USES_CONVERSION;
      const char* ptr;
      if( !PyArg_ParseTuple(obj, "s", &ptr) )
         res = -1;
      else
         value.str->assign(A2W(ptr));
      break;
   }
   case MemberFormat::mtNumber:
   {
      double ptr;
      if( !PyArg_ParseTuple(obj, "d", &ptr) )
         res = -1;
      else
         value.number = ptr;
      break;
   }
   case MemberFormat::mtDateTime:
   {
      if( !PyDateTime_Check(obj) )
         res = -1;
      else
      {
         SYSTEMTIME st;
         st.wYear = PyDateTime_GET_YEAR(obj);
         st.wMonth = PyDateTime_GET_MONTH(obj);
         st.wDay = PyDateTime_GET_DAY(obj);
         st.wHour = PyDateTime_DATE_GET_HOUR(obj);
         st.wMinute = PyDateTime_DATE_GET_MINUTE(obj);
         st.wSecond = PyDateTime_DATE_GET_SECOND(obj);
         st.wMilliseconds = 0;
         SystemTimeToFileTime(&st, &value.datetime);
      }
      break;
   }
   //case MemberFormat::mtObject:
   //   if( PyObject_Type(obj) != &PyServObjType )
   //      res = -1;
   //   else
   //   {
   //      ((PyServObject*)obj)->src->
   //   }
   //   break;
   //case MemberFormat::mtBinary:
   //   break;
   default: break;
   }

   return 0;
}

static PyObject* GetAttr(PyServObject *obj, const char *name)
{
   if( obj->src == NULL )
      return NULL;

   USES_CONVERSION;
   int idx = obj->src->format.FindMember(A2W(name));
   if( idx < 0 )
      return NULL;

   return MakePyField(obj->src->format.at(idx), obj->src->at(idx), obj);
}

static int SetAttr(PyServObject *obj, const char *name, PyObject *value)
{
   if( obj->src == NULL || value == NULL )
      return -1;

   USES_CONVERSION;
   int idx = obj->src->format.FindMember(A2W(name));
   if( idx < 0 )
      return -1;

   return SetFromPy(obj->src->format.at(idx), obj->src->at(idx), value);
}


static PyObject* MakePyServObject(ObjCollection* owner, Object* obj)
{
   PyServObject* so = (PyServObject*)PyServObjType.tp_alloc(&PyServObjType, 0);
   so->owner = owner;
   so->src = obj;
   return (PyObject*)so;
}

static PyObject* ObjColNew(PyObject *self, PyObject *args)
{
   ServObject* so = ConnData.GetObject((ObjCollection*)self);
   if( so == NULL )
   {
      PyErr_SetString(PyExc_RuntimeError, "no server object");
      return NULL;
   }

   Object *newObj = so->AddObject();
   return MakePyServObject((ObjCollection*)self, newObj);
}


static PyObject* ObjColWrite(PyObject *self, PyObject *args)
{
   const char* userid = NULL;
   if( !PyArg_ParseTuple(args, "(s)", &userid) )
   {
      PyErr_SetString(PyExc_RuntimeError, "argument error");
      return NULL;
   }

   ServObject* so = ConnData.GetObject((ObjCollection*)self);
   if( so == NULL )
   {
      PyErr_SetString(PyExc_RuntimeError, "no server object");
      return NULL;
   }

   if( so->size() == 0 )
   {
      PyErr_SetString(PyExc_RuntimeError, "empty server object");
      return NULL;
   }

   ConnData.WriteOrReplace(so, (ObjCollection*)self, userid, false);
   Py_INCREF(Py_None);
   return Py_None;
}

static PyObject* ObjColReplace(PyObject *self, PyObject *args)
{
   const char* userid;
   if( !PyArg_ParseTuple(args, "s", &userid) )
   {
      PyErr_SetString(PyExc_RuntimeError, "argument error");
      return NULL;
   }

   ServObject* so = ConnData.GetObject((ObjCollection*)self);
   if( so == NULL )
   {
      PyErr_SetString(PyExc_RuntimeError, "no server object");
      return NULL;
   }

   if( so->size() == 0 )
   {
      PyErr_SetString(PyExc_RuntimeError, "empty server object");
      return NULL;
   }

   ConnData.WriteOrReplace(so, (ObjCollection*)self, userid, true);
   Py_INCREF(Py_None);
   return Py_None;
}

ConnectionData::~ConnectionData()
{
   ObjHolder.Clear();
}

void ConnectionData::Close()
{
   ObjHolder.Clear();
   login.clear();
}

bool ConnectionData::Connect(const char* ip, int port, const char* alogin, const char* apassword)
{
   USES_CONVERSION;

   login = (alogin == NULL) ? COM_LOGIN : A2W(alogin);
   password = (apassword == NULL) ? L"" : A2W(apassword);

   this->ip = ip;
   this->port = port;

   bool ret = false;
   Socket s;
   if( s.Connect(ip, port) )
   {
      GetDuration();
      if( SendCommand(&s, GET_COMMAND, L"Agents", login.c_str(), password.c_str(), duration) )
      {
         bool bret;
         std::wstring answ;
         if( ReadAnswer(&s, READ_TIMEOUT, &bret, &answ, &duration) )
         {
            if( bret )
            {
               ret = bret;
               SaveDuration();
            }
            else
               error = W2A(answ.c_str());
         } else
            error = "No answer";
      } else
         error = "Can't send command";
   } else
   {
      char buf[500];
      PyOS_snprintf(buf, sizeof(buf), "Can't connect %s:%d", ip, port);
      error = buf;
   }

   return ret;
}

void ConnectionData::GetDuration()
{
   char buf[MAX_PATH];
#ifdef UNIX
   strcat(buf, "/tmp/grserver.tmp");
#else
   GetTempPathA(sizeof(buf)/sizeof(buf[0]), buf);
   strcat(buf, "\\grserver.tmp");
#endif

   FILE* f = fopen(buf, "rb");
   int res = 0;
   if( f )
   {
      fread(&res, sizeof(res), 1, f);
      fclose(f);
   }

   duration = res;
}

void ConnectionData::SaveDuration()
{
   char buf[MAX_PATH];
#ifdef UNIX
   strcat(buf, "/tmp/grserver.tmp");
#else
   GetTempPathA(sizeof(buf)/sizeof(buf[0]), buf);
   strcat(buf, "\\grserver.tmp");
#endif

   FILE* f = fopen(buf, "wb");
   if( f )
   {
      fwrite(&duration, sizeof(duration), 1, f);
      fclose(f);
   }
}

PyObject* ConnectionData::MakePyObject(ServObject* obj)
{
   ObjCollection* ret = ObjCollectionInit(obj);
   ObjHolder.Add(ret, obj);
   return (PyObject*)ret;
}

bool ConnectionData::DoCommand(const wchar_t* cmd, const wchar_t* param)
{
   bool res = false;
   Socket s;

   if( s.Connect(ip.c_str(), port) && SendCommand(&s, cmd, param, login.c_str(), password.c_str(), duration) )
   {
      SendCommand(&s, BYE_COMMAND, L"", login.c_str(), password.c_str(), duration);
      res = true;
   }

   return res;
}

PyObject* ConnectionData::ReadObject(const wchar_t* cmd, const wchar_t* param)
{
   Socket s;
   PyObject *res = NULL;

   if( s.Connect(ip.c_str(), port) && SendCommand(&s, cmd, param, login.c_str(), password.c_str(), duration) )
   {
      Binary buf;
      ExchangeList el(objCreator.GetFormatList());
      if( el.Read(&buf, &s, READ_TIMEOUT, NULL, &objCreator, true) && el.size() > 1 )
      {
         ServObject* curObj = el.at(1);
         SendCommand(&s, BYE_COMMAND, L"", login.c_str(), password.c_str(), duration);

         if( (res = MakePyObject(curObj)) != NULL )
            el.at(1) = NULL;
      } else
      {
         if( el.size() > 0 )
         {
            ServObject* curObj = el.at(0);
            int i = curObj->format->FindMember(MESSAGE_MEMBER);
            int ir = curObj->format->FindMember(RESPONSE_MEMBER);
            if( i >= 0 )
            {
               USES_CONVERSION;
               Object* co = curObj->at(0);
               CString *str = co->at(i).str;
               bool response = (co->at(ir).number > 0);
               error = ( str->empty() || response ) ? "Object missed or empty" : W2A(str->c_str());
            }
         } else
         {
            error = "Server not response";
         }
      }
   } else
      error = "Server not response";

   return res;
}

static bool HaveObject(const ServObject& src, Object* o)
{
   ServObject::const_iterator i = src.begin();
   for( ; i != src.end(); i++ )
      if( (*i) == o )
         return true;

   return false;
}

static void RemoveExisting(ExchangeList *list, ServObject* src)
{
   ExchangeList::iterator li = list->begin();
   for( ; li != list->end(); li++ )
   {
      if( (*li) == src )
      {
         (*li) = NULL;
         continue;
      }

      ServObject::iterator oi = (*li)->begin();
      for( ; oi != (*li)->end(); oi++ )
      {
         if( HaveObject(*src, (*oi)) )
            (*oi) = NULL;
      }
   }
}

static void MakeCommand(std::wstring *res, const wchar_t* cmd, const wchar_t* userid)
{
   *res = cmd;
   if( *userid != L'\0' )
   {
      res->append(L" "); res->append(IMPERSONATE); res->append(L" ");
      if( _wcsicmp(userid, NULL_TAG) == 0 )
         res->append(NULL_TAG);
      else
      {
         res->append(L"'");
         res->append(userid);
         res->append(L"'");
      }
   }
}

ServObject* ConnectionData::MakeCommandObject(const std::wstring& cmd, const wchar_t* param)
{
   FormatList* list = objCreator.GetFormatList();
   Format* fmt = list->GetFormat(ServerCommandFormat::Name());
   if( fmt == NULL )
   {
      fmt = new ServerCommandFormat();
      list->AddFormat(fmt, true);
   }

   ServObject *so = new ServObject(fmt);
   Object *ocmd = so->AddObject();

   (*ocmd)[COMMAND_MEMBER]->str->assign(cmd);
   (*ocmd)[PARAM_MEMBER]->str->assign(param);
   (*ocmd)[USERID_MEMBER]->str->assign(login);
   (*ocmd)[PASSWORD_MEMBER]->str->assign(password);
   (*ocmd)[DURATION_MEMBER]->number = duration;

   return so;
}

void ConnectionData::PrepareList(ExchangeList *res, const wchar_t* cmd, ServObject* object)
{
   int uindex = object->format->FindMember(USERID_MEMBER);
   if( uindex < 0 )
   {
      ServObject *so = MakeCommandObject(cmd);
      res->push_back(so);
      res->push_back(object);

      return;
   }

   CString uid, scmd;
   ServObject::iterator i = object->begin();
   for( ; i != object->end(); i++ )
   {
      CString *cuid = (*i)->at(uindex).str;
      if( i == object->begin() || uid.compare(*cuid) != 0 )
      {
         std::wstring tcmd;

         uid = *cuid;
         MakeCommand(&tcmd, cmd, uid.c_str());

         ServObject *so = MakeCommandObject(tcmd.c_str());
         res->push_back(so);

         so = new ServObject(object->format);
         res->push_back(so);
      }
      res->back()->push_back(*i);
   }
}

void ConnectionData::SyncObject(ServObject* dest, ObjCollection* src)
{
   std::map<Object*, PyServObject*> objects;
   int count = PyList_Size((PyObject*)src);
   for( int index=0; index<count; index++ )
   {
      PyServObject* so = (PyServObject*)PyList_GetItem((PyObject*)src, index);
      if( PyObject_TypeCheck((PyObject*)so, &PyServObjType) )
         objects.insert(std::map<Object*, PyServObject*>::value_type(so->src, so));
   }

   ServObject::iterator oi = dest->begin();
   for( ; oi != dest->end(); )
   {
      Object* o = *oi;
      std::map<Object*, PyServObject*>::iterator fnd = objects.find(*oi);
      if( fnd == objects.end() )
      {
         delete o;
         *oi = NULL;
         oi = dest->erase(oi);
      } else
      {
         std::vector<ChildInfo>::iterator chI = fnd->second->childObjects.begin();
         for( ; chI != fnd->second->childObjects.end(); chI++ )
            SyncObject(chI->servObj, chI->pyObj);
         oi++;
      }
   }
}

bool ConnectionData::WriteOrReplace(ServObject* src, ObjCollection* obj, const char* userid, bool replace)
{
   bool res = false;

   ExchangeList el(objCreator.GetFormatList());

   SyncObject(src, obj);

   USES_CONVERSION;
   if( replace )
   {
      // put remove command
      std::wstring param;
      param = src->format->name;
      param += L":userid ";
      const wchar_t* wuid = A2W(userid);
      if(_wcsicmp(wuid, NULL_TAG) == 0)
         param += L"is null";
      else
      {
         param += L"= '";
         param += wuid;
         param += L"'";
      }
      ServObject *so = MakeCommandObject(REMOVE_COMMAND, param.c_str());
      el.push_back(so);
   }

   if( *userid != '\0' )
   {
      std::wstring cmd;
      MakeCommand(&cmd, FORCE_PUT, A2W(userid));
      ServObject *so = MakeCommandObject(cmd.c_str());

      el.push_back(so);
      el.push_back(src);
   } else
   {
      PrepareList(&el, FORCE_PUT, src);
   }

   Socket s;
   if( s.Connect(ip.c_str(), port) )
   {
      el.Write(&s);
      if( ReadAnswer(&s, READ_TIMEOUT, &res, NULL) )
         SendCommand(&s, BYE_COMMAND, L"", login.c_str(), password.c_str());
      else
         error = "Error while writing";
   }

   RemoveExisting(&el, src);
   return res;
}

static PyObject* DoConnect(PyObject *self, PyObject *args)
{
   const char* ip, *login = NULL, *password = NULL;
   int port;

#ifdef UNIX
#else
   WSADATA wsaData;
   WSAStartup(MAKEWORD(2,2), &wsaData);
#endif

   if( !PyArg_ParseTuple(args, "si|ss", &ip, &port, &login, &password) )
   {
      PyErr_SetString(PyExc_RuntimeError, "bad arguments");
      return NULL;
   }

   if( !ConnData.Connect(ip, port, login, password) )
   {
      PyErr_SetString(PyExc_RuntimeError, ConnData.Error());
      return NULL;
   }

   Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* DoClose(PyObject *self, PyObject *args)
{
   ConnData.Close();
#ifdef UNIX
#else
   WSACleanup();
#endif
	Py_INCREF(Py_None);
	return Py_None;
}

static PyObject* DoGet(PyObject *self, PyObject *args)
{
   const char* object, *filter = NULL;
   if( !PyArg_ParseTuple(args, "s|s", &object, &filter) )
   {
      PyErr_SetString(PyExc_RuntimeError, "bad arguments");
      return NULL;
   }

   USES_CONVERSION;
   std::wstring param(A2W(object));
   param += L":";
   if( filter != NULL )
   {
      param += A2W(filter);
   }
   PyObject* ret = ConnData.ReadObject(SELECT_COMMAND, param.c_str());
   if( ret == NULL )
   {
	   Py_INCREF(Py_None);
	   return Py_None;
   }

   return ret;
}

static PyObject* DoNew(PyObject *self, PyObject *args)
{
   const char* object;
   if( !PyArg_ParseTuple(args, "s", &object) )
   {
      PyErr_SetString(PyExc_RuntimeError, "bad arguments");
      return NULL;
   }

   USES_CONVERSION;
   std::wstring param(A2W(object));
   PyObject* ret = ConnData.ReadObject(GET_OBJ_FORMAT, param.c_str());
   if( ret == NULL )
   {
	   Py_INCREF(Py_None);
	   return Py_None;
   }

   return ret;
}

static PyObject* DoError(PyObject *self, PyObject *args)
{
   return Py_BuildValue("s", ConnData.Error());
}

static PyObject* DoDelete(PyObject *self, PyObject *args)
{
   const char *object, *filter;
   if( !PyArg_ParseTuple(args, "ss", &object, &filter) )
   {
      PyErr_SetString(PyExc_RuntimeError, "bad arguments");
      return NULL;
   }

   USES_CONVERSION;
   std::wstring param(A2W(object));
   param += L":";
   param += A2W(filter);
   bool ret = ConnData.DoCommand(REMOVE_COMMAND, param.c_str());
   
   PyObject* retObj = (ret) ? Py_True : Py_False;

   Py_INCREF(retObj);
   return retObj;
}

//MODULE 
static PyMethodDef example_methods[] = {
	{"connect", DoConnect, METH_VARARGS, "сonnect(ip, port[, login, password]) connect to server"},
   {"get", DoGet, METH_VARARGS, "get(name[, filter]) get objects collection"},
   {"new", DoNew, METH_VARARGS, "new(name) get new objects collection"},
   {"error", DoError, METH_NOARGS, "error() get error message"},
   {"close", DoClose, METH_NOARGS, "close() close connections"},
   {"delete", DoDelete, METH_VARARGS, "delete(name, filter) remove objects from server"},

   {NULL, NULL}
};

PyMODINIT_FUNC initgrserver(void)
{
   ObjCollectionType.tp_base = &PyList_Type;
   if( PyType_Ready(&ObjCollectionType) < 0 )
      return;

   if( PyType_Ready(&PyServObjType) < 0 )
      return;

   PyDateTime_IMPORT;

	Py_InitModule("grserver", example_methods);
}
