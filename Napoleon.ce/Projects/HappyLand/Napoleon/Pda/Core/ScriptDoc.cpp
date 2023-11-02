/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Документ сценария
 *
 *  ert   21/06/2010   creating
 */ 

#include "stdafx.h"

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <MainFrame.h>

#include "Visit.h"
#include "OrgRmnts.h"

#include <StdFuncs.h>
#include <ListForm.h>
#include <InitDoc.h>
#include "NumInput.h"
#include "TaskList.h"
#include "ResAdd.h"
#include "ScriptDoc.h"
#include <FileType.h>

#include "AgentTask.h"

wchar_t dtScript[] = L"Визиты";
FILETIME lastScriptDoc;
bool ScriptData::lastRetToDoc = false;

const int WM_OPEN_EXIT_FORM = WM_USER + 2;
const int WM_DESTROY_FORM = WM_USER + 3;

BEGIN_TYPE_REFLECTION(ScriptDocItem)
   REGISTER_STRING_MEMBER(ScriptDocItem, type)
   REGISTER_TIMESTAMP_MEMBER(ScriptDocItem, date)
END_TYPE_REFLECTION(ScriptDocItem)

BEGIN_TYPE_REFLECTION(ScriptDoc)
   REGISTER_TIMESTAMP_MEMBER(ScriptDoc, date)
   REGISTER_TIMESTAMP_MEMBER(ScriptDoc, dateEnd)
   REGISTER_STRING_MEMBER(ScriptDoc, id)
   REGISTER_ULONG_SCALE_MEMBER(ScriptDoc, sum, SUM_SCALE)
   REGISTER_ULONG_MEMBER(ScriptDoc, flags)
   REGISTER_COLLECTION_MEMBER(ScriptDoc, items, ScriptDocItem)
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(ScriptDoc, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(ScriptDoc, longitude, GPS_SCALE)
#endif
   REGISTER_STRING_MEMBER(ScriptDoc, remark)
END_TYPE_REFLECTION(ScriptDoc)

BOOL CALLBACK DisableChildsProc(HWND hwnd, LPARAM lParam);
void OpenScriptDetail(ScriptImpl *script, bool retToDocList);

struct ScriptDetailData : public IFormData
{
   ScriptDetailData(ScriptImpl *script, bool retToDocList) { this->script = script; this->retToDocList = retToDocList; }
   ~ScriptDetailData() {} // не удаляем скрипт - он нужен еще в паренте :)

   ScriptImpl *script;
   bool retToDocList;
};

class ScriptDetailForm : public BaseForm
{
public:
   ScriptDetailForm();
   ~ScriptDetailForm();

   virtual DWORD GetResourceID() const { return IDD_SCRIPT_FORM; }
   virtual DWORD GetMenuBarID() const { return IDD_SCRIPT; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);
   virtual void WriteChanges();

   DECLARE_FORM(ScriptDetailForm, IDD_SCRIPT_FORM)

   BEGIN_MSG_MAP(ScriptDetailForm)
      NUM_INPUT_HANDLER(numInput)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
   END_MSG_MAP()

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   ScriptDetailData *data;
   CMenuBarCtrl menuBar;
   NumInput numInput;
};

class ScriptForm;
class ScriptPage
{
public:
   ScriptPage(const wchar_t* type, ScriptImpl *script, ScriptForm *parent, UINT retForm);
   ~ScriptPage();

   void SetForm(BaseForm* form);

   bool HaveDocument() const { return ((docType==NULL) || (document != NULL)); }

   bool AddDoc();
   bool RemoveDoc();

   void BindDocToScript();
   bool Activate(bool activate, bool writeChanges);

   bool HandleMessages(LRESULT &res, UINT uMsg, WPARAM wParam, LPARAM lParam)
   {
      if( form && form->m_hWnd )
      {
         res = ::SendMessage(form->m_hWnd, uMsg, wParam, lParam);
         return true;
      }

      return false;
   }

   void UpdateLayout(bool recalc)
   {
      if( form )
         form->UpdateLayout(recalc);
   }

protected:
   IDocument* document;
   const DocType* docType;
   ScriptImpl *script;
   ScriptForm *parent;
   BaseForm *form;
   UINT retForm;
};

#define CHAIN_TO_CHILD()  \
   if((uMsg == WM_NOTIFY || uMsg == WM_COMMAND) && activePage && \
      activePage->HandleMessages(lResult, uMsg, wParam, lParam ) ) \
   { return (bHandled = TRUE); } 

class ScriptParent : public CWindowImpl<ScriptParent>
{
public:
   ScriptParent() {}

   DECLARE_WND_CLASS(L"SCRIPT_PARENT");
   BEGIN_MSG_MAP(ScriptParent)
   END_MSG_MAP()
};

class ScriptForm : public BaseForm, IFrame
{
public:
   ScriptForm();
   ~ScriptForm();

   //------------------ IFrame -----------------
   virtual bool Load(DWORD formID, IFormData *data);
   virtual HWND LoadMenuBar(DWORD barID, DWORD barV5, DWORD flags)
   {
      return mainFrame->LoadMenuBar(barID, barV5, flags); 
   }

   virtual void Quit() { mainFrame->Quit(); }
   virtual void SetTitle(const wchar_t *title) { mainFrame->SetTitle(title); }
   virtual void CameraActive(bool active) { mainFrame->CameraActive(active); }
   //------------------ IFrame -----------------

   virtual DWORD GetResourceID() const { return IDD_SCRIPT; }
   virtual DWORD GetMenuBarID() const { return IDD_SCRIPT; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   DECLARE_FORM(ScriptForm, IDD_SCRIPT)

   BEGIN_MSG_MAP(ScriptForm)
      NOTIFY_CODE_HANDLER_EX(TCN_SELCHANGE, SelChange);
      MESSAGE_HANDLER(WM_OPEN_EXIT_FORM, OpenForm)
      MESSAGE_HANDLER(WM_DESTROY_FORM, DestroyForm)
      CHAIN_TO_CHILD()
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_ADD, AddDoc)
      COMMAND_ID_HANDLER(IDC_DEL, DelDoc)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
   END_MSG_MAP()

   LRESULT OpenForm(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT DestroyForm(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT AddDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT DelDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT SelChange(LPNMHDR hdr);

protected:
   void LoadMenuBar(bool hideSIP);
   void AddType(const wchar_t *type, const wchar_t* title);

protected:
   IFrame *mainFrame;

   ScriptPage* activePage;
   std::vector<ScriptPage*> pages;

   ScriptData *data;
   CMenuBarCtrl menuBar;
   CTabCtrl tabs;
   HFONT tabFont;
   ScriptParent pageParent;
   CStatic textWnd;
};

//
//----------------------------------- VisitType -------------------------------------------
//
struct ScriptFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new ScriptImpl(); }
   virtual void Free(IDocument* document) const { delete (ScriptImpl*)document; }
} scriptFactory;

ScriptType::ScriptType() :
   DocType(dtScript, &scriptFactory, dtHaveSum)
{
}
//
//----------------------------------- ScriptImpl -------------------------------------------
//
const wchar_t* ScriptImpl::Description() const
{
   return (flags & ScriptDoc::Exported) ? L"отправлен" : L"";
}

void ScriptImpl::EditDocument(UINT retForm)
{
   //OpenScript(this, (retForm != IDD_ORDER_LIST));
   ShowScriptList(this, (retForm != IDD_ORDER_LIST));
}

bool ScriptImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   //if( rid == NO_ROWID ) return false;
   if( clearCompleete )
   {
      DocDataList ddl;
      LoadDocuments(&ddl, false);
      ddl.ClearDirty(reverse);
      ddl.RemoveDocuments();
   }

   clearCompleete = false;

   if( reverse )
   {
      if( flags & ScriptDoc::Exported ) flags &= (~ScriptDoc::Exported);
      else flags |= ScriptDoc::Exported;
   } else
      flags |= ScriptDoc::Exported;
   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

void ScriptImpl::LoadDocuments(DocDataList* list, bool withScript)
{
   SendDocData sdd;
   if( withScript )
   {
      ScriptImpl* si = new ScriptImpl();
      si->Read(RID());
      sdd.document = si;
      sdd.docType = docTypeManager.GetDocType(dtScript);
      list->push_back(sdd);
   }

   if( flags & ScriptDoc::TaskAfterDone )
   {
      AgentTaskImpl::AddDocs(list, id, date, false);
      AgentTaskImpl::AddDocs(list, id, date, true);
   }

   IDocument *d;
   const DocType* dt = docTypeManager.GetDocType(dtVisit);
   if( dt )
   {
      d = dt->FindDocument(id, date);
      if( d )
      {
         sdd.document = d;
         sdd.docType = dt;
         list->push_back(sdd);
      }

      d = dt->FindDocument(id, dateEnd);
      if( d )
      {
         sdd.document = d;
         sdd.docType = dt;
         list->push_back(sdd);
      }
   }

   vector_t<ScriptDocItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      dt = docTypeManager.FindFromDataType(i->type);
      if( dt )
      {
         d = dt->FindDocument(id, i->date);
         if( d )
         {
            sdd.document = d;
            sdd.docType = dt;
            list->push_back(sdd);
         }
      }
   }

   dt = docTypeManager.GetDocType(dtRemnants);
   if( dt ) // load remnants
   {
      OrgRemnantsImpl* ri = new OrgRemnantsImpl();
      if( ri->Load(id, date) )
      {
         sdd.document = (IDocument*)ri;
         sdd.docType = dt;
         list->push_back(sdd);
      } else
         delete ri;
   }

}

