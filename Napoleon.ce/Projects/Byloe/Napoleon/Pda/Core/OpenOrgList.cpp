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

#include "OrgList.h"
#include "FldOrgs.h"
#include <DocType.h>

#include <PhoneDlg.h>

void OpenOrgList()
{
   OpenOrgList(dtOrder);
}

struct OrgListDataAdd : public OrgListData
{
   COLORREF GetItemColor(int index, COLORREF defaultColor) const;

   bool GetPhone(std::wstring *phone, int index) const;
};

class OrgListAdd : public OrgList, public CCustomDraw<OrgListAdd>
{
 public:
   OrgListAdd();

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDD_ORG_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_ORG_LIST; }

   BEGIN_MSG_MAP(OrgListAdd)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, DoSelect)
      CHAIN_MSG_MAP(CCustomDraw<OrgListAdd>)      
      CHAIN_MSG_MAP(OrgList)
   END_MSG_MAP()

   DECLARE_FORM(OrgListAdd, IDD_ORG_LIST_ADD)

   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/)
   {
      return CDRF_NOTIFYITEMDRAW;
   }

   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

   virtual LRESULT SetCellInfo(LPNMHDR hdr);
      
protected:
   virtual int ImageListID(ListViewMultiLine *list) const;
   LRESULT DoSelect(LPNMHDR hdr);
};

IMPLEMENT_FORM(OrgListAdd)

COLORREF OrgListDataAdd::GetItemColor(int index, COLORREF defaultColor) const
{
   OrgImpl org;
   if( !org.Read(GetOID(index)) )
      return defaultColor;

   if( (org.flags & ofStopList) != 0 ) return RGB(192,192,192);
   return defaultColor;
}

bool OrgListDataAdd::GetPhone(std::wstring *phone, int index) const
{
   OrgImpl org;
   if( !org.Read(GetOID(index)) )
      return false;

   *phone = org.phone;
   return true;
}

OrgListAdd::OrgListAdd()
{
}

LRESULT OrgListAdd::DoSelect(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;

   if( index >= 0 )
   {
      CRect bounds;
      listCtrl.GetItemRect(index, bounds, LVIR_ICON);
      if( bounds.PtInRect(((NMLISTVIEW*)hdr)->ptAction) )
      {
         std::wstring phone;
         if( ((OrgListDataAdd*)data)->GetPhone(&phone, index) && phone.size() > 0 )
         {
            PhoneDlg phoneDlg(phone.c_str());
            DWORD ret = phoneDlg.DoModal();
            if( ret == IDC_CALL )
               MakeCall(phone.c_str());
            else if( ret == IDC_SMS )
            {
               const wchar_t *text = phoneDlg.Text();
               if( *text != L'\0' )
                  SendSMS(phone.c_str(), text);
            }
         }
      } else
      {
         if( ((OrgListDataAdd*)data)->Selecting(index) )
         {
            listCtrl.SetItemState(index, 0, LVIS_SELECTED);
            listCtrl.RedrawItems(index, index);
         }
      }
   }
   return TRUE;
}

LRESULT OrgListAdd::SetCellInfo(LPNMHDR hdr)
{
   if( OrgList::SetCellInfo(hdr) == FALSE )
      return FALSE;

   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( di->item.mask & LVIF_IMAGE )
   {
      std::wstring phone;
      if( ((OrgListDataAdd*)data)->GetPhone(&phone, di->item.iItem) && phone.size() > 0 )
         di->item.iImage = 0;
      else
         di->item.iImage = -1;
   }

   return TRUE;
}

int OrgListAdd::ImageListID(ListViewMultiLine *list) const
{
   CImageList il;
   HBITMAP bmp;
   if( GetSystemMetrics(SM_CXSMICON) == 16 )
   {
      il.Create(16, 16, ILC_COLORDDB, 1, 0);
      bmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDC_PHONE));
      il.Add(bmp);
   } else
   {
      il.Create(32, 32, ILC_COLORDDB, 1, 0);
      bmp = LoadBitmap(_Module.GetResourceInstance(), MAKEINTRESOURCE(IDC_PHONE1));
      il.Add(bmp);
   }

   list->SetImageList(il, LVSIL_SMALL);
   DeleteObject((HGDIOBJ)bmp);

   return -1;
}

DWORD OrgListAdd::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)   
{
   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
   lvcd->clrText = ((OrgListDataAdd*)data)->GetItemColor(lvcd->nmcd.dwItemSpec, listCtrl.GetTextColor());
   return CDRF_NOTIFYITEMDRAW;
}

bool OrgListAdd::SetData(IFormData *_data)
{
   if( OrgList::SetData(_data) == false ) return false;

   return true;
}

void OpenOrgFolders(const wchar_t *type)
{
#ifdef SHEDULE
   SheduleData::ClearShedule();

   OrgFoldersData *od = new SheduleData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_SHEDULE, od);
#else
   OrgFoldersData *od = new OrgFoldersData();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_FLDORGS, od);
#endif
}

void OpenOrgList(const wchar_t* type)
{
   OrgFolderImpl of;
   SQLTable table(of.Name());
#ifdef SHEDULE
   SheduleData::ClearShedule();
#endif

   if( SQLTable::IsTableExist(of.Name()) && table.Count() != 0 )
   {
//#ifdef SHEDULE
      Preference prf;
      prf.Load();
      if( prf.flags & opfCalendarView )
      {
         OpenOrgFolders(type);
         return;
      }
//#else
//      OpenOrgFolders(type);
//      return;
//#endif
   }

   OrgListData *od = new OrgListDataAdd();
   od->SetDocType(type);
   _Module.GetFrame()->Load(IDD_ORG_LIST_ADD, od);
}
