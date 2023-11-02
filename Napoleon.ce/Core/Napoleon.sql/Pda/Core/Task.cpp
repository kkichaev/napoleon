/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Автоптека add-in
 *
 *  ert   14/09/2009   creating
 */
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>
#include <atlmisc.h>
#include <atlscrl.h>
#include <BaseForm.h>
#include <NapoleonRes.h>
#include <DocType.h>
#include <InitDoc.h>
#include "ObjImpl.h"
#include "Task.h"
#include <StdFuncs.h>
#include <FormEntries.h>

wchar_t dtTask[] = L"Задачи";

typedef std::vector<TaskImpl*> TaskList;
void OpenTask(const TaskList& t, bool openOrgDocs);

struct TaskFactory : public IDocFactory
{
   virtual IDocument* Create() const { return new TaskImpl(); }
   virtual void Free(IDocument* document) const { delete (TaskImpl*)document; }
} taskFactory;

struct TaskData : public IFormData
{
   TaskData(const TaskList& t, bool openOD) : task(t), openOrgDocs(openOD), taskClosed(NULL) {}
   TaskData(TaskImpl* t, TaskImpl::TaskClosed taskClosed);

   ~TaskData()
   {
      TaskList::iterator i = task.begin();
      for( ; i != task.end(); i++ )
         delete (*i);
   }

   void SaveData(TaskList::iterator current);
   void MarkSended(TaskList::iterator current);

   TaskList task;
   bool openOrgDocs;
   TaskImpl::TaskClosed taskClosed;
};

class TaskForm : public BaseForm
{
public:
   TaskForm() : data(NULL) {}
   ~TaskForm() { delete data; }

   DECLARE_FORM(TaskForm, IDC_TASK)

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDC_TASK; }
   virtual DWORD GetMenuBarID() const { return IDC_TASK; }

   BEGIN_MSG_MAP(TaskForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_NEXT, Nav)
      COMMAND_ID_HANDLER(IDC_PREV, Nav)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)

      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   void UpdateLayout();
   void LoadMenuBar();
   void UpdateNavButtons();
   void SetTaskData(const TaskImpl& task);
   bool UpdateCurrentData();

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Nav(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   TaskData *data;
   TaskList::iterator current;
   CMenuBarCtrl menuBar;
};

const wchar_t* TaskImpl::Description() const
{
   return L"";
}

void TaskImpl::EditDocument(UINT retForm)
{
   TaskList t;
   t.push_back(this);
   OpenTask(t, false);
}

bool TaskImpl::Init(const ROWID &orgID)
{
   return false;
}

bool TaskImpl::CanRemove() const
{
   return false;
}

const ROWID& TaskImpl::Serialize(StreamWriter* writer) const
{ 
   Task tt(*this);
   tt.task = L"";
   GetType().Serialize(writer, tt);
   return rid;
}

bool TaskImpl::ClearDirty(SQLTable *table, bool reverse)
{
   //if(rid == NO_ROWID) return false;

   if( !reverse )
      flags |= ofExported;
   else
   {
      if( flags & ofExported ) flags &= (~ofExported);
      else flags |= ofExported;
   }
   return (table != NULL) ? Update(L"flags") : true;
}

TaskData::TaskData(TaskImpl* t, TaskImpl::TaskClosed taskClosed)
{
   task.push_back(t);
   this->taskClosed = taskClosed;
}

void TaskData::SaveData(TaskList::iterator current)
{
   (*current)->Write();
}

static void MakeStmt(std::wstring *tstr, const wchar_t *id)
{
   tstr->assign(L"WHERE (id='");
   tstr->append(id);
   tstr->append(L"') AND (flags & 1 = 0) ORDER BY date");
}

struct TaskAdd : public TaskImpl
{
   ROWID rowid;

   DECLARE_TYPE_REFLECTION(TaskAdd);
};

BEGIN_TYPE_REFLECTION(TaskAdd)
   REGISTER_INT64_MEMBER(TaskAdd, rowid)
   CHAIN_REFLECTION(TaskAdd, Task)
END_TYPE_REFLECTION(TaskAdd)

void TaskImpl::EditTask(const wchar_t *id, bool openOrgDocs)
{
   TaskAdd tsk;
   SQLTable tbl(tsk.Name());

   std::wstring tstr;
   MakeStmt(&tstr, id);

   TaskList tl;
   bool bdo = tbl.Select(&tsk, tstr.c_str());
   while( bdo )
   {
      TaskImpl *ti = new TaskImpl();
      tsk.rid = tsk.rowid;
      *ti = tsk;

      ti->UnbindStrings();
      tl.push_back(ti);

      bdo = tbl.SelectNext(&tsk);
   }

   if( tl.size() > 0 )
      OpenTask(tl, openOrgDocs);
}

