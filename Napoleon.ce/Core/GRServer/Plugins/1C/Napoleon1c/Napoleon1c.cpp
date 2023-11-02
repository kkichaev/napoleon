/*
 * Copyright (C), 2009 - 2010, Денис Мосягин
 *
 * 1C внешняя компонента
 *
 * ert   19/08/2010   creating
 */

#include "stdafx.h"
#include "resource.h"
#include "Napoleon1C.h"

#include <ServerDefs.h>
#include <PluginName.h>
#include <thread.h>
#include "SODispatch.h"

using namespace GRServer;

const DWORD ANSWER_TIMEOUT     = 240000; // 4 min
const DWORD KEEP_ALIVE_TIMEOUT = 240000; // 4 min
const DWORD STOP_TIMEOUT       = 2000;


const wchar_t STATE_STR[] = L"Состояние";
const wchar_t CONNECTING_STR[] = L"Подключение...";
const wchar_t CONNECTED_STR[] = L"Соединение установлено";
const wchar_t STARTED_STR[] = L"Не соединен";

class GRServer::ThreadWorker : public IThreadWorker
{
public:
   ThreadWorker(CNapoleon1C* owner)
   {
      this->owner = owner;
   }

   virtual DWORD Execute();

   void ClearOwner() { owner = NULL; }

protected:
   CNapoleon1C *owner;
};

class ResultData : public ServObjDispatch
{
public:
   ResultData(ServObject *so, CNapoleon1C* owner);
   ~ResultData();

   void ToString(OutStream *os);

	STDMETHOD_(ULONG, Release)();

   HRESULT GetType(VARIANT *res, const DISPPARAMS& params);
   HRESULT GetRow(VARIANT *res, const DISPPARAMS& params);
   HRESULT Commit(VARIANT *res, const DISPPARAMS& params);

protected:
   CNapoleon1C* owner;

   int resultIndex, responseIndex;
};

class GRServer::ResultElement : public std::vector<ResultData*>
{
public:
   ResultElement()
   {
   }

   ~ResultElement()
   {
      //if( owner ) owner->FreeResult(this);
   }

   void FreeItems()
   {
      iterator i = begin();
      for( ; i != end(); i++ )
      {
         try
         {
            delete (*i);
         }
         catch(...)
         {
         }
      }
   }

   void Add(ResultData* data) { push_back(data); }

   void ToString(OutStream *os)
   {
      iterator i = begin();
      for( ; i != end(); i++ )
         (*i)->ToString(os);
   }

};

// CNapoleon1C
CNapoleon1C::PropData properties[] = 
{
   { L"Version", L"Версия", &CNapoleon1C::GetVersion, NULL },
   { L"ID", L"ID", &CNapoleon1C::GetID, NULL },
   { L"State", L"Состояние", &CNapoleon1C::GetState, NULL },

   // ответ сервера при подключении
   { L"ConnectResponse", L"ОтветСервера", &CNapoleon1C::GetResponse, NULL },
};

CNapoleon1C::MethodData methods[] = 
{
   // Подключить(ip, port, waitConnect);
   { L"Connect", L"Подключить", 3, true, &CNapoleon1C::Connect, &CNapoleon1C::ConnectParams },
   { L"CloseConnect", L"Закрыть", 0, false, &CNapoleon1C::CloseConnect, NULL },
   { L"GetData", L"ПолучитьДанные", 1, true, &CNapoleon1C::GetData, NULL },
   { L"Commit", L"ЗавершитьОбработку", 1, false, &CNapoleon1C::Commit, NULL },
   { L"CreateObject", L"СоздатьОбъект", 2, true, &CNapoleon1C::CreateObject, NULL },
};

static ServObjDispatch::DispData resultMethods[] =
{
   { L"Тип", (ServObjDispatch::TInvoke)(&ResultData::GetType) },
   { L"ПолучитьЭлемент", (ServObjDispatch::TInvoke)(&ResultData::MoveNext) },
   { L"ПолучитьСтроку", (ServObjDispatch::TInvoke)(&ResultData::GetRow) },
   { L"Завершить", (ServObjDispatch::TInvoke)(&ResultData::Commit) },
};

