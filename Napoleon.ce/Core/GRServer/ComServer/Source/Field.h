// Field.h: объявление CField

#pragma once
#include "resource.h"       // основные символы

#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif
#include <servobj.h>

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Однопотоковые COM-объекты не поддерживаются должным образом платформой Windows CE, например платформами Windows Mobile, в которых не предусмотрена полная поддержка DCOM. Определите _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA, чтобы принудить ATL поддерживать создание однопотоковых COM-объектов и разрешить использование его реализаций однопотоковых COM-объектов. Для потоковой модели в вашем rgs-файле задано значение 'Free', поскольку это единственная потоковая модель, поддерживаемая не-DCOM платформами Windows CE."
#endif



// CField

class ATL_NO_VTABLE CField :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CField, &CLSID_Field>,
	public IDispatchImpl<IField, &IID_IField, &LIBID_ComGRServerLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CField()
	{
	}

DECLARE_NO_REGISTRY()
//DECLARE_REGISTRY_RESOURCEID(IDR_FIELD)


BEGIN_COM_MAP(CField)
	COM_INTERFACE_ENTRY(IField)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
      formats = NULL;
		return S_OK;
	}

	void FinalRelease()
	{
	}

   void SetData(const GRServer::MemberFormat& data, const wchar_t* objName, GRServer::FormatList* formats)
   {
      this->data = data;
      this->formats = formats;
      this->objName = objName;
   }

public:

   STDMETHOD(get_Name)(BSTR* pVal);
   STDMETHOD(get_Type)(double* pVal);
   STDMETHOD(get_ChildObject)(IDispatch** pVal);

protected:
   GRServer::MemberFormat data;
   CAtlStringW objName;
   GRServer::FormatList* formats;
};

OBJECT_ENTRY_AUTO(__uuidof(Field), CField)
