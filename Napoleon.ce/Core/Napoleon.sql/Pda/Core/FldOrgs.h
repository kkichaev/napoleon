/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Папки организаций (маршруты)
 *
 *  ert   30/10/2007   creating
 */
#ifndef _ORGS_FOLDER_H
#define _ORGS_FOLDER_H

#include "SQLFolderForm.h"
#include "OrgFuncs.h"

#include <SQLSearch.h>

#include "ObjImpl.h"

class OrgFolders : public SQLFolderForm, public OrgFuncs, public SearchControl::ISearchEvent
{
public:
   OrgFolders();

   DECLARE_FORM(OrgFolders, IDD_FLDORGS);

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(OrgFolders)
      ORG_FUNC_HANDLER()
      COMMAND_ID_HANDLER(IDC_SHOW_2_ROW, ChangeRows)
      COMMAND_ID_HANDLER(IDC_CONTACTS, ChangeView)
      MSG_WM_CONTEXTMENU(ShowContextMenu)
      //MSG_WM_PAINT(OnPaint)
      CHAIN_MSG_MAP(SQLFolderForm)
   END_MSG_MAP()

   LRESULT ShowContextMenu(HWND hWnd, const CPoint &org);

   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuID() const;

   virtual void SearchClear(); // нажали на кнопку новый
   virtual void SearchDo(const wchar_t *text); 
   virtual int ImageIndex(int index) const;

   static int lastViewed;

protected:
   virtual void UpdateLayout(bool forceRecalc);
   virtual void Refresh();

   bool SetDataEx(IFormData *_data, int _scale);
   LRESULT ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ChangeView(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   //void OnPaint(HDC );
};

struct OrgDataItem : public FolderFormItem
{
   long sum;

   DECLARE_TYPE_REFLECTION(OrgDataItem)
};

#ifndef Alians

struct OrgFoldersData : public SQLFolderFormData, public OrgData
{
   OrgFoldersData();
   ~OrgFoldersData();

   virtual OrgFoldersData* Clone() const;

   virtual void InitData() { SelectFolder(lastFolder); }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual const DataReflector& DataType() const;

   virtual ROWID GetOID(int index) const;
   virtual bool Get(IReflectableData* data, int index) const;

   virtual void LoadTree();
   virtual void LoadFolderData(const TreeNode& folder);

   virtual bool IsTopLevel() const { return (inSearch) ? true : SQLFolderFormData::IsTopLevel(); }
   virtual bool HaveLeaf() const { return (inSearch) ? false : SQLFolderFormData::HaveLeaf(); }

   virtual bool SelectLeaf(int index);

   virtual DWORD Sum() const { return GetSum(); }

   bool FindItems(const wchar_t *text);
   void ClearSearch();

   bool IsCurDate(int index) const;

   virtual void RefreshCurrent();

protected:
   DWORD CountFolderSum(const OrgFolder &folder) const;

protected:
   class FOrgSum : public OrgSumImpl
   {
   public:
      DWORD GetFolderSum(const wchar_t *type, const vector_t<OrgFolderItem>& items);
   };
   virtual void AfterSetName(const Org& org) const {}

   mutable OrgFolderImpl orgFolder;
   mutable FOrgSum sums;
   mutable OrgImpl org;
   mutable std::wstring name;

   OrgDataItem odItem;
   bool inSearch;
   SQLTextSearcher searchHelper;

   static ROWID lastFolder;
};

#else

struct OrgFoldersData : public FolderFormData, public OrgData
{
   OrgFoldersData();
   ~OrgFoldersData();

   virtual void Load();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual const DataReflector& DataType() const;

   virtual CEOID GetOID(int index) const;
   virtual bool Get(IReflectableData* data, int index) const;

   virtual void LoadFolder(CEOID upFolder);
   virtual bool SelectLeaf(int index);
   virtual bool MoveToFolder(bool next);

   virtual DWORD Sum() const;

protected:
   DWORD CountFolderSum(DWORD oid) const;
   virtual void LoadLeafs(DWORD index, WORD size);

protected:
   mutable std::wstring name;
   OrgDataItem odItem;

   static CEOID lastFolder;
};

#endif // Alians

#ifdef SHEDULE
struct SheduleData : public OrgFoldersData
{
   SheduleData();

   virtual OrgFoldersData* Clone() const
   {
      OrgFoldersData *od = new SheduleData();
      od->SetDocType(GetDocType()->type);
      return od;
   }

   void ClearSavePos()
   {
      viewedDate = 0;
   }

   bool IsCurDate(int index) const;

   virtual bool Get(IReflectableData *data, int index) const;
   virtual void LoadFolder(CEOID upFolder);
   virtual bool SelectLeaf(int index);

   virtual DWORD GetSum() const { return currentSum; }

   DWORD currentSum;
   std::vector<std::wstring> times;

   static DWORD viewedDate;

   virtual void Load();

   void LoadData(CEOID folder);

   struct FolderItem : OrgFolderItem
   {
      FolderItem() { orderID = 0; }

      CEOID orderID;
   };

   struct Folder
   {
      Folder() { date = 0; sum = 0; dayOfWeek = 1; }
      Folder(DWORD _date) : date(_date) { sum = 0; dayOfWeek = 1; }

      DWORD date;
      DWORD sum;
      WORD  dayOfWeek;

      bool operator< (const Folder& _Ref) const { return date > _Ref.date; }
   };

   typedef std::map<Folder, std::vector<FolderItem> > FOLDER_MAP;
   FOLDER_MAP data;
   FOLDER_MAP::iterator currentData;

   // хранится расписание на дни недели 0 - 6
   std::vector<std::vector<OrgFolderItem> > shedule;
   StringHolder sh;

   void LoadShedule();

   void AddShedule(const SYSTEMTIME &st);

   void AddOrder(const Order& order, CEOID orderID);

   void MakeName(std::wstring *name, FOLDER_MAP::const_iterator &i) const;

   static const SYSTEMTIME& OrgShedule() { return orgShedule; }
   static void ClearShedule();

protected:
   static SYSTEMTIME orgShedule;
};

class SheduleForm : public OrgFolders
{
public:
   DECLARE_FORM(SheduleForm, IDD_SHEDULE)

   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_FLDORGS; }

   virtual LRESULT SetCellInfo(LPNMHDR hdr);

   BEGIN_MSG_MAP(SheduleForm)
      MESSAGE_HANDLER(WM_DESTROY, OnDestroy)
      CHAIN_MSG_MAP(OrgFolders)
   END_MSG_MAP()

   LRESULT OnDestroy(UINT /*uMsg*/, WPARAM wParam, LPARAM /*lParam*/, BOOL& bHandled)
   {
      lastViewed = GetLastVisibleItem();
      bHandled = false;
      return 0;
   }

   virtual bool SetData(IFormData *_data)
   {
      if( !OrgFolders::SetData(_data) ) return false;
      listCtrl.EnsureVisible(lastViewed, FALSE);
      return true;
   }

protected:
   virtual void UpdateLayout(bool forceRecalc);

   static SYSTEMTIME orgShedule;
};

#endif // SHEDULE

#endif
