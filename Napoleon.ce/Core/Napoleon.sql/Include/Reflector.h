
/*
 * Copyright (C), 2006-2007, Денис Мосягин
 *
 * Exchange Reflection
 *
 *  ert   01/12/2006   creating
 */ 
#ifndef __REFLECTION_TYPE_H
#define __REFLECTION_TYPE_H

//
//------------------------------- TYPE REFLECTIONS ------------------------------------------
// ---------------------------------- Exchange ----------------------------------------------
//
#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
BEGIN_TYPE_REFLECTION(OrgUnit)
#ifdef ORG_UNITS
   REGISTER_ULONG_MEMBER(OrgUnit, id)
#elif ORG_UNITS_STR
   REGISTER_STRING_MEMBER(OrgUnit, id)
#endif
   REGISTER_STRING_MEMBER(OrgUnit, name)
#ifdef PCMagazine
   REGISTER_USHORT_MEMBER(OrgUnit, type)
#endif
END_TYPE_REFLECTION(OrgUnit)
#endif

#if defined(Alians)
BEGIN_TYPE_REFLECTION(Contact)
   REGISTER_STRING_MEMBER(Contact, name)
   REGISTER_STRING_MEMBER(Contact, phone)
   REGISTER_STRING_MEMBER(Contact, remark)
END_TYPE_REFLECTION(Contact)
#endif
#ifdef ORG_INFO
BEGIN_TYPE_REFLECTION(Contact)
   REGISTER_STRING_MEMBER(Contact, name)
   REGISTER_STRING_MEMBER(Contact, phone)
#if defined(Zakroma) || defined(SklRybinsk)
   REGISTER_STRING_MEMBER(Contact, remark)
#endif
END_TYPE_REFLECTION(Contact)
#endif

#if defined(Autopteka) || defined(Autopteka_van)
BEGIN_TYPE_REFLECTION(IncomeItem)
   REGISTER_STRING_MEMBER(IncomeItem, id)
   REGISTER_ULONG_MEMBER(IncomeItem, folderID)
   REGISTER_ULONG_SCALE_MEMBER2(IncomeItem, qty, QTY_SCALE, true)
   REGISTER_STRING_MEMBER(IncomeItem, remark)
END_TYPE_REFLECTION(IncomeItem)

BEGIN_TYPE_REFLECTION(Incomes)
   REGISTER_FILETIME_MEMBER(Incomes, date)
   REGISTER_COLLECTION_MEMBER(Incomes, items, IncomeItem)
END_TYPE_REFLECTION(Incomes)

BEGIN_TYPE_REFLECTION(Dogovor)
   REGISTER_STRING_MEMBER(Dogovor, number)
   REGISTER_STRING_MEMBER(Dogovor, name)
   REGISTER_FILETIME_MEMBER(Dogovor, from)
   REGISTER_FILETIME_MEMBER(Dogovor, till)
   REGISTER_STRING_MEMBER(Dogovor, costType)
END_TYPE_REFLECTION(Dogovor)

BEGIN_TYPE_REFLECTION(OrgDocID)
   REGISTER_STRING_MEMBER(OrgDocID, id)
END_TYPE_REFLECTION(OrgDocID)

BEGIN_TYPE_REFLECTION(OrgProp)
   REGISTER_STRING_MEMBER(OrgProp, name)
   REGISTER_STRING_MEMBER(OrgProp, value)
END_TYPE_REFLECTION(OrgProp)

BEGIN_TYPE_REFLECTION(RplItem)
  REGISTER_STRING_MEMBER(RplItem, id)
  REGISTER_ULONG_SCALE_MEMBER(RplItem, qty, QTY_SCALE)
END_TYPE_REFLECTION(RplItem)

BEGIN_TYPE_REFLECTION(Replenishment)
  REGISTER_COLLECTION_MEMBER(Replenishment, items, RplItem)
END_TYPE_REFLECTION(Replenishment)
#endif

#ifdef Autopteka
BEGIN_TYPE_REFLECTION(OrdVan)
   CHAIN_REFLECTION(OrdVan, Order)
   REGISTER_STRING_MEMBER(OrdVan, docNum)
   REGISTER_USHORT_MEMBER(OrdVan, account)
END_TYPE_REFLECTION(OrdVan)
#endif

#ifdef PRICE_MATRIX
BEGIN_TYPE_REFLECTION(MatrixItem)
  REGISTER_STRING_MEMBER(MatrixItem, id)
END_TYPE_REFLECTION(MatrixItem)

BEGIN_TYPE_REFLECTION(Matrix)
  REGISTER_STRING_MEMBER(Matrix, name)
  REGISTER_COLLECTION_MEMBER(Matrix, items, MatrixItem)
END_TYPE_REFLECTION(Matrix)
#endif

#ifdef ORG_TASK
BEGIN_TYPE_REFLECTION(Task)
   REGISTER_TIMESTAMP_MEMBER(Task, date)
   REGISTER_STRING_MEMBER(Task, id)
   REGISTER_STRING_MEMBER(Task, task)
   REGISTER_STRING_MEMBER(Task, doing)
   REGISTER_ULONG_MEMBER(Task, flags)
END_TYPE_REFLECTION(Task)
#endif

#ifdef Spartak
BEGIN_TYPE_REFLECTION(Dogovor)
   REGISTER_STRING_MEMBER(Dogovor, number)
   REGISTER_STRING_MEMBER(Dogovor, name)
   REGISTER_FILETIME_MEMBER(Dogovor, from)
   REGISTER_FILETIME_MEMBER(Dogovor, till)
   REGISTER_STRING_MEMBER(Dogovor, costType)
   REGISTER_STRING_MEMBER(Dogovor, firm)
END_TYPE_REFLECTION(Dogovor)
#endif

#ifdef Voshod
BEGIN_TYPE_REFLECTION(Dogovor)
   REGISTER_STRING_MEMBER(Dogovor, number)
   REGISTER_STRING_MEMBER(Dogovor, name)
   REGISTER_STRING_MEMBER(Dogovor, ctype)
   REGISTER_STRING_MEMBER(Dogovor, firm)
END_TYPE_REFLECTION(Dogovor)

BEGIN_TYPE_REFLECTION(DiscountPriceItem)
   REGISTER_USHORT_MEMBER(DiscountPriceItem, index)
END_TYPE_REFLECTION(DiscountPriceItem)

