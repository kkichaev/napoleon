/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Окно с папками
 *
 *  ert   29/10/2007   creating
 */
#ifndef __FOLDER_FORM_H
#define __FOLDER_FORM_H

#include <TreeNode.h>
#include <ListForm.h>

struct SQLFolderFormData : public ListFormData
{
   enum FLAGS { flRefresh = 1 };

   SQLFolderFormData();

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;
   virtual const DataReflector& DataType() const { return FolderFormItem().GetType(); }

   virtual int Count() const { return folders.size() + leafs.size(); }

   virtual bool Selecting(int index);
   virtual bool Add(const IReflectableData& data, int index) { return false; }
   virtual bool Remove(int index) { return false; }
   virtual bool Update(const IReflectableData& data, int index) { return false; }

   virtual DWORD Sum() const = 0;

   virtual const wchar_t* GetTitle() const { return title.c_str(); }

   bool IsFolder(int index) const { return index < (int)folders.size(); }
   virtual bool IsTopLevel() const { return (current == &root); }
   virtual bool HaveLeaf() const { return leafs.size() != 0; }

   int Up(); // return selected index
   bool NextWithLeafs(bool next);
   void Refresh();

   const ROWID& UpFolder() const { return current->id; }

   virtual void InitData() { SelectFolder(NO_ROWID); }

   bool RefreshAfterSelect() const { return ((flags & flRefresh) != 0); }

protected:
   virtual void LoadTree() = 0;

   virtual bool SelectLeaf(int index) = 0;

   // load leafs & title
   virtual void LoadFolderData(const TreeNode& folder) = 0;

   void SetCurrent(TreeNode *node, bool expand)
   {
      expandLevel = (expand) ? current->Level() : -1;
      SetCurrent(node);
   }

   void SetCurrent(TreeNode *node);
   void LoadWithChilds(const TreeNode &node);
   void SelectFolder(const ROWID& folder, bool forceReload = false);

   int expandLevel;

protected:
   TreeNode root, *current;

   std::vector<ROWID> folders, leafs;
   std::wstring title;

   WORD flags;
};

class SQLFolderForm : public ListForm
{
public:
   SQLFolderForm();

   BEGIN_MSG_MAP(SQLFolderForm)
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
