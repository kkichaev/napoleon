/*
* Copyright (C), 2007, Денис Мосягин
*
* Стандартные функции системы
*
*  ert   04/12/2007   creating
*/

#ifndef __STD_FUNCS_H
#define __STD_FUNCS_H

#include <StdConsts.h>

#include <vector>
#include <string>

enum PlatformVersion
{
   vSP2002,
   vSP2003,
   vSP5,
   vSP6,
   vPPC2000,
   vPPC2002,
   vPPC2003,
   vPPC2003SE,
   vPPC5,
   vPPC6,
   vUnknown
};

PlatformVersion GetPlatformVersion();

DWORD GetDeviceID();

//
// кол-во свободной памяти
//
inline DWORD GetAvailMem()
{
   MEMORYSTATUS ms;
   GlobalMemoryStatus(&ms);
   return ms.dwAvailPhys;
}

//
// высота текста окна
//
void CalcTextHeight(HWND wnd, RECT *bounds);

//
// обнуляет время в SYSTEMTIME
//
void ResetTime(SYSTEMTIME *st);

//
// Включить на полную мощность
//
void PowerUp();

//
// 
//
HANDLE CreatePowerEvent();
void HandlePowerEvent();
void FreePowerEvent();


//
// Установить шрифт из системы
//
void SetSystemFont(HWND hWnd, BOOL redraw=FALSE);

//
// Удалить созданный шрифт
//
void FreeSystemFont();

//
// Изменить ориентацию экрана
//
bool ChangeOrientation(bool portrait);


//
// Позвонить по телефону
//
bool MakeCall(const wchar_t *phone);

//
// Послать СМС
//
bool SendSMS(const wchar_t *phone, const wchar_t *text);

//
// 
//
DWORD GetValue(const wchar_t *buf, DWORD scale);

//
//
//
bool GetVersionStr(std::wstring* ver, HINSTANCE hInst);

//
//
//
bool IsFileExist(const std::wstring& fileName);

//
// Рисует битмап на DC с масштабированием (если надо) в отведенном квадрате
//
void PaintScale(HDC hdc, HBITMAP hBmp, int width, int height);

//
// open close phone
//
typedef void (*NotifyCallback)();

bool OpenPhoneLine(NotifyCallback callback = NULL);
bool ClosePhoneLine(NotifyCallback callback = NULL);
bool IsPhoneOn();

//
// возвращает текст окна
//
bool GetString(std::wstring* dest, HWND hWnd);

//
// ANSI - UTF16
//
bool Convert(char* dest, const wchar_t* src, WORD len = -1);
bool Convert(wchar_t* dest, const char* src, WORD len = -1);

//
//
//
void StringToList(std::vector<std::wstring> *fields, const wchar_t *fieldsStr, wchar_t delimiter=L',', bool eatSpace = true);

//
// проверка квадратного экрана
//
inline bool IsSquareScreen() { return GetSystemMetrics(SM_CXSCREEN) == GetSystemMetrics(SM_CYSCREEN); }

//
// Функции для расчета без переполнения
//
inline DWORD ItemSum(DWORD cost, DWORD qty)
{
   return (DWORD)((__int64)cost * qty / QTY_SCALE);
}

inline DWORD ItemWeight(DWORD weight, DWORD qty)
{
   return (DWORD)(((__int64)weight * qty)/WEIGHT_SCALE);
}

inline DWORD ItemCost(DWORD sum, DWORD qty)
{
   if( qty == 0 ) return 0;
   return (DWORD)((__int64)sum * QTY_SCALE) / qty;
}

const DWORD MAX_VALUE = 2000000000l;

inline DWORD MulInPack(DWORD val, DWORD inPack, DWORD scale)
{
   if( inPack == 0 || scale == 0 ) return 0;
   DWORD sign = 1;
   if( (int)val < 0 )
   {
      sign = -1;
      val = (DWORD)-(int)val;
   }

   return sign * (DWORD)((__int64)val * inPack / scale);
}

inline DWORD DivideInPack(DWORD val, DWORD inPack, DWORD scale)
{
   if( inPack == 0 || scale == 0 ) return 0;
   DWORD sign = 1;
   if( (int)val < 0 )
   {
      sign = -1;
      val = (DWORD)-(int)val;
   }
   return sign * (DWORD)(((__int64)val * scale) / inPack);
}

const __int64 TWO_DAYS_SECONDS = (__int64)48 * 3600 * 10000000;
inline bool IsStartDate(const FILETIME& date)
{
   // проверка на начальную дату
   return ((*(__int64*)&date - 116444736000000000) < TWO_DAYS_SECONDS );
}

#ifdef DEBUG
void WriteToLog(wchar_t* msg, ...);
#else
inline void WriteToLog(wchar_t* msg, ...) {}
#endif


enum SoftKeys
{
   SWK1,
   SWK2,
   SWK3,
   PK1,
   PK2,
};

void RegisterHotKeys(HWND hWnd, bool dialogHK);
void UnregisterHotKeys(HWND hWnd, bool dialogHK);

#endif
