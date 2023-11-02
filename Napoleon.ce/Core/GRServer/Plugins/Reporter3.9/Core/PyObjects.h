#pragma once

#include <python.h>
#include "config.h"

#ifdef DEBUG
//#define DEBUG_OBJECTS
#endif

class GRServer::ReporterPlugin;

struct PythonObject;
struct PyObjList;
struct PyObjDict;
struct PyGRServer
{
   PyObject_HEAD;

   GRServer::ISession* session;
	bool haveParams;
	GRServer::ReporterPlugin *reporter;

#ifdef DEBUG_OBJECTS
   std::vector<PythonObject*> *objects;
   std::vector<PyObjList*> *lists;
   std::vector<PyObjDict*> *dicts;

   void Remove(PyObjList* list);
   void Remove(PyObjDict* dict);
   void Remove(PythonObject* obj);

   void Add(PyObjList* list) { lists->push_back(list); }
   void Add(PyObjDict* dict) { dicts->push_back(dict); }
   void Add(PythonObject* obj) { objects->push_back(obj); }
#endif

	static PyObject* Create(GRServer::ISession* session, GRServer::ReporterPlugin *reporter);
   static void Init();

   static PyTypeObject type;
};

struct PyObjList
{
   PyListObject list;
   GRServer::ISessionObject* src;
   GRServer::ServObject* servObj;
   PyGRServer* server;
	PyObject* name;

   static PyObject* Create(GRServer::ISessionObject* obj, GRServer::ServObject* servObj,  PyGRServer* server);
   static void Init();

   static PyTypeObject type;
};

struct GRServer::MemberFormat;
struct PyObjMemberFormat
{
	PyObject_HEAD;

	std::string nameBuf;

	char* name;
	int memType;

	static void Init();

	static PyObject* Create(const GRServer::MemberFormat &src);
	static PyTypeObject type;
};

struct PyObjDict
{
   PyDictObject list;
   GRServer::ISessionObject* src;
   GRServer::ServObject* servObj;
   PyGRServer* server;
	PyObject* name;
	int keyIndex;

   static PyObject* Create(GRServer::ISessionObject* obj, GRServer::ServObject* servObj,  PyGRServer* server, const wchar_t* keyId);
   static void Init();

   static PyTypeObject type;
};

struct PythonObject
{
   PyObject_HEAD;
   
   GRServer::Object* src;
   PyGRServer* server;

   std::map<int, PyObject*>* values;

   PyObject* GetValue(int idx);

   static PyObject* Create(GRServer::Object* src, PyGRServer* server);
   static void Init();

   static PyTypeObject type;
};

struct UserObject : public PythonObject
{
	static PyObject* Create(GRServer::Object* src, PyGRServer* server);
	static void Init();

	static PyTypeObject u_type;
};

struct PyComObject
{
   PyObject_HEAD;
	IDispatch *src;

	static PyObject* Create(IDispatch* src);
   static void Init();

   static PyTypeObject type;
};

struct PyComMethodWrapper
{
   PyObject_HEAD;
	IDispatch *src;
	DISPID id;

	static PyObject* Create(IDispatch* src, DISPID id);
   static void Init();

   static PyTypeObject type;
};


#ifdef USE_CURL

namespace GRServer
{
	class CurlService;
}

struct PyCurl
{
	PyObject_HEAD;

	//long code;
	//char* response;
	GRServer::CurlService* service;

	//void FreeResponse();

	PyObject* Do(bool getUrl, const char* url, PyObject* headers, PyObject* postData, PyObject* fileData);
	PyObject* Put(const char*url, PyObject* headers, const char* data);
	PyObject* Delete(const char*url, PyObject* headers);

	static PyObject* Create(GRServer::CurlService* service);
	static void Init();

	static PyTypeObject type;
};

struct PyCurlResult
{
	PyObject_HEAD;
	const char* response;
	long code;
	PyObject* success;

	static PyObject* Create(long code, const std::string& response);
	static void Init();
	static PyTypeObject type;
};

#endif

int NoSetter(void *self, PyObject *value, void *closure);
