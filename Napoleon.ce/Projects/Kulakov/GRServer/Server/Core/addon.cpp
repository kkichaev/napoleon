/*
 * Copyright (C), 2009-2010, ─хэшё ╠юё ушэ
 *
 * Add on - фюяюыэхэш  фы  Ёрчэ√ї ъышхэЄют
 *
 * ert   16/06/2010   creating
 */ 
#include "stdafx.h"
#include <vector>
#include <map>
#include "server.h"
#include "servobj.h"
#include "objdef.h"
#include "parse.h"
#include "datasource.h"
#include "session.h"

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

#include "creators.h"
#include "objects.h"
#include "sources.h"
#include "dbf.h"
#include "StdConsts.h"

using namespace GRServer;
using namespace std;

const int DEFAULT_WH = 6;

struct SbisSourceCreator : public DBFCreatorBase
{
   virtual const wchar_t* Name() const { return L"SbisOrderTable"; }

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object, 
      FilterReader::Data& filter, const ParamList& parameters) const { return NULL; }
};

struct SbisItemSourceCreator : public DBFCreatorBase
{
   virtual const wchar_t* Name() const { return L"SbisOrderItemTable"; }

   virtual IDataSource::IWriter* CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const;
   virtual IDataSource::IReader* Create(const std::string& fileName, const ISessionObject& object, 
      FilterReader::Data& filter, const ParamList& parameters) const { return NULL; }
};

class OrderItem
{
public:
   std::string id;
   double qty;
   double cost;
};

static const wchar_t* orderSFields[] = { L"id", L"userid", L"remark", L"ctype" };
static const wchar_t* orderDFields[] = { L"date", L"created" };
static const wchar_t* orderIFields[] = { L"id", L"qty", L"cost" };

class Order 
{
public:
   std::string id;
   std::string userid;

   FILETIME date, created;
   std::string remark;
   std::string ctype;

   std::string userName;

   WORD code;

   std::vector<OrderItem> items;

   bool Init(const ISessionObject& object);
   bool Read(const Object& o);

   double Sum() const
   {
      double s = 0;
      std::vector<OrderItem>::const_iterator i = items.begin();
      for( ; i != items.end(); i++ )
         s += i->cost * i->qty;
      return s;
   }

protected:
   int si[4], di[2], iitems;
   int ii[3];
};


class SbisWriter : public IDataSource::IWriter
{
public:
   SbisWriter() {}

   virtual bool Prepare(const ISessionObject& object)
   {
      exchangeFolder = object.GetSession().Config().ExchangeFolder();
      return order.Init(object);
   }
   virtual bool Write(const Object& o, RowID *rid);
   virtual void Close() {}

protected:
   Order order;
   std::string exchangeFolder;
};

class SbisItemWriter : public IDataSource::IWriter
{
public:
   SbisItemWriter() {}

   virtual bool Prepare(const ISessionObject& object) { return true; }
   virtual bool Write(const Object& o, RowID *rid) { return true; }
   virtual void Close() {}
};

DBRec billFields[] = {
   {"Дата",'D',8,0},
   {"Номер",'N',9,0},
   {"Название",'C',80,0},
   {"Сумма",'N',20,2},
   {"Сумма2",'N',20,2},
   {"Курс",'N',20,5},
   {"$Сумма",'N',20,5},
   {"ВидЦены",'C',20,0},
   {"Тема",'C',80,0},
   {"Примечание",'C',80,0},
   {"Получение",'D',8,0},
   {"Срок оплаты",'D',8,0},
   {"Группа нуме",'N',9,0},
   {"Лицо1",'N',9,0},
   {"Лицо2",'N',9,0},
   {"Правила-Док",'N',9,0},
},
billaFields[] = {
   {"N п/п",'N',9,0},
   {"N склада",'N',9,0},
   {"НомНомер",'C',MAX_ITEM_ID,0},
   {"Наименовани",'C',80,0},
   {"Кол_во",'N',20,5},
   {"Цена",'N',20,5},
   {"СуммаЦен",'N',20,2},
   {"Курс",'N',20,5},
   {"дата",'D',8,0},
   {"Комментарий",'C',80,0},
   {"Цена1",'N',20,2},
   {"Цена2",'N',20,5},
   {"СвязьС",'N',9,0},
},
billbFields[]= {
   {"Дата",'D',8,0},
   {"Номер",'N',9,0},
   {"Сумма",'N',20,2},
   {"N",'N',9,0},
   {"Вид связи",'C',20,0},
   {"DNUM",'N',9,0},
   {"DDATE",'D',8,0},
   {"DTYPE",'C',20,0},
   {"DGRNUM",'N',9,0},
   {"Связка2",'N',9,0},
},
faceFields[]= {
   {"Наименовани",'C',80,0},
   //  {"ИНН",'C',12,0},
   {"ИНН",'C',MAX_ORG_ID,0},
   {"ОКПО",'C',80,0},
   {"ОКОНХ",'C',80,0},
   {"Название",'C',80,0},
   {"Адрес",'C',80,0},
   {"Телефон",'C',80,0},
   {"Примечание",'C',80,0},
   {"Субсчет",'C',80,0},
   {"ФИО",'C',50,0},
   {"Паспорт",'C',50,0},
   {"Свидетельст",'C',50,0},
   {"Дата рожден",'C',50,0},
   {"Параметр1",'C',50,0},
   {"ТабНомер",'C',50,0},
   {"N склада",'C',50,0},
   {"Тема",'C',50,0},
   {"ИнвНомер",'C',50,0},
   {"TYPE",'C',50,0},
};

