/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Обмен объектами
 *
 *  ert   16/08/2007   creating
 */
#include "stdafx.h"
#include "Module.h"
#include <StdFuncs.h>
#include "MainFrame.h"
#include <SQLTable.h>

#include "ObjImpl.h"

#include <NetExchange.h>
#include <DataReader.h>
#include <ServerDefs.h>
#include <StdFuncs.h>

#include "Progress.h"
#include "ObjExchange.h"
#include "DocImpl.h"
#include "Invoice.h"
#include "FormEntries.h"
#include <PrfDlg.h>

class ServObjRcvr : public IReceiveObject
{
public:
   ServObjRcvr(IServObject* object) : reader(NULL) { this->object = object; }
   
   virtual const wchar_t* Name() const { return object->DataType().Name(); }
   virtual const wchar_t* ProgressText() const { return L""; }

   virtual const wchar_t* Command() const { return L""; }
   virtual const wchar_t* Params() const { return L""; }

   virtual bool Read(ReceivedStream* stream)
   {
      if( reader == NULL )
         reader = DataReader::CreateReader(object->GetSelf()->GetType(), stream);

      bool res = false;
      if( reader )
      {
         IReflectableData* data = object->GetSelf();
         if( reader->Read(data, stream) )
         {
            object->UnbindData();
            res = true;
         }
      }
      return res;
   }

   virtual void Close() { delete reader; }

protected:
   IServObject* object;
   DataReader* reader;
};

static bool SendCommand(NetworkExchange& net, const wchar_t* pcmd)
{
   OutStream cmdStream;
   StringHolder holder;
   ServerCommand cmd;

   MakeServerCommand(&cmd, &holder, NULL, NULL, pcmd, L"");

   const DataReflector& type = cmd.GetType();
   type.ToStream(&cmdStream);
   type.DataToStream(&cmdStream, cmd);

   return net.Send(cmdStream, NULL);
}

DWORD ObjExchange(SendObjParam* param)
{
   OutStream stream;
   NetworkExchange net;
   StringHolder holder;
   ServerCommand cmd;
   IPAddress addr;
   Preference p;
   p.Load();

   MakeServerCommand(&cmd, &holder, 
      ((p.flags & opfOnLineUseIP1) != 0) ? &addr : NULL, 
      ((p.flags & opfOnLineUseIP1) != 0) ? NULL : &addr, 
      OBJECTS_COMMAND, param->command);

   const DataReflector& type = cmd.GetType();
   type.ToStream(&stream);
   type.DataToStream(&stream, cmd);

   const wchar_t *alias = param->object->DataType().Name();
   IReflectableData* data = param->object->GetSelf();
   const DataReflector& dataType = data->GetType();

   dataType.ToStream(&stream, alias);
   dataType.DataToStream(&stream, *data);

   if( param->pi != NULL )
      param->pi->SetText(L"Передача...");

   param->ec = 1;
   param->answer = L"Сервер не отвечает";

   net.SetTimeout(NETWORK_TIMEOUT * 120); // 20 минут
   ReceivedStream* ostream = net.Receive(&addr, NULL, stream, NULL);
   if( ostream )
   {
      ostream->PrepareRead();

      // Auth
      bool good = CheckAnswer(ostream, &param->answer);
      if( good )
      {
         SendCommand(net, BYE_COMMAND);

         // obj result
         good = CheckAnswer(ostream, &param->answer);
         if( good )
         {
            if( param->pi != NULL )
               param->pi->SetText(L"Обработка...");

            ServObjRcvr sor(param->object);
            ReceiveObjects rcvObj;
            rcvObj.push_back(&sor);

            good = ProcessStream(ostream, rcvObj, NULL, NULL);
            if( good )
               param->ec = 0;
            else
               param->answer = L"Ошибка при обработке";
         }
      }
      delete ostream;
   }

   return 0;
}

int ObjectExchange(IServObject *object, const wchar_t* command, std::wstring *answer)
{
   ProgressWindow pw;
   SendObjParam param;

   pw.CreateSTDWindow(GetActiveWindow());

   param.object = object;
   param.command = command;
   param.pi = &pw;

   HANDLE thread = CreateThread(NULL, 0, (LPTHREAD_START_ROUTINE)ObjExchange, &param, 0, NULL);
   _Module.WaitThreadComplete(thread);
   *answer = param.answer;

   pw.DestroyWindow();
   return param.ec;
}

//
// ------------------------------------------ OnlineProperties -------------------------------------
//
OnlineProperties::OnlineProperties() : PrefPage(IDC_ONLINE_PREFERENCE, L"On-Line")
{
}

