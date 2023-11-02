/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Дополнения при приеме прайса
 * 
 *  ert   28/09/2010   creating
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
#include <MainFrame.h>

#include <Visit.h>
#include <Add.h>
#include <DocType.h>

#define TOP_APP_STR L"НеУбиратьСЭкрана"
#define ENABLE_SEND L"ПовторнаяОтправка"

void Log(const char* msg, ... );

static void MakeWhere(wchar_t* buf)
{
   FILETIME ft;
   _Module.GetLocalTime(&ft);

   *(__int64*)&ft -= (__int64)7 * 24 * 3600 * 10000000; // вычтем неделю
   wsprintf(buf,  L"WHERE date <= %d%09d", (DWORD)(*(__int64*)&ft / 1000000000), (DWORD)(*(__int64*)&ft % 1000000000));
   //Log("Remove docs WHERE date <= %d%09d", (DWORD)(*(__int64*)&ft / 1000000000), (DWORD)(*(__int64*)&ft % 1000000000));
}

static void RemoveOldVisits()
{
   wchar_t buf[200];
   MakeWhere(buf);

   VisitImpl v;
   std::vector<ROWID> recs;
   SQLTable tbl(v.Name());
   tbl.RIDList(&recs, buf);

   std::vector<ROWID>::const_iterator i = recs.begin();
   for( ; i != recs.end(); i++ )
   {
      v.Read((*i));
      v.RemoveDocument();
   }

   if( recs.size() )
      docTypeManager.Refresh(dtVisit);
}

static void RemoveOldDisplay()
{
   wchar_t buf[200];
   MakeWhere(buf);

   DisplayImpl v;
   std::vector<ROWID> recs;
   SQLTable tbl(v.Name());
   tbl.RIDList(&recs, buf);

   //Log("Count %d", recs.size());

   std::vector<ROWID>::const_iterator i = recs.begin();
   for( ; i != recs.end(); i++ )
   {
      v.Read((*i));
      v.RemoveDocument();
   }

   if( recs.size() )
      docTypeManager.Refresh(dtDisplay);
}

void BeforeReceviePrice(ReceivePacketParam* param)
{
}

void AfterReceviePrice(ReceivePacketParam* param)
{
   Preference prf;
   prf.Load();
   bool changed = false;

   ConfigImpl cfg;

   cfg.key = TOP_APP_STR;
   if( cfg.Read() )
   {
      if( _wtoi(cfg.value) )
      {
         if( (prf.flags & apfTopApp) == 0 )
         {
            prf.flags |= apfTopApp;
            changed = true;
         }
      } else
      {
         if( (prf.flags & apfTopApp) != 0 )
         {
            prf.flags &= (~apfTopApp);
            changed = true;
         }
      }      
   }

   cfg.key = ENABLE_SEND;
   if( cfg.Read() )
   {
      if( _wtoi(cfg.value) )
      {
         if( (prf.flags & npfConfirmOrderSend) == 0 )
         {
            prf.flags |= npfConfirmOrderSend;
            changed = true;
         }
      } else
      {
         if( (prf.flags & npfConfirmOrderSend) != 0 )
         {
            prf.flags &= (~npfConfirmOrderSend);
            changed = true;
         }
      }      
   }

   if( changed )
   {
      prf.Save();
      ((MainFrame*)_Module.GetFrame())->PreferenceChanged();
   }

   RemoveOldVisits();
   RemoveOldDisplay();
}

void BeforeSendDocs(SendPacketParam* param)
{
}

void AfterSendDocs(SendPacketParam* param)
{
   RemoveOldVisits();
}