bool Order::Init(const ISessionObject& iobject)
{
   USES_CONVERSION;

   const SessionObject& object = *(const SessionObject*)iobject.Self();
   const Session& s = (const Session&)object.GetSession();
   userName = W2A_CP(s.GetUser().UserName(), CP_OEMCP);

   const GRServer::Format *f = object.format;
   int i, idx;
   for(i = 0; i < 4; i++ )
   {
      idx = f->FindMember(orderSFields[i]);
      if( idx < 0 ) return false;
      si[i] = idx;
   }

   for(i = 0; i < 2; i++ )
   {
      idx = f->FindMember(orderDFields[i]);
      if( idx < 0 ) return false;
      di[i] = idx;
   }

   iitems = f->FindMember(L"items");
   const ISessionObject* so = object.GetChild(L"items");
   if( so == NULL ) return false;

   f = ((SessionObject*)so->Self())->format;
   for(i = 0; i < 3; i++ )
   {
      idx = f->FindMember(orderIFields[i]);
      if( idx < 0 ) return false;
      ii[i] = idx;
   }

   return (iitems >= 0);
}

bool Order::Read(const Object& o)
{
   USES_CONVERSION;
   std::string* svals[] = { &id, &userid, &remark, &ctype };
   int i;
   for( i=0; i<4; i++ )
      svals[i]->assign(W2A_CP(o.at(si[i]).str->c_str(), CP_OEMCP));

   FILETIME *ft[] = { &date, &created };
   for( i=0; i<2; i++ )
      *ft[i] = o.at(di[i]).datetime;

   code = 0;
   int ididx = id.find('\t');
   if( ididx >= 0 )
   {
      code = atoi(id.substr(ididx+1).c_str());
      id = id.substr(0, ididx);
   }

   items.clear();
   const ServObject* so = o.at(iitems).object;
   if( so )
   {
      ServObject::const_iterator ci = so->begin();
      for( ; ci != so->end(); ci++ )
      {
         OrderItem oi;
         oi.id = W2A_CP((*ci)->at(ii[0]).str->c_str(), CP_OEMCP);
         oi.qty = (*ci)->at(ii[1]).number;
         oi.cost = (*ci)->at(ii[2]).number;

         items.push_back(oi);
      }
   }
   return true;
}

static bool OpenBase( DataForm& base, const char* name, const char *path, int nEls, DBRec* fields )
{
   char fullName[MAX_PATH];
   strcpy(fullName,path);
   if( path[strlen(path)-1] != '\\' )
      strcat(fullName,"\\");
   strcat(fullName,name);

   if( base.Open(fullName) == False )
   {
      if( nEls )
         return (base.Create(fullName,nEls,fields)==True) ? true : false;
      return false;
   }
   return true;
} 

static int GetConfigKey(const char* ep, const char *key)
{
   int val = 0;
   std::string fn(ep);
   fn += "order.cfg";
   FILE *f = fopen(fn.c_str(), "rt");
   if( f == NULL )
      return DEFAULT_WH;
   char buf[200];
   int len = strlen(key) - 1;
   while(true)
   {
      if( fgets(buf, sizeof(buf), f) == NULL )
         break;

      if( strncmp(buf, key, len) == 0 )
      {
         const char *p = strchr(buf, '=');
         if( p )
         {
            val = atoi(p+1);
            break;
         }
      }
   }
   fclose(f);

   return val;
}

static long WriteFaceRecord( DataForm &face, const char *inn )
{
   face.ResetRec();
   face.Fill("ИНН",inn);
   face.Fill("TYPE", "Организации");
   face.Append();

   return face.GetRecNo();
}

static long WriteEmplRecord( DataForm &face, const char *tn )
{
   face.ResetRec();
   face.Fill("TYPE", "Сотрудники");
   face.Fill("ТабНомер", tn);
   face.Append();

   return face.GetRecNo();
}

static void WriteBillDetail(DataForm &billa, long orderPos, const Order &order, int whno)
{
   std::vector<OrderItem>::const_iterator i = order.items.begin();
   int ctr = 1;
   for( ; i != order.items.end(); i++ )
   {
      billa.ResetRec();

      billa.Fill("НомНомер", i->id.c_str());

      //CharToOemBuff(prcItem->name,buf,sizeof(prcItem->name));
      //buf[sizeof(prcItem->name)] = '\0';
      //billa.Fill("Наименовани",buf );

      double val;
      val = i->cost;

      double qty = i->qty;
      billa.Fill("СуммаЦен", val*qty);
      billa.Fill("СвязьС",(int)orderPos);
      billa.Fill("N склада", whno);
      billa.Fill("N п/п", ctr++);
      billa.Fill("Кол_во", qty);
      billa.Append();
   }
}

