/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using System;
using System.Collections.Generic;
namespace GRSoft.NapoleonManager
{
   class FormEntries
   {
      internal static DivisionForm OpenDivisionForm()
      {
         return new DivisionForm();
      }

      internal static FmDetail OpenDetailForm(FmDetailData data)
      {
         return new FmDetail(data);
      }

      internal static UserForm OpenUserForm(Divisions owner)
      {
         return new UserForm(owner);
      }

      internal static FmCensus OpenCensusForm()
      {
         return new FmCensusEx();
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         if (baseType == typeof(Divisions))
            return typeof(DivisionsEx);

         return baseType;
      }
   }
}