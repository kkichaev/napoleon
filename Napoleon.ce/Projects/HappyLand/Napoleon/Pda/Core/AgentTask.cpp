/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Задачи агента
 *
 *  ert   25/10/2010   creating
 */ 
#include "stdafx.h"
#include "AgentTask.h"
#include <ObjImpl.h>
#include <StdFuncs.h>

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <ListForm.h>

#include <DocType.h>
#include <FormEntries.h>
#include <atlcrack.h>

bool OutOfPlan = false;

struct NCOrgTaskItem : public IReflectableData
{
   FILETIME date;
   const wchar_t *text;
   const wchar_t *empty;

   DECLARE_TYPE_REFLECTION(NCOrgTaskItem)
};

class NCOrgTaskData : public ListFormData
{
public:
   NCOrgTaskData(const FILETIME& date, const wchar_t* id, bool canCheck, const DocType* docType, bool hideSend);
   ~NCOrgTaskData();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return NCOrgTaskItem().GetType(); }
   virtual int Count() const { return showed.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index);
   virtual bool Editing(int index);

   bool CheckTask(int index);

   bool IsDone(int index) const;
   bool IsSVTask(int index) const;
   bool CanCheck() const { return canCheck; }
   bool HaveData() const { return (items.size() > 0); }
   bool HideSend() const { return hideSend; }

   const std::vector<std::wstring>& Category() const { return category; }
   const std::wstring& CurrentCategory() const { return category[current]; }
   void Select(int curCat);

   void SetHitFlags(DWORD flags) { hitFlags = flags; }
   void Backing();

   const FILETIME DocDate() const { return checkDate; }
   const wchar_t* OrgID() const { return orgID.c_str(); }

protected:
   void Load(const FILETIME& date, const wchar_t* id);
   const AgentTaskImpl* Item(int index) const;

protected:
   static const DocType* prevDocType;

   StringHolder holder;

   const DocType* docType;
   FILETIME checkDate;
   std::wstring orgID;

   bool hideSend;
   bool canCheck;
   int current;
   DWORD hitFlags;

   std::vector<std::wstring> category;
   std::vector<AgentTaskImpl*> items;
   std::vector<int> showed;
};

class NCOrgTask : public ListForm, public CCustomDraw<NCOrgTask>
{
public:
   NCOrgTask();

   virtual DWORD GetResourceID() const { return IDC_NC_TASK; }
   virtual DWORD GetMenuBarID() const { return IDC_NC_TASK; }

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(NCOrgTask)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, DoSelect)
      NOTIFY_CODE_HANDLER_EX(LVN_GETDISPINFO, SetCellInfo)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      CHAIN_MSG_MAP(CCustomDraw<NCOrgTask>)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(NCOrgTask, IDC_NC_TASK)

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   virtual int ImageListID(ListViewMultiLine *list) const
   {
      return (GetSystemMetrics(SM_CXSMICON) == 16) ? IDB_CHECKED : IDB_CHECKED32; 
   }

protected:
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT DoSelect(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT SetCellInfo(LPNMHDR hdr);
};

struct AgentTaskItemData
{
   std::wstring category;
   AgentTaskImpl *task;
};

class AgentTaskForm;
class TaskGadget : public CWindowImpl<TaskGadget>
{
public:
   typedef CWindowImpl<TaskGadget> Base;

   TaskGadget(const AgentTaskForm& owner);
   ~TaskGadget();

   bool Create(AgentTaskItemData* item, HWND parent, WORD top, WORD height, UINT id);

   bool HaveData() { return (text.GetWindowTextLength() > 0); }
   void UpdateLayout(WORD width);
   void UpdateData();
   void Focus() { text.SetFocus(); }

   BEGIN_MSG_MAP(TaskGadget)
   END_MSG_MAP()

protected:
   const AgentTaskForm& owner;
   CStatic label;
   CEdit text;

   AgentTaskItemData* task;
};

class AgentTaskData : public IFormData
{
public:
   AgentTaskData(const FILETIME& date, const wchar_t* id, const wchar_t* docType, bool hideSend)
   {
      this->hideSend = hideSend;
      this->docType = docType;
      LoadTasks(date, id);
   }

