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

OrdPcdRcvr::OrdPcdRcvr() : Base(L"Обработка документов...", false)
{
}

bool OrdPcdRcvr::Prepare(ReceivedStream* stream)
{
   reader = DataReader::CreateReader(data.GetType(), stream);
   if( reader == NULL ) return false;

   SQLTable::StartTransaction(500);
   return true;
}

bool OrdPcdRcvr::Write(const OrderProceededImpl& rdata)
{
   const wchar_t* type =  rdata.type;
   if( *type == L'\0' )
#ifdef Voshod   
   {
      if( wcscmp(data.remark, L"BNS") == 0 )
         type = L"Bonus";
      else if( wcscmp(data.remark, L"VZT") == 0 )
         type = L"Return";
      else
         type = L"Order";
   }
#else
   type = L"Order";
#endif

   Handler* h = NULL;
   HandlerList::iterator fnd = handlers.find(type);
   if( fnd == handlers.end() )
   {
      IProceededHandler* ph = GetProceededHandler(type);
      if( ph != NULL )
      {
         SQLTable *t = ph->Prepare(rdata);
         if( t != NULL )
         {
            Handler *newH = new Handler();
            newH->table = t;
            newH->handler = ph;

            handlers[type] = newH;
            h = newH;
         } else
            delete ph;
      }
   } else
      h = fnd->second;

   if( h != NULL )
      h->handler->SetProceeded(h->table, rdata);
   return true;
}

void OrdPcdRcvr::Close()
{
   HandlerList::iterator i = handlers.begin();
   for( ; i != handlers.end(); i++ )
      delete i->second;

   handlers.clear();

   SQLTable::EndTransaction();
}