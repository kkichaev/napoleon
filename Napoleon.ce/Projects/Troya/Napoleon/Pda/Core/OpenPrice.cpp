/*
 * Copyright (C), 2007, Денис Мосягин
 *
 *  Процедура открытия прайс-листа
 *
 *  ert   06/02/2008   creating
 */
#include "stdafx.h"

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "Price.h"
#include "PriceForm.h"
#include "FormEntries.h"
#include <Table.h>

struct PriceDataAdd : public PriceFormData
{
   PriceDataAdd(OrderImpl *order);
   PriceDataAdd(OrderImpl* _order, CEOID upFolder);

   CEOID UpFolder() const { return (upFolders.size()) ? upFolders.back() : 0; }

   virtual COLORREF GetItemColor(int index) const;

   WORD orgKind;

 protected:
   void SetOrgKind(OrderImpl *order);
};

PriceDataAdd::PriceDataAdd(OrderImpl *order) : PriceFormData(order)
{
   SetOrgKind(order);
}

PriceDataAdd::PriceDataAdd(OrderImpl* _order, CEOID upFolder) : PriceFormData(_order)
{
   SetOrgKind(order);

   if( upFolder != NULL )
   {
      CETable folderTable(folderFormat);
      folderTable.Open(syncFolder.FileName());
      folderTable.SetTag(L"sort", true);

      folderTable.Seek(upFolder);
      MakeUpFolders(upFolder, &folderTable);
      LoadFolder(upFolder);
   }
}

void PriceDataAdd::SetOrgKind(OrderImpl *order)
{
   if( order != NULL )
   {
      Org org;
      SyncOrg so;
      CEDBFormat format(so);
      CETable table(format);

      table.Open(so.FileName());
      table.Seek(order->id);
      table.GetCurrent(&org);

      orgKind = org.kind;
   } else
      orgKind = 0;
}

COLORREF PriceDataAdd::GetItemColor(int index) const
{
   COLORREF clr = PriceFormData::GetItemColor(index);
   index -= folders.size();
   if( clr != textColor || index < 0 ) return clr;

   CETable priceTable(priceFormat);
   priceTable.Open(syncPrice.FileName());
   priceTable.Seek(leafs[index]);
   priceTable.GetCurrent(pItem);

   if( (pItem->types & orgKind) != 0 ) return RGB(0,0,192);
   return textColor;
}

class PriceFormAdd : public PriceForm
{
public:
   PriceFormAdd();

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST; }

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_SHOW_2_ROW, ChangeRows)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

protected:
   LRESULT ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
};

PriceFormAdd::PriceFormAdd()
{
}

IMPLEMENT_FORM(PriceFormAdd)

bool PriceFormAdd::SetData(IFormData *_data)
{
   if( PriceForm::SetData(_data) == false )
      return false;

   Preference p;
   p.Load();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (p.flags & ppfPriceRow2) ? 13 : 12;
   menuBar.SetButtonInfo(IDC_SHOW_2_ROW, &bi);

   return true;
}

LRESULT PriceFormAdd::ChangeRows(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Preference p;
   p.Load();

   bool orgRow2 = !((p.flags & ppfPriceRow2) != 0);
   if( orgRow2 ) p.flags |= ppfPriceRow2;
   else p.flags &= (~ppfPriceRow2);

   p.Save();

   OrderImpl *order = ((PriceDataAdd*)data)->UnbindOrder();
   CEOID upFolder = ((PriceDataAdd*)data)->UpFolder();

   PriceFormData *pfd = new PriceDataAdd(order, upFolder);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
   return 0;
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}

