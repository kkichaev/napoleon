/*
 * Copyright (C), 2006-2011, Денис Мосягин
 *
 * Регион Сибирь add-in
 *
 *  ert   17/03/2011   creating
 */ 
#ifndef __REG_SIBERIA_H
#define __REG_SIBERIA_H

#include <atlcrack.h>

#include <DocImpl.h>

#include <Module.h>
#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <NapoleonRes.h>
#include <BaseDialog.h>

// партии товара понедельник, среда, пятница - две недели от даты документа

struct GoodsRestItem : public IReflectableData
{
   wchar_t *id;
   WORD party; // партия - храним число дней от даты GoodsRest
   DWORD qty;  // QTY_SCALE

   DECLARE_TYPE_REFLECTION(GoodsRestItem)
};

struct GoodsRest : public IReflectableData
{
   wchar_t* id;
   FILETIME date;
   DWORD params;
   wchar_t* remark;

   vector_t<GoodsRestItem> items;

   DECLARE_TYPE_REFLECTION(GoodsRest)
};

struct GoodsItemInfo
{
   FILETIME date;
   WORD party;
   DWORD qty;
};

class GoodsRestImpl : public DBImpl<GoodsRest>, public IDocument, public ICreatableDocument
{
public:
   GoodsRestImpl() : DBImpl(L"GoodsRest") {}

   virtual const wchar_t*  KeyFields() const { return L"date"; }
   virtual const wchar_t** Indexes() const { static const wchar_t *index[] = { L"id", NULL }; return index; }

   virtual const wchar_t* ID() const { return id; }
   virtual const FILETIME& Date() const { return date; }
   virtual const wchar_t* Description() const;

   virtual DWORD Sum() const { return 0; }
   virtual const ROWID& RowID() const { return rid; }

   // methods for db read|write
   virtual IReflectableData* Data() { return this; }
   virtual const IDBData* DBData() const { return this; }
   virtual bool ReadDocument(const ROWID& rid) { return Read(rid); }

   // return null if is not creatable
   virtual ICreatableDocument* Creatable() { return (ICreatableDocument*)this; }

   virtual IDocument* Copy() { return NULL; }

   virtual bool CreateDocument(const ROWID &orgID);
   virtual bool Init(const ROWID &orgID);
   virtual bool CanRemove() const;
   virtual bool RemoveDocument() { return Remove(); } 
   virtual bool WriteDocument() { return Write(); }
   virtual void EditDocument(UINT retForm);

   virtual const ROWID& Serialize(StreamWriter* writer) const
   { 
      GetType().Serialize(writer, *this);
      return rid;
   }

   virtual bool IsDirty() const { return (params & ofExported) == 0; }
   virtual bool ClearDirty(SQLTable *updateTable, bool reverse);

   virtual const FILETIME& UID() const { return date; }

   bool EditItemData(const wchar_t* id);
   void GetItemInfo(std::vector<GoodsItemInfo> *res, const wchar_t* id);

   bool HaveItem(const wchar_t* id) const;
   DWORD GetQty(const wchar_t* id, WORD party) const;
   void UpdateItems(const std::vector<GoodsItemInfo> &res, const wchar_t* id);
};

class EditGoodsItem : public BaseDialog
{
public:
   EditGoodsItem(GoodsRestImpl *doc, const wchar_t* id);

   BEGIN_MSG_MAP(EditGoodsItem)
      MESSAGE_HANDLER(WM_SIZE, OnSizeChanged)
      MESSAGE_HANDLER(WM_INITDIALOG, OnInitDialog)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      COMMAND_RANGE_HANDLER(IDOK, IDCANCEL, Closing)
      CHAIN_MSG_MAP(BaseDialog)
   END_MSG_MAP()

   LRESULT OnInitDialog(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM /*lParam*/, BOOL& bHandled);
   LRESULT OnSizeChanged(UINT /*uMsg*/, WPARAM /*wParam*/, LPARAM lParam, BOOL& bHandled);
   LRESULT Closing(WORD wNotifyCode, WORD wID, HWND hWndCtl, BOOL& bHandled);
   LRESULT ItemSelected(LPNMHDR hdr);

protected:
   GoodsRestImpl *doc;
   const wchar_t* id;
   std::vector<GoodsItemInfo> data;
};

extern wchar_t dtGoodsRest[];

void OpenGoodsPrice(GoodsRestImpl* doc, bool retToDocList);
void OpenGoods(GoodsRestImpl* doc, bool retToDocList);

#endif
