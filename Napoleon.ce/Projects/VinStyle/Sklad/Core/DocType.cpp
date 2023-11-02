/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Тип документа
 *
 *  ert   13/03/2008   creating
 */
#include "stdafx.h"
#include "DocType.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DBImpl.h>

#include "FormEntries.h"

//
//------------------------------------------------- DocList ------------------------------
//
DocumentList::DocumentList(const IDocFactory &df, const std::vector<ROWID> &r) :
   factory(df), rids(r), document(NULL)
{
}

DocumentList::~DocumentList()
{
   factory.Free(document);
}

void DocumentList::Free(IDocument *doc)
{
   factory.Free(doc);
}

IDocument* DocumentList::Get(unsigned index)
{
   if( index >= rids.size() )
      return NULL;

   if( document == NULL )
      document = factory.Create();

   return ( document->ReadDocument(rids[index])) ? document : NULL;
}

void DocumentList::ClearCache()
{
   if( document != NULL )
   {
      factory.Free(document);
      document = NULL;
   }
}

//
//------------------------------------------------- DocType ------------------------------
//
DocType::DocType(const wchar_t* t, const IDocFactory *df, DWORD f) :  flags(f), type(t), factory(df)
{
   if( factory != NULL )
   {
      IDocument *doc = factory->Create();
      if( doc != NULL )
      {
         ICreatableDocument *c = doc->Creatable();
         if( c != NULL ) flags |= dtCreatable;
         else flags &= (~dtCreatable);

         factory->Free(doc);
      }
   }
}

void DocType::MakeWhereStr(std::wstring *sql, const wchar_t *orgid) const
{
   sql->append(L" WHERE type='"); sql->append(type); sql->append(L"'");
   if( *orgid != '\0' )
   {
      sql->append(L" AND id='"); 
      sql->append(orgid); 
      sql->append(L"'");
   }
}

DWORD DocType::GetSum() const
{
   return 0;
   //OrgSumImpl os;
   //SQLTable table(os.Name());
   //
   //std::wstring sql;
   //MakeWhereStr(&sql, L"");

   //Sum s;
   //DWORD sum = 0;
   //bool res = table.Select(&s, sql.c_str());
   //while( res )
   //{
   //   sum += s.sum;
   //   res = table.SelectNext(&s);
   //}
   //return sum;
}

//void DocType::OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const
//{ 
//   if( curForm != NULL )
//      curForm->SetViewType(this);
//   else
//      OpenOrgDocs(orgid, type);
//}
//
bool DocType::GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, const wchar_t *whereStr, const wchar_t *orderStr) const
{
   IDocument *doc = factory->Create();
   if( doc == NULL ) return false;

   std::wstring sql;
   
   if( *orgid != L'\0' )
   {
      sql.assign(L" WHERE id='"); sql.append(orgid); sql.append(L"'");
   }

   if( *whereStr != L'\0' )
   {
      if( sql.empty() )
      {
         sql.assign( L" WHERE " );
         sql.append(whereStr);
      }
      else
      {
         sql.append(L" AND (");
         sql.append(whereStr);
         sql.append(L")");
      }
    }

   if( *orderStr != L'\0' )
   {
      sql.append(L" ORDER BY ");
      sql.append(orderStr);
   }

   std::vector<ROWID> rids;
   SQLTable table(doc->DBData()->Name());
   table.RIDList(&rids, sql.c_str());

   factory->Free(doc);

   *orgDocs = new DocumentList(*factory, rids);
   return true;
}

