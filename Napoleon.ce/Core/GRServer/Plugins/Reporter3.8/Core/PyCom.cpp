
#include "stdafx.h"
#include "Reporter.h"
#include "PyObjects.h"

#include <datetime.h>

using namespace GRServer;

static PyObject* VariantToPython(const VARIANT& var)
{
	PyObject *res = NULL;
	VARIANT tvar;
	VariantInit(&tvar);

	if( (var.vt & VT_BYREF) )
   {
      WORD type = (var.vt & ~(VT_BYREF));
		if (type == VT_BSTR)
		{
#ifdef Python3
			int kind = sizeof(wchar_t);
			res = PyUnicode_FromKindAndData(kind, *var.pbstrVal, wcslen(*var.pbstrVal));
#else
			res = PyUnicode_FromWideChar(*var.pbstrVal, wcslen(*var.pbstrVal));
#endif
		}
      else if( type == VT_VARIANT )
         res = VariantToPython(*var.pvarVal);
		else
		{
			res = Py_None;
			Py_INCREF(res);
		}
   } else
	{
		switch(var.vt)
		{
		case VT_BOOL:
			res = (var.boolVal == VARIANT_TRUE) ? Py_True : Py_False;
			Py_INCREF(res);
			break;
		case VT_BSTR:
		{
#ifdef Python3
			int kind = sizeof(wchar_t);
			res = PyUnicode_FromKindAndData(kind, var.bstrVal, wcslen(var.bstrVal));
#else
			res = PyUnicode_FromWideChar(var.bstrVal, wcslen(var.bstrVal));
#endif
		}
			break;
		case VT_I2:
		case VT_I4:
		case VT_I1:
		case VT_INT:
			VariantChangeType(&tvar, &var, 0, VT_I4);
			res = PyLong_FromLong(V_I4(&tvar));
			break;
		case VT_UI1:
		case VT_UI2:
		case VT_UI4:
		case VT_UINT:
			VariantChangeType(&tvar, &var, 0, VT_UI4);
			res = PyLong_FromUnsignedLong(V_UI4(&tvar));
			break;
		case VT_I8:
		case VT_R4:
		case VT_R8:
		case VT_CY:
			VariantChangeType(&tvar, &var, 0, VT_R8);
			res = PyFloat_FromDouble(V_R8(&tvar));
			break;
		case VT_DISPATCH:
			var.pdispVal->AddRef();
			res = PyComObject::Create(var.pdispVal);
			break;
		case VT_DATE:
		{
			SYSTEMTIME st;
			VariantTimeToSystemTime(var.date, &st);
	      res = PyDateTime_FromDateAndTime(st.wYear, st.wMonth, st.wDay, st.wHour, st.wMinute, st.wSecond, 0);
			break;
		}
		default:
			Py_INCREF(Py_None);
			res = Py_None;
		}
	}
	return res;
}

