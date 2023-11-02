/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Контакты
 *
 *  ert   19/11/2010   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocType.h>
#include "ObjImpl.h"
#include <ListForm.h>
#include "Contacts.h"
#include "PhoneDlg.h"
#include "OrgDocs.h"

//
//--------------------------- ContactData -------------------------------------
//
struct ContactItem : public IReflectableData
{
   wchar_t *name;
   wchar_t *phone;

   DECLARE_TYPE_REFLECTION(ContactItem);
};

BEGIN_TYPE_REFLECTION(ContactItem)
   REGISTER_STRING_MEMBER(ContactItem, name)
   REGISTER_STRING_MEMBER(ContactItem, phone)
END_TYPE_REFLECTION(ContactItem)

static ListFormData::Header contactHeader[] =
{
   { ListFormData::Header::Left, L"ФИО", L"name", 80 },
   { ListFormData::Header::Right, L"Телефон", L"phone", 40 },
};

const DataReflector& ContactData::DataType() const
{
   return ContactItem().GetType();
}

const ListFormData::Header *ContactData::GetHeader() const
{
   return contactHeader;
}

int ContactData::ColumnsCount() const
{
   return sizeof(contactHeader) / sizeof(contactHeader[0]);
}

#ifdef ORG_INFO

void ContactData::SetContacts(const std::vector<Contact> &contacts)
{
   std::vector<Contact>::const_iterator i = contacts.begin();

   this->contacts.clear();
   for( ; i != contacts.end(); i++ )
   {
      Contact c = (*i);
      c.name = sh.Add(c.name);
      c.phone = sh.Add(c.phone);

      this->contacts.push_back(c);
   }
}

bool ContactData::Selecting(int index)
{
   if( index >= (int)contacts.size() ) return false;

   const Contact &c = contacts[index];

   PhoneDlg phoneDlg(c.phone);
   DWORD ret = phoneDlg.DoModal();
   if( ret == IDC_CALL )
      MakeCall(c.phone);
   else if( ret == IDC_SMS )
   {
      const wchar_t *text = phoneDlg.Text();
      if( *text != L'\0' )
         SendSMS(c.phone, text);
   }

   return false;
}

bool ContactData::Get(IReflectableData* data, int index) const
{
   if( index >= (int)contacts.size() || index < 0 ) return false;

   const Contact &item = contacts[index];

   ((ContactItem*)data)->name = item.name;
   ((ContactItem*)data)->phone = item.phone;

   return true;
}

#endif

//
//--------------------------- OrgInfo -------------------------------------
//
OrgInfo::OrgInfo() : showInfo(false), contactData(NULL)
{
}

OrgInfo::OrgInfo(bool _showInfo) : showInfo(_showInfo), contactData(NULL)
{
}

OrgInfo::~OrgInfo()
{
   delete contactData;
}

void OrgInfo::InitContactData()
{
   contactData = new ContactData();
}

void OrgInfo::Init(const ROWID& id, CWindow parent, UINT titleID, UINT addressID, UINT contactsID, OrgDocsListData* data)
{
   InitContactData();

   //CStatic title(GetDlgItem(IDC_ORG_TITLE)), address(GetDlgItem(IDC_ADDRESS_LABEL));
   this->parent = parent;

   OrgImpl org;
   org.Read(id);

#ifdef ORG_INFO
   contactData->SetContacts(org.contacts);

   address = parent.GetDlgItem(addressID);
   address.SetWindowText(org.address);
#endif

   contactList.SubclassWindow(parent.GetDlgItem(contactsID));

   int imageID = (GetSystemMetrics(SM_CXSMICON) == 16) ? IDB_FOLDER : IDB_FOLDER32;
   contactList.Setup(2, contactData, imageID);

   std::wstring ttl;
   data->GetTitle(org, &ttl);
   title = parent.GetDlgItem(titleID);
   title.SetWindowTextW(ttl.c_str());
}

