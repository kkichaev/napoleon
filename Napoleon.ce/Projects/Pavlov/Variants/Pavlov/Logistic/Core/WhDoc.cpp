/*
* Copyright (C), 2007 - 2010, Денис Мосягин
*
* Складской документ
*
*  ert   05/09/2010   creating
*/
#include "stdafx.h"
#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>
#include <StdFuncs.h>
#include <ListForm.h>
#include "FormEntries.h"

#include "ObjImpl.h"
#include <NumInput.h>

class WhData : public IFormData
{
public:
   WhData(WhDocImpl* doc, bool forSend) { this->doc = doc; this->forSend = forSend; }
   ~WhData() { delete doc; }

   WhDocImpl* doc;
   bool forSend;
};

class WhForm : public BaseForm
{
public:
   enum State { None, EnterBoard, EnterItem, EnterQty };

   WhForm() : numInput(IDC_ITEM_CODE), state(None) {}
   ~WhForm() { delete data; }

   DECLARE_FORM(WhForm, IDD_QTY)

   virtual DWORD GetResourceID() const { return IDD_QTY; }
   virtual DWORD GetMenuBarID() const { return IDD_QTY; }
   virtual DWORD GetMenuID() const { return -1; }

   BEGIN_MSG_MAP(WhForm)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDOK, EnterData)
      COMMAND_ID_HANDLER(IDC_SEND, Send)
      COMMAND_ID_HANDLER(IDCANCEL, Cancel)
      COMMAND_CODE_HANDLER(EN_SETFOCUS, OnSetFocus)
      MESSAGE_HANDLER(WM_SCAN_DATA, ScanData)
      NUM_INPUT_HANDLER(numInput)
      CHAIN_MSG_MAP(BaseForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data);

   LRESULT Send(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnSetFocus(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);

   LRESULT EnterData(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT Cancel(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

protected:
   void GetData(const wchar_t* id);
   // возможно повторение состояния для refresh data
   void SetState(State newState);
   void Close();

protected:
   WhData* data;
   NumInput numInput;
   static bool inNet;

   CMenuBarCtrl menuBar;
   State state;
   BoardsImpl curBoard;
   SkladImpl curItem;
};

IMPLEMENT_FORM(WhForm)

bool WhForm::inNet = false;

//
///---------------------------------- WhForm ----------------------------
//
bool WhForm::SetData(IFormData *_data)
{
   data = (WhData*)_data;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(),0,0));

   SetState(EnterBoard);
   StartScan(m_hWnd, WM_SCAN_DATA);

   // correct Dig0 width
   CRect bounds;
   CWindow d0(GetDlgItem(IDC_DIG_0));
   GetDlgItem(IDC_DIG_2).GetWindowRect(bounds);
   ScreenToClient(bounds);
   int right = bounds.right;
   d0.GetWindowRect(bounds);
   ScreenToClient(bounds);
   bounds.right = right;
   d0.MoveWindow(bounds);

   SHSipPreference(m_hWnd, SIP_DOWN);
   return true;
}

void WhForm::SetState(WhForm::State newState)
{
   state = newState;

   if( data->forSend && state != EnterBoard )
      return;

   CWindow ql(GetDlgItem(IDC_QTY_LABEL));
   CEdit q(GetDlgItem(IDC_QTY));
   CButton send(GetDlgItem(IDC_SEND));
   CWindow ul(GetDlgItem(IDC_UNIT_TEXT_LABEL));
   CWindow u(GetDlgItem(IDC_UNIT_TEXT));

   CWindow text(GetDlgItem(IDC_TEXT));
   std::wstring tstr;

   switch(state)
   {
   case EnterBoard:
      send.ShowWindow((data->doc->items.size()) ? SW_SHOW : SW_HIDE);
      ql.ShowWindow(SW_HIDE);
      q.ShowWindow(SW_HIDE);
      ul.ShowWindow(SW_HIDE);
      u.ShowWindow(SW_HIDE);
      text.SetWindowText(L"\nВведите штрих-код полки");
      numInput.SetTargetControl(IDC_ITEM_CODE);
      break;

   case EnterItem:
      send.ShowWindow(SW_HIDE);
      ql.ShowWindow(SW_HIDE);
      q.ShowWindow(SW_HIDE);
      ul.ShowWindow(SW_HIDE);
      u.ShowWindow(SW_HIDE);
      numInput.SetTargetControl(IDC_ITEM_CODE);
      tstr = curBoard.name;
      tstr += L"\nВведите штрих-код товара";
      text.SetWindowText(tstr.c_str());
      break;

   case EnterQty:
      send.ShowWindow(SW_HIDE);
      ql.ShowWindow(SW_SHOW);
      q.ShowWindow(SW_SHOW);
      ul.ShowWindow(SW_SHOW);
      u.ShowWindow(SW_SHOW);
      numInput.SetTargetControl(IDC_QTY);
      tstr = curBoard.name;
      tstr += L"\n";
      tstr += curItem.name;
      text.SetWindowText(tstr.c_str());
      u.SetWindowText(curItem.unit);

      SetScalingValue(q, data->doc->GetQty(curBoard.id, curItem.id) + QTY_SCALE, QTY_SCALE, true);
      q.SetSelAll();
      q.SetFocus();
      break;
   }

   GetDlgItem(IDC_ITEM_CODE).SetWindowText(L"");;
}

