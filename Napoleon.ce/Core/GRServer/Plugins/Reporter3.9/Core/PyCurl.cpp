#include "stdafx.h"
#include "Reporter.h"

#include "PyObjects.h"
#include <structmember.h>
#include "curl_service.h"

using namespace GRServer;

static PyObject* UrlGet(PyCurl* self, PyObject* args)
{
	const char* url = NULL;
	PyObject *headers = NULL;
	if (!PyArg_ParseTuple(args, "s|O", &url, &headers))
		return NULL;

	PyObject*ret = self->Do(true, url, headers, NULL, NULL);
	return ret;
}


static PyObject* UrlPost(PyCurl* self, PyObject* args)
{
	const char* url = NULL;
	PyObject *headers = NULL;
	PyObject *postData = NULL;
	PyObject *fileData = NULL;
	if (!PyArg_ParseTuple(args, "s|OOO", &url, &headers, &postData, &fileData))
		return NULL;

	PyObject* ret = self->Do(false, url, headers, postData, fileData);
	return ret;
}

static PyObject* UrlPut(PyCurl* self, PyObject* args)
{
	const char* url = NULL;
	const char* data = NULL;
	PyObject *headers = NULL;
	if (!PyArg_ParseTuple(args, "sOs", &url, &headers, &data))
		return NULL;

	PyObject* ret = self->Put(url, headers, data);
	return ret;
}

static PyObject* UrlDelete(PyCurl* self, PyObject* args)
{
	const char* url = NULL;
	PyObject *headers = NULL;
	if (!PyArg_ParseTuple(args, "s|O", &url, &headers))
		return NULL;

	PyObject* ret = self->Delete(url, headers);
	return ret;
}

//static PyObject* IsSuccess(PyCurl* self, PyObject* args)
//{
//	PyObject* ret = ((self->code / 100) == 2) ? Py_True : Py_False;
//	Py_INCREF(ret);
//	return ret;
//}

//void PyCurl::FreeResponse()
//{
//	if (*response != '\0')
//		free(response);
//	response = "";
//}

static void AddHeader(ICurlHandler* h, PyObject* header)
{
	const char *hdr = PyUnicode_AsUTF8(header);
	if (hdr != NULL && &hdr != '\0')
		h->AddHeader(hdr);
}

static void AddMimeList(ICurlHandler* h, PyObject* postData)
{
	PyObject *key, *ptype;
	size_t count = PyList_Size(postData);
	for (size_t i = 0; i < count; i++)
	{
		const char *p = NULL;
		const char *fileName = NULL;
		const char *type = NULL;
		size_t cb = 0;
		PyObject* value = PyList_GetItem(postData, i);

		if (PyList_Check(value))
		{
			size_t sz = PyList_Size(value);
			if (sz < 3)
				continue;

			key = PyList_GetItem(value, 0);

			PyObject* fn = PyList_GetItem(value, 1);
			if (fn == NULL)
				continue;
			fileName = PyUnicode_AsUTF8(fn);

			if (sz > 3)
			{
				ptype = PyList_GetItem(value, 3);
				type = PyUnicode_AsUTF8(ptype);
			}

			value = PyList_GetItem(value, 2);
			if (value == NULL)
				continue;
		}

		if (PyUnicode_Check(value))
		{
			Py_ssize_t bytes;
			p = PyUnicode_AsUTF8AndSize(value, &bytes);
			cb = bytes;
		}
		else if (PyByteArray_Check(value))
		{
			p = PyByteArray_AS_STRING(value);
			cb = PyByteArray_Size(value);
		}
		else if (PyBytes_Check(value))
		{
			p = PyBytes_AS_STRING(value);
			cb = PyBytes_Size(value);
		}

		if (cb != 0)
		{
			const char* skey = PyUnicode_AsUTF8(key);
			h->AddMimeFileData(skey, p, cb, fileName, type);
		}
	}
}

static void AddMimeDictionary(ICurlHandler* h, bool isPostData, PyObject* postData)
{
	PyObject *key, *value, *ptype;
	Py_ssize_t pos = 0;

	while (PyDict_Next(postData, &pos, &key, &value))
	{
		const char *p = NULL;
		const char *fileName = NULL;
		const char *type = NULL;
		size_t cb = 0;

		if (PyList_Check(value))
		{
			size_t sz = PyList_Size(value);
			if (sz < 2)
				continue;

			PyObject* fn = PyList_GetItem(value, 0);
			if (fn == NULL)
				continue;

			fileName = PyUnicode_AsUTF8(fn);

			if (sz > 2)
			{
				ptype = PyList_GetItem(value, 2);
				type = PyUnicode_AsUTF8(ptype);
			}

			value = PyList_GetItem(value, 1);
			if (value == NULL)
				continue;
		}

		if (PyUnicode_Check(value))
		{
			Py_ssize_t bytes;
			p = PyUnicode_AsUTF8AndSize(value, &bytes);
			cb = bytes;
		}
		else if (PyByteArray_Check(value))
		{
			p = PyByteArray_AS_STRING(value);
			cb = PyByteArray_Size(value);
		}
		else if (PyBytes_Check(value))
		{
			p = PyBytes_AS_STRING(value);
			cb = PyBytes_Size(value);
		}

		if (cb != 0)
		{
			const char* skey = PyUnicode_AsUTF8(key);
			if (isPostData)
				h->AddMimeData(skey, p, cb);
			else
				h->AddMimeFileData(skey, p, cb, fileName, NULL);
		}
	}
}