void OrgInfo::Paint(HDC)
{
   PAINTSTRUCT ps;
   HDC dc = parent.BeginPaint(&ps);

   int wdh = GetSystemMetrics(SM_CXSMICON);
   HICON hIco = (HICON)LoadImage(_Module.GetModuleInstance(), MAKEINTRESOURCE(IDD_ORG_INFO), IMAGE_ICON, wdh, wdh, 0);

   DrawIconEx(dc, wdh/8, wdh/8, hIco, wdh, wdh, 0, NULL, DI_NORMAL);
   DestroyIcon(hIco);

   if( showInfo )
   {
      CRect rc;
      title.GetWindowRect(rc);
      parent.ScreenToClient(rc);

      HPEN cpen = ::CreatePen(PS_SOLID,0,RGB(192,192,192));
      SelectObject(dc, cpen);
      MoveToEx(dc, 0, rc.bottom, NULL);
      LineTo(dc, rc.right, rc.bottom);
      DeleteObject(cpen);
   }

   parent.EndPaint(&ps);
}

void OrgInfo::UpdateLayout(int* contactsHeight, bool forceRecalc)
{
   if( parent.m_hWnd == NULL )
      return;

   CRect rc;
   parent.GetClientRect(rc);

   RECT bounds = {0};
   bounds.left = GetSystemMetrics(SM_CXSMICON);
   bounds.left += bounds.left / 4;
   bounds.right = rc.right-2;

   CalcTextHeight(title.m_hWnd, &bounds);

   // отступ для значка
   // height == bounds.left для квадратного отступа
   int height = bounds.bottom - bounds.top;
   if( height == 0 )
   {
      height = bounds.left; // высота по умолчанию
      bounds.bottom = bounds.top + height;
   } else 
   {
      if((title.GetStyle() & WS_BORDER) != 0)
         height += 8;
      if( height < bounds.left )
      {
         bounds.top = (bounds.left - height) / 2;
         height = bounds.left;
      }
   }

   title.MoveWindow(bounds.left, bounds.top, rc.right - bounds.left, height, FALSE);

   height += bounds.top;
   int docsTop = height + 1 + bounds.top;
   if( showInfo )
   {
      RECT b1 = {0};
      b1.right = rc.right;
      CalcTextHeight(address.m_hWnd, &b1);
      if( b1.bottom == b1.top )
      {
         b1.bottom = b1.top + height * 2;
      }
      if((address.GetStyle() & WS_BORDER) != 0)
         b1.bottom += 8;

      address.MoveWindow(b1.left, height + 1, rc.right - b1.left, b1.bottom - b1.top, FALSE);

      int top = b1.bottom + height + 2;

      docsTop = rc.Height()/2;

      rc.top = top;
      rc.bottom = docsTop - 1;
      contactList.SetLayout(forceRecalc, rc, contactData);
   }

   int show = (showInfo) ? SW_SHOW : SW_HIDE;
   address.ShowWindow(show);
   contactList.ShowWindow(show);

   *contactsHeight = docsTop;
}

bool OrgInfo::Selecting(LPNMHDR hdr)
{
   int index = ((NMLISTVIEW*)hdr)->iItem;
   return (index >= 0 && contactData->Selecting(index));
}

LRESULT OrgInfo::SetCellInfo(LPNMHDR hdr)
{
   NMLVDISPINFO *di = (NMLVDISPINFO*)hdr;
   if( (di->item.mask & LVIF_IMAGE) != 0 )
      di->item.iImage = 4;
 
   if( !(di->item.mask & LVIF_TEXT) )
      return TRUE;

   int index = di->item.iItem;
   const DataReflector& reflector = contactData->DataType();
   IReflectableData *rd = reflector.Create();
   if( contactData->Get(rd, index))
   {
      const MemberType &tp = reflector.Type(contactData->GetHeader()[di->item.iSubItem].field);
      tp.ToString(*rd, di->item.pszText, di->item.cchTextMax);
   } else
      *di->item.pszText = L'\0';

   delete rd;
   return TRUE;
}
