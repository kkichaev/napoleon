/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Список организаций
 *
 *  ert   13/08/2007   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocType.h>

#include <ObjImpl.h>
#include "OrgDocs.h"
#include "FormEntries.h"

#include "SQLFolderForm.h"
#include "Contacts.h"
#include "InitDoc.h"
#include <BaseDialog.h>
#include <EnterNumber.h>
#include <NplConfig.h>
#include "Add.h"

struct OrgBalanceData : public SQLFolderFormData
{
   OrgBalanceData(const wchar_t* orgID);
   ~OrgBalanceData() { FreePkos(); }

   virtual void InitData() { SelectFolder(curFolderIndex); curFolderIndex = NO_ROWID; }

   const ROWID& OrgID() const { return orgRID; }
   const wchar_t* ID() const { return orgID.c_str(); }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual const DataReflector& DataType() const;

   virtual DWORD Sum() const;
   virtual bool Get(IReflectableData* data, int index) const;

   virtual bool Adding();

   void SetItemColor(NMLVCUSTOMDRAW *lvcd);

protected:
   static ROWID curFolderIndex;

   struct FirmData
   {
      std::wstring name;
      std::wstring id;
      DWORD sum;

      struct DocData
      {
         ROWID rid;
         bool isDelivery;
         FILETIME date;
         std::wstring dogovor;
         std::wstring dogId;
         std::wstring number;
         std::wstring supplyer;
         bool fiscal;
         bool isCustom;
         DWORD sum, sumD;

         bool operator< (const DocData& _item) const
         {
            int cmp;
            cmp = dogovor.compare(_item.dogovor);
            if( cmp < 0 ) return true;
            if( cmp > 0 ) return false;

            if( fiscal < _item.fiscal ) return true;
            if( fiscal > _item.fiscal ) return false;

            cmp = CompareFileTime(&date, &_item.date);
            if( cmp < 0 ) return true;
            if( cmp > 0 ) return false;

            if( (int)!isDelivery < (int)!_item.isDelivery ) return true;
            if( (int)!isDelivery < (int)!_item.isDelivery ) return false;

            return (number.compare(_item.number) < 0);
         }
      };
      std::set<DocData> leafs;
   };
   // id -> Data
   typedef std::map<std::wstring, FirmData> FirmsList;
   FirmsList::iterator currentFolder;
   FirmsList firms;

   std::wstring orgID;
   ROWID orgRID;

   typedef std::map<std::wstring, PKOImpl*> PkoMap;
   PkoMap pkos;

   mutable std::wstring numBuf, dateBuf, sumBuf;

   void LoadPkos(const wchar_t* id);
   void FreePkos();

   void LoadFirms();
   virtual void LoadTree();
   virtual bool SelectLeaf(int index);
   virtual void LoadFolderData(const TreeNode& folder);

   void LoadDocs();
   void LoadDocs(const wchar_t* tableName, const OrgImpl& org, bool isDelivery);
};

class OrgBalance : public SQLFolderForm, public CCustomDraw<OrgBalance>
{
public:
   OrgBalance();

   DECLARE_FORM(OrgBalance, IDD_BALANCE);

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDD_BALANCE; }
   virtual DWORD GetMenuID() const { return 0; }

   virtual void LoadMenuBar(bool hideSIP);
   virtual bool CanSetColumn(int rowIndex, int colIndex) const;

   void FolderChanged() { menuBar.EnableButton(IDC_ADD, (((OrgBalanceData*)data)->IsTopLevel()) ? FALSE : TRUE); }

   BEGIN_MSG_MAP(OrgBalance)
#ifdef ORG_NOTE
      COMMAND_ID_HANDLER(IDC_NOTES, Notes)
#endif
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      MSG_WM_LBUTTONDOWN(MouseDown)
      MSG_WM_PAINT(Paint)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      CHAIN_MSG_MAP(CCustomDraw<OrgBalance>)
      CHAIN_MSG_MAP(SQLFolderForm)
   END_MSG_MAP()

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

protected:
   LRESULT ItemSelected(LPNMHDR hdr);
   void MouseDown(UINT flags, const CPoint &pt);
   void Paint(HDC dc);
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