void WhForm::Close()
{
   StopScan();

   if( data->forSend ) OpenDocList();
   else OpenMainForm();
}

LRESULT WhForm::Cancel(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( state == EnterQty )
      SetState(EnterItem);
   else if( state == EnterItem )
      SetState(EnterBoard);
   else
      Close();

   return 0;
}

LRESULT WhForm::Send(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   inNet = true;

   std::wstring answer;
   ServObject<WhDocImpl> so;

   ProgressWindow pw;
   pw.CreateSTDWindow(m_hWnd);

   CopyData(&so, *data->doc);
   so.UnbindStrings();

   long ec = _Module.ObjectExchange(&so, WRITE_OBJECTS, &answer, &pw);
   pw.DestroyWindow();

   if( ec != 0 || so.servResult == 0 )
   {
      if( ec == 0 )
         answer = so.servResponse;
      _Module.ShowErrorBox(ec, answer,  L"Ошибка при приеме:\n");
   } else
   {
      so.params |= ofExported;
      CopyData(data->doc, so);
      data->doc->UnbindStrings();
      data->doc->Write();

      MessageBox(L"Документ успешно проведен", L"Иняормация", MB_OK | MB_ICONINFORMATION);
      Close();
   }

   inNet = false;
   return 0;
}

LRESULT WhForm::Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   Close();
   return 0;
}

LRESULT WhForm::OnSetFocus(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
{
   numInput.SetTargetControl(wID);
   return 0;
}

LRESULT WhForm::EnterData(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
{
   if( state == EnterQty )
   {
      DWORD qty = GetValue(GetDlgItem(IDC_QTY), QTY_SCALE);
      data->doc->Add(curBoard.id, curItem.id, qty);
      SetState(EnterItem);
   } else
   {
      CWindow wnd(GetDlgItem(IDC_ITEM_CODE));
      int len = wnd.GetWindowTextLength() + 1;
      if( len > 1 )
      {
         wchar_t *buf = (wchar_t*)alloca(len * sizeof(wchar_t));
         wnd.GetWindowText(buf, len);

         GetData(buf);
      } else if( state == EnterItem )
         SetState(EnterBoard);
   }
   return 0;
}

LRESULT WhForm::ScanData(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   std::wstring data;
   if( GetScan(&data) )
   {
      GetDlgItem(IDC_ITEM_CODE).SetWindowText(data.c_str());
      GetData(data.c_str());
   }
   return 0;
}

void WhForm::GetData(const wchar_t* id)
{
   if( inNet )
      return;

   if( data->forSend )
      return;

   DWORD qty;
   switch( state )
   {
   case EnterBoard:
      curBoard.id = (wchar_t*)id;
      if( curBoard.Read() )
         SetState(EnterItem);
      else
      {
         MessageBox(L"Нет полки с таким кодом", L"Ошибка", MB_OK | MB_ICONSTOP);
         GetDlgItem(IDC_ITEM_CODE).SetWindowText(L"");
      }
      break;

   case EnterQty:
      qty = GetValue(GetDlgItem(IDC_QTY), QTY_SCALE);
      data->doc->Add(curBoard.id, curItem.id, qty);
      // fall throught
   case EnterItem:
      curItem.id = (wchar_t*)id;
      if( curItem.Read() )
         SetState(EnterQty);
      else
      {
         curBoard.id = (wchar_t*)id;
         if( curBoard.Read() )
            SetState(EnterItem);
         else
            MessageBox(L"Нет информации по такому коду", L"Ошибка", MB_OK | MB_ICONSTOP);
      }
      break;
   }
}

//
///---------------------------------- WhDocImpl ----------------------------
//
WhDocImpl::WhDocImpl() : DBImpl(L"whdoc")
{
   SYSTEMTIME st;
   GetLocalTime(&st);

   SystemTimeToFileTime(&st, &created);
   params = 0;
   remark = L"";

   const AgentsImpl* a = _Module.Agent();
   user = (a) ? holder.Add(a->id) : L"";
}

DWORD WhDocImpl::GetQty(const wchar_t* board, const wchar_t* item) const
{
   vector_t<WHDocItem>::const_iterator i = items.begin();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->board, board) == 0 && wcscmp(i->id, item) == 0 )
         return i->qty;
   }

   return 0;
}

void WhDocImpl::Add(const wchar_t* board, const wchar_t* item, DWORD qty) // 0 - remove
{
   vector_t<WHDocItem>::iterator i = items.begin(), bi = items.end();
   for( ; i != items.end(); i++ )
   {
      if( wcscmp(i->board, board) == 0 )
      {
         bi = i;
         if( wcscmp(i->id, item) == 0 )
            break;
      }
   }

   if( i != items.end() )
   {
      if( qty == 0 ) items.erase(i);
      else i->qty = qty;
   } else
   {
      WHDocItem di;
      di.board = holder.Add(board);
      di.id = holder.Add(item);
      di.qty = qty;
      if( bi != items.end() ) items.insert(++bi, di);
      else items.push_back(di);
   }

   Write();
}

void OpenDoc(WhDocImpl* doc, bool forSend)
{
   _Module.GetFrame()->Load(IDD_QTY, new WhData(doc, forSend));
}
