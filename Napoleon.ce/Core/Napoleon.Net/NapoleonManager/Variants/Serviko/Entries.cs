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
         return new UserForm(owner);
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

   class FirstDocTime : DataObject
   {
      public static readonly string OBJECT_NAME = "FirstDocTime";

      [KeyField]
      public string userid = "";

      [Reference("Agents", "userid")]
      public Agent agent = null;

      [KeyField]
      public int day = 0;
      public int time = 0;
   }
}