bool ScriptImpl::Init(const ROWID &orgID)
{
#ifdef GPS_POS
   if( !CheckGPSPos(L"Получить координаты?") )
      return false;

   latitude = gCurrentGPSPos.latitude;
   longitude = gCurrentGPSPos.longitude;
#endif

   OrgImpl org;
   org.Read(orgID);
   id = holder.Add(org.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   st.wMilliseconds = 0;

   SystemTimeToFileTime(&st, &date);
   *(__int64*)&dateEnd = *(__int64*)&date + (__int64)1000000; 

   //if( !Read() )
   //{
   //   flags = 0;
   //}
   return true;
}

bool ScriptImpl::CreateDocument(const ROWID &orgID)
{
   bool inited = false;
   if( *(__int64*)&lastScriptDoc != 0 )
   {
      OrgImpl o;
      o.Read(orgID);
      id = o.id;
      date = lastScriptDoc;
      inited = Read();
      *(__int64*)&lastScriptDoc = 0;
   }

   if( !inited && !Init(orgID) )
      return false;

   ShowScriptList(this, /*ScriptData::lastRetToDoc*/true);
   return true;
}

bool ScriptImpl::RemoveDocument()
{
   DocDataList ddl;
   LoadDocuments(&ddl, false);

   bool ret = Remove();
   if( ret )
   {
      DocDataList::iterator i = ddl.begin();
      for( ; i != ddl.end(); i++ )
      {
         if( i->document )
         {
            ICreatableDocument *cd = i->document->Creatable();
            if( cd )
               cd->RemoveDocument();
         }
      }
   }
   ddl.RemoveDocuments();
   return ret;
}

bool ScriptImpl::CanRemove() const
{
   return MessageBox(GetActiveWindow(), L"Удалить посещение?", L"Подтверждение", MB_YESNO|MB_ICONQUESTION) == IDYES;
}

bool ScriptImpl::IsDirty() const
{
   return ( (flags & ScriptDoc::Exported) == 0 );
   // или если документы outOfplan или все сделано
   //return ( (flags & (ScriptDoc::OrderOutOfPlan | ScriptDoc::IncassOutOfPlan)) != 0 || 
   //         (flags & (ScriptDoc::TaskBeforeDone | ScriptDoc::TaskBeforeDone | ScriptDoc::PhotoBefore | ScriptDoc::PhotoAfter)) == 
   //            (ScriptDoc::TaskBeforeDone | ScriptDoc::TaskBeforeDone | ScriptDoc::PhotoBefore | ScriptDoc::PhotoAfter) );
}

DWORD ScriptImpl::Sum() const
{
   return sum;
}

IDocument* ScriptImpl::GetDocument(const DocType* type)
{
   IDocument *d = type->CreateDocument();
   const wchar_t* docType = d->Data()->GetType().Name();
   type->FreeDocument(d);
   d = NULL;

   vector_t<ScriptDocItem>::iterator i = items.begin();

   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->type, docType) == 0 )
      {
         d = type->FindDocument(id, i->date);
         if( d == NULL )
         {
            items.erase(i);
            Write();
         }
         break;
      }
   }


   return d;
}