   ~AgentTaskData();

   bool canChange, hideSend;
   void Write();

   bool HideSend() const { return hideSend; }

   void LoadTasks(const FILETIME& date, const wchar_t* id);
   void AddTask(AgentTaskImpl* task);

   typedef std::vector<AgentTaskItemData> TaskList;
   TaskList tasks;
   std::wstring docType;
   std::wstring orgID;
   FILETIME appointDate, docDate;
};

class Pane : public CWindowImpl<Pane>
{
public:
   Pane() {}

   DECLARE_WND_CLASS(L"PANE")

   BEGIN_MSG_MAP(Pane)
   END_MSG_MAP()
};

class AgentTaskForm : public BaseForm
{
public:
   AgentTaskForm() : data(NULL), orgID(NO_ROWID) {}
   ~AgentTaskForm();

   DECLARE_FORM(AgentTaskForm, IDC_AGENT_TASK)

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDC_AGENT_TASK; }
   virtual DWORD GetMenuBarID() const { return IDC_AGENT_TASK; }

   BEGIN_MSG_MAP(AgentTaskForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_SEND, Sending)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
      MSG_WM_PAINT(OnPaint)
      //MESSAGE_HANDLER(WM_VSCROLL, OnVScroll)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual void UpdateLayout(bool recalc);
   void LoadMenuBar();

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/);
	//LRESULT OnVScroll(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& /*bHandled*/);

   const FILETIME& AppointDate() const;
   const FILETIME& DocDate() const { return data->docDate; }
   const ROWID& OrgID() const;

   void OnPaint(HDC );

protected:
   std::vector<TaskGadget*> items;
   AgentTaskData *data;

   CScrollContainer container;
   Pane pane;
   CMenuBarCtrl menuBar;
   mutable ROWID orgID;
   mutable FILETIME appointDate;
};

IMPLEMENT_FORM(NCOrgTask)
IMPLEMENT_FORM(AgentTaskForm)

const DocType* NCOrgTaskData::prevDocType = NULL;

//
// --------------------------------------- NCOrgTaskData ---------------------------------------
//
BEGIN_TYPE_REFLECTION(NCOrgTaskItem)
   REGISTER_STRING_MEMBER(NCOrgTaskItem, text)
   REGISTER_STRING_MEMBER(NCOrgTaskItem, empty)
   REGISTER_FILETIME_MEMBER(NCOrgTaskItem, date)
END_TYPE_REFLECTION(NCOrgTaskItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"", L"empty", 20 },
   { ListFormData::Header::Left, L"Дата", L"date", 40 },
   { ListFormData::Header::Left, L"", L"text", 80 },
};

NCOrgTaskData::NCOrgTaskData(const FILETIME& date, const wchar_t* id, bool canCheck, const DocType* docType, bool hideSend) : current (-1)
{
   this->hideSend = hideSend;
   this->docType = (docType) ? docType : prevDocType;
   this->canCheck = canCheck;

   Load(date, id);
}

NCOrgTaskData::~NCOrgTaskData()
{
   std::vector<AgentTaskImpl*>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
      delete (*i);
}

const ListFormData::Header* NCOrgTaskData::GetHeader() const
{
   return header;
}

int NCOrgTaskData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]);
}

bool NCOrgTaskData::Get(IReflectableData* data, int index) const
{
   const AgentTaskImpl* item = Item(index);

   bool ret = false;
   if( item )
   {
      ((NCOrgTaskItem*)data)->date = item->appointDate;
      ((NCOrgTaskItem*)data)->text = item->text;
      ((NCOrgTaskItem*)data)->empty = L"";

      ret = true;
   }

   return ret;
}

bool NCOrgTaskData::CheckTask(int index)
{
   if( !canCheck )
      return Editing(index);

   AgentTaskImpl* i = (AgentTaskImpl*)Item(index);
   if( i == NULL )
      return false;

   i->execDate = checkDate;
   if( (i->flags & AgentTask::Done) != 0 )
      i->flags &= (~AgentTask::Done);
   else
      i->flags |= AgentTask::Done;
   i->flags &= (~AgentTask::Exported);
   i->Write();

   return true;
}

