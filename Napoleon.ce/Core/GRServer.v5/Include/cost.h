#ifndef __COST_IMPL_H
#define __COST_IMPL_H

#include <string>
#include <vector>
#include <set>

struct Costs
{
   WORD priceCount;
   WORD costCount;
   DWORD costTypeOffset;
   DWORD priceOffset;
};

struct CostType
{
   std::string name;
   std::string id;
};

struct CTypeSet
{
   std::string id;
   long index;

   bool operator<(const CTypeSet &ref) const { return id.compare(ref.id) < 0; }
};

struct ItemCostData : public std::vector<DWORD>
{
};

struct PriceCostItem
{
   std::string id;
	std::vector<ItemCostData> costs;

   bool operator<(const PriceCostItem& ref) const { return id.compare(ref.id) < 0; }
};


#define COST_SERVICE_NAME L"CostService"

class Binary;
class CostService
{
public:
	CostService() {}
	~CostService() {}

	virtual Binary* MakeCostsBinary(const std::vector<CostType> &types, const std::set<PriceCostItem> &price, DWORD dataFieldCount);
};
extern CostService costService;


inline DWORD ScaleDouble(double val, DWORD scale)
{
   if( val >= 0 )
      return (DWORD)((val + 1.0 / (scale * 10)) * scale);
   else
      return (DWORD)((val - 1.0 / (scale * 10)) * scale);
}

#endif 