static WNDPROC prevFunc;
static CNapoleon1C *obj;
static LRESULT CALLBACK WindowProc(HWND hwnd, UINT uMsg, WPARAM wParam, LPARAM lParam)
{
   LRESULT res = CallWindowProc(prevFunc, hwnd, uMsg, wParam, lParam);

   if( uMsg == WM_MDICREATE )
   {
      SetWindowLong(hwnd, GWL_WNDPROC, (LONG)prevFunc);
      prevFunc = NULL;
      obj->SetAssociatedWindow((HWND)res);
      obj = NULL;
   }
   return res;
}

static void SetAssociatedWindow(IDispatch *pConnection, CNapoleon1C *cobj)
{
   if( prevFunc == NULL )
   {
      HRESULT hr;
      HWND hMDI;
      CComPtr<IExtWndsSupport> wnd;

      hr = pConnection->QueryInterface(IID_IExtWndsSupport, (void**)&wnd);
      if( !FAILED(hr) )
      {
         hr =  wnd->GetAppMDIFrame(&hMDI);
         if( !FAILED(hr) )
         {
            obj = cobj;
            prevFunc = (WNDPROC)SetWindowLong(hMDI, GWL_WNDPROC, (LONG)WindowProc);
         }
      }
   }
}

STDMETHODIMP CNapoleon1C::Init(IDispatch *pConnection)
{
   ::SetAssociatedWindow(pConnection, this);

   pConnection->QueryInterface(IID_IAsyncEvent,(void **)&events);
   if( events )
      events->SetEventBufferDepth(3);

   wchar_t buf[20];
   wsprintf(buf, L"%x%x", GetTickCount(), (DWORD)this);
   instanceID = buf;

   return S_OK;
}

STDMETHODIMP CNapoleon1C::Done()
{
   return S_OK;
}

STDMETHODIMP CNapoleon1C::GetInfo(SAFEARRAY **pInfo)
{
   // Component should put supported component technology version 
   // in VARIANT at index 0     
   long lInd = 0;
   VARIANT varVersion;
   V_VT(&varVersion) = VT_I4;
   // This component supports 2.0 version
   V_I4(&varVersion) = 2000;
   SafeArrayPutElement(*pInfo,&lInd,&varVersion);

   return S_OK;
}

// ILanguageExtender

STDMETHODIMP CNapoleon1C::RegisterExtensionAs(BSTR *bstrExtensionName)
{
   *bstrExtensionName = SysAllocString(L"Napoleon1C");
	return NULL;
}

STDMETHODIMP CNapoleon1C::GetNProps(long *plProps)
{
   *plProps = sizeof(properties)/sizeof(properties[0]);
	return S_OK;
}

STDMETHODIMP CNapoleon1C::FindProp(BSTR bstrPropName,long *plPropNum)
{
   *plPropNum = -1;

   PropData *p = properties;
   for( int i=0; i<sizeof(properties)/sizeof(properties[0]); i++, p++ )
   {
      if( wcscmp(p->name, bstrPropName) == 0 || wcscmp(p->alias, bstrPropName) == 0 )
      {
         *plPropNum = i;
         break;
      }
   }
	return S_OK;
}

STDMETHODIMP CNapoleon1C::GetPropName(long lPropNum,long lPropAlias,BSTR *pbstrPropName)
{
   if( lPropNum >= sizeof(properties)/sizeof(properties[0]) )
      return S_FALSE;

   PropData *p = properties + lPropNum;
   *pbstrPropName = SysAllocString((lPropAlias) ? p->alias : p->name);
	return S_OK;
}

STDMETHODIMP CNapoleon1C::GetPropVal(long lPropNum, VARIANT *pvarPropVal)
{
   if( lPropNum >= sizeof(properties)/sizeof(properties[0]) )
      return S_FALSE;

   PropData *p = properties + lPropNum;
   return (p->get == NULL) ? S_FALSE : (this->*p->get)(pvarPropVal);
}

STDMETHODIMP CNapoleon1C::SetPropVal(long lPropNum, VARIANT *pvarPropVal)
{
   if( lPropNum >= sizeof(properties)/sizeof(properties[0]) )
      return S_FALSE;

   PropData *p = properties + lPropNum;
   return (p->put == NULL) ? S_FALSE : (this->*p->put)(*pvarPropVal);
}

STDMETHODIMP CNapoleon1C::IsPropReadable(long lPropNum,BOOL *pboolPropRead)
{
   if( lPropNum >= sizeof(properties)/sizeof(properties[0]) )
      return S_FALSE;

   PropData *p = properties + lPropNum;
   return (p->get == NULL) ? S_FALSE : S_OK;
}