static void AddMimePost(ICurlHandler* h, PyObject* data)
{
	size_t sz = PyList_Size(data);
	for (size_t i = 0; i < sz; i++)
	{
		PyObject* item = PyList_GetItem(data, i);
		if (PyDict_Check(item))
		{
			PyObject *key, *value;
			Py_ssize_t pos = 0;

			while (PyDict_Next(item, &pos, &key, &value))
			{
				if (!PyUnicode_Check(key))
					continue;
				const char *skey = PyUnicode_AsUTF8(key);

				if (PyUnicode_Check(value))
				{
					Py_ssize_t bytes;
					const char *p = PyUnicode_AsUTF8AndSize(value, &bytes);
					h->AddMimeData(skey, p, bytes);
				}
				else if (PyLong_Check(value))
				{
					char buf[50];
					sprintf(buf, "%d", PyLong_AsLong(value));
					h->AddMimeData(skey, buf, strlen(buf));
				}
			}
		}
	}
}

static void AddHeaders(ICurlHandler* h, PyObject* headers)
{
	if (headers != NULL && headers != Py_None)
	{
		if (PyList_Check(headers))
		{
			size_t sz = PyList_Size(headers);
			for (size_t i = 0; i < sz; i++)
			{
				PyObject *hdr = PyList_GetItem(headers, i);
				AddHeader(h, hdr);
			}
		}
		else
		{
			AddHeader(h, headers);
		}
	}
}

PyObject* PyCurl::Put(const char*url, PyObject* headers, const char* data)
{
	ICurlHandler* h = service->CreateHandler();
	h->SetUrl(url);

	AddHeaders(h, headers);
	h->SetCustomRequest("PUT");

	if (data != NULL)
		h->AddData(data);

	PyThreadState *_save = PyEval_SaveThread();

	h->Preform();
	long code = h->GetResultCode();
	std::string msg;
	h->GetOutput(&msg);

	//FreeResponse();
	//response = (msg.empty()) ? "" : _strdup(msg.c_str());

	delete h;

	PyEval_RestoreThread(_save);

	PyObject* ret = PyCurlResult::Create(code, msg);
	return ret;
}

PyObject* PyCurl::Delete(const char*url, PyObject* headers)
{
	ICurlHandler* h = service->CreateHandler();
	h->SetUrl(url);

	AddHeaders(h, headers);
	h->SetCustomRequest("DELETE");

	PyThreadState *_save = PyEval_SaveThread();

	h->Preform();
	long code = h->GetResultCode();
	std::string msg;
	h->GetOutput(&msg);

	//FreeResponse();
	//response = (msg.empty()) ? "" : _strdup(msg.c_str());

	delete h;
	PyEval_RestoreThread(_save);

	PyObject* ret = PyCurlResult::Create(code, msg);
	return ret;
}



PyObject* PyCurl::Do(bool getUrl, const char* url, PyObject* headers, PyObject* postData, PyObject* fileData)
{
	ICurlHandler* h = service->CreateHandler();
	h->SetMethod(getUrl ? ICurlHandler::Get : ICurlHandler::Post);
	h->SetUrl(url);

	AddHeaders(h, headers);

	if (postData != NULL && postData != Py_None)
	{
		if (PyDict_Check(postData))
			AddMimeDictionary(h, true, postData);
		else if (PyUnicode_Check(postData))
		{
			const char *data = PyUnicode_AsUTF8(postData);
			if (data != NULL)
				h->AddData(data);
		}
		else if (PyList_Check(postData))
		{
			AddMimePost(h, postData);
		}
	}

	if (fileData != NULL) {
		if (PyDict_Check(fileData))
			AddMimeDictionary(h, false, fileData);
		else if(PyList_Check(fileData))
			AddMimeList(h, fileData);
	}

	PyThreadState *_save = PyEval_SaveThread();

	h->Preform();
	long code = h->GetResultCode();
	std::string msg;
	h->GetOutput(&msg);

	//FreeResponse();
	//response = (msg.empty()) ? "" : _strdup(msg.c_str());

	delete h;
	PyEval_RestoreThread(_save);

	PyObject* ret = PyCurlResult::Create(code, msg);
	return ret;
}

