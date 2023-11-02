/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Загрузчик типов докуменов дополнительный
 *
 *  ert   14/03/2008   creating
 */
#include "stdafx.h"
#include <Exchange.h>
#include <DocType.h>
#include <InitDoc.h>

#include "AgentTask.h"
#include "Incass.h"

#include <ObjImpl.h>

wchar_t dtAgentTask[] = L"Задачи агента";
wchar_t dtSVTask[] = L"Задачи супервайзера";

struct AgentTaskFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new AgentTaskImpl(); }
   virtual void Free(IDocument* document) const { delete (AgentTaskImpl*)document; }
} agentTaskFactory;

struct AgentTaskDocType : public DocType
{
   AgentTaskDocType() : DocType(dtAgentTask, &agentTaskFactory, 0) {}
   virtual bool ShowInDocumentList() const { return false; }
   virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const {}

   //// чтобы заглушить автоматическую загрузку документов при передаче
   virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, 
      const wchar_t *whereStr = L"", const wchar_t *orderStr = L"" ) const
   {
      std::wstring twstr( L"((flags & 4) = 0)" );
      if( *whereStr != L'\0' )
      {
         twstr.append( L" AND ");
         twstr.append(whereStr);
      }

      return DocType::GetDocuments(orgid, orgDocs, twstr.c_str(), orderStr);
   }
};

struct SVTaskDocType : public DocType
{
   SVTaskDocType() : DocType(dtSVTask, &agentTaskFactory, 0) {}
   virtual bool ShowInDocumentList() const { return false; }
   virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const {}

   virtual const wchar_t* SendTypeName() const { return L"SVTask"; }

   //// чтобы заглушить автоматическую загрузку документов при передаче
   virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, 
      const wchar_t *whereStr = L"", const wchar_t *orderStr = L"" ) const
   {
      std::wstring twstr( L"((flags & 4) != 0)" );
      if( *whereStr != L'\0' )
      {
         twstr.append( L" AND ");
         twstr.append(whereStr);
      }

      return DocType::GetDocuments(orgid, orgDocs, twstr.c_str(), orderStr);
   }
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new AgentTaskDocType());
   docTypeManager.insert(new SVTaskDocType());
   docTypeManager.insert(new IncassType());
}