bool NCOrgTaskData::Selecting(int index)
{
   return Editing(index);
}

bool NCOrgTaskData::Editing(int index)
{
   const AgentTaskImpl* item = Item(index);
   
   if( item )
      MessageBox(GetActiveWindow(), item->text, L"Информация", MB_OK);

   return false;
}

bool NCOrgTaskData::IsSVTask(int index) const
{
   const AgentTaskImpl* item = Item(index);
   return (item) ? ((item->flags & AgentTask::SuperTask) != 0) : false;
}

bool NCOrgTaskData::IsDone(int index) const
{
   const AgentTaskImpl* item = Item(index);
   return (item) ? ((item->flags & AgentTask::Done) != 0) : false;
}

void NCOrgTaskData::Backing()
{
   if( docType == NULL )
      docType = docTypeManager.GetDocType(dtOrder);

   if( !canCheck )
   {
      if( CreateNextDoc(orgID.c_str()) )
         return;

      SetNextCreatedDoc(dtAgentTask);
      prevDocType = docType;
      
      OrgImpl o;
      o.id = (wchar_t*)orgID.c_str();
      o.Read();

      if( !docType->CreateDocument(o.RID()) )
      {
         SetNextCreatedDoc(NULL);
         OpenOrgDocs(orgID.c_str(), docType->Type());
      }

      return;
   } 

   // save checking
   //vector_t<AgentTaskImpl*>::iterator i = items.begin();
   //for( ; i != items.end(); i++ )
   //{
   //   i->Write();
   //}
   
   AssignTask(checkDate, orgID.c_str(), docType->Type(), hideSend);
}

void NCOrgTaskData::Load(const FILETIME& date, const wchar_t* id)
{
   checkDate = date;
   category.push_back(L"<Все>");

   TaskCategoryImpl ti;
   SQLTable tt(ti.Name());
   bool bdo = tt.Select(&ti);
   while( bdo )
   {
      category.push_back(ti.name);
      bdo = tt.SelectNext(&ti);
   }

   orgID = id;

   wchar_t buf[50];
   std::vector<ROWID> rids;
   SQLTable table(AgentTaskImpl().Name());
   wsprintf(buf, L"%d%09d", (DWORD)(*(__int64*)&date / 1000000000), (DWORD)(*(__int64*)&date % 1000000000));
   std::wstring sql(L"WHERE id = '"); sql += id; sql += L"' AND (date = "; sql += buf; sql += L" OR ((flags & 1) = 0) )";
   sql += L" ORDER BY date";
   table.RIDList(&rids, sql.c_str());
   std::vector<ROWID>::const_iterator i = rids.begin();
   for( ; i != rids.end(); i++ )
   {
      AgentTaskImpl *t = new AgentTaskImpl();
      t->Read(*i);
      items.push_back(t);
   }

   Select(0);
}

const AgentTaskImpl* NCOrgTaskData::Item(int index) const
{
   if( index < 0 || (unsigned)index >= showed.size() )
      return NULL;

   return items[showed[index]];
}

void NCOrgTaskData::Select(int curCat)
{
   if( curCat >=0 && (unsigned)curCat < category.size() && current != curCat )
   {
      current = curCat;

      showed.clear();
      if( current == 0 )
      {
         // add all items
         for( unsigned i=0; i<items.size(); i++ )
            showed.push_back(i);
      } else
      {
         std::wstring& cat = category[current];
         std::vector<AgentTaskImpl*>::const_iterator i = items.begin();

         int idx = 0;
         for( ; i != items.end(); i++, idx++ )
         {
            if( cat.compare((*i)->category) == 0 )
               showed.push_back(idx);
         }
      }
   }
}

//
// --------------------------------------- NCOrgTask ---------------------------------------
//
NCOrgTask::NCOrgTask()
{
}

