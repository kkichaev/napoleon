/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Отчеты
 *
 *  ert   11/04/2008   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <FolderForm.h>
#include <DocType.h>
#include <FormEntries.h>
#include <Progress.h>
#include <Sync.h>
#include <Table.h>
#include "Reports.h"
#include <NplConfig.h>
#include <StdFuncs.h>

#include <SumLabel.h>

#define BRANDS_STR L"Brands"

struct ReportItem : public IReflectableData
{
   const wchar_t *name;
   DWORD    value; // QTY_SCALE

   DECLARE_TYPE_REFLECTION(ReportItem)
};

BEGIN_TYPE_REFLECTION(ReportItem)
   REGISTER_STRING_MEMBER(ReportItem, name)
   REGISTER_ULONG_SCALE_MEMBER2(ReportItem, value, QTY_SCALE, true)
END_TYPE_REFLECTION(ReportItem)

struct ReportType
{
   ReportType(const wchar_t *_name, CEOID orgID = 0);

   const wchar_t *name;

   FILETIME from;
   FILETIME till;

   DWORD sum; // QTY_SCALE
   CEOID orgID;
   
   bool fromOrders;

   virtual void Doing(IProgressIndicator *pi) = 0;
   virtual void LoadFolder(CEOID id, std::vector<CEOID> *folders, std::vector<CEOID> *leafs) = 0;
   virtual bool GetFolder(ReportItem *item, int index) = 0;
   virtual bool GetLeaf(ReportItem *item, int index) = 0;
   DWORD Sum() const { return sum; }

   virtual void UpdateDataFrom(CETable *priceTable, const IReflectableData &item) = 0;

   struct Param
   {
      ReportType *data;
      IProgressIndicator *pi;
   };
   static DWORD DoReport(Param *param);

};

struct SKU : public ReportType
{
   SKU() : ReportType(L"SKU") {}

   virtual void Doing(IProgressIndicator *pi);
   virtual void LoadFolder(CEOID id, std::vector<CEOID> *folders, std::vector<CEOID> *leafs);
   virtual bool GetFolder(ReportItem *item, int index);
   virtual bool GetLeaf(ReportItem *item, int index);
   virtual void UpdateDataFrom(CETable *priceTable, const IReflectableData &item);

   void Add(const Price &price, CEOID orgID);

   struct Data
   {
      std::wstring name;
      std::set<CEOID> clients;
      DWORD qty;

      bool operator < (const Data &ref) const { return name < ref.name; }
   };
   std::set<Data> data;
};

struct Clients : public ReportType
{
   Clients() : ReportType(L"Клиенты") {}

   virtual void Doing(IProgressIndicator *pi);
   virtual void LoadFolder(CEOID id, std::vector<CEOID> *folders, std::vector<CEOID> *leafs);
   virtual bool GetFolder(ReportItem *item, int index);
   virtual bool GetLeaf(ReportItem *item, int index);
   virtual void UpdateDataFrom(CETable *priceTable, const IReflectableData &item);

   struct Client
   {
      CEOID oid;
      std::wstring name;

      bool operator < (const Client &ref) const
      {
         int cmp = name.compare(ref.name);
         if( cmp < 0 ) return true;
         if( cmp > 0 ) return false;
         return oid < ref.oid; 
      }
   };

   std::set<Client> clients;
};

struct Sales : public ReportType
{
   Sales(CEOID orgID) : ReportType(L"Продажи", orgID), curFolder(NULL) { }

   virtual void Doing(IProgressIndicator *pi);
   virtual void LoadFolder(CEOID id, std::vector<CEOID> *folders, std::vector<CEOID> *leafs);
   virtual bool GetFolder(ReportItem *item, int index);
   virtual bool GetLeaf(ReportItem *item, int index);
   virtual void UpdateDataFrom(CETable *priceTable, const IReflectableData &item);

   struct Data
   {
      CEOID item;
      std::wstring name;
      DWORD value;

      bool operator< (const Data &ref) const { return name < ref.name; }
   };

   typedef std::set<Data> SalesData;
   struct Folder
   {
      std::wstring name;
      //std::vector<Data> data;
      SalesData data;

