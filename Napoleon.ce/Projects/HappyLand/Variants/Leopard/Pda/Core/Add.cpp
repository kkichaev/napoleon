/*
 * Copyright (C), 2007-2010, Денис Мосягин
 *
 * Дополнения Леопард
 *
 *  ert   21/12/2010   creating
 */
#include "stdafx.h"
#include "Add.h"

BEGIN_TYPE_REFLECTION(Sklad)
   REGISTER_STRING_MEMBER(Sklad, id)
   REGISTER_STRING_MEMBER(Sklad, name)
   REGISTER_ULONG_MEMBER(Sklad, flags)
END_TYPE_REFLECTION(Sklad)
