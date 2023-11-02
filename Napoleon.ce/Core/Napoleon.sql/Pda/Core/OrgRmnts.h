/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   15/03/2008   creating
 */
#ifndef __ORG_RMNTS_H
#define __ORG_RMNTS_H

#include "ObjImpl.h"
#include "DocImpl.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <ListForm.h>

class OrderImpl;
struct OrgRemnantsImpl : public DBImpl<OrgRemnants>, public IDocument, public ICreatableDocument
{
   //static void GetItemData(DWORD *qty, DWORD *vistDataBefore, const wchar_t *orgID, const wchar_t* itemID);

   OrgRemnantsImpl() : DBImpl(L"remnants") {}

   virtual const wchar_t*  KeyFields() const { return L"id,date"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
   virtual const wchar_t* ID() const { return id; } // id контрагента
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual void EditDocument(UINT retForm);

   // return null if is not creatable
   virtual ICreatableDocument* Creatable(){ return (ICreatableDocument*)this; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);

   virtual IDocument* Copy();

   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); }
   virtual bool WriteDocument() { return Write(); }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // for sending
   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   //virtual const char* CMD() const { return SND_ORG_RMNTS; }
   //virtual const wchar_t* SendText(int count) const { return L"Передача остатков"; }

   virtual bool IsDirty() const { return ((flags & orfDirty) != 0); }
   virtual bool ClearDirty(SQLTable *table, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   //------------------- Instance -----------------
   void Update(const wchar_t* id, DWORD qty, bool sku);
   OrgRemnantsItem *FindItem(const wchar_t* id) const;
   
   bool Load(const wchar_t* id, const FILETIME& refDate);
   void Load(const OrderImpl& order);

   DWORD GetItemQty(const wchar_t* id);
};

void OpenOrgRemnantsForm(OrgRemnantsImpl *r, bool retToOrgDocs);

extern wchar_t dtRemnants[];
#endif
