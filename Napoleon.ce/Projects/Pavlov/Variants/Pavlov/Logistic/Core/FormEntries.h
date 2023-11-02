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

void OpenMainForm();
void OpenPartnerList();
void OpenCost(const wchar_t *orgID);
void OpenDoc(WhDocImpl*, bool forSend = false);
void OpenDocList();

bool CheckPassword(HWND hWnd, const AgentsImpl& a);

void StartScan(HWND hWnd, UINT msg);
bool GetScan(std::wstring* data);
void StopScan();

void SetScalingValue(CWindow ctrl, int value, DWORD scale, bool hideRest);
DWORD GetValue(CWindow ctrl, DWORD scale);

#endif
