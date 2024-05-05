// ObjCol.h: объявление CObjCol

#pragma once
#include "resource.h"       // основные символы

#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif

#include <servobj.h>
#include "Server.h"


#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Однопотоковые COM-объекты не поддерживаются должным образом платформой Windows CE, например платформами Windows Mobile, в которых не предусмотрена полная поддержка DCOM. Определите _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA, чтобы принудить ATL поддерживать создание однопотоковых COM-объектов и разрешить использование его реализаций однопотоковых COM-объектов. Для потоковой модели в вашем rgs-файле задано значение 'Free', поскольку это единственная потоковая модель, поддерживаемая не-DCOM платформами Windows CE."
#endif

class CGRObject;
struct ObjData
{
   CGRObject* object;
   GRServer::Object* data;
};

class ObjList : public std::vector<ObjData>
{
public:
   typedef void (CGRObject::*Func)();

   void Add(CGRObject* object, GRServer::Object* data);

   GRServer::Object* Remove(CGRObject* object);
   void RemoveData(GRServer::Object* data, Func doFunc);

   void ForEach(Func doFunc);
};

// CObjCol

class ATL_NO_VTABLE CObjCol :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CObjCol, &CLSID_ObjCol>,
	public IDispatchImpl<IGRObjCol, &IID_IGRObjCol, &LIBID_ComGRServerLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CObjCol()
	{
	}

DECLARE_NO_REGISTRY()
//DECLARE_REGISTRY_RESOURCEID(IDR_OBJCOL)

DECLARE_NOT_AGGREGATABLE(CObjCol)

BEGIN_COM_MAP(CObjCol)
	COM_INTERFACE_ENTRY(IGRObjCol)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
      canFreeObject = true;
      servObject = NULL;
		return S_OK;
	}

	void FinalRelease();

   void SetObject(CServer* server, GRServer::ServObject* so, bool canFreeObject)
   {
      delete servObject;
      servObject = so;

      this->server = server;
      this->canFreeObject = canFreeObject;
      if( canFreeObject )
         server->AddRef();

      USES_CONVERSION;
      AddLog("ObjCol::SetObject %x %s", this, (so == NULL) ? "" : W2A(so->format->name.c_str()));
   }

   void ObjectDeleted(CGRObject* object)
   {
      objects.Remove(object);
   }

   void NeedDeleted(GRServer::Object* object);

   const GRServer::ServObject* GetServObject() const { return servObject; }
   ObjCreator& GetObjCreator() { return server->GetObjCreator(); }

   STDMETHOD(WriteInt)(BSTR userid, bool haveUserid, VARIANT_BOOL* result);
	
protected:
   GRServer::ServObject* servObject;
   CServer* server;
   bool canFreeObject;

   ObjList objects;

public:
   STDMETHOD(GetTypeInfo)(UINT itinfo, LCID lcid, ITypeInfo** pptinfo) { return E_NOTIMPL; }
	STDMETHOD(GetIDsOfNames)(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid);

   STDMETHOD(Get)(LONG index, IDispatch** object);
   STDMETHOD(New)(IDispatch** object);
   STDMETHOD(Write)(BSTR userid, VARIANT_BOOL* res);
   STDMETHOD(WriteDirect)(VARIANT_BOOL* res);
   STDMETHOD(Replace)(BSTR userid, VARIANT_BOOL* res);
   STDMETHOD(ReplaceDirect)(BSTR where, VARIANT_BOOL* res);
   STDMETHOD(Delete)(BSTR userid);
   STDMETHOD(get_Count)(double* pVal);
   STDMETHOD(get_Fields)(IDispatch** pVal);
   STDMETHOD(get_KeyFields)(IDispatch** pVal);
   STDMETHOD(RemoveObject)(ULONG index);
   STDMETHOD(get_Type)(BSTR* pVal);
   STDMETHOD(put_Type)(BSTR pVal);
};

OBJECT_ENTRY_AUTO(__uuidof(ObjCol), CObjCol)