      DWORD SumValue() const;
      void AddItem(const Data &d);
   };

   void Add(const Price &price, DWORD qty, CEOID id);

   std::vector<Folder>::iterator FindFolder(const Data &item);

   std::vector<Folder> folders;
   Folder* curFolder;
};

struct ReportsData : FolderFormData
{
   ReportsData(CEOID orgID);
   ~ReportsData();

   void Init(IProgressIndicator *pi)
   { 
      if( upFolders.size() == 0 )
      {
         Doing(pi);
         LoadFolder(0); 
      }
   }

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const { return ReportItem().GetType(); }
   virtual bool Get(IReflectableData* data, int index) const;

   const ReportType& Type() const { return *report; }
   void SetType(ReportType *rt) { delete report; report = rt; }

   const FILETIME& Start() const { return report->from; }
   void SetStart(SYSTEMTIME &st) { SystemTimeToFileTime(&st, &report->from); }

   const FILETIME& End() const { return report->till; }
   void SetEnd(SYSTEMTIME &st) { SystemTimeToFileTime(&st, &report->till); }

   virtual void LoadFolder(CEOID oid);
   virtual bool SelectLeaf(int index);
   virtual DWORD Sum() const;
   virtual bool MoveToFolder(bool next);

   void Doing(IProgressIndicator *pi) { report->sum = 0; report->Doing(pi); }

   CEOID OrgID() const { return report->orgID; }

   ReportsData* Clone();
protected:
   ReportType *report;
};

class Reports : public FolderForm
{
public:
   Reports();

   virtual bool SetData(IFormData *_data);
   virtual void UpdateLayout(bool forceRecalc);

   virtual DWORD GetResourceID() const { return IDD_REPORTS; }
   virtual DWORD GetMenuBarID() const { return IDD_REPORTS; }

   BEGIN_MSG_MAP(Reports)
      COMMAND_ID_HANDLER(IDD_REPORTS, DoReports)
      COMMAND_ID_HANDLER(IDC_CLOSE, Closing)
      COMMAND_ID_HANDLER(IDC_SHOW_2_ROW, ChangeRows)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, SetViewType)
      CHAIN_MSG_MAP(FolderForm)
   END_MSG_MAP()

   LRESULT ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT DoReports(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT SetViewType(int id, LPNMHDR header, BOOL &handled);

   DECLARE_FORM(Reports, IDC_REPORTS)

protected:
   virtual void Refresh();

protected:
   SumLabel sumLabel;
};

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"", L"name", 80 },
   { ListFormData::Header::Right, L"", L"value", 50 }
};

const ListFormData::Header* ReportsData::GetHeader() const
{
   return header;
}

ReportType::ReportType(const wchar_t *_name, CEOID orgID) : name(_name)
{
   sum = 0;
   this->orgID = orgID;

   SYSTEMTIME st;
   GetLocalTime(&st);

   ResetTime(&st);
   SystemTimeToFileTime(&st, &till);
   st.wDay = 1;
   SystemTimeToFileTime(&st, &from);

   fromOrders = false;
}

DWORD ReportType::DoReport(Sales::Param *param)
{
   SyncFormat &sf = (param->data->fromOrders) ? (SyncFormat&)SyncOrder() : (SyncFormat&)SyncDelivery();

   CEDBFormat format(sf);
   CETable table(format);
   if( table.Open(sf.FileName()) == false ) return 0;

   SyncPrice sp;
   CEDBFormat priceFormat(sp);
   CETable priceTable(priceFormat);
   priceTable.Open(sp.FileName());

   param->pi->SetText(L"Обработка...");

   const DataReflector &reflector = table.DataType();
   const MemberType &dateT = reflector.Type(L"date");
   IReflectableData *data = reflector.Create();

   if( param->data->orgID == 0 )
   {
      param->pi->SetMax(table.Count());
      int ctr = 0;
      CEOID oid = table.SetPos(0);
      while( oid != NULL )
      {
         table.GetCurrent(data);

         FILETIME *ft = (FILETIME*)dateT.GetValue(*data);
         if( CompareFileTime(ft, &param->data->from) >= 0 && CompareFileTime(ft, &param->data->till) <= 0 )
            param->data->UpdateDataFrom(&priceTable, *data);

         param->pi->SetPos(++ctr);
         oid = table.MoveNext(true);
      }
   } else
   {
      const DocType *dt = docTypeManager.GetDocType((param->data->fromOrders) ? dtOrder : dtDelivery);

      OrgDocs orgDocs;
      dt->GetDocuments(param->data->orgID, &orgDocs);
      std::vector<CEOID>::iterator i = orgDocs.documents.begin();

      param->pi->SetMax(orgDocs.documents.size());
      int ctr = 0;
      for( ; i != orgDocs.documents.end(); i ++ )
      {
         table.Seek((*i));
         table.GetCurrent(data);

         FILETIME *ft = (FILETIME*)dateT.GetValue(*data);
         if( CompareFileTime(ft, &param->data->from) >= 0 && CompareFileTime(ft, &param->data->till) <= 0 )
            param->data->UpdateDataFrom(&priceTable, *data);

         param->pi->SetPos(++ctr);
      }
   }

   delete data;
   return 0;
}

