// Server.h: объявление CServer

#pragma once
#include "resource.h"       // основные символы

#ifdef X64
#include "ComGRServer_x64_i.h"
#else
#include "ComGRServer_i.h"
#endif
#include "_IServerEvents_CP.h"
#include <stdobjs.h>

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Однопотоковые COM-объекты не поддерживаются должным образом платформой Windows CE, например платформами Windows Mobile, в которых не предусмотрена полная поддержка DCOM. Определите _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA, чтобы принудить ATL поддерживать создание однопотоковых COM-объектов и разрешить использование его реализаций однопотоковых COM-объектов. Для потоковой модели в вашем rgs-файле задано значение 'Free', поскольку это единственная потоковая модель, поддерживаемая не-DCOM платформами Windows CE."
#endif

using namespace GRServer;

struct ConnectData
{
   CAtlStringA address;
   WORD port;
   std::wstring login;
   std::wstring password;
	std::wstring category;

   unsigned duration;
   int timeout;

   ConnectData() : duration(0), port(0), timeout(10*1000) {}

   ServObject* MakeCommand(FormatList* list, const wchar_t* cmd, const wchar_t* param = L"") const;
	bool SendCommand(Socket* socket, const wchar_t* cmd, const wchar_t* param) const;
};

// CServer

class ATL_NO_VTABLE CServer :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CServer, &CLSID_Server>,
	public IConnectionPointContainerImpl<CServer>,
	public CProxy_IServerEvents<CServer>,
	public IDispatchImpl<IServer, &IID_IServer, &LIBID_ComGRServerLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CServer()
	{
	}

DECLARE_REGISTRY_RESOURCEID(IDR_SERVER)

DECLARE_NOT_AGGREGATABLE(CServer)

BEGIN_COM_MAP(CServer)
	COM_INTERFACE_ENTRY(IServer)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(IConnectionPointContainer)
END_COM_MAP()

BEGIN_CONNECTION_POINT_MAP(CServer)
	CONNECTION_POINT_ENTRY(__uuidof(_IServerEvents))
END_CONNECTION_POINT_MAP()


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
      WSADATA wsaData;
      WSAStartup(MAKEWORD(2,2), &wsaData);

		return S_OK;
	}

	void FinalRelease()
	{
      WSACleanup();
	}

   const ConnectData& GetConnectData() const { return connectData; }
   void SetErrorMessage(const wchar_t* msg) { errorMessage = msg; }

   ObjCreator& GetObjCreator() { return objCreator; }
   const ObjCreator& GetObjCreator() const { return objCreator; }

public:
   STDMETHOD(GetTypeInfo)(UINT itinfo, LCID lcid, ITypeInfo** pptinfo) { return E_NOTIMPL; }
	STDMETHOD(GetIDsOfNames)(REFIID riid, LPOLESTR* rgszNames, UINT cNames, LCID lcid, DISPID* rgdispid);

	STDMETHOD(Connect)(BSTR name, USHORT port, BSTR login, BSTR password, BSTR category, VARIANT_BOOL* result);
   STDMETHOD(Get)(BSTR name, BSTR filter, IDispatch** collection);
   STDMETHOD(Delete)(BSTR name, BSTR filter);
   STDMETHOD(New)(BSTR name, IDispatch** collection);
   STDMETHOD(get_ErrorMessage)(BSTR* pVal);
   STDMETHOD(Report)(BSTR name, IDispatch *params, IDispatch** collection);
   STDMETHOD(Call)(BSTR name, IDispatch* params, IDispatch** collection);
   STDMETHOD(Write)(IDispatch *objcol);
	STDMETHOD(EndSession)(void);
	STDMETHOD(ReportStrParam)(BSTR name, BSTR param, IDispatch** collection);

   STDMETHOD(get_Timeout)(int* pVal);
   STDMETHOD(put_Timeout)(int val);

protected:
   ConnectData connectData;
   CAtlStringW errorMessage;
   ObjCreator objCreator;

   int timeout;

   STDMETHOD(ReadObject)(const wchar_t* cmd, const wchar_t* param, IDispatch** collection);
   HRESULT DoReport(BSTR name, ServObject* param, IDispatch** collection);
};

void AddLog(const char* msg, ... );
bool VariantToStr(GRServer::CString *dest, const VARIANT& src);

OBJECT_ENTRY_AUTO(__uuidof(Server), CServer)
