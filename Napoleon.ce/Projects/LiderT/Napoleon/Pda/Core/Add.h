#ifndef __ADD_H
#define __ADD_H

#include <ObjImpl.h>

class DiscountImpl : public DBImpl<Discount>
{
public:
   DiscountImpl() : DBImpl(L"discounts") {}

   virtual const wchar_t*  KeyFields() const { return L"id"; }
   virtual const wchar_t** Indexes() const { return NULL; }
};


#endif