void OnlineProperties::Init()
{
   const Preference &preference = ((PreferenceDialog*)owner)->GetPreference();

   CComboBox ip(GetDlgItem(IDC_IP));

   ConfigImpl cfg;
   ip.AddString(cfg.LoadIP(ConfigImpl::IP1, preference));
   ip.AddString(cfg.LoadIP(ConfigImpl::IP2, preference));

   if( (preference.flags & opfOnLineUseIP1) != 0 ) ip.SetCurSel(0);
   else ip.SetCurSel(1);

   if( (preference.flags & opfSendOnLine) != 0 )
       CheckDlgButton(IDC_ORDER_ONLINE, BST_CHECKED);
}

void OnlineProperties::Save(Preference *preference)
{
   if( m_hWnd != NULL )
   {
      if( IsDlgButtonChecked(IDC_ORDER_ONLINE) == BST_CHECKED ) preference->flags |= opfSendOnLine;
      else preference->flags &= (~opfSendOnLine);

      if( ((CComboBox)GetDlgItem(IDC_IP)).GetCurSel() == 0 ) preference->flags |= opfOnLineUseIP1;
      else preference->flags &= (~opfOnLineUseIP1);
   }
}

//
// ------------------------------------------ InvoiceData -------------------------------------
//
static void CopyData(DeliveryImpl* dest, const OrderImpl &src)
{
   ClearMembers(dest);

   dest->number = src.number;
   dest->id = src.id;
   dest->date = src.date;
   dest->created = src.created;

   dest->items.clear();

   vector_t<OrderItem>::const_iterator i = src.items.begin();
   for( ; i != src.items.end(); i++ )
   {
      if( i->qty > 0 )
      {
         DeliveryItem di;
         di.id = i->id;
         di.qty = i->qty;
         di.sum = ItemSum(i->cost, i->qty);

         dest->items.push_back(di);
      }
   }

   dest->UnbindStrings();
   dest->Write();
}

//#ifdef TKSibir
//static void RefreshData(OrderImpl* dest, const OrderImpl& src)
//{
//   dest->items.clear();
//
//   vector_t<OrderItem>::const_iterator i = src.items.begin();
//   for( ; i != src.items.end(); i++ )
//   {
//      if( i->qty > 0 )
//      {
//         OrderItem di;
//         di.id = dest->holder.Add(i->id);
//         di.qty = i->qty;
//         di.cost = i->cost;
//
//         dest->items.push_back(di);
//      }
//   }
//
//   //dest->UnbindStrings();
//   //dest->Write();
//}
//#endif

bool InvoiceData::Send()
{
   Preference p;
   p.Load();

   if( (p.flags & opfSendOnLine) == 0 )
      return order->Send();

   if( order->params & ofProceeded )
   {
      if( MessageBox(GetActiveWindow(), L"Документ обработан. Хотите передать его еще раз?", 
         L"Вопрос", MB_YESNO | MB_ICONQUESTION) != IDYES )
      {
         return false;
      }
   }

   bool ret = false;
   std::wstring answer;
   ServObject<OrderImpl> o;

   CopyData(&o, *order);
   int res = ObjectExchange(&o, WRITE_OBJECTS, &answer);

   const wchar_t* msg = NULL;
   std::wstring buf;
   const wchar_t* title = NULL;
   DWORD addFlag = 0;
   if( res != 0 )
   {
      _Module.ShowErrorBox(res, answer.c_str(), L"Ошибка: ");
   } else
   {
      if( o.servResult == RESULT_FAIL )
      {
         title = L"Ошибка проведения";
         buf = o.servResponse;
         msg = buf.c_str();
         addFlag = MB_ICONSTOP;

         if( *o.number != L'\0' )
         {
            DeliveryImpl d;
            CopyData(&d, o);

            order->number = order->holder.Add(o.number);
            order->Write();
         }
      } else
      {
         if( *o.number == L'\0' )
         {
            msg = L"Документ сохранен без номера!";
            title = L"Ошибка проведения";
            addFlag = MB_ICONSTOP;
         } else
         {
//#ifdef TKSibir
//            RefreshData(order, o);
//#else
            DeliveryImpl d;
            CopyData(&d, o);
//#endif

            title = L"Информация";
            addFlag = MB_ICONINFORMATION;

            order->ClearDirty(NULL, false);

            if( o.servResult == RESULT_SAVE )
            {
               msg = L"Документ сохранен не полностью";
               order->params &= (~ofExported);
            } else if( o.servResult == RESULT_COMMIT )
            {
               msg = L"Документ успешно проведен";
               order->params |= ofProceeded;
            }

//#ifdef TKSibir
//#else
            order->number = order->holder.Add(o.number);
//#endif
            order->Write();
         }
      }
   }

   if( *o.servResponse != L'\0' )
      msg = o.servResponse;

   if( msg && title )
      MessageBox(GetActiveWindow(), msg, title, MB_OK | addFlag);

   OrderImpl *sv = order;
   order = NULL;
   OpenInvoice(sv, retToDocList);
   return ret;
}
