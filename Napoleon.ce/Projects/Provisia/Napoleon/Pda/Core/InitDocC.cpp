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
#include "Add.h"
#include "Refr.h"

wchar_t dtAgentTask[] = L"Задачи агента";
wchar_t dtPPay[] = L"Обещ.платеж";
wchar_t dtDocPay[] = L"Кассовый отчет";
wchar_t dtRfrDoc[] = L"Оборудование";

struct AgentTaskFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new AgentTaskImpl(); }
   virtual void Free(IDocument* document) const { delete (AgentTaskImpl*)document; }
} agentTaskFactory;

struct AgentTaskDocType : public DocType
{
   AgentTaskDocType() : DocType(dtAgentTask, &agentTaskFactory, 0) {}
   //virtual bool ShowInDocumentList() const { return false; }
   //virtual void OpenForm(const wchar_t *orgid, OrgDocsList* curForm) const {}

   // чтобы заглушить автоматическую загрузку документов при передаче
   //virtual bool GetDocuments(const wchar_t *orgid, DocumentList **orgDocs, 
   //   const wchar_t *whereStr = L"", const wchar_t *orderStr = L"" ) const { return false; }
};

struct PPayFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new PPay(); }
   virtual void Free(IDocument* document) const { delete (PPay*)document; }
} ppayFactory;

struct PPayDocType : public DocType
{
   PPayDocType() : DocType(dtPPay, &ppayFactory, dtHaveSum) {}
   virtual const wchar_t* SendTypeName() const { return L"PPay"; }
};

struct DocPayFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new DocPayImpl(); }
   virtual void Free(IDocument* document) const { delete (DocPayImpl*)document; }
} docPayFactory;

struct DocPayType : public DocType
{
   DocPayType() : DocType(dtDocPay, &docPayFactory, dtHaveSum) {}
};

class DocPayPHandler : public IProceededHandler
{
public:
   DocPayPHandler() {}

   virtual const wchar_t* Name() const { return L"DocPay"; }
   virtual SQLTable* Prepare(const OrderProceeded& data);
   virtual bool SetProceeded(SQLTable *table, const OrderProceeded& data) { return table->ExecCommand(data); }
};

struct DocRfrgFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new DocRfrgImpl(); }
   virtual void Free(IDocument* document) const { delete (DocRfrgImpl*)document; }
} docRfrgFactory;

struct DocRfrgType : public DocType
{
   DocRfrgType() : DocType(dtRfrDoc, &docRfrgFactory, 0) {}
};

void InitCustomDocTypeSet()
{
   docTypeManager.insert(new PPayDocType());
   docTypeManager.insert(new DocPayType());
   docTypeManager.insert(new AgentTaskDocType());
   docTypeManager.insert(new DocRfrgType());

   AddProceededHandler(new DocPayPHandler());
}


SQLTable* DocPayPHandler::Prepare(const OrderProceeded& data)
{
   const DataReflector& type = data.GetType();
   std::vector<MemberType*> params;

   std::wstring tname(DocPayImpl().Name());
   SQLTable *table = new SQLTable(tname.c_str());

   wchar_t buf[10];
   _itow(ofProceeded, buf, 10);

   std::wstring sql(L"UPDATE ");
   sql += tname;
   sql += L" SET flags = flags | ";
   sql += buf;

   sql += L", podRemark = ?";

   MemberType *remT = (MemberType*)&type.Type(L"remark");
   params.push_back(remT);

   sql += L" WHERE date = ?";

   MemberType *idT = (MemberType*)&type.Type(L"created");
   params.push_back(idT);

   if( !table->PrepareCommand(sql, params) )
   {
      delete table;
      table = NULL;
   }

   return table;
}
