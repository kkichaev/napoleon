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
const wchar_t AllCategory[] = L"<Все>";

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
   NCOrgTaskData(const ROWID& orgID, bool canCheck, const DocType* docType);
   ~NCOrgTaskData() {}

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return NCOrgTaskItem().GetType(); }
   virtual int Count() const { return items.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Selecting(int index);
   virtual bool Editing(int index);

   bool IsDone(int index) const;
   bool CanCheck() const { return canCheck; }
   bool HaveData() const { return (items.size() > 0); }

   const std::vector<std::wstring>& Category() const { return catList; }
   const std::wstring& CurrentCategory() const { return category; }
   void SelectCategory(const std::wstring& current);

   void Backing();

   static const DocType* PrevDocType() { return prevDocType; }
   static void SetPrevDocType(const DocType* dt) { prevDocType = dt; }

protected:
   void Load(const ROWID& rid);

protected:
   static const DocType* prevDocType;

   StringHolder holder;

   const DocType* docType;
   std::wstring orgID;

   bool canCheck;
   std::wstring category;
   std::vector<std::wstring> catList;

   std::vector<ROWID> items;
   mutable AgentTaskImpl item;
};

class NCOrgTask : public ListForm
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
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(NCOrgTask, IDC_NC_TASK)

   DWORD GetHitTest() const { return hitFlags; }

   virtual int ImageListID(ListViewMultiLine *list) const
   {
      return (GetSystemMetrics(SM_CXSMICON) == 16) ? IDB_CHECKED : IDB_CHECKED32; 
   }

protected:
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT DoSelect(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);
   LRESULT SetCellInfo(LPNMHDR hdr);

protected:
   DWORD hitFlags;
};

class AgentTaskData : public IFormData
{
public:
   AgentTaskData(AgentTaskImpl *at, bool retToDocList, const wchar_t *docType)
   {
      task = at;
      this->retToDocList = retToDocList;
      this->docType = (docType) ? docType : dtAgentTask;
   }

   ~AgentTaskData() { delete task; }

   AgentTaskImpl *task;
   bool retToDocList;
   std::wstring docType;
};

class AgentTaskForm : public BaseForm
{
public:
   AgentTaskForm() : data(NULL) {}
   ~AgentTaskForm() { delete data; }

   DECLARE_FORM(AgentTaskForm, IDC_AGENT_TASK)

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDC_AGENT_TASK; }
   virtual DWORD GetMenuBarID() const { return IDC_AGENT_TASK; }

   BEGIN_MSG_MAP(AgentTaskForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      MESSAGE_HANDLER(WM_SETTINGCHANGE, CheckSIP)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual void UpdateLayout(bool recalc);
   void LoadMenuBar();

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   LRESULT CheckSIP(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& /*bHandled*/);

protected:
   AgentTaskData *data;

   CMenuBarCtrl menuBar;
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
   { ListFormData::Header::Left, L"", L"empty", 15 },
   { ListFormData::Header::Left, L"Дата", L"date", 35 },
   { ListFormData::Header::Left, L"", L"text", 80 },
};

NCOrgTaskData::NCOrgTaskData(const ROWID& orgID, bool canCheck, const DocType* docType)
{
   this->docType = (docType) ? docType : prevDocType;
   this->canCheck = canCheck;

   Load(orgID);
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
   if( index < 0 || index >= (int)items.size() )
      return false;

   item.Read(items[index]);

   ((NCOrgTaskItem*)data)->date = item.date;
   ((NCOrgTaskItem*)data)->text = item.text;
   ((NCOrgTaskItem*)data)->empty = L"";

   return true;
}

bool NCOrgTaskData::Selecting(int index)
{
   if( !canCheck ) return Editing(index);

   if( index < 0 || index >= (int)items.size() )
      return false;

   //if( owner )
   //{
   //   DWORD flg = ((NCOrgTask*)owner)->GetHitTest();
   //   if( flg != LVHT_ONITEMICON )
   //      return Editing(index);
   //}

   item.Read(items[index]);
   item.flags &= (~AgentTask::Exported);
   if( (item.flags & AgentTask::Done) != 0 )
      item.flags &= (~AgentTask::Done);
   else
      item.flags |= AgentTask::Done;

   item.Write();
   return true;
}