//void DocType::RemoveDocuments(const wchar_t *orgid, const wchar_t *whereStr)
//{
//   IDocument *doc = factory->Create();
//   if( doc == NULL ) return;
//
//   std::wstring sql(L"DELETE FROM ");
// 
//   sql += doc->DBData()->Name();
//   factory->Free(doc);
//
//   if( *orgid != L'\0' )
//   {
//      sql.append(L" WHERE id='");
//      sql.append(orgid);
//      sql.append(L"'");
//   }
//
//   if( *whereStr != L'\0' )
//   {
//      if( *orgid != L'\0' )
//         sql.append(L" AND (");
//      else
//         sql.append(L" WHERE (");
//      sql.append(whereStr);
//      sql.append(L")");
//   }
//
//   SQLTable::Execute(sql.c_str());
//
//   if( *orgid != L'\0' )
//   {
//      OrgSumImpl os;
//      os.id = orgid;
//      os.type = type;
//      if( os.Read() )
//      {
//         os.sum = CalcSum(orgid);
//         os.Update(L"sum");
//      }
//   } else
//      Refresh();
//}

const wchar_t* DocType::SendTypeName() const
{
   if( sendTypeName.empty() )
   {
      IDocument* d = CreateDocument();
      sendTypeName = d->Data()->GetType().Name();
      FreeDocument(d);
   }
   return sendTypeName.c_str();
}

//const char* DocType::CMD() const
//{
//   const char *cmd = "";
//
//   IDocument *doc = factory->Create();
//   if( doc != NULL )
//   {
//      ICreatableDocument *c = doc->Creatable();
//      if( c != NULL ) cmd = c->CMD();
//      delete doc;
//   }
//
//   return cmd;
//}

//const wchar_t* DocType::SendText(int count) const
//{
//   const wchar_t *text = L"";
//
//   IDocument *doc = factory->Create();
//   if( doc != NULL )
//   {
//      ICreatableDocument *c = doc->Creatable();
//      if( c != NULL ) text = c->SendText(count);
//      delete doc;
//   }
//
//   return text;
//}

void DocType::ClearDirty(const std::vector<ROWID> &rids) const
{
   IDocument *doc = factory->Create();
   if( doc != NULL )
   {
      ICreatableDocument *c = doc->Creatable();

      SQLTable table(doc->DBData()->Name());

      std::vector<ROWID>::const_iterator i = rids.begin();
      for(; i != rids.end(); i++ )
      {
         doc->ReadDocument(*i);
         c->ClearDirty(&table, false);
      }

      factory->Free(doc);
   }
}

void DocType::DropTable()
{ 
   IDocument *doc = factory->Create();
   if( doc != NULL )
   {
      SQLTable::DropTable(doc->DBData()->Name());
      factory->Free(doc);
   }
}

DWORD DocType::CalcSum(const wchar_t *orgid) const
{
   if( (flags & dtHaveSum) == 0 ) return 0;

   DocumentList *dl;
   if( !GetDocuments(orgid, &dl) ) return 0;

   DWORD sum = 0;
   int i = dl->Count() - 1;
   while( i >= 0 )
   {
      IDocument *doc = dl->Get(i);
      if( doc != NULL )
         sum += doc->Sum();
      i--;
   }

   delete dl;
   return sum;
}

//bool DocType::CreateDocument(const ROWID &rid) const
//{
//   if( (flags & dtCreatable) == 0 ) return false;
//
//   IDocument *doc = factory->Create();
//   if( doc == NULL ) return false;
//
//   ICreatableDocument *c = doc->Creatable();
//
//   SQLCheckTable(*doc->DBData());
//   if( c->CreateDocument(rid) == false )
//   {
//      factory->Free(doc);
//      return false;
//   }
//
//   return true;
//}

//void DocType::Refresh()
//{
//   OrgImpl oi;
//   SQLTable table(oi.Name());
//   
//   OrgSumImpl osi;
//   std::wstring sql(L"DELETE FROM '"), wh;
//   MakeWhereStr(&wh, L"");
//
//   sql += osi.Name(); sql += L"' "; sql += wh;
//   SQLTable::Execute(sql.c_str());
//
//   bool doing = table.Select(&oi);
//   while( doing )
//   {
//      OrgSumImpl os;
//      os.sum = CalcSum(oi.id);
//      if( os.sum != 0 )
//      {
//         os.id = oi.id;
//         os.type = type;
//         os.Write();
//      }
//      doing = table.SelectNext(&oi);
//   }
//}

