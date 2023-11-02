/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Контакты
 *
 *  ert   19/11/2010   creating
 */
#ifndef __CONTACTS_CTRL_H
#define __CONTACTS_CTRL_H

struct ContactData : public ListFormData
{
   ContactData() {}

#ifdef ORG_INFO
   void SetContacts(const std::vector<Contact> &contacts);

   std::vector<Contact> contacts;
   StringHolder sh;

   virtual int Count() const { return contacts.size(); }
   virtual bool Get(IReflectableData* data, int index) const;
   virtual bool Selecting(int index);
#else
   virtual int Count() const { return 0; }
   virtual bool Get(IReflectableData* data, int index) const { return false; }
   virtual bool Selecting(int index) { return false; }
#endif

   virtual const Header *GetHeader() const;
   virtual int ColumnsCount() const;

   virtual const DataReflector& DataType() const;
};

struct OrgDocsListData;
class OrgInfo
{
public:
   OrgInfo();
   OrgInfo(bool showInfo);
   ~OrgInfo();

   virtual void Init(const ROWID& id, CWindow parent, UINT titleID, UINT addressID, UINT contactsID, OrgDocsListData* data);
   void SwitchInfo() { showInfo = !showInfo; }

   void Paint(HDC dc);

   void UpdateLayout(int* contactsHeight, bool forceRecalc);

   bool CanHandle(LPNMHDR hdr) const { return (hdr->hwndFrom == contactList.m_hWnd); }
   bool Selecting(LPNMHDR hdr);
   
   LRESULT SetCellInfo(LPNMHDR hdr);

protected:
   virtual void InitContactData();

   bool showInfo;
   ContactData* contactData;
   ListViewMultiLine contactList;

   CWindow title, address;
   CWindow parent;
};

#endif