bool NCOrgTaskData::Editing(int index)
{
   if( index < 0 || index >= (int)items.size() )
      return false;

   item.Read(items[index]);
   
   MessageBox(GetActiveWindow(), item.text, L"Информация", MB_OK);
   return false;
}

bool NCOrgTaskData::IsDone(int index) const
{
   if( index < 0 || index >= (int)items.size() )
      return false;

   item.Read(items[index]);
   return ((item.flags & AgentTask::Done) != 0);
}

void NCOrgTaskData::Backing()
{
   if( docType == NULL )
      docType = docTypeManager.GetDocType(dtOrder);

   if( !canCheck )
   {
      OrgImpl o;
      o.id = (wchar_t*)orgID.c_str();
      o.Read();

      SetNextCreatedDoc(dtAgentTask);
      prevDocType = docType;
      
      if( !docType->CreateDocument(o.RID()) )
      {
         SetNextCreatedDoc(NULL);
         OpenOrgDocs(orgID.c_str(), docType->Type());
      }

      return;
   } 

   OpenOrgDocs(orgID.c_str(), dtAgentTask);
}

void NCOrgTaskData::Load(const ROWID& rid)
{
   catList.push_back(AllCategory);

   TaskCategoryImpl ti;
   SQLTable tt(ti.Name());
   bool bdo = tt.Select(&ti);
   while( bdo )
   {
      catList.push_back(ti.name);
      bdo = tt.SelectNext(&ti);
   }

   OrgImpl oi;
   oi.Read(rid);
   orgID = oi.id;

   SelectCategory(catList[0]);
}

void NCOrgTaskData::SelectCategory(const std::wstring& current)
{
   category = current;

   SQLTable table(item.Name());
   std::wstring sql(L"WHERE id = '"); sql += orgID; sql += L"' and ((flags & 1) = 0)";
   if( current.compare(AllCategory) != 0 )
   {
      sql += L" and category = '"; sql += current; sql += L"'";
   }
   sql += L" ORDER BY date";
   items.clear();
   table.RIDList(&items, sql.c_str());
}

//
// --------------------------------------- NCOrgTask ---------------------------------------
//
NCOrgTask::NCOrgTask() : hitFlags(0)
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

   return true;
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
      ((NCOrgTaskData*)data)->SelectCategory(category[res-1]);

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

   listCtrl.HitTest(((NMLISTVIEW*)hdr)->ptAction, (UINT*)&hitFlags);
   if( index >= 0 && ((ListFormData*)data)->Selecting(index) )
   {
      listCtrl.SetItemState(index, 0, LVIS_SELECTED);
      listCtrl.RedrawItems(index, index);
   }
   return TRUE;
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

bool AgentTaskImpl::CreateDocument(const ROWID &orgID)
{
   if( NCOrgTaskData::PrevDocType()->Type() != dtAgentTask )
      OpenAgentTask(orgID, true, NCOrgTaskData::PrevDocType());
   else
   {
      if( !Init(orgID) )
         return false;

      OpenAgentTask(this, true, dtAgentTask);
   }
   return true;
}

