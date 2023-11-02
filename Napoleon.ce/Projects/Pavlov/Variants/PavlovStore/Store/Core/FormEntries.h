/*
 * Copyright (C), 2007 - 2010, Денис Мосягин
 *
 * Точки входа для форм. Функции сами освобождают параметр который передается
 *
 *  ert   03/09/2010   creating
 */
#ifndef __FORMS_ENTRIES_H
#define __FORMS_ENTRIES_H

#include "ObjImpl.h"

const UINT WM_SCAN_DATA = WM_USER + 0x1;

class OrderImpl;

struct QTYData
{
   QTYData();

   std::wstring id;

   DWORD qty; // QTY_SCALE
   WORD  flags; // oiInPack etc
   DWORD cost; // if cost == 0 -> cost == sum / qty
   DWORD sum;

   bool canChange;
};


void OpenMainForm();
void OpenSyncForm();
void OpenOrderList();
void OpenCtrlDocList();

void SetFKey(int vk, BOOL set);

bool SetQTY(QTYData *data); // data не удаляется

void SetScalingValue(CWindow ctrl, int value, DWORD scale, bool hideRest);
DWORD GetValue(CWindow ctrl, DWORD scale);
void RTrim(std::wstring *str);

void StartScan(HWND hWnd);
void StopScan();
bool GetScanData(std::wstring* data, LPARAM lParam);


#endif