//
// --------------------------------- SKU -------------------------------
//

void SKU::Doing(IProgressIndicator *pi)
{
   data.clear();

   Param param;
   param.data = this;
   param.pi = pi;
   HANDLE ht = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReport, &param, 0, NULL);
   _Module.WaitThreadComplete(ht);
}

void SKU::LoadFolder(CEOID oid, std::vector<CEOID> *vfolders, std::vector<CEOID> *leafs)
{
   if( oid == 0 )
   {
      int index = 1;
      std::set<Data>::const_iterator i = data.begin();
      for( ; i != data.end(); i++ )
         leafs->push_back(index++);
   }
}

bool SKU::GetFolder(ReportItem *item, int index)
{
   return false;
}

bool SKU::GetLeaf(ReportItem *item, int index)
{
   if( (unsigned)index >= data.size() ) return false;

   std::set<Data>::const_iterator i = data.begin();
   while( index-- > 0 )
      i++;

   item->name = i->name.c_str();
   item->value = i->qty;
   return true;
}

void SKU::Add(const Price &price, CEOID orgID)
{
   Data d;
   d.name = price.name;
   d.qty = QTY_SCALE;

   std::set<Data>::iterator fnd = data.find(d);
   if( fnd == data.end() )
   {
      d.clients.insert(orgID);
      data.insert(d);

      sum += QTY_SCALE;
   } else
   {
      std::set<CEOID>::iterator orgFnd = fnd->clients.find(orgID);
      if( orgFnd == fnd->clients.end() )
      {
         fnd->qty += QTY_SCALE;
         fnd->clients.insert(orgID);

         sum += QTY_SCALE;
      }
   }
}

void SKU::UpdateDataFrom(CETable *priceTable, const IReflectableData &item)
{
   if( fromOrders )
   {
      const Order &ord = (const Order&)item;
      vector_t<OrderItem>::const_iterator i = ord.items.begin();

      for( ; i != ord.items.end(); i ++ )
      {
         Price price;

         priceTable->Seek(i->id);
         priceTable->GetCurrent(&price);

         Add(price, ord.id);
      }
   } else
   {
      const Delivery &dlv = (const Delivery&)item;
      vector_t<DeliveryItem>::const_iterator i = dlv.items.begin();

      for( ; i != dlv.items.end(); i ++ )
      {
         Price price;

         priceTable->Seek(i->id);
         priceTable->GetCurrent(&price);

         Add(price, dlv.id);
      }
   }
}

//
// --------------------------------- Clients -------------------------------
//
void Clients::Doing(IProgressIndicator *pi)
{
   clients.clear();
   //qty = 0;

   Param param;
   param.data = this;
   param.pi = pi;
   HANDLE ht = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReport, &param, 0, NULL);
   _Module.WaitThreadComplete(ht);
}

void Clients::LoadFolder(CEOID oid, std::vector<CEOID> *vfolders, std::vector<CEOID> *leafs)
{
   if( oid == 0 )
      vfolders->push_back(1);
   else if( oid == 1 )
   {
      for( int i=1; i<=(int)clients.size(); i++ )
         leafs->push_back(i);
   }
}