bool AgentTaskImpl::Init(const ROWID &orgID)
{
   OrgImpl o;
   o.Read(orgID);

   id = holder.Add(o.id);

   SYSTEMTIME st;
   GetLocalTime(&st);

   SystemTimeToFileTime(&st, &date);
   
   *(__int64*)&appointDate = *(__int64*)&date + (__int64)10000000 * 3600 * 24 * 7;

   category = L"";
   text = L"";
   flags = 0;

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

void AgentTaskImpl::EditDocument(UINT retForm)
{
   OpenAgentTask(this, (retForm != IDD_ORDER_LIST), dtAgentTask);
}

bool AgentTaskImpl::CanRemove() const
{
   if( (flags & (AgentTask::Exported | AgentTask::Done)) == (AgentTask::Exported | AgentTask::Done) )
      return (MessageBox(NULL, L"Удалить задачу?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES);

   return false;
}

const wchar_t* AgentTaskImpl::Description() const
{
   return category;
}

//
// --------------------------------------- AgentTaskForm ---------------------------------------
//

bool AgentTaskForm::SetData(IFormData *_data)
{
   data = (AgentTaskData*)_data;

   GetDlgItem(IDC_AGENT_TASK).SetWindowText(data->task->category);
   GetDlgItem(IDC_TEXT).SetWindowText(data->task->text);

   SYSTEMTIME st;
   FileTimeToSystemTime(&data->task->appointDate, &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).SetSystemTime(GDT_VALID, &st);

   if( data->task->flags & (AgentTask::Done | AgentTask::Exported) )
   {
      GetDlgItem(IDC_AGENT_TASK).EnableWindow(FALSE);
      GetDlgItem(IDC_TEXT).EnableWindow(FALSE);
   }

   CComboBox wnd(GetDlgItem(IDC_AGENT_TASK));

   TaskCategoryImpl tc;
   SQLTable t(tc.Name());
   bool bdo = t.Select(&tc, L"ORDER BY name");
   while( bdo )
   {
      wnd.AddString(tc.name);
      bdo = t.SelectNext(&tc);
   }

   LoadMenuBar();
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
   CRect rc, rc2;
   GetClientRect(rc);

   CWindow wnd(GetDlgItem(IDC_AGENT_TASK));
   wnd.GetWindowRect(rc2);
   ScreenToClient(rc2);

   wnd.MoveWindow(rc2.left, rc2.top, rc.right - rc2.left, rc2.Height());

   CWindow wnd1(GetDlgItem(IDC_TEXT));
   wnd1.GetWindowRect(rc2);
   ScreenToClient(rc2);

   wnd1.MoveWindow(rc2.left, rc2.top, rc.right - rc2.left, rc.bottom - rc2.top - 2);
}

void AgentTaskForm::LoadMenuBar()
{
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));
}

static void GetText(std::wstring* value, CWindow wnd)
{
   int len = wnd.GetWindowTextLength() + 1;
   wchar_t *txt = (wchar_t*)alloca(len * sizeof(wchar_t));
   wnd.GetWindowText(txt, len);
   value->assign(txt);
}

LRESULT AgentTaskForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   std::wstring cat, text;
   GetText(&cat, GetDlgItem(IDC_AGENT_TASK));
   GetText(&text, GetDlgItem(IDC_TEXT));
   if( cat.empty() == false && text.empty() == false )
   {
      SYSTEMTIME st;
      ((CDateTimePickerCtrl)GetDlgItem(IDC_ORDER_DATE)).GetSystemTime(&st);
      SystemTimeToFileTime(&st, &data->task->appointDate);

      data->task->category = (wchar_t*)cat.c_str();
      data->task->text = (wchar_t*)text.c_str();
      data->task->Write();
   }

   if( !data->retToDocList )
      OpenListDoc(dtAgentTask);
   else
      OpenOrgDocs(data->task->id, data->docType.c_str());
   return 0;
}


void OpenAgentTask(AgentTaskImpl *at, bool retToDocList, const wchar_t* docType)
{
   _Module.GetFrame()->Load(IDC_AGENT_TASK, new AgentTaskData(at, retToDocList, docType));
}

void OpenAgentTask(const ROWID& orgID, bool canCheck, const DocType* docType)
{
   _Module.GetFrame()->Load(IDC_NC_TASK, new NCOrgTaskData(orgID, canCheck, docType));
}

void SetAgentNextDocType(const DocType* docType)
{
   NCOrgTaskData::SetPrevDocType(docType);
}