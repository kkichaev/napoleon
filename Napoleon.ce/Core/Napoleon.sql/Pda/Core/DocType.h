/*
 * Copyright (C), 2007-2009, ƒенис ћос€гин
 *
 * “ип документа - не содержит ссылок на базу
 *
 *  ert   13/03/2008   creating
 *  ert   22/06/2009   update
 *
 */
#ifndef __DOC_TYPE_H
#define __DOC_TYPE_H

#include <set>
#include <vector>
#include <Reflection.h>
#include <DBImpl.h>

#include <Document.h>

/*
use table org_sums(id,sum,docType,dirty)
*/

extern wchar_t dtOrder[];
extern wchar_t dtDelivery[];
extern wchar_t dtBalance[];

/*
enum DocumentTypes
{
   dtOrder,
   dtDelivery,
   dtBalance,
   dtOrgInfo,
   dtRemnants,
   dtPoll,
   dtRets, // ¬озвраты
   dtVisits, // посещени€
   dtProxy, // доверенности
};
*/

struct CompareDocType;
// DocType создает список документов
class DocumentList
{
public:
   DocumentList(const IDocFactory &df, const std::vector<ROWID> &rids);
   ~DocumentList();

   unsigned Count() const { return rids.size(); }

   // возвращает внутренний указатель, которые измен€етс€ при сл. обращении к функции
   // используетс€ Unbind, если надо отв€зать документ от коллекции
   // может вернуть NULL
   virtual IDocument* Get(unsigned index);

   void Unbind(IDocument* doc)
   {
      if( document == doc )
         document = NULL;
   }

   void Free(IDocument* doc);

   void ClearCache();

protected:
   std::vector<ROWID> rids;
   const IDocFactory &factory;
   IDocument *document;
};

struct OrgSums : public IReflectableData
{
   const wchar_t *id;
   const wchar_t *type;
   DWORD sum;

   DECLARE_TYPE_REFLECTION(OrgSums)
};

struct Sum : public IReflectableData
{
   DWORD sum;
   DECLARE_TYPE_REFLECTION(Sum)
};

class OrgSumImpl : public DBImpl<OrgSums>
{
public:
   OrgSumImpl() : DBImpl(L"org_sums") {}
   virtual const wchar_t*  KeyFields() const { return L"id,type"; }
   virtual const wchar_t** Indexes() const { return NULL; }

   DWORD GetSum(const wchar_t *docType, const wchar_t *orgID)
   {
      type = docType;
      id = orgID;
      if( Read() ) return sum;
      return 0;
   }
};

class OrgDocsList;
class DocType
{
public:
   enum Flags { dtHaveSum = 1, dtCreatable = 2 };

   // фабрика не ссылка, тольк из-за find в DocTypeManager, там предаетс€ NULL
   DocType(const wchar_t* t, const IDocFactory *df, DWORD f=0);

   const wchar_t* Type() const { return type; }

   //const char* CMD() const;
   //const wchar_t* SendText(int count) const;

   void Refresh();

   DWORD GetSum() const;

   DWORD CalcSum(const wchar_t *orgid) const;

   virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const;
   virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, 
      const wchar_t *whereStr = L"", const wchar_t *orderStr = L"" ) const;

   // используетс€ если надо отправл€ть документы под другим именем (Task)
   virtual const wchar_t* SendTypeName() const;

   void RemoveDocuments(const wchar_t *orgid, const wchar_t *whereStr = L"");

   virtual void ClearDirty(const std::vector<ROWID> &rids) const;

   IDocument* FindDocument(const wchar_t* id, const FILETIME& date) const;

   void DropTable();

   bool IsCreatable() const { return (flags & dtCreatable) != 0; }
   bool CreateDocument(const ROWID &rid) const;

#ifdef SCRIPT_DOC
   virtual bool ShowInDocumentList() const { return ((flags & dtCreatable) == 0); }
#else
   virtual bool ShowInDocumentList() const { return true; }
#endif

   bool operator< (const DocType &ref) const { return wcscmp(type, ref.type) < 0; }

   IDocument* CreateDocument() const { return (factory != NULL) ? factory->Create() : NULL; }
   void FreeDocument(IDocument* document) const { if(factory != NULL) { factory->Free(document); } }

   bool CheckDBFormat();

protected:
   void MakeWhereStr(std::wstring *sql, const wchar_t *orgid) const;

   const wchar_t *type;
   const IDocFactory *factory;
   DWORD flags;

   mutable std::wstring sendTypeName;
};


struct CompareDocType
{
   bool operator() (const DocType* _Left, const DocType* _Right) const 
   {
      return (*_Left) < (*_Right);
   }
};

class DocTypeManager : public std::vector<DocType*>
{
public:
   DocTypeManager();
   ~DocTypeManager();

   void insert(DocType* dt);
   DocTypeManager::iterator find(const wchar_t *type);

   void Init();

   void  SumChanged(const wchar_t* type, const wchar_t *orgid);
   void  Refresh(const wchar_t* type);

   const DocType* FindFromDataType(const wchar_t* type);
   const DocType* GetDocType(const wchar_t* type);

   void Replace(DocType *newDocType);

   void RemoveOrg(const wchar_t *orgid);
   void DropTable();

   static void AddDeliveryInfo(const IReflectableData &data, const ROWID &id);
   static void AddPaymentInfo(const IReflectableData &data, const ROWID &id);
   static void UpdateDocInfo();
};

extern DocTypeManager docTypeManager;

void InitDocTypeSet(); // вызываетс€ в конструкторе модул€ _Module
void InitCustomDocTypeSet(); // вызываетс€ в InitDocTypeSet() дл€ создани€ дополнительных типовы

void DestroyDocTypeSet(); // вызываетс€ при остановке приложени€ MainFrame::OnQuit

void AddProceededHandler(IProceededHandler* handler);
IProceededHandler* GetProceededHandler(const wchar_t* type);

void SetNextCreatedDoc(const wchar_t* type);
bool CreateNextDoc(const wchar_t* orgID);

#endif