STDMETHODIMP CNapoleon1C::IsPropWritable(long lPropNum,BOOL *pboolPropWrite)
{
   if( lPropNum >= sizeof(properties)/sizeof(properties[0]) )
      return S_FALSE;

   PropData *p = properties + lPropNum;
   return (p->put == NULL) ? S_FALSE : S_OK;
}

STDMETHODIMP CNapoleon1C::GetNMethods(long *plMethods)
{
   *plMethods = sizeof(methods) / sizeof(methods[0]);
	return S_OK;
}

STDMETHODIMP CNapoleon1C::FindMethod(BSTR bstrMethodName, long *plMethodNum)
{
   *plMethodNum = -1;

   MethodData *m = methods;
   for( int i=0; i<sizeof(methods) / sizeof(methods[0]); i++, m++ )
   {
      if( wcscmp(m->name, bstrMethodName) == 0 || wcscmp(m->alias, bstrMethodName) == 0 )
      {
         *plMethodNum = i;
         break;
      }
   }
	return S_OK;
}

STDMETHODIMP CNapoleon1C::GetMethodName(long lMethodNum,long lMethodAlias,BSTR *pbstrMethodName)
{
   if( lMethodNum >= sizeof(methods) / sizeof(methods[0]) )
      return S_FALSE;

   MethodData *m = methods + lMethodNum;
   *pbstrMethodName = SysAllocString((lMethodAlias) ? m->alias : m->name);
   return S_OK;
}

STDMETHODIMP CNapoleon1C::GetNParams(long lMethodNum,long *plParams)
{
   if( lMethodNum >= sizeof(methods) / sizeof(methods[0]) )
      return S_FALSE;

   *plParams = methods[lMethodNum].numParams;
   return S_OK;
}

STDMETHODIMP CNapoleon1C::GetParamDefValue(long lMethodNum,long lParamNum,VARIANT *pvarParamDefValue)
{
   if( lMethodNum >= sizeof(methods) / sizeof(methods[0]) )
      return S_FALSE;

   MethodData *m = methods + lMethodNum;
   if( m->numParams <= lParamNum )
      return S_FALSE;

   HRESULT res = S_OK;
   if( m->defValue )
      res = (this->*m->defValue)(lParamNum, pvarParamDefValue);
   else
      V_VT(pvarParamDefValue) = VT_EMPTY;
   return res;
}

STDMETHODIMP CNapoleon1C::HasRetVal(long lMethodNum,BOOL *pboolRetValue)
{
   if( lMethodNum >= sizeof(methods) / sizeof(methods[0]) )
      return S_FALSE;

   *pboolRetValue = (methods[lMethodNum].hasRetVal) ? TRUE : FALSE;
   return S_OK;
}

STDMETHODIMP CNapoleon1C::CallAsProc(long lMethodNum,SAFEARRAY **paParams)
{
   if( lMethodNum >= sizeof(methods) / sizeof(methods[0]) )
      return S_FALSE;

   return (this->*methods[lMethodNum].call)(NULL, paParams);
}

STDMETHODIMP CNapoleon1C::CallAsFunc(long lMethodNum,VARIANT *pvarRetValue,SAFEARRAY **paParams)
{
   if( lMethodNum >= sizeof(methods) / sizeof(methods[0]) )
      return S_FALSE;

   return (this->*methods[lMethodNum].call)(pvarRetValue, paParams);
}

HRESULT CNapoleon1C::GetVersion(VARIANT *res)
{
   V_VT(res) = VT_I4;
   V_I4(res) = OBJECT_VERSION;

   return S_OK;
}

HRESULT CNapoleon1C::GetResponse(VARIANT *res)
{
   V_VT(res) = VT_BSTR;
   V_BSTR(res) = SysAllocString(answer.c_str());

   return S_OK;
}

HRESULT CNapoleon1C::GetID(VARIANT *res)
{
   V_VT(res) = VT_BSTR;
   V_BSTR(res) = SysAllocString(instanceID.c_str());

   return S_OK;
}

HRESULT CNapoleon1C::GetState(VARIANT *res)
{
   V_VT(res) = VT_BSTR;
   V_BSTR(res) = SysAllocString(StateToStr(state));

   return S_OK;
}

