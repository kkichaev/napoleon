/*
 * Copyright (C), 2006-2009, Денис Мосягин
 *
 * загрузка цен
 *
 *  ert   01/06/2009   creating
 */ 
#ifndef __COST_MANAGER_H
#define __COST_MANAGER_H

struct Costs
{
   WORD priceCount;
   WORD costCount;
   DWORD costTypeOffset;
   DWORD priceOffset;
};

class CostManager
{
public:
   struct CostItem
   {
      std::string id;
      std::string name;
   };
   typedef std::vector<CostItem> CostList;

   static const CostList& CostTypes();
   static DWORD GetCost(const wchar_t *itemID, const wchar_t *costType);
   static DWORD GetCost(const wchar_t *itemID, DWORD costType);
   static DWORD CostIndex(const wchar_t* costType);

   static void Clear();

   static long ReceiveCosts(std::wstring *answer, IProgressIndicator *pi);

protected:
   static bool loaded;

   static std::vector<std::string> price;
   static CostList costTypes;
   static Costs header;

   static void LoadCostData();
};

#endif
