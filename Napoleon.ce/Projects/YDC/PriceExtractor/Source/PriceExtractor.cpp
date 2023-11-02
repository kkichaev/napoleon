// PriceExtractor.cpp: определяет точку входа для консольного приложения.
//

#include "stdafx.h"
#include "Program.h"


const wchar_t SHEET_NAME[] = L"исход";
const wchar_t CONTENT_NAME[] = L"[Content_Types].xml";

#define XML_STATIC
#include "expat\expat.h"
#include "unzip.h"
Binary* Unzip(HZIP hz, const wchar_t* itemName)
{
   int index;
   ZIPENTRY ze;
   if( *itemName == L'/' )
      itemName++;
   if( FindZipItem(hz, itemName, true, &index, &ze) != ZR_OK )
      return NULL;

   Binary *ret = new Binary();
   ret->length = ze.unc_size;
   ret->data = malloc(ze.unc_size);

   if( UnzipItem(hz, index, ret->data, ret->length) != ZR_OK )
   {
      delete ret;
      return NULL;
   }

   return ret;
}

void Utf8ToUtf16(std::wstring *dest, const char *str, int len)
{
   if( len <= 0 ) len = strlen(str);
   len++;

   wchar_t *tempW = (wchar_t*)alloca(len * sizeof(wchar_t));

   *tempW = L'\0';
   int wlen = MultiByteToWideChar(CP_UTF8, 0, str, len-1, tempW, len);
   dest->assign(tempW, wlen);
}


struct ParseContent
{
   ParseContent(std::wstring* sharedString, std::wstring* workbook)
   {
      this->sharedString = sharedString;
      this->workbook = workbook;

      sharedString->clear();
      workbook->clear();
   }

   std::wstring* sharedString;
   std::wstring* workbook;

   static void Parse(ParseContent *content, const XML_Char *name, const XML_Char **atts)
   {
      Attributes a(atts);
      std::wstring val;
      if( a.Find(&val, L"ContentType") )
      {
         
         if( val.compare(L"application/vnd.ms-excel.sheet.macroEnabled.main+xml") == 0 )
            a.Find(content->workbook, L"PartName");
         else if( val.compare(L"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml") == 0 )
            a.Find(content->sharedString, L"PartName");
         else if( val.compare(L"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml") == 0 )
            a.Find(content->workbook, L"PartName");
      }
   }
};

bool GetContent(std::wstring* sharedString, std::wstring* workbook, const Binary& content)
{
   ParseContent handler(sharedString, workbook);

   XML_Parser parser = XML_ParserCreate(NULL);
   XML_SetElementHandler(parser, (XML_StartElementHandler)ParseContent::Parse, NULL);
   XML_SetUserData(parser, &handler);

   bool retVal = (XML_Parse(parser, (const char*)content.data, content.length, 1) != XML_STATUS_ERROR);

   XML_ParserFree(parser);
   return retVal && (!sharedString->empty() && !workbook->empty());
}

struct SSHandler
{
   SSHandler(std::vector<std::wstring>* sharedStrings)
   {
      this->sharedStrings = sharedStrings;
      starting = false;
   }

   static void Parse(SSHandler *content, const XML_Char *name, const XML_Char **atts)
   {
      content->starting = (tolower(*name) == 't');
   }

   static void Content(SSHandler *content, const XML_Char *s, int len)
   {
      if( content->starting )
      {
         std::wstring val;
         Utf8ToUtf16(&val, s, len);

         content->sharedStrings->push_back(val);
         content->starting = false;
      }
   }

   bool starting;
   std::vector<std::wstring>* sharedStrings;
};

bool LoadSharedStrings(std::vector<std::wstring>* sharedStrings, const std::wstring& itemName, HZIP hz)
{
   Binary *content = Unzip(hz, itemName.c_str());
   if( content == NULL )
      return false;

   SSHandler handler(sharedStrings);

   XML_Parser parser = XML_ParserCreate(NULL);
   XML_SetElementHandler(parser, (XML_StartElementHandler)SSHandler::Parse, NULL);
   XML_SetCharacterDataHandler(parser, (XML_CharacterDataHandler)SSHandler::Content);
   XML_SetUserData(parser, &handler);

   bool retVal = (XML_Parse(parser, (const char*)content->data, content->length, 1) != XML_STATUS_ERROR);

   delete content;
   XML_ParserFree(parser);
   return retVal && (sharedStrings->size() > 0);
}

struct SHId
{
   SHId(std::wstring* id, const wchar_t* sheetName)
   {
      this->id = id;
      id->clear();

      this->sheetName = sheetName;
   }

