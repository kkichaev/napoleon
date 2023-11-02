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

#include <Add.h>

static DBObjectRcvr<SkladImpl>* si;
void BeforeReceviePrice(ReceivePacketParam* param)
{
   si = new DBObjectRcvr<SkladImpl>(L"Обработка складов...", true);
   param->objects.push_back(si);
}

void AfterReceviePrice(ReceivePacketParam* param)
{
   delete si;
}

void BeforeSendDocs(SendPacketParam* param)
{
}

void AfterSendDocs(SendPacketParam* param)
{
}