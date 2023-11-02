/*
* Copyright (C), 2007-2008, Денис Мосягин
*
* Удаление таблиц
* 
*  ert   20/03/2008   creating
*/ 
#include "stdafx.h"
#include <Exchange.h>
#include <Sync.h>
#include <Table.h>

void RemoveBases()
{
   CETable::DeleteDB(SyncOrder().FileName());
   CETable::DeleteDB(SyncOrg().FileName());
   CETable::DeleteDB(SyncFOrg().FileName());
   CETable::DeleteDB(SyncFolder().FileName());
   CETable::DeleteDB(SyncPrice().FileName());
   CETable::DeleteDB(SyncDelivery().FileName());
   CETable::DeleteDB(SyncPayment().FileName());

   CETable::DeleteDB(SyncOrgRemnants().FileName());
}