void ScriptImpl::AddDocument(IDocument* doc)
{
   const wchar_t* docType = doc->Data()->GetType().Name();
   ICreatableDocument* cd = doc->Creatable();
   if( cd != NULL )
   {
      FILETIME ft = cd->UID();

      vector_t<ScriptDocItem>::iterator i = items.begin();

      bool writed = false;
      for( ; i != items.end(); i++ )
      {
         if( wcscmp(i->type, docType) == 0 )
         {
            if( CompareFileTime(&i->date, &ft) != 0 )
            {
               i->date = ft;
               Write();
            }
            writed = true;
            break;
         }
      }

      if( !writed )
      {
         ScriptDocItem di;
         di.type = holder.Add(docType);
         di.date = ft;

         items.push_back(di);
         Write();
      }
   }
}

void ScriptImpl::RemoveDocument(const DocType* type)
{
   IDocument *d = type->CreateDocument();
   const wchar_t* docType = d->Data()->GetType().Name();
   type->FreeDocument(d);

   vector_t<ScriptDocItem>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->type, docType) == 0 )
      {
         items.erase(i);

         Write();

         if( type->Type() == dtOrder )
            docTypeManager.SumChanged(dtScript, id);
         break;
      }
   }
}

bool ScriptImpl::IsComplete() const
{
   bool check = ((flags & (ScriptDoc::TaskBeforeDone | ScriptDoc::TaskAfterDone | ScriptDoc::PhotoBefore | ScriptDoc::PhotoAfter)) == 
      (ScriptDoc::TaskBeforeDone | ScriptDoc::TaskAfterDone | ScriptDoc::PhotoBefore | ScriptDoc::PhotoAfter));

   if( check )
   {
      check = false;

      const DocType* dt = docTypeManager.GetDocType(dtVisit);
      IDocument *d = dt->FindDocument(id, dateEnd);
      if( d )
      {
         if( ((VisitImpl*)d)->items.size() > 0 )
            check = true;;
         dt->FreeDocument(d);
      }
   }

   return check;
}

