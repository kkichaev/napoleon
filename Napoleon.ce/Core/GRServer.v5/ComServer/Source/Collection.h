// Collection.h: объявление CCollection

#pragma once
#include "resource.h"       // основные символы

#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif
#include <servobj.h>
#include <vector>

using namespace GRServer;

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Однопотоковые COM-объекты не поддерживаются должным образом платформой Windows CE, например платформами Windows Mobile, в которых не предусмотрена полная поддержка DCOM. Определите _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA, чтобы принудить ATL поддерживать создание однопотоковых COM-объектов и разрешить использование его реализаций однопотоковых COM-объектов. Для потоковой модели в вашем rgs-файле задано значение 'Free', поскольку это единственная потоковая модель, поддерживаемая не-DCOM платформами Windows CE."
#endif


class CObjCol;
// CCollection

class ATL_NO_VTABLE CCollection :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CCollection, &CLSID_Collection>,
	public IDispatchImpl<ICollection, &IID_ICollection, &LIBID_ComGRServerLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CCollection()
	{
	}

DECLARE_NO_REGISTRY()
//DECLARE_REGISTRY_RESOURCEID(IDR_COLLECTION)

DECLARE_NOT_AGGREGATABLE(CCollection)

BEGIN_COM_MAP(CCollection)
	COM_INTERFACE_ENTRY(ICollection)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease();

   void SetData(const std::vector<GRServer::MemberFormat>& data, const wchar_t* objName, GRServer::FormatList* formats)
   {
      fields = data;
      this->formats = formats;
      this->objName = objName;
   }

   void Add(CObjCol* object) { objects.push_back(object); }

public:

   STDMETHOD(get_Count)(double* pVal);
   STDMETHOD(Get)(ULONG index, IDispatch** value);

protected:
   std::vector<CObjCol*> objects;

   std::vector<GRServer::MemberFormat> fields;
   CAtlStringW objName;
   GRServer::FormatList* formats;
};

OBJECT_ENTRY_AUTO(__uuidof(Collection), CCollection)