#ifdef ORG_NOTE
   LRESULT Notes(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
#endif

   virtual LRESULT SetCellInfo(LPNMHDR hdr);
   virtual void UpdateLayout(bool forceRecalc);
   virtual void Refresh();

   OrgInfo orgInfo;
};

int lastViewed = -1;
ROWID OrgBalanceData::curFolderIndex = NO_ROWID;
IMPLEMENT_FORM(OrgBalance)

OrgBalanceData::OrgBalanceData(const wchar_t* orgID)
{
   this->orgID = orgID;

   OrgImpl o;
   o.id = (wchar_t*)orgID;
   o.Read();
   
   orgRID = o.RID();
}

bool OrgBalanceData::Adding()
{
   //if( !IsTopLevel() && currentFolder != firms.end() )
   //{
   //   PaymentImpl *pi = new PaymentImpl();
   //   if( pi->Init(orgRID) )
   //   {
   //      curFolderIndex = UpFolder();
   //   if( owner ) lastViewed = owner->GetTopIndex();

   //      pi->supplyer = pi->holder.Add(currentFolder->second.id.c_str());
   //      pi->EditDocument(IDD_BALANCE);
   //   } else
   //      delete pi;
   //}

   return false;
}

struct ViewItem : public IReflectableData
{
   wchar_t* number;
   wchar_t* date;
   wchar_t* sum;

   DECLARE_TYPE_REFLECTION(ViewItem)
};

BEGIN_TYPE_REFLECTION(ViewItem)
   REGISTER_STRING_MEMBER(ViewItem, number)
   REGISTER_STRING_MEMBER(ViewItem, date)
   REGISTER_STRING_MEMBER(ViewItem, sum)
END_TYPE_REFLECTION(ViewItem)

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"", L"number", 80 },
   { ListFormData::Header::Left, L"Дата", L"date", 50 },
   { ListFormData::Header::Right, L"Долг/Сумма", L"sum", 50 }
};

const ListFormData::Header *OrgBalanceData::GetHeader() const
{
   return header;
}

int OrgBalanceData::ColumnsCount() const
{
   return sizeof(::header)/sizeof(::header[0]);
}

const DataReflector& OrgBalanceData::DataType() const
{
   return ViewItem().GetType();
}

bool OrgBalanceData::Get(IReflectableData* data, int index) const
{
   wchar_t buf[100], src[50];
   bool ret = false;
   if( IsTopLevel() )
   {
      if( index < (int)firms.size() )
      {
         FirmsList::const_iterator i = firms.begin();
         advance(i, index);

         long val = i->second.sum;
         ConvertScaling(src, val, SUM_SCALE);
         FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), val % SUM_SCALE, SUM_SCALE, false);
         sumBuf = buf;

         ((ViewItem*)data)->date = L"";
         ((ViewItem*)data)->sum = (wchar_t*)sumBuf.c_str();
         ((ViewItem*)data)->number = (wchar_t*)i->second.name.c_str();

         ret = true;
      }
   } else
   {
      if( currentFolder != firms.end() && index < (int)currentFolder->second.leafs.size() )
      {
         std::set<FirmData::DocData>::const_iterator li = currentFolder->second.leafs.begin();
         advance(li, index);
         const FirmData::DocData& dd = *li;
         SYSTEMTIME st;
         FileTimeToSystemTime(&dd.date, &st);

         numBuf = dd.number + L"\n";
         numBuf += dd.dogovor;
         ((ViewItem*)data)->number = (wchar_t*)numBuf.c_str();

         int cch = sizeof(buf)/sizeof(buf[0]);
         int wch = GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, cch);
         dateBuf = buf;
         dateBuf += L"\n";
         dateBuf += (dd.fiscal) ? L"" : L"не офиц.";
         ((ViewItem*)data)->date = (wchar_t*)dateBuf.c_str();

         long val;
         val = dd.sum;
         ConvertScaling(src, val, SUM_SCALE);
         FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), val % SUM_SCALE, SUM_SCALE, false);
         sumBuf = buf;

         PkoMap::const_iterator fnd = pkos.find(dd.number);
         if( fnd != pkos.end() )
         {
            val = fnd->second->sum;
            ConvertScaling(src, val, SUM_SCALE);
            FormatScaling(src, buf, sizeof(buf)/sizeof(buf[0]), val % SUM_SCALE, SUM_SCALE, false);
            sumBuf += L"\n";
            sumBuf += buf;
         }

         ((ViewItem*)data)->sum = (wchar_t*)sumBuf.c_str();

         ret = true;
      }
   }
   return ret;
}

