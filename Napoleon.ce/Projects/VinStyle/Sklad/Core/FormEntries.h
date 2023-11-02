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
void OpenInvent();
void OpenChkRack();
void OpenRouteList(const wchar_t* id);
void OpenMovmentDoc(const wchar_t* doc);
void OpenDKA1Doc(const wchar_t* doc);
void OpenDKA2Doc(const wchar_t* doc);
void OpenScanDoc(const wchar_t* doc);

void DecodeNumber(std::wstring* res, const wchar_t* src, bool isDoc);
void MakeMarkCode(std::wstring* val, const wchar_t*barcode);

void StartScan(HWND hWnd);
void StopScan();
bool GetScanData(std::wstring* data, LPARAM lParam);

bool SetQTY(QTYData *data); // data не удаляется

void SetScalingValue(CWindow ctrl, int value, DWORD scale, bool hideRest);
DWORD GetValue(CWindow ctrl, DWORD scale);

#include "BaseDialog.h"
class BCDialog : public BaseDialog
{
public:
   BCDialog() : BaseDialog(IDD_INPUT_BC, DEFAULT_FLAGS)
   {
      flags &= (~ShowSIP);
   }

   BEGIN_MSG_MAP(BCDialog)
      //MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseDialog)
   END_MSG_MAP()

   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled)
   {
      if( wID == IDOK )
         GetString(&text, GetDlgItem(IDC_FIND));

      bHandled = FALSE;
      return 0;
   }

   const wchar_t* GetText() const { return text.c_str(); }
private:
   std::wstring text;
};
#include "ListForm.h"
struct ChooseItem : public IReflectableData
{
   wchar_t *name;

   DECLARE_TYPE_REFLECTION(ChooseItem);
};

struct ChoosItemData : public ListFormData
{
	ChoosItemData() { items = NULL; }

	virtual int Count() const { return (items) ? items->size() : 0; }
   virtual bool Get(IReflectableData* data, int index) const;
	virtual bool Selecting(int index) { return false; }

	virtual const Header *GetHeader() const;
	virtual int ColumnsCount() const;

	virtual const DataReflector& DataType() const { return ChooseItem().GetType(); }

	const std::vector<ROWID>* items;
	mutable PriceImpl price;
};

#endif