bool NCOrgTask::SetData(IFormData *_data)
{
   if( ListForm::SetDataEx(_data, 2) == false )
      return false;

   if( ((NCOrgTaskData*)data)->HaveData() == false )
   {
      ((NCOrgTaskData*)data)->Backing();
      return true;
   }

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID()));
   SetListLayout(false);

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)((NCOrgTaskData*)data)->CurrentCategory().c_str();
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   if( ((NCOrgTaskData*)data)->HideSend() )
      menuBar.HideButton(IDC_SEND, TRUE);
   return true;
}

DWORD NCOrgTask::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   if( ((NCOrgTaskData*)data)->IsSVTask(lvcd->nmcd.dwItemSpec) )
      lvcd->clrText = RGB(255,0,0);
	return CDRF_NOTIFYITEMDRAW;
}

LRESULT NCOrgTask::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   CRect menuBounds;
   menuBar.GetRect(IDC_VIEW_TYPE, menuBounds);
   menuBar.ClientToScreen(menuBounds);

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   MENUITEMINFO mi;
   const std::vector<std::wstring>& category = ((NCOrgTaskData*)data)->Category();
   std::vector<std::wstring>::const_iterator i = category.begin();
   mi.cbSize = sizeof(mi);
   mi.fMask = 0;
   for( ; i != category.end(); i++ )
   {
      UINT flag = MF_STRING;
      std::wstring name(L"&");
      name += (*i);

      AppendMenu(hm, flag, ctr, name.c_str());
      ctr++;
   }

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN, 
      menuBounds.left, menuBounds.top, m_hWnd, NULL);

   if( res > 0 )
   {
      ((NCOrgTaskData*)data)->Select(res-1);

      TBBUTTONINFO bi;
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_TEXT;
      bi.pszText = (LPWSTR)((NCOrgTaskData*)data)->CurrentCategory().c_str();
      menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

      Refresh();
   }
   return 0;
}

LRESULT NCOrgTask::DoSelect(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;

   DWORD hitFlags = 0;
   if( index >= 0 )
   {
      CRect bounds;
      listCtrl.GetItemRect(index, bounds, LVIR_BOUNDS);
      bounds.right = bounds.left + GetSystemMetrics(SM_CXSMICON);
      bool redraw = false;
      if( bounds.PtInRect(((NMLISTVIEW*)hdr)->ptAction) )
         redraw = ((NCOrgTaskData*)data)->CheckTask(index);
      else 
         redraw = ((ListFormData*)data)->Selecting(index);

      if( redraw )
      {
         listCtrl.SetItemState(index, 0, LVIS_SELECTED);
         listCtrl.RedrawItems(index, index);
      }
   }
   return TRUE;
}

LRESULT NCOrgTask::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( ((NCOrgTaskData*)data)->CanCheck() )
   {
      DocDataList dl;
      AgentTaskImpl::AddDocs(&dl, ((NCOrgTaskData*)data)->OrgID(), ((NCOrgTaskData*)data)->DocDate(), false);
      SendDocuments(&dl);
      dl.RemoveDocuments();
   }
   return 0;
}

LRESULT NCOrgTask::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
{
   ((NCOrgTaskData*)data)->Backing();
   return 0;
}

LRESULT NCOrgTask::SetCellInfo(LPNMHDR hdr)
{
   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( ListForm::SetCellInfo(hdr) == FALSE )
   {
      if( di->item.mask & LVIF_IMAGE )
         di->item.iImage = 0;
      return FALSE;
   }

   int index = di->item.iItem;
   if( di->item.mask & LVIF_IMAGE )
      di->item.iImage = (((NCOrgTaskData*)data)->IsDone(index)) ? 1 : 0;
   
   return TRUE;
}

//
// --------------------------------------- AgentTaskImpl ---------------------------------------
//
bool AgentTaskImpl::CreateDocument(const ROWID &orgID)
{
   //OpenAgentTask(date, true, NULL);
   return true;
}

bool AgentTaskImpl::Init(const ROWID &orgID)
{
   OrgImpl o;
   o.Read(orgID);

   id = holder.Add(o.id);

   SYSTEMTIME st;
   GetLocalTime(&st);
   ResetTime(&st);

   SystemTimeToFileTime(&st, &date);
   *(__int64*)&appointDate = *(__int64*)&date + (__int64)1000000 * 3600 * 24 * 7; // 1 week add
   *(__int64*)&execDate = 0;
   flags = 0;

   category = L"";
   text = L"";

   return true;
}

