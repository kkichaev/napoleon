/*
 * Copyright (C), 2007, Денис Мосягин
 *
 * Коневертор версий
 * 
 *  ert   15/09/2007   creating
 */ 
#include "stdafx.h"
#include <atldef.h>
#include "VConvert.h"
#include "exchange.h"
#include "Sync.h"

/*
v3 - v4 +Org.suplDay
*/

struct OrgV3 : public IReflectableData
{
   wchar_t *id;
   wchar_t *name;

#ifdef ORG_COST_TYPE
   WORD costype;
#endif

   DECLARE_TYPE_REFLECTION(OrgV3);
};

BEGIN_TYPE_REFLECTION(OrgV3)
   REGISTER_STRING_MEMBER(Org, id)
   REGISTER_STRING_MEMBER(Org, name)
#ifdef ORG_COST_TYPE
   REGISTER_USHORT_MEMBER(Org, costype)
#endif
END_TYPE_REFLECTION(OrgV3)

struct OrgConvertV3 : public IConvertor
{
   enum { VERSION = 3 };

   OrgConvertV3()
   {
      versionManager.AddConvertor(ORG_OBJ, *this, VERSION);
   }
   ~OrgConvertV3() {}

   virtual IReflectableData *CreatePrevious() const { return new OrgV3(); }

   virtual bool ToCurrent(IReflectableData *current, const IReflectableData &prev) const
   {
      ((Org*)current)->id = ((const OrgV3&)prev).id;
      ((Org*)current)->name = ((const OrgV3&)prev).name;
#ifdef ORG_COST_TYPE
      ((Org*)current)->costype = ((const OrgV3&)prev).costype;
#endif
      ((Org*)current)->kind = 0;
      return true;
   }

   virtual bool ToPrevious(IReflectableData *prev, const IReflectableData &current) const
   {
      ((OrgV3*)prev)->id = ((const Org&)current).id;
      ((OrgV3*)prev)->name = ((const Org&)current).name;

#ifdef ORG_COST_TYPE
      ((OrgV3*)current)->costype = ((const Org&)prev).costype;
#endif
      return true;
   }
};

OrgConvertV3 pcv1;


#pragma warning(disable : 4073)
#pragma init_seg(lib)
VersionManager versionManager;
