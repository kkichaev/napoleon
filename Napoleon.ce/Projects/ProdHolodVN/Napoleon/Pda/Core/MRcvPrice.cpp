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

#include "Add.h"
static std::map<std::wstring, WORD> prices;

#ifdef AGENT_TASK
class SVTaskRcvr : public DBObjectRcvr<SVTaskImpl>
{
public:
   typedef DBObjectRcvr<SVTaskImpl> Base;

   SVTaskRcvr() : DBObjectRcvr<SVTaskImpl>(L"Задачи от супервайзера...", false) {}

   virtual bool Prepare(ReceivedStream* stream)
   {
      if( !Base::Prepare(stream) ) return false;
      std::wstring sql(L"DELETE FROM ");
      sql += Name(); sql += L" WHERE ((flags & 4) != 0)";
      SQLTable::Execute(sql.c_str());
      return true;
   }
};
#endif

class PriceRcvr : public DBObjectRcvr<PriceImpl>
{
public:
   typedef DBObjectRcvr<PriceImpl> Base;

   PriceRcvr(bool fullPrice) : DBObjectRcvr<PriceImpl>(L"Обработка товара...", false)
   {
      this->fullPrice = fullPrice;
   }

   ~PriceRcvr()
   {
      fclose(file);
   }

   virtual const wchar_t* Command() const { return (fullPrice) ? SELECT_COMMAND : GET_COMMAND; }
   
   virtual bool Write(const PriceImpl& data)
   {
      WORD count = wcslen(data.id);
      fwrite(&count, 1, sizeof(count), file);
      fwrite(data.id, sizeof(wchar_t), count, file);

      return Base::Write(data);
   }

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

      prices.clear();

      std::wstring fn;
      _Module.MakeFileName(&fn, PRICE_INDEX_FILE);
      file = _wfopen(fn.c_str(), L"wb");

#ifdef Zakroma
      return true;
#else
      return ClearPriceQty(data.Name());
#endif
   }

protected:
   FILE *file;
   bool fullPrice;
   mutable std::wstring cmd;
};

int PriceToIndex(const Price& price)
{
   if( prices.size() == 0 )
   {
      std::wstring fn;
      _Module.MakeFileName(&fn, PRICE_INDEX_FILE);

      FILE *f = _wfopen(fn.c_str(), L"rb");
      if( f != NULL )
      {
         int ctx = 0;
         WORD sz;
         while(true)
         {
            if( fread(&sz, 1, sizeof(sz), f) <= 0 )
               break;
            wchar_t* buf = (wchar_t*)alloca((sz + 1)* sizeof(wchar_t));
            if( fread(buf, sizeof(wchar_t), sz, f) <= 0 )
               break;

            buf[sz] = L'\0';
            prices[buf] = ctx++;
         }
         fclose(f);
      }
   }

   std::map<std::wstring, WORD>::const_iterator fnd = prices.find(price.id);
   return (fnd == prices.end()) ? -1 : (int)fnd->second;
}

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

   DBObjectRcvr<DiscountImpl> dsci(L"Обработка скидок", true);
   DBObjectRcvr<AgentPrefixImpl> agi(L"", true);

   DBObjectRcvr<PlanImpl> rPlan(L"Обработка планов...", true); 

   ReceivePacketParam param(pi);
   param.clearBase = clearBase;

   param.objects.push_back(&ci);
   param.objects.push_back(&sci);
   param.objects.push_back(&agi);
   param.objects.push_back(&oi);

   param.objects.push_back(&dsci);
   
   param.objects.push_back(&pri);
   param.objects.push_back(&fi);
   param.objects.push_back(&ofi);
   param.objects.push_back(&rPlan);

#ifdef AGENT_TASK
   DBObjectRcvr<TaskCategoryImpl> agtsk(L"Категории задач...", false);
   SVTaskRcvr svtsk;

   param.objects.push_back(&agtsk);
   param.objects.push_back(&svtsk);
#endif


#ifdef PRICE_MATRIX
   DBObjectRcvr<MatrixImpl> mtx(L"Обработка матриц...", true);
   param.objects.push_back(&mtx);
#endif

#ifdef ORG_TASK
   DBObjectRcvr<TaskImpl> tsk(L"Обработка задач...", false, L"doing,flags");
   param.objects.push_back(&tsk);
#endif

#ifdef FIRMS_TABLE
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
   _Module.UpdateApps();

  return param.ec;
}
