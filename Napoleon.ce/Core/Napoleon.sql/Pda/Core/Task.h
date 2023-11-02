/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Автоптека add-in
 *
 *  ert   14/09/2009   creating
 */
#ifndef __TASK_H
#define __TASK_H

#include <Exchange.h>
#include <DocImpl.h>
#include <DocType.h>

class TaskImpl : public DBImpl<Task>, public IDocument, public ICreatableDocument
{
public:
   TaskImpl() : DBImpl(L"orgTask") {}

   virtual const wchar_t*  KeyFields() const { return L"id,date"; }
   virtual const wchar_t** Indexes() const { return NULL; }
   
   virtual const wchar_t* ID() const { return id; } // id контрагента
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual DWORD Sum() const { return 0; }

   virtual IReflectableData* Data() { return this; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual void EditDocument(UINT retForm);

   // return null if is not creatable
   virtual ICreatableDocument* Creatable(){ return (ICreatableDocument*)this; }

   virtual bool CreateDocument(const ROWID &orgID) { return Init(orgID); }
   virtual bool Init(const ROWID &orgID);

   virtual IDocument* Copy() { return NULL; }

   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); }
   virtual bool WriteDocument() { return Write(); }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // for sending
   virtual const ROWID& Serialize(StreamWriter* writer) const;

   //virtual const char* CMD() const { return SND_ORG_DO; }
   //virtual const wchar_t* SendText(int count) const { return L"Передача заданий"; }

   virtual bool IsDirty() const { return ((flags & ofExported) == 0 && *doing != L'\0'); }
   virtual bool ClearDirty(SQLTable *table, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   bool CanEdit() const { return ((flags & ofExported) == 0); }

   static bool HaveTask(const wchar_t *id);
   static void EditTask(const wchar_t *id, bool openOrgDocs);

   typedef void (*TaskClosed)(const TaskImpl& task);
   static void EditTask(const ROWID& id, TaskClosed taskClosed);
};

#endif