bool Clients::GetFolder(ReportItem *item, int index)
{
   if( index == 0 )
   {
      item->name = L"Число активных клиентов";
      item->value = clients.size() * QTY_SCALE;
      return true;
   }
   return false;
}

bool Clients::GetLeaf(ReportItem *item, int index)
{
   std::set<Client>::const_iterator i = clients.begin();
   while( index-- > 0 ) i++;

   item->name = i->name.c_str();
   item->value = QTY_SCALE;
   return true;
}

void Clients::UpdateDataFrom(CETable *priceTable, const IReflectableData &item)
{
   //CEOID id;
   Client cl;
   if( fromOrders )
   {
      const Order &ord = (const Order&)item;
      cl.oid = ord.id;
   } else
   {
      const Delivery &dlv = (const Delivery&)item;
      cl.oid = dlv.id;
   }

   Org org;
   SyncOrg so;
   CEDBFormat format(so);
   CETable table(format);
   table.Open(so.FileName());
   table.Seek(cl.oid);
   table.GetCurrent(&org);
   cl.name = org.name;

   if( clients.find(cl) == clients.end() )
   {
      clients.insert(cl);
      sum += QTY_SCALE;
   }
}

//
// --------------------------------- Sales -------------------------------
//
void Sales::Doing(IProgressIndicator *pi)
{
   folders.clear();

   std::wstring val;
   NapoleonConfig npl;
   std::wstring::size_type off = 0, nextOff;

   npl.ReadValue(&val, BRANDS_STR);
   while( true )
   {
      nextOff = val.find(SEP_SYM, off);

      Folder f;
      f.name = val.substr(off, (nextOff != std::wstring::npos) ? 
            nextOff - off : std::wstring::npos);

      folders.push_back(f);
      if( nextOff == std::wstring::npos )
         break;
      off = nextOff + 1;
   }

   Param param;
   param.data = this;
   param.pi = pi;
   HANDLE ht = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)DoReport, &param, 0, NULL);
   _Module.WaitThreadComplete(ht);
}

std::vector<Sales::Folder>::iterator Sales::FindFolder(const Data &item)
{
   std::vector<Folder>::iterator i = folders.begin();
   for( ; i != folders.end(); i++ )
   {
      if( item.name.find(i->name) != std::wstring::npos )
         return i;
   }
   return i;
}

void Sales::LoadFolder(CEOID oid, std::vector<CEOID> *vfolders, std::vector<CEOID> *leafs)
{
   if( oid == 0 )
   {
      curFolder = NULL;
      for( unsigned i=1; i<=folders.size(); i++ )
         vfolders->push_back(i);
   } else
   {
      curFolder = &folders[oid-1];

      SalesData::const_iterator i = curFolder->data.begin();
      for( ; i != curFolder->data.end(); i++ )
         leafs->push_back(i->item);
   }
}

DWORD Sales::Folder::SumValue() const
{
   DWORD sum = 0;
   SalesData::const_iterator i = data.begin();
   for( ; i != data.end(); i++ )
      sum += i->value;
   return sum;
}

void Sales::Folder::AddItem(const Sales::Data &d)
{
/*
   for( int i = data.size()-1; i >= 0; i-- )
   {
      Sales::Data &cd = data[i];
      
      if( cd.name == d.name )
      {
         cd.value += d.value;
         return;
      }
   }

   data.push_back(d);
   */
   SalesData::iterator fnd = data.find(d);
   if( fnd == data.end() )
      data.insert(d);
   else
      fnd->value += d.value;
}

bool Sales::GetFolder(ReportItem *item, int index)
{
   if( (unsigned)index >= folders.size() ) return false;

   Folder& f = folders[index];
   item->name = f.name.c_str();
   item->value = f.SumValue();

   return true;
}

bool Sales::GetLeaf(ReportItem *item, int index)
{
   if( curFolder == NULL || (unsigned)index >= curFolder->data.size() ) return false;

   SalesData::const_iterator i = curFolder->data.begin();
   while( index-- > 0 ) i++;

   item->name = i->name.c_str();
   item->value = i->value;
   return true;
}

