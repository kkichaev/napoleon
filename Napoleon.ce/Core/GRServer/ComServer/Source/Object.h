// Object.h: объявление CGRObject

#pragma once
#include "resource.h"       // основные символы

#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif
#include <servobj.h>
#include "ObjCol.h"

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Однопотоковые COM-объекты не поддерживаются должным образом платформой Windows CE, например платформами Windows Mobile, в которых не предусмотрена полная поддержка DCOM. Определите _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA, чтобы принудить ATL поддерживать создание однопотоковых COM-объектов и разрешить использование его реализаций однопотоковых COM-объектов. Для потоковой модели в вашем rgs-файле задано значение 'Free', поскольку это единственная потоковая модель, поддерживаемая не-DCOM платформами Windows CE."
#endif



// CGRObject

class ATL_NO_VTABLE CGRObject :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CGRObject, &CLSID_GRObject>,
	public IDispatchImpl<IGRObject, &IID_IGRObject, &LIBID_ComGRServerLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CGRObject()
	{
	}

DECLARE_NO_REGISTRY()
//DECLARE_REGISTRY_RESOURCEID(IDR_GROBJECT)

DECLARE_NOT_AGGREGATABLE(CGRObject)

BEGIN_COM_MAP(CGRObject)
	COM_INTERFACE_ENTRY(IGRObject)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()

	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
      object = NULL;
		return S_OK;
	}

	void FinalRelease()
	{
      AddLog("   CGRObject::FinalRelease %x, %x", this, owner);

      if( owner )
         owner->ObjectDeleted(this);
      object = NULL;
	}

   void SetData(CServer* server, CObjCol* owner, GRServer::Object *object)
   {
      this->server = server;
      this->object = object;
      this->owner = owner;
   
      USES_CONVERSION;
      AddLog("   CGRObject::SetData %x, %x, %s", this, owner, (object == NULL) ? "" : W2A(object->GetFormat().name.c_str()));
   }

   int FindField(const wchar_t* name) const;

   void ObjectDeleted()
   {
      object = NULL;
   }

   void OwnerDeleted()
   {
      owner = NULL;
      object = NULL;
   }

   void FreeOwner()
   {
      owner = NULL;
   }

public:
	STDMETHOD(Invoke)(DISPID dispidMember, REFIID riid,
		LCID lcid, WORD wFlags, DISPPARAMS* pdispparams, VARIANT* pvarResult,
		EXCEPINFO* pexcepinfo, UINT* puArgErr);

   STDMETHOD(GetTypeInfo)(UINT itinfo, LCID lcid, ITypeInfo** pptinfo) { return E_NOTIMPL; }
	STDMETHOD(GetIDsOfNames)(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid);

   STDMETHOD(Get)(BSTR fieldName, VARIANT* value);
   STDMETHOD(Set)(BSTR fieldName, VARIANT value);
   STDMETHOD(Delete)(void);
   STDMETHOD(DateToString)(BSTR fieldName, VARIANT* value);
	STDMETHOD(DateFromString)(BSTR fieldName, BSTR dateValue);

protected:
   GRServer::Object *object;
   CServer* server;
   CObjCol* owner;

   HRESULT ToVariant(int index, VARIANT *res);
   HRESULT FromVariant(int index, const VARIANT& src);
};

OBJECT_ENTRY_AUTO(__uuidof(GRObject), CGRObject)