DWORD OrgBalanceData::Sum() const
{
   DWORD sum = 0;
   if( currentFolder == firms.end() )
   {
      FirmsList::const_iterator i = firms.begin();
      for( ; i != firms.end(); i++ )
         sum += i->second.sum;
   } else
      sum = currentFolder->second.sum;

   return sum;
}

void OrgBalanceData::LoadTree()
{
   LoadFirms();

   int idx = 0;
   FirmsList::const_iterator i = firms.begin();
   for( ; i != firms.end(); i++, idx++ )
   {
      TreeNode *current = new TreeNode(&root);
      current->id = idx;
      current->haveLeafs = (i->second.leafs.size() > 0);
      root.childs.push_back(current);
   }
}

struct DocInfoItem : public IReflectableData
{
   FILETIME date;
   __int64  rowid;
   wchar_t *dogId;
   wchar_t *number;
   wchar_t *supplyer;
   WORD     fiscal;
   DWORD    sumD;

   virtual DWORD Sum() const { return 0; }

   DECLARE_TYPE_REFLECTION(DocInfoItem)
};

struct PayInfoItem : public DocInfoItem
{
   DWORD sum;
   DWORD params;

   virtual DWORD Sum() const { return sum; }

   DECLARE_TYPE_REFLECTION(PayInfoItem)
};

struct DlvInfoItem : public DocInfoItem
{
   vector_t<DeliveryItem> items;

   virtual DWORD Sum() const
   {
      DWORD sum = 0;
      vector_t<DeliveryItem>::const_iterator i = items.begin();
      for( ; i != items.end(); i++ )
         sum += i->sum;

      return sum;
   }

   DECLARE_TYPE_REFLECTION(DlvInfoItem)
};

BEGIN_TYPE_REFLECTION(DocInfoItem)
   REGISTER_FILETIME_MEMBER(DocInfoItem, date)
   REGISTER_STRING_MEMBER(DocInfoItem, number)
   REGISTER_STRING_MEMBER(DocInfoItem, supplyer)
   REGISTER_INT64_MEMBER(DocInfoItem, rowid)
   REGISTER_STRING_MEMBER(DocInfoItem, dogId)
   REGISTER_STRING_MEMBER(DocInfoItem, supplyer)
   REGISTER_USHORT_MEMBER(DocInfoItem, fiscal)
   REGISTER_ULONG_MEMBER(DocInfoItem, sumD)
END_TYPE_REFLECTION(DocInfoItem)

BEGIN_TYPE_REFLECTION(PayInfoItem)
   REGISTER_ULONG_SCALE_MEMBER(PayInfoItem, sum, SUM_SCALE)
   REGISTER_ULONG_MEMBER(PayInfoItem, params)
   CHAIN_REFLECTION(PayInfoItem, DocInfoItem)
END_TYPE_REFLECTION(PayInfoItem)

BEGIN_TYPE_REFLECTION(DlvInfoItem)
   REGISTER_COLLECTION_MEMBER(DlvInfoItem, items, DeliveryItem)
   CHAIN_REFLECTION(DlvInfoItem, DocInfoItem)
END_TYPE_REFLECTION(DlvInfoItem)

static void GetDogovor(std::wstring* name, const OrgImpl& o, const wchar_t* dogNum)
{
   vector_t<Dogovor>::const_iterator i = o.dogovors.begin();
   for( ; i != o.dogovors.end(); i++ )
      if( wcscmp(i->number, dogNum) == 0 )
      {
         *name = i->name;
         return;
      }

   *name = dogNum;
   return;
}

