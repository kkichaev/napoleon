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

DWORD ControlDocImpl::Sum() const
{
   DWORD sum = 0;
   return sum;
}

DWORD ControlDocImpl::Qty() const
{
   DWORD sum = 0;
   for( unsigned i=0; i<items.size(); i++ )
      sum += items.at(i).qty;
   return sum;
}

bool ControlDocImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   const wchar_t *updStr = L"params";
   if( reverse )
   {
      if( (params & ofExported) != 0 ) params &= (~ofExported);
      else params |= ofExported;
   } else
   {
      params |= ofExported;
   }

   return (updateTable == NULL) ? true : updateTable->Update(*this, updStr, rid);
}

void ControlDocImpl::EditDocument(UINT retForm)
{
}

int ControlDocImpl::TotalQty(const wchar_t* id) const
{
   int qty = 0;

   std::vector<DocItem>::const_iterator i = items.begin();

   for( ; i!=items.end(); i++ )
      if( wcscmp(i->id, id) == 0 )
         qty +=  i->qty;

   return qty;
}

bool ControlDocImpl::Update(const wchar_t* id, int qty)
{
   DocItem di;
   di.id = holder.Add(id);
   di.qty = qty;
   di.order = items.size();
   items.push_back(di);

   return Write();
}

std::vector<DocItem>::iterator ControlDocImpl::FindItem(const wchar_t *id) const
{
   std::vector<DocItem>::iterator i = ((vector_t<DocItem>&)items).begin();

   for( ; i!=items.end(); i++ )
      if( wcscmp(i->id, id) == 0 ) return i;

   return ((vector_t<DocItem>&)items).end();
}

void ControlDocImpl::RemoveItems(const wchar_t* id)
{
   std::vector<DocItem>::iterator i = items.begin();

   for( ; i!=items.end(); )
   {
      if( wcscmp(i->id, id) == 0 )
         i = items.erase(i);
      else 
         i++;
   }
}

bool ControlDocImpl::Remove()
{
   if( rid == NO_ROWID ) return true;

   if( !table.Remove(rid) ) return false;

   rid = NO_ROWID;
   return true;
}

bool ControlDocImpl::Send()
{
   const class DocType* dt = docTypeManager.GetDocType(docType);
   return (_Module.SendDocument(this, dt) == 0);
}
