/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Документ сценария
 *
 *  ert   21/06/2010   creating
 */ 

#ifndef _SCRIPT_DOC_H
#define _SCRIPT_DOC_H

#include "ObjImpl.h"
#include "DocImpl.h"

struct ScriptDocItem : public IReflectableData
{
   wchar_t* type;
   FILETIME date;

   DECLARE_TYPE_REFLECTION(ScriptDocItem)
};

struct ScriptDoc : public IReflectableData
{
   enum States
   { 
      Exported = 1, 
      TaskBeforeDone = 2, 
      TaskAfterDone = 4, 
      OrderOutOfPlan = 8, 
      IncassOutOfPlan = 0x10, 
      PhotoBefore = 0x20, 
      PhotoAfter = 0x40,
      Interrupted = 0x80,
      OrderWasCreated = 0x100, // если заявка была создана, но потом удалилась (только съем остатков)
   };

   wchar_t *id;

   FILETIME date;  // visit date
   FILETIME dateEnd; // visit date

   DWORD sum; // SUM_SCALE
   DWORD flags;

   vector_t<ScriptDocItem> items;

#ifdef GPS_POS
   int   latitude;
   int   longitude;
#endif

   wchar_t* remark;

   DECLARE_TYPE_REFLECTION(ScriptDoc)
};

class ScriptImpl : public DBImpl<ScriptDoc>, public IDocument, public ICreatableDocument
{
public:
// ------------------------------- DBImpl -----------------------------------

   ScriptImpl() : DBImpl(L"scripts") { clearCompleete = false; }

   virtual const wchar_t*  KeyFields() const { return L"id,date"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { NULL }; return index; }
   
// ------------------------------- IDocument functions -----------------------------------
   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual IReflectableData* Data() { return this; }

   // methods for db read|write
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }
   
   virtual DWORD Sum() const;
   virtual const ROWID& RowID() const { return rid; }

// ------------------------------- ICreatableDocument functions -----------------------------------

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument();
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);


   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual bool IsDirty() const;
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   // ------------------------------- Instance functions -----------------------------------
   void AddDocument(IDocument* doc);
   void RemoveDocument(const DocType* type);
   IDocument* GetDocument(const DocType* type);

   void LoadDocuments(DocDataList* list, bool withScript = true);

   bool IsComplete() const;
   void SetClearCompleete() { clearCompleete = true; }

protected:
   bool clearCompleete;
};

struct ScriptData : public IFormData
{
   ScriptData(ScriptImpl *script, bool rdl);
   ~ScriptData() { delete script; }

   ScriptImpl *script;
   bool retToDocList;

   static bool lastRetToDoc;
};

extern wchar_t dtScript[];
extern FILETIME lastScriptDoc;

void OpenScript(ScriptImpl *script, bool retToDocList);
void ShowScriptList(ScriptImpl *script, bool retToDocList);

#endif