static bool PythonToVariant(VARIANT* ret, PyObject* src)
{
	if (PyBool_Check(src))
	{
		//gServer->AddLog("Bool arg");
		ret->vt = VT_BOOL;
		ret->boolVal = (src == Py_True) ? VARIANT_TRUE : VARIANT_FALSE;
	}
	else if (PyLong_Check(src))
	{
		ret->vt = VT_I4;
		ret->lVal = PyLong_AsLong(src);
	}
#ifdef Python3
	else 	if (PyLong_Check(src))
	{
		ret->vt = VT_I4;
		ret->lVal = PyLong_AsLong(src);
#else
	else 	if (PyInt_Check(src))
	{
		ret->vt = VT_I4;
		ret->lVal = PyInt_AsLong(src);
	}
	else if (PyString_Check(src))
	{
		USES_CONVERSION;
		ret->vt = VT_BSTR;
		ret->bstrVal = SysAllocString(A2W(PyString_AsString(src)));
#endif
	} else if( PyFloat_Check(src) )
	{
		ret->vt = VT_R8;
		ret->dblVal = PyFloat_AsDouble(src);
	} else if( PyUnicode_Check(src) )
	{
		ret->vt = VT_BSTR;
		ret->bstrVal = SysAllocString(PyUnicode_AsUnicode(src));
	} else if( PyDateTime_Check(src) )
	{
		SYSTEMTIME st = {0};
		st.wDay = PyDateTime_GET_DAY(src);
		st.wMonth = PyDateTime_GET_MONTH(src);
		st.wYear = PyDateTime_GET_YEAR(src);
		st.wHour = PyDateTime_TIME_GET_HOUR(src);
		st.wMinute = PyDateTime_TIME_GET_MINUTE(src);
		st.wSecond = PyDateTime_TIME_GET_SECOND(src);
	} else if( src->ob_type == &PyComObject::type )
	{
		ret->vt = VT_DISPATCH;
		ret->pdispVal = ((PyComObject*)src)->src;
		ret->pdispVal->AddRef();
	}
	return true;
}

static bool LoadParams(DISPPARAMS *params, PyObject* args)
{
	if (!PyTuple_Check(args))
	{
		bool ret = false;
		VARIANT *var = new VARIANT;
		if (PythonToVariant(var, args))
		{
			params->cArgs = 1;
			params->rgvarg = var;
			ret = true;
		}
		else
			delete var;

		return ret;
	}

	int count = PyTuple_Size(args);
	
	if( count > 0 )
	{
		params->cArgs = count;
		params->rgvarg = new VARIANT[count];

		for( int i=0; i<count; i++)
		{
			PyObject *src = PyTuple_GetItem(args, i);
			VARIANT *pVar = params->rgvarg + count - i - 1;
			VariantInit(pVar);
			PythonToVariant(pVar, src);
		}
	}
	return true;
}

static void FreeObject(PyComObject *obj)
{
	obj->src->Release();
#ifdef Python3
	obj->ob_base.ob_type->tp_free(obj);
#else
	obj->ob_type->tp_free(obj);
#endif
}

#ifdef Python3
static PyObject* GetAttr(PyComObject *obj, const wchar_t *name)
{
	if (obj->src == NULL)
	{
		PyErr_SetString(PyExc_TypeError, "No source in object");
		Py_INCREF(Py_None);
		return Py_None;
	}

	USES_CONVERSION;
	wchar_t *wName = (wchar_t*)name;
	DISPID dispid;
	HRESULT hres = obj->src->GetIDsOfNames(IID_NULL, &wName, 1, LOCALE_SYSTEM_DEFAULT, &dispid);
	if (SUCCEEDED(hres))
	{
		DISPPARAMS params;
		VARIANT vRes;

		VariantInit(&vRes);
		params.cArgs = 0;
		params.cNamedArgs = 0;

		PyThreadState *_save = PyEval_SaveThread();
		try
		{
			hres = obj->src->Invoke(dispid, IID_NULL, LOCALE_SYSTEM_DEFAULT, DISPATCH_PROPERTYGET, &params, &vRes, NULL, NULL);
		}
		catch (...)
		{
		}
		PyEval_RestoreThread(_save);

		if (SUCCEEDED(hres))
		{
			PyObject* ret = VariantToPython(vRes);
			VariantClear(&vRes);
			return ret;
		}
		else
		{
			// method wraper
			return PyComMethodWrapper::Create(obj->src, dispid);
		}
	}

	//return obj->GetValue(idx);
	Py_INCREF(Py_None);
	return Py_None;
}

static int SetAttr(PyComObject *obj, const char *name, PyObject *value)
{
	if (obj->src == NULL)
		return -1;

	int ret = -1;
	USES_CONVERSION;
	wchar_t *wName = A2W(name);
	DISPID dispid;
	HRESULT hres = obj->src->GetIDsOfNames(IID_NULL, &wName, 1, LOCALE_SYSTEM_DEFAULT, &dispid);
	if (SUCCEEDED(hres))
	{
		DISPPARAMS params;
		VARIANT vRes;
		DISPID mydispid = DISPID_PROPERTYPUT;

		VariantInit(&vRes);
		params.cArgs = 0;

		params.rgdispidNamedArgs = &mydispid;
		params.cNamedArgs = 1;
		LoadParams(&params, value);

		EXCEPINFO ei;
		ei.bstrDescription = NULL;

		PyThreadState *_save = PyEval_SaveThread();
		try
		{
			hres = obj->src->Invoke(dispid, IID_NULL, LOCALE_SYSTEM_DEFAULT, DISPATCH_PROPERTYPUT, &params, &vRes, &ei, NULL);
		}
		catch (...)
		{
		}
		PyEval_RestoreThread(_save);

		VariantClear(&vRes);
		if (SUCCEEDED(hres))
		{
			ret = 0;
		}
		else
		{
			if (ei.bstrDescription != NULL)
			{
				USES_CONVERSION;
				PyErr_SetString(PyExc_RuntimeError, W2A(ei.bstrDescription));
				return NULL;
			}
		}
	}

	return ret;
}

static int SetAttrO(PyComObject *obj, PyObject *name, PyObject *value)
{
	int res = -1;
	PyObject* strObj = PyObject_Str(name);

#ifdef Python3
	wchar_t* sname;
	if (strObj == NULL || (sname = PyUnicode_AsUnicode(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
	{
		USES_CONVERSION;
		res = SetAttr(obj, W2A(sname), value);
	}
#else
	const char* sname;
	if (strObj == NULL || (sname = PyString_AsString(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = SetAttr(obj, sname, value);
#endif

	if (strObj != NULL)
		Py_DECREF(strObj);
	return res;
}

static PyObject* GetAttrO(PyComObject *obj, PyObject *name)
{
	PyObject* res = NULL;
	PyObject* strObj = PyObject_Str(name);

#ifdef Python3
	wchar_t* sname;
	if (strObj == NULL || (sname = PyUnicode_AsUnicode(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = GetAttr(obj, sname);
#else
	const char* sname;
	if (strObj == NULL || (sname = PyString_AsString(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = GetAttr(obj, sname);
#endif

	if (strObj != NULL)
		Py_DECREF(strObj);
	return res;
}
#else
static PyObject* GetAttr(PyComObject *obj, const char *name)
{
	if (obj->src == NULL)
	{
		PyErr_SetString(PyExc_TypeError, "No source in object");
		Py_INCREF(Py_None);
		return Py_None;
	}

	USES_CONVERSION;
	wchar_t *wName = A2W(name);
	DISPID dispid;
	HRESULT hres = obj->src->GetIDsOfNames(IID_NULL, &wName, 1, LOCALE_SYSTEM_DEFAULT, &dispid);
	if (SUCCEEDED(hres))
	{
		DISPPARAMS params;
		VARIANT vRes;

		VariantInit(&vRes);
		params.cArgs = 0;
		params.cNamedArgs = 0;

		PyThreadState *_save = PyEval_SaveThread();
		try
		{
			hres = obj->src->Invoke(dispid, IID_NULL, LOCALE_SYSTEM_DEFAULT, DISPATCH_PROPERTYGET, &params, &vRes, NULL, NULL);
		}
		catch (...)
		{
		}
		PyEval_RestoreThread(_save);

		if (SUCCEEDED(hres))
		{
			PyObject* ret = VariantToPython(vRes);
			VariantClear(&vRes);
			return ret;
		}
		else
		{
			// method wraper
			return PyComMethodWrapper::Create(obj->src, dispid);
		}
	}

	//return obj->GetValue(idx);
	Py_INCREF(Py_None);
	return Py_None;
}

static int SetAttr(PyComObject *obj, const char *name, PyObject *value)
{
	if (obj->src == NULL)
		return -1;

	int ret = -1;
	USES_CONVERSION;
	wchar_t *wName = A2W(name);
	DISPID dispid;
	HRESULT hres = obj->src->GetIDsOfNames(IID_NULL, &wName, 1, LOCALE_SYSTEM_DEFAULT, &dispid);
	if (SUCCEEDED(hres))
	{
		DISPPARAMS params;
		VARIANT vRes;
		DISPID mydispid = DISPID_PROPERTYPUT;

		VariantInit(&vRes);
		params.cArgs = 0;

		params.rgdispidNamedArgs = &mydispid;
		params.cNamedArgs = 1;
		LoadParams(&params, value);

		EXCEPINFO ei;
		ei.bstrDescription = NULL;

		PyThreadState *_save = PyEval_SaveThread();
		try
		{
			hres = obj->src->Invoke(dispid, IID_NULL, LOCALE_SYSTEM_DEFAULT, DISPATCH_PROPERTYPUT, &params, &vRes, &ei, NULL);
		}
		catch (...)
		{
		}
		PyEval_RestoreThread(_save);

		VariantClear(&vRes);
		if (SUCCEEDED(hres))
		{
			ret = 0;
		}
		else
		{
			if (ei.bstrDescription != NULL)
			{
				USES_CONVERSION;
				PyErr_SetString(PyExc_RuntimeError, W2A(ei.bstrDescription));
				return NULL;
			}
		}
	}

	return ret;
}

static int SetAttrO(PyComObject *obj, PyObject *name, PyObject *value)
{
	int res = -1;
	const char* sname;
	PyObject* strObj = PyObject_Str(name);

#ifdef Python3
	if (strObj == NULL || (sname = PyBytes_AS_STRING(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = SetAttr(obj, sname, value);
#else
	if (strObj == NULL || (sname = PyString_AsString(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = SetAttr(obj, sname, value);
#endif

	if (strObj != NULL)
		Py_DECREF(strObj);
	return res;
}

static PyObject* GetAttrO(PyComObject *obj, PyObject *name)
{
	PyObject* res = NULL;
	const char* sname;
	PyObject* strObj = PyObject_Str(name);

#ifdef Python3
	if (strObj == NULL || (sname = PyBytes_AS_STRING(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = GetAttr(obj, sname);
#else
	if (strObj == NULL || (sname = PyString_AsString(strObj)) == NULL)
		PyErr_SetString(PyExc_RuntimeError, "bad arguments");
	else
		res = GetAttr(obj, sname);
#endif

	if (strObj != NULL)
		Py_DECREF(strObj);
	return res;
}
#endif

#ifdef Python3
PyTypeObject PyComObject::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	 "grserver.PyComObject",    /* tp_name */
	 sizeof(PyComObject),       /* tp_basicsize */
	 0,                       /* tp_itemsize */
	 (destructor)FreeObject,  /* tp_dealloc */
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
	 (getattrofunc)GetAttrO,  /* tp_getattro */
	 (setattrofunc)SetAttrO,  /* tp_setattro */
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
#else
PyTypeObject PyComObject::type =
{
	PyObject_HEAD_INIT(NULL)
	 0,                       /* ob_size */
	 "grserver.PyComObject",    /* tp_name */
	 sizeof(PyComObject),       /* tp_basicsize */
	 0,                       /* tp_itemsize */
	 (destructor)FreeObject,  /* tp_dealloc */
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
	 (getattrofunc)GetAttrO,  /* tp_getattro */
	 (setattrofunc)SetAttrO,  /* tp_setattro */
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
#endif

static void MthFreeObject(PyComMethodWrapper *obj)
{
	obj->src->Release();
#ifdef Python3
	obj->ob_base.ob_type->tp_free(obj);
#else
	obj->ob_type->tp_free(obj);
#endif
}

static PyObject* DoCall(PyComMethodWrapper* obj, PyObject *args, PyObject *kw)
{
	DISPPARAMS params;
	VARIANT vRes;

	VariantInit(&vRes);

	params.cArgs = 0;
	params.cNamedArgs = 0;

	LoadParams(&params, args);

	EXCEPINFO ei;
	ei.bstrDescription = NULL;

	PyThreadState *_save = PyEval_SaveThread();
	HRESULT hres = S_OK;
	try
	{
		hres = obj->src->Invoke(obj->id, IID_NULL, LOCALE_SYSTEM_DEFAULT, DISPATCH_METHOD, &params, &vRes, &ei, NULL);
	}
	catch (...)
	{
	}
	PyEval_RestoreThread(_save);

	for (unsigned i = 0; i<params.cArgs; i++)
		VariantClear(params.rgvarg + i);


	if( SUCCEEDED(hres) )
	{
		PyObject* ret = VariantToPython(vRes);
		VariantClear(&vRes);
		return ret;
	}
	else
	{
		if (ei.bstrDescription != NULL)
		{
			USES_CONVERSION;
			PyErr_SetString(PyExc_RuntimeError, W2A(ei.bstrDescription));
			return NULL;
		}
	}

   Py_INCREF(Py_None);
   return Py_None;
}

#ifdef Python3
PyTypeObject PyComMethodWrapper::type =
{
	PyVarObject_HEAD_INIT(NULL, 0)
	 "grserver.PyComMethodWrapper",    /* tp_name */
	 sizeof(PyComMethodWrapper),       /* tp_basicsize */
	 0,                       /* tp_itemsize */
	 (destructor)MthFreeObject,  /* tp_dealloc */
	 0,                       /* tp_print */
	 0,							  /* tp_getattr */
	 0,							  /* tp_setattr */
	 0,                       /* tp_compare */
	 0,                       /* tp_repr */
	 0,                       /* tp_as_number */
	 0,                       /* tp_as_sequence */
	 0,                       /* tp_as_mapping */
	 0,                       /* tp_hash */
	 (ternaryfunc)DoCall,     /* tp_call */
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

#else
PyTypeObject PyComMethodWrapper::type =
{
	PyObject_HEAD_INIT(NULL)
	 0,                       /* ob_size */
	 "grserver.PyComMethodWrapper",    /* tp_name */
	 sizeof(PyComMethodWrapper),       /* tp_basicsize */
	 0,                       /* tp_itemsize */
	 (destructor)MthFreeObject,  /* tp_dealloc */
	 0,                       /* tp_print */
	 0,							  /* tp_getattr */
	 0,							  /* tp_setattr */
	 0,                       /* tp_compare */
	 0,                       /* tp_repr */
	 0,                       /* tp_as_number */
	 0,                       /* tp_as_sequence */
	 0,                       /* tp_as_mapping */
	 0,                       /* tp_hash */
	 (ternaryfunc)DoCall,     /* tp_call */
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
#endif

PyObject* PyComObject::Create(IDispatch* src)
{
   PyObject* ret = type.tp_alloc(&type, 0);
   ((PyComObject*)ret)->src = src;

   return ret;
}

void PyComObject::Init()
{
	PyDateTime_IMPORT;
	PyType_Ready(&type);
}

PyObject* PyComMethodWrapper::Create(IDispatch* src, DISPID id)
{
   PyObject* ret = type.tp_alloc(&type, 0);
   ((PyComMethodWrapper*)ret)->src = src;
   ((PyComMethodWrapper*)ret)->id = id;
	src->AddRef();

   return ret;
}

void PyComMethodWrapper::Init()
{
   PyType_Ready(&type);
}
