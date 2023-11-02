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

#include "Add.h"

void BeforeReceviePrice(ReceivePacketParam* param)
{
}

void AfterReceviePrice(ReceivePacketParam* param)
{
}

void BeforeSendDocs(SendPacketParam* param)
{
   const DocType* dt = docTypeManager.GetDocType(dtPlans);
   if( dt )
   {
      DocumentList *dl;
      if( dt->GetDocuments(L"", &dl) )
      {
         AddToStream(param, dl, dt);
         delete dl;
      }
   }
}

void AfterSendDocs(SendPacketParam* param)
{
}
