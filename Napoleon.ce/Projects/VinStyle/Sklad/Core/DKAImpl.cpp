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

bool DKA1Impl::ClearDirty(SQLTable *updateTable, bool reverse)
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

void DKA1Impl::EditDocument(UINT retForm)
{
}

bool DKA1Impl::Remove()
{
   if( rid == NO_ROWID ) return true;

   if( !table.Remove(rid) ) return false;

   rid = NO_ROWID;
   return true;
}

bool DKA1Impl::Send()
{
   const class DocType* dt = docTypeManager.GetDocType(docType);
   return (_Module.SendDocument(this, dt) == 0);
}

bool DKA2Impl::ClearDirty(SQLTable *updateTable, bool reverse)
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

void DKA2Impl::EditDocument(UINT retForm)
{
}

bool DKA2Impl::Remove()
{
   if( rid == NO_ROWID ) return true;

   if( !table.Remove(rid) ) return false;

   rid = NO_ROWID;
   return true;
}

bool DKA2Impl::Send()
{
   const class DocType* dt = docTypeManager.GetDocType(docType);
   return (_Module.SendDocument(this, dt) == 0);
}

bool DKA2Impl::HaveItem(const wchar_t *code) const
{
	vector_t<OrderItem>::const_iterator i = items.begin();
	for( ; i != items.end(); i++ )
	{
		if( wcscmp(i->id, code) == 0 )
			return true;

		ItemGroupsImpl igi;
		if( igi.HaveItem(i->id, code) )
			return true;
	}
	return false;
}

void ItemGroupsImpl::LoadPriceItems(std::vector<std::wstring>* prcItems, const wchar_t* barcode)
{
	this->barcode = (wchar_t*)barcode;
	if(Read())
	{
		vector_t<PalletItem>::const_iterator i = items.begin();
		for( ; i != items.end(); i++ )
		{
			if( *i->id != L'\0' )
				prcItems->push_back(i->id);
			else
			{
				ItemGroupsImpl igi;
				igi.LoadPriceItems(prcItems, i->barcode);
			}
		}
	}
}

bool ItemGroupsImpl::HaveItem(const wchar_t *groupID, const wchar_t *itemID)
{
	this->barcode = (wchar_t*)groupID;
	if(Read())
	{
		vector_t<PalletItem>::const_iterator i = items.begin();
		for( ; i != items.end(); i++ )
		{
			if( *i->id == L'\0' )
			{
				if(wcscmp(i->barcode, itemID) == 0)
					return true;
				
				ItemGroupsImpl igi;
				if( igi.HaveItem(i->barcode, itemID) )
					return true;
			}
		}
	}

	return false;
}

bool ScanDocImpl::Send()
{
   const class DocType* dt = docTypeManager.GetDocType(docType);
   return (_Module.SendDocument(this, dt) == 0);
}
