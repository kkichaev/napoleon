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

#include <BaseDialog.h>

int saveIdWidth;
static ListFormData::Header priceHeader[] = 
{
   { ListFormData::Header::Left,  L"Название", L"name", 160 },
   { ListFormData::Header::Left,  L"Примечание", L"id", 40 },
   { ListFormData::Header::Right, L"Кол-во",   L"column2", 50 },
   { ListFormData::Header::Right, L"Сумма",    L"column3", 50 }
};

struct PFItemAdd : PriceFormItem
{
   const wchar_t *id;
   
   DECLARE_TYPE_REFLECTION(PFItemAdd)
};

BEGIN_TYPE_REFLECTION(PFItemAdd)
   REGISTER_STRING_MEMBER(PFItemAdd, id)
   CHAIN_REFLECTION(PFItemAdd, PriceFormItem)
END_TYPE_REFLECTION(PFItemAdd)


struct PriceFormDataAdd : public PriceFormData
{
   PriceFormDataAdd(OrderImpl* _order);
   PriceFormDataAdd(OrderImpl* _order, const ROWID &upFolder);
   
   virtual const Header *GetHeader() const
   { 
      return priceHeader; 
   }

   virtual const DataReflector& DataType() const { return PFItemAdd().GetType(); }

   virtual PriceBaseData* Clone()
   {
      OrderImpl *order = UnbindOrder();

      PriceBaseData *pfd = new PriceFormDataAdd(order, UpFolder());
      return pfd;
   }

   virtual bool CanSetColumn(int rowIndex, int colIndex) const
   {
      if( colIndex == 0 ) return true;
      if( rowIndex < (int)folders.size() && colIndex == 1 ) return false;
      return PriceFormData::CanSetColumn(rowIndex, colIndex-1); 
   }

   virtual bool Get(IReflectableData* data, int index) const
   {
      bool ret = PriceFormData::Get(data, index);
      if( !ret ) return false;
      
      if( (unsigned)index >= folders.size() )
         ((PFItemAdd*)data)->id = priceItem.remark;
      else
         ((PFItemAdd*)data)->id = L"";
      return true;
   }
   
   void UpdateTitle();

protected:
   virtual void Init();
};

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order) : PriceFormData(_order)
{
}

PriceFormDataAdd::PriceFormDataAdd(OrderImpl* _order, const ROWID& upFolder) : PriceFormData(_order, upFolder)
{      
}

void PriceFormDataAdd::Init()
{
   PriceFormData::Init();

   DataReflector *reflector = new DataReflector(PFItemAdd::Creator, L"PFItemAdd");
   RemoveTypeReflector(L"PFItemAdd");
   RegisterTypeReflector(reflector);

   reflector->AddMember(new StringType(L"id", offsetof(PFItemAdd, id)));
   reflector->AddMember(new ParentType(L"PriceFormItem"));
      
   columnCount++; 
}

class PriceFormAdd : public PriceForm
{
public:
   PriceFormAdd();

   DECLARE_FORM(PriceFormAdd, IDD_PRICE_LIST_ADD)

   BEGIN_MSG_MAP(PriceFormAdd)
      COMMAND_ID_HANDLER(IDC_PRICE_NUM, SwitchCode)
      CHAIN_MSG_MAP(PriceForm)
   END_MSG_MAP()

   virtual DWORD GetMenuBarID() const { return IDD_PRICE_LIST_ADD; }
   virtual DWORD GetResourceID() const { return IDD_PRICE_LIST; }

protected:
   virtual void UpdateLayout(bool forceRecalc);
   virtual void LoadMenuBar();

   LRESULT SwitchCode(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);

protected:
   static bool codeShowed;

   //ButtonBg codeBtn; 
};

bool PriceFormAdd::codeShowed = false;

PriceFormAdd::PriceFormAdd()
{
}

IMPLEMENT_FORM(PriceFormAdd)

LRESULT PriceFormAdd::SwitchCode(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   codeShowed = !codeShowed;
   UpdateLayout(true);
   return 0;
}

void PriceFormAdd::LoadMenuBar()
{
   PriceForm::LoadMenuBar();

   TBBUTTONINFO bi = {0};
   bi.cbSize = sizeof(bi);
   bi.dwMask = TBIF_IMAGE;
   bi.iImage = (codeShowed) ? 11 : 10;
   menuBar.SetButtonInfo(IDC_PRICE_NUM, &bi);
}

void PriceFormAdd::UpdateLayout(bool forceRecalc)
{
   if( menuBar.m_hWnd != NULL )
   {
      for( int i=0; i<sizeof(priceHeader)/sizeof(priceHeader[0]); i ++ )
         priceHeader[i].curWidth = listCtrl.GetColumnWidth(i);

      if( forceRecalc )
      {  // set startWidth
         if( codeShowed )
         {
             priceHeader[0].startWidth = 160;
             priceHeader[1].startWidth = 40;
         } else
         {
            priceHeader[0].startWidth = 200;
            priceHeader[1].startWidth = 0;
         }
      } else
      {
         if( codeShowed )
         {
            priceHeader[1].curWidth = (saveIdWidth != 0) ? saveIdWidth : priceHeader[0].curWidth/2;
            priceHeader[0].curWidth -= priceHeader[1].curWidth;
            if( priceHeader[0].curWidth < 0 ) priceHeader[0].curWidth = 0;
         } else
         {
            saveIdWidth = priceHeader[1].curWidth;
            priceHeader[0].curWidth += priceHeader[1].curWidth;
            priceHeader[1].curWidth = 0;
         }
      }
   } else
   {
      if( !codeShowed && priceHeader[1].curWidth != 0 )
      {
         if( priceHeader[0].curWidth != 0 )
            priceHeader[0].curWidth += priceHeader[1].curWidth;
         else
         {
            priceHeader[0].startWidth = 200;
            priceHeader[1].startWidth = 0;
         }
         priceHeader[1].curWidth = 0;
      }
   }

   listCtrl.SetRedraw(FALSE);
   PriceForm::UpdateLayout(forceRecalc);

   listCtrl.SetRedraw(TRUE);

   if( menuBar.m_hWnd != NULL )
   {
      TBBUTTONINFO bi = {0};
      bi.cbSize = sizeof(bi);
      bi.dwMask = TBIF_IMAGE;
      bi.iImage = (codeShowed) ? 11 : 10;
      menuBar.SetButtonInfo(IDC_PRICE_NUM, &bi);
   }
}

void OpenPriceList(OrderImpl* order)
{
   PriceFormData *pfd = new PriceFormDataAdd(order);
   _Module.GetFrame()->Load(IDD_PRICE_LIST_ADD, pfd);
}

