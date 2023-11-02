/*
 * Copyright (C), 2007-2009, Денис Мосягин
 *
 * РПК дополнения
 *
 *  ert   07/09/2009   creating
 */
#ifndef __ADD_RPK_H
#define __ADD_RPK_H

#include <Module.h>

#include <atlframe.h>
#include <atlctrls.h>
#include <atldlgs.h>

#include <atlmisc.h>
#include <atlscrl.h>

#include <DocImpl.h>
#include <PriceForm.h>

extern wchar_t dtDefects[];

//
//------------------------------------ Defetcts ----------------------------------------
//
class DefectImpl : public OrderImpl
{
public:

   DefectImpl() : OrderImpl(L"defects", dtDefects) { instanceFlags |= ifNoUpdatePrice;  }

   virtual bool CheckQty() const { return false; }
};

#endif