static const char* OrgName(std::string* tstr, const char *ep, const char *id, int code, const char *user)
{
   tstr->clear();

   char buf[50];
   DataForm orgs;
   std::string fileName(ep);

   wsprintfA(buf, "%s\t%3d", id, code);

   fileName += "O"; fileName += user;
   if( orgs.Open(fileName.c_str()) == false )
      return "";

   for( long rc=0; orgs.ReadRec(rc); rc++ )
   {
      if( orgs.IsDelete() == True )
         continue;

      std::string strBuf;
      if( !strcmp(Trunc(orgs["ID"], &strBuf), buf) )
      {
         Trunc(orgs["NAME"], tstr);
         break;
      }
   }

   return tstr->c_str();
}

static long WriteBillRecord(DataForm &bill, long faceRecNo, long emplRecNo, const Order &item, 
                            int orderNo, const char* ef)
{
   bill.ResetRec();

   double val = item.Sum();
   bill.Fill("Сумма",val);

   SYSTEMTIME st;
   char timeBuf[10];

   FileTimeToSystemTime(&item.created, &st);
   wsprintfA(timeBuf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   bill.Fill("SDATE", timeBuf);
 
   wsprintfA(timeBuf, "%02d:%02d", st.wHour, st.wMinute);
   bill.Fill("STIME", timeBuf);
 
   FileTimeToSystemTime(&item.date, &st);

   wsprintfA(timeBuf, "%d%02d%02d", st.wYear, st.wMonth, st.wDay);
   bill.Fill("Дата",timeBuf);
   bill.Fill("Срок оплаты",timeBuf); 

   char remBuf[150];
   char sumTypeBuf[100];
   strcpy(sumTypeBuf, item.ctype.c_str());

   *remBuf = '\0';
   if( item.code != 0 )
      wsprintfA(remBuf, "Катег%3d;", item.code);

   if( st.wHour || st.wMinute )
   {
      if( *remBuf )
         strcat(remBuf, ";");

      strcat(remBuf,"время ");
      wsprintfA(remBuf+strlen(remBuf),"%02d:%02d",st.wHour,st.wMinute);
   }
 
   if( item.remark.empty() == false )
   {
      if( *remBuf )
         strcat(remBuf, ";");
      strcat(remBuf, item.remark.c_str());
   }
 
   extern char *szSuppl;
 

   bill.Fill("Примечание", remBuf);

   bill.Fill("ВидЦены", sumTypeBuf);

   bill.Fill("Лицо1",(int)faceRecNo);
   bill.Fill("Лицо2",(int)emplRecNo);
   bill.Fill("Номер",orderNo);
   bill.Fill("Правила-Док", -1);
 
   GetLocalTime(&st);

   std::string tstr;
   wsprintfA( remBuf, "%02d:%02d %s:%s", st.wHour, st.wMinute, item.userName.c_str(), 
      OrgName(&tstr, ef, item.id.c_str(), item.code, item.userid.c_str()));

   bill.Fill("Название", remBuf);

   bill.Append();
   return bill.GetRecNo();
}

bool SbisWriter::Write(const Object& o, RowID *rid)
{
   if( !order.Read(o) )
      return false;

   DataForm bill, billa, billb, face;

   const char* ep = exchangeFolder.c_str();

   if( OpenBase(bill, "bill", ep, NumItems(billFields), billFields) == false )
      return false;
   if( OpenBase(billa, "billa", ep, NumItems(billaFields), billaFields) == false )
      return false;
   if( OpenBase(billb, "billb$", ep, NumItems(billbFields), billbFields) == false )
      return false;
   if( OpenBase(face, "face", ep, NumItems(faceFields), faceFields) == false )
      return false;

   int orgRec = WriteFaceRecord(face, order.id.c_str());
   int emplRec = WriteEmplRecord(face, order.userid.c_str());
   int orderRec = WriteBillRecord(bill, orgRec, emplRec, order, bill.GetRecNo(), ep);
   
   int whno = GetConfigKey(ep, "═юьхЁ ёъырфр");
   WriteBillDetail(billa, orderRec, order, whno);

   return true;
}

IDataSource::IWriter* SbisSourceCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return new SbisWriter();
}

IDataSource::IWriter* SbisItemSourceCreator::CreateWriter(IDataSource::IWriter* parent, const ParamList& parameters, const ISessionObject& object) const
{
   return new SbisItemWriter();
}

bool GRServer::AddOnInit()
{
   DataSource::AddCreator(new SbisSourceCreator());
   DataSource::AddCreator(new SbisItemSourceCreator());

   return true;
}