BEGIN_TYPE_REFLECTION(DiscountItem)
   REGISTER_SHORT_SCALE_MEMBER(DiscountItem, discount, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(DiscountItem, qty, QTY_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(DiscountItem, sum, SUM_SCALE)
   REGISTER_COLLECTION_MEMBER(DiscountItem, items, DiscountPriceItem)
END_TYPE_REFLECTION(DiscountItem)

BEGIN_TYPE_REFLECTION(Discount)
   REGISTER_STRING_MEMBER(Discount, id)
   REGISTER_STRING_MEMBER(Discount, dogovor)
   REGISTER_COLLECTION_MEMBER(Discount, items, DiscountItem)
END_TYPE_REFLECTION(Discount)

BEGIN_TYPE_REFLECTION(CloseFirmItem)
   REGISTER_STRING_MEMBER(CloseFirmItem, firm)
END_TYPE_REFLECTION(CloseFirmItem)
#endif
#ifdef LiderT
BEGIN_TYPE_REFLECTION(Card)
   REGISTER_STRING_MEMBER(Card, id)
   REGISTER_STRING_MEMBER(Card, name)
   REGISTER_STRING_MEMBER(Card, costype)
END_TYPE_REFLECTION(Card)
BEGIN_TYPE_REFLECTION(DiscountItem)
   REGISTER_ULONG_MEMBER(DiscountItem, id)
   REGISTER_SHORT_SCALE_MEMBER(DiscountItem, discount, DISCOUNT_SCALE)
END_TYPE_REFLECTION(DiscountItem)
BEGIN_TYPE_REFLECTION(Discount)
   REGISTER_STRING_MEMBER(Discount, id)
   REGISTER_COLLECTION_MEMBER(Discount, items, DiscountItem)
END_TYPE_REFLECTION(Discount)
#endif
#if defined(Tarpan_East) || defined(RosProdukt)
BEGIN_TYPE_REFLECTION(Dogovor)
   REGISTER_STRING_MEMBER(Dogovor, id)
   REGISTER_STRING_MEMBER(Dogovor, name)
END_TYPE_REFLECTION(Dogovor)
#endif
#ifdef ERCom
BEGIN_TYPE_REFLECTION(OrgDiscount)
   REGISTER_LONG_MEMBER(OrgDiscount, id)
   REGISTER_LONG_SCALE_MEMBER(OrgDiscount, discount, SUM_SCALE)
END_TYPE_REFLECTION(OrgDiscount)
#endif
#if defined(Orange) || defined(Provisia)
BEGIN_TYPE_REFLECTION(Refrigerator)
   REGISTER_STRING_MEMBER(Refrigerator, id)
   REGISTER_STRING_MEMBER(Refrigerator, name)
END_TYPE_REFLECTION(Refrigerator)
#endif
#ifdef BastionNeva
BEGIN_TYPE_REFLECTION(OrgProps)
   REGISTER_STRING_MEMBER(OrgProps, key)
   REGISTER_STRING_MEMBER(OrgProps, value)
END_TYPE_REFLECTION(OrgProps)
#endif
#ifdef Fusion
BEGIN_TYPE_REFLECTION(OrgDog)
   REGISTER_STRING_MEMBER(OrgDog, id)
   REGISTER_STRING_MEMBER(OrgDog, name)
END_TYPE_REFLECTION(OrgDog)
#endif

#if defined(Michailova_O)
BEGIN_TYPE_REFLECTION(OrgMatrix)
   REGISTER_STRING_MEMBER(OrgMatrix, name)
END_TYPE_REFLECTION(OrgMatrix)
#endif

BEGIN_TYPE_REFLECTION(Org)
   REGISTER_STRING_MEMBER(Org, id)
   REGISTER_STRING_MEMBER(Org, name)
#ifdef ORG_INFO
   REGISTER_STRING_MEMBER(Org, address)
   REGISTER_COLLECTION_MEMBER(Org, contacts, Contact)
#endif
#if defined(STOP_LIST) || defined(Vkk)
   REGISTER_USHORT_MEMBER(Org, flags)
#endif
#ifdef Migma
   REGISTER_STRING_MEMBER(Org, dcost)
#endif
#ifdef ORG_COST_TYPE
   REGISTER_USHORT_MEMBER(Org, costype)
#endif
#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   REGISTER_COLLECTION_MEMBER(Org, units, OrgUnit)
#endif
#ifdef Alians
   REGISTER_ULONG_MEMBER(Org, folderID)
   REGISTER_ULONG_MEMBER(Org, sort)
   REGISTER_STRING_MEMBER(Org, address)
   REGISTER_STRING_MEMBER(Org, workTime)
   REGISTER_STRING_MEMBER(Org, dinner)
   REGISTER_STRING_MEMBER(Org, remark)
   REGISTER_COLLECTION_MEMBER(Org, contacts, Contact)
   REGISTER_USHORT_MEMBER(Org, flags)
#endif
#ifdef Ila
   REGISTER_STRING_MEMBER(Org, suplDay)
#endif
#if defined(Troya) || defined(Provisia)
   REGISTER_USHORT_MEMBER(Org, kind)
#endif
#ifdef RPK
   REGISTER_STRING_MEMBER(Org, dogovor)
   REGISTER_USHORT_MEMBER(Org, discount)
   REGISTER_USHORT_MEMBER(Org, payDelay)
#endif
#if defined(Byloe) || defined(Byloe2)
   REGISTER_STRING_MEMBER(Org, phone)
   REGISTER_ULONG_MEMBER(Org, flags)
#endif
#ifdef Orange
   //REGISTER_ULONG_MEMBER(Org, costType)
   REGISTER_USHORT_MEMBER(Org, delay)
   REGISTER_COLLECTION_MEMBER(Org, refrigerators, Refrigerator)
   REGISTER_COLLECTION_MEMBER(Org, matrix, MatrixItem)
#endif
#ifdef Provisia
   REGISTER_COLLECTION_MEMBER(Org, refrigerators, Refrigerator)
#endif
#if defined(Autopteka) || defined(Autopteka_van)
   REGISTER_ULONG_MEMBER(Org, color)
   REGISTER_ULONG_SCALE_MEMBER(Org, premium, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(Org, plan, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(Org, fact, SUM_SCALE)
   REGISTER_FILETIME_MEMBER(Org, firstDebt)
   REGISTER_COLLECTION_MEMBER(Org, dogovors, Dogovor)
   REGISTER_COLLECTION_MEMBER(Org, props, OrgProp)
#ifdef Autopteka_van
   REGISTER_STRING_MEMBER(Org, phone)
   REGISTER_STRING_MEMBER(Org, inn)
   REGISTER_STRING_MEMBER(Org, bank)
   REGISTER_STRING_MEMBER(Org, fullName)
   REGISTER_COLLECTION_MEMBER(Org, dogOrgID, OrgDocID)
#endif
#ifdef Autopteka
   REGISTER_ULONG_SCALE_MEMBER(Org, minPremium, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(Org, minOrder, SUM_SCALE)
#endif
#endif
#ifdef PCMagazine
   REGISTER_USHORT_MEMBER(Org, delay)
   REGISTER_USHORT_MEMBER(Org, type)
#endif
#ifdef Spartak
   REGISTER_ULONG_MEMBER(Org, color)
   REGISTER_COLLECTION_MEMBER(Org, dogovors, Dogovor)
   REGISTER_COLLECTION_MEMBER(Org, matrix, MatrixItem)
#endif
#if defined(MediaDistribution) || (defined (VAN_SELLING) && !defined(Autopteka_van))
   REGISTER_STRING_MEMBER(Org, phone)
   REGISTER_STRING_MEMBER(Org, inn)
   REGISTER_STRING_MEMBER(Org, bank)
#endif
#ifdef ORG_COLOR
   REGISTER_ULONG_MEMBER(Org, color)
#endif
#ifdef VinStyle
   REGISTER_USHORT_MEMBER(Org, delay)
#endif
#ifdef Voshod
   REGISTER_COLLECTION_MEMBER(Org, dogovors, Dogovor)
   REGISTER_STRING_MEMBER(Org, ido)
   REGISTER_COLLECTION_MEMBER(Org, closed, CloseFirmItem)
#endif
#ifdef Fusion
   REGISTER_STRING_MEMBER(Org, factAddress)
   REGISTER_STRING_MEMBER(Org, fullName)
   REGISTER_STRING_MEMBER(Org, baseOrg)
   REGISTER_COLLECTION_MEMBER(Org, dogovors, OrgDog)
#endif
#ifdef Abdullin
   REGISTER_SHORT_SCALE_MEMBER(Org, discount, SUM_SCALE)
#endif
#ifdef Enoteka
   REGISTER_ULONG_MEMBER(Org, state)
#endif
#ifdef Leopard
   REGISTER_STRING_MEMBER(Org, costype)
   REGISTER_USHORT_MEMBER(Org, payDelay)
   REGISTER_ULONG_SCALE_MEMBER(Org, limit, SUM_SCALE)
   REGISTER_STRING_MEMBER(Org, stopMsg)
#endif
#if defined(Suchanov) || defined(TKSibir)
   REGISTER_STRING_MEMBER(Org, info)
#endif
#ifdef Suchanov
   REGISTER_STRING_MEMBER(Org, type1)
   REGISTER_STRING_MEMBER(Org, type2)
#endif
#ifdef Leonov
   REGISTER_FILETIME_MEMBER(Org, endDogovor)
   REGISTER_USHORT_MEMBER(Org, firm)
#endif
#ifdef LiderT
   REGISTER_COLLECTION_MEMBER(Org, cards, Card)
#endif
#if defined(Tarpan_East) || defined(RosProdukt)
   REGISTER_COLLECTION_MEMBER(Org, dogovors, Dogovor)
#endif
#if defined(BastionNeva) | defined(Kolbiko) | defined(Tukanov)
   REGISTER_COLLECTION_MEMBER(Org, matrix, MatrixItem)
#endif
#ifdef Lira
   REGISTER_FILETIME_MEMBER(Org, endLicense)
#endif
#ifdef Vkk
   REGISTER_USHORT_MEMBER(Org, delay)
   REGISTER_USHORT_MEMBER(Org, firm)
#endif
#ifdef Kolbiko
   REGISTER_USHORT_SCALE_MEMBER(Org, coef, SUM_SCALE)
#endif
#ifdef KondMir
   REGISTER_LONG_SCALE_MEMBER(Org, discount, SUM_SCALE)
   REGISTER_USHORT_MEMBER(Org, firm)
#endif
#ifdef DELIVERY_ADDRESS
   REGISTER_COLLECTION_MEMBER(Org, orgAddress, OrgAddress)
#endif
#ifdef ERCom
   REGISTER_COLLECTION_MEMBER(Org, discounts, OrgDiscount)
#endif
#ifdef KirovOpt
   REGISTER_STRING_MEMBER(Org, code)
#endif
#ifdef Fortune
   REGISTER_STRING_MEMBER(Org, stopMsg)
   REGISTER_STRING_MEMBER(Org, debtMsg)
#endif
#if defined(BastionNeva)
   REGISTER_COLLECTION_MEMBER(Org, params, OrgProps)
#endif
#if defined(Michailova_O)
   REGISTER_COLLECTION_MEMBER(Org, matrix, OrgMatrix)
#endif
END_TYPE_REFLECTION(Org)

#ifdef DELIVERY_ADDRESS
BEGIN_TYPE_REFLECTION(OrgAddress)
   REGISTER_STRING_MEMBER(OrgAddress, id)
   REGISTER_STRING_MEMBER(OrgAddress, name)
END_TYPE_REFLECTION(OrgAddress)
#endif

#ifdef Autopteka
BEGIN_TYPE_REFLECTION(OrgVan)
   REGISTER_STRING_MEMBER(OrgVan, id)
   REGISTER_STRING_MEMBER(OrgVan, name)
#ifdef ORG_INFO
   REGISTER_STRING_MEMBER(OrgVan, address)
   REGISTER_COLLECTION_MEMBER(OrgVan, contacts, Contact)
#endif
#ifdef STOP_LIST
   REGISTER_USHORT_MEMBER(OrgVan, flags)
#endif
#ifdef ORG_COST_TYPE
   REGISTER_USHORT_MEMBER(OrgVan, costype)
#endif
#if defined(ORG_UNITS) || defined(ORG_UNITS_STR)
   REGISTER_COLLECTION_MEMBER(OrgVan, units, OrgUnit)
#endif
#if defined(Autopteka) || defined(Autopteka_van)
   REGISTER_ULONG_MEMBER(OrgVan, color)
   REGISTER_ULONG_SCALE_MEMBER(OrgVan, premium, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(OrgVan, plan, SUM_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(OrgVan, fact, SUM_SCALE)
   REGISTER_FILETIME_MEMBER(OrgVan, firstDebt)
   REGISTER_COLLECTION_MEMBER(OrgVan, dogovors, Dogovor)
   REGISTER_COLLECTION_MEMBER(OrgVan, props, OrgProp)
   REGISTER_STRING_MEMBER(OrgVan, phone)
   REGISTER_STRING_MEMBER(OrgVan, inn)
   REGISTER_STRING_MEMBER(OrgVan, bank)
   REGISTER_STRING_MEMBER(OrgVan, fullName)
   REGISTER_COLLECTION_MEMBER(OrgVan, dogOrgID, OrgDocID)
#endif
END_TYPE_REFLECTION(OrgVan)

#endif

BEGIN_TYPE_REFLECTION(FolderObj)
   REGISTER_STRING_MEMBER(FolderObj, name)
   REGISTER_ULONG_MEMBER(FolderObj, id)
   //REGISTER_USHORT_MEMBER(FolderObj, size)
   REGISTER_USHORT_MEMBER(FolderObj, level)
#if defined(Imperia) || defined(Suchanov) || defined(Repnikov) || defined(Kirov_Pavel) || defined(Michailov_V)
   REGISTER_ULONG_MEMBER(FolderObj, firstID)
#endif
#if defined(ORD_SURVAY) || defined(Metelica)
   REGISTER_STRING_MEMBER(FolderObj, fid)
#endif
END_TYPE_REFLECTION(FolderObj)

#ifdef Alians
BEGIN_TYPE_REFLECTION(OrgFolder)
   REGISTER_STRING_MEMBER(OrgFolder, name)
   REGISTER_ULONG_MEMBER(OrgFolder, id)
   REGISTER_USHORT_MEMBER(OrgFolder, level)
   REGISTER_ULONG_MEMBER(OrgFolder, sort)
END_TYPE_REFLECTION(OrgFolder)
#else
BEGIN_TYPE_REFLECTION(OrgFolderItem)
   REGISTER_STRING_MEMBER(OrgFolderItem, name)
#if defined(Alians_sp) || defined(SHEDULE) || defined(Kolbiko)
   REGISTER_STRING_MEMBER(OrgFolderItem, time)
#endif
END_TYPE_REFLECTION(OrgFolderItem)

BEGIN_TYPE_REFLECTION(OrgFolder)
   REGISTER_STRING_MEMBER(OrgFolder, name)
   REGISTER_COLLECTION_MEMBER(OrgFolder, items, OrgFolderItem)
END_TYPE_REFLECTION(OrgFolder)
#endif

#ifdef Alians
BEGIN_TYPE_REFLECTION(Packet)
   REGISTER_STRING_MEMBER(Packet, code)
   REGISTER_ULONG_SCALE_MEMBER2(Packet, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(Packet)

BEGIN_TYPE_REFLECTION(PacketsChain)
   REGISTER_ULONG_SCALE_MEMBER2(PacketsChain, qty, QTY_SCALE, true)
   REGISTER_COLLECTION_MEMBER(PacketsChain, chain, Packet)
END_TYPE_REFLECTION(PacketsChain)
#endif

#if defined (Agama) || defined (Byloe) || defined(Byloe2)
BEGIN_TYPE_REFLECTION(PQty)
   REGISTER_LONG_SCALE_MEMBER2(PQty, qty, QTY_SCALE, true)
END_TYPE_REFLECTION(PQty)
#endif

BEGIN_TYPE_REFLECTION(CostItem)
  REGISTER_LONG_SCALE_MEMBER(CostItem, cost, SUM_SCALE)
END_TYPE_REFLECTION(CostItem)

#if defined(MULTI_WH) || defined(FIRMS_REST) || defined(WH_QTY)
BEGIN_TYPE_REFLECTION(QtyItem)
  REGISTER_LONG_SCALE_MEMBER(QtyItem, qty, QTY_SCALE)
END_TYPE_REFLECTION(QtyItem)
#endif

BEGIN_TYPE_REFLECTION(Price)
   REGISTER_STRING_MEMBER(Price, id)
   REGISTER_STRING_MEMBER(Price, name)
   REGISTER_STRING_MEMBER(Price, photo)
   REGISTER_ULONG_MEMBER(Price, folderID)   

   REGISTER_COLLECTION_MEMBER(Price, cost, CostItem)

#ifdef MULTI_WH
   REGISTER_COLLECTION_MEMBER(Price, qty, QtyItem)
#else
   REGISTER_LONG_SCALE_MEMBER(Price, qty, QTY_SCALE)
#endif
#ifdef FIRMS_REST
   REGISTER_COLLECTION_MEMBER(Price, firmQty, QtyItem)
#endif
   REGISTER_ULONG_SCALE_MEMBER2(Price, qtyInPack, QTY_SCALE, true)
   REGISTER_USHORT_MEMBER(Price, flags)
   REGISTER_USHORT_MEMBER(Price, tax1)

#ifdef Migma
   REGISTER_STRING_MEMBER(Price, remark)
#endif

#ifdef Suchanov
   REGISTER_USHORT_MEMBER(Price, type)
#else
   REGISTER_ULONG_SCALE_MEMBER2(Price, weight, WEIGHT_SCALE, true)
#endif

#ifdef Provisia
   REGISTER_ULONG_MEMBER(Price, minPart)
   REGISTER_USHORT_MEMBER(Price, types)
#endif
#ifdef Troya
   REGISTER_USHORT_MEMBER(Price, types)
#endif
#ifdef Alians
   REGISTER_COLLECTION_MEMBER(Price, packets, Packet)
#endif
#ifdef PRICE_COLOR
   REGISTER_ULONG_MEMBER(Price, color)
#endif
#if defined(Byloe) || defined(Byloe2)
   REGISTER_COLLECTION_MEMBER(Price, qtys, PQty)
#endif
#ifdef Autopteka
  REGISTER_STRING_MEMBER(Price, article)
#endif
#ifdef Autopteka_van
  REGISTER_STRING_MEMBER(Price, article)
  REGISTER_STRING_MEMBER(Price, packName)
  REGISTER_STRING_MEMBER(Price, ntd)
  REGISTER_STRING_MEMBER(Price, country)
  REGISTER_STRING_MEMBER(Price, countryCode)
#endif
#ifdef Gudkova
  REGISTER_ULONG_MEMBER(Price, itemQty)
#endif
#ifdef KK
  REGISTER_ULONG_SCALE_MEMBER(Price, minPart, QTY_SCALE)
#endif
#ifdef Spartak
  REGISTER_STRING_MEMBER(Price, article)
#endif
#if defined(MediaDistribution) || (defined (VAN_SELLING) && !defined(Autopteka_van))
  REGISTER_STRING_MEMBER(Price, packName)
  REGISTER_STRING_MEMBER(Price, ntd)
  REGISTER_STRING_MEMBER(Price, country)
  REGISTER_STRING_MEMBER(Price, countryCode)
  REGISTER_STRING_MEMBER(Price, unitCode)
#endif
#ifdef Voshod
  REGISTER_STRING_MEMBER(Price, unitName)
#endif
#ifdef Leopard
  REGISTER_COLLECTION_MEMBER(Price, packs, PackItem)
#endif
#ifdef BastionNeva
  REGISTER_ULONG_SCALE_MEMBER(Price, limit, QTY_SCALE)
#endif
#ifdef Vkk
  REGISTER_ULONG_SCALE_MEMBER(Price, mult, QTY_SCALE)
#endif
#ifdef Kolbiko
   REGISTER_ULONG_SCALE_MEMBER(Price, avgWeight, WEIGHT_SCALE)
   REGISTER_STRING_MEMBER(Price, unitName)
#endif
#ifdef WH_QTY
  REGISTER_COLLECTION_MEMBER(Price, whQty, QtyItem)
#endif
#ifdef Volnenko
   REGISTER_ULONG_SCALE_MEMBER(Price, minNac, SUM_SCALE)
#endif
END_TYPE_REFLECTION(Price)

#ifdef Leopard
BEGIN_TYPE_REFLECTION(PackItem)
   REGISTER_STRING_MEMBER(PackItem, pack)
   REGISTER_STRING_MEMBER(PackItem, warehouse)
   REGISTER_ULONG_MEMBER(PackItem, flags)
   REGISTER_ULONG_SCALE_MEMBER(PackItem, inPack,  QTY_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(PackItem, qty,  QTY_SCALE)
END_TYPE_REFLECTION(PackItem)
#endif

#ifdef Autopteka
BEGIN_TYPE_REFLECTION(PriceVan)
   REGISTER_STRING_MEMBER(PriceVan, id)
   REGISTER_STRING_MEMBER(PriceVan, name)
   REGISTER_STRING_MEMBER(PriceVan, photo)
   REGISTER_ULONG_MEMBER(PriceVan, folderID)   

   REGISTER_COLLECTION_MEMBER(PriceVan, cost, CostItem)

   REGISTER_LONG_SCALE_MEMBER(PriceVan, qty, QTY_SCALE)
   REGISTER_ULONG_MEMBER(PriceVan, qtyInPack)
   REGISTER_USHORT_MEMBER(PriceVan, flags)
   REGISTER_USHORT_MEMBER(PriceVan, tax1)

   REGISTER_ULONG_MEMBER(PriceVan, weight)
#ifdef PRICE_COLOR
   REGISTER_ULONG_MEMBER(PriceVan, color)
#endif
#ifdef Autopteka
  REGISTER_STRING_MEMBER(PriceVan, article)
  REGISTER_STRING_MEMBER(PriceVan, packName)
  REGISTER_STRING_MEMBER(PriceVan, ntd)
  REGISTER_STRING_MEMBER(PriceVan, country)
#endif
END_TYPE_REFLECTION(PriceVan)
#endif

BEGIN_TYPE_REFLECTION(PriceRemnants)
   REGISTER_STRING_MEMBER(PriceRemnants, id)
#ifdef MULTI_WH
   REGISTER_COLLECTION_MEMBER(PriceRemnants, qty, QtyItem)
#else
   REGISTER_LONG_SCALE_MEMBER(PriceRemnants, qty, QTY_SCALE)
#endif

#ifdef FIRMS_REST
   REGISTER_COLLECTION_MEMBER(PriceRemnants, firmQty, QtyItem)
#endif

#ifdef Agama
   REGISTER_COLLECTION_MEMBER(PriceRemnants, qtys, PQty)
#endif
END_TYPE_REFLECTION(PriceRemnants)

#ifdef ORD_SURVAY
BEGIN_TYPE_REFLECTION(Survay)
   REGISTER_ULONG_MEMBER(Survay, folder)
#ifdef COMPATIBILITY
#else
   REGISTER_STRING_MEMBER(Survay, fid)
#endif
   REGISTER_STRING_MEMBER(Survay, choice)
END_TYPE_REFLECTION(Survay)
#endif

BEGIN_TYPE_REFLECTION(OrderItem)
   REGISTER_STRING_MEMBER(OrderItem, id)
   REGISTER_LONG_SCALE_MEMBER2(OrderItem, qty, QTY_SCALE, true)
   REGISTER_ULONG_SCALE_MEMBER2(OrderItem, cost, SUM_SCALE, true)
   REGISTER_USHORT_MEMBER(OrderItem, flags)
#ifdef Alians
   REGISTER_COLLECTION_MEMBER(OrderItem, packets, PacketsChain)
#endif
#ifdef Proviant
   REGISTER_STRING_MEMBER(OrderItem, unit)
#endif
#ifdef ORD_ITEM_DISCOUNT
   REGISTER_LONG_SCALE_MEMBER(OrderItem, discount, DISCOUNT_SCALE)
#endif
#ifdef Leopard
   REGISTER_STRING_MEMBER(OrderItem, pack)
#endif
#ifdef SHOW_OFF_TAKE
#ifdef Provisia
#else
   REGISTER_LONG_SCALE_MEMBER(OrderItem, offTakeDiff, QTY_SCALE)
#endif
#endif
#ifdef Volnenko
   REGISTER_ULONG_MEMBER(OrderItem, costType)
#endif
//#ifdef ERCom
//   REGISTER_LONG_SCALE_MEMBER(OrderItem, discount, SUM_SCALE)
//#endif
END_TYPE_REFLECTION(OrderItem)

#ifdef Polus
BEGIN_TYPE_REFLECTION(PdaTime)
   REGISTER_FILETIME_MEMBER(PdaTime, time)
END_TYPE_REFLECTION(PdaTime)
#endif

BEGIN_TYPE_REFLECTION(Order)
   REGISTER_TIMESTAMP_MEMBER(Order, created)
   REGISTER_TIMESTAMP_MEMBER(Order, date)
   REGISTER_STRING_MEMBER(Order, id)
   REGISTER_ULONG_MEMBER(Order, params)
   REGISTER_USHORT_MEMBER(Order, supplyer)
   REGISTER_USHORT_MEMBER(Order, sumType)
   REGISTER_STRING_MEMBER(Order, remark)
   REGISTER_COLLECTION_MEMBER(Order, items, OrderItem)
   REGISTER_SHORT_MEMBER(Order, timeZone)
#ifdef PAY_DELAY
   REGISTER_USHORT_MEMBER(Order, delay)
#endif
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(Order, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(Order, longitude, GPS_SCALE)
#endif
#ifdef ORG_UNITS
   REGISTER_ULONG_MEMBER(Order, unitCode)
#elif ORG_UNITS_STR
   REGISTER_STRING_MEMBER(Order, unitCode)
#endif
#if defined(Provisia) || defined(ORDER_DISCOUNT)
   REGISTER_SHORT_SCALE_MEMBER(Order, discount, DISCOUNT_SCALE)
   REGISTER_USHORT_MEMBER(Order, sendBefore)
#endif
#ifdef ORD_DLV_BIND
   REGISTER_STRING_MEMBER(Order, number)
#endif
#ifdef POD_COMMENT
   REGISTER_STRING_MEMBER(Order, podRemark)
#endif
#ifdef Agama
   REGISTER_FILETIME_MEMBER(Order, supplDate)
   REGISTER_ULONG_MEMBER(Order, unitCode)
#endif
#ifdef Suchanov
   REGISTER_ULONG_MEMBER(Order, flags)
   REGISTER_USHORT_MEMBER(Order, bank)
   REGISTER_TIMESTAMP_MEMBER(Order, pay)
   REGISTER_USHORT_MEMBER(Order, specCondition)
   REGISTER_SHORT_SCALE_MEMBER(Order, discount, DISCOUNT_SCALE)
#endif
#ifdef Alians
   REGISTER_ULONG_MEMBER(Order, orderNumber)
#endif
#if defined(Alians_sp) || defined(SHEDULE)
  REGISTER_FILETIME_MEMBER(Order, shedule)
#endif
#if defined(Byloe) || defined(Byloe2)
   REGISTER_STRING_MEMBER(Order, whCode)
   REGISTER_STRING_MEMBER(Order, prcCode)
#endif
//#if defined(Orange)
//   REGISTER_ULONG_MEMBER(Order, unitCode)
//#endif
#if defined(Autopteka) || defined(Autopteka_van)
   REGISTER_FILETIME_MEMBER(Order, supplDate)
   REGISTER_ULONG_SCALE_MEMBER(Order, paySum, SUM_SCALE)
   REGISTER_STRING_MEMBER(Order, dogNum)
   REGISTER_STRING_MEMBER(Order, sumTypeID)
#endif
#ifdef ORD_SURVAY
   REGISTER_COLLECTION_MEMBER(Order, survay, Survay)
#endif
#if defined(Zakroma) || defined(SklRybinsk)
   REGISTER_ULONG_SCALE_MEMBER(Order, collectSum, SUM_SCALE) // инкассация
   REGISTER_STRING_MEMBER(Order, collectNum)
   REGISTER_STRING_MEMBER(Order, logistic)
   REGISTER_STRING_MEMBER(Order, fcontrol)
#endif
#ifdef Polus
   REGISTER_FILETIME_MEMBER(Order, curPdaTime)
   REGISTER_COLLECTION_MEMBER(Order, pdaTimeChanged, PdaTime)
#endif
#if defined(Autopteka_van)
   REGISTER_STRING_MEMBER(Order, docNum)
   REGISTER_USHORT_MEMBER(Order, account)
#endif
#ifdef Spartak
   REGISTER_STRING_MEMBER(Order, supplCode)
   REGISTER_STRING_MEMBER(Order, costCode)
   REGISTER_STRING_MEMBER(Order, dogovor)
#endif
#ifdef MULTI_WH
#ifdef Agama
   REGISTER_ULONG_MEMBER(Order, whIndex)
#else
   REGISTER_STRING_MEMBER(Order, warehouseCode)
#endif
#endif
#ifdef Judaev
   REGISTER_FILETIME_MEMBER(Order, invoiceClose);
#endif
#if defined(MediaDistribution)
   REGISTER_STRING_MEMBER(Order, docNum)
   REGISTER_USHORT_MEMBER(Order, account)
#endif
#if (defined (VAN_SELLING) && !defined(Autopteka_van))
   REGISTER_STRING_MEMBER(Order, docNum)
   REGISTER_STRING_MEMBER(Order, supplCode)
#endif
#ifdef Voshod
   REGISTER_STRING_MEMBER(Order, dogovor)
   REGISTER_STRING_MEMBER(Order, suplCode)
   REGISTER_STRING_MEMBER(Order, retNum)
   REGISTER_STRING_MEMBER(Order, dlvCode)
#endif
#ifdef PROVISIA_ADD
   REGISTER_STRING_MEMBER(Order, supplCode)
#endif
#ifdef Sega
   REGISTER_STRING_MEMBER(Order, supplCode)
   REGISTER_STRING_MEMBER(Order, costType)
#endif
#ifdef Leopard
   REGISTER_STRING_MEMBER(Order, supplCode)
   REGISTER_STRING_MEMBER(Order, costType)
   REGISTER_STRING_MEMBER(Order, ordType)
   REGISTER_STRING_MEMBER(Order, whCode)
#endif
#ifdef LiderT
   REGISTER_STRING_MEMBER(Order, card)
#endif
#if defined(Tarpan_East) || defined(RosProdukt)
   REGISTER_STRING_MEMBER(Order, dogovor)
#endif
#ifdef Kolbiko
   REGISTER_STRING_MEMBER(Order, payType)
   REGISTER_TIMESTAMP_MEMBER(Order, shedule);
   REGISTER_TIMESTAMP_MEMBER(Order, ordDate);
#endif
#ifdef Migma
   REGISTER_STRING_MEMBER(Order, costtype)
#endif
#ifdef DELIVERY_ADDRESS
   REGISTER_STRING_MEMBER(Order, adrCode)
#endif
#ifdef KirovOpt
   REGISTER_STRING_MEMBER(Order, unit)
#endif
#ifdef WH_QTY
  REGISTER_ULONG_MEMBER(Order, whIndex)
#endif
#ifdef Fusion
   REGISTER_STRING_MEMBER(Order, dogovor)
#endif
END_TYPE_REFLECTION(Order)

BEGIN_TYPE_REFLECTION(Config)
   REGISTER_STRING_MEMBER(Config, key)
   REGISTER_STRING_MEMBER(Config, value)
END_TYPE_REFLECTION(Config)

BEGIN_TYPE_REFLECTION(DeliveryItem)
   REGISTER_STRING_MEMBER(DeliveryItem, id)
   REGISTER_ULONG_SCALE_MEMBER2(DeliveryItem, qty, QTY_SCALE, true)
   REGISTER_ULONG_SCALE_MEMBER(DeliveryItem, sum, SUM_SCALE)
END_TYPE_REFLECTION(DeliveryItem)

#ifdef Alians
BEGIN_TYPE_REFLECTION(DeliveryPay)
   REGISTER_STRING_MEMBER(DeliveryPay, number)
   REGISTER_FILETIME_MEMBER(DeliveryPay, date)
   REGISTER_ULONG_SCALE_MEMBER(DeliveryPay, sum, SUM_SCALE)
END_TYPE_REFLECTION(DeliveryPay)
#endif

BEGIN_TYPE_REFLECTION(Delivery)
   REGISTER_FILETIME_MEMBER(Delivery, date)
   REGISTER_STRING_MEMBER(Delivery, id)
   REGISTER_STRING_MEMBER(Delivery, number)
   REGISTER_COLLECTION_MEMBER(Delivery, items, DeliveryItem)
#ifdef MAKE_BALANCE
#else
   REGISTER_LONG_SCALE_MEMBER(Delivery, sumD, SUM_SCALE)
#endif
#if defined(Zakroma) || defined(SklRybinsk)
   REGISTER_ULONG_SCALE_MEMBER(Delivery, sumT, SUM_SCALE)
#endif
#ifdef ORD_DLV_BIND
   REGISTER_FILETIME_MEMBER(Delivery, created)
#endif
#ifdef Alians
   REGISTER_ULONG_MEMBER(Delivery, costType)
   REGISTER_COLLECTION_MEMBER(Delivery, pays, DeliveryPay)
#endif
#if defined(Gudkova) || defined(Michailova_O)
   REGISTER_FILETIME_MEMBER(Delivery, payDate)
#endif
#if defined(Autopteka) || defined(Autopteka_van)
   REGISTER_FILETIME_MEMBER(Delivery, payDate)
#endif
#ifdef Suchanov
   REGISTER_STRING_MEMBER(Delivery, agent)
   REGISTER_FILETIME_MEMBER(Delivery, payDate)
   REGISTER_COLLECTION_MEMBER(Delivery, values, Config)
#endif
#ifdef Spartak
   REGISTER_FILETIME_MEMBER(Delivery, payDate)
#endif
#ifdef Voshod
   REGISTER_STRING_MEMBER(Delivery, supplyer)
   REGISTER_STRING_MEMBER(Delivery, dogId)
   REGISTER_USHORT_MEMBER(Delivery, fiscal)
   REGISTER_STRING_MEMBER(Delivery, type)
#endif
#ifdef HappyLand
   REGISTER_FILETIME_MEMBER(Delivery, payDate)
#endif
#ifdef BastionNeva
   REGISTER_FILETIME_MEMBER(Delivery, payDate)
#endif
END_TYPE_REFLECTION(Delivery)

BEGIN_TYPE_REFLECTION(Payment)
   REGISTER_FILETIME_MEMBER(Payment, date)
   REGISTER_STRING_MEMBER(Payment, id)
   REGISTER_STRING_MEMBER(Payment, number)
   REGISTER_ULONG_SCALE_MEMBER(Payment, sum, SUM_SCALE)
#ifdef Agama
   REGISTER_ULONG_MEMBER(Payment, sum2)
#endif
#ifdef RPK
   REGISTER_ULONG_MEMBER(Payment, color)
   REGISTER_ULONG_SCALE_MEMBER(Payment, outSum, SUM_SCALE)
#endif
#ifdef Repnikov
   REGISTER_FILETIME_MEMBER(Payment, dlvDate)
   REGISTER_ULONG_MEMBER(Payment, dlvSum)
   REGISTER_USHORT_MEMBER(Payment, payDelay)
#endif
#ifdef Provisia
   REGISTER_FILETIME_MEMBER(Payment, dlvDate)
   REGISTER_ULONG_MEMBER(Payment, color)
   REGISTER_USHORT_MEMBER(Payment, delay)
   REGISTER_STRING_MEMBER(Payment, type)
#endif
#ifdef VAN_SELLING
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(Payment, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(Payment, longitude, GPS_SCALE)
#endif
   REGISTER_TIMESTAMP_MEMBER(Payment, created)
   REGISTER_ULONG_MEMBER(Payment, params)
   REGISTER_STRING_MEMBER(Payment, remark)
   REGISTER_STRING_MEMBER(Payment, supplyer)
   REGISTER_ULONG_SCALE_MEMBER(Payment, sumTax, SUM_SCALE)
#endif
#ifdef Voshod
   REGISTER_STRING_MEMBER(Payment, supplyer)
   REGISTER_STRING_MEMBER(Payment, dogId)
   REGISTER_USHORT_MEMBER(Payment, fiscal)
#endif
#if defined(Byloe) || defined(Byloe2)
   REGISTER_ULONG_SCALE_MEMBER(Payment, dlvSum, SUM_SCALE)
   REGISTER_SHORT_MEMBER(Payment, payDelay)
   REGISTER_SHORT_MEMBER(Payment, overDelay)
   REGISTER_STRING_MEMBER(Payment, manager)
#endif
#ifdef Vkk
   REGISTER_STRING_MEMBER(Payment, agent)
   REGISTER_FILETIME_MEMBER(Payment, payDate)
#endif
END_TYPE_REFLECTION(Payment)

BEGIN_TYPE_REFLECTION(OrgRemnantsItem)
   REGISTER_STRING_MEMBER(OrgRemnantsItem, id)
   REGISTER_ULONG_SCALE_MEMBER(OrgRemnantsItem, qty, QTY_SCALE)
#ifdef ORG_REMNANTS
   REGISTER_ULONG_MEMBER(OrgRemnantsItem,flags)
#endif
END_TYPE_REFLECTION(OrgRemnantsItem)

BEGIN_TYPE_REFLECTION(OrgRemnants)
   REGISTER_STRING_MEMBER(OrgRemnants, id)
   REGISTER_TIMESTAMP_MEMBER(OrgRemnants, created)
   REGISTER_TIMESTAMP_MEMBER(OrgRemnants, date)
   REGISTER_USHORT_MEMBER(OrgRemnants, flags)
   REGISTER_COLLECTION_MEMBER(OrgRemnants, items, OrgRemnantsItem)
#ifdef GPS_POS
   REGISTER_LONG_SCALE_MEMBER(OrgRemnants, latitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(OrgRemnants, longitude, GPS_SCALE)
#endif
END_TYPE_REFLECTION(OrgRemnants)

BEGIN_TYPE_REFLECTION(OrgPollItem)
   REGISTER_USHORT_MEMBER(OrgPollItem, id)
   REGISTER_USHORT_MEMBER(OrgPollItem, value)
END_TYPE_REFLECTION(OrgPollItem)

BEGIN_TYPE_REFLECTION(OrgPoll)
   REGISTER_STRING_MEMBER(OrgPoll, id)
   REGISTER_STRING_MEMBER(OrgPoll, pollID)
   REGISTER_FILETIME_MEMBER(OrgPoll, date)
   REGISTER_COLLECTION_MEMBER(OrgPoll, items, OrgPollItem)
END_TYPE_REFLECTION(OrgPoll)

BEGIN_TYPE_REFLECTION(PollItem)
   REGISTER_USHORT_MEMBER(PollItem, id)
   REGISTER_STRING_MEMBER(PollItem, value)
END_TYPE_REFLECTION(PollItem)

BEGIN_TYPE_REFLECTION(Poll)
   REGISTER_STRING_MEMBER(Poll, id)
   REGISTER_STRING_MEMBER(Poll, name)
   REGISTER_COLLECTION_MEMBER(Poll, items, PollItem)
END_TYPE_REFLECTION(Poll)

BEGIN_TYPE_REFLECTION(PricePhoto)
   REGISTER_STRING_MEMBER(PricePhoto, id)
   REGISTER_STRING_MEMBER(PricePhoto, photo)
END_TYPE_REFLECTION(PricePhoto)

#ifdef Alians
BEGIN_TYPE_REFLECTION(OrgInfo)
   REGISTER_STRING_MEMBER(OrgInfo, id)
   REGISTER_STRING_MEMBER(OrgInfo, address)
   REGISTER_STRING_MEMBER(OrgInfo, workTime)
   REGISTER_STRING_MEMBER(OrgInfo, dinner)
   REGISTER_STRING_MEMBER(OrgInfo, remark)
   REGISTER_COLLECTION_MEMBER(OrgInfo, contacts, Contact)
END_TYPE_REFLECTION(OrgInfo)
#endif

#ifdef ORG_SKU
BEGIN_TYPE_REFLECTION(RestItem)
  REGISTER_STRING_MEMBER(RestItem, id)
END_TYPE_REFLECTION(RestItem)

BEGIN_TYPE_REFLECTION(OrgRest)
   REGISTER_FILETIME_MEMBER(OrgRest, date)
   REGISTER_STRING_MEMBER(OrgRest, id)
   REGISTER_COLLECTION_MEMBER(OrgRest, items, RestItem)
   REGISTER_USHORT_MEMBER(OrgRest, state)
END_TYPE_REFLECTION(OrgRest)
#endif // ORG_SKU

BEGIN_TYPE_REFLECTION(OrderProceeded)
   REGISTER_FILETIME_MEMBER(OrderProceeded, created)
#if defined(POD_COMMENT)
   REGISTER_STRING_MEMBER(OrderProceeded, remark)
#endif
   REGISTER_STRING_MEMBER(OrderProceeded, type)
END_TYPE_REFLECTION(OrderProceeded)

#ifdef PROXY_DOC
BEGIN_TYPE_REFLECTION(Proxy)
   REGISTER_STRING_MEMBER(Proxy, id)
   REGISTER_FILETIME_MEMBER(Proxy, date)
   REGISTER_ULONG_SCALE_MEMBER(Proxy, sum, SUM_SCALE)
   REGISTER_STRING_MEMBER(Proxy, remark)
   REGISTER_ULONG_MEMBER(Proxy, flags)
END_TYPE_REFLECTION(Proxy)
#endif // PROXY_DOC

#ifdef RCV_MESSAGE
BEGIN_TYPE_REFLECTION(Message)
   REGISTER_FILETIME_MEMBER(Message, date)
   REGISTER_STRING_MEMBER(Message, message)
END_TYPE_REFLECTION(Message)
#endif

#ifdef GPS_POS
BEGIN_TYPE_REFLECTION(GPSPos)
   REGISTER_USHORT_MEMBER(GPSPos, isGSM)
   REGISTER_TIMESTAMP_MEMBER(GPSPos, date)
   REGISTER_LONG_SCALE_MEMBER(GPSPos, longitude, GPS_SCALE)
   REGISTER_LONG_SCALE_MEMBER(GPSPos, latitude, GPS_SCALE)
   REGISTER_ULONG_SCALE_MEMBER(GPSPos, speed, GPS_SPEED_SCALE)
END_TYPE_REFLECTION(GPSPos)
#endif // GPS_POS

#ifdef RPK
BEGIN_TYPE_REFLECTION(PlanItem)
   REGISTER_STRING_MEMBER(PlanItem, id)
   REGISTER_ULONG_MEMBER(PlanItem, value)
END_TYPE_REFLECTION(PlanItem)

BEGIN_TYPE_REFLECTION(PlanTable)
   REGISTER_STRING_MEMBER(PlanTable, id)
   REGISTER_STRING_MEMBER(PlanTable, name)
END_TYPE_REFLECTION(PlanTable)

BEGIN_TYPE_REFLECTION(Plan)
   REGISTER_STRING_MEMBER(Plan, id)
   REGISTER_FILETIME_MEMBER(Plan, date)
   REGISTER_COLLECTION_MEMBER(Plan, items, PlanItem)
   REGISTER_ULONG_MEMBER(Plan, flags)
END_TYPE_REFLECTION(Plan)
#endif

#ifdef ORG_NOTE
BEGIN_TYPE_REFLECTION(OrgNote)
   REGISTER_STRING_MEMBER(OrgNote, id)
   REGISTER_STRING_MEMBER(OrgNote, note)
END_TYPE_REFLECTION(OrgNote)
#endif

#ifdef FIRMS_TABLE
BEGIN_TYPE_REFLECTION(Firm)
   REGISTER_STRING_MEMBER(Firm, id)
   REGISTER_STRING_MEMBER(Firm, name)
   REGISTER_STRING_MEMBER(Firm, address)
   REGISTER_STRING_MEMBER(Firm, phone)
   REGISTER_STRING_MEMBER(Firm, inn)
   REGISTER_STRING_MEMBER(Firm, bank)
#ifdef Fusion
   REGISTER_STRING_MEMBER(Firm, factAddress)
   REGISTER_STRING_MEMBER(Firm, buh)
   REGISTER_STRING_MEMBER(Firm, chief)
   REGISTER_STRING_MEMBER(Firm, fullName)
#endif
END_TYPE_REFLECTION(Firm)
#endif

#ifdef AGENT_TASK

BEGIN_TYPE_REFLECTION(AgentTask)
   REGISTER_TIMESTAMP_MEMBER(AgentTask, date)
   REGISTER_TIMESTAMP_MEMBER(AgentTask, execDate)
   REGISTER_TIMESTAMP_MEMBER(AgentTask, appointDate)
   REGISTER_STRING_MEMBER(AgentTask, id)
   REGISTER_STRING_MEMBER(AgentTask, category)
   REGISTER_STRING_MEMBER(AgentTask, text)
   REGISTER_ULONG_MEMBER(AgentTask, flags)
END_TYPE_REFLECTION(AgentTask)

BEGIN_TYPE_REFLECTION(SVTask)
   REGISTER_TIMESTAMP_MEMBER(SVTask, date)
   REGISTER_TIMESTAMP_MEMBER(SVTask, execDate)
   REGISTER_TIMESTAMP_MEMBER(SVTask, appointDate)
   REGISTER_STRING_MEMBER(SVTask, id)
   REGISTER_STRING_MEMBER(SVTask, category)
   REGISTER_STRING_MEMBER(SVTask, text)
   REGISTER_ULONG_MEMBER(SVTask, flags)
   //CHAIN_REFLECTION(SVTask, AgentTask)
END_TYPE_REFLECTION(SVTask)

BEGIN_TYPE_REFLECTION(TaskCategory)
   REGISTER_STRING_MEMBER(TaskCategory, name)
END_TYPE_REFLECTION(TaskCategory)
#endif

#endif