bool DocType::CheckDBFormat()
{
   IDocument *doc = CreateDocument();
   if( doc == NULL ) return false;

   SQLTable table(doc->DBData()->Name());
   bool ret = table.CheckDBFormat(doc->Data()->GetType());

   FreeDocument(doc);
   return ret;
}

//IDocument* DocType::FindDocument(const wchar_t* id, const FILETIME& date) const
//{
//   IDocument *d = NULL;
//   DocumentList *dl;
//
//   if( GetDocuments(id, &dl) )
//   {
//      for( int di=dl->Count()-1; di>=0; di-- )
//      {
//         IDocument* curDoc = dl->Get(di);
//         if( curDoc )
//         {
//            ICreatableDocument *cd = curDoc->Creatable();
//            if( cd != NULL )
//            {
//               if( CompareFileTime(&cd->UID(), &date) == 0 )
//               {
//                  dl->Unbind(curDoc);
//                  d = curDoc;
//                  break;
//               }
//            }
//         }
//      }
//
//      delete dl;
//   }
//
//   return d;
//}

//
//------------------------------------------------- DocTypeManager ------------------------------
//
DocTypeManager::DocTypeManager()
{
}

//void DocTypeManager::RemoveOrg(const wchar_t *orgid)
//{
//   DocTypeManager::iterator i = begin();
//   while( i != end() )
//   {
//      (*i)->RemoveDocuments(orgid);
//      i++;
//   }
//
//   OrgSumImpl os;
//   os.id = orgid;
//   os.RemoveByKey(L"ID");
//}

//void DocTypeManager::SumChanged(const wchar_t *type, const wchar_t *orgid)
//{
//   const DocType* dt = GetDocType(type);
//
//   OrgSumImpl os;
//   os.id = orgid;
//   os.type = dt->Type();
//
//   os.Read();
//
//   os.sum = dt->CalcSum(orgid);
//   os.Write();
//}

DocTypeManager::~DocTypeManager()
{
   DocTypeManager::iterator i = begin();
   for( ; i != end(); i++ )
   {
      delete (*i);
   }
}

const DocType* DocTypeManager::FindFromDataType(const wchar_t *type)
{
   DocTypeManager::const_iterator i = begin();
   for( ; i != end(); i++ )
   {
      IDocument *d = (*i)->CreateDocument();
      const wchar_t* docType = d->Data()->GetType().Name();
      (*i)->FreeDocument(d);

      if( wcscmp(docType, type) == 0 )
         break;
  }

   if( i == end() ) return NULL;
   return (*i);
}

const DocType* DocTypeManager::GetDocType(const wchar_t *type)
{
   DocTypeManager::const_iterator fnd = find(type);

   if( fnd == end() ) return NULL;
   return (*fnd);
}

void DocTypeManager::Replace(DocType *newDocType)
{
   DocTypeManager::iterator i = find(newDocType->Type());
   if( i != end() )
   {
      delete (*i);
      erase(i);
   }
   docTypeManager.insert(newDocType);
}

void DocTypeManager::Init()
{
   //OrgSumImpl os;
   //os.CreateTable();
}

void DocTypeManager::DropTable()
{
   DocTypeManager::iterator i = begin();
   while( i != end() )
   {
      (*i)->DropTable();
      i++;
   }

   //OrgSumImpl os;
   //SQLTable::DropTable(os.Name());
}

DocTypeManager::iterator DocTypeManager::find(const wchar_t *type)
{
   DocTypeManager::iterator i = begin();
   for( ; i != end(); i++ )
   {
      if( wcscmp((*i)->Type(), type) == 0 )
         break;
   }
   return i;
}

//void DocTypeManager::Refresh(const wchar_t *type)
//{
//   DocType* dt = (DocType*)GetDocType(type);
//   if( dt != NULL ) dt->Refresh();
//}

void DocTypeManager::insert(DocType* dt)
{
   push_back(dt);
   dt->CheckDBFormat();
}

#pragma warning(disable : 4073)
#pragma init_seg(lib)
DocTypeManager docTypeManager;

