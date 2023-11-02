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
         return new FmDetailEx(data);
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
         if (baseType == typeof(HtmlReport))
            return typeof(HtmlReportEx);
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);
         if (baseType == typeof(Route))
            return typeof(RouteEx);
         if (baseType == typeof(FmSelectContrAgent))
            return typeof(FmSelectContrAgentEx);
         return baseType;
      }
   }

   partial class Order
   {
      public int byPhone = 0;
   }

   public class PhoneCall : BaseDocument
   {
      public static readonly string OBJECT_NAME = "PhoneCall";

      public int actions = 0;
   }

   partial class Org
   {
      public string manager = "";
      public string direction = "";

      public string Manager { get { return manager; } }
      public string Direction {  get { return direction; } }
   }
}