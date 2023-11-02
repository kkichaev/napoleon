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

	bool bret = self->Do(true, url, headers, NULL, NULL);
	PyObject* ret = (bret) ? Py_True : Py_False;
	Py_INCREF(ret);
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

	bool bret = self->Do(false, url, headers, postData, fileData);
	PyObject* ret = (bret) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

static PyObject* UrlPut(PyCurl* self, PyObject* args)
{
	const char* url = NULL;
	const char* data = NULL;
	PyObject *headers = NULL;
	if (!PyArg_ParseTuple(args, "sOs", &url, &headers, &data))
		return NULL;

	bool bret = self->Put(url, headers, data);
	PyObject* ret = (bret) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

static PyObject* UrlDelete(PyCurl* self, PyObject* args)
{
	const char* url = NULL;
	PyObject *headers = NULL;
	if (!PyArg_ParseTuple(args, "s|O", &url, &headers))
		return NULL;

	bool bret = self->Delete(url, headers);
	PyObject* ret = (bret) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

static PyObject* IsSuccess(PyCurl* self, PyObject* args)
{
	PyObject* ret = ((self->code / 100) == 2) ? Py_True : Py_False;
	Py_INCREF(ret);
	return ret;
}

void PyCurl::FreeResponse()
{
	if (*response != '\0')
		free(response);
	response = "";
}

#ifdef Python3
static void AddHeader(ICurlHandler* h, PyObject* header)
{
	const char *hdr = PyUnicode_AsUTF8(header);
	if (hdr != NULL && &hdr != '\0')
		h->AddHeader(hdr);
}

static void AddMimeDictionary(ICurlHandler* h, bool isPostData, PyObject* postData)
{
	PyObject *key, *value;
	Py_ssize_t pos = 0;

	while (PyDict_Next(postData, &pos, &key, &value))
	{
		const char *p = NULL;
		const char *fileName = NULL;
		size_t cb = 0;

		if (PyList_Check(value))
		{
			if (PyList_Size(value) != 2)
				continue;

			PyObject* fn = PyList_GetItem(value, 0);
			if (fn == NULL)
				continue;

			fileName = PyUnicode_AsUTF8(fn);
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

		if (cb != 0)
		{
			const char* skey = PyUnicode_AsUTF8(key);
			if (isPostData)
				h->AddMimeData(skey, p, cb);
			else
				h->AddMimeFileData(skey, p, cb, fileName);
		}
	}
}
#else
static void AddHeader(ICurlHandler* h, PyObject* header)
{
	const char *hdr = PyString_AsString(header);
	if (hdr != NULL && &hdr != '\0')
		h->AddHeader(hdr);
}

static void AddMimeDictionary(ICurlHandler* h, bool isPostData, PyObject* postData)
{
	PyObject *key, *value;
	Py_ssize_t pos = 0;

	while (PyDict_Next(postData, &pos, &key, &value))
	{
		const char *p = NULL;
		const char *fileName = NULL;
		size_t cb = 0;

		if (PyList_Check(value))
		{
			if (PyList_Size(value) != 2)
				continue;

			PyObject* fn = PyList_GetItem(value, 0);
			if (fn == NULL)
				continue;

			fileName = PyString_AsString(fn);
			value = PyList_GetItem(value, 1);
			if (value == NULL)
				continue;
		}

		if (PyString_Check(value))
		{
			p = PyString_AsString(value);
			cb = PyString_GET_SIZE(value);
		}
		else if (PyByteArray_Check(value))
		{
			p = PyByteArray_AS_STRING(value);
			cb = PyByteArray_Size(value);
		}

		if (cb != 0)
		{
			const char* skey = PyString_AsString(key);
			if (isPostData)
				h->AddMimeData(skey, p, cb);
			else
				h->AddMimeFileData(skey, p, cb, fileName);
		}
	}
}
#endif

static void AddHeaders(ICurlHandler* h, PyObject* headers)
{
	if (headers != NULL && headers != Py_None)
	{
		if (PyList_Check(headers))
		{
			int sz = PyList_Size(headers);
			for (int i = 0; i < sz; i++)
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

bool PyCurl::Put(const char*url, PyObject* headers, const char* data)
{
	ICurlHandler* h = service->CreateHandler();
	h->SetUrl(url);

	AddHeaders(h, headers);
	h->SetCustomRequest("PUT");

	if (data != NULL)
		h->AddData(data);

	bool ret = h->Preform();
	code = h->GetResultCode();
	std::string msg;
	h->GetOutput(&msg);

	FreeResponse();
	response = (msg.empty()) ? "" : _strdup(msg.c_str());

	delete h;
	return ret;
}

bool PyCurl::Delete(const char*url, PyObject* headers)
{
	ICurlHandler* h = service->CreateHandler();
	h->SetUrl(url);

	AddHeaders(h, headers);
	h->SetCustomRequest("DELETE");

	bool ret = h->Preform();
	code = h->GetResultCode();
	std::string msg;
	h->GetOutput(&msg);

	FreeResponse();
	response = (msg.empty()) ? "" : _strdup(msg.c_str());

	delete h;
	return ret;
}


bool PyCurl::Do(bool getUrl, const char* url, PyObject* headers, PyObject* postData, PyObject* fileData)
{
	ICurlHandler* h = service->CreateHandler();
	h->SetMethod(getUrl ? ICurlHandler::Get : ICurlHandler::Post);
	h->SetUrl(url);

	AddHeaders(h, headers);

	if (postData != NULL && postData != Py_None)
	{
		if (PyDict_Check(postData))
			AddMimeDictionary(h, true, postData);
#ifdef Python3
		else if (PyUnicode_Check(postData))
		{
			const char *data = PyUnicode_AsUTF8(postData);
			if (data != NULL)
				h->AddData(data);
		}
#else
		else if (PyString_Check(postData))
		{
			const char *data = PyString_AsString(postData);
			if (data != NULL)
				h->AddData(data);
		}
#endif
	}

	if (fileData != NULL && PyDict_Check(fileData))
		AddMimeDictionary(h, false, fileData);

	bool ret = h->Preform();
	code = h->GetResultCode();
	std::string msg;
	h->GetOutput(&msg);

	FreeResponse();
	response = (msg.empty()) ? "" : _strdup(msg.c_str());

	delete h;
	return ret;
}

static void FreeObject(PyCurl *obj)
{
	obj->FreeResponse();
}

static PyMethodDef dic_methods[] = {
	{ "UrlDelete", (PyCFunction)UrlDelete, METH_VARARGS, "DELETE response from url UrlDelete(url [, list(headers) | str(header)])" },
	{ "UrlGet", (PyCFunction)UrlGet, METH_VARARGS, "GET response from url UrlGet(url[, list(headers) | str(header)])" },
	{ "UrlPost", (PyCFunction)UrlPost, METH_VARARGS, "POST to url UrlPost(url[, list(headers) | str(header), dict(name=>post_data) | str(post_data), dict(name=>list(fileName, file_data)])" },
	{ "UrlPut", (PyCFunction)UrlPut, METH_VARARGS, "PUT to url UrlPut(url, list(headers) | str(header), str(put_data)" },
	{ "IsSuccess", (PyCFunction)IsSuccess, METH_NOARGS, "True if response code 2xx" },
	{ NULL }  /* Sentinel */
};

static PyMemberDef dic_members[] = {
	{ "Code", T_LONG, offsetof(PyCurl, code), 0, "Response code" },
	{ "Response", T_STRING, offsetof(PyCurl, response), 0, "Response code" },
	{ NULL }  /* Sentinel */
};

#ifdef Python3
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
#else
PyTypeObject PyCurl::type =
{
	PyObject_HEAD_INIT(NULL)
	0,                       /* ob_size */
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
#endif

void PyCurl::Init()
{
	PyType_Ready(&type);
}

PyObject* PyCurl::Create(GRServer::CurlService* service)
{
	PyCurl* ret = (PyCurl*)type.tp_alloc(&type, 0);
	Py_INCREF(ret);
	ret->service = service;
	ret->code = 0;
	ret->response = "";

	return (PyObject*)ret;
}

