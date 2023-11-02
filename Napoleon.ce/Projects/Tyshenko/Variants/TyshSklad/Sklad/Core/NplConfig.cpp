/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Чтение/Запись конфигурации в реестр
 *
 *  ert   09/09/2007   creating
 */
#include "stdafx.h"
#include "NplConfig.h"
#include "ObjImpl.h"

#ifndef SEP_SYM
#define SEP_SYM ';'
#endif

//NapoleonConfig::NapoleonConfig()
//{
//}
//
//NapoleonConfig::~NapoleonConfig()
//{
//}
//
//bool NapoleonConfig::Remove(const wchar_t *key)
//{
//   ConfigImpl ci;
//   ci.key = (wchar_t*)key;
//   if( ci.Read() && ci.Remove() ) return true;
//   return false;
//}
//
//bool NapoleonConfig::ReadValue(std::wstring *value, const wchar_t *key)
//{
//   ConfigImpl ci;
//   ci.key = (wchar_t*)key;
//   if( !ci.Read() ) return false;
//
//   value->assign(ci.value);
//   return true;
//}
//
//bool NapoleonConfig::WriteValue(const wchar_t *value, const wchar_t *key)
//{
//   ConfigImpl ci;
//   ci.key = (wchar_t*)key;
//   ci.value = (wchar_t*)value;
//
//   return ci.Write();
//}
//
//bool NapoleonConfig::GetStringItem(std::wstring *value, const wchar_t *key, int item)
//{
//   std::wstring regVal;
//   if( ReadValue(&regVal, key) == false ) return false;
//
//   *value = L"";
//   std::wstring::size_type off = 0, nextOff;
//   while( true )
//   {
//      nextOff = regVal.find(SEP_SYM, off);
//      if( item-- == 0 )
//      {
//         *value = regVal.substr(off, (nextOff != std::wstring::npos) ? 
//            nextOff - off : std::wstring::npos);
//         return true;
//      }
//
//      if( nextOff == std::wstring::npos )
//         return false;
//      off = nextOff + 1;
//   }
//
//   return false;
//}