HRESULT CNapoleon1C::FinalConstruct()
{
   aWnd = NULL;
   state = None;
   worker = NULL;

   evStop = CreateEvent(NULL, TRUE, FALSE, NULL);

   fireEvent = true;

   WSADATA wsaData;
   int res = WSAStartup(MAKEWORD(2,2), &wsaData);
   return (res) ? S_FALSE : S_OK;
}

void CNapoleon1C::FinalRelease()
{
   if( worker )
   {
      worker->ClearOwner();
      worker = NULL;
   }

   SetEvent(evStop);

   if( WaitForSingleObject(Thread::StopEvent(), STOP_TIMEOUT) != WAIT_OBJECT_0 )
      Thread::KillingThreads();

   CloseHandle(evStop);

   WSACleanup();

   ClearResults();
}

DWORD CNapoleon1C::DoConnect(CNapoleon1C* obj)
{
   State newState = None;
   try
   {
      bool res = false;

      obj->answer.clear();
      obj->CloseConnect(Connecting, true);
      ResetEvent(obj->evStop);

      if( obj->socket.Connect(obj->ip.c_str(), obj->port) )
      {
         if( SendCommand(&obj->socket, PLUGIN_CONNECT, PLUGIN_NAME) )
         {
            if( ReadAnswer(&obj->socket, ANSWER_TIMEOUT, &res, &obj->answer, NULL, obj->evStop) && res )
            {
               obj->password = obj->answer;
               obj->answer.clear();
               obj->worker = new ThreadWorker(obj);
               Thread::Starting(obj->worker, NULL);

               newState = Connected;
            } else
            {
               if( obj->answer.empty() )
                  obj->answer = L"Сервер не отвечает";
            }
         } else
         {
            obj->answer = L"Сбой при подключении";
         }
      } else
      {
         int rc = WSAGetLastError();
         wchar_t buf[50];
         wsprintf(buf, L" код ошибки %d", rc);
         obj->answer = L"Не могу подключиться к серверу";
         obj->answer += buf;
      }
      obj->SetState(newState);
   }
   catch(...)
   {
      obj->SetState(newState);
   }
   return 0;
}

HRESULT CNapoleon1C::ConnectParams(long num, VARIANT *val)
{
   if( num != 2 )
      V_VT(val) = VT_EMPTY;
   else
   {
      V_VT(val) = VT_I4;
      V_I4(val) = 0;
   }

   return S_OK;
}

HRESULT CNapoleon1C::Connect(VARIANT* vres, SAFEARRAY **paParams)
{
   HRESULT res = S_FALSE;
   if( vres == NULL )
      return res;

   CComVariant ip, vport, wait;
   long ipIdx = 0, portIdx = 1, waitIdx = 2;

   V_VT(vres) = VT_I4;
   V_I4(vres) = 0;

   answer = L"Ошибка при чтении параметров";
   if( SafeArrayGetElement(*paParams, &ipIdx, &ip) == S_OK && 
      SafeArrayGetElement(*paParams, &portIdx, &vport) == S_OK &&
      SafeArrayGetElement(*paParams, &waitIdx, &wait) == S_OK )
   {
      std::wstring ipW;
      vport.ChangeType(VT_I2);
      wait.ChangeType(VT_I4);
      if( VariantToStr(&ipW, ip) )
      {
         USES_CONVERSION;
         this->ip = W2A(ipW.c_str());
         this->port = V_I2(&vport);

         fireEvent = (V_I4(&wait) == 0);

         HANDLE hThread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoConnect, (LPVOID)this, 0, NULL);
         if( hThread != NULL )
         {
            if( !fireEvent )
            {
               WaitForSingleObject(hThread, INFINITE);
               V_I4(vres) = (state == Connected) ? 1 : 0;
            } else
            {
               V_I4(vres) = 1;
            }

            CloseHandle(hThread);
            res = S_OK;
         } else
         {
            answer = L"Не могу запустить подключение к серверу";
         }
      }
   }

   return res;
}

void CNapoleon1C::CloseConnect(CNapoleon1C::State newState, bool fireChangeState)
{
   if( worker != NULL )
   {
      worker->ClearOwner();
      worker = NULL;
   }

   SetEvent(evStop);

   socket.Close();

   if( fireChangeState )
      SetState(newState);
   else
      state = newState;
}

void CNapoleon1C::LostConnection()
{
   worker = NULL;
   socket.Close();

   SetState(None);
}

const wchar_t* CNapoleon1C::StateToStr(CNapoleon1C::State newState)
{
   switch(newState)
   {
   case Connecting:
      return CONNECTING_STR;
   case Connected:
      return CONNECTED_STR;
   }

   return STARTED_STR;
}