bool AgentTaskImpl::ClearDirty(SQLTable *updateTable, bool reverse)
{
   if( reverse )
   {
      if( flags & AgentTask::Exported ) flags &= (~AgentTask::Exported);
      else flags |= AgentTask::Exported;
   } else
      flags |= AgentTask::Exported;

   return (updateTable == NULL) ? true : updateTable->Update(*this, L"flags", rid);
}

void AgentTaskImpl::SetDone()
{
   flags |= AgentTask::Done;
}

void AgentTaskImpl::EditDocument(UINT retForm)
{
   //OpenAgentTask(this, dtOrder);
}

void AgentTaskImpl::AddDocs(DocDataList* documents, const wchar_t* id, const FILETIME& date, bool isExecDate)
{
   wchar_t buf[50];
   std::vector<ROWID> rids;
   wsprintf(buf, L"%d%09d", (DWORD)(*(__int64*)&date / 1000000000), (DWORD)(*(__int64*)&date % 1000000000));
   const DocType* dt = docTypeManager.GetDocType(dtAgentTask);
   const DocType* dtSV = docTypeManager.GetDocType(dtSVTask);

   SQLTable t(AgentTaskImpl().Name());
   std::wstring sql(L"WHERE id='");  sql += id; sql += L"' AND "; sql += (isExecDate) ? L"execDate" : L"date"; sql += L" = "; sql += buf;
   t.RIDList(&rids, sql.c_str());
   std::vector<ROWID>::const_iterator i = rids.begin();
   for( ; i != rids.end(); i++ )
   {
      AgentTaskImpl *t = new AgentTaskImpl();
      t->Read(*i);

      SendDocData sdd;
      sdd.document = t;
      sdd.docType = ((t->flags & AgentTask::SuperTask) != 0) ? dtSV : dt;

      documents->push_back(sdd);
   }
}

//
// --------------------------------------- TaskGadget ---------------------------------------
//
TaskGadget::TaskGadget(const AgentTaskForm& _owner) : owner(_owner)
{
}

TaskGadget::~TaskGadget()
{
}

bool TaskGadget::Create(AgentTaskItemData* item, HWND parent, WORD top, WORD height, UINT id)
{
   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   int offset = 2 * scale;
   int labelHeight = 16 * scale;
   int defaultWidth = 100 * scale;

   task = item;

   CRect bounds;
   bounds.top = top;
   bounds.left = offset;
   bounds.bottom = top + labelHeight;
   bounds.right = 2* offset + defaultWidth;

   label.Create(parent, bounds, task->category.c_str(), WS_CHILD | WS_VISIBLE);
   SetSystemFont(label.m_hWnd, FALSE);

   bounds.top = bounds.bottom + offset;
   bounds.bottom = height - labelHeight - offset + bounds.top;
   if( bounds.bottom < offset ) bounds.bottom = offset;

   const wchar_t* p = (task->task) ? task->task->text : L"";
   text.Create(parent, bounds, p, WS_CHILD | WS_VISIBLE | WS_BORDER, 0, id);
   SetSystemFont(text.m_hWnd, FALSE);

   return true;
}

void TaskGadget::UpdateLayout(WORD width)
{
   const int offset = 2;

   CRect bounds;
   CWindow parent = label.GetParent();

   label.GetWindowRect(bounds);
   parent.ScreenToClient(bounds);
   bounds.right = width - offset;
   label.MoveWindow(bounds);

   text.GetWindowRect(bounds);
   parent.ScreenToClient(bounds);
   bounds.right = width - offset;
   text.MoveWindow(bounds);
}

void TaskGadget::UpdateData()
{
   int len = text.GetWindowTextLength() + 1;
   if( len > 1 )
   {
      if( task->task == NULL )
      {
         task->task = new AgentTaskImpl();
         task->task->Init(owner.OrgID());
         task->task->date = owner.DocDate();
         task->task->category = task->task->holder.Add(task->category.c_str());
      }

      task->task->appointDate = owner.AppointDate();
      wchar_t* data = (wchar_t*)alloca(len * sizeof(wchar_t));
      text.GetWindowText(data, len);

      task->task->text = task->task->holder.Add(data);
   } else if( task->task )
      task->task->text = L"";
}

