/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * Реализация функций заказа
 *
 *  ert   20/08/2007   creating
 *  ert   17/06/2008   modifying (SQL impl)
 */
#include "stdafx.h"

#include <Module.h>

#include "ObjImpl.h"
#include "DocImpl.h"
#include "FormEntries.h"
#include <StdFuncs.h>
#include <NapoleonRes.h>

#include "Preference.h"
#include <Doctype.h>

DWORD OrderImpl::Sum() const
{
   DWORD sum = 0;
   return sum;
}

DWORD OrderImpl::Qty() const
{
   DWORD sum = 0;
   for( unsigned i=0; i<items.size(); i++ )
      sum += items.at(i).qty;
   return sum;
}

bool OrderImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   return Remove();

   //const wchar_t *updStr = L"params";
   //if( reverse )
   //{
   //   if( (params & ofExported) != 0 ) params &= (~ofExported);
   //   else params |= ofExported;
   //} else
   //{
   //   params |= ofExported;
   //}

   //return (updateTable == NULL) ? true : updateTable->Update(*this, updStr, rid);
}

void OrderImpl::EditDocument(UINT retForm)
{
}

std::vector<OrderItem>::iterator OrderImpl::FindItem(const wchar_t *id) const
{
   std::vector<OrderItem>::iterator i = ((vector_t<OrderItem>&)items).begin();

   for( ; i!=items.end(); i++ )
      if( wcscmp(i->id, id) == 0 ) return i;

   return ((vector_t<OrderItem>&)items).end();
}

bool OrderImpl::Remove()
{
   if( rid == NO_ROWID ) return true;

   if( !table.Remove(rid) ) return false;

   rid = NO_ROWID;
   return true;
}

const OrderItem* OrderImpl::FindItem(const wchar_t* rack, const wchar_t* id) const
{
   std::vector<OrderItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( (*rack == L'\0' || wcscmp(rack, i->rack) == 0) && wcscmp(id, i->id) == 0 )
         return &(*i);

   return NULL;
}

const OrderItem* OrderImpl::FindMark(const wchar_t* mark) const
{
   std::vector<OrderItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( wcscmp(mark, i->mark) == 0 )
         return &(*i);

   return NULL;
}


const OrderItem* OrderImpl::FindBC(const wchar_t* rack, const wchar_t* barcode) const
{
   std::vector<OrderItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( (*rack == L'\0' || wcscmp(rack, i->rack) == 0) && wcscmp(barcode, i->barcode) == 0 )
         return &(*i);

   return NULL;
}

bool OrderImpl::Send()
{
   const class DocType* dt = docTypeManager.GetDocType(docType);
   return (_Module.SendDocument(this, dt) == 0);
}

const OrderItem* ControlDocImpl::FindItem(const wchar_t* rack, const wchar_t* id) const
{
   std::vector<OrderItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( (*rack == L'\0' || wcscmp(rack, i->rack) == 0) && wcscmp(id, i->id) == 0 )
         return &(*i);

   return NULL;
}

bool ControlDocImpl::HaveRack(const wchar_t* rack) const
{
   std::vector<OrderItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
      if( wcscmp(rack, i->rack) == 0 )
         return true;

   return false;
}
