/*
 * Copyright (C), 2010 - 2013, Гильдия Разработчиков
 *
 * Точки входа для форм
 * 
 * ert   15/11/2013   creating
 */

using GRSoft.Network;
using System;
using System.Windows.Forms;
namespace GRSoft.NapoleonAdmin
{
   class FormEntries
   {
      internal static System.Type GetObjectType(System.Type baseType)
      {
         if (baseType == typeof(MainForm))
            return typeof(MainFormEx);
         if (baseType == typeof(UserDataItem))
            return typeof(UserDataItemEx);

         return baseType;
      }

      internal static IFormDecorator GetFormDecorator(System.Type formType)
      {
         return new EmptyDecorator();
      }
   }

   class UserDataItemEx : UserDataItem
   {
      public UserDataItemEx(UserData container) : base(container) { }

      public string Supplier 
      {
         get { return manager == null ? "" : manager.suppl; } 
         set 
         {
            if(manager != null && manager.suppl != value)
            {
               Resolver resolver = new Resolver(name, manager.suppl, value, null);
               manager.suppl = value;
               container.FireChanging(resolver);
            }
         } 
      }

      public override void SetManager(DivisionManager manager)
      {
         base.SetManager(manager);
         manager.suppl = this.manager.suppl;
      }
   }

   public class Supplier : GRSoft.Network.DataObject, IComparable<Supplier>
   {
      public static readonly string OBJECT_NAME = "Suppliers";

      [KeyField]
      public string id = "";

      public string name = "";

      public override string ToString()
      {
         return name;
      }

      public string Name { get { return name; } }
      public string Id { get { return id; } }

      public int CompareTo(Supplier other)
      {
         return name.CompareTo(other.name);
      }
   }

   public partial class DivisionManager
   {
      public string suppl = "";
   }

}