/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <DocType.h>
#include <OrgRmnts.h>

struct BalanceAdd : public BalanceType
{
   BalanceAdd() {}
   virtual bool Indexing(DocIndex *docIndex, IProgressIndicator *pc) const
   {
      const DeliveryType *dt = (const DeliveryType*)docTypeManager.GetDocType(dtDelivery);
      return ::Indexing(docIndex, pc, type, *this, (dt != NULL ) ? &dt->docs : NULL, false);
   }

   virtual const SyncFormat& Format() const { return sp; }
   virtual DWORD Sum(const IReflectableData &data) const { return ((const Payment&)data).sum; }

   SyncPayment sp;
};

bool BalanceAdd::Indexing(DocIndex *docIndex, IProgressIndicator *pc) const
{
   return ::Indexing(docIndex, pc, type, *this, NULL, false);
}

struct RemnantsType : public DocType
{
   RemnantsType()
   {
      type = dtRemnants;
      name = L"Остатки";
   }

   virtual void OpenListForm(CEOID orgid) const { OpenOrgRemnantsForm(orgid); }
};

void InitCustomDocTypeSet()
{
   docTypeManager.Replace(dtBalance, new BalanceAdd());
   docTypeManager.insert(new RemnantsType());
}
