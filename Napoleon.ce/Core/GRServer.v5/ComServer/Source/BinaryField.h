// BinaryField.h: объявление CBinaryField

#pragma once
#include "resource.h"       // основные символы

#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif
#include <Binary.h>
#include "Object.h"


#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Однопотоковые COM-объекты не поддерживаются должным образом платформой Windows CE, например платформами Windows Mobile, в которых не предусмотрена полная поддержка DCOM. Определите _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA, чтобы принудить ATL поддерживать создание однопотоковых COM-объектов и разрешить использование его реализаций однопотоковых COM-объектов. Для потоковой модели в вашем rgs-файле задано значение 'Free', поскольку это единственная потоковая модель, поддерживаемая не-DCOM платформами Windows CE."
#endif



// CBinaryField

class ATL_NO_VTABLE CBinaryField :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CBinaryField, &CLSID_BinaryField>,
	public IDispatchImpl<IBinaryField, &IID_IBinaryField, &LIBID_ComGRServerLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CBinaryField()
	{
	}

DECLARE_NO_REGISTRY()
//DECLARE_REGISTRY_RESOURCEID(IDR_BINARYFIELD)

DECLARE_NOT_AGGREGATABLE(CBinaryField)

BEGIN_COM_MAP(CBinaryField)
	COM_INTERFACE_ENTRY(IBinaryField)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

   void SetData(Member *data)
   {
      this->data = data;
		if (data->binary == NULL)
			data->binary = new MemoryBinary();
   }

public:

   STDMETHOD(Write)(BSTR name);
   STDMETHOD(Read)(BSTR name);
   STDMETHOD(get_Size)(double* pVal);
	STDMETHOD(SetFrom)(IDispatch* pVal);

private:
   Member *data;

};

OBJECT_ENTRY_AUTO(__uuidof(BinaryField), CBinaryField)
