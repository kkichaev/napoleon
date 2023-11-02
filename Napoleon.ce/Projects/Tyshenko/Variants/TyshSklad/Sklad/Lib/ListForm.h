/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Формы списка
 *
 *  ert   13/08/2007   creating
 */
#ifndef __LIST_FORM_H
#define __LIST_FORM_H

#include <atlcrack.h>

#include <BaseForm.h>
#include <Reflection.h>
#include <NapoleonRes.h>

#include "SumLabel.h"

class ListForm;
struct ListFormData;

struct FolderFormItem : public IReflectableData
{
   const wchar_t *name;
   DECLARE_TYPE_REFLECTION(FolderFormItem)
};

class ListViewMultiLine : public CWindowImpl<ListViewMultiLine, CListViewCtrl>
{
public:
   ListViewMultiLine();

   DECLARE_WND_CLASS(L"NPLLSTML")

   BEGIN_MSG_MAP(ListViewMultiLine)
      MESSAGE_HANDLER(OCM_DRAWITEM, DrawItem)
      MESSAGE_HANDLER(WM_NOTIFY, OnNotify)
      MESSAGE_HANDLER(WM_CHAR, OnChar)

      //MESSAGE_HANDLER(WM_MOUSEMOVE, OnMouseMove)
      //MESSAGE_HANDLER(WM_LBUTTONDOWN, OnMousePress)
      //MESSAGE_HANDLER(WM_LBUTTONUP, OnMousePress)
   END_MSG_MAP()

   LRESULT DrawItem(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   void Set(ListForm *_owner) { owner = _owner; }

   int GetLastVisibleItem() const;

   void SetLayout(bool forceRecalc, const CRect& bounds, ListFormData *listData);
   void Setup(int scale, ListFormData *listData, int imageList);
   void LoadImageList(int index);

protected:
   //LRESULT OnMouseMove(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   //LRESULT OnMousePress(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);

   //LRESULT EndTrack(LPNMHDR hdr);
   LRESULT OnNotify(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT OnChar(UINT /*uMsg*/, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

   int  ItemNotify(DRAWITEMSTRUCT *ds, NMLVCUSTOMDRAW *cd, const CRect &textBounds, DWORD stage, int subItem);
   void DrawBack(DRAWITEMSTRUCT *ds, NMLVCUSTOMDRAW *cd);
   void DrawImage(DRAWITEMSTRUCT *ds, RECT *textBounds);

   ListForm *owner;
   bool endTrack;

   CImageList images;
};

struct ListFormData : public IFormData, public IDataCollection
{
   struct Header
   {
      enum Justify { Left, Right, Center };

      Justify  justify; 
      const UINT title; // заголовок
      const wchar_t *wTitle; // заголовок
      const wchar_t *field; // название поля
      WORD  startWidth;     // начальная ширина колонки
      WORD  curWidth;       // текущая ширина колонки
   };

   ListFormData();
   ~ListFormData();

   ListForm *owner;

   void SetWidth(int column, WORD width)
   {
      ((Header*)GetHeader())[column].curWidth = width;
   }

   virtual const Header *GetHeader() const = 0;
   virtual int ColumnsCount() const = 0;

   // IDataCollection memebers
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }
   virtual IReflectableData* GetItem(int index) { return NULL; }
   virtual void Clear() {};


   // ret true - refresh form, 
   // может быть не стандартное поведение
   // PriceFormData.Selecting()
   virtual bool Adding() { return false; }
   virtual bool Selecting(int index) { return false; }
   virtual bool Removing(int index) { return false; }
   virtual bool Editing(int index) { return false; }
};

class OrgFuncs;
class PictButton;
class ListForm : public BaseForm
{
public:
   ListForm();
   ~ListForm();

   BEGIN_MSG_MAP(ListForm)
      MESSAGE_HANDLER(WM_HOTKEY, OnHotKey)
      NOTIFY_CODE_HANDLER_EX(LVN_GETDISPINFO, SetCellInfo)
      NOTIFY_CODE_HANDLER_EX(LVN_COLUMNCLICK, Sorting)
      NOTIFY_CODE_HANDLER_EX(LVN_KEYDOWN, OnKeyDown)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      NOTIFY_CODE_HANDLER_EX(HDN_ITEMCHANGED, SetHeaderWidth)
      COMMAND_ID_HANDLER(IDC_ADD, HandleCommands)
      COMMAND_ID_HANDLER(IDC_DEL, HandleCommands)
      COMMAND_ID_HANDLER(IDC_EDIT, HandleCommands)
      MSG_WM_CONTEXTMENU(ShowContextMenu)
      CHAIN_MSG_MAP(BaseForm)
      REFLECT_NOTIFICATIONS()
   END_MSG_MAP()

   virtual DWORD GetResourceID() const { return IDR_LIST_FORM; }

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 1); }
   virtual void Refresh();
   virtual void Destroy();

   virtual void LoadMenuBar(bool hideSIP = false);

   int GetLastVisibleItem() const;
   int GetTopIndex() const { return listCtrl.GetTopIndex(); }

   int GetSelected() const { return listCtrl.GetSelectedIndex(); }
   int GetColumnWidth(int column) const { return listCtrl.GetColumnWidth(column); }

   virtual bool CanSetColumn(int rowIndex, int colIndex) const { return true; }

	virtual bool OnChar(ListViewMultiLine* ctrl, UINT charSym) { return false; }

protected:
   ListForm(int sumScale);

   virtual LRESULT SetCellInfo(LPNMHDR hdr);
   LRESULT OnKeyDown(LPNMHDR hdr);
   LRESULT Sorting(LPNMHDR hdr);
   LRESULT ItemSelected(LPNMHDR hdr);
   LRESULT ShowContextMenu(HWND hWnd, const CPoint &org);

   LRESULT HandleCommands(WORD nCode, WORD id, HWND hWnd, BOOL &bHanddled);

   LRESULT SetHeaderWidth(LPNMHDR hdr);

   LRESULT OnHotKey(UINT uMsg, WPARAM wParam, LPARAM lParam, BOOL& bHandled);

protected:
   bool SetDataEx(IFormData *_data, int scale);
   void SetupListCtrl(ListViewMultiLine *list, int scale, ListFormData *listData);
   void SetListLayout(bool forceRecalc, int listTop = 0, int height = 0, 
      ListViewMultiLine *list = NULL, ListFormData *listData = NULL); // верх листа - listTop
   virtual void UpdateLayout(const RECT& bounds, bool forceRecalc);

   virtual int ImageListID(ListViewMultiLine *list) const { return -1; }

   int Compare(int item1, int item2);
   static int CALLBACK CompareItems(LPARAM lParam1, LPARAM lParam2, LPARAM lParamSort);

   friend class OrgFuncs;

public:
   ListViewMultiLine listCtrl;

protected:
   //int sortedColumn;
   //bool reverse;

   CMenuBarCtrl menuBar;
   SumLabel sumLabel;
   
   IReflectableData *element;
   ListFormData *data;
   int curIndex;

   UINT hotKeysMaps[3];
   const UINT *cmdButtons;
   std::vector<PictButton*> buttons;
};

#endif