bool TaskImpl::HaveTask(const wchar_t *id)
{
   TaskImpl tsk;
   SQLTable tbl(tsk.Name());

   std::wstring tstr;
   MakeStmt(&tstr, id);

   return tbl.Select(&tsk, tstr.c_str());
}

IMPLEMENT_FORM(TaskForm)

void TaskData::MarkSended(TaskList::iterator i)
{
   (*i)->flags |= ofExported;
   (*i)->Update(L"flags");
}

bool TaskForm::SetData(IFormData *_data)
{
   data = (TaskData*)_data;

   current = data->task.begin();
   const TaskImpl& task = *(*current);

   SetTaskData(task);

   OrgImpl o;
   o.id = task.id;
   o.Read();
   SetDlgItemText(IDC_ORG_TITLE, o.name);

   LoadMenuBar();

   UpdateLayout();
   return true;
}

void TaskForm::LoadMenuBar()
{
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   if( data->task.size() == 1 )
   {
      menuBar.HideButton(IDC_NEXT);
      menuBar.HideButton(IDC_PREV);
   } else
      UpdateNavButtons();
}

void TaskForm::UpdateLayout()
{
   CRect rc, bounds;
   CStatic title(GetDlgItem(IDC_ORG_TITLE));

   GetClientRect(bounds);
   bounds.right -= 4;

   rc.top = 2;
   rc.bottom = 2;
   rc.left = 2;
   rc.right = bounds.right;
   CalcTextHeight(title.m_hWnd, &rc);
   rc.right = bounds.right;
   title.MoveWindow(rc, FALSE);

   bounds.left = 2;
   bounds.top = rc.bottom + 2;
   int height = bounds.Height() / 2 - 2;

   bounds.bottom = bounds.top + height;
   GetDlgItem(IDC_INFO).MoveWindow(bounds, FALSE);

   bounds.top = bounds.bottom + 2;
   bounds.bottom = bounds.top + height;
   GetDlgItem(IDC_INFO1).MoveWindow(bounds, FALSE);
}

void TaskForm::UpdateNavButtons()
{
   TaskList::iterator i = current;
   i++;
   menuBar.EnableButton(IDC_NEXT, (i == data->task.end()) ? FALSE : TRUE);
   menuBar.EnableButton(IDC_PREV, (current == data->task.begin()) ? FALSE : TRUE);
   //menuBar.EnableButton(IDC_SEND, ((*current)->IsDirty()) ? TRUE : FALSE);
}

bool TaskForm::UpdateCurrentData()
{
   CEdit edit(GetDlgItem(IDC_INFO));
   int len = edit.GetWindowTextLength() + 1;
   wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
   edit.GetWindowText(buf, len);

   if( wcscmp((*current)->doing, buf) )
   {
      (*current)->doing = (*current)->holder.Add(buf);
      data->SaveData(current);

      return true;
   }

   return false;
}

LRESULT TaskForm::Nav(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   UpdateCurrentData();

   if( id == IDC_NEXT )
   {
      TaskList::iterator i = current;
      if( ++i != data->task.end() )
         current = i;
   } else
   {
      if( current != data->task.begin() )
         current--;
   }

   SetTaskData(*(*current));
   UpdateNavButtons();

   return 0;
}

void TaskForm::SetTaskData(const TaskImpl& task)
{
   CWindow info(GetDlgItem(IDC_INFO));
   info.SetWindowText(task.doing);
   info.EnableWindow((task.CanEdit()) ? TRUE : FALSE);

   SetDlgItemText(IDC_INFO1, task.task);
}

LRESULT TaskForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   UpdateCurrentData();

   if( data->taskClosed && data->task.size() )
   {
      data->taskClosed(*data->task.front());
   } else
   {
      if( data->openOrgDocs )
         OpenOrgDocs((*current)->id, dtOrder);
      else
         OpenOrgList(dtOrder);
   }
   return 0;
}

LRESULT TaskForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   UpdateCurrentData();
   //if(  || (*current)->IsDirty() )
   {
      if( SendDocument(*current, docTypeManager.GetDocType(dtTask), L"Задача отправлена") )
      {
         data->MarkSended(current);
         GetDlgItem(IDC_INFO).EnableWindow(FALSE);
         UpdateNavButtons();
      }
   }
   return 0;
}


void OpenTask(const TaskList& t, bool openOrgDocs)
{
   if( t.size() > 0 )
      _Module.GetFrame()->Load(IDC_TASK, new TaskData(t, openOrgDocs));
}

TaskDoc::TaskDoc() : DocType(dtTask, &taskFactory, 0)
{
}

void TaskImpl::EditTask(const ROWID& id, TaskClosed taskClosed)
{
   TaskImpl* t = new TaskImpl();
   t->Read(id);

   _Module.GetFrame()->Load(IDC_TASK, new TaskData(t, taskClosed));
}