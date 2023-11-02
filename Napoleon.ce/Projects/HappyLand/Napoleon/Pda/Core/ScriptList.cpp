/*
 * Copyright (C), 2006-2010, Денис Мосягин
 *
 * Документ сценария
 *
 *  ert   17/12/2010   creating
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
#include "AgentTask.h"
#include "Incass.h"
#include <PicWindow.h>

#include <SumLabel.h>

class ScriptListForm : public BaseForm
{
public:
   ScriptListForm();
   ~ScriptListForm();

   //------------------ IFrame -----------------

   virtual DWORD GetResourceID() const { return IDD_SCRIPT_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_SCRIPT_LIST; }

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   void LoadMenuBar(bool hideSIP);

   DECLARE_FORM(ScriptListForm, IDD_SCRIPT_LIST)

   BEGIN_MSG_MAP(ScriptListForm)
      MESSAGE_HANDLER(WM_COMMAND, OnCommand)
      MESSAGE_HANDLER(WM_MEASUREITEM, MeasureItem)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      COMMAND_HANDLER(IDD_SCRIPT_LIST, LBN_SELCHANGE, SelectDoc)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   LRESULT MeasureItem(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT SelectDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnCommand(WORD msg, WPARAM, LPARAM, BOOL &bHandled);

   void MakePhoto(bool start);

   void AddIncass()
   {
      if( AddDocument(dtIncass) )
      {
         if( (data->script->flags & ScriptDoc::TaskBeforeDone) == 0 )
         {
            data->script->flags |= ScriptDoc::IncassOutOfPlan;
            data->script->Write();
         }
      }
   }

   bool SetCause();
   
   void AddOrder()
   {
      if( (data->script->flags & (ScriptDoc::TaskBeforeDone | ScriptDoc::OrderOutOfPlan)) == 0 )
      {
         if( SetCause() == false )
            return;
      }

      if( AddDocument(dtOrder) )
      {
         if( (data->script->flags & ScriptDoc::TaskBeforeDone) == 0 )
            data->script->flags |= ScriptDoc::OrderOutOfPlan;
         else
            data->script->flags |= ScriptDoc::OrderWasCreated;

         data->script->Write();
      }
   }

   bool AddDocument(const wchar_t* type);
   IDocument* CreateDocument(const wchar_t* type);

   ScriptData *data;
   CMenuBarCtrl menuBar;
   TaskList list;
   PicWindow *picWindow;
   SumLabel sumLabel;
};

enum States { sTaskBefore, sPhotoStart, sIncass, sOrder, sTaskAfter, sPhotoEnd };
TaskList::Item tdd[] =
{
   { L"Задачи", IDD_TASK_BMP, TaskList::Item::Checked },
   { L"Фото. Начало", IDD_TASK_CAMERA, TaskList::Item::Checked },
   { L"Инкассация", IDD_TASK_INKAS, 0 },
   { L"Остатки/Заявка", IDD_TASK_REST, TaskList::Item::Disabled },
   //{ L"Заявка", IDD_TASK_ORDER, TaskList::Item::Disabled },
   { L"Отчет по задачам", IDD_TASK_BMP, TaskList::Item::Disabled },
   { L"Фото. Окончание", IDD_TASK_CAMERA, TaskList::Item::Disabled },
};

//
//-------------------------------------------------- ScriptListForm ----------------------------------------------
//
IMPLEMENT_FORM(ScriptListForm)

ScriptListForm::ScriptListForm() : data(NULL), list(IDD_SCRIPT_LIST), picWindow(NULL)
{
}

ScriptListForm::~ScriptListForm()
{
   delete data;
}

LRESULT ScriptListForm::OnCommand(WORD msg, WPARAM wParam, LPARAM lParam, BOOL &bHandled)
{
   if( picWindow && (HWND)lParam != list.m_hWnd )
   {
      picWindow->Cancel();
      delete picWindow;
      picWindow = NULL;
   } else
      bHandled = FALSE;
   return 0;
}

bool ScriptListForm::SetData(IFormData *_data)
{
   data = (ScriptData*)_data;
   LoadMenuBar(false);

   list.SubclassWindow(GetDlgItem(IDD_SCRIPT_LIST));

   int index = 0;
   for( ; index < sizeof(tdd) / sizeof(tdd[0]); index++ )
   {
      tdd[index].flags = TaskList::Item::Disabled;
      list.AddString((LPCWSTR)&tdd[index]);
   }

   DWORD flags = data->script->flags;
   if( flags == 0 )
   {
      tdd[sTaskBefore].flags = 0;
      tdd[sIncass].flags = 0;
      tdd[sOrder].flags = 0;
   } else if( (flags & ScriptDoc::IncassOutOfPlan) )
   {
      tdd[sIncass].flags = (data->script->items.size()) ? TaskList::Item::Checked : 0;
   } else if( (flags & ScriptDoc::OrderOutOfPlan) )
   {
      tdd[sOrder].flags = (data->script->items.size()) ? TaskList::Item::Checked : 0;
   } else
   {
      int curTask = sTaskBefore;
      if( (flags & ScriptDoc::TaskBeforeDone) )
         tdd[curTask++].flags = TaskList::Item::Checked;

      bool canCheck = true;
      int docs = data->script->items.size();
      //
      // bug 90 Необходимо, чтобы без создания фотографии в посещениях движение дальше было невозможно.
      //
      const DocType* dt = docTypeManager.GetDocType(dtVisit);
      if( (flags & ScriptDoc::PhotoBefore) )
      {
         IDocument *d = dt->FindDocument(data->script->id, data->script->date);
         if( d )
         {
            if( ((VisitImpl*)d)->items.size() > 0 )
               tdd[curTask++].flags = TaskList::Item::Checked;
            else
               canCheck = false;
            dt->FreeDocument(d);
         }
         else
            canCheck = false;
         if( !canCheck )
            flags &= (~ScriptDoc::PhotoBefore);
      }
      if( canCheck )
      {
         if( docs >= 1 ) // Incass
            tdd[curTask++].flags = TaskList::Item::Checked;
         if( docs >= 2 || ((flags & ScriptDoc::OrderWasCreated) != 0) ) // Order & Rest || Visit ????
            tdd[curTask++].flags = TaskList::Item::Checked;
         if( (flags & ScriptDoc::TaskAfterDone) )
            tdd[curTask++].flags = TaskList::Item::Checked;
         if( (flags & ScriptDoc::PhotoAfter) ) // PhotoEnd
         {
            IDocument *d = dt->FindDocument(data->script->id, data->script->dateEnd);
            if( d )
            {
               if( ((VisitImpl*)d)->items.size() > 0 )
                  tdd[curTask++].flags = TaskList::Item::Checked;
               else
                  flags &= (~ScriptDoc::PhotoAfter);
               dt->FreeDocument(d);
            }
         }
      }
      if( curTask < sizeof(tdd)/sizeof(tdd[0]) && ((data->script->flags & ScriptDoc::Interrupted) == 0) )
         tdd[curTask].flags = 0;
   }

   UpdateLayout(false);

   sumLabel.CreateLabel(menuBar.m_hWnd, SumLabel::STD_WIDTH, GetSystemMetrics(SM_CXSMICON) * 9 / 4);
   sumLabel.SetSum(data->script->sum);
   return true;
}

LRESULT ScriptListForm::SelectDoc(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   int cs = list.GetCurSel();
   if( cs < sizeof(tdd) / sizeof(tdd[0]) && tdd[cs].flags != TaskList::Item::Disabled )
   {
      lastScriptDoc = data->script->date;
      SetNextCreatedDoc(dtScript);

      OrgImpl o;
      o.id = data->script->id;
      o.Read();

      switch( (States)cs )
      {
      case sTaskBefore:
         data->script->flags |= ScriptDoc::TaskBeforeDone;
         OpenAgentTask(data->script->date, data->script->id, docTypeManager.GetDocType(dtScript), false, 
            ((data->script->flags & (ScriptDoc::Interrupted | ScriptDoc::PhotoAfter)) == 0));
         break;
      case sPhotoStart:
         MakePhoto(true);
         break;
      case sIncass:
         AddIncass();
         break;
      case sOrder:
         AddOrder();
         break;
      case sTaskAfter:
         data->script->flags |= ScriptDoc::TaskAfterDone;
         OpenAgentTask(data->script->date, data->script->id, docTypeManager.GetDocType(dtScript), data->script->IsDirty(),
            ((data->script->flags & (ScriptDoc::Interrupted | ScriptDoc::PhotoAfter)) == 0));
         break;
      case sPhotoEnd:
         MakePhoto(false);
         break;
      }

      if( (data->script->flags & ScriptDoc::PhotoAfter) == 0 ) // поставим дату конца
      {
         SYSTEMTIME st;
         GetLocalTime(&st);
         SystemTimeToFileTime(&st, &data->script->dateEnd);
      }

      data->script->Write();
      list.SetCurSel(-1);
   }
   return 0;
}

void ScriptListForm::MakePhoto(bool first)
{
   ScriptImpl* s = data->script;
   //if( (s->flags & ScriptDoc::Exported) != 0 )
   //   return;

   const DocType* dt = docTypeManager.GetDocType(dtVisit);
   FILETIME date = (first) ? s->date : s->dateEnd;
   IDocument *d = dt->FindDocument(s->id, date);
   if( d == NULL && (s->flags & ScriptDoc::Interrupted) == 0 )
   {
      if( (first & ((s->flags & ScriptDoc::PhotoBefore) == 0)) || (!first & ((s->flags & ScriptDoc::PhotoAfter) == 0)) )
      {
         d = CreateDocument(dtVisit);
         if( d )
         {
            if( first )
            {
               s->flags |= ScriptDoc::PhotoBefore;
               s->date = d->Date();
            }
            else
            {
               s->flags |= ScriptDoc::PhotoAfter;
               s->dateEnd = d->Date();
            }
            s->Write();
         }
      }
   }

   if( d )
   {
      lastScriptDoc = data->script->date;
      SetNextCreatedDoc(dtScript);
      ScriptData::lastRetToDoc = data->retToDocList;

      d->EditDocument(IDD_ORG_DOCS);
   }
}

IDocument* ScriptListForm::CreateDocument(const wchar_t* type)
{
   const DocType* dt = docTypeManager.GetDocType(type);
   OrgImpl o;
   o.id = (wchar_t*)data->script->id;
   o.Read();

   IDocument* d = dt->CreateDocument();
   if( d != NULL )
   {
      ICreatableDocument *cd = d->Creatable();
      if( !cd->Init(o.RID()) )
      {
         dt->FreeDocument(d);
         delete d;
         d = NULL;
      } else
         cd->WriteDocument();
   }

   return d;
}

class CauseDialog : public CSimpleDialog<IDC_CAUSE, TRUE>
{
public:
   CauseDialog() {}

   std::wstring remark;

   typedef CSimpleDialog<IDC_CAUSE, TRUE> BaseClass;

   BEGIN_MSG_MAP(CauseDialog)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_ID_HANDLER(IDOK, Close)
      CHAIN_MSG_MAP(BaseClass)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT , WPARAM , LPARAM , BOOL& bHandled)
   {
      CRect rc, r1;
      GetClientRect(rc);

      CWindow b(GetDlgItem(IDOK));
      b.GetWindowRect(r1);
      ScreenToClient(r1);

      r1.right = r1.Width() + 2;
      r1.left = 2;
      r1.top = rc.Height() - 2 - r1.Height();
      r1.bottom = rc.Height() - 2;
      b.MoveWindow(r1);

      b = GetDlgItem(IDCANCEL);
      r1.left = rc.Width() - 2 - r1.Width();
      r1.right = rc.Width() - 2;
      b.MoveWindow(r1);

      CWindow text(GetDlgItem(IDC_CAUSE));
      r1.bottom = r1.top - 2;
      r1.top = rc.top - 2;
      r1.left = rc.left + 2;
      r1.right = rc.right - 2;
      text.MoveWindow(r1);

      bHandled = FALSE;
      return 0;
   }

   LRESULT Close(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
   {
      CWindow edit(GetDlgItem(IDC_CAUSE));
      int len = edit.GetWindowTextLength() + 1;
      wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
      edit.GetWindowText(buf, len);

      remark.assign(buf, len-1);

      bHandled = FALSE;
      return 0;
   }
};

bool ScriptListForm::SetCause()
{
   bool ret = false;
   CauseDialog cd;
   if( cd.DoModal() == IDOK && cd.remark.empty() == false )
   {
      data->script->remark = data->script->holder.Add(cd.remark.c_str());
      ret = true;
   }
   return ret;
}

bool ScriptListForm::AddDocument(const wchar_t* type)
{
   const DocType* dt = docTypeManager.GetDocType(type);
   IDocument* d = data->script->GetDocument(dt);

   OrgImpl o;
   o.id = (wchar_t*)data->script->id;
   o.Read();

   if( d == NULL )
   {
      d = CreateDocument(type);
      if( d  == NULL )
         return false;

      data->script->AddDocument(d);
   }

   lastScriptDoc = data->script->date;
   SetNextCreatedDoc(dtScript);
   ScriptData::lastRetToDoc = data->retToDocList;

   d->EditDocument(IDD_ORG_DOCS);
   return true;
}

void ScriptListForm::UpdateLayout(bool forceRecalc)
{
   list.UpdateLayout();
}

void ScriptListForm::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0,  (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));

   BOOL v = ((data->script->flags & 
      (ScriptDoc::IncassOutOfPlan | ScriptDoc::OrderOutOfPlan | ScriptDoc::Interrupted | ScriptDoc::PhotoAfter)) != 0) ? TRUE : FALSE;
   menuBar.EnableButton(IDC_SEND, v);
}

LRESULT ScriptListForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   DocDataList dl;
   data->script->LoadDocuments(&dl);
   SendDocuments(&dl, L"Данные отправлены");
   dl.RemoveDocuments();
   return 0;
}

LRESULT ScriptListForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( !data->script->IsComplete() && ((data->script->flags & ScriptDoc::TaskBeforeDone) != 0) && ((data->script->flags & ScriptDoc::Interrupted) == 0) )
   {
      if( MessageBox(L"Вы хотите прервать сценарий?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDNO )
         return 0;

      data->script->flags |= ScriptDoc::Interrupted;
      SYSTEMTIME st;
      GetLocalTime(&st);
      SystemTimeToFileTime(&st, &data->script->dateEnd);
      data->script->Write();
   }
   if( data->retToDocList )
      OpenOrgDocs(data->script->id, dtScript);
   else
      OpenListDoc(dtScript);
   return 0;
}

LRESULT ScriptListForm::MeasureItem(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled)
{
   MEASUREITEMSTRUCT *ms = (MEASUREITEMSTRUCT*)lParam;
   if( ms->CtlID == IDD_SCRIPT_LIST )
   {  
      ms->itemHeight = (GetSystemMetrics(SM_CXSMICON) == 16) ? 38 : 76;
   }
   return 0;
}

ScriptData::ScriptData(ScriptImpl *script, bool rdl) : retToDocList(rdl)
{
   this->script = script;
   const DocType* dt = docTypeManager.GetDocType(dtOrder);
   IDocument* d = script->GetDocument(dt);
   DWORD checkSum = 0;
   if( d != NULL )
   {
      checkSum = d->Sum();
      dt->FreeDocument(d);
   }

   if( script->sum != checkSum )
   {
      script->sum = checkSum;
      script->Write();
      docTypeManager.SumChanged(dtScript, script->id);
   }
}