void Sales::Add(const Price &price, DWORD qty, CEOID id)
{
   if( id == 0 )
      return;

   Data d;
   d.item = id;
   d.name = price.name;
   if( price.weight != 0 )
      d.value = MulInPack(price.weight, qty, WEIGHT_SCALE);
   else
      d.value = qty;

   std::vector<Folder>::iterator folder = FindFolder(d);
   if( folder == folders.end() )
      return;
   /*
   {
      Folder f;
      f.name = d.name;
      folder = folders.insert(folders.end(), f);
   }
   */
   (*folder).AddItem(d);
   sum += d.value;
}

void Sales::UpdateDataFrom(CETable *priceTable, const IReflectableData &item)
{
   if( fromOrders )
   {
      const Order &ord = (const Order&)item;
      vector_t<OrderItem>::const_iterator i = ord.items.begin();

      for( ; i != ord.items.end(); i ++ )
      {
         Price price;

         priceTable->Seek(i->id);
         priceTable->GetCurrent(&price);

         Add(price, i->qty, i->id);
      }
   } else
   {
      const Delivery &dlv = (const Delivery&)item;
      vector_t<DeliveryItem>::const_iterator i = dlv.items.begin();

      for( ; i != dlv.items.end(); i ++ )
      {
         Price price;

         priceTable->Seek(i->id);
         priceTable->GetCurrent(&price);

         Add(price, i->qty, i->id);
      }
   }
}

//
// ---------------------------- ReportsData ------------------------------
//
ReportsData::ReportsData(CEOID orgID)
{
   report = new Sales(orgID);
   //report = new Clients();
   //report = new SKU();
}

ReportsData::~ReportsData()
{
   delete report;
}

void ReportsData::LoadFolder(CEOID oid)
{
   folders.clear();
   leafs.clear();

   if( oid == 0 )
   {
      upFolders.clear();
      upFolders.push_back(0);
   }
   report->LoadFolder(oid, &folders, &leafs);
}

ReportsData* ReportsData::Clone()
{
   ReportsData *data = new ReportsData(report->orgID);

   data->SetType(report);
   report = NULL;

   data->upFolders = upFolders;
   data->folders = folders;
   data->leafs = leafs;

   return data;
}

DWORD ReportsData::Sum() const
{
   return report->Sum();
}

bool ReportsData::MoveToFolder(bool next)
{
   return false;
}

bool ReportsData::SelectLeaf(int index)
{
   return false;
}

int ReportsData::ColumnsCount() const
{
   return sizeof(header)/sizeof(header[0]);
}

bool ReportsData::Get(IReflectableData* data, int index) const
{
   if( (unsigned)index < folders.size() )
      return report->GetFolder((ReportItem*)data, index);
   return report->GetLeaf((ReportItem*)data, index - folders.size());
}

//
// ----------------------------------- Reports ---------------------------------
//
Reports::Reports() : sumLabel(QTY_SCALE)
{
}

bool Reports::SetData(IFormData *_data)
{
   Preference p;
   p.Load();

   int scale = (p.flags & ppfOrgRow2) ? 2 : 1;
   if( FolderForm::SetDataEx(_data, scale) == false ) return false;

   menuBar = _Module.GetFrame()->LoadMenuBar(GetMenuBarID());

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;
   bi.pszText = (LPWSTR)((ReportsData*)data)->Type().name;
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);

   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (scale > 1) ? 13 : 12;
   menuBar.SetButtonInfo(IDC_SHOW_2_ROW, &bi);

   sumLabel.CreateLabel(menuBar.m_hWnd);

   CDateTimePickerCtrl wnd(GetDlgItem(IDC_DATE_BEGIN));

   SYSTEMTIME st;
   FileTimeToSystemTime(&((ReportsData*)data)->Start(), &st);
   wnd.SetSystemTime(GDT_VALID, &st);
   FileTimeToSystemTime(&((ReportsData*)data)->End(), &st);
   ((CDateTimePickerCtrl)GetDlgItem(IDC_DATE_END)).SetSystemTime(GDT_VALID, &st);

   ProgressWindow pw;
   pw.CreateSTDWindow(GetActiveWindow());
   ((ReportsData*)data)->Init(&pw);
   //((ReportsData*)data)->Doing(&pw);
   pw.DestroyWindow();

   UpdateLayout(false);

   Refresh();
   return true;
}

