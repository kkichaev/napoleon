/*
 * Copyright (C), 2010 - 2011, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   28/03/2011   creating
 */
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   class FormEntries
   {
      internal static DivisionForm OpenDivisionForm()
      {
         return new DivisionFormEx();
      }

      internal static FmDetail OpenDetailForm(FmDetailData data)
      {
         return new FmDetail(data);
      }

      internal static UserForm OpenUserForm(Divisions owner)
      {
         return new UserFormEx(owner);
      }

      internal static FmCensus OpenCensusForm()
      {
         return new FmCensus();
      }

      internal static System.Type GetFormType(System.Type baseType)
      {
         return baseType;
      }
   }

   class OrgAsmMatrix : DataObject
   {
      public static readonly string OBJECT_NAME = "OrgAsmMatrix";

      [KeyField]
      public string id = "";
      public string userid = "";
   }
}