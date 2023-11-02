/*
 */
#include "stdafx.h"
#include "Program.h"

#include <dbf.h>
#define _CONVERSION_DONT_USE_THREAD_LOCALE
#include <atlconv.h>

DBRec frec[] =
{
   {"ID",'N',9,0},
   {"LEVEL",'N',2,0},
   {"NAME",'C',100,0}
};

DBRec prec[] =
{
   {"ID",'C',20,0},
   {"NAME",'C',100,0},
   {"FOLDER",'N',9,0},
   {"COST1",'N',9,2},
   {"QTY",'N',9,3},
   {"INPACK",'N',9,3},
};

const int START_ROW = 6;

int ctrLine = 0;

#define XML_STATIC
#include "expat\expat.h"

struct RowData
{
   std::string firm;
   std::string folder;
   std::string id;
   std::string name;

   std::string unit;

   double inPack;
   double cost;

   void Clear() { folder.clear(), id.clear(); name.clear(); unit.clear(); inPack = 1; cost = 0; }
   bool IsComplete() const { return (!firm.empty() && !folder.empty() && !id.empty() && !name.empty() && !unit.empty() && cost > 0); }
};

struct Handler
{
   enum State { None, ID, Firm, Group, Name, Unit, InPack, Cost };

   Handler(DataForm& _folder, DataForm& _price, const std::vector<std::wstring>& _sharedStrings) : 
      folder(_folder), price(_price), fi(0), state(None), sharedStrings(_sharedStrings), isSS(false) {}

   void ReadString(std::string* val, const XML_Char *s, int len)
   {
      std::wstring src;
      Utf8ToUtf16(&src, s, len);

      const wchar_t *sp = src.c_str();
      if( isSS )
      {
         int idx = _wtoi(sp);
         sp = sharedStrings[idx].c_str();
      }

      USES_CONVERSION;
      val->assign(W2A_CP(sp, CP_OEMCP));
   }

   double Read(const XML_Char *s, int len)
   {
      std::string str(s, len);
      return atof(str.c_str());
   }

   void ParseI(const XML_Char *name, const XML_Char **atts)
   {
      std::wstring val;
      Attributes a(atts);
      if( strcmp(name, "row") == 0 )
      {
         a.Find(&val, L"r");
         readRow = (_wtoi(val.c_str()) >= START_ROW);
      } else if( strcmp(name, "c") == 0 && readRow )
      {
         isSS = (a.Find(&val, L"t") && val.compare(L"s") == 0);
         a.Find(&val, L"r");
         wchar_t sym = *val.begin();
         state = (sym == L'B') ? ID : 
            (sym == L'C') ? Firm : 
            (sym == L'D') ? Group : 
            (sym == L'E') ? Name : 
            (sym == L'F') ? Unit : 
            (sym == L'G') ? InPack : 
            (sym == L'H') ? Cost : 
            None;
      }
   }

   void EndI(const XML_Char *name)
   {
      if( strcmp(name, "row") == 0 )
      {
         if( row.IsComplete() )
         {
            if( (ctrLine % 200) == 0 )
               printf("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\bВыгружается прайс %d", ctrLine);

            if( firmBuf.compare(row.firm.c_str()) )
            {
               fi++;
               folder.ResetRec();

               folder.Fill("ID", fi);
               folder.Fill("LEVEL", 1.0);
               folder.Fill("NAME", row.firm.c_str());
               folder.Append();

               firmBuf = row.firm;
            }

            if( folderBuf.compare(row.folder.c_str()) )
            {
               fi++;
               folder.ResetRec();
               folder.Fill("ID", fi);
               folder.Fill("LEVEL", 2.0);
               folder.Fill("NAME", row.folder.c_str());
               folder.Append();

               folderBuf = row.folder;
            }

            price.ResetRec();
            price.Fill("FOLDER", fi);
            price.Fill("ID", row.id.c_str());
            price.Fill("NAME", row.name.c_str());
            price.Fill("INPACK", row.inPack);
            price.Fill("QTY", 15000.0);
            price.Fill("COST1", row.cost);
            price.Append();

            ctrLine++;
         }

         row.Clear();
         state = None;
      }
   }

   void ReadValue(const XML_Char *s, int len)
   {
      switch( state )
      {
      case ID:
         ReadString(&row.id, s, len);
         break;
      case Group:
         ReadString(&row.folder, s, len);
         break;
      case Firm:
         ReadString(&row.firm, s, len);
         break;
      case Name:
         ReadString(&row.name, s, len);
         break;
      case Unit:
         ReadString(&row.unit, s, len);
         break;
      case InPack:
         row.inPack = Read(s, len);
         break;
      case Cost:
         row.cost = Read(s, len);
         break;
      }
   }

   static void Parse(Handler *content, const XML_Char *name, const XML_Char **atts) { content->ParseI(name, atts); }
   static void End(Handler *content, const XML_Char *name) { content->EndI(name); }
   static void Content(Handler *content, const XML_Char *s, int len) { content->ReadValue(s, len); }

   DataForm& folder;
   DataForm& price;
   const std::vector<std::wstring>& sharedStrings;
   std::string folderBuf, firmBuf;

   RowData row;
   double fi;
   State state;
   bool isSS, readRow;
};

bool Do(const Binary& priceSheet, const wchar_t* baseFolderW, const std::vector<std::wstring>& sharedStrings)
{
   USES_CONVERSION;
   const char *baseFolder = W2A(baseFolderW);

   std::string fileName;

   DataForm fld;
   DataForm price;

   fileName = baseFolder;
   if( *fileName.rbegin() != '//' ) fileName += "//";
   fileName += "folders.dbf";
   _unlink(fileName.c_str());

   fld.Create(fileName.c_str(), sizeof(frec) / sizeof(frec[0]), frec);

   fileName = baseFolder;
   if( *fileName.rbegin() != '//' ) fileName += "//";
   fileName += "warehous.dbf";
   _unlink(fileName.c_str());

   price.Create(fileName.c_str(), sizeof(prec) / sizeof(prec[0]), prec);

   Handler handler(fld, price, sharedStrings);

   XML_Parser parser = XML_ParserCreate(NULL);
   XML_SetElementHandler(parser, (XML_StartElementHandler)Handler::Parse, (XML_EndElementHandler)Handler::End);
   XML_SetCharacterDataHandler(parser, (XML_CharacterDataHandler)Handler::Content);
   XML_SetUserData(parser, &handler);

   bool retVal = (XML_Parse(parser, (const char*)priceSheet.data, priceSheet.length, 1) != XML_STATUS_ERROR);

   XML_ParserFree(parser);
   return retVal;
}
