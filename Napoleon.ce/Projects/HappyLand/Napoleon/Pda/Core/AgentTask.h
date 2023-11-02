/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Задачи агента
 *
 *  ert   25/10/2010   creating
 */ 
#ifndef __AGENS_TASK_H
#define __AGENS_TASK_H

#include <Exchange.h>
#include <DBImpl.h>
#include <Document.h>

class DocDataList;
class AgentTaskImpl : public DBImpl<AgentTask>, public IDocument, public ICreatableDocument
{
public:
   AgentTaskImpl() : DBImpl(L"AgentTask") {}

   virtual const wchar_t*  KeyFields() const { return L"id,date,category"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   // ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const { return L""; }

   virtual IReflectableData* Data() { return this; }

   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
   // ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const { return true; }
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual bool IsDirty() const { return (flags & AgentTask::Exported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
   void SetDone();

   static void AddDocs(DocDataList* documents, const wchar_t* id, const FILETIME& date, bool isExecDate);
};

extern wchar_t dtAgentTask[];
extern wchar_t dtSVTask[];
extern bool OutOfPlan;

class DocType;
void AssignTask(const FILETIME& date, const wchar_t* id, const wchar_t* docType, bool hideSend);
void OpenAgentTask(const FILETIME& date, const wchar_t* id, const DocType* docType, bool canCheck, bool hideSend); // список всех не выполненных задач по контрагенту 

#endif
