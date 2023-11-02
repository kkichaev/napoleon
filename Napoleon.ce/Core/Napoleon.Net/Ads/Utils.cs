using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Resources;
using GRSoft.NapoleonManager.Properties;
using System.ComponentModel;

namespace GRSoft.NapoleonManager
{
   interface IReloadData
   {
      void ReloadData();
   }

   class Utils
   {
      private static readonly string ENABLED = "Enabled";

      public static void DataConnectionError(Control ctrl, object refresh, string err)
      {
         DataModule.ClearEvents();

         ctrl.Invoke(new EmptyParamHandler(delegate
         {
            FmWait.CloseForm();
            refresh.GetType().GetProperty(ENABLED).SetValue(refresh, true, null);
            MessageBox.Show(err, Resources.error, MessageBoxButtons.OK,
               MessageBoxIcon.Error);
         }));
      }

      public static void DataProcessed(Control ctrl, object refresh)
      {
         DataModule.ClearEvents();

         ctrl.Invoke(new InvokeDelegate(delegate
         {
            FmWait.CloseForm();

            if (ctrl is IReloadData)
               ((IReloadData)ctrl).ReloadData();

            refresh.GetType().GetProperty(ENABLED).SetValue(refresh, true, null);
         }));
      }

      public static DialogResult AskToApplyDelete()
      {
         return MessageBox.Show(Resources.askToDelRow, Resources.question,
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
      }

      public static string GUID
      {
         get { return System.Guid.NewGuid().ToString().Replace("-", ""); }
      }

      public static DialogResult AskToSaveChangingData()
      {
         return MessageBox.Show(Resources.askToSaveChangingData, Resources.question,
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
      }

      internal static void ErrorSaveDB()
      {
         MessageBox.Show(Resources.errorSaveDb, Resources.error,
            MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
      }
   }

   class SortBindingList<T> : BindingList<T>
   {
      protected override void ApplySortCore(PropertyDescriptor prop, ListSortDirection direction)
      {
         List<T> items = Items as List<T>;

         if (items != null)
         {
            items.Sort(delegate(T lhs, T rhs)
            {
               if (prop != null)
               {
                  object lhsValue = lhs == null ? null : prop.GetValue(lhs);
                  object rhsValue = rhs == null ? null : prop.GetValue(rhs);
                  int result = System.Collections.Comparer.Default.Compare(lhsValue, rhsValue);

                  if (direction == ListSortDirection.Descending)
                  {
                     result = -result;
                  }

                  return result;
               }
               else
               {
                  return 0;
               }
            });
         }
      }

      protected override void RemoveSortCore()
      {

      }

      protected override bool SupportsSortingCore
      {
         get { return true; }
      }
   }

   public struct Address
   {
      public string city;
      public string street;
      public string house;

      public Address(string city, string street, string house)
      {
         this.city = city;
         this.street = street;
         this.house = house;
      }

      public override string ToString()
      {
         StringBuilder result = new StringBuilder();

         if (street.Length > 0)
            result.Append(street);

         if (result.Length > 0 && house.Length > 0)
            result.Append(" ").Append(house);

         if (city.Length > 0)
         {
            if (result.Length > 0)
               result.Insert(0, city + ", "); 
            else
               result.Append(city);
         }
           
         return result.ToString(); ;
      }
   }
}
