using System;
using System.Collections.Generic;
using System.Text;
using System.Reflection;

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
      private string fieldName;
      private bool isAscending;

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
      public int CompareTo(T other)
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

         if (cc == null)
            throw new NotImplementedException();

         return Comparator.CompareItems(this, other, cc.FieldName, cc.IsAscending);
      }
   }

   class Comparator
   {
      public static int CompareItems(object item1, object item2, string cmpField, bool asc)
      {
         const string EMPTY_SEARCH_FIELD_MSG = "Поле для поиска должно быть определено.";

         if (cmpField.Length == 0)
            throw new NotImplementedException(EMPTY_SEARCH_FIELD_MSG);

         PropertyInfo fi = item1.GetType().GetProperty(cmpField);
         PropertyInfo f2 = item2.GetType().GetProperty(cmpField);


         const string ITEM_1_NAME = "item_1";
         const string ITEM_DOESNT_CONTAIN_FIELD_MSG = "Объект сравнения \"{0}\" должен содержать поле {1}";

         if (fi == null)
            throw new NotImplementedException(
               String.Format(ITEM_DOESNT_CONTAIN_FIELD_MSG, ITEM_1_NAME, cmpField));

         const string ITEM_2_NAME = "item_2";

         if (f2 == null)
            throw new NotImplementedException(
               String.Format(ITEM_DOESNT_CONTAIN_FIELD_MSG, ITEM_2_NAME, cmpField));

         object srcV = fi.GetValue(item1,null);
         object destV = fi.GetValue(item2,null);

         if (fi.PropertyType == typeof(int))
         {
            int src = Convert.ToInt32(srcV);
            int dest = Convert.ToInt32(destV);

            return (asc) ? src - dest : dest - src;
         }

         if (fi.PropertyType == typeof(double))
         {
            double src1 = Convert.ToInt32(srcV);
            double dest1 = Convert.ToInt32(destV);

            return asc ? (int)(src1 - dest1) : (int)(dest1 - src1);
         }

         if (fi.PropertyType == typeof(DateTime))
         {
            int cmp = ((DateTime)srcV).CompareTo((DateTime)destV);
            return asc ? cmp : -cmp;
         }

         string srcStr = Convert.ToString(srcV);
         string destStr = Convert.ToString(destV);

         return asc ? srcStr.CompareTo(destStr) : destStr.CompareTo(srcStr);
      }
   }
}