//
//-------------------------------------------------- ScriptPage ----------------------------------------------
//
ScriptPage::ScriptPage(const wchar_t* type, ScriptImpl *script, ScriptForm *parent, UINT retForm) : form(NULL), document(NULL)
{
   this->parent = parent;
   this->retForm = retForm;
   this->script = script;

   if( type == NULL )
      docType = NULL;
   else
   {
      docType = docTypeManager.GetDocType(type);
      document = script->GetDocument(docType);
   }
}

ScriptPage::~ScriptPage()
{
   delete form;
   if( docType != NULL && document )
      docType->FreeDocument(document);
}

bool ScriptPage::Activate(bool activate, bool writeChanges)
{
   bool ret = true;
   if( !activate )
   {
      if( form != NULL && form->m_hWnd )
      {
         if( writeChanges )
            form->WriteChanges();

         BindDocToScript();

         form->ShowWindow(SW_HIDE);
         form->Destroy();
         form = NULL;
         document = NULL; // документ должен разрушаться редактируемой формой
      }
   } else
   {
      if( docType == NULL )
      {
         OpenScriptDetail(script, (retForm != IDD_ORDER_LIST));
      } else
      {
         if( document == NULL )
            document = script->GetDocument(docType);

         if( document != NULL )
         {
            document->EditDocument(retForm);
         } else
            ret = false;
      }
   }

   return ret;
}

void ScriptPage::SetForm(BaseForm* form)
{
   if( this->form != NULL )
   {
      this->form->ShowWindow(SW_HIDE);
      SendMessage(parent->m_hWnd, WM_DESTROY_FORM, (WPARAM)this->form, NULL);
   }

   this->form = form;
   if( form != NULL )
      form->SetWindowPos(HWND_TOP, 0, 0, 0, 0, SWP_NOSIZE | SWP_NOMOVE);
}

void ScriptPage::BindDocToScript()
{
   if( docType != NULL )
   {
      if( document && document->RowID() != NO_ROWID )
         script->AddDocument(document);
      else
         script->RemoveDocument(docType);

      if( docType->Type() == dtOrder )
         docTypeManager.SumChanged(dtScript, script->id);
   }
}

bool ScriptPage::AddDoc()
{
   if( docType != NULL && document == NULL )
   {
      OrgImpl oi;
      oi.id = script->id;
      oi.Read();

      document = docType->CreateDocument();
      ICreatableDocument *cd = document->Creatable();

      if( !cd->Init(oi.rid) )
      {
         docType->FreeDocument(document);
         document = NULL;
      } else
         document->EditDocument(retForm);
   }

   return true;
}

bool ScriptPage::RemoveDoc()
{
   return true;
}

//
//-------------------------------------------------- ScriptDetailForm ----------------------------------------------
//

IMPLEMENT_FORM(ScriptDetailForm)

ScriptDetailForm::ScriptDetailForm() : data(NULL), numInput(IDC_SUM)
{
}

ScriptDetailForm::~ScriptDetailForm()
{
   delete data;
}

