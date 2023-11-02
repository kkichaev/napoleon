/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Восход дополнения
 *
 *  ert   02/08/2010   creating
 */
#ifndef __ADD_TUKANOV_H
#define __ADD_TUKANOV_H

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocImpl.h>
#include <PriceForm.h>

const int PLAN_SCALE = 10;

extern wchar_t dtReturn[];

class ReturnImpl : public OrderImpl
{
public:
   ReturnImpl() : OrderImpl(L"Returns", dtReturn) { instanceFlags |= ifNoUpdatePrice; }

   virtual bool Init(const ROWID &orgID);
   virtual void EditDocument(UINT retForm);
   virtual bool CanRemove() const;

   virtual bool HideRemnants() const { return true; }
   virtual bool CreateDocument(const ROWID &orgID);

   virtual bool CheckQty() const { return false; }
   virtual bool EditDetail();
};

#endif