//
// --------------------------------------- AgentTaskForm ---------------------------------------
//

bool AgentTaskForm::SetData(IFormData *_data)
{
   data = (AgentTaskData*)_data;

   UINT id = 1000;
   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   WORD startTop = 0;//30 * scale;
   WORD top = startTop;
   WORD height = 50 * scale;
   int offset = 3 * scale;

   container.Create(m_hWnd, NULL, NULL, WS_CHILD | WS_VISIBLE);

   pane.Create(container.m_hWnd, NULL, NULL, WS_CHILD | WS_VISIBLE);

   SYSTEMTIME st;
   FileTimeToSystemTime(&data->appointDate, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   std::vector<AgentTaskItemData>::iterator i = data->tasks.begin();
   for( ; i != data->tasks.end(); i++ )
   {
      TaskGadget* tg = new TaskGadget(*this);
      tg->Create(&(*i), pane.m_hWnd, top, height, id);

      items.push_back(tg);
      id++;
      top += height + offset;
   }

   CRect rc;
   GetClientRect(rc);
   rc.bottom = top;
   pane.MoveWindow(rc);
   container.SetClient(pane);

   SetSystemFont(m_hWnd, FALSE);

   LoadMenuBar();
   if( ((AgentTaskData*)data)->HideSend() )
      menuBar.HideButton(IDC_SEND, TRUE);

   UpdateLayout(true);
   return true;
}

LRESULT AgentTaskForm::CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/)
{
   if( IsSquareScreen() == false && wParam == SPI_SETSIPINFO )
   {
      CRect bounds;
      CWindow parent = GetParent();
      parent.GetClientRect(bounds);

      SIPINFO si;
      memset (&si, 0, sizeof (si));
      si.cbSize = sizeof (si);

      if (SipGetInfo(&si)) 
      {
         CRect rc(si.rcVisibleDesktop);
         parent.ScreenToClient(rc);
         rc.right -= rc.left;
         rc.bottom -= rc.top;

         if( rc.bottom > bounds.bottom )
            rc.bottom = bounds.bottom;

         MoveWindow(rc, TRUE);
         UpdateLayout(false);
      }
   }
   return 0;
}

void AgentTaskForm::UpdateLayout(bool recalc)
{
   CRect rc;
   GetClientRect(rc);

   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   rc.top = 30 * scale;
   container.MoveWindow(rc);

   std::vector<TaskGadget*>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
      (*i)->UpdateLayout((WORD)rc.right);
}

void AgentTaskForm::LoadMenuBar()
{
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));
}

void AgentTaskForm::OnPaint(HDC )
{
   CRect rc;
   PAINTSTRUCT pPaint;

   HDC hdc = BeginPaint(&pPaint);
   GetClientRect(rc);

   //WORD curTop = 0;

   //rc.left = 3;
   //rc.top = 5 - curTop;
   //rc.bottom = 25;
   //HFONT hf = GetFont();
   //SelectObject(hdc, hf);
   //DrawText(hdc, L"Введите задачи на следующий визит", -1, rc, DT_SINGLELINE | DT_VCENTER | DT_LEFT);

   int scale = GetSystemMetrics(SM_CXSMICON) / 16;
   MoveToEx(hdc, 0, 27 * scale, NULL);
   LineTo(hdc, rc.right, 27 * scale);

   EndPaint(&pPaint);
}

const FILETIME& AgentTaskForm::AppointDate() const
{
   SYSTEMTIME st;
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
   SystemTimeToFileTime(&st, &appointDate);
   return appointDate;
}

const ROWID& AgentTaskForm::OrgID() const
{
   if( orgID == NO_ROWID )
   {
      OrgImpl o;
      o.id = (wchar_t*)data->orgID.c_str();
      o.Read();
      orgID = o.RID();
   }
   return orgID;
}