void OrgBalanceData::LoadDocs(const wchar_t* tableName, const OrgImpl& org, bool isDelivery)
{
   DocInfoItem* dii = (isDelivery) ? (DocInfoItem*)new DlvInfoItem() : (DocInfoItem*)new PayInfoItem();
   SQLTable t(tableName);
   std::wstring sql(L" WHERE id='");
   bool bdo;

   sql += orgID + L"' ORDER BY date";

   dii->sumD = 0;
   bdo = t.Select(dii, sql.c_str());
   while( bdo )
   {
      FirmData::DocData dd;
      GetDogovor(&dd.dogovor, org, dii->dogId);

      FirmsList::iterator fnd = firms.find(dii->supplyer);
      if( fnd == firms.end() ) fnd = firms.begin();
      
      dd.rid = dii->rowid;
      dd.isDelivery = isDelivery;
      dd.date = dii->date;
      dd.dogId = dii->dogId;
      dd.number = dii->number;
      dd.fiscal = (dii->fiscal > 0);
      dd.supplyer = dii->supplyer;
      dd.sum = dii->Sum();
      dd.sumD = dii->sumD;
      //if( !isDelivery )
      //   dd.isCustom = ((((PayInfoItem*)dii)->params & Payment::Created) != 0);
      //else
         dd.isCustom = false;

      if( !dd.isCustom )
         fnd->second.sum += (isDelivery) ? dd.sumD : dd.sum;
      fnd->second.leafs.insert(dd);

      bdo = t.SelectNext(dii);
   }

   delete dii;
}

void OrgBalanceData::LoadPkos(const wchar_t* id)
{
   std::vector<ROWID> rids;
   SQLTable st(PKOImpl().Name());
   std::wstring sql(L"WHERE id='"); sql += id; sql += L"'";
   st.RIDList(&rids, sql.c_str());

   std::vector<ROWID>::const_iterator i = rids.begin();
   for( ; i != rids.end(); i++ )
   {
      PKOImpl* p = new PKOImpl();
      p->Read(*i);
      pkos[p->number] = p;
   }
}

void OrgBalanceData::FreePkos()
{
   PkoMap::iterator i = pkos.begin();
   for( ; i != pkos.end(); i++ )
      delete i->second;
   pkos.clear();
}

void OrgBalanceData::LoadDocs()
{
   OrgImpl o;
   o.Read(orgRID);

   //LoadDocs(DeliveryImpl().Name(), o, true);
   LoadDocs(PaymentImpl().Name(), o, false);
   LoadPkos(o.id);
}

void OrgBalanceData::LoadFirms()
{
   firms.clear();

   NapoleonConfig config;
   std::wstring val;
   config.ReadValue(&val, SUPPL_TYPE);
   std::wstring::size_type sp = 0;
   for( int i=0; ; i++ )
   {
      std::wstring::size_type ep = val.find_first_of(L';', sp);
      std::wstring tval = val.substr(sp, (ep != std::wstring::npos) ? ep - sp : std::wstring::npos);

      std::wstring::size_type sepSym = tval.find(L'\t');

      if( sepSym != std::wstring::npos )
      {
         FirmData fd;
         fd.sum = 0;
         fd.name = tval.substr(0, sepSym);
         fd.id = tval.substr(sepSym + 1);

         firms[fd.id] = fd;
      }

      if( ep == std::wstring::npos ) break;
      sp = ep + 1;
   }

   LoadDocs();
   currentFolder = firms.end();
}

void OrgBalanceData::SetItemColor(NMLVCUSTOMDRAW *lvcd)
{
   if( IsTopLevel() )
      return;

   DWORD index = lvcd->nmcd.dwItemSpec;
   if( currentFolder != firms.end() && index < currentFolder->second.leafs.size() )
   {
      std::set<FirmData::DocData>::const_iterator li = currentFolder->second.leafs.begin();
      advance(li, index);
      const FirmData::DocData& dd = *li;
      if( dd.isCustom )
         lvcd->clrText = RGB(127, 127, 127);
   }
}