LRESULT Reports::ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

   bool orgRow2 = !((p.flags & ppfOrgRow2) != 0);
   if( orgRow2 ) p.flags |= ppfOrgRow2;
   else p.flags &= (~ppfOrgRow2);

   p.Save();

   ReportsData *d = ((ReportsData*)data)->Clone();
   _Module.GetFrame()->Load(GetID(), d);

   return 0;
}

void Reports::UpdateLayout(bool forceRecalc)
{
   CDateTimePickerCtrl wnd(GetDlgItem(IDC_DATE_BEGIN));
   CRect bnds;
   wnd.GetWindowRect(bnds);

   ScreenToClient(bnds);
   int hgh = bnds.bottom;

   CWindow ob(GetDlgItem(IDD_INVOICE));
   ob.GetWindowRect(bnds);
   ScreenToClient(bnds);
   ob.MoveWindow(bnds.left, (hgh-bnds.Height())/2, bnds.Width(), bnds.Height());

   ob.ShowWindow(SW_HIDE);

   SetListLayout(forceRecalc, hgh);
}


LRESULT Reports::DoReports(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   SYSTEMTIME st;
   CDateTimePickerCtrl dt(GetDlgItem(IDC_DATE_BEGIN));
   dt.GetSystemTime(&st);
   ((ReportsData*)data)->SetStart(st);

   ((CDateTimePickerCtrl)GetDlgItem(IDC_DATE_END)).GetSystemTime(&st);
   ((ReportsData*)data)->SetEnd(st);

   ProgressWindow pw;
   pw.CreateSTDWindow(GetActiveWindow());
   ((ReportsData*)data)->Doing(&pw);
   pw.DestroyWindow();

   ((ReportsData*)data)->LoadFolder(0);
   Refresh();

   return 0;
}

LRESULT Reports::Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( ((ReportsData*)data)->OrgID() == 0 )
      OpenOrgList(dtOrder);
   else
      OpenOrgDocs(((ReportsData*)data)->OrgID(), dtOrder);
   return 0;
}

LRESULT Reports::SetViewType(int id, LPNMHDR header, BOOL &handled)
{
   if( ((NMTOOLBAR*)header)->iItem != IDC_VIEW_TYPE ) return 0;
   if( ((ReportsData*)data)->OrgID() != 0 ) return 0;

   CRect menuBounds;
   menuBar.GetRect(IDC_VIEW_TYPE, menuBounds);
   menuBar.ClientToScreen(menuBounds);

   HMENU hm = CreatePopupMenu();

   int ctr = 1;
   UINT flag = MF_STRING;
   std::wstring name;
   name = L"&";
   name += Sales(0).name;
   AppendMenu(hm, flag, ctr++, name.c_str());

   name = L"&";
   name += Clients().name;
   AppendMenu(hm, flag, ctr++, name.c_str());

   name = L"&";
   name += SKU().name;
   AppendMenu(hm, flag, ctr++, name.c_str());

   int res = TrackPopupMenuEx(hm,  TPM_RETURNCMD | TPM_BOTTOMALIGN, 
      menuBounds.left, menuBounds.top, m_hWnd, NULL);
   DestroyMenu(hm);

   if( res == 0 ) return NULL;

   TBBUTTONINFO bi;
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_TEXT;

   ReportType *rt;
   switch( res )
   {
   case 1:
      rt = new Sales(0);
      break;
   case 2:
      rt = new Clients();
      break;
   default:
      rt = new SKU();
      break;
   }

   bi.pszText = (LPWSTR)rt->name;
   menuBar.SetButtonInfo(IDC_VIEW_TYPE, &bi);
   ((ReportsData *)data)->SetType(rt);

   return DoReports(0, 0, m_hWnd, handled);
}

void Reports::Refresh()
{
   FolderForm::Refresh();
   sumLabel.SetSum(((ReportsData*)data)->Sum(), true);
}

IMPLEMENT_FORM(Reports)

void OpenReports(CEOID orgID)
{
   _Module.GetFrame()->Load(IDC_REPORTS, new ReportsData(orgID));
}
