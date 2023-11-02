/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Окно прайс-листа
 *
 *  ert   18/08/2007   creating
 */
#ifndef __PRICE_FORM_H
#define __PRICE_FORM_H

#include "PriceBase.h"

#include "DocImpl.h"

#ifdef SHOW_OFF_TAKE
#include "OrgRmnts.h"
#endif

struct PriceFormItem : public FolderFormItem
{
   DWORD column2;
   DWORD column3;

   DECLARE_TYPE_REFLECTION(PriceFormItem)
};

struct QTYData;
class OrderImpl;
struct IPriceSelect;
struct PriceFormData : public PriceBaseData
{
   PriceFormData(OrderImpl* _order, IPriceSelect* selector = NULL);
   PriceFormData(OrderImpl* _order, const ROWID& upFolder, IPriceSelect* selector = NULL);

   ~PriceFormData();

   virtual PriceBaseData* Clone();

   virtual COLORREF GetItemColor(int index) const;

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const { return columnCount; }
   virtual const DataReflector& DataType() const;

   virtual bool Get(IReflectableData* data, int index) const;

   virtual void UpdateHeadTitle();

   virtual bool IsItemMarked(const wchar_t *id) const;
   virtual bool IsTopLevel() const { return ((state & pdInMatrix) != 0) ? true : PriceBaseData::IsTopLevel(); }
   virtual bool HaveLeaf() const { return ((state & pdInMatrix) != 0) ? false : PriceBaseData::HaveLeaf(); }

   const wchar_t *Photo(int index) const;
   void  SetPhoto(const wchar_t *photo, int index);

   virtual DWORD Sum() const;

   virtual bool CanSetColumn(int rowIndex, int colIndex) const;

   void SetFlatPrice(bool flatPrice)
   {
      SetCurrent(current, flatPrice);
   }

   bool HaveOrder() const { return order != NULL; }

   bool Closed();

#ifdef ORD_SURVAY
   virtual bool HaveSurvay(int index, int column) const;
   const wchar_t* GetSurvay(int index) const;
   void SetSurvay(int index, const wchar_t *choice);
#endif

   OrderImpl *UnbindOrder()
   { 
      OrderImpl *po = order; 
      order = NULL; 
      return po; 
   }

#ifdef PRICE_MOVER
   const ROWID& LeafID(int index) const { return leafs[index]; }
   int LeafCount() const { return leafs.size(); }

   SumLabel *sumLabel;
#endif

#ifdef PRICE_MATRIX
   //const wchar_t* GetMatrix() const { return matrixes[curMatrix].name.c_str(); }
   void Matrixes(std::vector<wchar_t*> *matrixes) const;
   void SetMatrix(int matrix);
   int CurMatrix() const { return curMatrix; }
#endif

#if defined(MULTI_WH) || defined(FIRMS_REST)
   virtual void SetWarehous(short newWh);
   short CurWarehous() const { return currentWh; }
#endif

protected:
   virtual void SetQTYDataFromOrder(QTYData *qd, const Price& p, std::vector<OrderItem>::iterator item);
   virtual void UpdateOrder(const QTYData &qd, std::vector<OrderItem>::iterator item);

   virtual void LoadFolderData(const TreeNode& folder);

   virtual void AfterLoadFolder() const {}
   virtual void AfterLoadPrice() const {}

   std::vector<OrderItem>::iterator InitQTYData(QTYData* qd, const Price& p, int index); 

   bool ColumnHaveData(int rowIndex, PriceColumnField) const;

   virtual bool SelectLeaf(int index);
   void SetColumnData(int index, DWORD *data, PriceColumnField field) const;