bool ScriptDetailForm::SetData(IFormData *_data)
{
   data = (ScriptDetailData*)_data;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   wchar_t buf[20], dest[20];
   DWORD sum = data->script->sum;
   ConvertScaling(buf, sum, SUM_SCALE);
   FormatScaling(buf, dest, sizeof(dest)/sizeof(dest[0]), sum % SUM_SCALE, SUM_SCALE, false);

   CEdit e(GetDlgItem(IDC_SUM));
   e.SetWindowText(dest);
   e.SetSel(0, -1);

   if( !data->script->IsDirty() )
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);

   return true;
}

void ScriptDetailForm::UpdateLayout(bool forceRecalc)
{
}

void ScriptDetailForm::WriteChanges()
{
   if( data->script->IsDirty() )
   {
      wchar_t buf[50];

      GetDlgItem(IDC_SUM).GetWindowText(buf, sizeof(buf)/sizeof(buf[0]));
      DWORD sum = GetValue(buf, SUM_SCALE);

      if( data->script->sum != sum && MessageBox(L"Сохранить изменения?", L"Вопрос", MB_YESNO) == IDYES )
      {
         data->script->sum = sum;
         data->script->Write();
      }
   }
}

LRESULT ScriptDetailForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   WriteChanges();

   if( data->retToDocList )
      OpenOrgDocs(data->script->id, dtScript);
   else
      OpenListDoc(dtScript);

   return 0;
}

LRESULT ScriptDetailForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   WriteChanges();

   if( SendDocument(data->script, docTypeManager.GetDocType(dtScript), L"Инкасация отправлена") )
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      data->script->Write();
   }
   return 0;
}

//
//-------------------------------------------------- ScriptForm ----------------------------------------------
//
IMPLEMENT_FORM(ScriptForm)

ScriptForm::ScriptForm() : data(NULL), activePage(NULL), tabFont(NULL)
{
   mainFrame = _Module.GetFrame();
   _Module.SetFrame(this);
}

ScriptForm::~ScriptForm()
{
   if( tabFont != NULL )
      DeleteObject(tabFont);

   _Module.SetFrame(mainFrame);

   activePage = NULL;

   std::vector<ScriptPage*>::iterator i = pages.begin();
   for( ; i != pages.end(); i++ )
      delete (*i);

   delete data;
}

LRESULT ScriptForm::SelChange(LPNMHDR hdr)
{
   ScriptPage* newPage = NULL;
   int cs = tabs.GetCurSel();
   if( cs >= 0 )
   {
      newPage = pages[cs];
   }

   if( activePage != newPage )
   {
      textWnd.ShowWindow(SW_HIDE);

      if( activePage != NULL )
         activePage->Activate(false, true);

      activePage = newPage;
      if( !activePage->Activate(true, false) )
      {
         textWnd.ShowWindow(SW_SHOW);
         LoadMenuBar(false);
      }
   }
   return 0;
}

LRESULT ScriptForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( activePage != NULL )
      activePage->Activate(false, true);

   if( data->retToDocList )
      OpenOrgDocs(data->script->id, dtScript);
   else
      OpenListDoc(dtScript);

   return 0;
}

LRESULT ScriptForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   WriteChanges();

   if( SendDocument(data->script, docTypeManager.GetDocType(dtScript), L"Визит отправлен") )
   {
      EnumChildWindows(m_hWnd, DisableChildsProc, NULL);
      data->script->Write();
   }
   return 0;
}

LRESULT ScriptForm::AddDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int sel = tabs.GetCurSel();
   if( sel >= 0 )
      pages[sel]->AddDoc();

   return 0;
}

LRESULT ScriptForm::DelDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int sel = tabs.GetCurSel();
   if( sel >= 0 )
      pages[sel]->RemoveDoc();

   return 0;
}

LRESULT ScriptForm::DestroyForm(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   if( wParam != 0 )
      ((IForm*)wParam)->Destroy();
   return 0;
}

LRESULT ScriptForm::OpenForm(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   if( activePage != NULL )
      activePage->Activate(false, false);

   _Module.SetFrame(mainFrame);

   if( wParam == IDD_ORDER_LIST )
      OpenListDoc(dtScript);
   else
      OpenOrgDocs(this->data->script->id, dtScript);

   return 0;
}