static bool IsForegroundWindow(HWND hWnd)
{
   HWND hf = GetForegroundWindow();
   while( hf != NULL )
   {
      if( hf == hWnd )
         return true;

      hf = GetParent(hf);
   }

   return false;
}

bool CNapoleon1C::CanFireEvent()
{
   return true;
   //bool res = false;

   //if( events != NULL && IsWindow(aWnd) )
   //{
   //   res = true;

   //   WINDOWINFO wi = {0};
   //   wi.cbSize = sizeof(wi);
   //   GetWindowInfo(aWnd, &wi);

   //   if( wi.dwStyle & WS_MINIMIZE )
   //      ShowWindow(aWnd, SW_SHOW);

   //   // надо еще проверять чтобы окно было активное и пытаться его активировать
   //   if( !IsForegroundWindow(aWnd) )
   //   {
   //      DWORD thread = GetWindowThreadProcessId(aWnd, NULL);
   //      SetForegroundWindow(aWnd);

   //      AttachThreadInput(GetCurrentThreadId(), thread, TRUE);
   //      SetActiveWindow(aWnd);
   //      AttachThreadInput(GetCurrentThreadId(), thread, FALSE);

   //   }
   //}

   //return res;
}

void CNapoleon1C::SetState(CNapoleon1C::State newState)
{
   state = newState;

   if( fireEvent && CanFireEvent() )
   {
      USES_CONVERSION;
      BSTR bstrSrc, bstrMsg, bstrData;

      bstrSrc = T2OLE((LPTSTR)instanceID.c_str());
      bstrMsg = T2OLE((LPTSTR)STATE_STR);
      bstrData = T2OLE((LPTSTR)StateToStr(newState));

      events->ExternalEvent(bstrSrc, bstrMsg, bstrData);
   }
}

bool CNapoleon1C::Read()
{
   Binary buf;
   ExchangeList obj(objCreator.GetFormatList());

   bool res = obj.Read(&buf, &socket, KEEP_ALIVE_TIMEOUT, evStop, &objCreator);
   if( res )
      HandleObject(&obj);
   else
      res = (socket.GetReadState() == Socket::Readed);

   return res;
}

void CNapoleon1C::ProcessObject(ServObject* object, const std::wstring& command)
{
   if( object )
   {
      wchar_t id[40];
      ResultElement *res = new ResultElement();
      res->Add(new ResultData(object, this));
      results.push_back(res);

      wsprintf(id, L"%d", (LONG)res);

      if( CanFireEvent() )
      {
         USES_CONVERSION;
         BSTR bstrSrc, bstrMsg, bstrData;

         bstrSrc = T2OLE((LPTSTR)instanceID.c_str());
         bstrMsg = T2OLE((LPTSTR)command.c_str());
         bstrData = T2OLE((LPTSTR)id);

         if( events->ExternalEvent(bstrSrc, bstrMsg, bstrData) != S_OK )
         {
            OutStream os;

            WriteAnswer(&os, false, DO_OBJ_CMD);

            Packet* pkt = Packet::MakePacket(os, GZIP_OPT);
            socket.Write(*pkt);
            delete pkt;
         }
      }
   }
}

HRESULT CNapoleon1C::Commit(VARIANT* res, SAFEARRAY **paParams)
{
   CComVariant prm;
   LONG idx = 0;
   std::wstring result;

   if( SafeArrayGetElement(*paParams, &idx, &prm) == S_OK && VariantToStr(&result, prm) )
   {
      ResultElement* fnd = (ResultElement*)_wtoi(result.c_str());
      ResultList::iterator i = results.begin();
      for( ; i != results.end(); i++ )
      {
         if( (*i) == fnd )
         {
            OutStream os;

            WriteAnswer(&os, true, DO_OBJ_CMD);
            (*i)->ToString(&os);

            Packet *pkt = Packet::MakePacket(os, GZIP_OPT);
            socket.Write(*pkt);
            delete pkt;

            delete (*i);
            results.erase(i);
            //FreeResult((*i));
            break;
         }
      }
   }

   return S_OK;
}

