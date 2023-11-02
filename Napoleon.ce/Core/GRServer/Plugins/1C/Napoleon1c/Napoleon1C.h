/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * 1C plugin
 *
 * ert   19/08/2010   creating
 */

#pragma once
#include "resource.h"       // основные символы

#include "Napoleon1c_i.h"
#include <Socket.h>
#include <ithread.h>
#include <stdobjs.h>

const int OBJECT_VERSION = 0x100;

namespace GRServer {

class ThreadWorker;
class ExchangeList;
class ResultElement;

// CNapoleon1C

class ATL_NO_VTABLE CNapoleon1C :
   public IInitDone,
   public ILanguageExtender,
   public CComObjectRootEx<CComSingleThreadModel>,
   public CComCoClass<CNapoleon1C, &CLSID_Napoleon1C>
{
public:
   enum State { None = 0, Connecting, Connected };

   struct PropData
   {
      const wchar_t *name;
      const wchar_t *alias;
      HRESULT (CNapoleon1C::*get)(VARIANT* res);
      HRESULT (CNapoleon1C::*put)(const VARIANT& val);
   };

   struct MethodData
   {
      const wchar_t *name;
      const wchar_t *alias;
      int numParams;
      bool hasRetVal;
      HRESULT (CNapoleon1C::*call)(VARIANT* res, SAFEARRAY **paParams);
      HRESULT (CNapoleon1C::*defValue)(long num, VARIANT *val);
   };

   DECLARE_REGISTRY_RESOURCEID(IDR_NAPOLEON1C)

   BEGIN_COM_MAP(CNapoleon1C)
      COM_INTERFACE_ENTRY(IInitDone)
      COM_INTERFACE_ENTRY(ILanguageExtender)
   END_COM_MAP()

   DECLARE_PROTECT_FINAL_CONSTRUCT()

   HRESULT FinalConstruct();
   void FinalRelease();

   // IInitDone
public:
   STDMETHOD(Init)(IDispatch *pConnection);
   STDMETHOD(Done)();
   STDMETHOD(GetInfo)(SAFEARRAY **pInfo);

   // ILanguageExtender
public:

   STDMETHOD(RegisterExtensionAs)(BSTR *bstrExtensionName);

   STDMETHOD(GetNProps)(long *plProps);
   STDMETHOD(FindProp)(BSTR bstrPropName,long *plPropNum);
   STDMETHOD(GetPropName)(long lPropNum,long lPropAlias,BSTR *pbstrPropName);
   STDMETHOD(GetPropVal)(long lPropNum,VARIANT *pvarPropVal);
   STDMETHOD(SetPropVal)(long lPropNum,VARIANT *pvarPropVal);
   STDMETHOD(IsPropReadable)(long lPropNum,BOOL *pboolPropRead);
   STDMETHOD(IsPropWritable)(long lPropNum,BOOL *pboolPropWrite);

   STDMETHOD(GetNMethods)(long *plMethods);
   STDMETHOD(FindMethod)(BSTR bstrMethodName,long *plMethodNum);
   STDMETHOD(GetMethodName)(long lMethodNum,long lMethodAlias,BSTR *pbstrMethodName);
   STDMETHOD(GetNParams)(long lMethodNum,long *plParams);
   STDMETHOD(GetParamDefValue)(long lMethodNum,long lParamNum,VARIANT *pvarParamDefValue);
   STDMETHOD(HasRetVal)(long lMethodNum,BOOL *pboolRetValue);
   STDMETHOD(CallAsProc)(long lMethodNum,SAFEARRAY **paParams);
   STDMETHOD(CallAsFunc)(long lMethodNum,VARIANT *pvarRetValue,SAFEARRAY **paParams);

public:
   void SetAssociatedWindow(HWND hWnd) { aWnd = hWnd; }

   // properties
   HRESULT GetVersion(VARIANT *res);
   HRESULT GetResponse(VARIANT *res);
   HRESULT GetID(VARIANT *res);
   HRESULT GetState(VARIANT *res);

   // methods
   HRESULT Connect(VARIANT* res, SAFEARRAY **paParams);
   HRESULT ConnectParams(long num, VARIANT *val);

   HRESULT CloseConnect(VARIANT* res, SAFEARRAY **paParams)
   {
      CloseConnect(None, false);
      return S_OK;
   }

   HRESULT GetData(VARIANT* res, SAFEARRAY **paParams);
   HRESULT Commit(VARIANT* res, SAFEARRAY **paParams);
   HRESULT CreateObject(VARIANT* res, SAFEARRAY **paParams);
   void FreeResult(ResultElement* result);

   void LostConnection();
   void SetState(State newState);
   void HandleObject(ExchangeList *obj);

   bool Read();
   FormatList* GetFormatList() const { return (FormatList*)objCreator.GetFormatList(); }

protected:
   static DWORD DoConnect(CNapoleon1C* obj);

   void ProcessObject(ServObject* object, const std::wstring& command);
   void CloseConnect(State newState, bool fireChangeState);
   const wchar_t* StateToStr(State newState);
   bool CanFireEvent();

   void ClearResults();

private:
   HWND aWnd; // associated widow

   std::string ip;
   WORD port;
   Socket socket;

   std::wstring answer;
   std::wstring instanceID;
   std::wstring password;

   State state;
   bool fireEvent;
   CComPtr<IAsyncEvent> events;

   ObjCreator objCreator;

   typedef std::vector<ResultElement*> ResultList;
   ResultList results;

   HANDLE evStop;
   ThreadWorker *worker;
   friend class ThreadWorker;
};

OBJECT_ENTRY_AUTO(__uuidof(Napoleon1C), CNapoleon1C)

} // namespace GRServer