   void MakeHeader();
   void UpdateHeadTitle(PriceColumnField column2, PriceColumnField column3);

#ifdef MULTI_WH
   virtual DWORD PriceQty(const Price &price) const { return (currentWh >= price.qty.size()) ? 0 : price.qty[currentWh]; }
#elif FIRMS_REST
   virtual DWORD PriceQty(const Price &price) const { return (currentWh <= 0) ? price.qty : (currentWh > price.firmQty.size()) ? 0 : price.firmQty[currentWh-1]; }
#elif WH_QTY
   virtual DWORD PriceQty(const Price &price) const { return (currentWh <= 0) ? price.qty : (currentWh > price.whQty.size()) ? 0 : price.whQty[currentWh-1]; }
#else
   virtual DWORD PriceQty(const Price &price) const { return price.qty; }
#endif

   virtual DWORD ItemCost(const Price &price, WORD ct) const;

#ifdef ORD_ITEM_DISCOUNT
   // DISCOUNT_SCALE
   virtual int   ItemDiscount(const Price &price) const { return 0; }
#endif


   LongScaleType* CreateColumnFormat(const wchar_t *name, WORD offset, PriceColumnField fmt, const Preference &pref);

protected:
   IPriceSelect* selector;

   std::set<std::wstring> lastSaledItems; // товары из последней поставки или заказа
   std::map<ROWID, std::wstring> photos;
   OrderImpl* order;
   int columnCount;

#ifdef SHOW_OFF_TAKE
   OrgRemnantsImpl remnants;
#endif

#ifdef ORD_SURVAY
   MemberType *groupFaultType;
#endif

#ifdef PRICE_MATRIX
   struct Matrix
   {
      std::wstring name;
      std::vector<ROWID> items;
   };

   std::vector<Matrix> matrixes;
   int curMatrix;

   virtual void LoadMatrix();
#endif

public:
   virtual void Init(); // вызываем в SetData для работы с виртуальными функциями
   virtual void SetDataDone() {}
};

class PicWindow;
class PriceForm : public PriceBaseForm
{
public:
   PriceForm();

   DECLARE_FORM(PriceForm, IDD_PRICE_LIST)

   virtual bool SetData(IFormData *_data);

   BEGIN_MSG_MAP(PriceForm)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      MESSAGE_HANDLER(WM_COMMAND, OnCommand)
      COMMAND_ID_HANDLER(IDC_PHOTO, SetPhoto)
      COMMAND_ID_HANDLER(IDC_DEL, DelPhoto)
#if defined(PRICE_MATRIX) || defined(MULTI_WH) || defined(FIRMS_REST)
      NOTIFY_CODE_HANDLER(TBN_ENDDRAG, ShowList)
#endif
      CHAIN_MSG_MAP(PriceBaseForm)
   END_MSG_MAP()

   LRESULT OnCommand(WORD msg, WPARAM, LPARAM, BOOL &bHandled);

   virtual DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);

#ifdef ORD_SURVAY
   virtual DWORD OnItemPostPaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);
   int PopSurvayMenu(std::vector<std::wstring> *choices, int index);
#endif

   virtual int SumLabelOffset() const { return GetSystemMetrics(SM_CXSMICON) * 9 / 4; }
   virtual void LoadMenuBar();

protected:
   LRESULT ItemSelected(LPNMHDR hdr);
   LRESULT DelPhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT SetPhoto(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

#if defined(PRICE_MATRIX) || defined(MULTI_WH) || defined(FIRMS_REST)
   LRESULT ShowList(int id, LPNMHDR header, BOOL &handled);
#endif
#ifdef PRICE_MATRIX
   LRESULT SetMatrix(int id, LPNMHDR header, BOOL &handled);
#endif
#if defined(MULTI_WH) || defined(FIRMS_REST)
   LRESULT SetWarehouse(int id, LPNMHDR header, BOOL &handled);
#endif

   virtual LRESULT Closing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

   virtual bool CanSetColumn(int rowIndex, int colIndex) const;

   virtual LRESULT SetCellInfo(LPNMHDR hdr);

   void ShowPhoto(const wchar_t *fileName);
   bool SetDataEx(IFormData *_data, int scale);

protected:
   PicWindow *picWindow;
};

#endif
