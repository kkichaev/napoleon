/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * отметка о приеме заявки
 *
 *  ert   17/06/2009   creating
 */ 
#include "stdafx.h"
#include <DocImpl.h>
#include <DataReader.h>
#include <NetExchange.h>
#include "OrdPcd.h"
#include "Add.h"

static void MakeUpdStmt(std::wstring* sql, const wchar_t* table)
{
   wchar_t buf[10];
   _itow(ofProceeded, buf, 10);

   sql->assign(L"UPDATE ");
   sql->append(table);
   sql->append(L" SET params = params | ");
   sql->append(buf);
}

bool OrdPcdRcvr::Prepare(ReceivedStream* stream)
{
   if( !Base::Prepare(stream) ) // если таблицы нет, то промолчим
   {
      skipData = true;
      return true;
   }

   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;
   std::wstring sql;

   MakeUpdStmt(&sql, data.Name());

#ifdef POD_COMMENT   
   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

#endif
   sql += L" WHERE created = ?";

   MemberType *idT = (MemberType*)&type.Type(L"created");
   params.push_back(idT);

   skipData = !table->PrepareCommand(sql, params);
   return true;
}

bool OrdPcdRcvr::Write(const OrderProceededImpl& data)
{
   bool ret = false;
   if( wcscmp(data.remark, L"GDSRST") == 0 )
   {
      wchar_t buf[50];
      GoodsRestImpl bdoc;
      std::wstring sql;

      __int64 val = *(__int64*)&data.created;
      wsprintf(buf,  L"%d%09d", (DWORD)(val / 1000000000), (DWORD)(val % 1000000000));

      MakeUpdStmt(&sql, bdoc.Name());
   
      sql += L" WHERE date = ";
      sql += buf;

      ret = SQLTable::Execute(sql.c_str());
   } else
      ret = (skipData) ? skipData : table->ExecCommand(data);

   return ret;
}