bool OrgBalanceData::SelectLeaf(int index)
{
   if( currentFolder == firms.end() || index >= (int)currentFolder->second.leafs.size() )
      return false;

   std::set<FirmData::DocData>::const_iterator li = currentFolder->second.leafs.begin();
   advance(li, index);
   const FirmData::DocData& dd = *li;

   PKOImpl *pko;
   PkoMap::iterator fnd = pkos.find(dd.number);
   if( fnd != pkos.end() )
   {
      pko = fnd->second;
      if( (pko->params & PKO::Exported) )
         return false;
   }
   else
   {
      pko = new PKOImpl();
      if( !pko->Init(orgRID) )
      {
         delete pko;
         return false;
      }
      pko->number = pko->holder.Add(dd.number.c_str());
      pko->dogId = pko->holder.Add(dd.dogId.c_str());
      pko->supplyer = pko->holder.Add(dd.supplyer.c_str());
      pko->fiscal = (dd.fiscal) ? 1 : 0;
      pko->date = dd.date;
   }
    
   EnterNumber dlg;
   dlg.value = dd.sum - pko->sum;
   dlg.title = L"Сумма";
   bool ret = (dlg.DoModal() == IDOK);
   if( ret )
   {
      if( dlg.value > dd.sum )
         dlg.value = dd.sum;
      
      pko->sum = dlg.value;
      pko->Write();
      docTypeManager.SumChanged(dtPKO, pko->id);


      if( fnd == pkos.end() )
         pkos[dd.number] = pko;
   } else
   {
      if( fnd == pkos.end() )
         delete pko;
   }
   
   return ret;
}

void OrgBalanceData::LoadFolderData(const TreeNode& folder)
{
   if( owner )
      ((OrgBalance*)owner)->FolderChanged();

   title.clear();
   currentFolder = firms.end();
   if( folder.id == NO_ROWID )
      return;

   int index = (int)folder.id;
   if( index < (int)firms.size() )
   {
      currentFolder = firms.begin();
      advance(currentFolder, index);

      title = currentFolder->second.name;
      int ri = 0;
      std::set<FirmData::DocData>::const_iterator di = currentFolder->second.leafs.begin();
      for( ; di != currentFolder->second.leafs.end(); di++, ri++ )
         leafs.push_back(ri);
   }
}

OrgBalance::OrgBalance()
{
}

DWORD OrgBalance::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
{
   ((OrgBalanceData*)data)->SetItemColor((NMLVCUSTOMDRAW*)lpNMCustomDraw);
   return CDRF_NOTIFYITEMDRAW;
}

class ODEx : public OrgDocsListData
{
public:
   ODEx(const wchar_t *org, const wchar_t* type) : OrgDocsListData(org, type) {}
};

bool OrgBalance::SetData(IFormData *_data)
{
   if( SQLFolderForm::SetDataEx(_data, 3) == false )
      return false;

   ODEx od(((OrgBalanceData*)_data)->ID(), dtBalance);
   orgInfo.Init(((OrgBalanceData*)_data)->OrgID(), *this, IDC_ORG_TITLE, IDC_ADDRESS_LABEL, IDC_CONTACTS, &od);

#ifdef ORG_NOTE
   LoadMenuBar(false);
   menuBar.EnableWindow(FALSE);
   OpenNote(m_hWnd, ((OrgDocsListData*)data)->ID(), true);
   menuBar.EnableWindow(TRUE);
#endif

   LoadMenuBar(true); // call UpdateLayout internal
   menuBar.EnableButton(IDC_ADD, (((OrgBalanceData*)_data)->IsTopLevel()) ? FALSE : TRUE);
   return true;
}

void OrgBalance::MouseDown(UINT flags, const CPoint &pt)
{
   orgInfo.SwitchInfo();

   UpdateLayout(false);
   Invalidate();
   UpdateWindow();
}

void OrgBalance::LoadMenuBar(bool hideSIP)
{
   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, (hideSIP) ? SHCMBF_HIDESIPBUTTON : 0));

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)dtBalance;
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   if( hideSIP )
   {
      sumLabel.CreateLabel(menuBar.m_hWnd);
      sumLabel.SetSum(((OrgDocsListData*)data)->GetSum());
   }

