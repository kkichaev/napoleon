/*
 * Copyright (C), 2006-2013, Денис Мосягин
 *
 * Список заявок
 *
 *  ert   22/04/2013   creating
 */
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include "DocImpl.h"
#include "FormEntries.h"
#include "PictButton.h"

#include <Preference.h>
#include "AppBaseForm.h"
#include "BaseDialog.h"

#include "MainFrame.h"
#include "Qty.h"

//#include "syslib.h"

//#ifdef ZEBEX
//
//typedef BOOL (*pZBCRSetPower)(BOOL dwState);
//typedef BOOL (*pGetLastNotifyEvent) (PDWORD lpNotifyEvent);
//typedef BOOL (*pGetLastBarcode) (LPTSTR lpszBarcode);
//typedef BOOL (*pZBCRStartScan) (void);
//typedef BOOL (*pZBCRStopScan) (void);
//typedef BOOL (*pZBCRSetOutputMode) (BYTE dwMode);
//
//typedef BOOL (*pSysSetFxKeyState)(DWORD dwVKCode,BOOL dwEnableState);
//
//#endif
//
//struct RouteListItem : public IReflectableData
//{
//   wchar_t* item;
//   DECLARE_TYPE_REFLECTION(RouteListItem)
//};
//
//BEGIN_TYPE_REFLECTION(RouteListItem)
//   REGISTER_STRING_MEMBER(RouteListItem, item)
//END_TYPE_REFLECTION(RouteListItem)
//
//static ListFormData::Header header[] = 
//{
//   { ListFormData::Header::Left,  /*L"Название"*/IDS_DOC, NULL, L"item", 150 },
//};
//
//struct RouteCheckItem
//{
//   bool presents, isControl;
//   std::wstring id;
//   std::wstring name;
//
//   void Set(const wchar_t* docId, bool isControlDoc)
//   {
//      id = docId;
//      DecodeNumber(&name, docId, true);
//      presents = false;
//      isControl = isControlDoc;
//   }
//};
//
//class RouteListData : public ListFormData
//{
//public:
//   enum ItemType { Document, Rack, Item, Qty, ErrorType };
//   enum ControlType { CanInputWORack = -1, None = 0, Warning, Error };
//
//   RouteListData(const wchar_t *docId);
//   ~RouteListData();
//
//   virtual const Header *GetHeader() const { return header; }
//   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }
//
//   virtual const DataReflector& DataType() const { return RouteListItem().GetType(); }
//   virtual int Count() const;
//
//   virtual bool Add(const IReflectableData& data, int index) { return false; }
//   virtual bool Remove(int index) { return false; }
//   virtual bool Update(const IReflectableData& data, int index) { return false; }
//   virtual void Clear() {}
//
//   virtual bool Editing(int index) { return Selecting(index); }
//
//   virtual bool Selecting(int index);
//
//   virtual bool Get(IReflectableData* data, int index) const;
//
//   bool New(const wchar_t* doc, ItemType type);
//
//   bool CanChange(ItemType type) const;
//   bool CanExit() const;
//
//   bool CheckCurItem() const;
//   bool HaveChanges(int index) const;
//
//   void GetDocTitle(std::wstring* text) const;
//   void GetRackTitle(std::wstring* text) const;
//
//   void RemoveDoc();
//
//   bool ControlDocError() const { return ((ctrlDoc.rid == NO_ROWID) && (docType.controlDoc > 0)); }
//
//protected:
//   OrderImpl *current;
//
//   WhAgents agent;
//
//   DocTypeImpl docType;
//   ControlDocImpl ctrlDoc;
//
//   std::vector<RouteCheckItem> items;
//
//protected:
//   void RefreshItems();
//
//   ControlType CheckType(ItemType type) const;
//
//   bool NewDoc(const wchar_t* doc);
//};
//
//#if ZEBEX
//class RouteListForm : public ListForm, public BarcodeHandler, public CCustomDraw<RouteListForm>
//#else
//class RouteListForm : public ListForm, public CCustomDraw<RouteListForm>
//#endif
//{
//public:
//   RouteListForm() {}
//
//   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 2); }
//
//   BEGIN_MSG_MAP(DocumentForm)
//      COMMAND_ID_HANDLER(IDC_BACK, Backing)
//      COMMAND_ID_HANDLER(IDC_REMOVE, Remove)
//      COMMAND_ID_HANDLER(IDC_INPUT_BARCODE, OnInputBC)
//      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
//      CHAIN_MSG_MAP(CCustomDraw<RouteListForm>)
//      CHAIN_MSG_MAP(ListForm)
//   END_MSG_MAP()
//
//   virtual DWORD GetResourceID() const { return IDD_ROUTE_LIST; }
//   virtual DWORD GetMenuID() const { return IDD_ROUTE_LIST; }
//
//   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);
//
//#if ZEBEX
//   virtual void HandleEvent();
//
//	HMODULE hLib;
//   pGetLastNotifyEvent GetBCEvent;
//   pGetLastBarcode GetBarcode;
//#endif
//
//   DECLARE_FORM(RouteListForm, IDD_ROUTE_LIST)
//
//   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
//   LRESULT Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
//
//   LRESULT OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
//   DWORD OnPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/) { return CDRF_NOTIFYITEMDRAW; }
//   DWORD OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW /*lpNMCustomDraw*/);
//   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
//
//protected:
//   bool SetDataEx(IFormData *_data, int scale);
//   void OnBarcode(const wchar_t* barcode);
//};
//
//
//IMPLEMENT_FORM(RouteListForm);
//
////
////----------------------------------- RouteListData ----------------------------------
////
//RouteListData::RouteListData(const wchar_t* id)
//{
//   SQLTable tb(WhAgentsImpl().Name());
//   tb.Select(&agent, L"where id=userid");
//
//   ctrlDoc.id = (wchar_t*)id;
//   ctrlDoc.Read();
//
//   current = new OrderImpl();
//   current->id = (wchar_t*)id;
//   if( !current->Read() )
//   {
//      SYSTEMTIME st;
//      GetLocalTime(&st);
//      SystemTimeToFileTime(&st, &current->created);
//      current->params = 0;
//      current->id = current->holder.Add(id);
//      current->Write();
//   } else
//   {
//      if( !current->IsDirty() )
//      {
//         current->ClearDirty(NULL, true);
//         current->Write();
//      }
//   }
//
//   RefreshItems();
//
//   wchar_t buf[3], *dest;
//   dest = buf;
//   *dest++ = id[2];
//   *dest++ = id[3];
//   *dest = L'\0';
//
//   docType.id = buf;
//   bool fail = false;
//   docType.Read();
//}
//
//RouteListData::~RouteListData()
//{
//   if( current )
//   {
//      if( current->items.size() == 0 )
//      {
//         current->Remove();
//      }
//      delete current;
//      current = NULL;
//   }
//}
//
//static RouteCheckItem* FindItem(const std::vector<RouteCheckItem>& items, const wchar_t* id)
//{
//   std::vector<RouteCheckItem>::const_iterator i = items.begin();
//   for( ; i != items.end(); i++ )
//      if( i->id.compare(id) == 0 )
//         return (RouteCheckItem*)(&(*i));
//
//   return NULL;
//}
//
//inline RouteCheckItem* FindItem(const std::vector<RouteCheckItem>& items, const OrderItem& item)
//{
//   return FindItem(items, item.id);
//}
//
//void RouteListData::RefreshItems()
//{
//   items.clear();
//   std::vector<OrderItem>::const_iterator i = ctrlDoc.items.begin();
//   for( ; i != ctrlDoc.items.end(); i++ )
//   {
//      RouteCheckItem item;
//      item.Set(i->id, true);
//      items.push_back(item);
//   }
//
//   if( current == NULL )
//      return;
//
//   i = current->items.begin();
//   for( ; i != current->items.end(); i++ )
//   {
//      RouteCheckItem* item = FindItem(items, *i);
//      if( item == NULL )
//      {
//         RouteCheckItem ditem;
//         ditem.Set(i->id, false);
//         items.push_back(ditem);
//      } else
//      {
//         item->presents = true;
//      }
//   }
//}
//
//bool RouteListData::HaveChanges(int index) const
//{
//   if( index < 0 || index >= (int)items.size() )
//      return false;
//
//   return !items.at(index).presents;
//}
//
//bool RouteListData::CanExit() const
//{
//   return true;
//}
//
//void RouteListData::RemoveDoc()
//{
//   if( current != NULL )
//   {
//      current->Remove();
//
//      delete current;
//      current = NULL;
//   }
//}
//
//int RouteListData::Count() const
//{
//   if( current == NULL )
//      return 0;
//
//   return items.size();
//}
//
//RouteListData::ControlType RouteListData::CheckType(RouteListData::ItemType type) const
//{
//   switch( type )
//   {
//   case Document:
//      return (ControlType)docType.controlDoc;
//   case Rack:
//      return (ControlType)docType.controlRack;
//   case Item:
//      return (ControlType)docType.controlItem;
//   case Qty:
//      return (ControlType)docType.controlQty;
//   }
//
//   return None;
//}
//
//bool RouteListData::CanChange(RouteListData::ItemType type) const
//{
//   if( type != Document )
//   {
//      MessageBox(NULL, L"Можно ввеодить только документы", L"Ошибка", MB_OK | MB_ICONSTOP);
//      return false;
//   }
//   return true;
//}
//
//bool RouteListData::New(const wchar_t* doc, RouteListData::ItemType type)
//{
//   bool ret = false;
//
//   switch(type)
//   {
//   case Document:
//      ret = NewDoc(doc);
//      break;
//   }
//   return ret;
//}
//
//bool RouteListData::NewDoc(const wchar_t* doc)
//{
//   const OrderItem* si = ctrlDoc.FindItem(L"", doc);
//   if( si == NULL )
//   {
//      if( docType.controlDoc != None )
//      {
//         MessageBox(NULL, L"Нет такого документа в маршрутном листе", L"Ошибка", MB_OK | MB_ICONSTOP );
//         return false;
//      }
//   }
//   const OrderItem* di = current->FindItem(L"", doc);
//   if( di != NULL )
//      return false;
//
//   OrderItem oi;
//   oi.id = current->holder.Add(doc);
//   oi.qty = QTY_SCALE;
//   oi.flags = 0;
//	oi.mark = L"";
//   oi.rack = L"";
//	oi.rackDest = L"";
//	oi.palletBarcode = L"";
//	oi.barcode = L"";
//
//   current->items.push_back(oi);
//   current->Write();
//   RefreshItems();
//
//   return true;
//}
//
//void RouteListData::GetDocTitle(std::wstring* text) const
//
//{
//   text->clear();
//   if( current != NULL )
//      DecodeNumber(text, current->id, true);
//}
//
//bool RouteListData::Get(IReflectableData* data, int index) const
//{
//   if( index < 0 || index >= (int)items.size())
//      return false;
//
//   const RouteCheckItem& i = items.at(index);
//   ((RouteListItem*)data)->item = (wchar_t*)i.name.c_str();
//
//   return true;
//}
//
//bool RouteListData::Selecting(int index)
//{
//   return false;
//}
//
////
////-------------------------------- RouteListForm --------------------------------
////
//bool RouteListForm::SetDataEx(IFormData *_data, int scale)
//{
//   if( !ListForm::SetDataEx(_data, scale) )
//      return false;
//   
//#ifdef ZEBEX
//   hLib = LoadLibrary(L"zbcrlib.dll");
//   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
//   fn(TRUE);
//
//   pZBCRSetOutputMode outMode = (pZBCRSetOutputMode)GetProcAddress(hLib, L"ZBCRSetOutputMode");
//   outMode(BCR_DISABLE_OUTPUT);
//
//   GetBCEvent = (pGetLastNotifyEvent)GetProcAddress(hLib, L"ZBCRGetLastNotifyEvent");
//   GetBarcode = (pGetLastBarcode)GetProcAddress(hLib, L"ZBCRGetLastBarcode");
//
//   HMODULE hm = LoadLibrary(L"syslib.dll");
//   pSysSetFxKeyState keyFn = (pSysSetFxKeyState)GetProcAddress(hm, L"SysSetFxKeyState");
//   if( keyFn )
//      keyFn(VK_F1, FALSE);
//   FreeLibrary(hm);
//
//   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(this);
//#else
//   //StartScan(m_hWnd);
//#endif
//
//   wchar_t text[MAX_PATH];
//   std::wstring tstr;
//   ((RouteListData*)data)->GetDocTitle(&tstr);
//   wsprintf(text, L"Документ: %s", tstr.c_str());
//   SetDlgItemText(IDC_DOC, text);
//
//   return true;
//}
//
//LRESULT RouteListForm::OnInputBC(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
//{
//   BCDialog dlg;
//   if( dlg.DoModal() == IDOK && *dlg.GetText() != L'\0' )
//   {
//      wchar_t buf[MAX_PATH];
//      wcscpy(buf, dlg.GetText());
//      OnBarcode(buf);
//   }
//   return 0;
//}
//
//static bool readBarcode = false;
//void RouteListForm::OnBarcode(const wchar_t* _barcode)
//{
//	if( readBarcode )
//		return;
//	
//	readBarcode = true;
//
//	wchar_t text[MAX_PATH];
//	wchar_t *barcode = (wchar_t*)alloca((wcslen(_barcode) + 1) * sizeof(wchar_t));
//	wcscpy(barcode, _barcode);
//
//   wchar_t *p = wcschr(barcode, L'\n');
//   if( p ) *p = L'\0';
//   p = wcschr(barcode, L'\r');
//   if( p ) *p = L'\0';
//   bool isItem = (wcslen(barcode) == 13);
//   bool refresh = false;
//
//   RouteListData::ItemType itemType = RouteListData::Item;
//   if( !isItem && *barcode == L'9' && *(barcode+1) == L'1' )
//   {
//      itemType = RouteListData::Document;
//   }
//
//   if( ((RouteListData*)data)->CanChange(itemType) && ((RouteListData*)data)->New(barcode, itemType) )
//   {
//      std::wstring tstr;
//      ((RouteListData*)data)->GetDocTitle(&tstr);
//      wsprintf(text, L"Документ: %s", tstr.c_str());
//      SetDlgItemText(IDC_DOC, text);
//      Refresh();
//   }
//
//	readBarcode = false;
//}
//
//LRESULT RouteListForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
//{
//   std::wstring data;
//  // if( GetScanData(&data, lParam) )
//		//OnBarcode(data.c_str());
//
//	return 1;
//}
//
//#ifdef ZEBEX
//void RouteListForm::HandleEvent()
//{
//   DWORD eventCode;
//
//   if( GetBCEvent(&eventCode) && eventCode == BCR_NOTIFY_RECEIVE_BARCODE )
//   {
//      wchar_t buf[MAX_PATH];
//      GetBarcode(buf);
//      OnBarcode(buf);
//   }
//}
//#endif
//
//DWORD RouteListForm::OnItemPrePaint(int /*idCtrl*/, LPNMCUSTOMDRAW lpNMCustomDraw)
//{
//   NMLVCUSTOMDRAW *lvcd = (NMLVCUSTOMDRAW*)lpNMCustomDraw;
//   bool isHelded = ((RouteListData*)data)->HaveChanges(lvcd->nmcd.dwItemSpec);
//   if( isHelded )
//   {
//      lvcd->clrTextBk = RGB(192, 192, 192);
//   }
//   return CDRF_NOTIFYITEMDRAW;
//}
//
//LRESULT RouteListForm::Remove(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
//{
//   if( data->Count() > 0 )
//   {
//      if( MessageBox(L"Удалить документ?", L"Вопрос", MB_YESNO | MB_ICONQUESTION) == IDYES )
//      {
//         ((RouteListData*)data)->RemoveDoc();
//
//         SetDlgItemText(IDC_DOC, L"Документ:");
//         Refresh();
//      }
//   }
//   return 0;
//}
//
//LRESULT RouteListForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
//{
//   if( !((RouteListData*)data)->CanExit() )
//      return 0;
//
//#ifdef ZEBEX
//   ((MainFrame*)_Module.GetFrame())->SetBarcodeHandler(NULL);
//
//   pZBCRSetPower fn = (pZBCRSetPower)GetProcAddress(hLib, L"ZBCRSetPower");
//   fn(FALSE);
//
//   FreeLibrary(hLib);
//
//   HMODULE hm = LoadLibrary(L"syslib.dll");
//   pSysSetFxKeyState keyFn = (pSysSetFxKeyState)GetProcAddress(hm, L"SysSetFxKeyState");
//   if( keyFn )
//      keyFn(VK_F1, TRUE);
//   FreeLibrary(hm);
//#else
//	//StopScan();
//#endif
//
//   OpenOrderList(); 
//   return 0;
//}
//
//void RouteListForm::UpdateLayout(const RECT& bounds, bool forceRecalc)
//{
//   CRect rc, rc2;
//
//   CWindow btn(GetDlgItem(IDC_REMOVE));
//   btn.GetWindowRect(rc2);
//
//   CWindow wnd(GetDlgItem(IDC_DOC));
//   wnd.GetWindowRect(rc);
//   ScreenToClient(rc);
//   int top = rc.top;
//   int right = bounds.right - rc.left - rc2.Width() - 2;
//   wnd.MoveWindow(rc.left, rc.top, right, rc.Height());
//
//   btn.MoveWindow(rc.left + right+1, top, rc2.Width(), rc2.Height());
//
//   SetListLayout(forceRecalc, top + rc2.Height() + 2, bounds.bottom - rc.bottom + 3);
//}
//
//void OpenRouteList(const wchar_t *id)
//{
//   RouteListData *pfd = new RouteListData(id);
//   if( pfd->ControlDocError() )
//   {
//      MessageBox(NULL, L"Нет контрольного документа для маршрутного листа", L"Ошибка", MB_ICONSTOP | MB_OK);
//      delete pfd;
//      return;
//   }
//   _Module.GetFrame()->Load(IDD_ROUTE_LIST, pfd);
//}