void CNapoleon1C::HandleObject(ExchangeList *object)
{
   const ServObject* curObj = object->front();
   if( curObj->Name().compare(SERVER_COMMAND) == 0 && curObj->size())
   {
      const Object* obj = curObj->front();
      const Member *m = (*obj)[COMMAND_MEMBER];
      if( m )
      {
         if( m->str->compare(PLUGIN_CLOSED) == 0 )
         {
            CloseConnect(None, true);
         } else if( m->str->compare(DO_OBJ_CMD) == 0 )
         {
            const std::wstring& action = (const std::wstring&)*((*obj)[PARAM_MEMBER]->str);
            ProcessObject( (object->size() > 1) ? object->at(1) : NULL, action);
         }
      }
   }
}

HRESULT CNapoleon1C::CreateObject(VARIANT* res, SAFEARRAY **paParams)
{
   V_VT(res) = VT_EMPTY;
   CComVariant data, obj;
   LONG idxData = 0, idxObj = 1;

   if( state == Connected && SafeArrayGetElement(*paParams, &idxData, &data) == S_OK && SafeArrayGetElement(*paParams, &idxObj, &obj) == S_OK )
   {
      std::wstring objName, result;
      if( VariantToStr(&result, data) && VariantToStr(&objName, obj) )
      {
         ResultElement* fnd = (ResultElement*)_wtoi(result.c_str());
         ResultList::iterator i = results.begin();
         for( ; i != results.end(); i++ )
         {
            if( (*i) == fnd && (*i)->size() > 0 )
            {
               Socket s;
               if( s.Connect(ip.c_str(), port) && 
                  SendCommand(&s, GET_OBJ_FORMAT, objName.c_str(), PLUGIN_NAME, password.c_str()) )
               {
                  Binary buf;
                  ExchangeList servObj(objCreator.GetFormatList());
                  bool bres = servObj.Read(&buf, &s, KEEP_ALIVE_TIMEOUT, evStop, &objCreator);

                  if( bres && servObj.size() > 1 )
                  {
                     ServObject* curObj = servObj.at(1);
                     ResultData* rd = new ResultData(curObj, this);
                     (*i)->Add(rd);

                     IDispatch* ri;
                     if( rd->QueryInterface(IID_IDispatch, (void**)&ri) == S_OK )
                     {
                        V_VT(res) = VT_DISPATCH;
                        V_DISPATCH(res) = ri;
                     }
                  }
               }
            }
         }
      }
   }
      
      
   return S_OK;
}

HRESULT CNapoleon1C::GetData(VARIANT* res, SAFEARRAY **paParams)
{
   V_VT(res) = VT_EMPTY;

   CComVariant prm;
   std::wstring result;
   LONG idx = 0;

   if( SafeArrayGetElement(*paParams, &idx, &prm) == S_OK && VariantToStr(&result, prm) )
   {
      ResultElement* fnd = (ResultElement*)_wtoi(result.c_str());
      ResultList::iterator i = results.begin();
      for( ; i != results.end(); i++ )
      {
         if( (*i) == fnd && (*i)->size() > 0 )
         {
            ResultData *rd = (*i)->at(0);
            IDispatch* ri;
            if( rd->QueryInterface(IID_IDispatch, (void**)&ri) == S_OK )
            {
               V_VT(res) = VT_DISPATCH;
               V_DISPATCH(res) = ri;
            }
            break;
         }
      }
   }

   return S_OK;
}

void CNapoleon1C::FreeResult(ResultElement *result)
{
   ResultList::iterator i = results.begin();
   for( ; i != results.end(); i++ )
   {
      if( (*i) == result )
      {
         results.erase(i);
         break;
      }
   }
}

void CNapoleon1C::ClearResults()
{
   // теоретически все результаты должны быть освобождены раньше, но...
   ResultList::iterator i = results.begin();
   for( ; i != results.end(); i++ )
   {
      (*i)->FreeItems();
      delete (*i);
   }
   results.clear();
}

//
// ----------------------------- ResultData ---------------------------
//
ResultData::ResultData(ServObject *so, CNapoleon1C* owner)
{
   Format *objFormat = so->format;
   ServObject *obj = new ServObject(objFormat);

   this->owner = owner;

   resultIndex = objFormat->FindMember(SERV_RESULT);
   responseIndex = objFormat->FindMember(SERV_RESPONSE);

   while( so->size() )
   {
      so->front()->MoveTo(obj->AddObject());
      so->erase(so->begin());
   }
   Init(objFormat, owner->GetFormatList());
   Init(obj);

   for( int i=0; i<sizeof(resultMethods)/sizeof(resultMethods[0]); i++ )
      methods.push_back(resultMethods[i]);
}

