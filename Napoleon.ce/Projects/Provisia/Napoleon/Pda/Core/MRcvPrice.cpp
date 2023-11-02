/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Модуль приложения + globals
 * 
 *  ert   08/08/2007   creating
 *  ert   13/08/2007   modifing
 */ 
#include "stdafx.h"

#include "ObjImpl.h"

#include <Module.h>
#include <Compress.h>

#include <StringHolder.h>
#include <DocImpl.h>
#include <Network.h>
#include "Progress.h"
#include "NplConfig.h"
#include <DocType.h>
#include "PrfDlg.h"
#include <NetExchange.h>
#include <DataReader.h>
#include <ServerDefs.h>
#include <StdFuncs.h>

#ifdef VISIT_DOC
#include <Visit.h>
#endif

#ifdef Autopteka
#include <Add.h>
#endif

#ifdef ORG_TASK
#include <Task.h>
#endif

#include <UpdateConfig.h>

#include <AgentTask.h>

#include <Add.h>

class PriceRcvr : public DBObjectRcvr<PriceImpl>
{
public:
   typedef DBObjectRcvr<PriceImpl> Base;

   PriceRcvr(bool fullPrice) : DBObjectRcvr<PriceImpl>(L"Обработка товара...", false)
   {
      this->fullPrice = fullPrice;
   }

   virtual const wchar_t* Command() const { return (fullPrice) ? SELECT_COMMAND : GET_COMMAND; }

   virtual const wchar_t* Params() const
   {
      cmd = data.Type().Name();
      if( fullPrice )
         cmd += L":SetQtyFilter(False)";
      return cmd.c_str();
   }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( !Base::Prepare(stream) ) return false;

#ifdef Zakroma
      return true;
#else
      return ClearPriceQty(data.Name());
#endif
   }

protected:
   bool fullPrice;
   mutable std::wstring cmd;
};

class ConfigRcvr : public DBObjectRcvr<ConfigImpl>
{
public:
   typedef DBObjectRcvr<ConfigImpl> Base;

   ConfigRcvr() : DBObjectRcvr<ConfigImpl>(L"Обработка настроек...", false)
   {
   }

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( !Base::Prepare(stream) ) return false;
      std::wstring stmt(L"DELETE FROM '"); stmt += data.Name(); stmt += L"' WHERE NOT key LIKE 'ServerIPName%'";
      SQLTable::Execute(stmt.c_str());
      return true;
   }
};

long NapoleonApp::ReceivePrice(std::wstring *answer, IProgressIndicator *pi, bool clearBase, bool fullPrice)
{
   ConfigRcvr ci;
   DBObjAliasRcvr<ConfigImpl> sci(L"Обработка настроек...", false, L"ServerConfig");
   DBObjectRcvr<OrgImpl> oi(L"Обработка организаций...", false);
   PriceRcvr pri(fullPrice);
   DBObjectRcvr<FolderImpl> fi(L"Обработка папок товара...", true);
   DBObjectRcvr<OrgFolderImpl> ofi(L"Обработка маршрутов...", true);

   DBObjectRcvr<TaskCategoryImpl> agtsk(L"Категории задач...", false);
   DBObjectRcvr<FocusedItemsImpl> foci(L"Фокусный товар...", true);


   ReceivePacketParam param(pi);
   param.clearBase = clearBase;

   param.objects.push_back(&ci);
   param.objects.push_back(&sci);
   param.objects.push_back(&oi);
   param.objects.push_back(&pri);
   param.objects.push_back(&fi);
   param.objects.push_back(&ofi);
   param.objects.push_back(&foci);

   param.objects.push_back(&agtsk);

#ifdef PRICE_MATRIX
   DBObjectRcvr<MatrixImpl> mtx(L"Обработка матриц...", true);
   param.objects.push_back(&mtx);
#endif

#ifdef ORG_TASK
   DBObjectRcvr<TaskImpl> tsk(L"Обработка задач...", false, L"doing,flags");
   param.objects.push_back(&tsk);
#endif

#ifdef VAN_SELLING
   DBObjectRcvr<FirmImpl> ffi(L"Обработка фирм...", true);
   param.objects.push_back(&ffi);
#endif

#ifdef Autopteka
   DBObjectRcvr<IncomeImpl> incrcvr(L"Обработка приходов...", true);
   param.objects.push_back(&incrcvr);
#endif

   BeforeReceviePrice(&param);

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReceive, &param, 0, NULL);
   WaitThreadComplete(thread);
   *answer = param.answer;

#ifdef Migma
   if( param.ec == 0 )
   {
      Preference p;
      p.Load();

      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &p.lastPrice);
      p.Save();
   }
#endif

   AfterReceviePrice(&param);

  return param.ec;
}
