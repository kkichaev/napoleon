/*
* Copyright (C), 2007-2009, Денис Мосягин
*
* Сообщения
*
*  ert   22/07/2009   creating
*/ 
#include "stdafx.h"
#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <ListForm.h>

#include "FormEntries.h"
#include "DocType.h"
#include "ObjImpl.h"

static ListFormData::Header header[] = 
{
   { ListFormData::Header::Left, L"Дата", L"date", 15 },
   { ListFormData::Header::Left, L"Сообщение", L"message", 50 }
};

struct MessageData : public ListFormData
{
   MessageData();

   virtual const Header *GetHeader() const { return header; }
   virtual int ColumnsCount() const { return sizeof(header)/sizeof(header[0]); }

   virtual const DataReflector& DataType() const { return Message().GetType(); }
   virtual int Count() const { return recs.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual bool Editing(int index) { return Selecting(index); }
   virtual bool Selecting(int index);
   virtual bool Removing(int index);

   bool LoadMessage(int index, Message *message, std::wstring *buf) const;

   void RemoveAll()
   {
      SQLTable::DropTable(message.Name());
      recs.clear();
   }

   std::vector<ROWID> recs;
   mutable MessageImpl message;
   mutable std::wstring msg;
};

class MessageList : public ListForm
{
 public:
   MessageList() {}

   virtual bool SetData(IFormData *_data);

   virtual DWORD GetResourceID() const { return IDD_MESSAGE_LIST; }

   BEGIN_MSG_MAP(MessageList)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)    
      COMMAND_ID_HANDLER(IDC_REMOVE_ORDERS, RemoveMessages)    
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   DECLARE_FORM(MessageList, IDD_MESSAGE_LIST)

   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      OpenOrgList(dtOrder);
      return 0;
   }

   LRESULT RemoveMessages(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled)
   {
      if( MessageBox( L"Удалить все сообщения?", L"Вопрос", MB_YESNO|MB_ICONQUESTION) == IDYES )
      {
         ((MessageData*)data)->RemoveAll();
         Refresh();
      }
      return 0;
   }
};

IMPLEMENT_FORM(MessageList)

MessageData::MessageData()
{
   const DataReflector &r = message.GetType();
   MemberType &mt = (MemberType&)r.Type(L"date");
   
   DateTimeType::DateTimeFormat df;
   df.appearance = DateTimeType::DateTimeFormat::DateTime;
   mt.SetFormat(df);

   SQLTable table(message.Name());

   table.RIDList(&recs, L"ORDER BY date");
}

bool MessageData::LoadMessage(int index, Message *m, std::wstring *buf) const
{
   if( index >= (int)recs.size() ) return false;

   message.Read(recs[index]);
   
   buf->assign(message.message);
   m->message = (wchar_t*)buf->c_str();
   m->date = message.date;

   return true;
}

bool MessageData::Get(IReflectableData* data, int index) const
{
   if( !LoadMessage(index, (Message*)data, &msg) ) return false;

   std::wstring::size_type pos = msg.find(L'\n');
   while( pos != std::wstring::npos )
   {
      msg.replace(pos, 1, 1, L' ');
      pos = msg.find(L'\n', pos);
   }
   ((Message*)data)->message = (wchar_t*)msg.c_str();

   return true;
}

bool MessageData::Selecting(int index)
{
   MessageImpl m;
   std::wstring buf;
   if( LoadMessage(index, &m, &buf) )
      m.Show();

   return false;
}

bool MessageData::Removing(int index)
{
   return false;
}

bool MessageList::SetData(IFormData *_data)
{
   if( !ListForm::SetDataEx(_data, 2) ) return false;

   menuBar.m_hWnd = NULL;
   menuBar.Attach(_Module.GetFrame()->LoadMenuBar(GetMenuBarID(), 0, 0));

   UpdateLayout(false);   
   return true;
}

#include <BaseDialog.h>
class MsgDialog : public BaseDialog
{
public:
   MsgDialog(const Message& _msg) : BaseDialog(IDC_MESSAGE), msg(_msg) {}

   BEGIN_MSG_MAP(CAboutDlg)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      MESSAGE_HANDLER(WM_SIZE, OnSize)
      CHAIN_MSG_MAP(BaseDialog)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);

protected:
   CScrollContainer container;
   CStatic text;
   const Message& msg;
};

LRESULT MsgDialog::OnSize(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled)
{
   CRect bounds;
   WORD wdh = LOWORD(lParam), hgh = HIWORD(lParam);

   MoveButtons(wdh, hgh);
   GetDlgItemRect(bounds, IDOK);
   container.MoveWindow(offset, nTitleHeight + offset, wdh - 2 * offset, bounds.top - nTitleHeight - 2 * offset);

   return TRUE;
}

LRESULT MsgDialog::OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled)
{
   RECT bounds = {0};
   HDC dc = GetDC();
   DrawText(dc, msg.message, -1, &bounds, DT_CALCRECT);
   ReleaseDC(dc);

   std::wstring ttext(L"Сообщение от ");
   SYSTEMTIME st;
   wchar_t buf[50];
   FileTimeToSystemTime(&msg.date, &st);
   GetDateFormatW(LOCALE_USER_DEFAULT, DATE_SHORTDATE, &st, NULL, buf, sizeof(buf)/sizeof(buf[0]));
   ttext += buf;
   SetWindowText(ttext.c_str());


   container.Create(m_hWnd, NULL, NULL, WS_CHILD | WS_VISIBLE);
   text.Create(container.m_hWnd, &bounds, msg.message, WS_CHILD | WS_VISIBLE);
   container.SetClient(text);

   //CWindow wnd(GetDlgItem(IDC_TEXT));
   //wnd.SetWindowText(msg);

   bHandled = FALSE;
   return FALSE;
}


void MessageImpl::Show()
{
   MsgDialog dlg(*this);
   dlg.DoModal();
}

void OpenMessageList()
{
   _Module.GetFrame()->Load(IDD_MESSAGE_LIST, new MessageData());
}

