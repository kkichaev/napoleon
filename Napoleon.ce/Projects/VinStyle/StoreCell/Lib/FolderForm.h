/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Окно с папками
 *
 *  ert   29/10/2007   creating
 */
#ifndef __FOLDER_FORM_H
#define __FOLDER_FORM_H

#include <ListForm.h>
#include <SyncFormat.h>
#include <Table.h>

struct FolderFormData : public ListFormData
{
   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual const DataReflector& DataType() const { return FolderFormItem().GetType(); }

   virtual int Count() const { return folders.size() + leafs.size(); }

   // upFolder == 0 начинать с начала
   virtual void LoadFolder(CEOID upFolder) = 0;
   virtual bool SelectLeaf(int index) = 0;
   virtual const wchar_t* GetTitle() const { return title.c_str(); }
   virtual DWORD Sum() const = 0;
   virtual bool MoveToFolder(bool next) = 0;

   virtual bool Selecting(int index);

   bool HaveLeaf() const { return leafs.size() != 0; }
   CEOID Backing();
   
   bool IsFolder(int index) const { return index < (int)folders.size(); }
   bool IsTopLevel() const { return (upFolders.size() <= 1); }

   // номер папки по порядку или -1
   int  IndexOf(CEOID) const;

   // ---- 
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

protected:
   void MakeUpFolders(CEOID cur, CETable *folderTable);
   void LoadFolders(const SyncFormat &syncFolder, CEOID upFolder);
   bool HaveLeafItems(CETable *folderTable, IReflectableData *item, CEOID *oid);

   virtual void LoadLeafs(DWORD index, WORD size) {}
   virtual void LoadFolders(CETable &folderTable, IReflectableData *fItem, CEOID oid,
                            WORD curLevel, WORD *priceSize, DWORD *priceItem);

protected:
   std::vector<CEOID> folders, leafs, upFolders;
   std::wstring title;
};

class FolderForm : public ListForm
{
public:
   FolderForm();

   BEGIN_MSG_MAP(FolderForm)
      NOTIFY_CODE_HANDLER_EX(LVN_COLUMNCLICK, MoveUp)
      NOTIFY_CODE_HANDLER_EX(NM_CLICK, ItemSelected)
      NOTIFY_CODE_HANDLER_EX(LVN_KEYDOWN, OnKeyDown)
      COMMAND_ID_HANDLER(IDC_BACK, Backing)
      COMMAND_ID_HANDLER(IDC_NEXT, OnChangeFolder)
      COMMAND_ID_HANDLER(IDC_PREV, OnChangeFolder)
      CHAIN_MSG_MAP(ListForm)
   END_MSG_MAP()

   virtual bool SetData(IFormData *_data) { return SetDataEx(_data, 1); }
   virtual LRESULT SetCellInfo(LPNMHDR hdr);

   virtual int SumLabelOffset() const { return SumLabel::STD_OFFSET; }

   void SetTitle(const wchar_t *title);
   void ChangeFolder(bool next);

   LRESULT ItemSelected(LPNMHDR hdr);
   LRESULT Backing(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled);
   LRESULT OnKeyDown(LPNMHDR hdr);
   LRESULT OnChangeFolder(WORD nCode, WORD id, HWND hWnd, BOOL &bHandled)
   {
      ChangeFolder((id == IDC_NEXT));
      return 0;
   }

protected:
   LRESULT MoveUp(LPNMHDR hdr);
   virtual void Refresh();
   virtual int ImageListID(ListViewMultiLine *list) const;

   bool SetDataEx(IFormData *_data, int scale);

   virtual int ImageIndex(int index) const;

protected:
   BOOL enableUp;
};

#endif