bool ScriptForm::Load(DWORD formID, IFormData *data)
{
   if( formID == IDD_ORG_DOCS || formID == IDD_ORDER_LIST )
   {
      delete data;

      PostMessage(WM_OPEN_EXIT_FORM, formID, 0);
      return true;
   }

   int curSel = tabs.GetCurSel();
   BaseForm *form = NULL;

   if( curSel >= 0 )
   {
      FormCreator creator = GetFormCreator(formID);
      if( creator != NULL )
      {
         form = (BaseForm*)creator();
         if( form != NULL )
         {
            if( form->Load(pageParent.m_hWnd) )
            {
               pages[curSel]->SetForm(form);

               form->SetData(data);
               form->ShowWindow(SW_SHOW);
               form->UpdateLayout(true);
            } else
            {
               delete form;
               form = NULL;
            }
         }
      }
   }

   return (form != NULL);
}

void ScriptForm::AddType(const wchar_t *type, const wchar_t *title)
{
   ScriptPage *pg = new ScriptPage(type, data->script, this,
      (data->retToDocList) ? IDD_ORG_DOCS : IDD_ORDER_LIST );

   TCITEM item = {0};
   item.mask = TCIF_IMAGE | TCIF_TEXT;
   item.pszText = (wchar_t*)title;
   item.iImage = -1; //(data->script->IsDirty() == false) ? -1 : (pg->HaveDocument()) ? 1 : 0;

   tabs.InsertItem(tabs.GetItemCount(), &item);
   pages.push_back(pg);
}

void ScriptForm::UpdateLayout(bool forceRecalc)
{
   if( tabs.m_hWnd )
   {
      CRect rc;
      GetClientRect(rc);
      tabs.AdjustRect(FALSE, rc);
      pageParent.MoveWindow(rc);

      if( activePage )
         activePage->UpdateLayout(forceRecalc);
   }
}

void ScriptForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));
}

bool ScriptForm::SetData(IFormData *_data)
{
   data = (ScriptData*)_data;

   LoadMenuBar(false);

   CRect rc;
   GetClientRect(rc);
   tabs.Create(m_hWnd, rc, NULL, TCS_FOCUSNEVER | TCS_BOTTOM | WS_CHILD | WS_CLIPSIBLINGS | WS_VISIBLE);

   tabFont = (HFONT)GetStockObject(SYSTEM_FONT);
   LOGFONT lf;
   GetObject(tabFont, sizeof(lf), &lf);
   lf.lfHeight = -11;
   tabFont = CreateFontIndirect(&lf);
   SendMessage(tabs.m_hWnd, WM_SETFONT, (WPARAM)tabFont, MAKELPARAM(FALSE, 0));

   //SIZE pad = { 2, 3 };
   //tabs.SetPadding(pad);
   HBITMAP bmp;
   CImageList il;
   ATL::_U_STRINGorID bid(IDD_SCRIPT);
   il.Create(bid, 16, 1, RGB(192, 192, 192));
   bmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDD_SCRIPT));
   il.Add(bmp);
   tabs.SetImageList(il);
   DeleteObject((HGDIOBJ)bmp);

   tabs.SetImageList(il);
   DeleteObject((HGDIOBJ)bmp);

   AddType(dtVisit, L"Визит");
   AddType(dtRemnants, L"Остатки");
   AddType(NULL, L"Инкасс.");
   AddType(dtOrder, L"Заявка");

   tabs.AdjustRect(FALSE, rc);
   pageParent.Create(tabs.m_hWnd, rc, NULL, WS_CHILD | WS_CLIPSIBLINGS | WS_VISIBLE);
   rc.left += 4;
   rc.right -= 4;
   rc.top = 30;
   rc.bottom = rc.top + 50;
   textWnd.Create(pageParent, rc, L"Для создания документа\nНажмите кнопку 'Новый'", SS_CENTER | WS_CHILD | WS_CLIPSIBLINGS | WS_VISIBLE);

   activePage = pages[0];
   if( !activePage->Activate(true, false) )
   {
      textWnd.ShowWindow(SW_SHOW);
   }

   UpdateLayout(true);

   return true;
}

void OpenScriptDetail(ScriptImpl *script, bool retToDocList)
{
   ScriptDetailData *data = new ScriptDetailData(script, retToDocList);
   _Module.GetFrame()->Load(IDD_SCRIPT_FORM, data);
}

void OpenScript(ScriptImpl *script, bool retToDocList)
{
   ScriptData *data = new ScriptData(script, retToDocList);
   _Module.GetFrame()->Load(IDD_SCRIPT, data);
}

void ShowScriptList(ScriptImpl *script, bool retToDocList)
{
   ScriptData *data = new ScriptData(script, retToDocList);
   _Module.GetFrame()->Load(IDD_SCRIPT_LIST, data);
}