LRESULT AgentTaskForm::Sending(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->canChange )
   {
      std::vector<TaskGadget*>::iterator i = items.begin();
      for( ; i != items.end(); i++ )
         (*i)->UpdateData();

      data->Write();
   }

   DocDataList dl;
   AgentTaskImpl::AddDocs(&dl, data->orgID.c_str(), DocDate(), false);
   SendDocuments(&dl);
   dl.RemoveDocuments();
   return 0;
}

LRESULT AgentTaskForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( data->canChange )
   {
      std::vector<TaskGadget*>::iterator i = items.begin();
      for( ; i != items.end(); i++ )
      {
         if( !(*i)->HaveData() )
            break;
      }
      if( i != items.end() )
      {
         (*i)->Focus();
         MessageBox(L"Не все задачи заполненны!", L"Ошибка", MB_OK | MB_ICONSTOP);
         return 0;
      }
      for( i = items.begin() ; i != items.end(); i++ )
         (*i)->UpdateData();

      data->Write();
   }

   if( !CreateNextDoc(data->orgID.c_str()) )
   {
      //if( data->retToDocList )
      //   OpenListDoc(data->docType.c_str());
      //else
         OpenOrgDocs(data->orgID.c_str(), data->docType.c_str());
   }
   return 0;
}

AgentTaskForm::~AgentTaskForm()
{
   std::vector<TaskGadget*>::iterator i = items.begin();
   for( ; i != items.end(); i++ )
      delete (*i);

   delete data;
}

AgentTaskData::~AgentTaskData()
{
   TaskList::const_iterator i = tasks.begin();
   for( ; i != tasks.end(); i++ )
      delete i->task;
}

void AgentTaskData::Write()
{
   TaskList::iterator i = tasks.begin();
   for( ; i != tasks.end(); i++ )
   {
      if( i->task )
      {
         i->task->flags &= (~AgentTask::Exported);
         i->task->Write();
      }
   }
}

void AgentTaskData::AddTask(AgentTaskImpl* task)
{
   TaskList::iterator i = tasks.begin();
   for( ; i != tasks.end(); i++ )
   {
      if( i->category.compare(task->category) == 0 )
      {
         delete i->task;
         i->task = task;
      }
   }
}

void AgentTaskData::LoadTasks(const FILETIME& date, const wchar_t* id)
{
   docDate = date;
   canChange = true;

   TaskCategoryImpl ti;
   SQLTable tt(ti.Name());
   bool bdo = tt.Select(&ti);
   while( bdo )
   {
      AgentTaskItemData data;
      data.category = ti.name;
      data.task = NULL;
      tasks.push_back(data);

      bdo = tt.SelectNext(&ti);
   }

   bool setADate = false;
   wchar_t buf[50];
   std::vector<ROWID> rids;
   SQLTable t(AgentTaskImpl().Name());

   wsprintf(buf, L"%d%09d", (DWORD)(*(__int64*)&date / 1000000000), (DWORD)(*(__int64*)&date % 1000000000));
   std::wstring sql(L"WHERE id='");  sql += id; sql += L"' AND date = "; sql += buf;

   orgID = id;
   t.RIDList(&rids, sql.c_str());
   std::vector<ROWID>::const_iterator i = rids.begin();
   for( ; i != rids.end(); i++ )
   {
      AgentTaskImpl *t = new AgentTaskImpl();
      t->Read(*i);

      if( !t->IsDirty() )
         canChange = false;

      appointDate = t->appointDate;
      setADate = true;

      AddTask(t);
   }

   if( !setADate )
      *(__int64*)&appointDate = *(__int64*)&date + (__int64)10000000 * 3600 * 24 * 7;
}

void AssignTask(const FILETIME& date, const wchar_t* id, const wchar_t* docType, bool hideSend)
{
   _Module.GetFrame()->Load(IDC_AGENT_TASK, new AgentTaskData(date, id, docType, hideSend));
}

void OpenAgentTask(const FILETIME& date, const wchar_t* id, const DocType* docType, bool canCheck, bool hideSend)
{
   _Module.GetFrame()->Load(IDC_NC_TASK, new NCOrgTaskData(date, id, canCheck, docType, hideSend));
}
