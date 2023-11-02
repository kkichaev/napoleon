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

#include "PriceForm.h"
#include "FormEntries.h"

#include <EnterNumber.h>
#include <StdFuncs.h>

class EnterQty : public EnterNumberT<IDD_ENTER_QTY, QTY_SCALE, true>
{
public:
   EnterQty() { inPack = false; }

   virtual void Save()
   {
      inPack = (IsDlgButtonChecked(IDC_PACK) == BST_CHECKED);
   }

   bool inPack;
};

struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order, IPriceSelect* selector = NULL) : 
      PriceFormData(_order, selector), packetInput(false)
   {
   }

   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder, IPriceSelect* selector = NULL) : 
      PriceFormData(_order, upFolder, selector), packetInput(false)
   {
   }

   virtual PriceFormData* Clone();

   bool SwitchInput();

   virtual COLORREF GetItemColor(int index) const;
   virtual bool SelectLeaf(int index);

   bool packetInput;
   std::set<ROWID> packetSet;
};

class PriceFormAdd : public PriceForm
{
public:
   PriceFormAdd() {}

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }
   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST_ADD; }
   virtual DWORD GetMenuID() const { return IDD_PRICE_LIST; }

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_PACKET_INPUT, PacketInput)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

protected:

   LRESULT PacketInput(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   virtual void LoadMenuBar();
};

IMPLEMENT_FORM(PriceFormAdd)

PriceFormData* PriceFormDataAdd::Clone()
{
   OrderImpl *order = UnbindOrder();
   ROWID upFolder = UpFolder();

   PriceFormData *pfd = new PriceFormDataAdd(order, upFolder);
   return pfd;
}

COLORREF PriceFormDataAdd::GetItemColor(int index) const
{
   int ci = index - folders.size();
   if( ci >= 0 )
   {
      ROWID id = leafs[ci];
      if( packetSet.find(id) != packetSet.end() )
         return selectColor;
   }
   return PriceFormData::GetItemColor(index);
}

bool PriceFormDataAdd::SelectLeaf(int index)
{
   if( !packetInput ) return PriceFormData::SelectLeaf(index);

   ROWID id = leafs[index];
   std::set<ROWID>::iterator fnd = packetSet.find(id);
   if( fnd == packetSet.end() ) packetSet.insert(id);
   else packetSet.erase(fnd);

   return true;
}

bool PriceFormDataAdd::SwitchInput()
{
   if( order == NULL ) return false;
   packetInput = !packetInput;

   if( packetInput ) return false;

   if( packetSet.size() == 0 ) return false;

   EnterQty dlg;
   if( dlg.DoModal() )
   {
      DWORD value = dlg.value;
      std::set<ROWID>::const_iterator i = packetSet.begin();
      PriceImpl pi;
      for( ; i != packetSet.end(); i++ )
      {
         QTYData q;
         pi.Read((*i));

         std::vector<OrderItem>::iterator fnd = InitQTYData(&q, pi, 0);
         
         if( dlg.inPack )
            q.flags |= oiInPack;
         else
            q.flags &= (~oiInPack);

         if( (q.flags & oiInPack) != 0 )
            q.qty = MulInPack(value, pi.qtyInPack, QTY_SCALE);
         else
            q.qty = value;

         order->UpdateOrder(fnd, q);
      }
   }

   packetSet.clear();
   return true;
}

void PriceFormAdd::LoadMenuBar()
{
   PriceForm::LoadMenuBar();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (((PriceFormDataAdd*)data)->packetInput) ? 9 : 8;
   menuBar.SetButtonInfo(IDC_PACKET_INPUT, &bi);
}

LRESULT PriceFormAdd::PacketInput(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   bool res = ((PriceFormDataAdd*)data)->SwitchInput();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (((PriceFormDataAdd*)data)->packetInput) ? 9 : 8;
   menuBar.SetButtonInfo(IDC_PACKET_INPUT, &bi);

   if( res )
      Refresh();

   return 0;
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}

void SelectPriceItem(IPriceSelect *selector, OrderImpl *o)
{
   PriceFormData *pfd = new PriceFormDataAdd(o, selector);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}