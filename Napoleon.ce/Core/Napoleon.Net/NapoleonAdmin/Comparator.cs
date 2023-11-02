using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;
using System.ComponentModel;
using System.Collections;

namespace GRSoft.NapoleonAdmin
{
   /// <summary>
   /// Аттрибут для CompareCondition
   /// </summary>
   public class CompareAttribute : Attribute{}

   /// <summary>
   /// Класс хранит данные, которые применяются для сортировки
   /// </summary>
   public class CompareCondition
   {
      protected string fieldName;
      protected bool isAscending;

      public void SetCompareCondition(string field, bool isAsc)
      {
         fieldName = field;
         isAscending = isAsc;
      }

      public string FieldName { get { return fieldName; } }
      public bool IsAscending { get { return isAscending; } }
   }

   /// <summary>
   /// Предок классов, что будут поддерживать условия сортировки CompareCondition
   /// </summary>
   /// <typeparam name="T"></typeparam>
   public abstract class CmpByField<T> : IComparable<T>
   {
      public virtual int CompareTo(T other)
      {
         CompareCondition cc = GetCompareCondition();

         if (cc == null)
            throw new NotImplementedException();

         return Comparator.CompareItems(this, other, cc.FieldName, cc.IsAscending);
      }

      protected CompareCondition GetCompareCondition()
      {
         //Ищем поле с условием для сравнения
         FieldInfo[] fields = typeof(T).GetFields(BindingFlags.Public | BindingFlags.Static);

         CompareCondition cc = null;

         foreach (FieldInfo fi in fields)
         {
            object[] attrs = fi.GetCustomAttributes(false);

            foreach (object a in attrs)
            {
               if (a is CompareAttribute)
               {
                  cc = (CompareCondition)fi.GetValue(this);
               }
            }
         }
         return cc;
      }
   }

   class Comparator
   {
      static int DoCompare(object srcV, object destV, Type objType, bool asc)
      {
         if (objType == typeof(int))
         {
            int src = Convert.ToInt32(srcV);
            int dest = Convert.ToInt32(destV);

            return (asc) ? src - dest : dest - src;
         }

         if (objType == typeof(double))
         {
            double src1 = Convert.ToInt32(srcV);
            double dest1 = Convert.ToInt32(destV);

            return asc ? (int)(src1 - dest1) : (int)(dest1 - src1);
         }

         if (objType == typeof(DateTime))
         {
            DateTime src2 = Convert.ToDateTime(srcV);
            DateTime dest2 = Convert.ToDateTime(destV);

            return asc ? src2.CompareTo(dest2) : dest2.CompareTo(src2);
         }

         string srcStr = Convert.ToString(srcV);
         string destStr = Convert.ToString(destV);

         return asc ? srcStr.CompareTo(destStr) : destStr.CompareTo(srcStr);
      }

      public static int CompareItems(object item1, object item2, string cmpField, bool asc)
      {
         if (cmpField.Length == 0)
            throw new NotImplementedException();

         FieldInfo fi = item1.GetType().GetField(cmpField, BindingFlags.Instance | BindingFlags.NonPublic);
         if( fi != null )
            return DoCompare(fi.GetValue(item1), fi.GetValue(item2), fi.FieldType, asc);

         PropertyInfo pi = item1.GetType().GetProperty(cmpField, BindingFlags.Instance | BindingFlags.NonPublic | BindingFlags.Public);
         if (pi == null)
            throw new NotImplementedException();

         return DoCompare(pi.GetValue(item1, null), pi.GetValue(item2, null), pi.PropertyType, asc);
      }
   }

   public class SortableBindingList<T> : BindingList<T>
   {
      private bool _isSorted;
      private ListSortDirection _sortDirection = ListSortDirection.Ascending;
      private PropertyDescriptor _sortProperty;

      public SortableBindingList(IList<T> list) : base(list) { }
      public SortableBindingList() { }
      public SortableBindingList(IList<T> list, PropertyDescriptor property, ListSortDirection direction) :
         base(list)
      {
         ApplySortCore(property, direction);
      }

      protected override void ApplySortCore(PropertyDescriptor property, ListSortDirection direction)
      {
         _sortProperty = property;
         _sortDirection = direction;

         // Get list to sort
         List<T> items = this.Items as List<T>;

         // Apply and set the sort, if items to sort
         if (items != null)
         {
            PropertyComparer<T> pc = new PropertyComparer<T>(property, direction);
            items.Sort(pc);
            _isSorted = true;
         }
         else
         {
            _isSorted = false;
         }

         // Let bound controls know they should refresh their views
         this.OnListChanged(new ListChangedEventArgs(ListChangedType.Reset, -1));
      }

      protected override bool SupportsSortingCore { get { return true; } }
      protected override bool IsSortedCore { get { return _isSorted; } }
      protected override PropertyDescriptor SortPropertyCore { get { return _sortProperty; } }
      protected override ListSortDirection SortDirectionCore { get { return _sortDirection; } }

      protected override void RemoveSortCore()
      {
         _sortDirection = ListSortDirection.Ascending;
         _sortProperty = null;
         _isSorted = false;
      }
   }

   public class PropertyComparer<T> : IComparer<T>
   {
      private PropertyDescriptor property;
      private ListSortDirection sortDirection;

      public PropertyComparer(PropertyDescriptor property, ListSortDirection sortDirection)
      {
         this.property = property;
         this.sortDirection = sortDirection;
      }

      public int Compare(T x, T y)
      {
         object valueX = property.GetValue(x);
         object valueY = property.GetValue(y);

         return (sortDirection == ListSortDirection.Ascending) ? 
            Comparer.Default.Compare(valueX, valueY) : 
            Comparer.Default.Compare(valueY, valueX);
      }
   }
}
