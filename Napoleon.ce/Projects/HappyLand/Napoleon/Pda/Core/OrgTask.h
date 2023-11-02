/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Документ задачи по контрагенту
 *
 *  ert   21/06/2010   creating
 */ 
#ifndef _HL_ORG_TASK_H
#define _HL_ORG_TASK_H

#include <Exchange.h>
#include <DBImpl.h>
#include <Document.h>

struct OrgPlan : public IReflectableData
{
   enum Params { Done = 1, Exported = 2, SVTask = 4 };

   FILETIME date;

   wchar_t* id;
   wchar_t* task;
   wchar_t* remark;

   DWORD params;

   DECLARE_TYPE_REFLECTION(OrgPlan)
};

class OrgPlanImpl : public DBImpl<OrgPlan>, public IDocument, public ICreatableDocument
{
public:

// ------------------------------- DBImpl -----------------------------------
   OrgPlanImpl() : DBImpl(L"orgPlans") {}

   virtual const wchar_t*  KeyFields() const { return L"date"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"id", NULL }; return index; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return task; }

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const
   {
      // можно удалить только выполненную и отправленную задачу
      return ( (params & Done) != 0 && (params & Exported) != 0 );
   }

   virtual bool RemoveDocument() { return Remove(); }

   virtual void EditDocument(UINT retForm) { OpenPlan(this, retForm); }

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual bool IsDirty() const { return ((params & Exported) == 0); }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------

   static void OpenPlan(OrgPlanImpl* plan, UINT retForm);
   static void OpenPlanList(const wchar_t* id, bool onlyNotDone);
};

#endif
