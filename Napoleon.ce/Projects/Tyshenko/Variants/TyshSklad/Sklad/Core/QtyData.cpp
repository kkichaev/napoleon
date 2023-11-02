/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * Методы относящиеся к QTYData
 *
 *  ert   01/12/2009   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "FormEntries.h"
#include "DocImpl.h"

#include <PicWindow.h>
#include <StdFuncs.h>

#include "NumInput.h"

#include <BaseDialog.h>

#ifdef SAVE_IN_PACK
WORD saveInPack=SAVE_IN_PACK;
#endif
//
//QTYData::QTYData()
//{
//   id = L"";
//   qty = 0;
//   flags = 0;
//   cost = 0;
//   sum = 0;
//   canChange = true;
//}
//
//
//static bool UpdatePrice(OrderImpl *order, const wchar_t *id, int qty)
//{
//   //if( qty == 0 ) return true;
//
//   //PriceImpl p;
//   //p.id = (wchar_t*)id;
//   //if( !p.Read() ) return false;
//
//   //p.qty += qty;
//   //return p.Update(L"qty");
//   return true;
//}
//
//void OrderImpl::UpdateOrder(std::vector<OrderItem>::iterator item, const QTYData &qd)
//{
//   int priceUpdate = 0;
//   WORD flags = qd.flags;
//   if( item == items.end() )
//   { // insert
//      if( qd.qty == 0 ) return;
//
//      OrderItem oi;
//      oi.id = holder.Add(qd.id.c_str());
//      //oi.sum = qd.sum;
//      oi.flags = flags;
//      oi.qty = qd.qty;
//		oi.mark = L"";
//		oi.rack = L"";
//		oi.rackDest = L"";
//		oi.palletBarcode = L"";
//		oi.barcode = L"";
//
//      items.push_back(oi);
//      
//      priceUpdate = -(int)qd.qty;
//   } else
//   {
//      priceUpdate = item->qty;
//      item->flags = flags;
//      if( qd.qty != 0 )
//      { 
//         // change
//         item->qty = qd.qty;
//         //item->sum = qd.sum;
//         priceUpdate -= (int)qd.qty;
//      } else
//      { 
//         items.erase(item);
//      }
//   }
//
//   bool res;
//   if( items.size() ) res = Write();
//   else res = Remove();
//
//   if( res )
//   {
//      //ItemQtyChanged(qd.id.c_str(), -priceUpdate);
//
//      //if( (instanceFlags & ifNoUpdatePrice) == 0 )
//         UpdatePrice(this, qd.id.c_str(), priceUpdate);
//      //docTypeManager.SumChanged(docType, id);
//   }
//}
//
//
//class QtyDlg : public BaseDialog
//{
//public:
//   QtyDlg(OrderImpl* _order, int index);
//
//   BEGIN_MSG_MAP(QtyDlg)
//      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
//      COMMAND_HANDLER(IDC_QTY, EN_CHANGE, CheckQty)
//      //COMMAND_HANDLER(IDC_CAUSE, CBN_SELCHANGE, ResetQty)
//      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
//      NUM_INPUT_HANDLER(numInput)
//      CHAIN_MSG_MAP(BaseDialog)
//   END_MSG_MAP()
//
//   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
//   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
//   LRESULT CheckQty(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
//   //LRESULT ResetQty(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
//
//protected:
//   PriceImpl price;
//   OrderImpl* order;
//   OrderItem* item;
//
//   StringHolder holder;
//   NumInput numInput;
//};
//
//QtyDlg::QtyDlg(OrderImpl* _order, int index) : BaseDialog(IDD_QTY, 0), order(_order), numInput(IDC_QTY)
//{
//   item = (index < (int)order->items.size() && index >= 0) ? &order->items.at(index) : NULL;
//}
//
////LRESULT QtyDlg::ResetQty(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
////{
////   CComboBox cb(GetDlgItem(IDC_CAUSE));
////   if( cb.GetCurSel() >= 0 )
////      SetDlgItemText(IDC_QTY, L"0");
////
////   return 0;
////}
//
//LRESULT QtyDlg::CheckQty(WORD code, WORD id, HWND hWnd, BOOL& bHandled)
//{
//   //wchar_t buf[100];
//   //GetDlgItemText(IDC_QTY, buf, sizeof(buf)/sizeof(buf[0]));   
//   //DWORD val = GetValue(buf, SUM_SCALE);
//
//   return 0;
//}
//
//LRESULT QtyDlg::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
//{
//   const int UNDLV_ITEM_CAUSE = 1;
//
//   bHandled = FALSE;
//
//   if( item == NULL )
//      return 0;
//
//   price.id = item->id;
//   price.Read();
//	
//   SetDlgItemText(IDC_ITEM_NAME, price.name);
//   SetScalingValue(IDC_ORDER, item->qty, QTY_SCALE, true);
//
//   return 0;
//}
//
//LRESULT QtyDlg::Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
//{
//   if( wID == IDOK && item != NULL )
//   {
//      wchar_t buf[100];
//      GetDlgItemText(IDC_QTY, buf, sizeof(buf)/sizeof(buf[0]));      
//      DWORD val = GetValue(buf, QTY_SCALE);
//
//      item->qty = val;
//
//      order->Write();
//   }
//
//   bHandled = FALSE;
//   return 0;
//}
//
