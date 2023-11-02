/*
 * Copyright (C), 2006-2008, Денис Мосягин
 *
 * Диалог количества
 *
 *  ert   17/08/2007   creating
 */
#ifndef __QTY_H
#define __QTY_H

#include <Form.h>
#include "FormEntries.h"
#include "BaseDialog.h"
#include "SAnchor.h"
#include "NumInput.h"
#include "ObjImpl.h"
#include "RADrawer.h"

#include <atlcrack.h>

#ifdef PRICE_MOVER
#include <PictButton.h>
#endif

class CQTYDialog : public BaseDialog
{
public:
   CQTYDialog(QTYData *_data);

   BEGIN_MSG_MAP(CQTYDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_CODE_HANDLER(EN_SETFOCUS, OnSetFocus)
      COMMAND_HANDLER(IDC_QTY, EN_CHANGE, SetSum)
      COMMAND_HANDLER(IDC_PACK, BN_CLICKED, SetSum)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
#ifdef PRICE_MOVER
      COMMAND_ID_HANDLER(IDC_NEXT, ChangeItem)
      COMMAND_ID_HANDLER(IDC_PREV, ChangeItem)
#endif
#ifdef ORD_ITEM_DISCOUNT
      COMMAND_HANDLER(IDC_DISCOUNT_VALUE, STN_CLICKED, ChangeNac)
#endif
#ifdef SHOW_OFF_TAKE
      COMMAND_HANDLER(IDD_REMNANTS_QTY, EN_CHANGE, UpdateRemnants)
#endif
      COMMAND_HANDLER(IDC_SALES_HIST, STN_CLICKED, ShowHistory)
      NUM_INPUT_HANDLER(numInput)
      //MSG_WM_PAINT(OnPaint)
      CHAIN_MSG_MAP(BaseDialog)

#ifdef PRICE_MOVER
      REFLECT_NOTIFICATIONS()
#endif
   END_MSG_MAP()

protected:
   CQTYDialog(QTYData *_data, int formID);

   virtual DWORD GetStartQty(bool inPack, int qip) { return (inPack) ? qip : 1 * QTY_SCALE; }

protected:
   RightAlignDrawer qtyText;
#ifdef Kirov_Pavel
   StaticAnchor histText;
#endif
   bool inHistory;

   void SetSalesInfo();

   LRESULT OnSetFocus(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT ShowHistory(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT SetSum(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

#ifdef PRICE_MOVER
   LRESULT ChangeItem(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   PictButton next, prev;
#endif
   //void OnPaint(HDC);
#ifdef SHOW_OFF_TAKE
   LRESULT UpdateRemnants(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
#endif

   virtual bool CheckQty();
   void SetData(bool setQtyFocus = true);

   virtual void SetData(const PriceImpl& price);
   virtual void SaveData() {}
   virtual void MoveControls(WORD wdh, WORD hgh) {}
   virtual DWORD QTYInPack(const PriceImpl& price) { return price.qtyInPack; }

   bool UpdateQTY();

#ifdef MULTI_WH
   virtual DWORD PriceQty(const Price &item) const
   { return (data->whIndex < (short)item.qty.size()) ? item.qty[data->whIndex] : 0; }
#elif FIRMS_REST
   virtual DWORD PriceQty(const Price &item) const
   { return (data->whIndex <= 0) ? item.qty : (data->whIndex <= (short)item.firmQty.size()) ? item.firmQty[data->whIndex-1] : 0; }
#elif WH_QTY
   virtual DWORD PriceQty(const Price &item) const
   { return (data->whIndex <= 0) ? item.qty : (data->whIndex <= (short)item.whQty.size()) ? item.whQty[data->whIndex-1] : 0; }
#else
   virtual DWORD PriceQty(const Price &item) const { return item.qty; }
#endif

protected:
   QTYData *data; 
   DWORD    qtyInPack, prevQty;
   int     *addedCtrls, *addedLabels;

   void RightAlign(int rightPos, int ctrl);

   int      priceQty;
   NumInput numInput;

#ifdef ORD_ITEM_DISCOUNT
   LRESULT ChangeNac(WORD code, WORD id, HWND hWnd, BOOL& bHandled);
   virtual bool CanSetDiscount(int dsc) { return true; }
   StaticAnchor nac;
#endif
};

#endif
