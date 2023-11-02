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

class AgentTaskImpl : public DBImpl<AgentTask>, public IDocument, public ICreatableDocument
{
public:
   AgentTaskImpl() : DBImpl(L"AgentTask") {}

   virtual const wchar_t*  KeyFields() const { return L"id,date"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *idx[] = {L"appointDate", NULL}; return idx; }

   // ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return appointDate; }
   virtual const wchar_t* Description() const;

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
   virtual bool CanRemove() const;
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
};

extern wchar_t dtAgentTask[];
extern bool OutOfPlan;

class DocType;
void SetAgentNextDocType(const DocType* docType);
void OpenAgentTask(AgentTaskImpl *task, bool retToDocList, const wchar_t* docType);
void OpenAgentTask(const ROWID& orgID, bool canCheck, const DocType* docType); // список всех не выполненных задач по контрагенту 

#endif