ResultData::~ResultData()
{
   delete data;
}

void ResultData::ToString(OutStream *os)
{
   data->format->ToString(os, owner->GetFormatList());

   ServObject::const_iterator i = data->begin();
   for( ; i != data->end(); i++ )
   {
     (*i)->ToString(os, owner->GetFormatList());
   }
}


ULONG ResultData::Release()
{
	ULONG l = InterlockedDecrement(&rc);
	if (l == 0)
		delete this;
	return l;
}

//HRESULT ResultData::Invoke(DISPID dispidMember, REFIID riid,
//	LCID lcid, WORD wFlags, DISPPARAMS* pdispparams, VARIANT* pvarResult,
//	EXCEPINFO* pexcepinfo, UINT* puArgErr)
//{
//   HRESULT res = DISPATCH_PROPERTYGET;
//   if( wFlags & DISPATCH_METHOD )
//   {
//      if( dispidMember > 0 && dispidMember <= sizeof(resultMethods)/sizeof(resultMethods[0]) )
//      {
//         res = (this->*resultMethods[dispidMember-1].invoke)(pvarResult, *pdispparams);
//      }
//   } else 
//   {
//      res = ServObjDispatch::Invoke(dispidMember, riid, lcid, wFlags, pdispparams, pvarResult, pexcepinfo, puArgErr);
//   }
//
//	return res;
//}

HRESULT ResultData::GetType(VARIANT *res, const DISPPARAMS& params)
{
   V_VT(res) = VT_BSTR;
   V_BSTR(res) = SysAllocString(data->format->name.c_str());
   return S_OK;
}

static int GetResult(const VARIANT& v)
{
   int res = 0;
   if( v.vt == VT_R8 )
   {
      res = (int)V_R8(&v);
   } else 
   {
      CComVariant vi;
      if( VariantChangeType(&vi, &v, 0, VT_R8) == S_OK )
         res = (int)V_R8(&vi);
   }
   return res;
}

HRESULT ResultData::Commit(VARIANT *res, const DISPPARAMS& params)
{
   int ires = 1;
   std::wstring response;

   if( params.cArgs == 1 )
   {
      ires = GetResult(params.rgvarg[0]);
   } else if( params.cArgs == 2 )
   {
      VariantToStr(&response, params.rgvarg[0]);
      ires = GetResult(params.rgvarg[1]);
   }
   if( curObject >= 0 && curObject < (int) data->size() )
   {
      Object* o = data->at(curObject);
      if( resultIndex >= 0 )
         o->at(resultIndex).number = ires;
      if( responseIndex >= 0 )
      {
         Member& m = o->at(responseIndex);
         if( m.str == NULL ) m.str = new CString();
         m.str->assign(response);
      }
   }

   return S_OK;
}

HRESULT ResultData::GetRow(VARIANT *res, const DISPPARAMS& params)
{
   V_VT(res) = VT_I4;
   V_I4(res) = 0;

   if( curObject >= 0 && curObject < (int)data->size() )
   {
      ObjectList::iterator oi = objects.begin();
      if( params.cArgs > 0 )
      {
         bool clear = false;
         VARIANT v(*params.rgvarg);
         if( v.vt != VT_BSTR )
         {
            VariantChangeType(&v, &v, 0, VT_BSTR);
            clear = true;
         }

         for( ; oi != objects.end(); oi++ )
         {
            if( _wcsicmp(oi->name, v.bstrVal) == 0 )
               break;
         }

         if( clear )
            VariantClear(&v);
      }

      if( oi != objects.end() )
         oi->object->MoveNext(res, params);
   }
   return S_OK;
}

//
// ----------------------------- ThreadWorker ---------------------------
//
DWORD ThreadWorker::Execute()
{
   while( true )
   {
      if( owner->Read() && owner )
         continue;

      // smth wrong
      if( owner == NULL || owner->socket.GetReadState() != Socket::WaitTimeout )
         break;

      // send keep-alive
      bool res;
      if( !SendCommand(&owner->socket, KEEP_ALIVE, PLUGIN_NAME) || owner == NULL ||
          !ReadAnswer(&owner->socket, ANSWER_TIMEOUT, &res, NULL, NULL, owner->evStop) )
      {
         break;
      }
   }

   if( owner )
      owner->LostConnection();

   return 0;
}