#ifdef ORG_NOTE
   {
      TBBUTTON button;
      button.iBitmap = 22;
      button.idCommand = IDC_NOTES;
      button.fsState = TBSTATE_ENABLED;
      button.fsStyle = TBSTYLE_BUTTON | TBSTYLE_AUTOSIZE;
      button.dwData = 0;
      button.iString = IDC_NOTES;

      menuBar.AddButtons(1, &button);
   }
#endif
}

LRESULT OrgBalance::SetCellInfo(LPNMHDR hdr)
{
   if( !orgInfo.CanHandle(hdr) )
      return SQLFolderForm::SetCellInfo(hdr);

   return orgInfo.SetCellInfo(hdr);
}

LRESULT OrgBalance::ItemSelected(LPNMHDR hdr)
{
   if( !orgInfo.CanHandle(hdr) )
      return SQLFolderForm::ItemSelected(hdr);

   if( orgInfo.Selecting(hdr) )
      Refresh();

   return TRUE;
}

LRESULT OrgBalance::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;

   const DocType *dt = SelectDocType(&menuBar, m_hWnd);
   if( dt != NULL && dt->Type() != dtBalance )
      dt->OpenForm(((OrgBalanceData*)data)->ID(), NULL);

   return 0;
}

LRESULT OrgBalance::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   OpenOrgList(dtBalance);
   return 0;
}

void OrgBalance::UpdateLayout(bool forceRecalc)
{
   int docsTop;
   CRect rc;

   // надо установить размер до вызова UpdateLayout
   GetParent().GetClientRect(rc);
   SetWindowPos(NULL, 0, 0, rc.right, rc.Height(), SWP_NOZORDER|SWP_NOOWNERZORDER);

   orgInfo.UpdateLayout(&docsTop, forceRecalc);

   SetListLayout(forceRecalc, docsTop);
   sumLabel.UpdateLayout();
   listCtrl.EnsureVisible(lastViewed, FALSE);
}

void OrgBalance::Paint(HDC dc)
{
   orgInfo.Paint(dc);
   SetMsgHandled(FALSE);
}

void OrgBalance::Refresh()
{
   SQLFolderForm::Refresh();
}

bool OrgBalance::CanSetColumn(int rowIndex, int colIndex) const
{
   if( ((OrgBalanceData*)data)->IsTopLevel() && colIndex == 1 ) return false;
   return true;
}

#ifdef ORG_NOTE
LRESULT OrgBalance::Notes(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   LoadMenuBar(false);
   menuBar.EnableWindow(FALSE);
   OpenNote(m_hWnd, ((OrgDocsListData*)data)->ID(), false);
   menuBar.EnableWindow(TRUE);
   LoadMenuBar(true);

   return 0;
}
#endif

class OrgDocAdd : public OrgDocsList
{
public:
   virtual DWORD GetResourceID() const { return IDD_ORG_DOCS; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_DOCS; }

   virtual void SetViewType(const DocType *newDT)
   {
      const wchar_t *nt = newDT->Type();
      if( nt != ((OrgDocsListData*)data)->GetDocType()->Type() != 0 )
      {
         if( nt != dtBalance /* && nt != dtDelivery*/ )
         {
            ((OrgDocsListData*)data)->SetDocType(nt);
            Refresh();
         } else
            OpenOrgDocs(((OrgDocsListData*)data)->ID(), nt);
      }
   }

   DECLARE_FORM(OrgDocAdd, IDD_ORG_DOCS_ADD)
};

IMPLEMENT_FORM(OrgDocAdd)

//void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
//{
//   if( type == dtBalance )
//   {
//      _Module.GetFrame()->Load(IDD_BALANCE, new OrgBalanceData(orgID));
//   } else
//   {
//      _Module.GetFrame()->Load(IDD_ORG_DOCS, new OrgDocsListData(orgID, type));
//   }
//}
//

void OpenOrgDocs(const wchar_t* orgID, const wchar_t* type)
{
   if( type == dtBalance /*|| type == dtDelivery*/ )
      _Module.GetFrame()->Load(IDD_BALANCE, new OrgBalanceData(orgID));
   else
      _Module.GetFrame()->Load(IDD_ORG_DOCS_ADD, new OrgDocsListData(orgID, type));
}