static void FreeObject(PyCurl *obj)
{
	//obj->FreeResponse();
	obj->ob_base.ob_type->tp_free(obj);
}

static PyMethodDef dic_methods[] = {
	{ "UrlDelete", (PyCFunction)UrlDelete, METH_VARARGS, "DELETE response from url UrlDelete(url [, list(headers) | str(header)])" },
	{ "UrlGet", (PyCFunction)UrlGet, METH_VARARGS, "GET response from url UrlGet(url[, list(headers) | str(header)])" },
	{ "UrlPost", (PyCFunction)UrlPost, METH_VARARGS, "POST to url UrlPost(url[, list(headers) | str(header), dict(name=>post_data) | str(post_data), dict(name=>list(fileName, file_data [, mime-type])]) | str(post_data), list(list(name,fileName, file_data [, mime-type])]) " },
	{ "UrlPut", (PyCFunction)UrlPut, METH_VARARGS, "PUT to url UrlPut(url, list(headers) | str(header), str(put_data)" },
	//{ "IsSuccess", (PyCFunction)IsSuccess, METH_NOARGS, "True if response code 2xx" },
	{ NULL }  /* Sentinel */
};

static PyMemberDef dic_members[] = {
	//{ "Code", T_LONG, offsetof(PyCurl, code), 0, "Response code" },
	//{ "Response", T_STRING, offsetof(PyCurl, response), 0, "Response code" },
	{ NULL }  /* Sentinel */
};

PyTypeObject PyCurl::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	"grserver.PyCurl",    /* tp_name */
	sizeof(PyCurl),       /* tp_basicsize */
	0,                       /* tp_itemsize */
	(destructor)FreeObject,  /* tp_dealloc */
	0,                       /* tp_print */
	0,							  /* tp_getattr */
	0,							  /* tp_setattr */
	0,                       /* tp_compare */
	0,                       /* tp_repr */
	0,                       /* tp_as_number */
	0,                       /* tp_as_sequence */
	0,                       /* tp_as_mapping */
	0,                       /* tp_hash */
	0,								/* tp_call */
	0,                       /* tp_str */
	0,							  /* tp_getattro */
	0,							 /* tp_setattro */
	0,                       /* tp_as_buffer */
	Py_TPFLAGS_DEFAULT,      /* tp_flags */
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

void PyCurl::Init()
{
	PyType_Ready(&type);
}

PyObject* PyCurl::Create(GRServer::CurlService* service)
{
	PyCurl* ret = (PyCurl*)type.tp_alloc(&type, 0);
	ret->service = service;
	//ret->code = 0;
	//ret->response = "";

	return (PyObject*)ret;
}

static void PCRFreeObject(PyCurlResult *obj)
{
	Py_DECREF(obj->success);
	free((void*)obj->response);
	obj->ob_base.ob_type->tp_free(obj);
}

static PyMemberDef crul_res_members[] = {
	{ "Response", T_STRING, offsetof(PyCurlResult, response), 0, "Response body" },
	{ "Code", T_LONG, offsetof(PyCurlResult, code), 0, "Response code" },
	{ "IsSuccess", T_OBJECT, offsetof(PyCurlResult, success), 0, "Is request success" },
	{ NULL }  /* Sentinel */
};

PyTypeObject PyCurlResult::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	"grserver.PyCurlResult",    /* tp_name */
	sizeof(PyCurlResult),       /* tp_basicsize */
	0,                       /* tp_itemsize */
	(destructor)PCRFreeObject,  /* tp_dealloc */
	0,                       /* tp_print */
	0,							  /* tp_getattr */
	0,							  /* tp_setattr */
	0,                       /* tp_compare */
	0,                       /* tp_repr */
	0,                       /* tp_as_number */
	0,                       /* tp_as_sequence */
	0,                       /* tp_as_mapping */
	0,                       /* tp_hash */
	0,								/* tp_call */
	0,                       /* tp_str */
	0,							  /* tp_getattro */
	0,							 /* tp_setattro */
	0,                       /* tp_as_buffer */
	Py_TPFLAGS_DEFAULT,      /* tp_flags */
	0,                       /* tp_doc */
	0,                       /* tp_traverse */
	0,                       /* tp_clear */
	0,                       /* tp_richcompare */
	0,                       /* tp_weaklistoffset */
	0,                       /* tp_iter */
	0,                       /* tp_iternext */
	0,             /* tp_methods */
	crul_res_members,        /* tp_members */
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

void PyCurlResult::Init()
{
	PyType_Ready(&type);
}

PyObject* PyCurlResult::Create(long code, const std::string& response)
{
	PyCurlResult* ret = (PyCurlResult*)type.tp_alloc(&type, 0);

	ret->code = code;
	ret->response = _strdup(response.c_str());

	PyObject* tobj = ((code / 100) == 2) ? Py_True : Py_False;
	Py_INCREF(tobj);

	ret->success = tobj;

	return (PyObject*)ret;
}
