/*
 * Copyright (C), 2007, ─хэшё ╠юё ушэ
 *
 * ╨хрышчрЎш  ёшэїЁюэшчрЎшш чрърчр
 *
 *  ert   09/09/2007   creating
 */ 
#include "stdafx.h"

#include <atldef.h>

#include <StringHolder.h>
#include <dbf.h>

#include <fcntl.h>

#include <algorithm>

#include <exchange.h>
#include <sync.h>
#include "Server.h"
#include <Config.h>

#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

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

//
//------------------------------------ Sync Order -------------------------------------
//
static long GetValue(const char *p, const char *ep)
{
   long val = 0;
   while( p != ep && *p )
   {
      val *= 10;
      val += *p - '0';
      p++;
   }
   return val;
}

const char* SyncOrder::FileName() const
{ 
   return "bill.dbf"; 
}

bool SyncOrder::SetFromDB(IReflectableData *_data, const DataForm &db, StringHolder*) const
{
   return false;
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

static void WriteBillDetail(DataForm &billa, long orderPos, const OrderSend &order, int whno)
{
   USES_CONVERSION;
   std::vector<OrderItemSend>::const_iterator i = order.items.begin();
   int ctr = 1;
   for( ; i != order.items.end(); i++ )
   {
      billa.ResetRec();

      billa.Fill("НомНомер", W2A_CP(i->id, CP_OEMCP));

      //CharToOemBuff(prcItem->name,buf,sizeof(prcItem->name));
      //buf[sizeof(prcItem->name)] = '\0';
      //billa.Fill("Наименовани",buf );

      double val;
      val = ((double)i->cost)/SUM_SCALE;

      double qty = ((double)i->qty)/QTY_SCALE;
      billa.Fill("СуммаЦен", val*qty);
      billa.Fill("СвязьС",(int)orderPos);
      billa.Fill("N склада", whno);
      billa.Fill("N п/п", ctr++);
      billa.Fill("Кол_во", qty);
      billa.Append();
   }
}

static void SetDate(DateType &dta, struct tm &sTime)
{
   wsprintf((char*)dta.month,"%02d",sTime.tm_mon+1);
   wsprintf((char*)dta.day,"%02d",sTime.tm_mday);
   _itoa(sTime.tm_year+1900,(char*)dta.year,10);
}

const char* OrgName(const char *exchangePath, const char *orgID, const char *userID);
 
// ansi <-> oem check
static long WriteBillRecord(DataForm &bill, long faceRecNo, long emplRecNo, const OrderSend &item, 
                            int orderNo, const NapoleonConfig &config, int code, int code1, const char *userName, const char *userID)
{
   USES_CONVERSION;

   bill.ResetRec();
   Summing orderSum;
   double val = std::for_each(item.items.begin(), item.items.end(), orderSum);
   bill.Fill("Сумма",val);

   struct tm locTime;
   DateType dta;

   SYSTEMTIME st;

   FileTimeToSystemTime(&item.created, &st);
   locTime.tm_mday = st.wDay;
   locTime.tm_mon = st.wMonth-1;
   locTime.tm_year = st.wYear-1900;
   SetDate(dta, locTime);
   bill.Fill("SDATE", &dta);
 
   char timeBuf[10];
   wsprintf(timeBuf, "%02d:%02d", st.wHour, st.wMinute);
   bill.Fill("STIME", timeBuf);
 
   FileTimeToSystemTime(&item.date, &st);

   locTime.tm_mday = st.wDay;
   locTime.tm_mon = st.wMonth-1;
   locTime.tm_year = st.wYear-1900;
   SetDate(dta, locTime);
   bill.Fill("Дата",&dta);
   bill.Fill("Срок оплаты",&dta); 

   char remBuf[150];
   char sumTypeBuf[100];
   strcpy(sumTypeBuf, config.GetStringItem(COST_TYPE, item.sumType));

   *remBuf = '\0';
   if( code1 != 0 )
      wsprintf(remBuf, "╩Єу%d;", code1);
   else if( code != 0 )
      wsprintf(remBuf, "╩рЄху%3d;", code);

   if( st.wHour || st.wMinute )
   {
      if( *remBuf )
         strcat(remBuf, ";");

      strcat(remBuf,"тЁхь  ");
      wsprintf(remBuf+strlen(remBuf),"%02d:%02d",st.wHour,st.wMinute);
   }
 
   DWORD dwParams = item.params;
   if( dwParams & ofCash )
   {
      if( *remBuf )
         strcat(remBuf, ";");

      strcat(remBuf,"эр ЁхрышчрЎш■");
   }
   if( *item.remark )
   {
      if( *remBuf )
         strcat(remBuf, ";");
      strcat(remBuf, W2A(item.remark));
   }
 
   extern char *szSuppl;
 
   if( st.wHour || st.wMinute )
   {
      strcat(remBuf, ";тЁхь  ");
      wsprintf(remBuf+strlen(remBuf), "%02d:%02d", st.wHour, st.wMinute);
   }


   CharToOem(remBuf, remBuf);
   bill.Fill("Примечание", remBuf);

   CharToOem(sumTypeBuf, sumTypeBuf);
   bill.Fill("ВидЦены", sumTypeBuf);

   bill.Fill("Лицо1",(int)faceRecNo);
   bill.Fill("Лицо2",(int)emplRecNo);
   bill.Fill("Номер",orderNo);
   bill.Fill("Правила-Док", -1);
 
   if( userName && *userName )
   {
      SYSTEMTIME st;
      GetLocalTime(&st);

      wsprintf( remBuf, "%02d:%02d %s:%s", st.wHour, st.wMinute, userName, 
         OrgName(ExchangeFolder(), W2A_CP(item.id, CP_OEMCP), userID));

      bill.Fill("Название", remBuf);
   }

   bill.Append();
   return bill.GetRecNo();
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

bool SyncOrder::SetToDB(DataForm *db, const IReflectableData &_data) const
{
   DataForm billa, billb, face;

   const char *ep = ExchangeFolder();
   if( OpenBase(billa, "billa", ep, NumItems(billaFields), billaFields) == false )
      return false;
   if( OpenBase(billb, "billb$", ep, NumItems(billbFields), billbFields) == false )
      return false;
   if( OpenBase(face, "face", ep, NumItems(faceFields), faceFields) == false )
      return false;

   USES_CONVERSION;
   const OrderSend &order = (const OrderSend&)_data;
   char innbuf[100];
   int code, code1;
   strncpy(innbuf, W2A_CP(order.id, CP_OEMCP), sizeof(innbuf));
   innbuf[sizeof(innbuf)-1] = '\0';
   char *p = strchr(innbuf, '\t');
   code1 = 0;
   if( p )
   {
      *p = '\0';
      code = atoi(p+1);
   } else
   {
      code = 0;

      DataForm orgs;
      std::string fileName(ExchangeFolder()), orgID;

      orgID = W2A_CP(order.id, CP_OEMCP);
      
      SyncOrg so(userID);
      fileName += so.FileName();
      if( orgs.Open(fileName.c_str()) )
         for( long rc=0; orgs.ReadRec(rc); rc++ )
         {
            if( orgs.IsDelete() == True )
               continue;

            if( !strcmp(Trunc(orgs["ID"]), orgID.c_str()) && strcmp(Trunc(orgs["INN"]), orgID.c_str()) )
            {
               strncpy(innbuf, Trunc(orgs["INN"]), sizeof(innbuf));
               code1 = atoi(orgID.c_str());
            }
         }
   }
   NapoleonConfig config;
   int orgRec = WriteFaceRecord(face, innbuf);
   int emplRec = WriteEmplRecord(face, userID);
   int orderRec = WriteBillRecord(*db, orgRec, emplRec, order, db->GetRecNo(), config, 
                                  code, code1, UserName(ep, userID), userID);

   std::string whv;
   config.GetItem(&whv, W2A(WH_NUMBER));
   int whno = atoi(whv.c_str());
   WriteBillDetail(billa, orderRec, order, whno);

   WriteToLog(order, userID);
   return true;
}

DBRec* SyncOrder::BaseHeader(int *count) const
{
   *count = sizeof(billFields)/sizeof(billFields[0]);
   return billFields;
}