   static void Parse(SHId *content, const XML_Char *name, const XML_Char **atts)
   {
      if( strcmp(name, "sheet") == 0 )
      {
         std::wstring val;
         Attributes a(atts);
         if( a.Find(&val, L"name") )
         {
            if( val.compare(content->sheetName) == 0 )
            {
               Attributes::const_iterator i = a.begin();
               for( ; i != a.end(); i++ )
               {
                  if( (int)i->first.find_last_of(L":id") > 0 )
                  {
                     content->id->assign(i->second);
                     break;
                  }
               }
            }
         }
      }
   }

   std::wstring* id;
   const wchar_t* sheetName;
};

bool GetSheetId(std::wstring* id, const std::wstring& wb, const wchar_t* sheetName, HZIP hz)
{
   Binary *content = Unzip(hz, wb.c_str());
   if( content == NULL )
      return false;

   SHId handler(id, sheetName);

   XML_Parser parser = XML_ParserCreate(NULL);
   XML_SetElementHandler(parser, (XML_StartElementHandler)SHId::Parse, NULL);
   XML_SetUserData(parser, &handler);

   bool retVal = (XML_Parse(parser, (const char*)content->data, content->length, 1) != XML_STATUS_ERROR);

   delete content;
   XML_ParserFree(parser);
   return retVal && !id->empty();
}

struct SHName
{
   SHName(std::wstring* name, const std::wstring& id)
   {
      this->name = name;
      this->id = id;
      name->clear();
   }

   static void Parse(SHName *content, const XML_Char *name, const XML_Char **atts)
   {
      if( _stricmp(name, "Relationship") == 0 )
      {
         std::wstring val;
         Attributes a(atts);
         if( a.Find(&val, L"Id") )
         {
            if( val.compare(content->id) == 0 )
               a.Find(content->name, L"Target");
         }
      }
   }

   std::wstring* name;
   std::wstring id;
};

bool GetIdName(std::wstring* name, const std::wstring& id, const std::wstring& wb, HZIP hz)
{
   int pos = wb.find_last_of(L"/");
   std::wstring base, item;
   base = wb.substr(0, pos) + L"/";
   item = base + L"_rels" + wb.substr(pos) + L".rels";

   Binary *content = Unzip(hz, item.c_str());
   if( content == NULL )
      return false;

   SHName handler(name, id);

   XML_Parser parser = XML_ParserCreate(NULL);
   XML_SetElementHandler(parser, (XML_StartElementHandler)SHName::Parse, NULL);
   XML_SetUserData(parser, &handler);

   bool retVal = (XML_Parse(parser, (const char*)content->data, content->length, 1) != XML_STATUS_ERROR);

   delete content;
   XML_ParserFree(parser);
   if( !name->empty() )
      name->insert(0, base);
   return retVal && !name->empty();
}

bool ReadSheet(Binary** priceSheet, const std::wstring& wb, const wchar_t* sheetName, HZIP hz)
{
   std::wstring id, name;
   bool ret = false;
   if( GetSheetId(&id, wb, sheetName, hz) && GetIdName(&name, id, wb, hz) )
   {
      *priceSheet = Unzip(hz, name.c_str());
      ret = (*priceSheet != NULL);
   }

   return ret;
}

bool LoadPrice(std::vector<std::wstring>* sharedStrings, Binary** priceSheet, const _TCHAR* fileName, const _TCHAR* sheetName)
{
   DWORD err;
   HZIP hz = OpenZip(fileName, NULL);
   if( hz == NULL )
   {
      err = GetLastError();
      return false;
   }

   bool ret = false;
   std::wstring ss, wb;
   Binary *content = Unzip(hz, CONTENT_NAME);
   ret = GetContent(&ss, &wb, *content);
   delete content;

   if( ret )
   {
      //xl\\_rels\\workbook.xml.rels
      ret = LoadSharedStrings(sharedStrings, ss, hz) && ReadSheet(priceSheet, wb, sheetName, hz);
   }
   CloseZip(hz);
   return ret;
}

int _tmain(int argc, _TCHAR* argv[])
{
   if( argc != 3 )
   {
      wprintf(L"%s priceFile exchangeFolder", argv[0]);
      return 1;
   }

   std::vector<std::wstring> sharedStrings;
   Binary *sheet = NULL;
   if( !LoadPrice(&sharedStrings, &sheet, argv[1], SHEET_NAME) )
   {
      wprintf(L"error load price");      
      return 1;
   }

   Do(*sheet, argv[2], sharedStrings);
   delete sheet;
	return 0;
}
